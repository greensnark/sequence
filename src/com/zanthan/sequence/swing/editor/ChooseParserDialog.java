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

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import com.zanthan.sequence.parser.Parser;
import com.zanthan.sequence.parser.ParserFactory;
import com.zanthan.sequence.swing.Sequence;

class ChooseParserDialog
        extends ChooseParserDialogBase {

    private final static Logger log =
            Logger.getLogger(ChooseParserDialog.class);

    public final static int OK_BUTTON = 1;
    private final static int CANCEL_BUTTON = 2;

    private int buttonId = CANCEL_BUTTON;

    public ChooseParserDialog() {
        super(Sequence.getInstance());
        setLocationRelativeTo(Sequence.getInstance());
        init(Locale.getDefault());

        ParserFactory parserFactory = ParserFactory.getInstance();
        List parsers = parserFactory.getAvailableParsers();

        DefaultListModel listModel = (DefaultListModel) getParserList().getModel();
        for (int i = 0; i < parsers.size(); i++) {
            Parser parser = (Parser) parsers.get(i);
            listModel.addElement(parserFactory.getParserName(parser));
        }
    }

    protected void addParserListEventHandlers(JList parserList) {
        super.addParserListEventHandlers(parserList);
        parserList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String selectedParserName = (String) getParserList().getSelectedValue();
                    if (selectedParserName != null) {
                        getParserDescription().setText(ParserFactory.getInstance().getParserDescription(selectedParserName));
                    } else {
                        getParserDescription().setText("");
                    }
                }
            }
        });
    }

    /**
     *
     * @param locale
     * @return
     */
    protected ResourceBundle getResources(Locale locale) {
        return ResourceBundle.getBundle("com.zanthan.sequence.swing.editor.ChooseParserDialog", locale);
    }

    public void setSelectedParser(String parserName) {
        getParserList().setSelectedValue(parserName, true);
    }

    protected void cancelButtonActionPerformed(ActionEvent event) {
        buttonId = CANCEL_BUTTON;
        dispose();
    }

    protected void okButtonActionPerformed(ActionEvent event) {
        if (log.isDebugEnabled()) {
            log.debug("okButtonActionPerformed(" + event + ")");
        }
        buttonId = OK_BUTTON;
        dispose();
    }

    protected void closeDialog(WindowEvent event) {
        if (log.isDebugEnabled()) {
            log.debug("closeDialog(" + event + ")");
        }
        dispose();
    }

    public String getNameOfSelectedParser() {
        return (String) getParserList().getSelectedValue();
    }

    public int getPressedButton() {
        return buttonId;
    }
}
