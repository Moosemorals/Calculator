/*
 * The MIT License
 *
 * Copyright 2015 Osric Wilkinson <osric@fluffypeople.com>.
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

import com.moosemorals.calculator.Button;
import static com.moosemorals.calculator.xml.BaseParser.NAMESPACE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Osric Wilkinson <osric@fluffypeople.com>
 */
public class ButtonsParser extends BaseParser<List<Button>> {

    private final Logger log = LoggerFactory.getLogger(ButtonsParser.class);

    @Override
    public List<Button> parse(XMLStreamReader parser) throws XMLStreamException, IOException {
        parser.require(XMLStreamReader.START_ELEMENT, NAMESPACE, "buttons");

        List<Button> buttons = new ArrayList<>();

        while (parser.next() != XMLStreamReader.END_ELEMENT) {
            if (parser.getEventType() != XMLStreamReader.START_ELEMENT) {
                continue;
            }

            switch (parser.getLocalName()) {
                case "button":
                    buttons.add(parseButton(parser));
                    break;
                default:
                    log.error("Unexpected tag {} at {}, skiping", parser.getLocalName(), getLocation(parser));
                    skipTag(parser);
                    break;
            }
        }
        return buttons;
    }

    private Button parseButton(XMLStreamReader parser) throws XMLStreamException, IOException {
        parser.require(XMLStreamReader.START_ELEMENT, NAMESPACE, "button");

        Button.Builder builder = new Button.Builder();

        builder.setX(readIntAttribute(parser, "x"));
        builder.setY(readIntAttribute(parser, "y"));

        String raw = parser.getAttributeValue(NAMESPACE, "width");
        if (raw != null && !raw.isEmpty()) {
            try {
                builder.setWidth(Integer.parseInt(raw, 10));
            } catch (NumberFormatException ex) {
                throw new XMLStreamException("Can't parse width [" + raw + "] at " + getLocation(parser) , ex);
            }
        }

        raw = parser.getAttributeValue(NAMESPACE, "height");
        if (raw != null && !raw.isEmpty()) {
            try {
                builder.setHeight(Integer.parseInt(raw, 10));
            } catch (NumberFormatException ex) {
                throw new XMLStreamException("Can't parse height [" + raw + "] at " + getLocation(parser) , ex);
            }
        }

        while (parser.next() != XMLStreamReader.END_ELEMENT) {
            if (parser.getEventType() != XMLStreamReader.START_ELEMENT) {
                continue;
            }

            switch (parser.getLocalName()) {
                case "in":
                    builder.setIn(readIntTag(parser, "in"));
                    break;
                case "out":
                    builder.setOut(readIntTag(parser, "out"));
                    break;
                case "label":
                    builder.setLabel(readTag(parser, "label"));
                    break;
                case "name":
                    builder.setName(readTag(parser, "name"));
                    break;
                case "script":
                    builder.setCode(readTag(parser, "script"));
                    break;
                case "key":
                    raw = readTag(parser, "key");
                    if (raw != null && !raw.isEmpty()) {
                        switch (raw) {
                            case "\\n":
                                builder.setKey('\n');
                                break;
                            case "\\t":
                                builder.setKey('\t');
                                break;
                            default:
                                builder.setKey(raw.charAt(0));
                        }
                    } else {
                        builder.setKey((char) 0);
                    }
                    break;
                default:
                    log.error("Unexpected tag {} at {}, skiping", parser.getLocalName(), getLocation(parser));
                    skipTag(parser);
                    break;
            }
        }

        return builder.build();
    }

}
