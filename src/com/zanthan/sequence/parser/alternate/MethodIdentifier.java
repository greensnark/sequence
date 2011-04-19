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

public class MethodIdentifier {
    private boolean iteration;
    private String condition;
    // Either methodName or stereotype can be set, not both.
    private String methodName;
    private String stereotype;
    // arguments may be null
    private String arguments;

    private String name;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
        name = null;
    }

    private boolean isIteration() {
        return iteration;
    }

    public void setIteration(boolean iteration) {
        this.iteration = iteration;
        name = null;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
        stereotype = null;
        name = null;
    }

    public String getStereotype() {
        return stereotype;
    }

    public void setStereotype(String stereotype) {
        this.stereotype = stereotype;
        methodName = null;
        name = null;
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public String getName() {
        if (name != null) {
            return name;
        }
        StringBuffer sb = new StringBuffer();
        if (isIteration()) {
            sb.append('*');
        }
        if (condition != null) {
            sb.append(condition);
        }
        // Add a space to separate the iteration and condition
        // from the method name.
        if (sb.length() > 0) {
            sb.append(" ");
        }
        if (methodName != null) {
            sb.append(methodName);
        } else {
            sb.append(stereotype);
        }
        if (arguments != null) {
            sb.append(arguments);
        }
        name = sb.toString();
        return name;
    }
}
