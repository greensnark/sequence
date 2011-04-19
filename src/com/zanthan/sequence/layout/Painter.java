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

import java.awt.Dimension;
import java.awt.Rectangle;

public interface Painter {

    /**
     * Draw the header at the top of an object lifeline. This has a box around it and holds one or
     * more strings which are drawn centered horizontally.
     * @param strings the strings to draw
     * @param surroundingBox the box surrounding the strings
     * @param textOffset the offset from the left to the start of the longest string and from the top to the baseline
     * of the first string
     * @param selected should the header be drawn selected
     */ 
    public void paintObjectLifeLineHeader(String[] strings,
                                          Rectangle surroundingBox,
                                          Dimension textOffset,
                                          boolean selected);

    public void paintObjectLifeLineLine(int fromX, int fromY, int toX, int toY);

    public void paintMethodExecution(Rectangle size, boolean selected);

    /**
     * Draw the line to represent a call from one object to another
     * @param name the text to associate with the call
     * @param textOffset the x and y offset from the fromX and fromY values to the name
     * @param fromX the x coordinate of the start of the line
     * @param fromY the y coordinate of the start of the line
     * @param toX the x coordinate of the end of the line
     * @param toY the y coordinate of the end of the line
     */
    public void paintCall(String name, Dimension textOffset,
                          int fromX, int fromY, int toX, int toY);

    public void paintCall(String name, Dimension textOffset,
                          int len, int fromX, int fromY, int toX, int toY);

    public void paintReturn(String name, Dimension textOffset,
                            int fromX, int fromY, int toX, int toY);

    public void paintReturn(String name, Dimension textOffset,
                            int len, int fromX, int fromY, int toX, int toY);

    public int getCanvasMaxY();
}
