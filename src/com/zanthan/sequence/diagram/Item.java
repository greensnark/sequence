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

import java.awt.Point;

import com.zanthan.sequence.layout.LayoutData;
import com.zanthan.sequence.layout.Painter;

public abstract class Item {

    Object userData;
    boolean selected = false;

    public abstract String getName();

    public Object getUserData() {
        return userData;
    }

    public boolean isSelected() {
        return selected;
    }

    public void select() {
        selected = true;
    }

    public void deselect() {
        selected = false;
    }

    public abstract ObjectLifeLine getObjectLifeLine();

    public abstract ItemIdentifier getIdentifier();

    public abstract void layout(LayoutData layoutData);

    public abstract void paint(Painter painter);

    public abstract boolean encloses(Point p);
}
