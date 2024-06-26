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

import com.vthmgnpipola.kibsymbolbuilder.sexpr.RawSEToken;
import com.vthmgnpipola.kibsymbolbuilder.sexpr.SEToken;
import com.vthmgnpipola.kibsymbolbuilder.sexpr.SEWriter;

import java.util.StringJoiner;

public class TextEffectsToken extends SEToken<Void> {
    public static final String HIDE_TAG = "hide";
    public static final String JUSTIFY_TAG = "justify";
    public static final String FONT_TAG = "font";
    public static final String FONT_SIZE_TAG = "size";

    private final SEToken<Void> font;
    private final SEToken<Double> fontSize;
    private final SEToken<Justification> justification;
    private final SEToken<Boolean> hide;

    public TextEffectsToken() {
        super("effects");

        fontSize = new SEToken<>(FONT_SIZE_TAG);
        fontSize.setProperty(0, 1.27d);
        fontSize.setProperty(1, 1.27d);

        font = addChild(FONT_TAG);
        font.getChildren().add(fontSize);

        justification = new SEToken<>(JUSTIFY_TAG);
        hide = new SEToken<>(HIDE_TAG);
    }

    @Override
    public void read(RawSEToken token) {
        super.read(token);

        for (RawSEToken child : token.getChildren()) {
            switch (child.getName()) {
                case HIDE_TAG -> hide.setProperty(0, Boolean.parseBoolean(child.getValues().getFirst()));
                case FONT_TAG -> {
                    for (RawSEToken fontChild : child.getChildren()) {
                        switch (fontChild.getName()) {
                            case JUSTIFY_TAG -> {
                                StringJoiner joiner = new StringJoiner(" ");
                                fontChild.getValues().forEach(joiner::add);
                                justification.setProperty(0, Justification.convert(joiner.toString()));
                            }
                            case FONT_SIZE_TAG -> {
                                double height = Double.parseDouble(fontChild.getValues().getFirst());
                                double width = Double.parseDouble(fontChild.getValues().get(1));
                                setFontSize(height, width);
                            }
                        }
                    }
                }
            }
        }
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

        public static Justification convert(String string) {
            String horizontal;
            if (string.contains("left")) {
                horizontal = "LEFT";
            } else if (string.contains("right")) {
                horizontal = "RIGHT";
            } else {
                horizontal = "CENTER";
            }

            String vertical;
            if (string.contains("top")) {
                vertical = "TOP";
            } else if (string.contains("bottom")) {
                vertical = "BOTTOM";
            } else {
                vertical = "CENTER";
            }

            return valueOf(vertical + "_" + horizontal);
        }

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
