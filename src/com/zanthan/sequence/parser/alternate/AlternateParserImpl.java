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
package com.zanthan.sequence.parser.alternate;

import java.io.Reader;
import java.io.StringReader;

import com.zanthan.sequence.diagram.DiagramParserIface;
import com.zanthan.sequence.parser.Parser;
import com.zanthan.sequence.parser.ParserException;
import org.apache.log4j.Logger;

public class AlternateParserImpl implements Parser {

    private static final Logger log = Logger.getLogger(AlternateParserImpl.class);
    private AlternateParser theParser;
    private DiagramParserIface diagram;
    private boolean diagramChanged;

    /**
     * Parse the characters provided by a Reader. The results of the
     * parse are added to the diagram.
     *
     * @param r the reader to parse
     */
    public void parse(Reader r) throws ParserException {
        if (log.isDebugEnabled()) {
            log.debug("parse(" +
                    r +
                    ") theParser " + theParser +
                      " diagramChanged " + diagramChanged);
        }

        if (theParser == null) {
            theParser = new AlternateParser(r);
        } else {
            theParser.ReInit(r);
        }
        if (diagramChanged) {
            theParser.setDiagram(diagram);
        }
        try {
            theParser.parse(r);
        } catch (Throwable e) {
            throw new ParserException(e.getMessage(), e);
        }
    }

    /**
     * Parse a string. The results of the parse are added to the diagram.
     *
     * @param s the string to parse
     */
    public void parse(String s) throws ParserException {
        if (log.isDebugEnabled()) {
            log.debug("parse(" +
                    s +
                    ")");
        }

        parse(new StringReader(s));
    }

    /**
     * Set the diagram the parser should add nodes to.
     *
     * @param diagram the diagram to build
     */
    public void setDiagram(DiagramParserIface diagram) {
        if (log.isDebugEnabled()) {
            log.debug("setDiagram(" +
                    diagram +
                    ")");
        }

        if (this.diagram == null || this.diagram != diagram) {
            this.diagram = diagram;
            diagramChanged = true;
        }
    }
}
