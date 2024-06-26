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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class KiCadSymbolUnit extends SEToken<String> {
    private final SEToken<String> unitName;

    private String symbolName;
    private int unitId;

    private int width;

    private int blockPaddingSize;
    private boolean enableBlocks;
    private final List<PinBlock> pinBlocks;

    public KiCadSymbolUnit(String symbolName, int unitId) {
        super("symbol");
        this.symbolName = symbolName;
        this.unitId = unitId;

        setProperty(0, symbolName + "_" + unitId + "_0");

        unitName = addChild("unit_name");

        blockPaddingSize = 3;
        enableBlocks = false;
        pinBlocks = new ArrayList<>();
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

    public int getBlockPaddingSize() {
        return blockPaddingSize;
    }

    public void setBlockPaddingSize(int blockPaddingSize) {
        this.blockPaddingSize = blockPaddingSize;
    }

    public boolean isEnableBlocks() {
        return enableBlocks;
    }

    public void setEnableBlocks(boolean enableBlocks) {
        this.enableBlocks = enableBlocks;
    }

    public List<PinBlock> getPinBlocks() {
        return pinBlocks;
    }

    @Override
    public void write(SEWriter writer) {
        if (!unitName.getProperties().isEmpty() && !unitName.getProperties().getFirst().isBlank()) {
            getChildren().add(unitName);
        }

        if (!enableBlocks) {
            AtomicInteger totalPins = new AtomicInteger(0);
            pinBlocks.forEach(block -> totalPins.addAndGet(block.getPins().size()));

            int pinsPerSide = totalPins.get() / 2;
        }

        super.write(writer);

        getChildren().remove(unitName);
        pinBlocks.forEach(block -> getChildren().removeAll(block.getPins()));
    }
}
