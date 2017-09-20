/*
 * The MIT License
 *
 * Copyright 2015 osric.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.moosemorals.calculator.xml;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Bits that all parsers have in common
 *
 * @param <T> Return type for parser
 * @author Osric Wilkinson <osric@fluffypeople.com>
 */
public abstract class BaseParser<T> {

    protected final static String NAMESPACE = null;

    public T parse(InputStream in) throws IOException, XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newFactory();
        XMLStreamReader parser = factory.createXMLStreamReader(in, "UTF-8");

        parser.nextTag();

        return parse(parser);
    }

    public abstract T parse(XMLStreamReader parser) throws XMLStreamException, IOException;

    protected int readIntAttribute(XMLStreamReader parser, String name) throws XMLStreamException {
        String result = parser.getAttributeValue(NAMESPACE, name);
        if (result == null) {
            throw new XMLStreamException("Missing required " + name + " attribute at " + getLocation(parser));
        }
        try {
            return Integer.parseInt(result, 10);
        } catch (NumberFormatException ex) {
            throw new XMLStreamException("Can't parse integer from " + name + " attribute at " + getLocation(parser));
        }
    }

    protected double readDoubleAttribute(XMLStreamReader parser, String name) throws XMLStreamException {
        String raw = parser.getAttributeValue(NAMESPACE, name);
        if (raw == null) {
            throw new XMLStreamException("Missing required " + name + " attribute at " + getLocation(parser));
        }
        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException ex) {
            throw new XMLStreamException("Can't parse integer from " + name + " attribute at " + getLocation(parser));
        }
    }

    protected long readLongAttribute(XMLStreamReader parser, String name) throws XMLStreamException {
        String raw = parser.getAttributeValue(NAMESPACE, name);
        if (raw == null) {
            throw new XMLStreamException("Missing required " + name + " attribute at " + getLocation(parser));
        }
        try {
            return Long.parseLong(raw, 10);
        } catch (NumberFormatException ex) {
            throw new XMLStreamException("Can't parse integer from " + name + " attribute at " + getLocation(parser));
        }
    }

    protected String readStringAttribute(XMLStreamReader parser, String name) throws XMLStreamException {
        return readStringAttribute(parser, name, true);
    }

    protected String readStringAttribute(XMLStreamReader parser, String name, boolean required) throws XMLStreamException {
        String result = parser.getAttributeValue(NAMESPACE, name);
        if (required && result == null) {
            throw new XMLStreamException("Missing required " + name + " attribute at " + getLocation(parser));
        }
        return result;
    }

    protected String readTag(XMLStreamReader parser, String tagName) throws XMLStreamException {
        parser.require(XMLStreamReader.START_ELEMENT, NAMESPACE, tagName);
        StringBuilder result = new StringBuilder();
        while (parser.next() == XMLStreamReader.CHARACTERS) {
            result.append(parser.getText());
        }
        parser.require(XMLStreamReader.END_ELEMENT, NAMESPACE, tagName);
        return result.toString();
    }

    protected int readIntTag(XMLStreamReader parser, String tagName) throws IOException, XMLStreamException {
        String raw = readTag(parser, tagName);
        try {
            return Integer.parseInt(raw, 10);
        } catch (NumberFormatException ex) {
            throw new XMLStreamException("Can't parse integer from " + tagName + " tag at " + getLocation(parser));
        }
    }

    protected long readLongTag(XMLStreamReader parser, String tagName) throws IOException, XMLStreamException {
        String raw = readTag(parser, tagName);
        try {
            return Long.parseLong(raw, 10);
        } catch (NumberFormatException ex) {
            throw new XMLStreamException("Can't parse long from " + tagName + " tag at " + getLocation(parser));
        }
    }

    protected float readFloatTag(XMLStreamReader parser, String tagName) throws IOException, XMLStreamException {
        String raw = readTag(parser, tagName);
        try {
            return Float.parseFloat(raw);
        } catch (NumberFormatException ex) {
            throw new XMLStreamException("Can't parse float from " + tagName + " tag at " + getLocation(parser));
        }
    }

    protected void skipTag(XMLStreamReader parser) throws XMLStreamException {
        if (parser.getEventType() != XMLStreamReader.START_ELEMENT) {
            throw new XMLStreamException("Trying to skip when not at the start of an element at " + getLocation(parser));
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XMLStreamReader.END_ELEMENT:
                    depth--;
                    break;
                case XMLStreamReader.START_ELEMENT:
                    depth++;
                    break;
            }
        }
    }

    public String getLocation(XMLStreamReader parser) {

        Location where = parser.getLocation();
        return "Line " +
                where.getLineNumber() +
                " Char " +
                where.getColumnNumber();
    }

}
