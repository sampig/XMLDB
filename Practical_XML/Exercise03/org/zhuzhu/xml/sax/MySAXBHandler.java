/*
 * Copyright (c) 2016, Chenfeng Zhu. All rights reserved.
 * 
 */
package org.zhuzhu.xml.sax;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Output the population of the capital of Germany.
 * 
 * @author Chenfeng Zhu
 *
 */
public class MySAXBHandler extends DefaultHandler {

    private String lineEnd = System.getProperty("line.separator");

    private String content;
    private boolean isCountry = false;
    private boolean isGermany = false;
    private String capID;
    private String cityID;
    // private String capName;
    private String population;
    private boolean isCapital = false;
    private int posName = 0;
    private OutputStreamWriter writer;

    public MySAXBHandler() {
        super();
        initOutput();
    }

    private void initOutput() {
        writer = new OutputStreamWriter(System.out);
    }

    private void print(String text) {
        try {
            writer.write(text + lineEnd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startDocument() throws SAXException {
        System.out.println("Start Document");
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        String elementName;
        if (qName == null || qName.equals("")) {
            elementName = localName;
        } else {
            elementName = qName;
        }
        if ("country".equalsIgnoreCase(elementName)) {
            isCountry = true;
            for (int i = 0; i < attributes.getLength(); i++) {
                if ("capital".equalsIgnoreCase(attributes.getQName(i))) {
                    capID = attributes.getValue(i);
                    break;
                }
            }
        } else if (("city".equalsIgnoreCase(elementName)) && isGermany) {
            for (int i = 0; i < attributes.getLength(); i++) {
                if ("id".equalsIgnoreCase(attributes.getQName(i))) {
                    cityID = attributes.getValue(i);
                    break;
                }
            }
            if (capID != null && capID.equalsIgnoreCase(cityID)) {
                isCapital = true;
            } else {
                isCapital = false;
            }
        }
    }

    public void characters(char ch[], int start, int length) throws SAXException {
        content = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        String elementName;
        if (qName == null || qName.equals("")) {
            elementName = localName;
        } else {
            elementName = qName;
        }
        if ("country".equalsIgnoreCase(elementName)) {
            isCountry = false;
            isGermany = false;
            isCapital = false;
            posName = 0;
        } else if ("city".equalsIgnoreCase(elementName)) {
            isCapital = false;
        }
        if (isCountry && ("name".equalsIgnoreCase(elementName)) && posName == 0) {
            if ("Germany".equalsIgnoreCase(content)) {
                isGermany = true;
                this.print("Country: " + content);
                this.print("Capital ID: " + capID);
            }
            posName++;
        }
        if (isCountry && isGermany && isCapital && ("name".equalsIgnoreCase(elementName))) {
            this.print("Capital Name: " + content);
        }
        if (isCountry && isGermany && isCapital && ("population".equalsIgnoreCase(elementName))) {
            // always reset to get the newest one.
            population = content;
        }
    }

    public void endDocument() throws SAXException {
        this.print("Capital Population: " + population);
        System.out.println("End Document.");
        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
