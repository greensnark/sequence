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
package com.zanthan.sequence.swing.model;

import org.apache.log4j.Logger;

import com.zanthan.sequence.swing.CommonDialogs;

public abstract class ModifiedConfirmAction
        extends ModelAction {

    private static final Logger log =
            Logger.getLogger(ModifiedConfirmAction.class);

    public ModifiedConfirmAction(String resourcePrefix, Model model, boolean withIcon) {
        super(resourcePrefix, model, withIcon);
    }

    public ModifiedConfirmAction(String name, String shortDescription, Model model) {
        super(name, shortDescription, model);
    }

    public void performAction() {
        confirmThenDoIt();
    }

    public void confirmThenDoIt() {
        if (confirmed())
            doIt();
    }

    private boolean confirmed() {
        if (getModel().isModified()) {
            int ret = CommonDialogs.getInstance().showConfirmDialog(getConfirmMessage(),
                                                                    getConfirmTitle());
            if (ret == CommonDialogs.CANCEL_OPTION)
                return false;
            if (ret == CommonDialogs.YES_OPTION)
                if (!getModel().getSaveAction().doIt())
                    return false;
        }
        return true;
    }

    protected String getConfirmMessage() {
        return getResource("confirmMessage");
    }

    protected String getConfirmTitle() {
        return getResource("confirmTitle");
    }
}
