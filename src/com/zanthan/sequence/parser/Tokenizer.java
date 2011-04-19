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

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

/**
 * Convert the characters read from a PushbackReader into a series of
 * tokens. There are three sorts of token, strings, open parens, and
 * close parens. Strings can be surrounded by double or single quotes.
 * Inside a single quoted string a double quote can be used without
 * having to escape it with a \, and vice versa.
 *
 */
public class Tokenizer
        implements Iterator {

    private static final Logger log =
            Logger.getLogger(Tokenizer.class);

    /**
     * A token representing a string.
     */
    public static final int STRING_TOKEN_TYPE = 1;
    /**
     * A token representing a open paren.
     */
    public static final int OPEN_PAREN_TOKEN_TYPE = 2;
    /**
     * A token representing a close paren.
     */
    public static final int CLOSE_PAREN_TOKEN_TYPE = 3;

    private PushbackReader r = null;

    private Token nextToken = null;

    public Tokenizer(Reader r) {
        if (r instanceof PushbackReader) {
            this.r = (PushbackReader) r;
        } else {
            this.r = new PushbackReader(r);
        }
    }

    /**
     * Return true if there is another token available.
     *
     * @return true or false
     */
    public boolean hasNext() {
        if (nextToken == null)
            findNextToken();
        return nextToken != null;
    }

    /**
     * Return the next token.
     *
     * @return an instance of Token
     * @throws java.util.NoSuchElementException if no more tokens
     */
    public Object next() {
        if (nextToken == null) {
            findNextToken();
            if (nextToken == null)
                throw new NoSuchElementException();
        }
        Token t = nextToken;
        nextToken = null;
        return t;
    }

    /**
     * You can not remove tokens so this always
     * throws UnsupportedOperationException;
     *
     * @throws java.lang.UnsupportedOperationException always
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private void findNextToken() {

        try {
            skipWhitespace();
            readToken();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private void skipWhitespace()
            throws IOException {

        int c = -1;
        while (Character.isWhitespace((char) (c = r.read()))) {
        }

        if (c != -1)
            r.unread(c);
    }

    private void readToken()
            throws IOException {

        nextToken = null;
        int c = r.read();
        switch (c) {
            case -1:
                // End of read
                return;
            case '(':
                // Open paren
                nextToken = new OpenParenToken();
                return;
            case ')':
                // Close paren
                nextToken = new CloseParenToken();
                return;
            case '\'':
            case '"':
                // quoted string
                nextToken = new StringToken(readQuotedString((char) c));
                return;
            default:
                // anything else
                nextToken = new StringToken(readNonWhitespaceString((char) c));
                return;
        }
    }

    private String readQuotedString(char quoteChar)
            throws IOException {

        StringBuffer sb = new StringBuffer();
        int c = -1;
        boolean escaped = false;
        while ((c = r.read()) != -1) {
            if ((c == quoteChar) && !escaped)
                break;
            if (c == '\\') {
                escaped = true;
                continue;
            }
            escaped = false;
            sb.append((char) c);
        }
        return sb.toString();
    }

    private String readNonWhitespaceString(char firstChar)
            throws IOException {

        StringBuffer sb = new StringBuffer();
        sb.append(firstChar);
        int c = -1;
        while ((c = r.read()) != -1) {
            if ((c == ')') || (c == '('))
                break;
            else if (Character.isWhitespace((char) c))
                break;
            else
                sb.append((char) c);
        }
        if (c != -1)
            r.unread(c);
        return sb.toString();
    }

    public abstract static class Token {

        private String value = null;

        void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public abstract int getType();
    }

    public static class StringToken
            extends Token {

        private StringToken(String value) {
            setValue(value);
        }

        public int getType() {
            return STRING_TOKEN_TYPE;
        }

        public String toString() {
            return "StringToken: <" + getValue() + ">";
        }
    }

    public static class OpenParenToken
            extends Token {

        public String getValue() {
            return "(";
        }

        public int getType() {
            return OPEN_PAREN_TOKEN_TYPE;
        }

        public String toString() {
            return "OpenParenToken";
        }
    }

    public static class CloseParenToken
            extends Token {

        public String getValue() {
            return ")";
        }

        public int getType() {
            return CLOSE_PAREN_TOKEN_TYPE;
        }

        public String toString() {
            return "CloseParenToken";
        }
    }
}
