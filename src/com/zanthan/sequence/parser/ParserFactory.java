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

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.zanthan.sequence.diagram.NodeFactory;

public class ParserFactory {

    private static ParserFactory theInstance;

    private ResourceBundle resources;
    private List parsers;
    private List nodeFactories;

    public synchronized static ParserFactory getInstance() {
        if (theInstance == null) {
            theInstance = new ParserFactory();
        }
        return theInstance;
    }

    private ParserFactory() {
        resources = ResourceBundle.getBundle("com.zanthan.sequence.parser.ParserFactory");

        parsers = new ArrayList();
        String[] parserClassNames = resources.getString("ParserClass").split("[ ]+");
        for (int i = 0; i < parserClassNames.length; i++) {
            String parserClassName = parserClassNames[i];
            try {
                Class parserClass = Class.forName(parserClassName);
                parsers.add((Parser) parserClass.newInstance());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            } catch (InstantiationException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
        }

        nodeFactories = new ArrayList();
        String[] nodeFactoryClassNames = resources.getString("NodeFactory").split("[ ]+");
        for (int i = 0; i < nodeFactoryClassNames.length; i++) {
            String nodeFactoryClassName = nodeFactoryClassNames[i];
            try {
                Class nodeFactoryClass = Class.forName(nodeFactoryClassName);
                nodeFactories.add((NodeFactory) nodeFactoryClass.newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            } catch (ClassNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
        }
    }

    public Parser getDefaultParser() {
        String defaultParserClassName = resources.getString("DefaultParser");
        for (int i = 0; i < parsers.size(); i++) {
            Parser p = (Parser) parsers.get(i);
            if (p.getClass().getName().equals(defaultParserClassName)) {
                return p;
            }
        }
        return null;
    }

    public List getAvailableParsers() {
        return parsers;
    }

    public Parser getParser(String name) {
        for (int i = 0; i < parsers.size(); i++) {
            Parser parser = (Parser) parsers.get(i);
            if (getParserName(parser).equals(name)) {
                return parser;
            }
        }
        return null;
    }

    public NodeFactory getNodeFactoryForParser(Parser p) {
        String parserClassName = p.getClass().getName();
        String nodeFactoryClassName = resources.getString(parserClassName + ".NodeFactory");
        for (int i = 0; i < nodeFactories.size(); i++) {
            NodeFactory nodeFactory = (NodeFactory) nodeFactories.get(i);
            if (nodeFactory.getClass().getName().equals(nodeFactoryClassName)) {
                return nodeFactory;
            }
        }
        return null;
    }

    public String getParserName(Parser p) {
        return getParserName(p.getClass().getName());
    }

    public String getParserName(String parserClassName) {
        try {
            return resources.getString(parserClassName + ".Name");
        } catch (MissingResourceException mre) {
            return parserClassName;
        }
    }

    private String getParserDescription(Parser p) {
        String parserClassName = p.getClass().getName();
        try {
            return resources.getString(parserClassName + ".Description");
        } catch (MissingResourceException mre) {
            return parserClassName;
        }
    }

    public String getParserDescription(String parserName) {
        return getParserDescription(getParser(parserName));
    }

    public String getExampleText(String parserName) {
        Parser p = getParser(parserName);
        String parserClassName = p.getClass().getName();
        String resourceKey = parserClassName + ".Example";
        try {
            return resources.getString(resourceKey);
        } catch (MissingResourceException mre) {
            return resourceKey;
        }
    }

}
