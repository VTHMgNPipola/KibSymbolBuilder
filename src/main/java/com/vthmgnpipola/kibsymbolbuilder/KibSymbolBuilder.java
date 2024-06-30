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

import com.vthmgnpipola.kibsymbolbuilder.kicad.sexpr.SEKiCadLibrary;
import com.vthmgnpipola.kibsymbolbuilder.kicad.sexpr.SEKiCadSymbol;
import com.vthmgnpipola.kibsymbolbuilder.kicad.sexpr.SETextEffectsToken;
import com.vthmgnpipola.kibsymbolbuilder.sexpr.SEReader;
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
        SEKiCadLibrary library = new SEKiCadLibrary();
        SEKiCadSymbol symbol = new SEKiCadSymbol("74HC04");
        symbol.setValue("74HC04");
        symbol.getValue().getTextEffects().setJustification(SETextEffectsToken.Justification.TOP_LEFT);
        symbol.setDatasheet("http://www.ti.com/lit/gpn/sn74HC04");
        symbol.getDatasheet().getTextEffects().setJustification(SETextEffectsToken.Justification.BOTTOM_CENTER);
        symbol.setReference("U");
        symbol.getReference().getTextEffects().setJustification(SETextEffectsToken.Justification.CENTER_RIGHT);
        symbol.setDescription("Hex Inverter");
        symbol.setKeywords("TTL not inv");
        symbol.setFootprintFilters("DIP*W7.62mm* SSOP?14* TSSOP?14*");
        library.getChildren().add(symbol);

        SEWriter writer = new SEWriter();
        library.write(writer);
        writer.finish(Paths.get("./exemplo.txt"));

        // Read file
        SEKiCadLibrary readLibrary = new SEKiCadLibrary();

        SEReader reader = new SEReader();

        readLibrary.read(reader.read(Paths.get("./exemplo.txt")));
        readLibrary.write(writer);
        writer.finish(Paths.get("./exemplo2.txt"));

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

        loader.setLocation(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("com/vthmgnpipola/kibsymbolbuilder/fxml/KSymbolBuilder.fxml")));
        Parent root = loader.load();

        stage.setTitle(resourceBundle.getString("symbolEditor.title"));
        stage.setScene(new Scene(root));
        stage.show();
    }
}
