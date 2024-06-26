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

public class SEKiCadSymbolPin extends SEToken<Object> {
    private final SEPositionToken position;
    private final SEToken<Double> length;
    private final SENamedTextToken pinName;
    private final SENamedTextToken pinNumber;

    public SEKiCadSymbolPin(String pinName, String pinNumber) {
        super("pin");

        setProperty(0, ElectricalType.UNSPECIFIED);
        setProperty(1, GraphicStyle.LINE);

        position = new SEPositionToken();
        getChildren().add(position);

        length = addChild("length", 2.54d);

        this.pinName = new SENamedTextToken("name", pinName);
        this.pinNumber = new SENamedTextToken("number", pinNumber);
    }

    public ElectricalType getElectricalType() {
        return (ElectricalType) getProperties().getFirst();
    }

    public void setElectricalType(ElectricalType electricalType) {
        setProperty(0, electricalType);
    }

    public GraphicStyle getGraphicStyle() {
        return (GraphicStyle) getProperties().get(1);
    }

    public void setGraphicStyle(GraphicStyle graphicStyle) {
        setProperty(1, graphicStyle);
    }

    public SEPositionToken getPosition() {
        return position;
    }
    
    public double getLength() {
        return length.getProperties().getFirst();
    }
    
    public void setLength(double length) {
        this.length.setProperty(0, length);
    }

    public SENamedTextToken getPinName() {
        return pinName;
    }

    public void setPinName(String pinName) {
        this.pinName.setText(pinName);
    }

    public SENamedTextToken getPinNumber() {
        return pinNumber;
    }

    public void setPinNumber(String pinNumber) {
        this.pinNumber.setText(pinNumber);
    }

    public enum ElectricalType {
        INPUT, OUTPUT, BIDIRECTIONAL, TRI_STATE, PASSIVE, FREE, UNSPECIFIED, POWER_IN, POWER_OUT, OPEN_COLLECTOR,
        OPEN_EMITTER, NO_CONNECT;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public enum GraphicStyle {
        LINE, INVERTED, CLOCK, INVERTED_CLOCK, INPUT_LOW, CLOCK_LOW, OUTPUT_LOW, EDGE_CLOCK_HIGH, NON_LOGIC;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}
