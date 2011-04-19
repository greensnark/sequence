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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ResourceBundle;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.help.HelpBroker;

import org.apache.log4j.Logger;

import com.zanthan.sequence.diagram.Diagram;
import com.zanthan.sequence.diagram.NodeFactory;
import com.zanthan.sequence.parser.Parser;
import com.zanthan.sequence.parser.ParserFactory;
import com.zanthan.sequence.swing.model.Model;
import com.zanthan.sequence.swing.preferences.PreferencesAction;

/**
 * The main frame that holds the sequence panel. It is the sequence panel that contains the diagram and editor.
 */
public class Sequence
        extends JFrame
        implements PropertyChangeListener, ExceptionHandler {

    private static final Logger log =
            Logger.getLogger(Sequence.class);

    private static Sequence thisInstance = null;

    private HelpBroker helpBroker = null;
    private SequencePanel sequencePanel;

    public static Sequence getInstance() {
        return thisInstance;
    }

    /**
     * Create a new instance of Sequence.
     *
     * @param offerParserChoice if true then a button is provided to switch parsers
     */
    public Sequence(boolean offerParserChoice) {

        initializeHelpBroker();

        setTitle((File) null);

        Parser parser = ParserFactory.getInstance().getDefaultParser();
        NodeFactory nodeFactory = ParserFactory.getInstance().getNodeFactoryForParser(parser);
        Diagram diagram = new Diagram(parser, nodeFactory);
        sequencePanel = new SequencePanel(offerParserChoice, this, diagram);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu =
                new JMenu(SequenceResources.getString("Sequence.menu.file.label"));

        menuBar.add(fileMenu);

        fileMenu.add(new JMenuItem(sequencePanel.getNewAction()));
        fileMenu.add(new JMenuItem(sequencePanel.getOpenAction()));
        fileMenu.add(new JMenuItem(sequencePanel.getSaveAction()));
        fileMenu.add(new JMenuItem(sequencePanel.getSaveAsAction()));
        fileMenu.add(new JMenuItem(sequencePanel.getExportAction()));
        fileMenu.add(new JSeparator());
        final ExitAction exitAction = new ExitAction(sequencePanel.getModel());
        fileMenu.add(new JMenuItem(exitAction));

        JMenu editMenu =
                new JMenu(SequenceResources.getString("Sequence.menu.edit.label"));

        menuBar.add(editMenu);

        editMenu.add(new JMenuItem(sequencePanel.getCutAction()));
        editMenu.add(new JMenuItem(sequencePanel.getCopyAction()));
        editMenu.add(new JMenuItem(sequencePanel.getPasteAction()));
        editMenu.add(new JSeparator());
        editMenu.add(new JMenuItem(new PreferencesAction(this)));

        JMenu helpMenu =
                new JMenu(SequenceResources.getString("Sequence.menu.help.label"));

        menuBar.add(helpMenu);

        ExampleMenu exampleMenu = new ExampleMenu(sequencePanel.getModel());
        diagram.addParserChangedListener(exampleMenu);
        helpMenu.add(exampleMenu);

        if (getHelpBroker() != null) {
            helpMenu.add(new JMenuItem(new HelpAction(getHelpBroker())));
        }
        helpMenu.add(new JMenuItem(new AboutAction()));

        getContentPane().add(sequencePanel, BorderLayout.CENTER);

        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        int width = size.width / 2;
        int height = size.height / 2;

        pack();
        setSize(new Dimension(width, height));
        setLocation(width / 2, height / 2);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exitAction.confirmThenDoIt();
            }
        });

        setVisible(true);

        sequencePanel.setDividerLocation(0.6d);

        sequencePanel.getModel().addPropertyChangeListener(Model.FILE_PROPERTY_NAME, this);
    }

    /**
     * Setup the helpBroker field.
     */
    private void initializeHelpBroker() {
        String helpSetName = "help/sequence.hs";
        ClassLoader cl = this.getClass().getClassLoader();
        try {
            URL hsURL = HelpSet.findHelpSet(cl, helpSetName);
            HelpSet hs = new HelpSet(null, hsURL);
            helpBroker = hs.createHelpBroker("main");
            // Enable help key on main panel to point to this
            helpBroker.enableHelpKey(this.getRootPane(), "top", hs);
        } catch (HelpSetException e) {
            log.error("Could not load helpset", e);
        }
    }

    public HelpBroker getHelpBroker() {
        return helpBroker;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (!evt.getPropertyName().equals("file"))
            return;
        setTitle((File) evt.getNewValue());
    }

    /**
     * Set the title of the frame to the title from the properties bundle plus the
     * name of the file passed in.
     *
     * @param f supplies name to put in title bar
     */
    private void setTitle(File f) {

        String name = (f == null) ? "" : f.getName();
        super.setTitle(SequenceResources.getString("Sequence.frame.title") + " " + name);
    }

    public void preferencesChanged() {
        sequencePanel.preferencesChanged();
    }

    /**
     * This method should be called whenever an exception is
     * caught. It will display the exception to the user.
     *
     * @param e the exception
     */
    public void exception(Exception e) {
        log.error("exception", e);
    }
}
