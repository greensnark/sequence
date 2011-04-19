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
package com.zanthan.sequence.swing;

import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

public class ActionResources {
    private String resourcePrefix;

    public ActionResources(String resourcePrefix) {
        this.resourcePrefix = resourcePrefix;
    }

    public String getName() {
        return getResource("name");
    }

    public String getShortDescription() {
        return getResource("shortDesc");
    }

    public ImageIcon getIcon() {
        return getIcon("icon");
    }

    String getResource(String key) {
        return SequenceResources.getString(resourcePrefix + "." + key);
    }

    private ImageIcon getIcon(String key) {
        URL iconURL = getClass().getResource(getResource(key));
        return new ImageIcon(iconURL, key);
    }

}
