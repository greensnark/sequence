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
package com.zanthan.sequence.diagram;

import java.io.PushbackReader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashSet;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.zanthan.sequence.diagram.Call;
import com.zanthan.sequence.diagram.MethodExecution;
import com.zanthan.sequence.diagram.NodeFactoryImpl;
import com.zanthan.sequence.diagram.ObjectLifeLine;
import com.zanthan.sequence.parser.SimpleParserImpl;
import com.zanthan.sequence.parser.Parser;
import com.zanthan.sequence.parser.alternate.AlternateParserImpl;
import junit.framework.TestCase;

public class TestAlternateParser
        extends TestCase {

    private static final Logger log =
            Logger.getLogger(TestAlternateParser.class);

    public TestAlternateParser(String name) {
        super(name);
    }

    private void printParserInfo(Diagram d, int objCount) {

        Collection objs = d.getAllObjects();
        assertEquals("Number of objects", objCount, objs.size());
        for (Iterator it = objs.iterator(); it.hasNext();) {
            log.debug(it.next());
        }
    }

    public void test00() {
        if (log.isDebugEnabled())
            log.debug("test00()");
        Diagram diagram = new Diagram(new AlternateParserImpl(), new NodeFactoryImpl());
        diagram.parse("anObject.aMethod -> aReturn {\n" +
                      "  anOtherObject .anotherMethod -> anotherReturn {\n" +
                      "    thirdObject.<<stereotype method>>\n" +
                      "  }\n" +
                      "}");

        assertEquals("Must be one root object", 1, diagram.getRootObjects().size());
        assertEquals("Root Object should have one method",
                     1,
                     ((ObjectLifeLine) diagram.getRootObjects().get(0)).getMethodExecutionCount());
        printParserInfo(diagram, 3);

        HashMap callCount = new HashMap();
        callCount.put("anObject", new Integer(1));
        callCount.put("anOtherObject", new Integer(1));
        callCount.put("thirdObject", new Integer(0));
        checkCallCount(diagram, callCount);

        for (Iterator it = diagram.getAllObjects().iterator(); it.hasNext();) {
            for (Iterator itMeth = ((ObjectLifeLine) it.next()).getMethodExecutions(); itMeth.hasNext();) {
                MethodExecution methodExecution = (MethodExecution) itMeth.next();
                checkMethodExecution(methodExecution);
            }
        }

        checkDuplicateMethodExecutions(diagram);
    }


    public void test01() {
        if (log.isDebugEnabled())
            log.debug("test01()");
        Diagram diagram = new Diagram(new SimpleParserImpl(), new NodeFactoryImpl());
        diagram.parse(new PushbackReader(new StringReader("(Comments newComment (CommentFactory newComment " +
                                                          "(Comment new (IDGen nextID) (Transaction add<this>))) " +
                                                          "(Comments addToCache))")));

        assertEquals("Must be one root object", 1, diagram.getRootObjects().size());
        assertEquals("Root Object should have two method",
                     2,
                     ((ObjectLifeLine) diagram.getRootObjects().get(0)).getMethodExecutionCount());
        printParserInfo(diagram, 5);

        HashMap callCount = new HashMap();
        callCount.put("anObject", new Integer(1));
        callCount.put("Comments", new Integer(2));
        callCount.put("CommentFactory", new Integer(1));
        callCount.put("Comment", new Integer(2));

        checkCallCount(diagram, callCount);

        for (Iterator it = diagram.getAllObjects().iterator(); it.hasNext();) {
            for (Iterator itMeth = ((ObjectLifeLine) it.next()).getMethodExecutions(); itMeth.hasNext();) {
                MethodExecution methodExecution = (MethodExecution) itMeth.next();
                checkMethodExecution(methodExecution);
            }
        }

        checkDuplicateMethodExecutions(diagram);
    }

    private void checkCallCount(Diagram d, HashMap callCount) {
        for (Iterator it = d.getAllObjects().iterator(); it.hasNext();) {
            ObjectLifeLine objectLifeLine = (ObjectLifeLine) it.next();
            int thisCallCount = 0;
            for (Iterator itMeth = objectLifeLine.getMethodExecutions(); itMeth.hasNext();) {
                MethodExecution methodExecution = (MethodExecution) itMeth.next();
                thisCallCount += methodExecution.getCallCount();
            }
            if (callCount.containsKey(objectLifeLine.getName()))
                assertEquals("Number of calls for " + objectLifeLine.getName(),
                             ((Integer) callCount.get(objectLifeLine.getName())).intValue(),
                             thisCallCount);
        }
    }


    private void checkMethodExecution(MethodExecution me) {
        log.debug("checkMethodExecution MethodExecution -> " + me);
        for (Iterator itCall = me.getCalls(); itCall.hasNext();) {
            Call call = (Call) itCall.next();
            log.debug("checkMethodExecution Call -> " + call);
            assertEquals("Should be calling from this method", me, call.getCallingMethodExecution());
        }
    }

    private void checkDuplicateMethodExecutions(Diagram d) {
        HashSet seenMethodExecutions = new HashSet();
        for (Iterator it = d.getAllObjects().iterator(); it.hasNext();) {
            for (Iterator itMeth = ((ObjectLifeLine) it.next()).getMethodExecutions(); itMeth.hasNext();) {
                MethodExecution methodExecution = (MethodExecution) itMeth.next();
                log.debug("checkDuplicateMethodExecutions " + methodExecution);
                if (seenMethodExecutions.contains(methodExecution))
                    fail("Found method execution " + methodExecution + " on two different objects");
                seenMethodExecutions.add(methodExecution);
            }
        }
    }

}
