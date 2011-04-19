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

import org.apache.log4j.Logger;

import com.zanthan.sequence.layout.LayoutData;
import com.zanthan.sequence.layout.Painter;
import com.zanthan.sequence.layout.StringMeasurement;

/**
 * Represents a call from one execution of a method, represented by a MethodExecution instance, to another.
 *
 * @author Alex Moffat
 */
public class Call
        extends Item {

    private static final Logger log =
            Logger.getLogger(Call.class);

    /**
     * The return type of the call.
     */
    private String returnType = null;
    /**
     * The method this call is callingMethodExecution
     */
    private MethodExecution callingMethodExecution = null;
    /**
     * The method this call is calledMethodExecution
     */
    private MethodExecution calledMethodExecution = null;

    private StringMeasurement callMeasurement;
    private StringMeasurement returnMeasurement;

    private int startCallY;
    private int endCallY;
    private Dimension callTextOffset = null;
    private int callHorizontalSpace;
    private int startReturnY;
    private int endReturnY;
    private Dimension returnTextOffset = null;
    private int returnHorizontalSpace;
    private boolean selfCall = false;

    /**
     * Create a new call.
     *
     * @param callingMethodExecution call is from this method execution
     * @param calledMethodExecution call is to this method execution
     * @param returnType the data returned by the call, may be null
     * @param userData any extra user data to store with this call
     */
    public Call(MethodExecution callingMethodExecution,
                   MethodExecution calledMethodExecution,
                   String returnType,
                   Object userData) {
        if (log.isDebugEnabled())
            log.debug("Call(" + callingMethodExecution + ", " + calledMethodExecution + ", " + returnType + ")");
        this.returnType = returnType;
        this.callingMethodExecution = callingMethodExecution;
        this.calledMethodExecution = calledMethodExecution;
        this.userData = userData;
    }

    private ObjectLifeLine getCalledObjectLifeLine() {
        return calledMethodExecution.getObjectLifeLine();
    }

    private ObjectLifeLine getCallingObjectLifeLine() {
        return callingMethodExecution.getObjectLifeLine();
    }

    protected MethodExecution getCallingMethodExecution() {
        return callingMethodExecution;
    }

    public MethodExecution getCalledMethodExecution() {
        return calledMethodExecution;
    }

    private String getSignature() {
        return calledMethodExecution.getName();
    }

    public String getReturn() {
        return returnType != null ? returnType : "void";
    }

    public void layout(LayoutData layoutData) {

        callMeasurement = layoutData.measureString(getSignature());
        returnMeasurement = layoutData.measureString(getReturn());

        int xPadding = 2 * layoutData.getTextXPad();
        callHorizontalSpace = callMeasurement.getWidth() + xPadding;
        returnHorizontalSpace = returnMeasurement.getWidth() + xPadding;
        int maxHorizontalSpace = Math.max(callHorizontalSpace, returnHorizontalSpace);

        callTextOffset =
                new Dimension(layoutData.getTextXPad(),
                              callMeasurement.getHeight() - (layoutData.getTextYPad() + callMeasurement.getYOffset()));
        if (getCallingObjectLifeLine() == getCalledObjectLifeLine()) {
            callingSelf(layoutData, maxHorizontalSpace);
        } else {
            callingOtherObject(layoutData, maxHorizontalSpace);
        }
        returnTextOffset =
                new Dimension(layoutData.getTextXPad(),
                              returnMeasurement.getHeight() - (layoutData.getTextYPad() + returnMeasurement.getYOffset()));
    }

    public void paint(Painter painter) {
        MethodExecution callingMethod = getCallingMethodExecution();
        MethodExecution calledMethod = getCalledMethodExecution();

        if (isSelfCall()) {
            painter.paintCall(getSignature(), getCallTextOffset(),
                              getCallHorizontalSpace(),
                              callingMethod.getMaxX(), getStartCallY(),
                              calledMethod.getMaxX(), getEndCallY());
            painter.paintReturn(getReturn(), getReturnTextOffset(),
                                getReturnHorizontalSpace(),
                                calledMethod.getMaxX(), getStartReturnY(),
                                callingMethod.getMaxX(), getEndReturnY());
        } else {
            int callingMaxX = callingMethod.getMaxX();
            int calledMaxX = calledMethod.getMaxX();
            if (callingMaxX < calledMaxX) {
                painter.paintCall(getSignature(), getCallTextOffset(),
                                  callingMaxX, getStartCallY(), calledMethod.getMinX(), getEndCallY());
                painter.paintReturn(getReturn(), getReturnTextOffset(),
                                    calledMethod.getMinX(), getStartReturnY(), callingMaxX, getEndReturnY());
            } else {
                painter.paintCall(getSignature(), getCallTextOffset(),
                                  callingMethod.getMinX(), getStartCallY(), calledMaxX, getEndCallY());
                painter.paintReturn(getReturn(), getReturnTextOffset(),
                                    calledMaxX, getStartReturnY(), callingMethod.getMinX(), getEndReturnY());
            }
        }
    }

    private void callingSelf(LayoutData layoutData, int maxHorizontalSpace) {
        selfCall = true;

        int yPadding = 2 * layoutData.getTextYPad();
        startCallY = layoutData.getMaxY() + callMeasurement.getHeight() + yPadding;
        endCallY = startCallY + 5;
        layoutData.setMaxY(endCallY);

        int nextX = getCallingMethodExecution().getMaxX() +
                maxHorizontalSpace;

        getCallingObjectLifeLine().setSelfCallX(nextX);
        layoutData.setMaxX(nextX);

        // If there are objects calledMethodExecution the right of the one this call is coming callingMethodExecution
        // they may need calledMethodExecution be moved.
        // todo ajm temp commented out. Useful
        //layoutData.adjustObjectsToTheRight(getCallingObjectLifeLine(), nextX);

        MethodExecution calledMethodExecution = getCalledMethodExecution();
        calledMethodExecution.setStartY(endCallY);
        calledMethodExecution.layout(layoutData);

        startReturnY = layoutData.getMaxY() + returnMeasurement.getHeight() + yPadding;
        calledMethodExecution.setEndY(startReturnY);
        endReturnY = startReturnY + 5;
        layoutData.setMaxY(endReturnY);
    }

    private void callingOtherObject(LayoutData layoutData, int requiredHorizontalSpace) {
        selfCall = false;

        int yPadding = 2 * layoutData.getTextYPad();
        startCallY = endCallY = layoutData.getMaxY() + callMeasurement.getHeight() + yPadding;
        layoutData.setMaxY(endCallY);

        // If the object being called has already been positioned it may need calledMethodExecution be moved.
        // If not then it will get positioned when the called method execution is laid out
        if (layoutData.alreadyVisited(getCalledObjectLifeLine())) {
            if (getCalledMethodExecution().toTheRightOf(getCallingMethodExecution())) {
                int availableGap = getCalledMethodExecution().getMinX() -
                        getCallingObjectLifeLine().getRightMostMethodExecutionX();
                if (availableGap < requiredHorizontalSpace) {
                    int translateAmount = requiredHorizontalSpace - availableGap;
                    getCalledObjectLifeLine().translate(translateAmount);
                    layoutData.translateObjectsToTheRight(getCalledObjectLifeLine(), translateAmount);
                }
            } else {
                int availableGap = Math.abs(getCalledObjectLifeLine().getRightMostMethodExecutionX() -
                                            getCallingMethodExecution().getMinX());
                if (log.isDebugEnabled()) {
                    log.debug("Calling " + getCalledObjectLifeLine().getName() + " method " + getName() +
                              " availableGap " + availableGap + " requiredHorizontalSpace " + requiredHorizontalSpace);
                }
                if (availableGap < requiredHorizontalSpace) {
                    int translateAmount = requiredHorizontalSpace - availableGap;
                    getCallingObjectLifeLine().translate(translateAmount);
                    layoutData.translateObjectsToTheRight(getCallingObjectLifeLine(), translateAmount);
                }
            }
        } else {
            int x = getCallingMethodExecution().getMaxX() +
                    requiredHorizontalSpace;
            layoutData.setNextObjectLifeLineX(x);
        }
        MethodExecution calledMethodExecution = getCalledMethodExecution();
        calledMethodExecution.setStartY(endCallY);
        calledMethodExecution.layout(layoutData);

        startReturnY = endReturnY = layoutData.getMaxY() + returnMeasurement.getHeight() + yPadding;
        calledMethodExecution.setEndY(startReturnY);
        layoutData.setMaxY(endReturnY);
    }

    private int getStartCallY() {
        return startCallY;
    }

    private int getEndReturnY() {
        return endReturnY;
    }

    private int getStartReturnY() {
        return startReturnY;
    }

    private int getEndCallY() {
        return endCallY;
    }

    private Dimension getCallTextOffset() {
        return callTextOffset;
    }

    private Dimension getReturnTextOffset() {
        return returnTextOffset;
    }

    private int getCallHorizontalSpace() {
        return callHorizontalSpace;
    }

    private int getReturnHorizontalSpace() {
        return returnHorizontalSpace;
    }

    private boolean isSelfCall() {
        return selfCall;
    }

    public String toString() {
        return "<Call-" + hashCode() + " callingMethodExecution " + callingMethodExecution + " calledMethodExecution " + calledMethodExecution + ">";
    }

    public boolean encloses(Point p) {
        return false;
    }

    public String getName() {
        return calledMethodExecution.getName();
    }

    public ItemIdentifier getIdentifier() {
        return null;
    }

    /**
     * Return the object life line of the method this call is calling.
     * @return an object life line
     */
    public ObjectLifeLine getObjectLifeLine() {
        return calledMethodExecution.getObjectLifeLine();
    }
}
