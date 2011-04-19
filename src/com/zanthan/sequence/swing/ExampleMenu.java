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
package com.zanthan.sequence.swing;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.io.IOException;
import java.util.ResourceBundle;

import com.zanthan.sequence.swing.model.ModifiedConfirmAction;
import com.zanthan.sequence.swing.model.Model;
import com.zanthan.sequence.diagram.ParserChangedListener;
import com.zanthan.sequence.diagram.ParserChangedEvent;
import com.zanthan.sequence.parser.ParserFactory;
import com.zanthan.sequence.parser.Parser;

/**
 * User: ajm
 * Date: Jan 18, 2004
 * Time: 8:18:03 PM
 */
public class ExampleMenu extends JMenu implements ParserChangedListener {

    private final static Logger log =
            Logger.getLogger(ExampleMenu.class);

    private Model model;
    private String currentParserName;

    public ExampleMenu(Model model) {
        this.model = model;

        setText(SequenceResources.getString("ExamplesMenu.name"));
        setToolTipText(SequenceResources.getString("ExamplesMenu.shortDesc"));

        loadExamples(ParserFactory.getInstance().getDefaultParser().getClass().getName());
    }

    private void loadExamples(String parserName) {

        if (currentParserName != null && currentParserName.equals(parserName)) {
            return;
        }

        // Get rid of all the menu entries
        removeAll();

        String xmlFileName = computeExamplesFileName(parserName);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            InputStream in = this.getClass().getResourceAsStream(xmlFileName);
            if (in == null) {
                log.error("No examples provided for parser " + parserName);
                return;
            }
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(in);

            Element root = doc.getDocumentElement();
            NodeList examples = root.getElementsByTagName("example");

            for (int i = 0; i < examples.getLength(); ++i) {
                Element example = (Element) examples.item(i);
                if (example.hasAttribute("name")) {
                    MenuAction action = new MenuAction(example.getAttribute("name"),
                                                       example.getAttribute("short-description"));

                    StringBuffer exampleText = new StringBuffer();
                    NodeList children = example.getChildNodes();
                    for (int j = 0; j < children.getLength(); ++j) {
                        Node n = children.item(j);
                        if (n.getNodeType() == Node.CDATA_SECTION_NODE) {
                            exampleText.append(((Text)n).getData());
                        }
                    }
                    action.setExample(exampleText.toString());
                    JMenuItem item = new JMenuItem(action);
                    add(item);
                }
            }
        } catch (ParserConfigurationException e) {
            log.error("Could not configure xml parser", e);
        } catch (IOException e) {
            log.error("IOError parsing examples.xml", e);
        } catch (SAXException e) {
            log.error("SAXException parsing examples.xml", e);
        }
    }

    /**
     * Given the name of parser class return the file name of the examples.xml file
     * that is in the same package as the class. For instance if parserClassName was
     * com.foo.bar.Parser then the returned name would be /com/foo/bar/examples.xml
     *
     * @param parserClassName the name of the parser class
     * @return the name of the file of examples for the parser
     */
    private String computeExamplesFileName(String parserClassName) {
        String packageName;
        int i = parserClassName.lastIndexOf('.');
        if (i == -1) {
            packageName = parserClassName;
        } else {
            packageName = parserClassName.substring(0, i + 1);
        }

        String fileName = packageName.replace('.', '/') + "examples.xml";
        if (!fileName.startsWith("/")) {
            fileName = "/" + fileName;
        }
        if (log.isDebugEnabled()) {
            log.debug("computeExamplesFileName(" + parserClassName + ") -> " + fileName);
        }
        return fileName;
    }

    public void parserChanged(ParserChangedEvent pce) {
        loadExamples(pce.getParserClassName());
    }

    private class MenuAction extends ModifiedConfirmAction {

        private String example;

        public MenuAction(String name, String shortDescription) {
            super(name, shortDescription, model);
        }

        private void setExample(String example) {
            this.example = example;
        }

        protected boolean doIt() {
            model.setText(this, example);
            model.setModified(false);
            return true;
        }

        protected String getConfirmMessage() {
            return SequenceResources.getString("ExamplesMenu.confirmMessage");
        }

        protected String getConfirmTitle() {
            return SequenceResources.getString("ExamplesMenu.confirmTitle");
        }
    }
}
