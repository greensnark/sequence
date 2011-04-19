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
package com.zanthan.sequence.swing.preferences;

import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;

class PositiveNumberFormattedTextFieldVerifier
        extends InputVerifier {

    public boolean verify(JComponent input) {
        if (input instanceof JFormattedTextField) {
            JFormattedTextField ftf = (JFormattedTextField) input;
            NumberFormatter formatter = (NumberFormatter) ftf.getFormatter();
            if (formatter != null) {
                String text = ftf.getText();
                try {
                    Number n = ((NumberFormat) formatter.getFormat()).parse(text);
                    if (n.intValue() > 0) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (ParseException pe) {
                    return false;
                }
            }
        }
        return true;
    }
}
