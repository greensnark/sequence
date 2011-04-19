/**
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
package com.zanthan.sequence.diagram;

import java.io.PushbackReader;
import java.io.StringReader;

import org.apache.log4j.Logger;

import com.zanthan.sequence.parser.Tokenizer;
import junit.framework.TestCase;

public class TestTokenizer
		extends TestCase {

	private static final Logger log =
			Logger.getLogger(TestTokenizer.class);

	public TestTokenizer(String name) {
		super(name);
	}

	public void test00() {
		checkTokenizer("(objOne methOne (objTwo methTwo))",
				new String[]{
					"(",
					"objOne",
					"methOne",
					"(",
					"objTwo",
					"methTwo",
					")",
					")"
				});
	}

	public void test01() {
		checkTokenizer("(Comments newComment (CommentFactory newComment (Comment new (IDGen nextID) (Transaction add<this>))) (Comments addToCache))",
				new String[]{
					"(",
					"Comments",
					"newComment",
					"(",
					"CommentFactory",
					"newComment",
					"(",
					"Comment",
					"new",
					"(",
					"IDGen",
					"nextID",
					")",
					"(",
					"Transaction",
					"add<this>",
					")",
					")",
					")",
					"(",
					"Comments",
					"addToCache",
					")",
					")"
				});
	}

	public void test02() {
		checkTokenizer("('single quoted' \"double quoted\" ('single and \" double' \"double and 'single\") (\"double \\\" escaped\" 'single \\' escaped'))",
				new String[]{
					"(",
					"single quoted",
					"double quoted",
					"(",
					"single and \" double",
					"double and 'single",
					")",
					"(",
					"double \" escaped",
					"single ' escaped",
					")",
					")"
				});
	}

	private void checkTokenizer(String s, String[] tokenValues) {
		log.debug("Checking string " + s);
		Tokenizer t = new Tokenizer(new PushbackReader(new StringReader(s)));
		int i = 0;
		while (t.hasNext()) {
			Tokenizer.Token tok = (Tokenizer.Token) t.next();
			assertEquals("Token number " + i,
					tokenValues[i],
					tok.getValue());
			++i;
		}
	}
}
