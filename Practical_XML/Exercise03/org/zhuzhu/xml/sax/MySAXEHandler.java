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
 * Output the name and population for every city in each country (with at least 10 valid city population) within its table.
 * 
 * @author Chenfeng Zhu
 *
 */
public class MySAXEHandler extends DefaultHandler {

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
    private List<String> listCity = new ArrayList<String>(0);
    private List<Integer> listPopulation = new ArrayList<Integer>(0);
    private int countPopulation = 0;

    private OutputStreamWriter writer;
    private OutputStreamWriter printer;

    private String lineEnd = System.getProperty("line.separator");
    private boolean useNew = true;

    public MySAXEHandler(String output) {
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
            if (countPopulation >= 10) {
                write("    <li><h2>" + country + "</h2>");
                write("    <p>City number: " + listCity.size() + "</p>");
                int sum = 0;
                for (int i : listPopulation) {
                    sum += i;
                }
                int avg = sum / listPopulation.size();
                write("    <p>Average city population: " + avg + "</p>");
                int pos = 0;
                int temp = Math.abs(listPopulation.get(0) - avg);
                for (int i = 1; i < listPopulation.size(); i++) {
                    int diff = Math.abs(listPopulation.get(i) - avg);
                    if (diff < temp) {
                        temp = diff;
                        pos = i;
                    }
                }
                write("    <div><table border='1'>");
                write("        <tr><th>City</th><th>Population</th></tr>");
                if (listCity.size() != listPopulation.size()) {
                    System.out.println(listCity);
                    System.out.println(listPopulation);
                    print(listCity.size() + ",");
                    print(listPopulation.size() + ",");
                }
                for (int i = 0; i < listPopulation.size(); i++) {
                    String c = listCity.get(i);
                    int p = listPopulation.get(i);
                    if (i == pos) {
                        write("        <tr><td>" + c + "</td><td><font color='blue'>" + p + "~</font></td></tr>");
                    } else {
                        write("        <tr><td>" + c + "</td><td>" + p + "</td></tr>");
                    }
                }
                write("    </table></div>");
                write("    </li>");
            }
            isCountry = false;
            isCity = false;
            isCapital = false;
            listCity = new ArrayList<String>(0);
            listPopulation = new ArrayList<Integer>(0);
            country = null;
            city = null;
            population = null;
            posName = 0;
            posPopulation = 0;
            countPopulation = 0;
        } else if ("city".equalsIgnoreCase(elementName)) {
            listCity.add(city);
            if (population != null && !("".equalsIgnoreCase(population))) {
                countPopulation++;
            } else {
                population = "0";
            }
            listPopulation.add(Integer.parseInt(population));
            isCity = false;
            isCapital = false;
            posPopulation = 0;
            city = null;
            population = null;
        }
        if (isCountry && ("name".equalsIgnoreCase(elementName)) && posName == 0) {
            country = content;
            posName++;
        }
        if (isCountry && isCity && ("name".equalsIgnoreCase(elementName))) {
            city = content;
            if (isCapital) {
                city = "<font color='red'>" + city + "*</font>";
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
