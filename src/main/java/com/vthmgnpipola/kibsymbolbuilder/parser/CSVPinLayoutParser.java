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
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class CSVPinLayoutParser implements PinLayoutParser {
    private static final Logger logger = LoggerFactory.getLogger(CSVPinLayoutParser.class);

    private String data;
    private String pinNumberHeader;
    private String pinNameHeader;
    private String pinUnitHeader;
    private String pinBlockHeader;
    private boolean loadHeaders;

    private final List<CSVRecord> records;

    public CSVPinLayoutParser() {
        data = null;
        records = new ArrayList<>();
    }

    /*@Override
    public List<UnitData> parse(Path path) {
        logger.info("Trying to parse '{}' as a CSV file", path.getFileName());

        try (Reader reader = Files.newBufferedReader(path)) {
            List<UnitData> units = new ArrayList<>();
            Map<String, Integer> bankMapping = new HashMap<>();
            AtomicInteger bankIndexCounter = new AtomicInteger(0);

            Iterable<CSVRecord> records =

            for (CSVRecord record : records) {
                String bank = record.get(BANK_CSV_HEADER);
                String name = record.get(PIN_NAME_CSV_HEADER);
                String number = record.get(PIN_ADDRESS_CSV_HEADER);

                SEKiCadSymbolPin pin = new SEKiCadSymbolPin(name, number);

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
    }*/

    private boolean updateRecords() {
        records.clear();
        try {
            CSVFormat.Builder builder = CSVFormat.RFC4180.builder();
            if (loadHeaders) {
                builder.setHeader();
            }
            Iterable<CSVRecord> recordsIterable = builder
                    .build()
                    .parse(new StringReader(data));

            for (CSVRecord record : recordsIterable) {
                records.add(record);
            }

            logger.info("Parsed CSV file successfully.");
            return true;
        } catch (Throwable e) {
            logger.debug("Unable to parse CSV file.");
            return false;
        }
    }

    @Override
    public boolean load(String data) {
        this.data = data;
        return updateRecords();
    }

    @Override
    public void setLoadColumnHeaders(boolean loadColumnHeaders) {
        loadHeaders = loadColumnHeaders;
        if (data != null) {
            updateRecords();
        }
    }

    @Override
    public List<String> getHeaders(ResourceBundle resources) {
        if (records.isEmpty()) {
            return null;
        }

        List<String> headers = new ArrayList<>();
        Map<String, String> recordMap = records.getFirst().toMap();
        if (recordMap.isEmpty()) {
            String format = resources.getString("symbolEditor.pinAssignmentsLoader.columnNumber");
            for (int i = 0; i < records.getFirst().size(); i++) {
                headers.add(String.format(format, i + 1));
            }
        } else {
            headers.addAll(recordMap.keySet());
        }

        return headers;
    }

    @Override
    public void setPinNumberHeader(String header) {
        pinNumberHeader = header;
    }

    @Override
    public void setPinNameHeader(String header) {
        pinNameHeader = header;
    }

    @Override
    public void setPinUnitHeader(String header) {
        pinUnitHeader = header;
    }

    @Override
    public void setPinBlockHeader(String header) {
        pinBlockHeader = header;
    }

    @Override
    public List<UnitData> getPins() {
        return List.of();
    }
}
