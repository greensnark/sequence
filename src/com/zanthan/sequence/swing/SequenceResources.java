package com.zanthan.sequence.swing;

import java.util.ResourceBundle;

/**
 *
 */
public class SequenceResources {

    private static ResourceBundle bundle = null;

    public static ResourceBundle getResources() {
        if (bundle == null) {
            bundle = ResourceBundle.getBundle("com.zanthan.sequence.swing.Sequence");
        }
        return bundle;
    }

    public static String getString(String key) {
        return getResources().getString(key);
    }
}
