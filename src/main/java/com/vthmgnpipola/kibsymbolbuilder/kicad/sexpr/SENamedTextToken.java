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

import com.vthmgnpipola.kibsymbolbuilder.sexpr.SEToken;

public class SENamedTextToken extends SEToken<String> {
    private final SETextEffectsToken textEffects;

    public SENamedTextToken(String name, String text) {
        super(name);
        setProperty(0, text);

        textEffects = new SETextEffectsToken();
        getChildren().add(textEffects);
    }

    public void setText(String text) {
        setProperty(0, text);
    }

    public SETextEffectsToken getTextEffects() {
        return textEffects;
    }
}
