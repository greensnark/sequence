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
package com.zanthan.sequence.diagram;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

import com.zanthan.sequence.layout.LayoutData;
import com.zanthan.sequence.parser.Parser;
import com.zanthan.sequence.parser.ParserException;

/**
 * The actual diagram.
 *
 * @author Alex Moffat
 */
public class Diagram implements DiagramParserIface {

    private static final Logger log = Logger.getLogger(Diagram.class);

    private Parser parser;
    private NodeFactory nodeFactory;
    private List rootObjects;
    private Map objectsByName;

    private EventListenerList listenerList = new EventListenerList();

    public Diagram(Parser parser, NodeFactory nodeFactory) {
        if (log.isDebugEnabled())
            log.debug("Diagram(" +
                      parser + ", " +
                      nodeFactory +
                      ")");
        setParserAndNodeFactory(parser, nodeFactory);
    }

    public void setParserAndNodeFactory(Parser parser, NodeFactory nodeFactory) {
        this.parser = parser;
        this.nodeFactory = nodeFactory;
        reset();
        parser.setDiagram(this);
        fireParserChangedEvent();
    }

    public String getParserClassName() {
        return parser.getClass().getName();
    }

    private void reset() {
        rootObjects = new ArrayList();
        objectsByName = new HashMap();
    }

    /**
     * Set the root object for the diagram.
     * @param rootObjectLifeLine
     */
    public void addRootObject(ObjectLifeLine rootObjectLifeLine) {
        rootObjects.add(rootObjectLifeLine);
    }

    public List getRootObjects() {
        return rootObjects;
    }

    /**
     * Return all of the objects from the last parse. The returned collection
     * contains instances of ObjectLifeLine.
     * @return
     */
    public Collection getAllObjects() {
        return objectsByName.values();
    }

    public Item findItem(ItemIdentifier identifier) {
        ObjectLifeLine objectLifeLine = (ObjectLifeLine) objectsByName.get(identifier.getObjectLifeLineName());
        if (objectLifeLine == null) {
            return null;
        }
        String methodExecutionName = identifier.getMethodExecutionName();
        if (methodExecutionName == null) {
            return objectLifeLine;
        }
        return objectLifeLine.findMethodExecution(methodExecutionName, identifier.getSeq());
    }

    public Call newCall(MethodExecution callingMethod, MethodExecution calledMethod, String returnType, Object userData) {
        if (log.isDebugEnabled()) {
            log.debug("newCall(" +
                    callingMethod + ", " +
                    calledMethod + ", " +
                    returnType + ", " +
                    userData +
                    ")");
        }
        return nodeFactory.newCall(callingMethod, calledMethod, returnType, userData);
    }

    public MethodExecution newMethodExecution(ObjectLifeLine objectLifeLine, String name, Object userData) {
        if (log.isDebugEnabled()) {
            log.debug("newMethodExecution(" +
                    objectLifeLine + ", " +
                    name + ", " +
                    userData +
                    ")");
        }
        return nodeFactory.newMethodExecution(objectLifeLine, name, userData);
    }

    public ObjectLifeLine newObjectLifeLine(String name, Object userData) {
        if (log.isDebugEnabled()) {
            log.debug("newObjectLifeLine(" +
                    name + ", " +
                    userData +
                    ")");
        }
        ObjectLifeLine objectLifeLine = nodeFactory.newObjectLifeLine(name, userData);
        objectsByName.put(objectLifeLine.getName(), objectLifeLine);
        return objectLifeLine;
    }

    public void parse(String s) throws ParserException {
        reset();
        parser.parse(s);
    }

    public void parse(Reader r) throws ParserException {
        reset();
        parser.parse(r);
    }

    public void layout(LayoutData layoutData) {
        
        Iterator allObjects = getAllObjects().iterator();
        while (allObjects.hasNext()) {
            ObjectLifeLine objectLifeLine = (ObjectLifeLine) allObjects.next();
            objectLifeLine.setInitialY(layoutData);
        }

        List roots = getRootObjects();
        for (int i = 0; i < roots.size(); i++) {
            ObjectLifeLine root = (ObjectLifeLine) roots.get(i);
            root.layout(layoutData);
        }
    }

    public void addParserChangedListener(ParserChangedListener l) {
        listenerList.add(ParserChangedListener.class, l);
    }

    public void removeParserChangedListener(ParserChangedListener l) {
        listenerList.remove(ParserChangedListener.class, l);
    }

    private synchronized void fireParserChangedEvent() {
        ParserChangedEvent pce = new ParserChangedEvent(this);
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ParserChangedListener.class)
                ((ParserChangedListener) listeners[i + 1]).parserChanged(pce);
        }
    }
}
