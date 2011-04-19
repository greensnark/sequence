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
package com.zanthan.sequence.swing.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.help.CSH;

import org.apache.log4j.Logger;

import com.zanthan.sequence.swing.ActionResources;
import com.zanthan.sequence.swing.ExceptionHandler;
import com.zanthan.sequence.swing.model.Model;
import com.zanthan.sequence.swing.model.ModelEvent;
import com.zanthan.sequence.swing.model.ModelListener;
import com.zanthan.sequence.swing.model.ModelParseFailedEvent;
import com.zanthan.sequence.swing.model.ModelParseSucceededEvent;
import com.zanthan.sequence.swing.model.ModelPreferencesChangedEvent;
import com.zanthan.sequence.diagram.ParserChangedListener;
import com.zanthan.sequence.diagram.ParserChangedEvent;

public class Editor
        extends JPanel
        implements DocumentListener, ModelListener, ParserChangedListener {

    private static final Logger log =
            Logger.getLogger(Editor.class);

    private ExceptionHandler exceptionHandler = null;
    private Model model = null;
    private JEditorPane editPane = new JEditorPane();

    private Action cutAction = null;
    private Action copyAction = null;
    private Action pasteAction = null;

    private boolean ignoreChange = false;
    private boolean documentChanged = false;
    private long lastChangeTime = 0;

    public Editor(boolean offerParserChoice, ExceptionHandler exceptionHandler, Model model) {
        this.exceptionHandler = exceptionHandler;
        this.model = model;
        // Setup initial help id for the editor panel
        CSH.setHelpIDString(this, "syntax");
        // Listen for changes to the parser. The helpid is changed when the parser is.
        model.addParserChangedListener(this);

        setLayout(new BorderLayout());

        if (offerParserChoice) {
            addParserChoicePanel(model);
        }

        editPane.setFont(new Font("Monospaced",
                                  Font.PLAIN,
                                  editPane.getFont().getSize() + 1));
        add(new JScrollPane(editPane,
                            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS),
            BorderLayout.CENTER);

        editPane.setText(model.getText());
        editPane.getDocument().addDocumentListener(this);
        new Thread(new ChangeNotifier()).start();
    }

    private void addParserChoicePanel(Model model) {
        JPanel parserChoice = new JPanel();
        BorderLayout layout = new BorderLayout();
        layout.setHgap(5);
        parserChoice.setLayout(layout);
        parserChoice.add(new JButton(new ChooseParserAction(model)), BorderLayout.WEST);
        parserChoice.add(new ParserIdentifierLabel(model), BorderLayout.CENTER);
        add(parserChoice, BorderLayout.NORTH);
    }

    public synchronized Action getCutAction() {
        if (cutAction == null) {
            cutAction = new DefaultEditorKit.CutAction();
            initAction(cutAction, "CutAction");
        }
        return cutAction;
    }

    public synchronized Action getPasteAction() {
        if (pasteAction == null) {
            pasteAction = new DefaultEditorKit.PasteAction();
            initAction(pasteAction, "PasteAction");
        }
        return pasteAction;
    }

    public synchronized Action getCopyAction() {
        if (copyAction == null) {
            copyAction = new DefaultEditorKit.CopyAction();
            initAction(copyAction, "CopyAction");
        }
        return copyAction;
    }

    private static void initAction(Action act, String resourcePrefix) {
        ActionResources actionResources = new ActionResources(resourcePrefix);

        act.putValue(Action.NAME, actionResources.getName());
        act.putValue(Action.SHORT_DESCRIPTION, actionResources.getShortDescription());
        act.putValue(Action.SMALL_ICON, actionResources.getIcon());
    }

    public void parserChanged(ParserChangedEvent pce) {
        CSH.setHelpIDString(this, pce.getParserClassName());
    }

    /**
     * Called when the text has been successfully parsed and the model updated.
     *
     * @param mpse contains the string parsed and the diagram
     */
    public void modelParseSucceeded(ModelParseSucceededEvent mpse) {
        if (editPane.getForeground() != Color.black) {
            editPane.setForeground(Color.black);
        }
        if (mpse.getSource() == this)
            return;

        if (log.isDebugEnabled())
            log.debug("modelParseSucceeded(...) changing");

        updateDocumentText(mpse);
    }

    /**
     * Called when the parser detects an error in parsing the text.
     *
     * @param mpfe contains the string being parsed, the diagram, and the parser exception
     */
    public void modelParseFailed(ModelParseFailedEvent mpfe) {
        if (editPane.getForeground() != Color.red) {
            editPane.setForeground(Color.red);
        }
        if (mpfe.getSource() == this)
            return;

        updateDocumentText(mpfe);
    }

    /**
     * Called when the preferences have changed.
     *
     * @param mpce contains the string parsed and the diagram
     */
    public void modelPreferencedChanged(ModelPreferencesChangedEvent mpce) {
        // Do nothing
    }

    private void updateDocumentText(ModelEvent mte) {
        Document doc = editPane.getDocument();
        try {
            ignoreChange = true;
            doc.remove(0, doc.getLength());
            doc.insertString(0, mte.getText(), null);
        } catch (BadLocationException ble) {
            exceptionHandler.exception(ble);
        } finally {
            ignoreChange = false;
        }
    }

    private void documentChanged(DocumentEvent e) {
        if (log.isDebugEnabled())
            log.debug("documentChanged(...) ignoreChange " +
                      ignoreChange);
        if (ignoreChange)
            return;
        documentChanged = true;
        lastChangeTime = System.currentTimeMillis();
    }

    public void changedUpdate(DocumentEvent e) {
        documentChanged(e);
    }

    public void insertUpdate(DocumentEvent e) {
        documentChanged(e);
    }

    public void removeUpdate(DocumentEvent e) {
        documentChanged(e);
    }

    private class ChangeNotifier
            implements Runnable {

        public void run() {
            final int EXCEPTION_LIMIT = 10;
            int exceptionCount = 0;
            while (true) {
                synchronized (Editor.this) {
                    if (documentChanged) {
                        if ((System.currentTimeMillis() - lastChangeTime) > 500) {
                            String s = null;
                            try {
                                Document doc = editPane.getDocument();
                                s = doc.getText(0, doc.getLength());
                                model.setText(Editor.this, s);
                                documentChanged = false;
                            } catch (BadLocationException ble) {
                                exceptionHandler.exception(ble);
                            } catch (Exception e) {
                                ++exceptionCount;
                                log.error("Exception processing string " + s,
                                          e);
                                // Too many exceptions. Giving up.
                                if (exceptionCount > EXCEPTION_LIMIT) {
                                    exceptionHandler.exception(e);
                                    return;
                                }
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(250);
                } catch (InterruptedException ie) {
                }
            }
        }
    }
}
