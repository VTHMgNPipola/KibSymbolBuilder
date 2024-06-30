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
import java.util.Objects;
import java.util.ResourceBundle;

public class KSymbolBuilderController {
    private static final Logger logger = LoggerFactory.getLogger(KSymbolBuilderController.class);

    @FXML private ResourceBundle resources;

    @FXML private TreeView<String> librariesTreeView;
    @FXML private KSymbolEditorPanelController editorController;

    private MapProperty<Path, SEKiCadLibrary> libraries;

    @FXML
    private void initialize() {
        logger.info("Initializing KSymbolBuilderController...");

        libraries = new SimpleMapProperty<>(FXCollections.observableHashMap());
        libraries.addListener((MapChangeListener<? super Path, ? super SEKiCadLibrary>) c -> {
            TreeItem<String> rootItem = new TreeItem<>(resources.getString("symbolEditor.librariesTreeItem"));
            rootItem.setExpanded(true);

            for (SEKiCadLibrary library : libraries.values()) {
                TreeItem<String> libraryTreeItem = new TreeItem<>(library.getLibraryName());

                for (SEToken<?> child : library.getChildren()) {
                    if (child instanceof SEKiCadSymbol symbol) {
                        TreeItem<String> symbolTreeItem = new TreeItem<>(symbol.getSymbolName());
                        libraryTreeItem.getChildren().add(symbolTreeItem);
                    }
                }
                rootItem.getChildren().add(libraryTreeItem);
            }

            librariesTreeView.setRoot(rootItem);
        });

        MKiCadSymbol kiCadSymbol = new MKiCadSymbol();

        editorController.setSymbol(kiCadSymbol);
        kiCadSymbol.loadSExpression(new SEKiCadSymbol(""));

        logger.info("Successfully initialized KSymbolBuilderController");
    }

    @FXML
    private void openLibraryManager() {
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(resources);

        loader.setLocation(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("com/vthmgnpipola/kibsymbolbuilder/fxml/KLibraryManager.fxml")));
        try {
            Parent libraryManagerRoot = loader.load();
            ((KLibraryManagerController) loader.getController()).setLibraries(libraries);

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
}
