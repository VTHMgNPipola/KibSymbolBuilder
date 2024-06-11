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

public class TextEffectsToken extends SEToken<Object> {
    private final SEToken<Object> font;
    private final SEToken<Double> fontSize;
    private final SEToken<Justification> justification;
    private final SEToken<Boolean> hide;

    public TextEffectsToken() {
        super("effects");

        fontSize = new SEToken<>("size");
        fontSize.setProperty(0, 1.27d);
        fontSize.setProperty(1, 1.27d);

        font = addChild("font");
        font.getChildren().add(fontSize);

        justification = new SEToken<>("justify");
        hide = new SEToken<>("hide");
    }

    public void setFontSize(double height, double width) {
        fontSize.setProperty(0, height);
        fontSize.setProperty(1, width);
    }

    public void setJustification(Justification justification) {
        this.justification.setProperty(0, justification);
    }

    public void setHide(boolean hide) {
        this.hide.setProperty(0, hide);
    }

    @Override
    public void write(SEWriter writer) {
        if (!justification.getProperties().isEmpty() &&
                justification.getProperties().getFirst() != Justification.CENTER_CENTER) {
            font.getChildren().add(justification);
        }
        if (!hide.getProperties().isEmpty() && hide.getProperties().getFirst()) {
            getChildren().add(hide);
        }

        super.write(writer);

        font.getChildren().remove(justification);
        getChildren().remove(hide);
    }

    public enum Justification {
        TOP_LEFT, TOP_CENTER, TOP_RIGHT,
        CENTER_LEFT, CENTER_CENTER, CENTER_RIGHT,
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT;

        @Override
        public String toString() {
            String[] parts = name().toLowerCase().split("_");
            if (parts[0].equals("center") && parts[1].equals("center")) {
                return "";
            } else if (parts[0].equals("center")) {
                return parts[1];
            } else if (parts[1].equals("center")) {
                return parts[0];
            } else {
                return parts[1] + " " + parts[0]; // Inverted because left/right comes before top/bottom
            }
        }
    }
}
