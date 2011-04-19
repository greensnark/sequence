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

import com.zanthan.sequence.preferences.CurrentDirectory;

import java.io.File;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public abstract class CommonDialogs {

    private static CommonDialogs thisInstance = null;

    public static final int YES_OPTION = JOptionPane.YES_OPTION;
    public static final int NO_OPTION = JOptionPane.NO_OPTION;
    public static final int CANCEL_OPTION = JOptionPane.CANCEL_OPTION;

    public static final int APPROVE_OPTION = JFileChooser.APPROVE_OPTION;

    private static String implementationClassName = null;

    protected static File file;

    private static String getImplementationClassName() {
        return implementationClassName;
    }

    public static void setImplementationClassName(String implementationClassName) {
        thisInstance = null;
        CommonDialogs.implementationClassName = implementationClassName;
    }

    /**
     * Return the instance of a CommonDialogs implementation to use. The
     * name of the class to instantiate is taken whichever of getImplementationClassName(),
     * the system property zanthan.CommonDialogsImpl or the CommonDialogs.ImplClass
     * property in Sequence.properties is found. If
     * any errors occur a RuntimeException is
     * thrown.
     * @return the instance of CommonDialogs to use
     */
    public static CommonDialogs getInstance() {
        if (thisInstance == null) {
            String className = getImplementationClassName();
            if (className == null) {
                className = System.getProperty("zanthan.CommonDialogsImpl");
            }
            if (className == null) {
                className = SequenceResources.getString("CommonDialogs.ImplClass");
            }
            if (className == null) {
                throw new RuntimeException("Can not find class name for CommonDialogs implementation to use.");
            }
            try {
                Class implClass = Class.forName(className);
                thisInstance = (CommonDialogs) implClass.newInstance();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Can not find class " + className, e);
            } catch (InstantiationException e) {
                throw new RuntimeException("Can not create instance of class " + className, e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Can not access class " + className, e);
            }
        }
        return thisInstance;
    }

    /**
     * Show a confirmation dialog with the message and title provided, yes, no, and cancel buttons.
     * The return is the button that was selected, which can be YES_OPTION, NO_OPTION, or CANCEL_OPTION.
     * @param message the message to show
     * @param title the title for the dialog
     * @return the button selected
     */
    public abstract int showConfirmDialog(String message, String title);

    public int showOpenDialog(String title, File suggestedFile) {
        final JFileChooser chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        setupChooser(chooser, title, suggestedFile);

        int returnVal = chooser.showOpenDialog(Sequence.getInstance());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            CommonDialogsImpl.file = chooser.getSelectedFile();
        } else {
            CommonDialogsImpl.file = null;
        }

        return returnVal;
    }

    public int showSaveDialog(String title, File suggestedFile) {
        final JFileChooser chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        setupChooser(chooser, title, suggestedFile);

        int returnVal = chooser.showSaveDialog(Sequence.getInstance());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            CommonDialogsImpl.file = chooser.getSelectedFile();
        } else {
            CommonDialogsImpl.file = null;
        }

        return returnVal;
    }

    private void setupChooser(JFileChooser chooser, String title, File suggestedFile) {
        chooser.setDialogTitle(title);
        if (suggestedFile != null) {
            File currentDirectory;
            if (suggestedFile.isDirectory()) {
                currentDirectory = suggestedFile;
            } else {
                currentDirectory = suggestedFile.getParentFile();
            }
            chooser.setCurrentDirectory(currentDirectory);
            CurrentDirectory.setCurrentDirectory(currentDirectory);
        } else {
            if (CurrentDirectory.getCurrentDirectory() != null) {
                chooser.setCurrentDirectory(CurrentDirectory.getCurrentDirectory());
            }
        }
    }

    /**
     * Show an informational message with a title
     * @param message the message to show
     * @param title the title for the message
     */
    public abstract void showMessageDialog(String message, String title);

    /**
     * Return the file selected by the last invocation of showSaveDialog or showOpenDialog.
     * @return the selected file, will be null if APPROVE_OPTION was not chosen.
     */
    public File getSelectedFile() {
        return CommonDialogsImpl.file;
    }
}
