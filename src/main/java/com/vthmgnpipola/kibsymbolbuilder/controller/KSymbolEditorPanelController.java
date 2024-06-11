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

import com.vthmgnpipola.kibsymbolbuilder.kicad.KiCadSymbol;
import com.vthmgnpipola.kibsymbolbuilder.sexpr.SEWriter;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.StringJoiner;

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

    private KiCadSymbol symbol;
    private SEWriter seWriter;

    @FXML
    private void initialize() {
        logger.debug("Initializing KSymbolEditorPanelController");

        symbol = new KiCadSymbol(""); // For testing only

        footprintFilterListView.setEditable(true);
        footprintFilterListView.setCellFactory(TextFieldListCell.forListView());
        footprintFilterListView.itemsProperty().addListener((observable, ov, nv) -> { // FIXME
            StringJoiner joiner = new StringJoiner(" ");
            nv.forEach(joiner::add);
            symbol.setFootprintFilters(joiner.toString());
        });

        symbolName.textProperty().addListener((observable, ov, nv) -> symbol.setName(nv));
        symbolValue.textProperty().addListener((observable, ov, nv) -> symbol.setValue(nv));
        symbolDatasheet.textProperty().addListener((observable, ov, nv) -> symbol.setDatasheet(nv));
        symbolReference.textProperty().addListener((observable, ov, nv) -> symbol.setReference(nv));
        symbolDefaultFootprint.textProperty().addListener((observable, ov, nv) -> symbol.setFootprint(nv));
        symbolKeywords.textProperty().addListener((observable, ov, nv) -> symbol.setKeywords(nv));
        symbolDescription.textProperty().addListener((observable, ov, nv) -> symbol.setDescription(nv));
        
        symbolInBom.selectedProperty().addListener((observable, ov, nv) -> symbol.setIncludeInBom(nv));
        symbolOnBoard.selectedProperty().addListener((observable, ov, nv) -> symbol.setIncludeOnBoard(nv));
        symbolExcludeFromSim.selectedProperty().addListener((observable, ov, nv) -> symbol.setExcludeFromSimulation(nv));

        seWriter = new SEWriter();

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

    @FXML
    public void saveSymbol() throws IOException {
        // For testing only
        symbol.write(seWriter);
        seWriter.finish(Paths.get("./simbolo.txt"));
    }

    public KiCadSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(KiCadSymbol symbol) {
        this.symbol = symbol;
    }
}
