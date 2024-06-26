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

public class KiCadSymbol extends SEToken<String> {
    public static final String EXCLUDE_FROM_SIM_TAG = "exclude_from_sim";
    public static final String IN_BOM_TAG = "in_bom";
    public static final String ON_BOARD_TAG = "on_board";
    private static final String REFERENCE_TAG = "Reference";
    public static final String VALUE_TAG = "Value";
    public static final String FOOTPRINT_TAG = "Footprint";
    public static final String DATASHEET_TAG = "Datasheet";
    public static final String DESCRIPTION_TAG = "Description";
    public static final String KEYWORDS_TAG = "ki_keywords";
    public static final String FOOTPRINT_FILTERS_TAG = "ki_fp_filters";

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

    public KiCadSymbol(String symbolName) {
        super("symbol");
        setProperty(0, symbolName);

        excludeFromSimulation = addChild(EXCLUDE_FROM_SIM_TAG, false);
        includeInBom = addChild(IN_BOM_TAG, true);
        includeOnBoard = addChild(ON_BOARD_TAG, true);

        reference = new PropertyToken(REFERENCE_TAG);
        value = new PropertyToken(VALUE_TAG);
        footprint = new PropertyToken(FOOTPRINT_TAG);
        datasheet = new PropertyToken(DATASHEET_TAG);
        description = new PropertyToken(DESCRIPTION_TAG);

        keywords = new PropertyToken(KEYWORDS_TAG);
        footprintFilters = new PropertyToken(FOOTPRINT_FILTERS_TAG);

        footprint.getTextEffects().setHide(true);
        datasheet.getTextEffects().setHide(true);
        description.getTextEffects().setHide(true);
        keywords.getTextEffects().setHide(true);
        footprintFilters.getTextEffects().setHide(true);

        getChildren().add(reference);
        getChildren().add(value);
        getChildren().add(footprint);
        getChildren().add(datasheet);
        getChildren().add(description);
        getChildren().add(keywords);
        getChildren().add(footprintFilters);
    }

    @Override
    public void read(RawSEToken token) {
        super.read(token);

        setSymbolName(token.getValues().getFirst());

        for (RawSEToken child : token.getChildren()) {
            switch (child.getName()) {
                case EXCLUDE_FROM_SIM_TAG -> excludeFromSimulation
                        .setProperty(0, Boolean.parseBoolean(child.getValues().getFirst()));
                case IN_BOM_TAG -> includeInBom
                        .setProperty(0, Boolean.parseBoolean(child.getValues().getFirst()));
                case ON_BOARD_TAG -> includeOnBoard
                        .setProperty(0, Boolean.parseBoolean(child.getValues().getFirst()));
                case PropertyToken.TOKEN_NAME -> {
                    String propertyName = child.getValues().getFirst();
                    switch (propertyName) {
                        case REFERENCE_TAG -> reference.read(child);
                        case VALUE_TAG -> value.read(child);
                        case FOOTPRINT_TAG -> footprint.read(child);
                        case DATASHEET_TAG -> datasheet.read(child);
                        case DESCRIPTION_TAG -> description.read(child);
                        case KEYWORDS_TAG -> keywords.read(child);
                        case FOOTPRINT_FILTERS_TAG -> footprintFilters.read(child);
                    }
                }
            }
        }
    }

    public String getSymbolName() {
        return getProperties().getFirst();
    }

    public void setSymbolName(String name) {
        setProperty(0, name);
        getChildren().forEach(token -> {
            if (token instanceof KiCadSymbolUnit unit) {
                unit.setSymbolName(name);
            }
        });
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
