/*
 * SEQUENCE - A very simple sequence diagram editor
 * Copyright (C) 2002, 2003, 2004 Alex Moffat
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.zanthan.sequence.parser;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.zanthan.sequence.diagram.Call;
import com.zanthan.sequence.diagram.Diagram;
import com.zanthan.sequence.diagram.MethodExecution;
import com.zanthan.sequence.diagram.ObjectLifeLine;

public class SimpleUnparserImpl
        implements Unparser {

    private static final Logger log = Logger.getLogger(SimpleUnparserImpl.class);

    private Diagram diagram;
    private Set visitedMethods;
    private int indent;

    public void setDiagram(Diagram diagram) {
        this.diagram = diagram;
    }

    /**
     * Turn the diagram associated with this parser back into a string.
     *
     * @return
     */
    public String unparse() {
        visitedMethods = new HashSet();
        StringWriter sw = new StringWriter();
        try {
            unparse(sw);
        } catch (IOException e) {
            log.error("IOException", e);
            return null;
        }
        return sw.toString();
    }

    /**
     * Write the diagram associated with this parser to the writer.
     *
     * @param writer
     */
    public void unparse(Writer writer) throws IOException {
        indent = 0;
        boolean notFirst = false;
        List rootObjects = diagram.getRootObjects();
        for (int i = 0; i < rootObjects.size(); i++) {
            if (notFirst) {
                writer.write(" ");
            }
            notFirst = true;
            ObjectLifeLine objectLifeLine = (ObjectLifeLine) rootObjects.get(i);
            unparse(writer, objectLifeLine);
        }
    }

    private void unparse(Writer writer, ObjectLifeLine objectLifeLine) throws IOException {
        boolean notFirst = false;
        Iterator it = objectLifeLine.getMethodExecutions();
        while (it.hasNext()) {
            MethodExecution methodExecution = (MethodExecution) it.next();
            if (visitedMethods.contains(methodExecution)) {
                continue;
            }
            if (notFirst) {
                newlineAndIndent(writer);
            }
            notFirst = true;
            unparse(writer, methodExecution);
        }
    }

    private void newlineAndIndent(Writer writer) throws IOException {
        writer.write(System.getProperty("line.separator"));
        for (int i = 0; i < indent; ++i) {
            writer.write(" ");
        }
    }

    private void unparse(Writer writer, MethodExecution methodExecution) throws IOException {
        writer.write("(");
        indent += 2;
        writer.write(methodExecution.getObjectLifeLine().getName());
        writer.write(" ");
        writer.write(methodExecution.getName());
        Iterator callIterator = methodExecution.getCalls();
        while (callIterator.hasNext()) {
            newlineAndIndent(writer);
            unparse(writer, (Call) callIterator.next());
        }
        writer.write(")");
        indent -= 2;
    }

    private void unparse(Writer writer, Call call) throws IOException {
        indent += 2;
        writer.write("(");
        MethodExecution calledMethodExecution = call.getCalledMethodExecution();
        writer.write(calledMethodExecution.getObjectLifeLine().getName());
        writer.write(" ");
        writer.write(calledMethodExecution.getName());
        if (call.getReturn() != null && !call.getReturn().equals("void")) {
            writer.write(call.getReturn());
            writer.write(" ");
        }
        Iterator callIterator = calledMethodExecution.getCalls();
        while (callIterator.hasNext()) {
            newlineAndIndent(writer);
            unparse(writer, (Call) callIterator.next());
        }
        writer.write(")");
        indent -= 2;
    }
}
