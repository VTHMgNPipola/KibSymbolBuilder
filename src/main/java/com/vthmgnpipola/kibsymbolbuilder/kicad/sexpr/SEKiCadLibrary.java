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

package com.vthmgnpipola.kibsymbolbuilder.kicad.sexpr;

import com.vthmgnpipola.kibsymbolbuilder.sexpr.RawSEToken;
import com.vthmgnpipola.kibsymbolbuilder.sexpr.SEToken;
import com.vthmgnpipola.kibsymbolbuilder.sexpr.SEWriter;

import java.time.LocalDate;

public class SEKiCadLibrary extends SEToken<Void> {
    public static final String GENERATOR = "kib_symbol_builder";
    public static final String GENERATOR_VERSION = "1.0-SNAPSHOT";

    private static final String VERSION_TAG = "version";
    private static final String GENERATOR_TAG = "generator";
    private static final String GENERATOR_VERSION_TAG = "generator_version";

    private String libraryName;

    private final SEToken<Integer> version;
    private final SEToken<String> generator;
    private final SEToken<String> generatorVersion;

    public SEKiCadLibrary() {
        super("kicad_symbol_lib");

        version = addChild(VERSION_TAG, 0);
        generator = addChild(GENERATOR_TAG, GENERATOR);
        generatorVersion = addChild(GENERATOR_VERSION_TAG, GENERATOR_VERSION);
    }

    public String getGenerator() {
        return generator.getProperties().getFirst();
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    @Override
    public void write(SEWriter writer) {
        LocalDate localDate = LocalDate.now();
        String date = String.format("%04d%02d%02d", localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
        version.setProperty(0, Integer.parseInt(date));

        super.write(writer);
    }

    @Override
    public void read(RawSEToken token) {
        super.read(token);

        for (RawSEToken child : token.getChildren()) {
            switch (child.getName()) {
                case VERSION_TAG -> version.setProperty(0, Integer.parseInt(child.getValues().getFirst()));
                case GENERATOR_TAG -> generator.setProperty(0, child.getValues().getFirst());
                case GENERATOR_VERSION_TAG -> generatorVersion.setProperty(0, child.getValues().getFirst());
                default -> {
                    SEKiCadSymbol symbol = new SEKiCadSymbol(null);
                    symbol.read(child);
                    getChildren().add(symbol);
                }
            }
        }
    }
}
