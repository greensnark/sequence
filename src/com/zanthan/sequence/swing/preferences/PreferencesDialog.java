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
package com.zanthan.sequence.swing.preferences;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

import com.zanthan.sequence.preferences.Prefs;
import com.zanthan.sequence.swing.SequenceAction;
import com.zanthan.sequence.swing.SequenceResources;

public class PreferencesDialog
        extends JDialog {

    private String resourcePrefix = null;

    private Prefs prefs = null;

    private boolean okChosen = false;

    private JPanel mainPanel = null;
    private JPanel buttonPanel = null;

    private AbstractAction okAction = null;
    private AbstractAction cancelAction = null;

    public PreferencesDialog(JFrame owner, Prefs prefs) {
        super(owner, true);
        this.prefs = prefs;
        resourcePrefix = "PreferencesDialog";
        init();
    }

    public boolean wasOKChosen() {
        return okChosen;
    }

    private void init() {
        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(getMainPanel(), BorderLayout.CENTER);

        getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);

        setTitle(getResource("title"));
    }

    private JPanel getMainPanel() {
        if (mainPanel != null)
            return mainPanel;

        mainPanel = new JPanel();

        GridBagLayout gbl = new GridBagLayout();
        mainPanel.setLayout(gbl);

        int row = 0;
        for (Iterator it = prefs.getPrefs(); it.hasNext();) {
            addPref(mainPanel, gbl, (Prefs.Pref) it.next(), row++);
        }

        return mainPanel;
    }

    private void addPref(JPanel panel, GridBagLayout gbl, Prefs.Pref pref, int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        JLabel label = new JLabel(pref.getLabel());
        label.setToolTipText(pref.getShortDesc());

        gbc.anchor = gbc.NORTHWEST;
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.insets = new Insets(5, 5, 0, 6);

        gbl.setConstraints(label, gbc);
        panel.add(label);

        String type = pref.getType();

        gbc.gridx = 1;
        gbc.fill = gbc.HORIZONTAL;
        gbc.weightx = 1;
        JComponent typeSpecificComponent = null;
        if (type.equals("boolean")) {
            typeSpecificComponent = getBooleanComponent(pref);
        } else if (type.equals("color")) {
            typeSpecificComponent = getColorComponent(pref);
        } else if (type.equals("string")) {
            typeSpecificComponent = getStringComponent(pref);
        } else if (type.equals("integer")) {
            typeSpecificComponent = getIntegerComponent(pref);
        }
        gbl.setConstraints(typeSpecificComponent, gbc);
        panel.add(typeSpecificComponent);
    }

    private JComponent getIntegerComponent(final Prefs.Pref pref) {
        JFormattedTextField field = new JFormattedTextField();
        field.setFormatterFactory(new JFormattedTextField.AbstractFormatterFactory() {
            public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
                return new NumberFormatter(new DecimalFormat("##0"));
            }
        });
        field.setInputVerifier(new PositiveNumberFormattedTextFieldVerifier());
        field.setText(pref.getStringValue());
        field.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                pref.setStringValue(((JTextField) e.getSource()).getText().trim());
            }
        });
        return field;
    }

    private static JComponent getBooleanComponent(final Prefs.Pref pref) {
        JCheckBox box = new JCheckBox();
        box.setSelected(pref.getBooleanValue());
        box.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pref.setBooleanValue(((JCheckBox) e.getSource()).isSelected());
            }
        });
        return box;
    }

    private JComponent getColorComponent(final Prefs.Pref pref) {
        JButton button = new JButton();
        button.setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Properties16.gif"),
                                     pref.getLabel()));
        button.setBackground(pref.getColorValue());
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color newColor =
                        JColorChooser.showDialog(PreferencesDialog.this,
                                                 pref.getLabel(),
                                                 pref.getColorValue());
                if (newColor != null) {
                    pref.setColorValue(newColor);
                    ((JButton) e.getSource()).setBackground(newColor);
                }
            }
        });
        return button;
    }

    private JComponent getStringComponent(final Prefs.Pref pref) {
        JTextField field = new JTextField();
        field.setText(pref.getStringValue());
        field.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                pref.setStringValue(((JTextField) e.getSource()).getText().trim());
            }
        });
        return field;
    }

    private JPanel getButtonPanel() {
        if (buttonPanel != null)
            return buttonPanel;

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JPanel buttonGrid = new JPanel();
        buttonGrid.setLayout(new GridLayout());
        buttonGrid.add(new JButton(getOKAction()));
        buttonGrid.add(new JButton(getCancelAction()));

        buttonPanel.add(buttonGrid);

        return buttonPanel;
    }

    private AbstractAction getOKAction() {
        if (okAction != null)
            return okAction;

        okAction = new OKAction();
        return okAction;
    }

    private AbstractAction getCancelAction() {
        if (cancelAction != null)
            return cancelAction;

        cancelAction = new CancelAction();
        return cancelAction;
    }

    private String getResource(String key) {
        return SequenceResources.getString(resourcePrefix + "." + key);
    }

    private class OKAction
            extends SequenceAction {

        private OKAction() {
            super("PreferencesDialog.OKAction", false);
        }

        public void performAction() {
            okChosen = true;
            hide();
        }
    }

    private class CancelAction
            extends SequenceAction {

        private CancelAction() {
            super("PreferencesDialog.CancelAction", false);
        }

        public void performAction() {
            okChosen = false;
            hide();
        }
    }
}
