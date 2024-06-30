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

package com.vthmgnpipola.kibsymbolbuilder.kicad.gui;

import com.vthmgnpipola.kibsymbolbuilder.kicad.sexpr.SEKiCadSymbol;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.StringJoiner;

public class MKiCadSymbol {
    private static final Logger logger = LoggerFactory.getLogger(MKiCadSymbol.class);

    private final BooleanProperty excludeFromSim;
    private final BooleanProperty includeInBom;
    private final BooleanProperty includeOnBoard;

    private final StringProperty name;
    private final StringProperty value;
    private final StringProperty datasheet;
    private final StringProperty reference;
    private final StringProperty footprint;
    private final StringProperty keywords;
    private final StringProperty description;
    private final ListProperty<String> footprintFilters;

    public MKiCadSymbol() {
        excludeFromSim = new SimpleBooleanProperty(false);
        includeInBom = new SimpleBooleanProperty(true);
        includeOnBoard = new SimpleBooleanProperty(true);

        name = new SimpleStringProperty();
        value = new SimpleStringProperty();
        datasheet = new SimpleStringProperty();
        reference = new SimpleStringProperty();
        footprint = new SimpleStringProperty();
        keywords = new SimpleStringProperty();
        description = new SimpleStringProperty();

        ObservableList<String> fpFiltersObservable = FXCollections.observableArrayList();
        footprintFilters = new SimpleListProperty<>(fpFiltersObservable);
    }

    public void loadSExpression(SEKiCadSymbol symbol) {
        logger.info("Loading KiCad symbol from S-Expression wrapper...");

        name.set(symbol.getSymbolName());
        value.set(symbol.getValue().getPropertyValue());
        datasheet.set(symbol.getDatasheet().getPropertyValue());
        reference.set(symbol.getReference().getPropertyValue());
        footprint.set(symbol.getFootprint().getPropertyValue());
        keywords.set(symbol.getKeywords().getPropertyValue());
        description.set(symbol.getDescription().getPropertyValue());

        footprintFilters.clear();
        footprintFilters.addAll(Arrays.stream(symbol.getFootprintFilters().getPropertyValue().split(" "))
                .filter(s -> !s.isBlank()).toList());
    }

    public SEKiCadSymbol generateSExpression() {
        logger.info("Generating S-Expression symbol...");

        SEKiCadSymbol symbol = new SEKiCadSymbol(name.get());
        symbol.setValue(value.get());
        symbol.setDatasheet(datasheet.get());
        symbol.setReference(reference.get());
        symbol.setFootprint(footprint.get());
        symbol.setKeywords(keywords.get());
        symbol.setDescription(description.get());

        StringJoiner joiner = new StringJoiner(" ");
        footprintFilters.forEach(joiner::add);
        symbol.setFootprintFilters(joiner.toString());

        return symbol;
    }

    public void setExcludeFromSim(boolean excludeFromSim) {
        this.excludeFromSim.set(excludeFromSim);
    }

    public void setIncludeInBom(boolean includeInBom) {
        this.includeInBom.set(includeInBom);
    }

    public void setIncludeOnBoard(boolean includeOnBoard) {
        this.includeOnBoard.set(includeOnBoard);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public void setDatasheet(String datasheet) {
        this.datasheet.set(datasheet);
    }

    public void setReference(String reference) {
        this.reference.set(reference);
    }

    public void setFootprint(String footprint) {
        this.footprint.set(footprint);
    }

    public void setKeywords(String keywords) {
        this.keywords.set(keywords);
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public void setFootprintFilters(String... footprintFilters) {
        this.footprintFilters.clear();
        this.footprintFilters.addAll(footprintFilters);
    }

    public boolean isExcludeFromSim() {
        return excludeFromSim.get();
    }

    public BooleanProperty excludeFromSimProperty() {
        return excludeFromSim;
    }

    public boolean isIncludeInBom() {
        return includeInBom.get();
    }

    public BooleanProperty includeInBomProperty() {
        return includeInBom;
    }

    public boolean isIncludeOnBoard() {
        return includeOnBoard.get();
    }

    public BooleanProperty includeOnBoardProperty() {
        return includeOnBoard;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getValue() {
        return value.get();
    }

    public StringProperty valueProperty() {
        return value;
    }

    public String getDatasheet() {
        return datasheet.get();
    }

    public StringProperty datasheetProperty() {
        return datasheet;
    }

    public String getReference() {
        return reference.get();
    }

    public StringProperty referenceProperty() {
        return reference;
    }

    public String getFootprint() {
        return footprint.get();
    }

    public StringProperty footprintProperty() {
        return footprint;
    }

    public String getKeywords() {
        return keywords.get();
    }

    public StringProperty keywordsProperty() {
        return keywords;
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public ObservableList<String> getFootprintFilters() {
        return footprintFilters.get();
    }

    public ListProperty<String> footprintFiltersProperty() {
        return footprintFilters;
    }
}
