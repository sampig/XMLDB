/*
 * Copyright (c) 2016, Chenfeng Zhu. All rights reserved.
 * 
 */
package org.zhuzhu.dom;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * Output the countries with at least 10 valid city population into a file.
 * 
 * @author Chenfeng Zhu
 *
 */
public class MyStAXAEHandler {

    private boolean useNew = true;

    private FileInputStream inputStream;
    private OutputStream outputStream;

    private List<MyStAXCountry> listCountries = new ArrayList<MyStAXCountry>(0);

    public MyStAXAEHandler(FileInputStream input, OutputStream output) {
        this.inputStream = input;
        this.outputStream = output;
    }

    public void init(String input, String output) {
        try {
            this.inputStream = new FileInputStream(input);
            this.outputStream = new FileOutputStream(output);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void parse() {
        if (inputStream == null || outputStream == null) {
            System.out.println("Error: please specify the input and output.");
            return;
        }
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLStreamReader reader = inputFactory.createXMLStreamReader(inputStream);
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(outputStream);
            // new IndentingXMLStreamWriter(outputFactory.createXMLStreamWriter(outputStream));
            // writer.setIndentStep(" ");

            listCountries = new ArrayList<MyStAXCountry>(0);
            boolean isCountry = false;
            int posName = 0;
            int posPopulation = 0;
            MyStAXCountry country = new MyStAXCountry();
            String capID = null;
            String cityID = null;
            boolean isCapital = false;
            boolean isCity = false;
            String cityname = null;
            String population = null;

            if (reader.getEventType() == XMLStreamConstants.START_DOCUMENT) {
                System.out.println("Start Document");
                writer.writeStartDocument("utf-8", "1.0");
                writer.writeStartElement("div");
                writer.writeStartElement("ul");
            }

            while (reader.hasNext()) {
                int event = reader.next();
                String localname = null;
                switch (event) {
                case XMLStreamConstants.END_DOCUMENT:
                    System.out.println("End Document: " + listCountries.size());
                    writer.writeEndElement(); // ul
                    writer.writeEndElement(); // div
                    listCountries = new ArrayList<MyStAXCountry>(0);
                    reader.close();
                    writer.flush();
                    writer.close();
                    break;
                case XMLStreamConstants.START_ELEMENT:
                    localname = reader.getLocalName();
                    if ("country".equals(localname)) {
                        isCountry = true;
                        if (country == null) {
                            country = new MyStAXCountry();
                        }
                        capID = reader.getAttributeValue(null, "capital");
                    } else if (("city".equals(localname)) && isCountry) {
                        isCity = true;
                        cityID = reader.getAttributeValue(null, "id");
                        if (capID != null && capID.equals(cityID)) {
                            isCapital = true;
                        } else {
                            isCapital = false;
                        }
                    }
                    if (isCountry && !isCity && ("name".equals(localname)) && posName == 0) {
                        country.setName(reader.getElementText());
                        posName++;
                    }
                    if (isCountry && isCity && ("name".equals(localname))) {
                        cityname = reader.getElementText();
                        if (isCapital) {
                            country.setCapitalName(cityname);
                        }
                    }
                    if (isCountry && isCity && ("population".equals(localname))) {
                        if (useNew) {
                            population = reader.getElementText();
                        } else if (posPopulation == 0) {
                            population = reader.getElementText();
                            posPopulation++;
                        }
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    localname = reader.getLocalName();
                    if ("country".equals(localname)) {
                        if (country.withEnoughValid()) {
                            listCountries.add(country);
                            writer.writeStartElement("li");
                            // country name
                            writer.writeStartElement("h2");
                            writer.writeCharacters(country.getName());
                            writer.writeEndElement(); // h2
                            // city number
                            writer.writeStartElement("p");
                            writer.writeCharacters("City Number: " + country.getListCity().size());
                            writer.writeEndElement(); // p
                            // Average
                            writer.writeStartElement("p");
                            writer.writeCharacters("Average city population: " + country.getPopulationAvg());
                            writer.writeEndElement(); // p
                            // city table
                            writer.writeStartElement("div");
                            writer.writeStartElement("table");
                            writer.writeAttribute("border", "1");
                            writer.writeStartElement("tr");
                            writer.writeStartElement("th");
                            writer.writeCharacters("city");
                            writer.writeEndElement(); // th:city
                            writer.writeStartElement("th");
                            writer.writeCharacters("population");
                            writer.writeEndElement(); // th:population
                            writer.writeEndElement(); // tr:header
                            int capindex = country.getCapitalIndex();
                            int avgindex = country.getCityClosestIndex();
                            for (int i = 0; i < country.getListCity().size(); i++) {
                                String c = country.getListCity().get(i);
                                int p = country.getListPopulation().get(i);
                                writer.writeStartElement("tr");
                                writer.writeStartElement("td");
                                if (i == capindex) {
                                    writer.writeStartElement("font");
                                    writer.writeAttribute("color", "red");
                                    writer.writeCharacters(c + "*");
                                    writer.writeEndElement();
                                } else {
                                    writer.writeCharacters(c);
                                }
                                writer.writeEndElement(); // td:city
                                writer.writeStartElement("td");
                                if (i == avgindex) {
                                    writer.writeStartElement("font");
                                    writer.writeAttribute("color", "blue");
                                    writer.writeCharacters(p + "~");
                                    writer.writeEndElement();
                                } else {
                                    writer.writeCharacters(String.valueOf(p));
                                }
                                writer.writeEndElement(); // td:population
                                writer.writeEndElement(); // tr:header
                            }
                            writer.writeEndElement(); // table
                            writer.writeEndElement(); // div
                            writer.writeEndElement(); // li
                        }
                        isCountry = false;
                        posName = 0;
                        country = new MyStAXCountry();
                    } else if ("city".equals(localname)) {
                        country.getListCity().add(cityname);
                        if (population == null || ("".equalsIgnoreCase(population))) {
                            population = "0";
                        }
                        country.getListPopulation().add(Integer.parseInt(population));
                        isCity = false;
                        isCapital = false;
                        posPopulation = 0;
                        cityname = null;
                        population = null;
                    }
                    break;
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

}
