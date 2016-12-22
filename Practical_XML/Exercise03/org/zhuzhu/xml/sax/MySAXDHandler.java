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
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Output the name and population for every city in each country within its table.
 * 
 * @author Chenfeng Zhu
 *
 */
public class MySAXDHandler extends DefaultHandler {

    private int count = 0;
    private String content;
    private boolean isCountry = false;
    private String capID;
    private String cityID;
    private boolean isCapital = false;
    private boolean isCity = false;
    private int posName = 0;
    private int posPopulation = 0;
    private String country;
    private String city;
    private String population;
    private List<String> list = new ArrayList<String>(0);

    private OutputStreamWriter writer;
    private OutputStreamWriter printer;

    private String lineEnd = System.getProperty("line.separator");
    private boolean useNew = true;

    public MySAXDHandler(String output) {
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
        print("Start Document");
        this.write("<div>");
        this.write("<ul>");
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
            isCity = true;
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
            write("    <li><h2>" + country + "</h2>");
            write("<div><table border='1'>");
            write("    <tr><th>City</th><th>Population</th></tr>");
            for (String str : list) {
                write(str);
            }
            write("</table></div>");
            write("</li>");
            isCountry = false;
            isCity = false;
            isCapital = false;
            list = new ArrayList<String>(0);
            country = null;
            city = null;
            population = null;
            posName = 0;
            posPopulation = 0;
        } else if ("city".equalsIgnoreCase(elementName)) {
            list.add("    <tr><td>" + city + "</td><td>" + population + "</td></tr>");
            isCity = false;
            isCapital = false;
            posPopulation = 0;
        }
        if (isCountry && ("name".equalsIgnoreCase(elementName)) && posName == 0) {
            country = content;
            posName++;
        }
        if (isCountry && isCity && ("name".equalsIgnoreCase(elementName))) {
            city = content;
            if (isCapital) {
                city += "*";
            }
        }
        if (isCountry && isCity && ("population".equalsIgnoreCase(elementName))) {
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
        this.write("</ul>");
        this.write("</div>");
        try {
            writer.flush();
            printer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
