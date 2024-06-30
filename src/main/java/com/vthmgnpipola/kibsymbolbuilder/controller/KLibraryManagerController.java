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

import com.vthmgnpipola.kibsymbolbuilder.kicad.sexpr.SEKiCadLibrary;
import com.vthmgnpipola.kibsymbolbuilder.sexpr.RawSEToken;
import com.vthmgnpipola.kibsymbolbuilder.sexpr.SEReader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class KLibraryManagerController {
    private static final Logger logger = LoggerFactory.getLogger(KLibraryManagerController.class);

    @FXML private ResourceBundle resources;

    @FXML private ListView<String> libraryListView;

    private Map<Path, SEKiCadLibrary> libraries;

    @FXML
    private void initialize() {
        logger.info("Initializing KLibraryManagerController...");

        logger.info("Successfully initialized KLibraryManagerController");
    }

    @FXML
    private void addLibrary() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(resources.getString("libraryManager.openLibraryFile"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                resources.getString("libraryManager.openLibraryFile.extensionName"), "*.kicad_sym"));

        File chosenFile = fileChooser.showOpenDialog(libraryListView.getScene().getWindow());
        if (chosenFile != null) {
            if (libraryListView.getItems().contains(chosenFile.getAbsolutePath())) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        resources.getString("libraryManager.libraryAlreadyExists"));
                alert.show();
            } else {
                libraryListView.getItems().add(chosenFile.getAbsolutePath());
            }
        }
    }

    @FXML
    private void removeLibrary() {
        libraryListView.getItems().remove(libraryListView.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void closeManager(ActionEvent e) {
        ((Stage) ((Node) e.getSource()).getScene().getWindow()).close();
    }

    @FXML
    private void applyModifications(ActionEvent e) {
        SEReader reader = new SEReader();

        boolean error = false;
        Map<Path, SEKiCadLibrary> intermediaryMap = new HashMap<>();
        for (String pathString : libraryListView.getItems()) {
            Path path = Path.of(pathString);
            RawSEToken libraryToken = reader.read(path);

            if (libraryToken == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        resources.getString("generic.ioException") + "\n" + path);
                alert.show();
                continue;
            }

            SEKiCadLibrary library = new SEKiCadLibrary();
            try {
                library.read(libraryToken);

                String libraryName = path.getFileName().toString();
                int posExtension = libraryName.lastIndexOf('.');
                if (posExtension >= 0 && posExtension < libraryName.length() - 1) {
                    libraryName = libraryName.substring(0, posExtension);
                }
                library.setLibraryName(libraryName);
            } catch (Throwable t) {
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        resources.getString("libraryManager.invalidSexpr") + "\n" + path);
                alert.show();
                continue;
            }

            if (!library.getGenerator().equals(SEKiCadLibrary.GENERATOR)) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                        resources.getString("libraryManager.invalidGenerator") + "\n" + library.getLibraryName());
                Optional<ButtonType> selectedButton = alert.showAndWait();
                if (!(selectedButton.isPresent() && selectedButton.get() == ButtonType.OK)) {
                    error = true;
                    break;
                }
            }

            intermediaryMap.put(path, library);
        }

        if (!error) {
            libraries.clear();
            libraries.putAll(intermediaryMap);

            ((Stage) ((Node) e.getSource()).getScene().getWindow()).close();
        }
    }

    public void setLibraries(Map<Path, SEKiCadLibrary> libraries) {
        this.libraries = libraries;

        libraryListView.getItems().clear();
        for (Path path : libraries.keySet()) {
            libraryListView.getItems().add(path.toAbsolutePath().toString());
        }
    }
}
