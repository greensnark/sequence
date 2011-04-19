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

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import com.zanthan.sequence.layout.StringMeasure;
import com.zanthan.sequence.layout.StringMeasurement;

public class SwingStringMeasure
        implements StringMeasure {

    private Graphics2D g2;


    public SwingStringMeasure(Graphics2D g2) {
        this.g2 = g2;
    }

    public StringMeasurement measureString(String s) {
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds(s, g2);
        int maxAscent = fm.getMaxAscent();
        return new StringMeasurement(maxAscent + fm.getMaxDescent(), (int) rect.getWidth(), maxAscent);
    }
}
