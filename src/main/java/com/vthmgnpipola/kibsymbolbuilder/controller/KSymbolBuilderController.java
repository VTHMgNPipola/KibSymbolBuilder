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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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

    @FXML
    private ResourceBundle resources;

    @FXML
    private MenuItem newSymbolMenuItem;

    @FXML
    private TreeView<String> librariesTreeView;
    @FXML
    private KSymbolEditorPanelController editorController;

    private MapProperty<Path, SEKiCadLibrary> libraries;
    private AtomicBoolean modifyingLibrariesMap;

    private Path selectedLibraryPath;
    private Path currentLibraryPath;
    private int currentSymbolIndex;
    private MKiCadSymbol kiCadSymbol;

    @FXML
    private void initialize() {
        logger.info("Initializing KSymbolBuilderController...");

        modifyingLibrariesMap = new AtomicBoolean(false);

        selectedLibraryPath = null;
        currentLibraryPath = null;
        currentSymbolIndex = -1;

        libraries = new SimpleMapProperty<>(FXCollections.observableHashMap());
        libraries.addListener((MapChangeListener<? super Path, ? super SEKiCadLibrary>) c -> {
            if (modifyingLibrariesMap.get()) {
                return;
            }

            updateLibrariesTreeView();

            selectedLibraryPath = null;
            currentLibraryPath = null;
            currentSymbolIndex = -1;
            kiCadSymbol.loadSExpression(new SEKiCadSymbol(""));
        });

        updateLibrariesTreeView();

        kiCadSymbol = new MKiCadSymbol();

        editorController.setSymbol(kiCadSymbol);
        kiCadSymbol.loadSExpression(new SEKiCadSymbol(""));

        MenuItem newLibraryContextMenuItem = new MenuItem(resources.getString("symbolEditor.newLibrary"));
        newLibraryContextMenuItem.setOnAction(event -> createNewLibrary());

        MenuItem newSymbolContextMenuItem = new MenuItem(resources.getString("symbolEditor.newSymbol"));
        newSymbolContextMenuItem.setOnAction(event -> createNewSymbol());
        newSymbolContextMenuItem.setDisable(true);

        SeparatorMenuItem separator = new SeparatorMenuItem();

        MenuItem removeSymbolsMenuItem = new MenuItem(resources.getString("symbolEditor.removeSymbols"));
        removeSymbolsMenuItem.setOnAction(event -> {
            List<TreeItem<String>> selectedItems = librariesTreeView.getSelectionModel().getSelectedItems();
            if (selectedItems.isEmpty()) {
                return;
            }

            // Verify that all selected items are symbols, while mapping all libraries that correspond to the selected
            // symbols. The mapping is used to reduce the number of lookups that need to be done to remove each symbol.
            Map<String, List<String>> removedSymbols = new HashMap<>();
            for (TreeItem<String> item : selectedItems) {
                if (!item.isLeaf()) {
                    return;
                }

                String libraryName = item.getParent().getValue();
                if (removedSymbols.containsKey(libraryName)) {
                    removedSymbols.get(libraryName).add(item.getValue());
                } else {
                    List<String> list = new ArrayList<>();
                    list.add(item.getValue());
                    removedSymbols.put(libraryName, list);
                }
            }

            for (Map.Entry<String, List<String>> entry : removedSymbols.entrySet()) {
                Optional<Map.Entry<Path, SEKiCadLibrary>> libraryOptional = libraries.entrySet().stream()
                        .filter(e -> e.getValue().getLibraryName().equals(entry.getKey()))
                        .findAny();
                assert libraryOptional.isPresent();
                SEKiCadLibrary library = libraryOptional.get().getValue();

                logger.info("Removing symbols from library '{}'...", library.getLibraryName());

                for (String symbol : entry.getValue()) {
                    logger.info("Removing symbol '{}'...", symbol);
                    library.getChildren()
                            .removeIf(token -> token instanceof SEKiCadSymbol s && s.getSymbolName().equals(symbol));
                }
            }

            kiCadSymbol.loadSExpression(new SEKiCadSymbol(""));
            currentSymbolIndex = -1;

            updateLibrariesTreeView();
        });
        removeSymbolsMenuItem.setDisable(true);

        ContextMenu treeViewContextMenu = new ContextMenu(newLibraryContextMenuItem, newSymbolContextMenuItem,
                separator, removeSymbolsMenuItem);
        librariesTreeView.setContextMenu(treeViewContextMenu);

        librariesTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        librariesTreeView.setOnMouseClicked(event -> {
            if (!librariesTreeView.getSelectionModel().isEmpty()) {
                TreeItem<String> selectedItem = librariesTreeView.getSelectionModel().getSelectedItem();

                if (librariesTreeView.getRoot() == null || selectedItem.getParent() == null) {
                    removeSymbolsMenuItem.setDisable(true);
                    return;
                }

                boolean allSymbols = true;
                for (TreeItem<String> item : librariesTreeView.getSelectionModel().getSelectedItems()) {
                    if (!item.isLeaf()) {
                        allSymbols = false;
                        break;
                    }
                }
                removeSymbolsMenuItem.setDisable(!allSymbols);

                boolean librarySelected = !(selectedItem.isLeaf() ||
                        !librariesTreeView.getRoot().equals(selectedItem.getParent()));
                newSymbolContextMenuItem.setDisable(!librarySelected);
                newSymbolMenuItem.setDisable(!librarySelected);

                if (librarySelected) {
                    Optional<Map.Entry<Path, SEKiCadLibrary>> libraryOptional = libraries.entrySet().stream()
                            .filter(e -> e.getValue().getLibraryName().equals(selectedItem.getValue()))
                            .findAny();
                    assert libraryOptional.isPresent();

                    selectedLibraryPath = libraryOptional.get().getKey();
                } else if (event.getClickCount() >= 2 && selectedItem.isLeaf()) {
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
                    selectedLibraryPath = currentLibraryPath;
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
        applySymbolChanges();

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
    private void createNewSymbol() {
        TreeItem<String> selectedItem = librariesTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem == null || !selectedItem.getParent().equals(librariesTreeView.getRoot())) {
            return;
        }

        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setContentText(resources.getString("symbolEditor.newSymbolName"));
        Optional<String> nameOptional = nameDialog.showAndWait();
        if (nameOptional.isEmpty()) {
            return;
        }
        String name = nameOptional.get();

        SEKiCadLibrary library = libraries.get(selectedLibraryPath);
        boolean symbolExists = library.getChildren().stream()
                .anyMatch(token -> token instanceof SEKiCadSymbol s && s.getSymbolName().equals(name));
        if (symbolExists) {
            new Alert(Alert.AlertType.WARNING, resources.getString("symbolEditor.symbolAlreadyExists")).show();
            return;
        }

        SEKiCadSymbol newSymbol = new SEKiCadSymbol(name);
        newSymbol.setValue(name);
        library.getChildren().add(newSymbol);
        currentSymbolIndex = library.getChildren().indexOf(newSymbol);
        currentLibraryPath = selectedLibraryPath;

        kiCadSymbol.loadSExpression(newSymbol);

        updateLibrariesTreeView();
    }

    @FXML
    private void createNewLibrary() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(resources.getString("symbolEditor.newLibraryFile"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                resources.getString("generic.kicadLibraryExtensionName"), "*.kicad_sym"));

        File chosenFile = fileChooser.showSaveDialog(librariesTreeView.getScene().getWindow());
        if (chosenFile != null) {
            String pathString = chosenFile.getAbsolutePath();
            if (!pathString.endsWith(".kicad_sym")) {
                pathString += ".kicad_sym";
            }

            Path path = Path.of(pathString);
            if (Files.exists(path) || libraries.keySet().stream().anyMatch(p -> p.equals(path))) {
                new Alert(Alert.AlertType.WARNING, resources.getString("symbolEditor.libraryAlreadyExists")).show();
                return;
            }

            SEKiCadLibrary newLibrary = new SEKiCadLibrary();

            String libraryName = path.getFileName().toString();
            int posExtension = libraryName.lastIndexOf('.');
            if (posExtension >= 0 && posExtension < libraryName.length() - 1) {
                libraryName = libraryName.substring(0, posExtension);
            }
            newLibrary.setLibraryName(libraryName);

            libraries.put(path, newLibrary);
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
