package com.zanthan.sequence.diagram;

import java.io.PushbackReader;
import java.io.StringReader;
import java.util.List;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.zanthan.sequence.parser.SimpleParserImpl;
import com.zanthan.sequence.parser.Parser;
import com.zanthan.sequence.diagram.NodeFactoryImpl;
import com.zanthan.sequence.layout.LayoutData;
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
 *
 * User: ajm
 * Date: Feb 8, 2003
 * Time: 8:48:10 PM
 */
public class TestLayout
 extends TestCase {

    private final static Logger log =
            Logger.getLogger(TestLayout.class);

    static class TestData {
        String stringToParse = null;
        String[] objectNames = null;
        private int methodCount;

        TestData(String stringToParse, String[] objectNames, int methodCount) {
            this.stringToParse = stringToParse;
            this.objectNames = objectNames;
            this.methodCount = methodCount;
        }
    }

    static TestData[] testData = new TestData[] {
        new TestData("(Comments newComment (CommentFactory newComment (Comment new (IDGen nextID) (Transaction add<this>))) (Comments addToCache))",
                     new String[] {"Comments", "CommentFactory", "Comment", "IDGen", "Transaction"},
                     6
        )
    };

    public void test00() {

        TestData testDataInst = testData[0];

        Diagram diagram = new Diagram(new SimpleParserImpl(), new NodeFactoryImpl());
        diagram.parse(new PushbackReader(new StringReader(testDataInst.stringToParse)));

        assertEquals("Must be one root object", 1, diagram.getRootObjects().size());
        assertEquals("Root Object should have two methods",
                     2,
                     ((ObjectLifeLine) diagram.getRootObjects().get(0)).getMethodExecutionCount());

        LayoutData layoutData = new LayoutData();
        List roots = diagram.getRootObjects();
        for (int i = 0; i < roots.size(); i++) {
            ObjectLifeLine root = (ObjectLifeLine) roots.get(i);
            root.layout(layoutData);
        }

        List objectLayouts = layoutData.getObjectLifeLines();
        assertEquals("Wrong number of layouts", testDataInst.objectNames.length, objectLayouts.size());
        for (int i = 0; i < objectLayouts.size(); i++) {
            ObjectLifeLine objectLifeLineLayout = (ObjectLifeLine)objectLayouts.get(i);
            assertEquals("Name should be the same", testDataInst.objectNames[i], objectLifeLineLayout.getName());
            assertEquals("Sequence should be same", i, objectLifeLineLayout.getSeq());
        }

        assertEquals("Should have n visited methods", testDataInst.methodCount, layoutData.getMethodExecutions().size());
    }

    public void test01() {

        TestData testDataInst = testData[0];

        Diagram diagram = new Diagram(new SimpleParserImpl(), new NodeFactoryImpl());
        diagram.parse(new PushbackReader(new StringReader(testDataInst.stringToParse)));

        assertEquals("Must be one root object", 1, diagram.getRootObjects().size());
        assertEquals("Root Object should have two methods",
                     2,
                     ((ObjectLifeLine) diagram.getRootObjects().get(0)).getMethodExecutionCount());

        LayoutData layoutData = new LayoutData();
         List roots = diagram.getRootObjects();
        for (int i = 0; i < roots.size(); i++) {
            ObjectLifeLine root = (ObjectLifeLine) roots.get(i);
            root.layout(layoutData);
        }

        List objectLayouts = layoutData.getObjectLifeLines();
        assertEquals("Wrong number of layouts", testDataInst.objectNames.length, objectLayouts.size());
        for (int i = 0; i < objectLayouts.size(); i++) {
            ObjectLifeLine objectLifeLineLayout = (ObjectLifeLine) objectLayouts.get(i);
            assertEquals("Name should be the same", testDataInst.objectNames[i], objectLifeLineLayout.getName());
            assertEquals("Sequence should be same", i, objectLifeLineLayout.getSeq());
            log.debug(objectLifeLineLayout);
        }

        assertEquals("Should have n visited methods", testDataInst.methodCount, layoutData.getMethodExecutions().size());

        for (int i = 0; i < objectLayouts.size(); i++) {
            ObjectLifeLine objectLifeLine = (ObjectLifeLine) objectLayouts.get(i);
            log.debug(objectLifeLine);
            Iterator methodIterator = objectLifeLine.getMethodExecutions();
            while (methodIterator.hasNext()) {
                MethodExecution methodExecution = (MethodExecution) methodIterator.next();
                log.debug(methodExecution);
            }
        }

        Set callLayouts = layoutData.getCalls();
        for (Iterator iterator = callLayouts.iterator(); iterator.hasNext();) {
            Call call = (Call) iterator.next();
            log.debug(call);
        }
    }
}
