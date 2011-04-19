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
import java.awt.Dialog;
import java.awt.HeadlessException;
import java.awt.Frame;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.BorderFactory;

import org.apache.log4j.Logger;

import com.zanthan.sequence.diagram.Diagram;
import com.zanthan.sequence.swing.display.Display;
import com.zanthan.sequence.swing.editor.Editor;
import com.zanthan.sequence.swing.model.Model;
import com.zanthan.sequence.swing.model.ModelAction;
import com.zanthan.sequence.swing.model.ModelListener;
import com.zanthan.sequence.swing.model.ModelParseFailedEvent;
import com.zanthan.sequence.swing.model.ModelParseSucceededEvent;
import com.zanthan.sequence.swing.model.ModelPreferencesChangedEvent;
import com.zanthan.sequence.parser.ParserException;
import com.zanthan.sequence.parser.alternate.ParseException;

/**
 * A panel with a display of a model at the top and an editor for the text description of the model at the bottom.
 */
public class SequencePanel
        extends JPanel
        implements ModelListener {

    private static final Logger log = Logger.getLogger(SequencePanel.class);
    private ExceptionHandler exceptionHandler;
    private Model model;
    private Display display;
    private Editor editor;
    private JSplitPane split;

    private boolean errorMessagePanelShowing = false;
    private ErrorMessagePanel messagePanel = new ErrorMessagePanel();

    public SequencePanel(boolean offerParserChoice, ExceptionHandler exceptionHandler, Diagram diagram) {
        if (log.isDebugEnabled())
            log.debug("SequencePanel(" +
                      exceptionHandler + ", " +
                      diagram +
                      ")");
        this.exceptionHandler = exceptionHandler;

        split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        model = new Model(exceptionHandler, diagram);

        display = new Display(exceptionHandler, model);
        split.setTopComponent(new JScrollPane(display,
                                              JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                              JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));

        editor = new Editor(offerParserChoice, exceptionHandler, model);
        split.setBottomComponent(editor);

        setLayout(new BorderLayout());

        add(split, BorderLayout.CENTER);

        model.addModelListener(editor);

        model.addModelListener(this);

        display.refresh();

    }

    public void preferencesChanged() {
        model.preferencesChanged();
    }

    public Model getModel() {
        return model;
    }

    public Display getDisplay() {
        return display;
    }

    public ModelAction getNewAction() {
        return model.getNewAction();
    }

    public ModelAction getOpenAction() {
        return model.getOpenAction();
    }

    public ModelAction getSaveAction() {
        return model.getSaveAction();
    }

    public ModelAction getSaveAsAction() {
        return model.getSaveAsAction();
    }

    public SequenceAction getExportAction() {
        return display.getExportAction();
    }

    public Action getCutAction() {
        return editor.getCutAction();
    }

    public Action getPasteAction() {
        return editor.getPasteAction();
    }

    public Action getCopyAction() {
        return editor.getCopyAction();
    }

    public void setDividerLocation(double proportionalLocation) {
        split.setDividerLocation(proportionalLocation);
    }

    /**
     * Called when the parser detects an error in parsing the text.
     *
     * @param mpfe contains the string being parsed, the diagram, and the parser exception
     */
    public void modelParseFailed(ModelParseFailedEvent mpfe) {
        if (!errorMessagePanelShowing) {
            errorMessagePanelShowing = true;
            add(messagePanel, BorderLayout.SOUTH);
            validate();
        }
        ParserException pe = mpfe.getParserException();
        Throwable t = pe.getCause();
        if (t instanceof ParseException){
            ParseException e = (ParseException) t;
            messagePanel.setError(e.currentToken.beginLine, e.currentToken.beginColumn, e);
        } else {
            messagePanel.setText(pe.getMessage(), t);
        }
    }

    /**
     * Called when the text has been successfully parsed and the model updated.
     *
     * @param mpse contains the string parsed and the diagram
     */
    public void modelParseSucceeded(ModelParseSucceededEvent mpse) {
        if (errorMessagePanelShowing) {
            errorMessagePanelShowing = false;
            messagePanel.setText("", null);
            remove(messagePanel);
            validate();
        }
    }

    /**
     * Called when the preferences have changed.
     *
     * @param mpce contains the string parsed and the diagram
     */
    public void modelPreferencedChanged(ModelPreferencesChangedEvent mpce) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private class ErrorMessagePanel extends JPanel {

        private ErrorMessageDialog emd;
        private JButton moreButton;
        private JLabel text;
        private MessageFormat messageFormat;
        private Throwable t;

        private ErrorMessagePanel() {
            setLayout(new BorderLayout(5, 5));

            moreButton = new JButton(SequenceResources.getString("SequencePanel.ErrorMessagePanel.MoreButton"));
            add(moreButton, BorderLayout.WEST);

            moreButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ErrorMessageDialog dialog = getErrorMessageDialog();
                    if (!dialog.isShowing()) {
                        dialog.show();
                        dialog.setError(t);
                    }
                }
            });

            text = new JLabel();
            add(text, BorderLayout.CENTER);
        }

        ErrorMessageDialog getErrorMessageDialog() {
            if (emd == null) {
                Container cont = SequencePanel.this.getParent();
                while (cont != null) {
                    if (cont instanceof Frame) {
                        emd = new ErrorMessageDialog((Frame) cont);
                        break;
                    }
                    if (cont instanceof Dialog) {
                        emd = new ErrorMessageDialog((Dialog) cont);
                        break;
                    }
                    cont = cont.getParent();
                }
                if (cont == null) {
                    throw new RuntimeException("Can not find parent for SequencePanel instance");
                }
            }
            return emd;
        }

        /**
         * Display some general error message
         *
         * @param message the message
         * @param t the actual exception
         */
        void setText(String message, Throwable t) {
            text.setText(message);
            this.t = t;
            if (getErrorMessageDialog().isShowing()) {
                getErrorMessageDialog().setError(t);
            }
        }

        /**
         * Indicate that there is an error on a particular line and column
         *
         * @param line the line the error is on
         * @param col the column where the error is
         * @param t the actual exception
         */
        void setError(int line, int col, Throwable t) {
            if (messageFormat == null) {
                messageFormat = new MessageFormat(SequenceResources.getString("SequencePanel.ErrorMessagePanel.Message"));
            }
            setText(messageFormat.format(new String[] {Integer.toString(line), Integer.toString(col)}), t);
        }
    }

    private class ErrorMessageDialog extends JDialog {

        private JTextArea textArea;

        public ErrorMessageDialog(Dialog owner)
                throws HeadlessException {
            super(owner, SequenceResources.getString("SequencePanel.ErrorMessageDialog.Title"), false);
            init();
        }

        public ErrorMessageDialog(Frame owner)
                throws HeadlessException {
            super(owner, SequenceResources.getString("SequencePanel.ErrorMessageDialog.Title"), false);
            init();
        }

        private void init() {
            getContentPane().setLayout(new BorderLayout(5, 5));

            textArea = new JTextArea(10, 20);
            JScrollPane scroller = new JScrollPane(textArea);
            scroller.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            getContentPane().add(scroller, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
            JButton closeButton =
                    new JButton(SequenceResources.getString("SequencePanel.ErrorMessageDialog.CloseButton"));
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ErrorMessageDialog.this.hide();
                }
            });
            buttonPanel.add(closeButton);

            getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        }

        void setError(Throwable t) {
            if (t != null) {
                textArea.setText(t.getMessage());
            } else {
                textArea.setText("");
            }
        }

        /**
         * Makes the Dialog visible. If the dialog and/or its owner
         * are not yet displayable, both are made displayable.  The
         * dialog will be validated prior to being made visible.
         * If the dialog is already visible, this will bring the dialog
         * to the front.
         * <p/>
         * If the dialog is modal and is not already visible, this call will
         * not return until the dialog is hidden by calling <code>hide</code> or
         * <code>dispose</code>. It is permissible to show modal dialogs from
         * the event dispatching thread because the toolkit will ensure that
         * another event pump runs while the one which invoked this method
         * is blocked.
         *
         * @see java.awt.Component#hide
         * @see java.awt.Component#isDisplayable
         * @see java.awt.Component#validate
         * @see java.awt.Dialog#isModal
         */
        public void show() {
            if (getSize().getWidth() < 200d || getSize().getHeight() < 200d) {
                setSize(300, 300);
            }
            super.show();
        }
    }
}
