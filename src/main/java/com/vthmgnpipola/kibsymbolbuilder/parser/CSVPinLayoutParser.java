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

import com.vthmgnpipola.kibsymbolbuilder.kicad.KiCadSymbolPin;
import com.vthmgnpipola.kibsymbolbuilder.kicad.PinBlock;
import com.vthmgnpipola.kibsymbolbuilder.kicad.UnitData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CSVPinLayoutParser implements PinLayoutParser {
    private static final Logger logger = LoggerFactory.getLogger(CSVPinLayoutParser.class);

    public static final String BANK_CSV_HEADER = "Bank";
    public static final String PIN_NAME_CSV_HEADER = "Pin Name";
    public static final String PIN_ADDRESS_CSV_HEADER = "Pin Address";

    @Override
    public List<UnitData> parse(Path path) {
        logger.info("Trying to parse '{}' as a CSV file", path.getFileName());

        try (Reader reader = Files.newBufferedReader(path)) {
            List<UnitData> units = new ArrayList<>();
            Map<String, Integer> bankMapping = new HashMap<>();
            AtomicInteger bankIndexCounter = new AtomicInteger(0);

            Iterable<CSVRecord> records = CSVFormat.RFC4180.builder()
                    .setHeader()
                    .setSkipHeaderRecord(false)
                    .build()
                    .parse(reader);

            for (CSVRecord record : records) {
                String bank = record.get(BANK_CSV_HEADER);
                String name = record.get(PIN_NAME_CSV_HEADER);
                String number = record.get(PIN_ADDRESS_CSV_HEADER);

                KiCadSymbolPin pin = new KiCadSymbolPin(name, number);

                if (bankMapping.containsKey(bank)) {
                    units.get(bankMapping.get(bank)).getBlocks().getFirst().getPins().add(pin);
                } else {
                    UnitData unit = new UnitData();
                    unit.setBlocksEnabled(false);
                    unit.setBlockPaddingSize(3);
                    unit.getBlocks().add(new PinBlock());

                    unit.getBlocks().getFirst().getPins().add(pin);

                    bankMapping.put(bank, bankIndexCounter.getAndIncrement());
                    units.add(bankMapping.get(bank), unit);
                }
            }

            return units;
        } catch (IOException e) {
            logger.error("Unable to parte file as a CSV file!", e);
            return null;
        }
    }
}
