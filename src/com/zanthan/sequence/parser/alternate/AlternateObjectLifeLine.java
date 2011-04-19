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
package com.zanthan.sequence.parser.alternate;

import java.util.List;
import java.util.ArrayList;
import java.awt.Rectangle;

import org.apache.log4j.Logger;

import com.zanthan.sequence.diagram.ObjectLifeLine;
import com.zanthan.sequence.layout.StringMeasurement;
import com.zanthan.sequence.layout.LayoutData;
import com.zanthan.sequence.layout.Painter;

/**
 * 
 * @author Alex Moffat
 * @version 1.0
 */
public class AlternateObjectLifeLine extends ObjectLifeLine {

    private static final Logger log = Logger.getLogger(AlternateObjectLifeLine.class);

    private String[] strings;

    public AlternateObjectLifeLine(String name, Object userData) {
        super(name, userData);
        ObjectIdentifier oi = (ObjectIdentifier) getUserData();
        List stringList = new ArrayList();
        if (oi.getObjectName() != null) {
            stringList.add(oi.getObjectName());
        }
        if (oi.getClassName() != null) {
            stringList.add(oi.getClassName());
        }
        if (oi.getStereotype() != null) {
            stringList.add(oi.getStereotype());
        }
        strings = (String[]) stringList.toArray(new String[stringList.size()]);
    }

    protected StringMeasurement getHeaderMeasurement(LayoutData layoutData) {
        ObjectIdentifier oi = (ObjectIdentifier) getUserData();
        List measurements = new ArrayList();
        int numberOfStrings = 0;
        if (oi.getObjectName() != null) {
            measurements.add(layoutData.measureString(oi.getObjectName()));
        }
        if (oi.getClassName() != null) {
            measurements.add(layoutData.measureString(oi.getClassName()));
        }
        if (oi.getStereotype() != null) {
            measurements.add(layoutData.measureString(oi.getStereotype()));
        }
        StringMeasurement sm = (StringMeasurement) measurements.get(0);
        for (int i = 1; i < measurements.size(); i++) {
            StringMeasurement stringMeasurement = (StringMeasurement) measurements.get(i);
            if (stringMeasurement.getWidth() > sm.getWidth()) {
                sm.setWidth(stringMeasurement.getWidth());
            }
            sm.setHeight(sm.getHeight() + stringMeasurement.getHeight());
        }
        return sm;
    }

    public void paint(Painter painter) {
           int x = getX();
           int y = getY();
           int headerWidth = getHeaderWidth();
           int headerHeight = getHeaderHeight();
           painter.paintObjectLifeLineHeader(strings,
                                             new Rectangle(x, y, headerWidth, headerHeight),
                                             getTextOffset(),
                                             isSelected());

           int centerX = getCenterX();
           painter.paintObjectLifeLineLine(centerX, y + headerHeight, centerX, painter.getCanvasMaxY());
       }

}
