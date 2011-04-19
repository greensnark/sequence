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
package com.zanthan.sequence.layout;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.zanthan.sequence.diagram.Call;
import com.zanthan.sequence.diagram.Item;
import com.zanthan.sequence.diagram.MethodExecution;
import com.zanthan.sequence.diagram.ObjectLifeLine;
import com.zanthan.sequence.preferences.Prefs;

public class LayoutData {

    private List objectLifeLineList = new ArrayList();
    private Set visitedObjectLifeLines = new HashSet();
    private List methodExecutionsList = new ArrayList();
    private Set visitedMethodExecutions = new HashSet();
    private Set visitedCalls = new HashSet();
    // The maximum x value for the whole diagram
    private int maxX = 0;
    // The x value for the next object life line to be laid out
    private int nextObjectLifeLineX = 0;
    private int maxY = 0;
    private int initialY = 0;
    private int horizontalSeq = 0;
    private int methodExecutionHalfWidth = 0;
    private int textXPad = 0;
    private int textYPad = 0;
    private int objectLifeLineSpacing = 0;
    private int methodExecutionSpacing = 0;
    private StringMeasure stringMeasure = null;

    public LayoutData() {
        this(new SimpleStringMeasure());
    }

    public LayoutData(StringMeasure stringMeasure) {
        this.stringMeasure = stringMeasure;

        maxX = Prefs.getIntegerValue(Prefs.INITIAL_X_POSITION);
        maxY = initialY = Prefs.getIntegerValue(Prefs.INITIAL_Y_POSITION);
        methodExecutionHalfWidth = Prefs.getIntegerValue(Prefs.METHOD_EXECUTION_WIDTH) / 2;
        textXPad = Prefs.getIntegerValue(Prefs.TEXT_X_PAD);
        textYPad = Prefs.getIntegerValue(Prefs.TEXT_Y_PAD);
        objectLifeLineSpacing = Prefs.getIntegerValue(Prefs.OBJECT_LIFELINE_SPACING);
        methodExecutionSpacing = Prefs.getIntegerValue(Prefs.METHOD_EXECUTION_SPACING);
    }

    public List getObjectLifeLines() {
        return objectLifeLineList;
    }

    public ObjectLifeLine getObjectLifeLine(int seq) {
        return (ObjectLifeLine) objectLifeLineList.get(seq);
    }

    public List getMethodExecutions() {
        return methodExecutionsList;
    }

    public Set getCalls() {
        return visitedCalls;
    }

    /**
     * Return the method execution enclosing a point, or null if one can't be
     * found.
     * @param p the point
     * @return the enclosing method or null
     */
    public Item findItemAtPoint(Point p) {
        for (int i = methodExecutionsList.size() - 1; i >= 0; i--) {
            MethodExecution methodExecution = (MethodExecution) methodExecutionsList.get(i);
            if (methodExecution.encloses(p)) {
                return methodExecution;
            }
        }
        for (int i = 0; i < objectLifeLineList.size(); i++) {
            ObjectLifeLine objectLifeLine = (ObjectLifeLine) objectLifeLineList.get(i);
            if (objectLifeLine.encloses(p)) {
                return objectLifeLine;
            }
        }
        return null;
    }

    /**
     * Mark the method execution layout as visited and return true if
     * it hasn't been visited before.
     * @param objectLifeLine
     * @return true if this is the first visit
     */
    public boolean firstVisit(ObjectLifeLine objectLifeLine) {
        if (!visitedObjectLifeLines.contains(objectLifeLine)) {
            visitedObjectLifeLines.add(objectLifeLine);
            objectLifeLineList.add(objectLifeLine);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return true if the object life line layout has already been visited.
     * @param objectLifeLine
     * @return true or false
     */
    public boolean alreadyVisited(ObjectLifeLine objectLifeLine) {
        return visitedObjectLifeLines.contains(objectLifeLine);
    }

    /**
     * Mark the method execution layout as visited and return true if
     * it hasn't been visited before.
     * @param methodExecution
     * @return true if this is the first visit
     */
    public boolean firstVisit(MethodExecution methodExecution) {
        if (!visitedMethodExecutions.contains(methodExecution)) {
            visitedMethodExecutions.add(methodExecution);
            methodExecutionsList.add(methodExecution);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return true if the method execution layout has already been visited.
     * @param methodExecution
     * @return true or false
     */
    public boolean alreadyVisited(MethodExecution methodExecution) {
        return visitedMethodExecutions.contains(methodExecution);
    }

    /**
     * Mark the call layout as visited and return true if
     * it hasn't been visited before.
     * @param call
     * @return true if this is the first visit
     */
    public boolean firstVisit(Call call) {
        if (!visitedCalls.contains(call)) {
            visitedCalls.add(call);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return the current maximum x value. This value is the rightmost method execution or self call, not
     * the rightmost object header. Use getMaxHeaderX to get that value.
     * @return current max x value
     */
    public int getCurrentMaxX() {
        return maxX;
    }

    /**
     * Set the maximum x value to the value passed in if it is larger
     * than the current maximum value.
     * @param x
     */
    public void setMaxX(int x) {
        if (x > maxX)
            maxX = x;
    }

    /**
     * The next x value to use for an object lifeline
     * @return a x value
     */
    public int getNextObjectLifeLineX() {
        return nextObjectLifeLineX;
    }

    /**
     * Set the x position for the next object lifeline
     * @param nextObjectLifeLineX
     */
    public void setNextObjectLifeLineX(int nextObjectLifeLineX) {
        if (nextObjectLifeLineX > this.nextObjectLifeLineX) {
            this.nextObjectLifeLineX = nextObjectLifeLineX;
        }
        setMaxX(nextObjectLifeLineX);
    }

    private int getMaxHeaderX() {
        return ((ObjectLifeLine) objectLifeLineList.get(objectLifeLineList.size() - 1)).getMaxHeaderX();
    }

    /**
     * Return the next horizontal sequence number to use
     * @return
     */
    public int getNextHorizontalSeq() {
        return horizontalSeq++;
    }

    /**
     * Measures the string and returns the space needed to display it.
     * @param s
     * @return the length needed to display the string
     */
    public StringMeasurement measureString(String s) {
        return stringMeasure.measureString(s);
    }

    /**
     * Return the current max value for y
     * @return the maximum y value so far
     */
    public int getMaxY() {
        return maxY;
    }

    public int getInitialY() {
        return initialY;
    }

    public void setMaxY(int y) {
        if (y > maxY)
            maxY = y;
    }

//    public void adjustObjectsToTheRight(ObjectLifeLine objectLifeLineLayout, int newPos) {
//
//        int objectsSeq = objectLifeLineLayout.getSeq();
//        // Is this the rightmost object?
//        if (objectsSeq == horizontalSeq - 1)
//            return;
//
//        // Find object to the right
//        ObjectLifeLine nextObject =
//                (ObjectLifeLine) objectLifeLineList.get(objectsSeq + 1);
//        // If enough space then leave
//        int translateAmount = newPos - nextObject.getMinX();
//        if (translateAmount < 0)
//            return;
//
//        nextObject.translate(translateAmount);
//        for (int i = objectsSeq + 2; i < objectLifeLineList.size(); ++i) {
//            ((ObjectLifeLine) objectLifeLineList.get(i)).translate(translateAmount);
//        }
//
//        setMaxX(((ObjectLifeLine) objectLifeLineList.get(objectLifeLineList.size() - 1)).getMaxX());
//    }

    public void translateObjectsToTheRight(ObjectLifeLine objectLifeLine, int translateAmount) {
        for (int i = objectLifeLine.getSeq() + 1; i < objectLifeLineList.size(); ++i) {
            ((ObjectLifeLine) objectLifeLineList.get(i)).translate(translateAmount);
        }

        setNextObjectLifeLineX(((ObjectLifeLine) objectLifeLineList.get(objectLifeLineList.size() - 1)).getMaxX());
    }

    public int getMethodExecutionHalfWidth() {
        return methodExecutionHalfWidth;
    }

    public int getTextXPad() {
        return textXPad;
    }

    public int getTextYPad() {
        return textYPad;
    }

    public int getObjectLifeLineSpacing() {
        return objectLifeLineSpacing;
    }

    public int getMethodExecutionSpacing() {
        return methodExecutionSpacing;
    }

    public void paint(Painter painter) {

        List objects = getObjectLifeLines();
        for (int i = 0; i < objects.size(); i++) {
            ObjectLifeLine objectLifeLine = (ObjectLifeLine) objects.get(i);
            objectLifeLine.paint(painter);
        }

        Iterator methodIterator = getMethodExecutions().iterator();
        while (methodIterator.hasNext()) {
            MethodExecution methodExecution = (MethodExecution) methodIterator.next();
            methodExecution.paint(painter);
        }

        Iterator callIterator = getCalls().iterator();
        while (callIterator.hasNext()) {
            Call call = (Call) callIterator.next();
            call.paint(painter);
        }
    }

    public int getWidth() {
        int currentMaxX = getCurrentMaxX();
        int maxHeaderX = getMaxHeaderX();
        int width = 0;
        if (currentMaxX > maxHeaderX) {
            width = currentMaxX;
        } else {
            width = maxHeaderX;
        }
        width += Prefs.getIntegerValue(Prefs.INITIAL_X_POSITION) + 1;
        return width;
    }

    public int getHeight() {
        return getMaxY() + Prefs.getIntegerValue(Prefs.INITIAL_Y_POSITION) + 1;
    }

    private static class SimpleStringMeasure
            implements StringMeasure {

        public StringMeasurement measureString(String s) {

            return new StringMeasurement(s.length() * 2, 5, 3);
        }

    }

}
