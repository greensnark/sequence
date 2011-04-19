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

public class ObjectIdentifier {
    private String objectName;
    private String className;
    private String stereotype;
    private String name;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
        name = null;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
        name = null;
    }

    public String getStereotype() {
        return stereotype;
    }

    public void setStereotype(String stereotype) {
        this.stereotype = stereotype;
        name = null;
    }

    public String getName() {
        if (name != null) {
            return name;
        }
        StringBuffer sb = new StringBuffer();
        if (objectName != null) {
            sb.append(objectName);
        }
        if (className != null) {
            if (sb.length() > 0) {
                sb.append('.');
            }
            sb.append(className);
        }
        if (stereotype != null) {
            if (sb.length() > 0) {
                sb.append('.');
            }
            sb.append(stereotype);
        }
        name = sb.toString();
        return name;
    }
}
