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
package com.zanthan.sequence;

import javax.swing.JDialog;

import com.zanthan.sequence.diagram.Diagram;
import com.zanthan.sequence.diagram.NodeFactoryImpl;
import com.zanthan.sequence.parser.SimpleParserImpl;
import com.zanthan.sequence.swing.ExceptionHandler;
import com.zanthan.sequence.swing.SequencePanel;

public class SimpleTest {

    public static void main(String[] args) {
        SequencePanel panel = new SequencePanel(true, new ExceptionHandler() {
            /**
             * Call this when an exception occurs.
             * @param e
             */
            public void exception(Exception e) {
                e.printStackTrace();
            }
        }, new Diagram(new SimpleParserImpl(), new NodeFactoryImpl()));
        JDialog dialog = new JDialog();
        dialog.getContentPane().add(panel);
        dialog.show();
    }
}
