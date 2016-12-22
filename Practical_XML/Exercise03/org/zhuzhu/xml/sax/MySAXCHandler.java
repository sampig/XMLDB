/*
 * Copyright (c) 2016, Chenfeng Zhu. All rights reserved.
 * 
 */
package org.zhuzhu.xml.sax;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Output the names of all countries and their capital and capital population.
 * <ol>
 * <li>With System.out.</li>
 * <li>Into a file.</li>
 * </ol>
 * 
 * @author Chenfeng Zhu
 *
 */
public class MySAXCHandler extends DefaultHandler {

    private int count = 0;
    private String content;
    private boolean isCountry = false;
    private String capID;
    private String cityID;
    private boolean isCapital = false;
    private int posName = 0;
    private int posPopulation = 0;
    private String country;
    private String capital;
    private String population;

    private OutputStreamWriter writer;
    private OutputStreamWriter printer;

    private String lineEnd = System.getProperty("line.separator");
    private boolean useNew = false;
    private final static String COLW1 = "%1$-35s";
    private final static String COLW2 = "%1$-25s";
    private final static String COLW3 = "%1$-20s";

    public MySAXCHandler(String output) {
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
            printer = new OutputStreamWriter(System.out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void print(String text) {
        try {
            printer.write(text + lineEnd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(String text) {
        try {
            writer.write(text + lineEnd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUseNew(boolean useNew) {
        this.useNew = useNew;
    }

    public void startDocument() throws SAXException {
        System.out.println("Start Document");
        this.print(String.format(COLW1, " Country") + "|" + String.format(COLW2, " Capital") + "|"
                + String.format(COLW3, " Capital Population"));
        this.print(String.format("%1$-82s", "").replace(" ", "="));
        this.write("<table border='1'>");
        this.write("    <tr>");
        this.write("        <th>Country</th>");
        this.write("        <th>Capital</th>");
        this.write("        <th>Capital Population</th>");
        this.write("    </tr>");
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
            for (int i = 0; i < attributes.getLength(); i++) {
                if ("capital".equalsIgnoreCase(attributes.getQName(i))) {
                    capID = attributes.getValue(i);
                    break;
                }
            }
        } else if (("city".equalsIgnoreCase(elementName)) && isCountry) {
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
        if ("country".equals(elementName)) {
            print(String.format(COLW1, country) + "|" + String.format(COLW2, capital) + "|" + String.format(COLW3, population));
            write("    <tr><td>" + country + "</td><td>" + capital + "</td><td>" + population + "</td></tr>");
            isCountry = false;
            isCapital = false;
            country = null;
            capital = null;
            population = null;
            posName = 0;
            posPopulation = 0;
        } else if ("city".equalsIgnoreCase(elementName)) {
            isCapital = false;
            posPopulation = 0;
        }
        if (isCountry && ("name".equalsIgnoreCase(elementName)) && posName == 0) {
            country = content;
            posName++;
        }
        if (isCountry && isCapital && ("name".equalsIgnoreCase(elementName))) {
            capital = content;
        }
        if (isCountry && isCapital && ("population".equalsIgnoreCase(elementName))) {
            if (useNew) {
                population = content;
            } else if (posPopulation == 0) {
                population = content;
                posPopulation++;
            }
        }
    }

    public void endDocument() throws SAXException {
        this.print("End Document: " + this.count);
        this.write("</table>");
        try {
            writer.flush();
            printer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
