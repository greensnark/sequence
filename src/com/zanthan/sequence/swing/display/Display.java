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
package com.zanthan.sequence.swing.display;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.zanthan.sequence.diagram.Item;
import com.zanthan.sequence.diagram.ItemIdentifier;
import com.zanthan.sequence.layout.LayoutData;
import com.zanthan.sequence.preferences.Prefs;
import com.zanthan.sequence.swing.ExceptionHandler;
import com.zanthan.sequence.swing.SequenceAction;
import com.zanthan.sequence.swing.model.Model;
import com.zanthan.sequence.swing.model.ModelListener;
import com.zanthan.sequence.swing.model.ModelParseFailedEvent;
import com.zanthan.sequence.swing.model.ModelParseSucceededEvent;
import com.zanthan.sequence.swing.model.ModelPreferencesChangedEvent;

/**
 * Display a model. It implements ModelListener to listen to parse and preferences events from the model. When it
 * receives these it updates the display.
 */
public class Display
        extends JPanel
        implements ModelListener, MouseListener {

    private static final Logger log = Logger.getLogger(Display.class);
    private Map hintsMap = new HashMap();
    private LayoutData layoutData = null;
    private Model model = null;
    private boolean layoutDone = false;
    private SequenceAction exportAction = null;
    private SwingPainter painter = new SwingPainter();

    public Display(ExceptionHandler exceptionHandler, Model model) {
        this.model = model;
        hintsMap.put(RenderingHints.KEY_ANTIALIASING,
                     RenderingHints.VALUE_ANTIALIAS_ON);

        setPreferredSize(new Dimension(200, 200));

        model.addModelListener(this);
        exportAction = new ExportAction(exceptionHandler, this, model);

        addMouseListener(this);
        setToolTipText("");
    }

    public synchronized void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;

        if (!layoutDone && !model.getRootObjects().isEmpty()) {
            // Layout and if the size has changed call revalidate
            // and then repaint instead of continuing with this paint. We'll get
            // called again in a short while as a result of the paint.
            if (doLayout(g2)) {
                revalidate();
                repaint();
                return;
            }
        }

        setBackground(Prefs.getColorValue(Prefs.BACKGROUND_COLOR));

        super.paintComponent(g);

        g2.addRenderingHints(hintsMap);

        Insets insets = getInsets();
//        int currentWidth = getWidth() - insets.left - insets.right;
//        int currentHeight = getCanvasHeight() - insets.top - insets.bottom;

        g2.translate(insets.left, insets.top);

        if (model.getRootObjects().isEmpty())
            return;

        painter.setGraphics(g2);

        layoutData.paint(painter);
    }

    /**
     * Layout the display. Return true if the preferred size of the display has
     * changed.
     * @param g2
     * @return true or false
     */
    private synchronized boolean doLayout(Graphics2D g2) {

        ItemIdentifier selectedItemIdentifier = getSelectedItem();

        layoutData = new LayoutData(new SwingStringMeasure(g2));
        painter = new SwingPainter();

        model.layout(layoutData);

        setSelectedItem(selectedItemIdentifier);

        layoutDone = true;

        Dimension oldSize = getPreferredSize();
        Dimension newSize = new Dimension(layoutData.getWidth(), layoutData.getHeight());
        if (!oldSize.equals(newSize)) {
            setPreferredSize(newSize);
            return true;
        } else {
            return false;
        }
    }

    private ItemIdentifier getSelectedItem() {
        ItemIdentifier selectedItemIdentifier = null;
        Item selectedItem = model.getSelectedItem();
        if (selectedItem != null) {
            selectedItemIdentifier = selectedItem.getIdentifier();
        }
        return selectedItemIdentifier;
    }

    private void setSelectedItem(ItemIdentifier selectedItemIdentifier) {
        if (selectedItemIdentifier != null) {
            Item selectedItem =
                    model.findItem(selectedItemIdentifier);
            model.setSelectedItem(selectedItem);
        }
    }

    /**
     * Called when the text has been successfully parsed and the model updated.
     *
     * @param mpse contains the string parsed and the diagram
     */
    public void modelParseSucceeded(ModelParseSucceededEvent mpse) {
        refresh();
    }

    /**
     * Called when the parser detects an error in parsing the text.
     *
     * @param mpfe contains the string being parsed, the diagram, and the parser exception
     */
    public void modelParseFailed(ModelParseFailedEvent mpfe) {
        refresh();
    }

    /**
     * Called when the preferences have changed.
     *
     * @param mpce contains the string parsed and the diagram
     */
    public void modelPreferencedChanged(ModelPreferencesChangedEvent mpce) {
        refresh();
    }

    public void refresh() {
        if (log.isDebugEnabled())
            log.debug("refresh(" +
                      ")");
        layoutDone = false;
        repaint();
    }

    public SequenceAction getExportAction() {
        return exportAction;
    }

    /**
     * Invoked when the mouse button has been clicked (pressed
     * and released) on a component.
     */
    public void mouseClicked(MouseEvent e) {
        Point where = e.getPoint();
        Item selected = model.getSelectedItem();
        if (layoutData != null) {
            Item mightSelect = layoutData.findItemAtPoint(where);
            if (selected != mightSelect) {
                model.setSelectedItem(mightSelect);
                repaint();
            }
        }
    }

    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * Returns the string to be used as the tooltip for <i>event</i>.
     * By default this returns any string set using
     * <code>setToolTipText</code>.  If a component provides
     * more extensive API to support differing tooltips at different locations,
     * this method should be overridden.
     */
    public String getToolTipText(MouseEvent event) {
        if (layoutData == null) {
            return "";
        }
        Item itemAtPoint = layoutData.findItemAtPoint(event.getPoint());
        if (itemAtPoint != null) {
            return itemAtPoint.getName();
        } else {
            return null;
        }
    }
}
