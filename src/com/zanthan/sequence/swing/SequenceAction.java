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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apache.log4j.Logger;

public abstract class SequenceAction
        extends AbstractAction {

    private static final Logger log =
            Logger.getLogger(SequenceAction.class);

    private ActionResources actionResources = null;

    public SequenceAction(String resourcePrefix) {
        this(resourcePrefix, true);
    }

    public SequenceAction(String resourcePrefix, boolean withIcon) {

        actionResources = new ActionResources(resourcePrefix);

        putValue(NAME, actionResources.getName());

        putValue(SHORT_DESCRIPTION, actionResources.getShortDescription());

        if (withIcon)
            putValue(SMALL_ICON, actionResources.getIcon());
    }

    public SequenceAction(String name, String shortDescription) {
        putValue(NAME, name);
        putValue(SHORT_DESCRIPTION, shortDescription);
    }

    protected String getResource(String key) {
        return actionResources.getResource(key);
    }

    public void actionPerformed(ActionEvent e) {
        performAction();
    }

    public abstract void performAction();
}
