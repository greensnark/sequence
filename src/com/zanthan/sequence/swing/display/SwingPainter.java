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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.FontMetrics;
import java.awt.geom.Rectangle2D;

import org.apache.log4j.Logger;

import com.zanthan.sequence.layout.Painter;
import com.zanthan.sequence.preferences.Prefs;

public class SwingPainter
        implements Painter {

    //
    // MRing 4/8/2003
    // Normal or dashed strokes
    //
    private final static BasicStroke STROKE_NORM = new BasicStroke(1.0f);
    private final static float[] dash1 = {5.0f};
    private final static BasicStroke STROKE_DASH = new BasicStroke(1.0f,
                                                           BasicStroke.CAP_BUTT,
                                                           BasicStroke.JOIN_MITER,
                                                           5.0f, dash1, 0.0f);

    private static final Logger log = Logger.getLogger(SwingPainter.class);
    private Graphics2D graphics;

    private Color objectFillColor;
    private Color objectBorderColor;
    private Color objectTextColor;
    private Color objectLineColor;
    private Color methodFillColor;
    private Color methodBorderColor;
    private Color selectedFillColor;
    private Color selectedBorderColor;
    private Color callLineColor;
    private Color callTextColor;


    public SwingPainter() {
        objectFillColor = Prefs.getColorValue(Prefs.OBJECT_FILL_COLOR);
        objectBorderColor = Prefs.getColorValue(Prefs.OBJECT_BORDER_COLOR);
        objectTextColor = Prefs.getColorValue(Prefs.OBJECT_TEXT_COLOR);
        objectLineColor = Prefs.getColorValue(Prefs.OBJECT_LINE_COLOR);
        methodFillColor = Prefs.getColorValue(Prefs.METHOD_FILL_COLOR);
        methodBorderColor = Prefs.getColorValue(Prefs.METHOD_BORDER_COLOR);
        selectedFillColor = Prefs.getColorValue(Prefs.SELECTED_FILL_COLOR);
        selectedBorderColor = Prefs.getColorValue(Prefs.SELECTED_BORDER_COLOR);
        callLineColor = Prefs.getColorValue(Prefs.LINK_COLOR);
        callTextColor = Prefs.getColorValue(Prefs.LINK_TEXT_COLOR);
    }

    public void setGraphics(Graphics2D graphics) {
        this.graphics = graphics;
    }
    
    /**
     * Draw the header at the top of an object lifeline. This has a box around it and holds one or
     * more strings which are drawn centered horizontally.
     * @param strings the strings to draw
     * @param surroundingBox the box surrounding the strings
     * @param textOffset the offset from the left to the start of the longest string and from the top to the baseline
     * of the first string
     * @param selected should the header be drawn selected
     */
    public void paintObjectLifeLineHeader(String[] strings, Rectangle surroundingBox, Dimension textOffset, boolean selected) {

        if (selected) {
            paintBorderedRectangle(selectedFillColor, selectedBorderColor, surroundingBox);
        } else {
            paintBorderedRectangle(objectFillColor, objectBorderColor, surroundingBox);
        }

        graphics.setPaint(objectTextColor);
        FontMetrics fm = graphics.getFontMetrics();
        int yPos = surroundingBox.y;
        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];
            yPos += textOffset.height;
            int xPos = surroundingBox.x + textOffset.width;
            Rectangle2D rect = fm.getStringBounds(string, graphics);
            int spaceLeft = (int) (surroundingBox.width - rect.getWidth() - 2 * textOffset.width);
            if (spaceLeft > 0) {
                xPos += spaceLeft / 2;
            }
            graphics.drawString(string, xPos, yPos);
        }
    }

    public void paintObjectLifeLineLine(int fromX, int fromY, int toX, int toY) {

        paintLine(objectLineColor, fromX, fromY, toX, toY, STROKE_NORM);
    }

    public void paintMethodExecution(Rectangle size, boolean selected) {
        if (selected) {
            paintBorderedRectangle(selectedFillColor, selectedBorderColor, size);
        } else {
            paintBorderedRectangle(methodFillColor, methodBorderColor, size);
        }
    }

    public void paintCall(String name, Dimension textOffset,
                          int len, int fromX, int fromY, int toX, int toY) {
        graphics.setPaint(callTextColor);
        graphics.drawString(name, Math.min(fromX, toX) + textOffset.width, fromY - textOffset.height);
        int maxX = fromX + len;
        paintLine(callLineColor, fromX, fromY, maxX, fromY, STROKE_NORM);
        paintLine(callLineColor, maxX, fromY, maxX, toY, STROKE_NORM);
        paintLineWithArrow(callLineColor, maxX, toY, toX, toY, STROKE_NORM);
    }

    public void paintCall(String name, Dimension textOffset,
                          int fromX, int fromY, int toX, int toY) {
        graphics.setPaint(callTextColor);
        graphics.drawString(name, Math.min(fromX, toX) + textOffset.width, fromY - textOffset.height);

        paintLineWithArrow(callLineColor, fromX, fromY, toX, toY, STROKE_NORM);
    }

    public void paintReturn(String name, Dimension textOffset,
                            int len, int fromX, int fromY, int toX, int toY) {
        graphics.setPaint(callTextColor);
        graphics.drawString(name, Math.min(fromX, toX) + textOffset.width, fromY - textOffset.height);
        int maxX = fromX + len;
        paintLine(callLineColor, fromX, fromY, maxX, fromY, STROKE_DASH);
        paintLine(callLineColor, maxX, fromY, maxX, toY, STROKE_DASH);
        paintLineWithArrow(callLineColor, maxX, toY, toX, toY, STROKE_DASH);
    }

    public void paintReturn(String name, Dimension textOffset,
                            int fromX, int fromY, int toX, int toY) {
        graphics.setPaint(callTextColor);
        graphics.drawString(name, Math.min(fromX, toX) + textOffset.width, fromY - textOffset.height);

        paintLineWithArrow(callLineColor, fromX, fromY, toX, toY, STROKE_DASH);
    }

    public int getCanvasMaxY() {
        return (int) (graphics.getClipBounds().getHeight() + graphics.getClipBounds().getY());
    }

    private void paintBorderedRectangle(Color fillColor, Color borderColor, Rectangle size) {
        graphics.setPaint(fillColor);
        graphics.fillRect(size.x, size.y, size.width, size.height);

        graphics.setPaint(borderColor);
        graphics.drawRect(size.x, size.y, size.width - 1, size.height - 1);
    }

    private void paintLine(Color lineColor, int fromX, int fromY, int toX, int toY, Stroke stroke) {
        graphics.setStroke(stroke);
        graphics.setPaint(lineColor);
        graphics.drawLine(fromX, fromY, toX, toY);
    }

    private void paintLineWithArrow(Color lineColor, int fromX, int fromY, int toX, int toY, Stroke stroke) {
        graphics.setStroke(stroke);
        graphics.setPaint(lineColor);
        graphics.drawLine(fromX, fromY, toX, toY);
        int x[] = new int[3];
        int y[] = new int[3];
        x[0] = toX;
        y[0] = toY;
        int xOffset;
        if (toX > fromX) {
            xOffset = -5;
        } else {
            xOffset = 5;
        }
        x[1] = toX + xOffset;
        y[1] = toY + 4;
        x[2] = toX + xOffset;
        y[2] = toY - 3;
        Shape arrow = new Polygon(x, y, 3);
        graphics.fill(arrow);
    }
}
