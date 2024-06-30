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
import com.vthmgnpipola.kibsymbolbuilder.kicad.sexpr.SEKiCadSymbol;
import javafx.fxml.FXML;
import javafx.scene.control.TreeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KSymbolBuilderController {
    private static final Logger logger = LoggerFactory.getLogger(KSymbolBuilderController.class);

    @FXML private TreeView<String> librariesTreeView;
    @FXML private KSymbolEditorPanelController editorController;

    @FXML
    private void initialize() {
        logger.info("Initializing KSymbolBuilderController");

        MKiCadSymbol kiCadSymbol = new MKiCadSymbol();

        editorController.setSymbol(kiCadSymbol);
        kiCadSymbol.loadSExpression(new SEKiCadSymbol(""));

        logger.info("Successfully initialized KSymbolBuilderController");
    }
}
