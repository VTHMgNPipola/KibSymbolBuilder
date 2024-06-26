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
import java.io.Reader;
import java.io.StreamTokenizer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;

public class SEReader {
    private static final Logger logger = LoggerFactory.getLogger(SEReader.class);

    public RawSEToken read(Path path) {
        logger.info("Trying to read S-Expression from file '{}'", path.getFileName());

        try {
            return process(Files.newBufferedReader(path));
        } catch (Throwable t) {
            logger.error("Unable to open/read file!", t);
            return null;
        }
    }

    private RawSEToken process(Reader reader) throws IOException {
        StreamTokenizer tokenizer = new StreamTokenizer(reader);
        tokenizer.resetSyntax();

        tokenizer.whitespaceChars(0, ' ');
        tokenizer.wordChars(' ' + 1,255);
        tokenizer.ordinaryChar('(');
        tokenizer.ordinaryChar(')');
        tokenizer.ordinaryChar('\'');
        tokenizer.commentChar(';');
        tokenizer.quoteChar('"');

        Deque<RawSEToken> tokens = new ArrayDeque<>();

        tokenizer.nextToken();
        if (tokenizer.ttype == '(') {
            tokens.add(new RawSEToken());
        } else {
            throw new IOException("Invalid first token!");
        }

        RawSEToken currentToken = tokens.peekFirst();
        boolean nameToken = true;
        while (tokenizer.ttype != StreamTokenizer.TT_EOF) {
            tokenizer.nextToken();

            assert tokens.peekFirst() != null;
            if (nameToken) {
                tokens.peekFirst().setName(tokenizer.sval);
                nameToken = false;
                continue;
            }

            assert tokens.peekFirst() != null;
            if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
                String sval = tokenizer.sval;
                if (sval.equalsIgnoreCase("yes")) {
                    sval = "true";
                } else if (sval.equalsIgnoreCase("no")) {
                    sval = "false";
                }

                tokens.peekFirst().getValues().add(sval);
            } else if (tokenizer.ttype == '"') {
                tokens.peekFirst().getValues().add(tokenizer.sval);
            } else if (tokenizer.ttype == StreamTokenizer.TT_NUMBER) {
                tokens.peekFirst().getValues().add(String.valueOf(tokenizer.nval));
            } else if (tokenizer.ttype == ')') {
                currentToken = tokens.removeFirst();
                if (tokens.peekFirst() != null) {
                    tokens.peekFirst().getChildren().add(currentToken);
                }
            } else if (tokenizer.ttype == '(') {
                tokens.addFirst(new RawSEToken());
                nameToken = true;
            }
        }

        if (!tokens.isEmpty()) {
            throw new IllegalArgumentException("Syntax error on S-Expression! There are unclosed tokens!");
        }

        return currentToken;
    }
}
