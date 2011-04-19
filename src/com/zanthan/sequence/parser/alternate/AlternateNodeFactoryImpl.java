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

import com.zanthan.sequence.diagram.NodeFactoryImpl;
import com.zanthan.sequence.diagram.ObjectLifeLine;

/**
 *
 * @author Alex Moffat
 * @version 1.0
 */
public class AlternateNodeFactoryImpl extends NodeFactoryImpl {

    /**
     * Construct and return a new ObjectLifeLine.
     * @param name the name of the object
     * @return the new object
     */
    public ObjectLifeLine newObjectLifeLine(String name,
                                            Object userData) {
        return new AlternateObjectLifeLine(name, userData);
    }
}
