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

import com.zanthan.sequence.headless.PngCreator;
import com.zanthan.sequence.parser.ParserException;
import com.zanthan.sequence.preferences.CurrentDirectory;
import com.zanthan.sequence.swing.Sequence;
import org.apache.log4j.Logger;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Main {

    private static final Logger log = Logger.getLogger(Main.class);

    private static boolean isHeadless = false;
    private static boolean offerParserChoice = false;
    private static boolean useOldParser = false;

    private static void runHeadless(List args) {
        if (args.size() == 0) {
            log.error("Running Headless. Must supply an input file name on the command line.");
            log.error("Use java -jar sequence.jar [--use_old_parser] --headless <input_file_name>");
            System.exit(1);
        }
        String inFileName = (String) args.get(0);
        String outFileName = inFileName;
        int dotPosition = outFileName.lastIndexOf('.');
        if (dotPosition > -1) {
            outFileName = outFileName.substring(0, dotPosition);
        }
        outFileName += ".png";
        System.out.println("Reading \"" + inFileName + "\" " +
                           "and creating \"" + outFileName + "\".");
        try {
            PngCreator pngCreator = new PngCreator(inFileName, outFileName, useOldParser);
            pngCreator.output();
            System.out.println("Done.");
        } catch (IOException e) {
            log.error(e);
            System.out.println("Error occurred.");
            System.exit(1);
        } catch (ParserException e) {
            log.error(e);
            System.out.println("Error occurred.");
            System.exit(1);
        }
    }

    private static void runWithDisplay(List args) {
        determineInitialDirectory(args);
        Sequence sequence = new Sequence(offerParserChoice, args);
    }

    private static void determineInitialDirectory(List args) {
        File initialDir;
        try {
            if (args.size() == 0) {
                initialDir = getExistingDir(new File(System.getProperty("user.dir")).getCanonicalFile());
            } else {
                initialDir = getExistingDir(new File((String) args.get(0)).getCanonicalFile());
            }
        } catch (IOException e) {
            initialDir = null;
        }
        CurrentDirectory.setCurrentDirectory(initialDir);
    }

    private static File getExistingDir(File startingDir) {
        File dir = startingDir;
        while (dir != null) {
            if (dir.isDirectory() && dir.exists()) {
                break;
            }
            dir = dir.getParentFile();
        }
        return dir;
    }

    /**
     * Process the array of arguments stripping out those we recognize and returning a list containing the
     * remaining ones.
     *
     * @param args the initial array of arguments
     * @return a list of strings containing the arguments left after removing the know ones
     */
    private static List parseArgs(String[] args) {
        List argsList = new ArrayList(Arrays.asList(args));

        for (Iterator it = argsList.listIterator(); it.hasNext();) {
            String arg = (String) it.next();
            if (arg.equals("--headless")) {
                isHeadless = true;
                it.remove();
            } else if (arg.equals("--offer_parser_choice")) {
                offerParserChoice = true;
                it.remove();
            } else if (arg.equals("--use_old_parser")) {
                useOldParser = true;
                it.remove();
            }
        }

        return argsList;
    }

    public static void main(String[] args) {
        List argsList = parseArgs(args);
        if (GraphicsEnvironment.isHeadless() || isHeadless) {
            runHeadless(argsList);
        } else {
            runWithDisplay(argsList);
        }
    }


}
