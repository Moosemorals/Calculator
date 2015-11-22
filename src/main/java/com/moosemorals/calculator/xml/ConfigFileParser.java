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

import com.moosemorals.calculator.Config;
import static com.moosemorals.calculator.xml.BaseParser.NAMESPACE;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Osric Wilkinson <osric@fluffypeople.com>
 */
public class ConfigFileParser extends BaseParser<Config> {

    private final Logger log = LoggerFactory.getLogger(ConfigFileParser.class);

    @Override
    public Config parse(XMLStreamReader parser) throws XMLStreamException, IOException {
        parser.require(XMLStreamReader.START_ELEMENT, NAMESPACE, "calculator");

        Config.Builder builder = new Config.Builder();

        while (parser.next() != XMLStreamReader.END_ELEMENT) {
            if (parser.getEventType() != XMLStreamReader.START_ELEMENT) {
                continue;
            }

            switch (parser.getLocalName()) {
                case "buttons":
                    log.debug("Parsing buttons");
                    builder.addButtons(new ButtonsParser().parse(parser));
                    break;
                case "config":
                    parseConfig(parser, builder);
                    break;
                default:
                    log.error("Unexpected tag {} at {}, skiping", parser.getLocalName(), getLocation(parser));
                    skipTag(parser);
                    break;
            }
        }

        return builder.build();
    }

    protected void parseConfig(XMLStreamReader parser, Config.Builder builder) throws XMLStreamException, IOException {
        parser.require(XMLStreamReader.START_ELEMENT, NAMESPACE, "config");
        while (parser.next() != XMLStreamReader.END_ELEMENT) {
            if (parser.getEventType() != XMLStreamReader.START_ELEMENT) {
                continue;
            }

            switch (parser.getLocalName()) {
                case "cols":
                    builder.setCols(readIntTag(parser, "cols"));
                    break;
                case "size":
                    builder.setSize(readIntTag(parser, "size"));
                    break;
                default:
                    log.error("Unexpected tag {} at {}, skiping", parser.getLocalName(), getLocation(parser));
                    skipTag(parser);
                    break;
            }
        }

    }

}
