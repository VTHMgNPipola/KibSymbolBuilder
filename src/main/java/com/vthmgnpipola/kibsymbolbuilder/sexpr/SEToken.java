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

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class SEToken<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -2934200357119406723L;

    private final String name;
    private final List<T> properties;
    private final List<SEToken<?>> children;

    public SEToken(String name) {
        this.name = name;
        properties = new ArrayList<>();
        children = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<T> getProperties() {
        return properties;
    }

    public void setProperty(int index, T property) {
        if (properties.size() <= index) {
            properties.add(property);
        }

        properties.set(index, property);
    }

    public List<SEToken<?>> getChildren() {
        return children;
    }

    @SafeVarargs
    protected final <U> SEToken<U> addChild(String name, U... properties) {
        SEToken<U> token = new SEToken<>(name);
        for (U property : properties) {
            token.getProperties().add(property);
        }
        children.add(token);

        return token;
    }

    public void write(SEWriter writer) {
        StringJoiner joiner = new StringJoiner(" ", "(", "");
        joiner.add(name);
        properties.forEach(p -> {
            switch (p) {
                case null -> {
                }
                case Boolean b -> joiner.add(b ? "yes" : "no");
                case String s -> joiner.add("\"" + s + "\"");
                default -> joiner.add(p.toString());
            }
        });

        if (!children.isEmpty()) {
            writer.appendLine(joiner.toString());

            writer.nestUp();
            for (SEToken<?> child : children) {
                child.write(writer);
            }
            writer.nestDown();

            writer.appendLine(")");
        } else {
            writer.appendLine(joiner + ")");
        }
    }
}
