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

public interface DiagramParserIface {

    /**
     * Set the root object for the diagram.
     * @param rootObjectLifeLine
     */
    void addRootObject(ObjectLifeLine rootObjectLifeLine);

    /**
     * Create a new Call from the callingMethod to the calledMethod with the specified returnType.
     *
     * @param callingMethod the method from which the call is made
     * @param calledMethod the method which is being called
     * @param returnType the return type from the calledMethod, may be null for void
     * @param userData any additional data the parser wants to store in the call
     * @return a new Call instance
     */
    Call newCall(MethodExecution callingMethod, MethodExecution calledMethod, String returnType, Object userData);

    /**
     * Create a new MethodExecution representing the execution of a method on an object.
     *
     * @param objectLifeLine the object lifeline the method is executed on
     * @param name the name of the method to execute
     * @param userData any additional data the parser wants to store with the method execution
     * @return a new MethodExecution instance
     */
    MethodExecution newMethodExecution(ObjectLifeLine objectLifeLine, String name, Object userData);

    /**
     * Create a new object lifeline.
     *
     * @param name the name of the object lifeline
     * @param userData any additional data the parser wants to store with the object lifeline
     * @return a new ObjectLifeline instance
     */
    ObjectLifeLine newObjectLifeLine(String name, Object userData);
}
