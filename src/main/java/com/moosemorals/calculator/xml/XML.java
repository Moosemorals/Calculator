/*
 * The MIT License
 *
 * Copyright 2017 Osric Wilkinson (osric@fluffypeople.com).
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

import java.io.Writer;
import java.util.List;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Osric Wilkinson (osric@fluffypeople.com)
 */
public class XML implements AutoCloseable {

    private final Logger log = LoggerFactory.getLogger(XML.class);
    private final XMLStreamWriter xml;

    public XML(Writer out) throws XMLStreamException {
        xml = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
        xml.writeStartDocument();
    }

    private void writeAttr(String... attr) throws XMLStreamException {
        if (attr != null) {
            if (attr.length % 2 != 0) {
                throw new IllegalArgumentException("Attributes must come in pairs");
            }
            for (int i = 0; i < attr.length; i += 2) {
                xml.writeAttribute(attr[i], attr[i + 1]);
            }
        }
    }

    public XML start(String name, String... attr) throws XMLStreamException {
        xml.writeStartElement(name);
        writeAttr(attr);
        return this;
    }

    public XML add(String name, String content, String... attr) throws XMLStreamException {
        if (content != null) {
            xml.writeStartElement(name);
        } else {
            xml.writeEmptyElement(name);
        }
        writeAttr(attr);
        if (content != null) {
            xml.writeCharacters(content);
            xml.writeEndElement();
        }
        return this;
    }

    public XML add(String name, String content) throws XMLStreamException {
        return add(name, content, (String[])null);
    }
    
    public XML add(String name) throws XMLStreamException {        
        return add(name, null, (String[])null);        
    }
    
    public XML add(XMLable other) throws XMLStreamException {
        other.toXML(this);
        return this;
    }
    
    public XML add(String name, int value) throws XMLStreamException {
        return add(name, String.format("%d", value), (String[])null);            
    }

    public XML add(String name, List<? extends XMLable> l) throws XMLStreamException {
        if (!l.isEmpty()) {
            xml.writeStartElement(name);
            for (XMLable a : l) {
                a.toXML(this);
            }
            xml.writeEndElement();
        }
        return this;
    }

    public XML end() throws XMLStreamException {
        xml.writeEndElement();
        return this;
    }

    public XML endDocument() throws XMLStreamException {
        xml.writeEndDocument();
        return this;
    }

    @Override
    public void close() throws XMLStreamException {
        xml.flush();
        xml.close();
    }
}
