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

public interface NodeFactory {

    /**
     * Construct and return a new ObjectLifeLine.
     * @param name the name of the object
     * @param userData any additional data the user wants to associate with the object life line
     * @return the new object
     */
    public ObjectLifeLine newObjectLifeLine(String name, Object userData);

    /**
     * Construct and return a new MethodExecution
     * @param objectLifeLine the object the method is executing on
     * @param name the name of the method
     * @param userData any additional data the user wants to associate with the method execution
     * @return the new object
     */
    public MethodExecution newMethodExecution(ObjectLifeLine objectLifeLine, String name, Object userData);

    /**
     * Construct and return a new Call.
     * @param callingMethod the method that is the origin of the call
     * @param calledMethod the method that is being called
     * @param returnType the type of the return
     * @param userData any additional data the user wants to associate with the call
     * @return the new object
     */
    public Call newCall(MethodExecution callingMethod, MethodExecution calledMethod, String returnType, Object userData);
}
