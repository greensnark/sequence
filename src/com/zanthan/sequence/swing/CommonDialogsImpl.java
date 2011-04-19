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

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class CommonDialogsImpl
        extends CommonDialogs {

    public int showConfirmDialog(String message, String title) {
        return JOptionPane.showConfirmDialog(Sequence.getInstance(), message, title, JOptionPane.YES_NO_CANCEL_OPTION);
    }

    public void showMessageDialog(String message, String title) {
        JOptionPane.showMessageDialog(Sequence.getInstance(),
                                      message,
                                      title,
                                      JOptionPane.INFORMATION_MESSAGE);
    }

}
