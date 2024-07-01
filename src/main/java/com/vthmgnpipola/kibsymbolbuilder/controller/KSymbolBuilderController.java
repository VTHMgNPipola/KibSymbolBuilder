/*
 * Kib Symbol Builder
 * Copyright © 2024  VTHMgNPipola
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.vthmgnpipola.kibsymbolbuilder.controller;

import com.vthmgnpipola.kibsymbolbuilder.kicad.gui.MKiCadSymbol;
import com.vthmgnpipola.kibsymbolbuilder.kicad.sexpr.SEKiCadLibrary;
import com.vthmgnpipola.kibsymbolbuilder.kicad.sexpr.SEKiCadSymbol;
import com.vthmgnpipola.kibsymbolbuilder.sexpr.SEToken;
import com.vthmgnpipola.kibsymbolbuilder.sexpr.SEWriter;
import javafx.application.Platform;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class KSymbolBuilderController {
    private static final Logger logger = LoggerFactory.getLogger(KSymbolBuilderController.class);

    @FXML private ResourceBundle resources;

    @FXML private TreeView<String> librariesTreeView;
    @FXML private KSymbolEditorPanelController editorController;

    private MapProperty<Path, SEKiCadLibrary> libraries;
    private AtomicBoolean modifyingLibrariesMap;

    private Path currentLibraryPath;
    private int currentSymbolIndex;
    private MKiCadSymbol kiCadSymbol;

    @FXML
    private void initialize() {
        logger.info("Initializing KSymbolBuilderController...");

        modifyingLibrariesMap = new AtomicBoolean(false);

        currentLibraryPath = null;
        currentSymbolIndex = -1;

        libraries = new SimpleMapProperty<>(FXCollections.observableHashMap());
        libraries.addListener((MapChangeListener<? super Path, ? super SEKiCadLibrary>) c -> {
            if (modifyingLibrariesMap.get()) {
                return;
            }

            updateLibrariesTreeView();

            currentLibraryPath = null;
            currentSymbolIndex = -1;
            kiCadSymbol.loadSExpression(new SEKiCadSymbol(""));
        });

        kiCadSymbol = new MKiCadSymbol();

        editorController.setSymbol(kiCadSymbol);
        kiCadSymbol.loadSExpression(new SEKiCadSymbol(""));

        librariesTreeView.setOnMouseClicked(event -> {
            if (event.getClickCount() >= 2 && !librariesTreeView.getSelectionModel().isEmpty()) {
                TreeItem<String> selectedItem = librariesTreeView.getSelectionModel().getSelectedItem();
                if (selectedItem.getChildren().isEmpty()) {
                    TreeItem<String> selectedLibraryTreeItem = selectedItem.getParent();
                    Optional<Map.Entry<Path, SEKiCadLibrary>> libraryOptional = libraries.entrySet().stream()
                            .filter(e -> e.getValue().getLibraryName().equals(selectedLibraryTreeItem.getValue()))
                            .findAny();
                    assert libraryOptional.isPresent();

                    SEKiCadSymbol symbol = null;
                    List<SEToken<?>> tokens = libraryOptional.get().getValue().getChildren();
                    for (int i = 0; i < tokens.size(); i++) {
                        if (tokens.get(i) instanceof SEKiCadSymbol s &&
                                s.getSymbolName().equals(selectedItem.getValue())) {
                            symbol = s;
                            currentSymbolIndex = i;
                        }
                    }
                    assert symbol != null;

                    kiCadSymbol.loadSExpression(symbol);
                    currentLibraryPath = libraryOptional.get().getKey();
                }
            }
        });

        logger.info("Successfully initialized KSymbolBuilderController");
    }

    @FXML
    private void loadPinAssignments() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);
            loader.setLocation(Objects.requireNonNull(getClass().getClassLoader()
                    .getResource("com/vthmgnpipola/kibsymbolbuilder/fxml/KPinAssignmentsLoader.fxml")));
            Parent pinAssignmentsLoaderRoot = loader.load();

            Stage stage = new Stage();
            stage.setTitle(resources.getString("symbolEditor.pinAssignmentsLoader.title"));
            stage.setScene(new Scene(pinAssignmentsLoaderRoot));
            stage.show();
        } catch (IOException e) {
            logger.error("Unable to load pin assignments loader window!", e);
            Alert alert = new Alert(Alert.AlertType.ERROR, resources.getString("generic.ioException"));
            alert.show();
        }
    }

    @FXML
    private void openLibraryManager() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);
            loader.setLocation(Objects.requireNonNull(getClass().getClassLoader()
                    .getResource("com/vthmgnpipola/kibsymbolbuilder/fxml/KLibraryManager.fxml")));
            Parent libraryManagerRoot = loader.load();
            KLibraryManagerController controller = loader.getController();
            controller.setLibraries(libraries);
            controller.setModifyingMapFlagRef(modifyingLibrariesMap);

            Stage stage = new Stage();
            stage.setTitle(resources.getString("libraryManager.title"));
            stage.setScene(new Scene(libraryManagerRoot));
            stage.show();
        } catch (IOException e) {
            logger.error("Unable to load library manager window!", e);
            Alert alert = new Alert(Alert.AlertType.ERROR, resources.getString("generic.ioException"));
            alert.show();
        }
    }

    @FXML
    private void saveLibraries() {
        logger.info("Saving all libraries...");
        SEWriter writer = new SEWriter();

        boolean error = false;
        for (Map.Entry<Path, SEKiCadLibrary> libraryEntry : libraries.entrySet()) {
            logger.info("Saving library {}...", libraryEntry.getValue().getLibraryName());
            try {
                libraryEntry.getValue().write(writer);
                writer.finish(libraryEntry.getKey());
            } catch (Throwable t) {
                logger.error("Error while saving library!", t);
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        resources.getString("generic.ioException") + "\n" + libraryEntry.getKey().toString());
                alert.show();
                error = true;
            }
        }

        if (!error) {
            logger.info("Successfully saved all libraries!");
        } else {
            logger.warn("Saved libraries with errors!");
        }
    }

    @FXML
    private void applySymbolChanges() {
        if (currentLibraryPath == null || currentSymbolIndex < 0) {
            return;
        }

        SEKiCadSymbol symbol = kiCadSymbol.generateSExpression();

        libraries.get(currentLibraryPath).getChildren().set(currentSymbolIndex, symbol);

        updateLibrariesTreeView();
    }

    @FXML
    private void cancelSymbolChanges() {
        if (currentLibraryPath == null || currentSymbolIndex < 0) {
            return;
        }

        kiCadSymbol.loadSExpression((SEKiCadSymbol) libraries.get(currentLibraryPath)
                .getChildren().get(currentSymbolIndex));
    }

    @FXML
    private void exit() {
        logger.info("User requested exiting the application.");
        Platform.exit();
    }

    private void updateLibrariesTreeView() {
        TreeItem<String> oldRoot = librariesTreeView.getRoot();
        Map<String, Boolean> expandedStatusMap;
        if (oldRoot != null) {
            expandedStatusMap = librariesTreeView.getRoot().getChildren().stream()
                    .collect(Collectors.toMap(TreeItem::getValue, TreeItem::isExpanded));
        } else {
            expandedStatusMap = new HashMap<>();
        }

        TreeItem<String> rootItem = new TreeItem<>(resources.getString("symbolEditor.librariesTreeItem"));
        rootItem.setExpanded(true);

        for (SEKiCadLibrary library : libraries.values()) {
            String libraryName = library.getLibraryName();
            TreeItem<String> libraryTreeItem = new TreeItem<>(libraryName);

            for (SEToken<?> child : library.getChildren()) {
                if (child instanceof SEKiCadSymbol symbol) {
                    TreeItem<String> symbolTreeItem = new TreeItem<>(symbol.getSymbolName());
                    libraryTreeItem.getChildren().add(symbolTreeItem);
                }
            }

            if (expandedStatusMap.containsKey(libraryName)) {
                libraryTreeItem.setExpanded(expandedStatusMap.get(libraryName));
            }
            rootItem.getChildren().add(libraryTreeItem);
        }

        librariesTreeView.setRoot(rootItem);
    }
}
