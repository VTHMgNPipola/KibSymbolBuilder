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

public class KiCadSymbolUnit extends SEToken<String> {
    private final SEToken<String> unitName;

    private String symbolName;
    private int unitId;

    public KiCadSymbolUnit(String symbolName, int unitId) {
        super("symbol");
        this.symbolName = symbolName;
        this.unitId = unitId;

        setProperty(0, symbolName + "_" + unitId + "_0");

        unitName = addChild("unit_name");
    }

    public String getSymbolName() {
        return symbolName;
    }

    public void setSymbolName(String symbolName) {
        this.symbolName = symbolName;
        setProperty(0, symbolName + "_" + unitId + "_0");
    }

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
        setProperty(0, symbolName + "_" + unitId + "_0");
    }

    public SEToken<String> getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName.setProperty(0, unitName);
    }

    @Override
    public void write(SEWriter writer) {
        if (!unitName.getProperties().isEmpty() && !unitName.getProperties().getFirst().isBlank()) {
            getChildren().add(unitName);
        }

        super.write(writer);

        getChildren().remove(unitName);
    }
}
