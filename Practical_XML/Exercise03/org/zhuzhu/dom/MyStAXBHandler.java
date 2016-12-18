/*
 * Copyright (c) 2016, Chenfeng Zhu. All rights reserved.
 * 
 */
package org.zhuzhu.dom;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * Output the names of all countries into a file.
 * 
 * @author Chenfeng Zhu
 *
 */
public class MyStAXBHandler implements Runnable {

    private FileInputStream inputStream;
    private OutputStream outputStream;

    private List<MyStAXCountry> listCountries = new ArrayList<MyStAXCountry>(0);
    private List<String> listOrg = new ArrayList<String>(0);

    private boolean pausing = false;

    public MyStAXBHandler(FileInputStream input, OutputStream output) {
        this.inputStream = input;
        this.outputStream = output;
    }

    public void parse() {
        if (inputStream == null || outputStream == null) {
            this.log("Error: please specify the input and output.");
            return;
        }
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLStreamReader reader = inputFactory.createXMLStreamReader(inputStream);
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(outputStream);

            listCountries = new ArrayList<MyStAXCountry>(0);
            MyStAXCountry country = new MyStAXCountry();
            boolean isCountry = false;
            // int posName = 0;
            String orgid = null;
            String orgname = null;
            String members = null;
            String headq = null;
            String headqname = null;
            boolean isOrg = false;
            boolean isHeadqCap = false;
            boolean isPrint = false;
            boolean isCapital = false;
            boolean isCity = false;

            if (reader.getEventType() == XMLStreamConstants.START_DOCUMENT) {
                this.log("Start Document");
                // writer.writeStartDocument("utf-8", "1.0");
                writer.writeStartElement("result");
            }

            while (reader.hasNext()) {
                String localname = null;
                int event = reader.next();
                switch (event) {
                case XMLStreamConstants.END_DOCUMENT:
                    writer.writeEndElement(); // result
                    reader.close();
                    writer.flush();
                    writer.close();
                    this.log("End Document: " + listOrg.size());
                    break;
                case XMLStreamConstants.START_ELEMENT:
                    localname = reader.getLocalName();
                    if ("country".equals(localname)) {
                        isCountry = true;
                        if (country == null) {
                            country = new MyStAXCountry();
                        }
                        country.setCapitalID(reader.getAttributeValue(null, "capital"));
                        country.setCarcode(reader.getAttributeValue(null, "car_code"));
                    } else if (("city".equals(localname)) && isCountry) {
                        isCity = true;
                        String cityID = reader.getAttributeValue(null, "id");
                        if (cityID != null && cityID.equals(country.getCapitalID())) {
                            isCapital = true;
                        } else {
                            isCapital = false;
                        }
                    } else if ("organization".equals(localname)) {
                        orgid = reader.getAttributeValue(null, "id");
                        headq = reader.getAttributeValue(null, "headq");
                        headqname = getCapitalName(headq, listCountries);
                        if (headqname != null) {
                            isHeadqCap = true;
                        }
                        if (!isHeadqCap) {
                            isPrint = false;
                        }
                        isOrg = true;
                    }
                    if (isCountry && isCity && ("name".equals(localname))) {
                        if (isCapital) {
                            country.setCapitalName(reader.getElementText());
                        }
                    }
                    if (isHeadqCap) { // only if the headquarter is a capital
                        if (isOrg && "name".equals(reader.getLocalName())) {
                            orgname = reader.getElementText();
                        } else if (isOrg && "members".equals(reader.getLocalName())) {
                            if (!isPrint) {
                                members = reader.getAttributeValue(null, "country");
                                isPrint = isPrint(headq, members, listCountries);
                            }
                        }
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if ("country".equals(reader.getLocalName())) {
                        if (country.getCapitalID() != null) {
                            listCountries.add(country);
                        }
                        country = new MyStAXCountry();
                        isCountry = false;
                        // posName = 0;
                    } else if ("organization".equals(reader.getLocalName())) {
                        if (isPrint) {
                            listOrg.add(orgid + "|" + orgname + "|" + headqname);
                            writer.writeEmptyElement("organization");
                            writer.writeAttribute("name", orgname);
                            writer.writeEmptyElement("city");
                            writer.writeAttribute("name", headqname);
                            if (pausing) {
                                this.log(orgid + "|" + orgname + "|" + headqname);
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException ie) {
                                    ie.printStackTrace();
                                }
                            }
                        }
                        isOrg = false;
                        isHeadqCap = false;
                        isPrint = false;
                        orgid = null;
                        orgname = null;
                        headq = null;
                        headqname = null;
                        members = null;
                    } else if ("city".equals(localname)) {
                        isCity = false;
                        isCapital = false;
                    }
                    break;
                }
            }
            reader.close();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    public void printListOrg() {
        String cw1 = "%1$-15s";
        String cw2 = "%1$-45s";
        String cw3 = "%1$-25s";
        String header = String.format(cw1, " Org ID") + "|" + String.format(cw2, " Org Name") + "|"
                + String.format(cw3, " Headquarter Name");
        System.out.println(header);
        for (String text : listOrg) {
            String[] col = text.split("\\|"); // .split("[|]")
            // split uses regular expression and in regex | is a metacharacter representing the OR operator.
            System.out.println(String.format(cw1, col[0]) + "|" + String.format(cw2, col[1]) + "|" + String.format(cw3, col[2]));
        }
        System.out.println(listOrg.size());
    }

    @Override
    public void run() {
        this.log("Start writing...");
        this.pausing = true;
        this.parse();
        this.log("Finish writing.");
    }

    private String getCapitalName(String headq, List<MyStAXCountry> list) {
        if (headq == null) {
            return null;
        }
        for (MyStAXCountry country : list) {
            if (country.getCapitalID() == null || country.getCapitalName() == null) {
                continue;
            }
            if (headq.equals(country.getCapitalID())) {
                return country.getCapitalName();
            }
        }
        return null;
    }

    private boolean isPrint(String headq, String members, List<MyStAXCountry> list) {
        if (headq == null) {
            return false;
        }
        List<String> listMember = Arrays.asList(members.split(" "));
        for (MyStAXCountry country : list) {
            if (country.getCapitalID() == null || country.getCarcode() == null) {
                continue;
            }
            if (listMember.contains(country.getCarcode())) { // if this country is one member
                if (headq.equals(country.getCapitalID())) { // if this country's capital is headquarter
                    return true;
                }
            }
        }
        return false;
    }

    private void log(String text) {
        System.out.println("MyStAXBHandler-Writer: " + text);
    }

}
