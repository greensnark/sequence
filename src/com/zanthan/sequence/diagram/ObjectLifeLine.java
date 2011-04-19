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
package com.zanthan.sequence.diagram;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.zanthan.sequence.layout.LayoutData;
import com.zanthan.sequence.layout.Painter;
import com.zanthan.sequence.layout.StringMeasurement;

public class ObjectLifeLine
        extends Item {

    private static final Logger log =
            Logger.getLogger(ObjectLifeLine.class);

    /**
     * Name of this object.
     */
    private String name = null;
    /**
     * List of methods executions on this object in the order
     * in which they occur.
     */
    private List methodExecutions = new ArrayList();

    // x and y coordinates of the top left of the box containing the name
    private int x = -1;
    private int y = -1;
    // width and height of the box containing the name
    private int headerWidth = -1;
    private int headerHeight = -1;
    // x and y offset to start of text within box containing name
    private Dimension textOffset = null;

    private int seq = -1;
    private int currentMethodExecutionIndent = 0;
    private int selfCallOffset = 0;

    public ObjectLifeLine(String name, Object userData) {
        this.name = name;
        this.userData = userData;
    }

    public String getName() {
        return name;
    }

    public void addMethodExecution(MethodExecution methodExecution) {
        methodExecutions.add(methodExecution);
    }

    public Iterator getMethodExecutions() {
        return methodExecutions.iterator();
    }

    public String toString() {
        return "<ObjectLifeLine-" + hashCode() + " " + getName() + ">";
    }

    public int getMethodExecutionCount() {
        return methodExecutions.size();
    }

    protected StringMeasurement getHeaderMeasurement(LayoutData layoutData) {
        return layoutData.measureString(getName());
    }

    /**
     * Measure the height of the object header. Record the maximum value found.
     *
     * @param layoutData
     */
    void setInitialY(LayoutData layoutData) {
        StringMeasurement headerMeasurement = getHeaderMeasurement(layoutData);
        headerWidth = headerMeasurement.getWidth() + (2 * layoutData.getTextXPad());
        headerHeight = headerMeasurement.getHeight() + (2 * layoutData.getTextYPad());
        textOffset = new Dimension(layoutData.getTextXPad(), headerMeasurement.getYOffset() + layoutData.getTextYPad());

        y = layoutData.getInitialY();
        layoutData.setMaxY(y + headerHeight);
    }

    void setInitialX(LayoutData layoutData) {
        seq = layoutData.getNextHorizontalSeq();
        if (seq == 0) {
            x = layoutData.getNextObjectLifeLineX();
        } else {
            // minimum separation puts x right after the previous header
            ObjectLifeLine previous = layoutData.getObjectLifeLine(seq - 1);
            x = previous.getX() + previous.getHeaderWidth() + layoutData.getObjectLifeLineSpacing();
            // adjust if this does not allow enough space
            int offset = layoutData.getNextObjectLifeLineX() - getMinX();
            if (offset > 0) {
                x += offset;
            }
        }
        layoutData.setNextObjectLifeLineX(x + headerWidth + layoutData.getObjectLifeLineSpacing());
    }

    protected int getX() {
        return x;
    }

    protected int getCenterX() {
        return x + headerWidth / 2;
    }

    public int getMinX() {
        return ((MethodExecution) getMethodExecutions().next()).getMinX();
    }

    public int getMaxX() {
        if (selfCallOffset > 0)
            return x + selfCallOffset;
        else
            return getRightMostMethodExecutionX();
    }

    public int getMaxHeaderX() {
        return x + headerWidth;
    }

    int getRightMostMethodExecutionX() {
        int maxX = 0;
        for (Iterator it = getMethodExecutions(); it.hasNext();) {
            MethodExecution methodExecution = (MethodExecution) it.next();
            int x = methodExecution.getMaxX();
            if (x > maxX)
                maxX = x;
        }
        return maxX;
    }

    void setX(int x) {
        this.x = x;
    }

    /**
     * Set the x value for a call from this object that goes back to itself
     * @param x
     */
    void setSelfCallX(int x) {
        int newSelfCallOffset = this.x - x;
        if (newSelfCallOffset > selfCallOffset)
            selfCallOffset = newSelfCallOffset;
    }

    protected int getY() {
        return y;
    }

    protected int getHeaderWidth() {
        return headerWidth;
    }

    protected int getHeaderHeight() {
        return headerHeight;
    }

    protected Dimension getTextOffset() {
        return textOffset;
    }

    public int getSeq() {
        return seq;
    }

    public void layout(LayoutData layoutData) {

        if (layoutData.firstVisit(this))
            setInitialX(layoutData);

        for (Iterator it = getMethodExecutions(); it.hasNext();) {
            MethodExecution methodExecution = (MethodExecution) it.next();
            if (!layoutData.alreadyVisited(methodExecution)) {
                methodExecution.setStartY(layoutData.getMaxY() + 5);
                methodExecution.layout(layoutData);
                methodExecution.setEndY(layoutData.getMaxY());
            }
        }
    }

    public void paint(Painter painter) {
        int x = getX();
        int y = getY();
        int headerWidth = getHeaderWidth();
        int headerHeight = getHeaderHeight();
        painter.paintObjectLifeLineHeader(new String[] {getName()},
                                          new Rectangle(x, y, headerWidth, headerHeight),
                                          getTextOffset(),
                                          isSelected());

        int centerX = getCenterX();
        painter.paintObjectLifeLineLine(centerX, y + headerHeight, centerX, painter.getCanvasMaxY());
    }

    public void translate(int translateAmount) {
        x += translateAmount;
    }

    int incrementMethodExecutionIndent() {
        return currentMethodExecutionIndent++;
    }

    int decrementMethodExecutionIndent() {
        return --currentMethodExecutionIndent;
    }

    public MethodExecution findMethodExecution(String methodExecutionName, int seq) {

        MethodExecution currentMethodExecution = null;
        int count = seq;
        for (int i = 0; i < methodExecutions.size(); ++i) {
            currentMethodExecution = (MethodExecution) methodExecutions.get(i);
            if (currentMethodExecution.getName().equals(methodExecutionName)) {
                if (count == 0) {
                    break;
                }
                --count;
            }
        }
        return currentMethodExecution;
    }

    ItemIdentifier getMethodExecutionIdentifier(MethodExecution methodExecution) {

        int seq = -1;
        for (int i = 0; i < methodExecutions.size(); i++) {
            MethodExecution current = (MethodExecution) methodExecutions.get(i);
            if (current.getName().equals(methodExecution.getName())) {
                ++seq;
                if (current == methodExecution) {
                    break;
                }
            }
        }
        if (seq == -1) {
            throw new RuntimeException("InternalError: getMethodExecutionIdentifier " +
                                       "called and methodExecution not found.");
        }
        return new ItemIdentifier(getName(), methodExecution.getName(), seq);
    }

    public boolean encloses(Point p) {
        if (p.x > x && p.x < x + headerWidth) {
            if (p.y > y && p.y < y + headerHeight) {
                return true;
            } else {
                int centerX = getCenterX();
                if (p.x > centerX - 2 && p.x < centerX + 2) {
                    return true;
                }
            }
        }
        return false;
    }

    public ItemIdentifier getIdentifier() {
        return new ItemIdentifier(getName());
    }

    public ObjectLifeLine getObjectLifeLine() {
        return this;
    }
}
