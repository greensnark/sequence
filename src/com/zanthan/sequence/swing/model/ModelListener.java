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

import java.util.EventListener;

public interface ModelListener
        extends EventListener {

    /**
     * Called when the text has been successfully parsed and the model updated.
     *
     * @param mpse contains the string parsed and the diagram
     */
    public void modelParseSucceeded(ModelParseSucceededEvent mpse);

    /**
     * Called when the parser detects an error in parsing the text.
     *
     * @param mpfe contains the string being parsed, the diagram, and the parser exception
     */
    public void modelParseFailed(ModelParseFailedEvent mpfe);

    /**
     * Called when the preferences have changed.
     *
     * @param mpce contains the string parsed and the diagram
     */
    public void modelPreferencedChanged(ModelPreferencesChangedEvent mpce);
}

