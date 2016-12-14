/*
 * Copyright (c) 2016, Chenfeng Zhu. All rights reserved.
 * 
 */
package org.zhuzhu.dom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Output the names of all countries.
 * 
 * @author Chenfeng Zhu
 *
 */
public class MySAXAHandler extends DefaultHandler {

    private int count = 0;
    private List<String> countryNames = new ArrayList<String>(0);
    private String content;
    private boolean isCountry = false;
    private int posName = 0;
    private OutputStreamWriter writer;

    private String lineEnd = System.getProperty("line.separator");

    public MySAXAHandler() {
        super();
    }

    public MySAXAHandler(String output) {
        super();
        System.out.println("Output HTML file: " + output);
        this.initOutput(output);
    }

    private void initOutput(String output) {
        if (!(new File(output)).exists()) {
            try {
                (new File(output)).createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            OutputStream os = new FileOutputStream(output);
            writer = new OutputStreamWriter(os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void writeIntoFile(String text) {
        try {
            writer.write(text + lineEnd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getAllCountriesName() {
        return this.countryNames;
    }

    public void startDocument() throws SAXException {
        System.out.println("Start Document");
        this.writeIntoFile("<table border='1'>");
        this.writeIntoFile("    <tr><th>Name</th></tr>");
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        String elementName;
        if (qName == null || qName.equals("")) {
            elementName = localName;
        } else {
            elementName = qName;
        }
        if ("country".equalsIgnoreCase(elementName)) {
            this.count++;
            isCountry = true;
            // System.out.println("Count: " + count);
            // System.out.println("element: " + elementName);
        }
    }

    public void characters(char ch[], int start, int length) throws SAXException {
        content = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        // if (!isCountry) {
        // return;
        // }
        String elementName;
        if (qName == null || qName.equals("")) {
            elementName = localName;
        } else {
            elementName = qName;
        }
        if ("country".equals(elementName)) {
            isCountry = false;
            posName = 0;
            // System.out.println("Element End: " + elementName + ", " + content);
        }
        if (isCountry && ("name".equalsIgnoreCase(elementName)) && posName == 0) {
            countryNames.add(content);
            this.writeIntoFile("    <tr><td>" + content + "</td></tr>");
            posName++;
        }
    }

    public void endDocument() throws SAXException {
        System.out.println("End Document: " + this.count);
        this.writeIntoFile("</table>");
        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
