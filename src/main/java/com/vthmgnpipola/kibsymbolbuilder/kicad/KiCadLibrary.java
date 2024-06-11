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

package com.vthmgnpipola.kibsymbolbuilder.kicad;

import com.vthmgnpipola.kibsymbolbuilder.sexpr.SEToken;
import com.vthmgnpipola.kibsymbolbuilder.sexpr.SEWriter;

import java.time.LocalDate;

public class KiCadLibrary extends SEToken<Object> {
    public static final String GENERATOR = "kib_symbol_builder";
    public static final String GENERATOR_VERSION = "1.0-SNAPSHOT";

    private final SEToken<Integer> version;

    public KiCadLibrary() {
        super("kicad_symbol_lib");

        version = addChild("version", 0);
        addChild("generator", GENERATOR);
        addChild("generator_version", GENERATOR_VERSION);
    }

    @Override
    public void write(SEWriter writer) {
        LocalDate localDate = LocalDate.now();
        String date = String.format("%04d%02d%02d", localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
        version.setProperty(0, Integer.parseInt(date));

        super.write(writer);
    }
}
