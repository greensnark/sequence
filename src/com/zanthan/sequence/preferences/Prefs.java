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
package com.zanthan.sequence.preferences;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

public class Prefs {

    private static final Logger log =
            Logger.getLogger(Prefs.class);

//    public final static String FIRST_OBJECT_NAME = "first_object_name";
//    public final static String FIRST_METHOD_NAME = "first_method_name";

    public final static String INITIAL_X_POSITION = "initial_x_position";
    public static final String INITIAL_Y_POSITION = "initial_y_position";
    public static final String METHOD_EXECUTION_WIDTH = "method_execution_width";


    private final static String SHOW_RETURN_TYPES = "show_return_types";
    public final static String BACKGROUND_COLOR = "background_color";
    public final static String LINK_TEXT_COLOR = "link_text_color";
    public final static String LINK_COLOR = "link_color";
    public final static String OBJECT_BORDER_COLOR = "object_border_color";
    public final static String OBJECT_FILL_COLOR = "object_fill_color";
    public final static String OBJECT_TEXT_COLOR = "object_text_color";
    public final static String OBJECT_LINE_COLOR = "object_line_color";
    public final static String METHOD_BORDER_COLOR = "method_border_color";
    public final static String METHOD_FILL_COLOR = "method_fill_color";
    public final static String SELECTED_BORDER_COLOR = "selected_border_color";
    public final static String SELECTED_FILL_COLOR = "selected_fill_color";
    public final static String TEXT_X_PAD = "text_x_pad";
    public final static String TEXT_Y_PAD = "text_y_pad";
    public final static String OBJECT_LIFELINE_SPACING = "object_lifeline_spacing";
    public final static String METHOD_EXECUTION_SPACING = "method_execution_spacing";

    private static final String[] propertyNames = {
        INITIAL_X_POSITION,
        INITIAL_Y_POSITION,
        METHOD_EXECUTION_WIDTH,
        SHOW_RETURN_TYPES,
        BACKGROUND_COLOR,
        LINK_TEXT_COLOR,
        LINK_COLOR,
        OBJECT_BORDER_COLOR,
        OBJECT_FILL_COLOR,
        OBJECT_TEXT_COLOR,
        OBJECT_LINE_COLOR,
        METHOD_FILL_COLOR,
        METHOD_BORDER_COLOR,
        SELECTED_FILL_COLOR,
        SELECTED_BORDER_COLOR,
        TEXT_X_PAD,
        TEXT_Y_PAD,
        OBJECT_LIFELINE_SPACING,
        METHOD_EXECUTION_SPACING
    };

    private static Prefs instance = null;

    private ResourceBundle bundle = null;
    private List preferencesList = new ArrayList();
    private Map preferencesMap = new HashMap();

    public static boolean getBooleanValue(String preferenceName) {
        return getInstance().getPref(preferenceName).getBooleanValue();
    }

    public static Color getColorValue(String preferenceName) {
        return getInstance().getPref(preferenceName).getColorValue();
    }

    public static String getStringValue(String preferenceName) {
        return getInstance().getPref(preferenceName).getStringValue();
    }

    public static int getIntegerValue(String preferenceName) {
        return getInstance().getPref(preferenceName).getIntegerValue();
    }

    private static Prefs getInstance() {
        if (instance == null) {
            instance = new Prefs();
            instance.init();
        }
        return instance;
    }

    public static void setInstance(Prefs inst) {
        instance = inst;
    }

    public static Prefs getClonedInstance() {
        Prefs clone = new Prefs();
        try {
            for (Iterator it = instance.getPrefs(); it.hasNext();) {
                Pref pref = (Pref) ((Pref) it.next()).clone();
                clone.preferencesList.add(pref);
                clone.preferencesMap.put(pref.getName(), pref);
            }
        } catch (CloneNotSupportedException cnse) {
            throw new RuntimeException(cnse);
        }
        return clone;
    }

    public static void save() {
        instance._save();
    }

    private Prefs() {
    }

    private void init() {
        String prefsFileName = System.getProperty("zanthan.prefs");
        try {
            if (prefsFileName != null) {
                File prefsFile = new File(prefsFileName);
                if (prefsFile.exists() && prefsFile.isFile() && prefsFile.canRead())
                    bundle = new PropertyResourceBundle(new FileInputStream(prefsFile));
            }
        } catch (IOException ioe) {
            //Sequence.getInstance().exception(ioe);
        } finally {
            if (bundle == null)
                bundle = ResourceBundle.getBundle("com.zanthan.sequence.preferences.Preferences");
        }
        for (int i = 0; i < propertyNames.length; ++i) {
            Pref pref = new Pref(propertyNames[i]);
            preferencesList.add(pref);
            preferencesMap.put(pref.getName(), pref);
        }
    }

    private void _save() {
        String prefsFileName = System.getProperty("zanthan.prefs");
        if (prefsFileName == null)
            return;
        File prefsFile = new File(prefsFileName);
        try {
            PrintWriter out
                    = new PrintWriter(new BufferedWriter(new FileWriter(prefsFile)));
            for (Iterator it = getPrefs(); it.hasNext();) {
                ((Pref) it.next()).write(out);
            }
            out.close();
        } catch (IOException ioe) {
            //Sequence.getInstance().exception(ioe);
        }
    }

    public Iterator getPrefs() {
        return preferencesList.iterator();
    }

    private Pref getPref(String preferenceName) {
        return (Pref) preferencesMap.get(preferenceName);
    }

    public class Pref
            implements Cloneable {

        private String name = null;
        private String label = null;
        private String shortDesc = null;
        private String value = null;
        private String type = null;
        private Color color = null;

        private Pref(String name) {
            this.name = name;
            label = bundle.getString(name + ".label");
            shortDesc = bundle.getString(name + ".shortDesc");
            value = bundle.getString(name + ".value");
            type = bundle.getString(name + ".type");
            if (type.equals("color"))
                color = new Color(getColorInt(value));
        }

        private int getColorInt(String value) {
            int pos = value.indexOf(' ');
            int red = Integer.parseInt(value.substring(0, pos));
            int lastPos = pos + 1;
            pos = value.indexOf(' ', lastPos);
            int green = Integer.parseInt(value.substring(lastPos, pos));
            lastPos = pos + 1;
            int blue = Integer.parseInt(value.substring(lastPos));

            return (((red * 256) + green) * 256) + blue;
        }

        public String getName() {
            return name;
        }

        public String getLabel() {
            return label;
        }

        public String getShortDesc() {
            return shortDesc;
        }

        public String getStringValue() {
            return value;
        }

        public void setStringValue(String val) {
            value = val;
        }

        public boolean getBooleanValue() {
            return value.equals("true");
        }

        public void setBooleanValue(boolean val) {
            value = (val ? "true" : "false");
        }

        public Color getColorValue() {
            return color;
        }

        public void setColorValue(Color val) {
            color = val;
            value = color.getRed() + " " + color.getGreen() + " " + color.getBlue();
        }

        public int getIntegerValue() {
            return Integer.parseInt(value);
        }

        public String getType() {
            return type;
        }

        private void write(PrintWriter pw) {
            pw.println(name + ".label = " + label);
            pw.println(name + ".shortDesc = " + shortDesc);
            pw.println(name + ".type = " + type);
            pw.println(name + ".value = " + value);
            pw.println();
        }

        public Object clone()
                throws CloneNotSupportedException {

            return super.clone();
        }


    }
}
