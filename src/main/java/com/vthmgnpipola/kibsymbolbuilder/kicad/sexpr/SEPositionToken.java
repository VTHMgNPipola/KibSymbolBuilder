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

public class SEPositionToken extends SEToken<Double> {
    public SEPositionToken() {
        super("at");

        setProperty(0, 0d);
        setProperty(1, 0d);
        setProperty(2, 0d);
    }

    @Override
    public void read(RawSEToken token) {
        super.read(token);

        setProperty(0, Double.parseDouble(token.getValues().getFirst()));
        setProperty(1, Double.parseDouble(token.getValues().get(1)));
        setProperty(2, Double.parseDouble(token.getValues().get(2)));
    }

    public double getX() {
        return getProperties().getFirst();
    }

    public void setX(double x) {
        setProperty(0, x);
    }

    public double getY() {
        return getProperties().get(1);
    }

    public void setY(double y) {
        setProperty(1, y);
    }

    public double getAngle() {
        return getProperties().getLast();
    }

    public void setAngle(double angle) {
        setProperty(2, angle);
    }
}
