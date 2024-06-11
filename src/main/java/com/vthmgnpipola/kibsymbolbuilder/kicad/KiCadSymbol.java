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

public class KiCadSymbol extends SEToken<String> {
    private final SEToken<Boolean> excludeFromSimulation;
    private final SEToken<Boolean> includeInBom;
    private final SEToken<Boolean> includeOnBoard;

    private final PropertyToken reference;
    private final PropertyToken value;
    private final PropertyToken footprint;
    private final PropertyToken datasheet;
    private final PropertyToken description;

    // These are "reserved" fields that "cannot be used for user defined properties", even though you can set these
    // using the symbol editor
    private final PropertyToken keywords;
    private final PropertyToken footprintFilters;

    public KiCadSymbol(String name) {
        super("symbol");
        setProperty(0, name);

        excludeFromSimulation = addChild("exclude_from_sim", false);
        includeInBom = addChild("in_bom", true);
        includeOnBoard = addChild("on_board", true);

        reference = new PropertyToken("Reference");
        value = new PropertyToken("Value");
        footprint = new PropertyToken("Footprint");
        datasheet = new PropertyToken("Datasheet");
        description = new PropertyToken("Description");

        keywords = new PropertyToken("ki_keywords");
        footprintFilters = new PropertyToken("ki_fp_filters");

        getChildren().add(reference);
        getChildren().add(value);
        getChildren().add(footprint);
        getChildren().add(datasheet);
        getChildren().add(description);
        getChildren().add(keywords);
        getChildren().add(footprintFilters);
    }

    public String getName() {
        return getProperties().getFirst();
    }

    public void setName(String name) {
        setProperty(0, name);
    }

    public boolean getExcludeFromSimulation() {
        return excludeFromSimulation.getProperties().getFirst();
    }

    public void setExcludeFromSimulation(boolean excludeFromSimulation) {
        this.excludeFromSimulation.setProperty(0, excludeFromSimulation);
    }

    public boolean getIncludeInBom() {
        return includeInBom.getProperties().getFirst();
    }

    public void setIncludeInBom(boolean includeInBom) {
        this.includeInBom.setProperty(0, includeInBom);
    }

    public boolean getIncludeOnBoard() {
        return includeOnBoard.getProperties().getFirst();
    }

    public void setIncludeOnBoard(boolean includeOnBoard) {
        this.includeOnBoard.setProperty(0, includeOnBoard);
    }

    public PropertyToken getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference.setPropertyValue(reference);
    }

    public PropertyToken getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value.setPropertyValue(value);
    }

    public PropertyToken getFootprint() {
        return footprint;
    }

    public void setFootprint(String footprint) {
        this.footprint.setPropertyValue(footprint);
    }

    public PropertyToken getDatasheet() {
        return datasheet;
    }

    public void setDatasheet(String datasheet) {
        this.datasheet.setPropertyValue(datasheet);
    }

    public PropertyToken getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description.setPropertyValue(description);
    }

    public PropertyToken getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords.setPropertyValue(keywords);
    }

    public PropertyToken getFootprintFilters() {
        return footprintFilters;
    }

    public void setFootprintFilters(String footprintFilters) {
        this.footprintFilters.setPropertyValue(footprintFilters);
    }
}
