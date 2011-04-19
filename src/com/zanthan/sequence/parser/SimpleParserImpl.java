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
package com.zanthan.sequence.parser;

import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;

import com.zanthan.sequence.diagram.DiagramParserIface;
import com.zanthan.sequence.diagram.MethodExecution;
import com.zanthan.sequence.diagram.ObjectLifeLine;

public class SimpleParserImpl implements Parser {

    private static final Logger log =
            Logger.getLogger(SimpleParserImpl.class);

    private Tokenizer tokenizer;
    private Tokenizer.Token lastToken;
    private Map objectLifeLines;
    private Stack methodExecutionStack;
    private DiagramParserIface diagram;

    public void setDiagram(DiagramParserIface diagram) {
        this.diagram = diagram;
    }

    private void initialize(Reader r) {
        objectLifeLines = new HashMap();
        methodExecutionStack = new Stack();
        tokenizer = new Tokenizer(r);
        lastToken = null;
    }

    /**
     * Parse a string.
     *
     * @param s the string to parse
     */
    public void parse(String s) {

        parse(new PushbackReader(new StringReader(s)));
    }

    /**
     * Parse the characters provided by a PushbackReader.
     *
     * @param r the reader to parse
     */
    public void parse(Reader r) {

        if (log.isDebugEnabled())
            log.debug("parse(...)");

        initialize(r);

        boolean wantOpenParen = true;
        while (true) {
            Tokenizer.Token tok = nextToken();
            if (tok == null)
                break;
            if (wantOpenParen) {
                if (tok.getType() == Tokenizer.OPEN_PAREN_TOKEN_TYPE) {
                    wantOpenParen = false;
                    parseCall();
                } else {
                    log.warn("Looking for open paren. Found " + tok);
                }
            } else {
                if (tok.getType() == Tokenizer.OPEN_PAREN_TOKEN_TYPE) {
                    parseCall();
                } else if (tok.getType() == Tokenizer.CLOSE_PAREN_TOKEN_TYPE) {
                    parsedReturn();
                } else {
                    log.warn("Found unwanted token " + tok);
                }
            }
        }

    }

    /**
     * Try to parse an object call from the next three tokens returned
     * from the tokenizer.
     *
     */
    private void parseCall() {

        if (log.isDebugEnabled())
            log.debug("parseCall()");

        String objName = getStringTokenValue();
        if (objName == null)
            return;
        String mthName = getStringTokenValue();
        if (mthName == null)
            return;
        String rtnType = getStringTokenValue();
        if (rtnType == null)
            rtnType = "void";
        parsedCall(objName, mthName, rtnType);
    }

    /**
     * Return the value of the next token if it is a string token,
     * otherwise return null;
     *
     * @return the value of the next token or null
     */
    private String getStringTokenValue() {
        Tokenizer.Token tok = nextToken();
        if (tok == null)
            return null;
        if (tok.getType() != Tokenizer.STRING_TOKEN_TYPE) {
            pushToken(tok);
            return null;
        }

        return tok.getValue();
    }

    /**
     * Return the next token to process, or null if no more tokens
     * are available.
     *
     * @return the next token
     */
    private Tokenizer.Token nextToken() {
        if (lastToken != null) {
            Tokenizer.Token tok = lastToken;
            lastToken = null;
            return tok;
        } else {
            if (tokenizer.hasNext()) {
                return (Tokenizer.Token) tokenizer.next();
            } else {
                return null;
            }
        }
    }

    /**
     * Save a token so that the next call to nextToken will return it.
     *
     * @param tok the token to save
     */
    private void pushToken(Tokenizer.Token tok) {
        if (lastToken != null)
            throw new RuntimeException("InternalError. pushToken called with lastToken not null");
        lastToken = tok;
    }

    /**
     * A call has been recognized during the parse.
     *
     * @param calledObject the object being called
     * @param calledMethod the method being called on the object
     * @param returnType the type of the variable being returned from the call
     */
    private void parsedCall(String calledObject, String calledMethod, String returnType) {

        // Find or create the called object
        ObjectLifeLine objectLifeLine = (ObjectLifeLine) objectLifeLines.get(calledObject);
        if (objectLifeLine == null) {
            objectLifeLine = diagram.newObjectLifeLine(calledObject, null);
            objectLifeLines.put(objectLifeLine.getName(), objectLifeLine);
            // Is this another root?
            if (methodExecutionStack.isEmpty()) {
                diagram.addRootObject(objectLifeLine);
            }
        }
        // Create the new method to be executed
        MethodExecution newMethodExecution = diagram.newMethodExecution(objectLifeLine, calledMethod, null);
        // See if we're currently executing a method. We won't be if this is a root
        if (!methodExecutionStack.isEmpty()) {
            // Get the current method being executed
            MethodExecution currentMethodExecution = (MethodExecution) methodExecutionStack.peek();
            // Add a call from the current method being executed to the new method
            currentMethodExecution.addCall(diagram.newCall(currentMethodExecution, newMethodExecution, returnType, null));
        }
        // Add the new method execution to the object life line being called
        objectLifeLine.addMethodExecution(newMethodExecution);
        // Put the new method execution on the top of the stack
        methodExecutionStack.push(newMethodExecution);
    }

    /**
     * A return has been recognized during the parse.
     */
    private void parsedReturn() {
        // A malformed string may result in too many close parens. Don't
        // want an empty stack exception so guard against the problem
        if (!methodExecutionStack.isEmpty()) {
            methodExecutionStack.pop();
        }
    }

}
