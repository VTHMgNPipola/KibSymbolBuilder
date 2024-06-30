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
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KSymbolEditorPanelController {
    private static final Logger logger = LoggerFactory.getLogger(KSymbolEditorPanelController.class);

    @FXML private TextField symbolName;
    @FXML private TextField symbolValue;
    @FXML private TextField symbolDatasheet;
    @FXML private TextField symbolReference;
    @FXML private TextField symbolDefaultFootprint;
    @FXML private TextField symbolKeywords;
    @FXML private TextField symbolDescription;

    @FXML private CheckBox symbolInBom;
    @FXML private CheckBox symbolOnBoard;
    @FXML private CheckBox symbolExcludeFromSim;

    @FXML
    private ListView<String> footprintFilterListView;

    private MKiCadSymbol symbol;

    @FXML
    private void initialize() {
        logger.info("Initializing KSymbolEditorPanelController...");

        footprintFilterListView.setEditable(true);
        footprintFilterListView.setCellFactory(TextFieldListCell.forListView());

        logger.info("Successfully initialized KSymbolEditorPanelController");
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

    public MKiCadSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(MKiCadSymbol symbol) {
        logger.info("Rebinding KiCadSymbol to symbol editor panel...");

        this.symbol = symbol;

        footprintFilterListView.itemsProperty().bindBidirectional(symbol.footprintFiltersProperty());

        // Set listeners
        symbolName.textProperty().bindBidirectional(symbol.nameProperty());
        symbolValue.textProperty().bindBidirectional(symbol.valueProperty());
        symbolDatasheet.textProperty().bindBidirectional(symbol.datasheetProperty());
        symbolReference.textProperty().bindBidirectional(symbol.referenceProperty());
        symbolDefaultFootprint.textProperty().bindBidirectional(symbol.footprintProperty());
        symbolKeywords.textProperty().bindBidirectional(symbol.keywordsProperty());
        symbolDescription.textProperty().bindBidirectional(symbol.descriptionProperty());

        symbolInBom.selectedProperty().bindBidirectional(symbol.includeInBomProperty());
        symbolOnBoard.selectedProperty().bindBidirectional(symbol.includeOnBoardProperty());
        symbolExcludeFromSim.selectedProperty().bindBidirectional(symbol.excludeFromSimProperty());
    }
}
