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
package com.zanthan.sequence.swing.model;

import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.List;

import javax.swing.event.EventListenerList;
import javax.swing.event.SwingPropertyChangeSupport;

import org.apache.log4j.Logger;

import com.zanthan.sequence.diagram.Diagram;
import com.zanthan.sequence.diagram.Item;
import com.zanthan.sequence.diagram.ItemIdentifier;
import com.zanthan.sequence.diagram.NodeFactory;
import com.zanthan.sequence.diagram.ParserChangedListener;
import com.zanthan.sequence.layout.LayoutData;
import com.zanthan.sequence.parser.Parser;
import com.zanthan.sequence.parser.ParserException;
import com.zanthan.sequence.parser.ParserFactory;
import com.zanthan.sequence.swing.ExceptionHandler;

public class Model {

    private static final Logger log =
            Logger.getLogger(Model.class);

    private String s = " ";

    private SwingPropertyChangeSupport changeSupport = null;

    private File file = null;
    private boolean modified = false;
    private ModelAction newAction = null;
    private ModelAction openAction = null;
    private ModelAction saveAction = null;
    private ModelAction saveAsAction = null;

    private EventListenerList listenerList = new EventListenerList();

    private ExceptionHandler exceptionHandler = null;
    private Diagram diagram;

    private Item selectedItem;

    private static final String MODIFIED_PROPERTY_NAME = "modified";
    public static final String FILE_PROPERTY_NAME = "file";

    public Model(ExceptionHandler exceptionHandler, Diagram diagram) {
        this.exceptionHandler = exceptionHandler;
        this.diagram = diagram;
        changeSupport = new SwingPropertyChangeSupport(this);
        newAction = new NewAction(this);
        openAction = new OpenAction(this);
        saveAction = new SaveAction(this);
        saveAsAction = new SaveAsAction(this);
    }

    public void addPropertyChangeListener(String propName,
                                          PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(propName, listener);
    }

    public void removePropertyChangeListener(String propName,
                                             PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(propName, listener);
    }

    boolean loadNew() {
        setFile(null);
        internalSetText(this, "");
        setModified(false);
        return true;
    }

    public boolean readFromFile(File f) {
        try {
            StringBuffer sb = new StringBuffer(1024);
            BufferedReader br = new BufferedReader(new FileReader(f));
            int lineNumber = 0;
            String parserClassName = null;
            String s = null;
            while ((s = br.readLine()) != null) {
                if (lineNumber == 0) {
                    if (s.startsWith("# Parser Class:")) {
                        parserClassName = s.substring(15).trim();
                    }
                }
                if (s.startsWith("#")) {
                    continue;
                }
                sb.append(s);
                sb.append("\n");
            }
            br.close();
            setFile(f);
            if (parserClassName != null) {
                setParser(ParserFactory.getInstance().getParserName(parserClassName));
            } else {
                setParser(ParserFactory.getInstance().getParserName(ParserFactory.getInstance().getDefaultParser()));
            }
            internalSetText(this, sb.toString());
            setModified(false);
            return true;
        } catch (IOException ioe) {
            exceptionHandler.exception(ioe);
            return false;
        }
    }

    boolean writeToFile(File f) {
        try {
            PrintWriter out
                    = new PrintWriter(new BufferedWriter(new FileWriter(f)));
            out.println("# Parser Class: " + diagram.getParserClassName());
            BufferedReader br =
                    new BufferedReader(new StringReader(getText()));
            String s = null;
            while ((s = br.readLine()) != null) {
                out.println(s);
            }
            out.close();
            setFile(f);
            setModified(false);
            return true;
        } catch (IOException ioe) {
            exceptionHandler.exception(ioe);
            return false;
        }
    }

    boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        boolean oldModified = this.modified;
        this.modified = modified;
        if (log.isDebugEnabled())
            log.debug("setModified(...) oldModified " +
                      oldModified +
                      " modified " +
                      modified);
        if (modified != oldModified)
            changeSupport.firePropertyChange(MODIFIED_PROPERTY_NAME,
                                             oldModified,
                                             modified);
    }

    private void setFile(File f) {
        File oldFile = file;
        file = f;
        changeSupport.firePropertyChange(FILE_PROPERTY_NAME,
                                         oldFile,
                                         file);
    }

    public String getText() {
        return s;
    }

    public void setText(Object setter, String s) {
        internalSetText(setter, s);
        setModified(true);
    }

    public void setParser(String parserName) {
        if (log.isDebugEnabled()) {
            log.debug("setParser(" + parserName + ")");
        }
        Parser p = ParserFactory.getInstance().getParser(parserName);
        NodeFactory nf = ParserFactory.getInstance().getNodeFactoryForParser(p);
        diagram.setParserAndNodeFactory(p, nf);
    }

    private void internalSetText(Object setter, String s) {
        this.s = s;
        try {
            diagram.parse(s);
            fireModelParseSucceeded(setter, s, diagram);
        } catch (ParserException pe) {
            fireModelParseFailed(setter, s, diagram, pe);
        }
    }

    public void preferencesChanged() {
        fireModelPreferencesChanged(this, getText(), diagram);
    }

    public void layout(LayoutData layoutData) {
        diagram.layout(layoutData);
    }

    public Item findItem(ItemIdentifier identifier) {
        return diagram.findItem(identifier);
    }

    public List getRootObjects() {
        return diagram.getRootObjects();
    }

    public void addParserChangedListener(ParserChangedListener l) {
        diagram.addParserChangedListener(l);
    }

    public void removeParserChangedListener(ParserChangedListener l) {
        diagram.removeParserChangedListener(l);
    }

    public String getParserName() {
        return ParserFactory.getInstance().getParserName(diagram.getParserClassName());
    }

    public File getFile() {
        return file;
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Item item) {
        if (item != null) {
            item.select();
        }
        if (selectedItem != null) {
            selectedItem.deselect();
        }
        selectedItem = item;
    }

    public ModelAction getNewAction() {
        return newAction;
    }

    public ModelAction getOpenAction() {
        return openAction;
    }

    public ModelAction getSaveAction() {
        return saveAction;
    }

    public ModelAction getSaveAsAction() {
        return saveAsAction;
    }

    public void addModelListener(ModelListener l) {
        listenerList.add(ModelListener.class, l);
    }

    public void removeModelListener(ModelListener l) {
        listenerList.remove(ModelListener.class, l);
    }

    private synchronized void fireModelParseSucceeded(Object setter, String s, Diagram diagram) {
        ModelParseSucceededEvent mte = new ModelParseSucceededEvent(setter, s, diagram);
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ModelListener.class)
                ((ModelListener) listeners[i + 1]).modelParseSucceeded(mte);
        }
    }

    private synchronized void fireModelParseFailed(Object setter, String s, Diagram diagram, ParserException pe) {
        ModelParseFailedEvent mte = new ModelParseFailedEvent(setter, s, diagram, pe);
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ModelListener.class)
                ((ModelListener) listeners[i + 1]).modelParseFailed(mte);
        }
    }

    private synchronized void fireModelPreferencesChanged(Object setter, String s, Diagram diagram) {
        ModelPreferencesChangedEvent mte = new ModelPreferencesChangedEvent(setter, s, diagram);
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ModelListener.class)
                ((ModelListener) listeners[i + 1]).modelPreferencedChanged(mte);
        }
    }

}
