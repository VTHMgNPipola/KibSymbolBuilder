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

package com.vthmgnpipola.kibsymbolbuilder.sexpr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SEWriter {
    private static final Logger logger = LoggerFactory.getLogger(SEWriter.class);

    private StringBuilder buffer;

    private int nestingLevel;

    public SEWriter() {
        nestingLevel = 0;
        buffer = new StringBuilder();

        logger.info("S-Expression Writer created.");
    }

    public void nestUp() {
        nestingLevel++;
    }

    public void nestDown() {
        nestingLevel--;
        if (nestingLevel < 0) {
            nestingLevel = 0;
        }
    }

    public void appendLine(String line) {
        buffer.append("\t".repeat(Math.max(0, nestingLevel)));
        buffer.append(line);
        buffer.append('\n');
    }

    public void finish(Path destinationPath) throws IOException {
        Files.writeString(destinationPath, buffer.toString());
        buffer = new StringBuilder();

        logger.info("S-Expression written to path '{}' and buffer cleared.",
                destinationPath.toAbsolutePath().getFileName().toString());
    }
}
