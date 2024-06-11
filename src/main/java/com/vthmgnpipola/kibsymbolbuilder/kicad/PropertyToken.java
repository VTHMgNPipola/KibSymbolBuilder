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

public class PropertyToken extends SEToken<String> {
    private XYAngleToken position;
    private TextEffectsToken textEffects;

    public PropertyToken(String name) {
        this(name, "");

        position = new XYAngleToken();
        getChildren().add(position);

        textEffects = new TextEffectsToken();
        getChildren().add(textEffects);
    }

    public PropertyToken(String propertyName, String propertyValue) {
        super("property");

        getProperties().add(propertyName);
        getProperties().add(propertyValue);
    }

    public void setPropertyName(String propertyName) {
        setProperty(0, propertyName);
    }

    public String getPropertyName() {
        return getProperties().getFirst();
    }

    public void setPropertyValue(String propertyValue) {
        setProperty(1, propertyValue);
    }

    public String getPropertyValue() {
        return getProperties().get(1);
    }

    private void resetProperties() {
        getProperties().clear();
        getProperties().add("");
        getProperties().add("");
    }

    public XYAngleToken getPosition() {
        return position;
    }

    public TextEffectsToken getTextEffects() {
        return textEffects;
    }
}
