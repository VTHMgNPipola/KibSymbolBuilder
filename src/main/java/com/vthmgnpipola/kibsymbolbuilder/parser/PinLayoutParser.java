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

package com.vthmgnpipola.kibsymbolbuilder.parser;

import com.vthmgnpipola.kibsymbolbuilder.kicad.sexpr.UnitData;

import java.util.List;
import java.util.ResourceBundle;

public interface PinLayoutParser {
    boolean load(String data);

    void setLoadColumnHeaders(boolean loadColumnHeaders);

    List<String> getHeaders(ResourceBundle resources);

    void setPinNumberHeader(String header);

    void setPinNameHeader(String header);

    void setPinUnitHeader(String header);

    void setPinBlockHeader(String header);

    List<UnitData> getPins();
}
