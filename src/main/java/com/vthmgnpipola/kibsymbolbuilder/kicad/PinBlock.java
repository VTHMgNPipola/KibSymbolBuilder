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

import java.util.ArrayList;
import java.util.List;

public class PinBlock {
    private String blockName;
    private int index;
    private boolean placedRight;
    private List<KiCadSymbolPin> pins;

    public PinBlock() {
        pins = new ArrayList<>();
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isPlacedRight() {
        return placedRight;
    }

    public void setPlacedRight(boolean placedRight) {
        this.placedRight = placedRight;
    }

    public List<KiCadSymbolPin> getPins() {
        return pins;
    }

    public void setPins(List<KiCadSymbolPin> pins) {
        this.pins = pins;
    }
}
