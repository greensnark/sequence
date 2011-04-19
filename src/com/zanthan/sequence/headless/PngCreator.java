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
package com.zanthan.sequence.headless;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.zanthan.sequence.diagram.Diagram;
import com.zanthan.sequence.diagram.NodeFactoryImpl;
import com.zanthan.sequence.diagram.NodeFactory;
import com.zanthan.sequence.layout.LayoutData;
import com.zanthan.sequence.layout.StringMeasure;
import com.zanthan.sequence.parser.ParserException;
import com.zanthan.sequence.parser.SimpleParserImpl;
import com.zanthan.sequence.parser.ParserFactory;
import com.zanthan.sequence.parser.Parser;
import com.zanthan.sequence.parser.alternate.AlternateParserImpl;
import com.zanthan.sequence.parser.alternate.AlternateNodeFactoryImpl;
import com.zanthan.sequence.preferences.Prefs;
import com.zanthan.sequence.swing.display.SwingPainter;
import com.zanthan.sequence.swing.display.SwingStringMeasure;
import org.apache.log4j.Logger;

public class PngCreator {

    private static final Logger log = Logger.getLogger(PngCreator.class);

    private String inFileName;
    private String outFileName;
    private boolean useOldParser;

    public PngCreator(String inFileName, String outFileName, boolean useOldParser) {
        if (log.isDebugEnabled()) {
            log.debug("PngCreator(" +
                    inFileName + ", " +
                    outFileName + ", " +
                    useOldParser +
                    ")");
        }
        this.inFileName = inFileName;
        this.outFileName = outFileName;
        this.useOldParser = useOldParser;
    }

    public void output()
            throws IOException, ParserException {

        outputPng(layoutDiagram(createDiagram()));
    }

    private Diagram createDiagram()
            throws FileNotFoundException, ParserException {
        Parser parser;
        if (useOldParser) {
            parser = new SimpleParserImpl();
        } else {
            parser = ParserFactory.getInstance().getDefaultParser();
        }
        NodeFactory nodeFactory = ParserFactory.getInstance().getNodeFactoryForParser(parser);

        Diagram diagram = new Diagram(parser, nodeFactory);
        diagram.parse(new PushbackReader(new FileReader(inFileName)));
        return diagram;
    }

    private static LayoutData layoutDiagram(Diagram diagram) {
        // A very small image just so we can get a graphics instance for measuring strings.
        BufferedImage bi = new BufferedImage(10, 10,
                                             BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = bi.createGraphics();

        StringMeasure sm = new SwingStringMeasure(graphics);

        LayoutData layoutData = new LayoutData(sm);
        if (log.isDebugEnabled()) {
            log.debug("parsed diagram contains " + diagram.getAllObjects().size() + " objects");
        }
        diagram.layout(layoutData);

        return layoutData;
    }

    private void outputPng(LayoutData layoutData)
            throws IOException {

        int height = layoutData.getHeight();
        int width = layoutData.getWidth();

        BufferedImage png = new BufferedImage(width,
                                              height,
                                              BufferedImage.TYPE_INT_ARGB);
        Graphics2D pngGraphics = png.createGraphics();
        pngGraphics.setClip(0, 0, width, height);
        Map hintsMap = new HashMap();
        hintsMap.put(RenderingHints.KEY_ANTIALIASING,
                     RenderingHints.VALUE_ANTIALIAS_ON);
        pngGraphics.addRenderingHints(hintsMap);
        pngGraphics.setBackground(Prefs.getColorValue(Prefs.BACKGROUND_COLOR));
        pngGraphics.fillRect(0, 0, width, height);

        SwingPainter painter = new SwingPainter();
        painter.setGraphics(pngGraphics);
        layoutData.paint(painter);

        ImageIO.write(png, "png", new File(outFileName));
    }
}
