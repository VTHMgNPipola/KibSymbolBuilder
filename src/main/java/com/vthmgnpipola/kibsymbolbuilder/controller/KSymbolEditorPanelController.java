package com.vthmgnpipola.kibsymbolbuilder.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KSymbolEditorPanelController {
    private static final Logger logger = LoggerFactory.getLogger(KSymbolEditorPanelController.class);

    @FXML
    private ListView<String> footprintFilterListView;

    @FXML
    private void initialize() {
        logger.debug("Initializing KSymbolEditorPanelController");

        footprintFilterListView.setEditable(true);
        footprintFilterListView.setCellFactory(TextFieldListCell.forListView());

        logger.debug("Successfully initialized KSymbolEditorPanelController");
    }

    @FXML
    public void addFootprintFilter() {
        footprintFilterListView.getItems().add("...");
        footprintFilterListView.scrollTo(footprintFilterListView.getItems().size() - 1);
        footprintFilterListView.edit(footprintFilterListView.getItems().size() - 1);
    }

    @FXML
    public void removeFootprintFilter() {
        if (!footprintFilterListView.getSelectionModel().isEmpty()) {
            for (Integer index : footprintFilterListView.getSelectionModel().getSelectedIndices()) {
                footprintFilterListView.getItems().remove((int) index);
            }
        }
    }
}
