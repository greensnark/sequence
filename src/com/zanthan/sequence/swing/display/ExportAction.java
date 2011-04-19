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
package com.zanthan.sequence.swing.display;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.zanthan.sequence.preferences.Prefs;
import com.zanthan.sequence.swing.CommonDialogs;
import com.zanthan.sequence.swing.ExceptionHandler;
import com.zanthan.sequence.swing.SequenceAction;
import com.zanthan.sequence.swing.model.Model;

public class ExportAction
        extends SequenceAction {

    private static final Logger log =
            Logger.getLogger(ExportAction.class);

    private ExceptionHandler exceptionHandler;
    private Display display = null;
    private Model model;

    ExportAction(ExceptionHandler exceptionHandler, Display display, Model model) {
        super("ExportAction", true);
        this.exceptionHandler = exceptionHandler;
        this.display = display;
        this.model = model;
    }

    public void performAction() {
        int returnVal = CommonDialogs.getInstance().showSaveDialog(getResource("dialogTitle"),
                                                                   model.getFile());
        if (returnVal == CommonDialogs.APPROVE_OPTION) {
            export(CommonDialogs.getInstance().getSelectedFile());
        }
    }

    private void export(File file) {
        Dimension size = display.getPreferredSize();
        int height = size.height + Prefs.getIntegerValue(Prefs.INITIAL_Y_POSITION) + 1;
        BufferedImage bi = new BufferedImage(size.width,
                                             height,
                                             BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = bi.createGraphics();
        graphics.setClip(0, 0, size.width, height);
        display.paintComponent(graphics);
        try {
            ImageIO.write(bi, "png", file);
        } catch (IOException ioe) {
            exceptionHandler.exception(ioe);
        }
    }
}
