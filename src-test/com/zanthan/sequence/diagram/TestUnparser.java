package com.zanthan.sequence.diagram;

import java.io.PushbackReader;
import java.io.StringReader;

import org.apache.log4j.Logger;

import com.zanthan.sequence.parser.SimpleParserImpl;
import com.zanthan.sequence.parser.Unparser;
import com.zanthan.sequence.parser.SimpleUnparserImpl;
import junit.framework.TestCase;

/**
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
public class TestUnparser
 extends TestCase {

    private static final Logger log = Logger.getLogger(TestUnparser.class);

    public void test01() {
     if (log.isDebugEnabled())
         log.debug("test01(" +
                   ")");

        Diagram diagram = new Diagram(new SimpleParserImpl(), new NodeFactoryImpl());
        diagram.parse(new PushbackReader(new StringReader("(Comments newComment (CommentFactory newComment " +
                                                          "(Comment new (IDGen nextID) (Transaction add<this>))) " +
                                                          "(Comments addToCache))")));

        Unparser unparser = new SimpleUnparserImpl();
        unparser.setDiagram(diagram);

        String output = unparser.unparse();

        log.debug("***");
        log.debug(output);

        System.out.println(output);
    }
}
