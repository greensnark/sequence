// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ChooseParserDialogBase.java

package com.zanthan.sequence.swing.editor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public abstract class ChooseParserDialogBase extends JDialog
{
    private class InnerResourceBundle extends ResourceBundle
    {
        private class IteratorWrapper
            implements Enumeration
        {

            public boolean hasMoreElements()
            {
                return iterator.hasNext();
            }

            public Object nextElement()
            {
                return iterator.next();
            }

            private Iterator iterator;

            public IteratorWrapper(Iterator iterator)
            {
                this.iterator = iterator;
            }
        }


        public Enumeration getKeys()
        {
            Set keys = new HashSet();

            addEnumKeys(keys, parent.getKeys());
            if (child != null)
              addEnumKeys(keys, child.getKeys());
            return new IteratorWrapper(keys.iterator());
        }

      private void addEnumKeys(Set keys, Enumeration keyEnum) {
        for (; keyEnum.hasMoreElements(); ) {
          keys.add(keyEnum.nextElement());
        }
      }

        protected Object handleGetObject(String key)
        {
            return child.getObject(key);
        }

        private ResourceBundle child;

        public InnerResourceBundle(ResourceBundle parent, ResourceBundle child)
        {
            this.child = child;
            setParent(parent);
        }
    }

    public ChooseParserDialogBase()
        throws HeadlessException
    {
    }

    public ChooseParserDialogBase(Dialog owner)
        throws HeadlessException
    {
        super(owner);
    }

    public ChooseParserDialogBase(Dialog owner, String title, boolean modal, GraphicsConfiguration gc)
        throws HeadlessException
    {
        super(owner, title, modal, gc);
    }

    public ChooseParserDialogBase(Frame owner)
        throws HeadlessException
    {
        super(owner);
    }

    public ChooseParserDialogBase(Frame owner, String title, boolean modal, GraphicsConfiguration gc)
    {
        super(owner, title, modal, gc);
    }

    protected void init(Locale locale)
    {
        resources = new InnerResourceBundle(ResourceBundle.getBundle("com.zanthan.sequence.swing.editor.ChooseParserDialogBase", locale), getResources(locale));
        setTitle(resources.getString("this.title"));
        setModal(true);
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent event)
            {
                closeDialog(event);
            }

        }
);
        addChildren(getContentPane());
    }

    protected abstract ResourceBundle getResources(Locale locale);

    protected abstract void closeDialog(WindowEvent windowevent);

    protected void addChildren(Container container)
    {
        GridBagLayout layout = new GridBagLayout();
        container.setLayout(layout);
        GridBagConstraints constraints = new GridBagConstraints(0, -1, 1, 1, 1.0D, 0.0D, 10, 1, new Insets(0, 0, 0, 0), 0, 0);
        layout.setConstraints(getScrollParserList(), constraints);
        container.add(getScrollParserList());
        constraints = new GridBagConstraints(0, -1, 1, 1, 1.0D, 1.0D, 10, 1, new Insets(0, 0, 0, 0), 0, 0);
        layout.setConstraints(getScrollParserDescription(), constraints);
        container.add(getScrollParserDescription());
        constraints = new GridBagConstraints(0, -1, 1, 1, 1.0D, 0.0D, 10, 2, new Insets(0, 0, 0, 0), 0, 0);
        layout.setConstraints(getButtonPanel(), constraints);
        container.add(getButtonPanel());
    }

    protected JScrollPane getScrollParserList()
    {
        if(scrollParserList == null)
        {
            scrollParserList = newScrollParserList();
            setScrollParserListProperties(scrollParserList);
            addScrollParserListChildren(scrollParserList);
            addScrollParserListEventHandlers(scrollParserList);
        }
        return scrollParserList;
    }

    protected JScrollPane newScrollParserList()
    {
        JScrollPane container = new JScrollPane();
        return container;
    }

    protected void setScrollParserListProperties(JScrollPane scrollParserList)
    {
        scrollParserList.setVerticalScrollBarPolicy(22);
    }

    protected void addScrollParserListEventHandlers(JScrollPane jscrollpane)
    {
    }

    protected void addScrollParserListChildren(JScrollPane container)
    {
        container.setViewportView(getParserList());
    }

    protected JScrollPane getScrollParserDescription()
    {
        if(scrollParserDescription == null)
        {
            scrollParserDescription = newScrollParserDescription();
            setScrollParserDescriptionProperties(scrollParserDescription);
            addScrollParserDescriptionChildren(scrollParserDescription);
            addScrollParserDescriptionEventHandlers(scrollParserDescription);
        }
        return scrollParserDescription;
    }

    protected JScrollPane newScrollParserDescription()
    {
        JScrollPane container = new JScrollPane();
        return container;
    }

    protected void setScrollParserDescriptionProperties(JScrollPane scrollParserDescription)
    {
        scrollParserDescription.setVerticalScrollBarPolicy(22);
    }

    protected void addScrollParserDescriptionEventHandlers(JScrollPane jscrollpane)
    {
    }

    protected void addScrollParserDescriptionChildren(JScrollPane container)
    {
        container.setViewportView(getParserDescription());
    }

    protected JPanel getButtonPanel()
    {
        if(buttonPanel == null)
        {
            buttonPanel = newButtonPanel();
            setButtonPanelProperties(buttonPanel);
            addButtonPanelChildren(buttonPanel);
            addButtonPanelEventHandlers(buttonPanel);
        }
        return buttonPanel;
    }

    protected JPanel newButtonPanel()
    {
        JPanel container = new JPanel();
        return container;
    }

    protected void setButtonPanelProperties(JPanel jpanel)
    {
    }

    protected void addButtonPanelEventHandlers(JPanel jpanel)
    {
    }

    protected void addButtonPanelChildren(JPanel container)
    {
        FlowLayout layout = new FlowLayout();
        layout.setAlignment(2);
        container.setLayout(layout);
        container.add(getButtonGroup());
    }

    protected JPanel getButtonGroup()
    {
        if(buttonGroup == null)
        {
            buttonGroup = newButtonGroup();
            setButtonGroupProperties(buttonGroup);
            addButtonGroupChildren(buttonGroup);
            addButtonGroupEventHandlers(buttonGroup);
        }
        return buttonGroup;
    }

    protected JPanel newButtonGroup()
    {
        JPanel container = new JPanel();
        return container;
    }

    protected void setButtonGroupProperties(JPanel jpanel)
    {
    }

    protected void addButtonGroupEventHandlers(JPanel jpanel)
    {
    }

    protected void addButtonGroupChildren(JPanel container)
    {
        GridLayout layout = new GridLayout();
        layout.setColumns(0);
        layout.setRows(1);
        container.setLayout(layout);
        container.add(getOkButton());
        container.add(getCancelButton());
    }

    protected JList getParserList()
    {
        if(parserList == null)
        {
            parserList = newParserList();
            setParserListProperties(parserList);
            addParserListEventHandlers(parserList);
        }
        return parserList;
    }

    protected JList newParserList()
    {
        JList component = new JList(new DefaultListModel());
        return component;
    }

    protected void setParserListProperties(JList parserList)
    {
        parserList.setSelectionMode(0);
    }

    protected void addParserListEventHandlers(JList jlist)
    {
    }

    protected JTextArea getParserDescription()
    {
        if(parserDescription == null)
        {
            parserDescription = newParserDescription();
            setParserDescriptionProperties(parserDescription);
            addParserDescriptionEventHandlers(parserDescription);
        }
        return parserDescription;
    }

    protected JTextArea newParserDescription()
    {
        JTextArea component = new JTextArea();
        return component;
    }

    protected void setParserDescriptionProperties(JTextArea parserDescription)
    {
        parserDescription.setLineWrap(true);
        parserDescription.setRows(5);
        parserDescription.setWrapStyleWord(true);
    }

    protected void addParserDescriptionEventHandlers(JTextArea jtextarea)
    {
    }

    protected JButton getOkButton()
    {
        if(okButton == null)
        {
            okButton = newOkButton();
            setOkButtonProperties(okButton);
            addOkButtonEventHandlers(okButton);
        }
        return okButton;
    }

    protected JButton newOkButton()
    {
        JButton component = new JButton();
        return component;
    }

    protected void setOkButtonProperties(JButton okButton)
    {
        okButton.setText(resources.getString("okButton.text"));
    }

    protected void addOkButtonEventHandlers(JButton okButton)
    {
        okButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event)
            {
                okButtonActionPerformed(event);
            }

        }
);
    }

    protected abstract void okButtonActionPerformed(ActionEvent actionevent);

    protected JButton getCancelButton()
    {
        if(cancelButton == null)
        {
            cancelButton = newCancelButton();
            setCancelButtonProperties(cancelButton);
            addCancelButtonEventHandlers(cancelButton);
        }
        return cancelButton;
    }

    protected JButton newCancelButton()
    {
        JButton component = new JButton();
        return component;
    }

    protected void setCancelButtonProperties(JButton cancelButton)
    {
        cancelButton.setText(resources.getString("cancelButton.text"));
    }

    protected void addCancelButtonEventHandlers(JButton cancelButton)
    {
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event)
            {
                cancelButtonActionPerformed(event);
            }

        }
);
    }

    protected abstract void cancelButtonActionPerformed(ActionEvent actionevent);

    protected ResourceBundle resources;
    private JScrollPane scrollParserList;
    private JList parserList;
    private JScrollPane scrollParserDescription;
    private JTextArea parserDescription;
    private JPanel buttonPanel;
    private JPanel buttonGroup;
    private JButton okButton;
    private JButton cancelButton;
}
