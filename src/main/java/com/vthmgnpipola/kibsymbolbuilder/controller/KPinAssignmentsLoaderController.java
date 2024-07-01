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

import com.vthmgnpipola.kibsymbolbuilder.parser.CSVPinLayoutParser;
import com.vthmgnpipola.kibsymbolbuilder.parser.PinLayoutParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class KPinAssignmentsLoaderController {
    private static final Logger logger = LoggerFactory.getLogger(KPinAssignmentsLoaderController.class);

    @FXML private ResourceBundle resources;

    @FXML private TextField filePathTextField;
    @FXML private CheckBox loadColumnsCheckBox;
    @FXML private ChoiceBox<String> pinNumberChoiceBox;
    @FXML private ChoiceBox<String> pinNameChoiceBox;
    @FXML private ChoiceBox<String> pinUnitChoiceBox;
    @FXML private ChoiceBox<String> pinBlockChoiceBox;

    private URI selectedFile;

    private PinLayoutParser parser;

    @FXML
    private void initialize() {
        logger.info("Initializing KPinAssignmentsLoaderController...");

        loadColumnsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (parser != null) {
                parser.setLoadColumnHeaders(newValue);
                updateColumnHeaders();
            }
        });

        filePathTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                selectedFile = Paths.get(newValue).toUri();
            } catch (Throwable t) {
                try {
                    selectedFile = new URI(newValue);
                } catch (Throwable t1) {
                    // Inputted value cannot be converted to OS file path or URI, which may happen when user is
                    // inputting data
                    selectedFile = null;
                }
            }

            if (selectedFile != null) {
                boolean error;
                if (selectedFile.toString().endsWith(".csv")) {
                    IOException exception = null;
                    parser = new CSVPinLayoutParser();
                    parser.setLoadColumnHeaders(loadColumnsCheckBox.isSelected());
                    try {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(selectedFile.toURL().openStream()));
                        String data = reader.lines().collect(Collectors.joining("\n"));
                        error = !parser.load(data);
                    } catch (IOException e) {
                        parser = null;
                        error = true;
                        exception = e;
                    }
                    if (error && exception != null) {
                        logger.debug("Error loading CSV file.", exception);
                    } else if (error) {
                        logger.debug("Error loading CSV file.");
                    }
                }

                updateColumnHeaders();
            }
        });

        logger.info("Successfully initialized KPinAssignmentsLoaderController");
    }

    @FXML
    private void selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(resources.getString("symbolEditor.pinAssignmentsLoader.fileChooserTitle"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                resources.getString("generic.spreadsheetFileExtensionName"), "*.csv")); // Add ODT and XLSX

        File chosenFile = fileChooser.showOpenDialog(filePathTextField.getScene().getWindow());
        if (chosenFile != null) {
            filePathTextField.setText(chosenFile.getAbsolutePath());
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    @FXML
    private void loadAssignments() {

    }

    private void updateColumnHeaders() {
        String noColumn = resources.getString("generic.none");

        pinNumberChoiceBox.getItems().clear();
        pinNumberChoiceBox.getItems().add(noColumn);

        pinNameChoiceBox.getItems().clear();

        pinUnitChoiceBox.getItems().clear();
        pinUnitChoiceBox.getItems().add(noColumn);

        pinBlockChoiceBox.getItems().clear();
        pinBlockChoiceBox.getItems().add(noColumn);

        if (parser == null) {
            return;
        }

        List<String> headers = parser.getHeaders(resources);
        if (headers != null) {
            pinNumberChoiceBox.getItems().addAll(headers);
            pinNameChoiceBox.getItems().addAll(headers);
            pinUnitChoiceBox.getItems().addAll(headers);
            pinBlockChoiceBox.getItems().addAll(headers);

            int headerNum = headers.size();
            if (headerNum >= 4) {
                pinNumberChoiceBox.getSelectionModel().clearAndSelect(1);
                pinNameChoiceBox.getSelectionModel().clearAndSelect(1);
                pinUnitChoiceBox.getSelectionModel().clearAndSelect(3);
                pinBlockChoiceBox.getSelectionModel().clearAndSelect(4);
            } else if (headerNum == 3) {
                pinNumberChoiceBox.getSelectionModel().clearAndSelect(1);
                pinNameChoiceBox.getSelectionModel().clearAndSelect(1);
                pinUnitChoiceBox.getSelectionModel().clearAndSelect(3);
                pinBlockChoiceBox.getSelectionModel().clearAndSelect(0);
            } else if (headerNum == 2) {
                pinNumberChoiceBox.getSelectionModel().clearAndSelect(1);
                pinNameChoiceBox.getSelectionModel().clearAndSelect(1);
                pinUnitChoiceBox.getSelectionModel().clearAndSelect(0);
                pinBlockChoiceBox.getSelectionModel().clearAndSelect(0);
            } else if (headerNum == 1) {
                pinNumberChoiceBox.getSelectionModel().clearAndSelect(0);
                pinNameChoiceBox.getSelectionModel().clearAndSelect(0);
                pinUnitChoiceBox.getSelectionModel().clearAndSelect(0);
                pinBlockChoiceBox.getSelectionModel().clearAndSelect(0);
            }
        }
    }
}
