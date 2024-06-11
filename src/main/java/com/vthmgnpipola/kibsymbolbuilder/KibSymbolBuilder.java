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

package com.vthmgnpipola.kibsymbolbuilder;

import com.vthmgnpipola.kibsymbolbuilder.kicad.KiCadLibrary;
import com.vthmgnpipola.kibsymbolbuilder.kicad.KiCadSymbol;
import com.vthmgnpipola.kibsymbolbuilder.sexpr.SEWriter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.ResourceBundle;

public class KibSymbolBuilder extends Application {
    public static void main(String[] args) throws IOException {
        // Example S-Expression
        KiCadLibrary library = new KiCadLibrary();
        KiCadSymbol symbol = new KiCadSymbol("74HC04");
        symbol.setValue("74HC04");
        symbol.setDatasheet("http://www.ti.com/lit/gpn/sn74HC04");
        symbol.setReference("U");
        symbol.setDescription("Hex Inverter");
        symbol.setKeywords("TTL not inv");
        symbol.setFootprintFilters("DIP*W7.62mm* SSOP?14* TSSOP?14*");
        library.getChildren().add(symbol);

        SEWriter writer = new SEWriter();
        library.write(writer);
        writer.finish(Paths.get("./exemplo.txt"));
        // End example

        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Platform.setImplicitExit(true);

        ResourceBundle resourceBundle =
                ResourceBundle.getBundle("com.vthmgnpipola.kibsymbolbuilder.KibSymbolBuilder");

        FXMLLoader loader = new FXMLLoader();
        loader.setResources(resourceBundle);

        Parent root = loader.load(Objects.requireNonNull(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("com/vthmgnpipola/kibsymbolbuilder/fxml/KSymbolEditorPanel.fxml")).openStream()));

        stage.setTitle(resourceBundle.getString("symbolEditor.title"));
        stage.setScene(new Scene(root));
        stage.show();
    }
}
