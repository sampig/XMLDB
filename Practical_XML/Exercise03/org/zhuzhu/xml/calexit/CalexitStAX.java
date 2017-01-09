/*
 * Copyright (c) 2016, Chenfeng Zhu. All rights reserved.
 * 
 */
package org.zhuzhu.xml.calexit;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * Practical XML Exercise 03.<br/>
 * Calexit with StAX.
 * 
 * @author Chenfeng Zhu
 *
 */
public class CalexitStAX {

    // User-defined file path
    private String sourcePath = "/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/lib/mondial.xml";
    private String ouptutPath = "/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/ex03/mondial_new_sax.xml";
    private String newinfoPath = "/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/ex03/calexit_new.xml";

    // User-defined static variables
    private final static String DTD = "<!DOCTYPE mondial SYSTEM \"mondial.dtd\">";
    private String provid = "prov-United-States-6";
    private List<String> listOrgException = Arrays.asList("org-G-5", "org-G-7");
    private String goverment = "CA federal republic";
    private List<String> listNaturename = Arrays.asList("sea", "river", "lake", "island", "mountain", "desert");
    private List<String> listNaturenameSub = Arrays.asList("source", "estuary");

    private InputStream inputStream;
    private OutputStream outputStream;
    private InputStream newinforStream;
    private ByteArrayOutputStream outputNewCountry;
    private String newline = "\n";

    // global information
    private String carcodeNew = "carcodeNew";
    private String carcodeOld = "carcodeOld";
    private String countrynameNew = "countrynameNew";
    private String countrynameOrigi = "countrynameOrigi";
    private String[] contentNewinfor = new String[2];

    private String capitalNew;
    private String membershipsNew;
    private List<String> listMembersNew = new ArrayList<String>(0);

    private Double dAreaProvince;
    private Map<String, String> mapBorder = new HashMap<String, String>(0);
    private Map<String, Long> mapPopProvince = new HashMap<String, Long>(0);
    private Long lPopProvince = 0l;
    private Long lPopCountry = 0l;
    private Map<String, Double> mapPoprateProvince = new HashMap<String, Double>(0);

    private Map<String, Double> mapGdpProvince = new HashMap<String, Double>(0);
    private Double dGdpProvince = 0d;
    private Double dGdpCountry = 0d;

    private List<String> listCityoldidProvince = new ArrayList<String>(0);

    private List<String> listNatureidSingle = new ArrayList<String>(0);
    private Map<String, List<String>> mapNatureSubidSingle = new HashMap<String, List<String>>(0);
    private List<String> listNatureidMulti = new ArrayList<String>(0);
    private Map<String, List<String>> mapNatureSubidMulti = new HashMap<String, List<String>>(0);

    public static void main(String... strings) {
        String filepath = null;
        if (strings.length >= 1) {
            filepath = strings[0];
        }
        String outputFile = null;
        if (strings.length >= 2) {
            outputFile = strings[1];
        }
        String newinforFile = null;
        if (strings.length >= 3) {
            newinforFile = strings[2];
        }

        CalexitStAX cstax = new CalexitStAX(filepath, outputFile, newinforFile);
        cstax.readNewinfor();
        cstax.readOrigicountry();
        cstax.calExit();
        cstax.validate();
    }

    public CalexitStAX(String source, String target, String newinfor) {
        if (source != null && !("".equalsIgnoreCase(source))) {
            this.sourcePath = source;
        }
        System.out.println("Source XML File: " + sourcePath);
        if (target != null && !("".equalsIgnoreCase(target))) {
            this.ouptutPath = target;
        }
        System.out.println("Target XML File: " + ouptutPath);
        if (newinfor != null && !("".equalsIgnoreCase(newinfor))) {
            this.newinfoPath = newinfor;
        }
        System.out.println("New Information XML File: " + newinfoPath);
        try {
            this.inputStream = new FileInputStream(sourcePath);
            this.outputStream = new FileOutputStream(ouptutPath);
            this.newinforStream = new FileInputStream(newinfoPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read the information of new country.
     */
    public void readNewinfor() {
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            // InputStream is = new FileInputStream(newinfoPath);
            XMLStreamReader reader = inputFactory.createXMLStreamReader(newinforStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(baos);
            while (reader.hasNext()) {
                int event = reader.next();
                String nodename = null;
                if (event == XMLStreamConstants.START_ELEMENT || event == XMLStreamConstants.END_ELEMENT) {
                    nodename = reader.getLocalName();
                }
                if (event == XMLStreamConstants.END_DOCUMENT) {
                    break;
                } else if (event == XMLStreamConstants.START_ELEMENT) {
                    if ("newinfo".equals(nodename)) {
                    } else if ("country".equals(nodename)) {
                        carcodeNew = reader.getAttributeValue(null, "car_code");
                    } else {
                        // writer.writeCharacters(newline);
                        writer.writeStartElement(nodename);
                        for (int i = 0; i < reader.getAttributeCount(); i++) {
                            writer.writeAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                        }
                        String text = reader.getElementText(); // once using this, reader will move to next and next.
                        if (nodename.contains("gdp_") || "inflation".equals(nodename)) {
                            if ("gdp_total".equals(nodename)) {
                                dGdpProvince = Double.parseDouble(text);
                            }
                            mapGdpProvince.put(nodename, Double.parseDouble(text));
                        } else if ("population_growth".equals(nodename) || "infant_mortality".equals(nodename)) {
                            mapPoprateProvince.put(nodename, Double.parseDouble(text));
                        } else if ("unemployment".equals(nodename)) {
                            mapPoprateProvince.put(nodename, Double.parseDouble(text));
                            writer.writeCharacters(text);
                            writer.writeEndElement();
                            break;
                        }
                        writer.writeCharacters(text);
                        writer.writeEndElement();
                    }
                } else if (event == XMLStreamConstants.CHARACTERS) {
                    writer.writeCharacters(reader.getText());
                } else if (event == XMLStreamConstants.END_ELEMENT) {
                    if ("unemployment".equals(nodename)) {
                        break;
                    }
                }
            }
            writer.flush();
            // System.out.println(baos.toString());
            contentNewinfor[0] = baos.toString();
            // System.out.println(baos.toString());
            baos.reset();
            while (reader.hasNext()) {
                int event = reader.next();
                String nodename = null;
                if (event == XMLStreamConstants.START_ELEMENT || event == XMLStreamConstants.END_ELEMENT) {
                    nodename = reader.getLocalName();
                }
                if (event == XMLStreamConstants.END_DOCUMENT) {
                    break;
                } else if (event == XMLStreamConstants.START_ELEMENT) {
                    writer.writeStartElement(nodename);
                    for (int i = 0; i < reader.getAttributeCount(); i++) {
                        writer.writeAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                    }
                    if ("border".equals(nodename)) {
                        mapBorder.put(reader.getAttributeValue(null, "country"), reader.getAttributeValue(null, "length"));
                    } else if ("ethnicgroup".equals(nodename) || "religion".equals(nodename) || "language".equals(nodename)) {
                        String percent = reader.getAttributeValue(null, "percentage");
                        String type = reader.getElementText();
                        mapPoprateProvince.put(type, Double.parseDouble(percent));
                        writer.writeCharacters(type);
                        writer.writeEndElement();
                    }
                } else if (event == XMLStreamConstants.CHARACTERS) {
                    writer.writeCharacters(reader.getText());
                } else if (event == XMLStreamConstants.END_ELEMENT) {
                    if ("newinfo".equals(nodename)) {
                        break;
                    } else if ("country".equals(nodename)) {
                        break;
                    } else {
                        writer.writeEndElement();
                    }
                }
            }
            writer.flush();
            contentNewinfor[1] = baos.toString();
            // System.out.println(new String(baos.toByteArray(), "UTF-8"));
        } catch (XMLStreamException e) {
            e.printStackTrace();
            // } catch (FileNotFoundException e) {
            // e.printStackTrace();
            // } catch (UnsupportedEncodingException e) {
            // e.printStackTrace();
        }
        System.out.println("\n\nNew car_code: " + carcodeNew);
    }

    /**
     * Read the information from the original country.
     * <ol>
     * <li>Read information of the target province.</li>
     * <li>Read the information of the nature which is referred to the province.</li>
     * </ol>
     */
    public void readOrigicountry() {
        boolean isTarget = false;
        boolean isCountry = false;
        boolean isProv = false;
        boolean isCity = false;
        String cc = null;
        String ctryname = null;
        String members = null;
        String provname = null;
        int posCtryname = 0;
        int posProvname = 0;
        boolean isNature = false;
        boolean isNatureSub = false;
        String natureid = null;
        String parentnode = null;
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            InputStream is = new FileInputStream(sourcePath);
            XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
            outputNewCountry = new ByteArrayOutputStream();
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(outputNewCountry);

            boolean addNewinfor = true;
            String continent = null;

            boolean flagStop = false;
            String nodename = null;

            while (reader.hasNext()) {
                int event = reader.next();
                switch (event) {
                case XMLStreamConstants.END_DOCUMENT:
                    break;
                case XMLStreamConstants.START_ELEMENT:
                    nodename = reader.getLocalName();
                    if ("country".equals(nodename)) {
                        isCountry = true;
                        cc = reader.getAttributeValue(null, "car_code");
                        members = reader.getAttributeValue(null, "memberships");
                    } else if ("airport".equals(nodename)) {
                        flagStop = true;
                    } else if ("province".equals(nodename)) {
                        isProv = true;
                        String id = reader.getAttributeValue(null, "id");
                        if (provid.equals(id)) {
                            carcodeOld = cc;
                            countrynameOrigi = ctryname.replace(" ", "-");
                            membershipsNew = members;
                            isTarget = true;
                            capitalNew = reader.getAttributeValue(null, "capital");
                            for (String str : listOrgException) {
                                membershipsNew = membershipsNew.replace(str, "");
                            }
                            while (membershipsNew.contains("  ")) {
                                membershipsNew = membershipsNew.replace("  ", " ");
                            }
                            listMembersNew = Arrays.asList(membershipsNew.split(" "));
                        }
                    } else if (isCountry && !isProv && "name".equals(nodename) && posCtryname == 0) {
                        ctryname = reader.getElementText();
                        posCtryname++;
                    } else if ("encompassed".equals(nodename)) {
                        if ("100".equals(reader.getAttributeValue(null, "percentage"))) {
                            continent = reader.getAttributeValue(null, "continent");
                        }
                    } else if (isTarget) {
                        if ("name".equals(nodename) && posProvname == 0) {
                            provname = reader.getElementText();
                            countrynameNew = provname.replace(" ", "-");
                            posProvname++;
                        } else if ("area".equals(nodename)) {
                            String area = reader.getElementText();
                            dAreaProvince = Double.parseDouble(area);
                            // only after getting the area value, start to write country element.
                            writer.writeCharacters(newline);
                            writer.writeStartElement("country");
                            writer.writeAttribute("car_code", carcodeNew);
                            writer.writeAttribute("area", area);
                            capitalNew = capitalNew.replace(countrynameOrigi, countrynameNew);
                            writer.writeAttribute("capital", capitalNew);
                            writer.writeAttribute("memberships", membershipsNew);
                            writer.writeCharacters(newline);
                            writer.writeStartElement("name");
                            writer.writeCharacters(countrynameNew);
                            writer.writeEndElement();
                        } else if ("population".equals(nodename) && !isCity) {
                            writer.writeStartElement(nodename);
                            for (int i = 0; i < reader.getAttributeCount(); i++) {
                                writer.writeAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                            }
                            String year = reader.getAttributeValue(null, "year");
                            String text = reader.getElementText();
                            mapPopProvince.put(year, Long.parseLong(text));
                            lPopProvince = Long.parseLong(text);
                            writer.writeCharacters(text);
                            writer.writeEndElement();
                        } else if ("city".equals(nodename)) {
                            isCity = true;
                            if (addNewinfor) {
                                writer.writeDTD(contentNewinfor[0]); // writeCharacters will translate <>.
                                writer.writeCharacters(newline);
                                writer.writeStartElement("indep_date");
                                Date date = new Date();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                writer.writeAttribute("from", carcodeOld);
                                writer.writeCharacters(sdf.format(date));
                                writer.writeEndElement();
                                writer.writeCharacters(newline);
                                writer.writeStartElement("government");
                                writer.writeCharacters(goverment);
                                writer.writeEndElement();
                                if (continent != null) {
                                    writer.writeCharacters(newline);
                                    writer.writeEmptyElement("encompassed");
                                    writer.writeAttribute("continent", continent);
                                    writer.writeAttribute("percentage", "100");
                                }
                                writer.writeDTD(contentNewinfor[1]);
                                // writer.writeCharacters(newline);
                                addNewinfor = false;
                            }
                            writer.writeStartElement(nodename);
                            String id = reader.getAttributeValue(null, "id");
                            listCityoldidProvince.add(id);
                            id = id.replace(countrynameOrigi, countrynameNew);
                            writer.writeAttribute("id", id);
                            writer.writeAttribute("country", carcodeNew);
                            if (capitalNew.equals(id)) {
                                writer.writeAttribute("is_country_cap", "yes");
                            }
                        } else {
                            writer.writeStartElement(nodename);
                            for (int i = 0; i < reader.getAttributeCount(); i++) {
                                writer.writeAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                            }
                        }
                    } else if (listNaturename.contains(nodename)) {
                        isNature = true;
                        natureid = reader.getAttributeValue(null, "id");
                    } else if (listNaturenameSub.contains(nodename)) {
                        isNatureSub = true;
                        parentnode = nodename;
                    } else if (isNature) {
                        if ("located".equals(nodename)) {
                            String prov = reader.getAttributeValue(null, "province");
                            if (provid.equals(prov)) {
                                if (isNatureSub) {
                                    if (mapNatureSubidSingle.containsKey(natureid)) {
                                        mapNatureSubidSingle.get(natureid).add(parentnode);
                                    } else {
                                        mapNatureSubidSingle.put(natureid, Arrays.asList(parentnode));
                                    }
                                } else {
                                    listNatureidSingle.add(natureid);
                                }
                            } else if (prov != null && prov.contains(provid)) {
                                if (isNatureSub) {
                                    if (mapNatureSubidMulti.containsKey(natureid)) {
                                        mapNatureSubidMulti.get(natureid).add(parentnode);
                                    } else {
                                        mapNatureSubidMulti.put(natureid, Arrays.asList(parentnode));
                                    }
                                } else {
                                    listNatureidMulti.add(natureid);
                                }
                            }
                        }
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    if (isTarget) {
                        writer.writeCharacters(reader.getText());
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    nodename = reader.getLocalName();
                    if (isTarget) {
                        if ("province".equals(nodename)) {
                            writer.writeEndElement();
                            writer.writeCharacters(newline);
                            isProv = false;
                            isTarget = false;
                            // flagStop = true;
                        } else if ("area".equals(nodename)) {
                        } else if ("population".equals(nodename) && !isCity) {
                        } else {
                            writer.writeEndElement();
                        }
                    } else if ("country".equals(nodename)) {
                        isCountry = false;
                        posCtryname = 0;
                    } else if ("province".equals(nodename)) {
                        isProv = false;
                        isTarget = false;
                        posProvname = 0;
                    } else if (listNaturename.contains(nodename)) {
                        isNature = false;
                    } else if (listNaturenameSub.contains(nodename)) {
                        isNatureSub = false;
                    }
                    break;
                }
                if (flagStop) {
                    break;
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Orig car_code: " + carcodeOld);
        System.out.println("New country name: " + countrynameNew);
        System.out.println("Orig country name: " + countrynameOrigi);
        // System.out.println(outputNewCountry.toString());
        // System.out.println(mapBorder);
        // System.out.println(mapPopProvince);
        // System.out.println(lPopProvince);
        // System.out.println(lPopCountry);
        // System.out.println(mapPoprateProvince);
        // System.out.println(listNatureidSingle);
        // System.out.println(mapNatureSubidSingle);
        // System.out.println(listNatureidMulti);
        // System.out.println(mapNatureSubidMulti);
    }

    /**
     * Change the exit.
     */
    public void calExit() {
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLStreamReader inReader = inputFactory.createXMLStreamReader(inputStream);
            XMLStreamWriter outWriter = outputFactory.createXMLStreamWriter(outputStream);

            if (inReader.getEventType() == XMLStreamConstants.START_DOCUMENT) {
                System.out.println("Start Document");
                outWriter.writeStartDocument("utf-8", "1.0");
                outWriter.writeCharacters(newline);
            }

            boolean isProvince = false;
            boolean isOrigiCountry = false;
            boolean isBorderCountry = false;
            boolean isTarget = false;
            boolean addNewBorder = true;
            String carcodeCurrent = null;

            boolean isOrgMember = false;

            boolean isNature = false;
            boolean isNatureSub = false;
            String natureid = null;

            String nodename = null;

            while (inReader.hasNext()) {
                int event = inReader.next();
                switch (event) {
                case XMLStreamConstants.END_DOCUMENT:
                    // outWriter.writeEndElement();
                    break;
                case XMLStreamConstants.DTD:
                    System.out.println("There is no internal DTD.");
                    outWriter.writeDTD(DTD);
                    // outWriter.writeStartElement("mondial");
                    break;
                case XMLStreamConstants.START_ELEMENT:
                    nodename = inReader.getLocalName();
                    if ("country".equals(nodename)) {
                        carcodeCurrent = inReader.getAttributeValue(null, "car_code");
                        if (carcodeOld.equals(carcodeCurrent)) {
                            // add new country
                            outWriter.writeCharacters(newline);
                            outWriter.writeDTD(outputNewCountry.toString());
                            isOrigiCountry = true;
                            outWriter.writeStartElement(nodename);
                            for (int i = 0; i < inReader.getAttributeCount(); i++) {
                                String value = inReader.getAttributeValue(i);
                                if ("area".equals(inReader.getAttributeLocalName(i))) {
                                    Double p = Double.parseDouble(value);
                                    value = new DecimalFormat("#.##").format(p - dAreaProvince);
                                }
                                outWriter.writeAttribute(inReader.getAttributeLocalName(i), value);
                            }
                        } else {
                            if (mapBorder.containsKey(carcodeCurrent)) {
                                isBorderCountry = true;
                            }
                            outWriter.writeCharacters(newline);
                            outWriter.writeStartElement(nodename);
                            for (int i = 0; i < inReader.getAttributeCount(); i++) {
                                outWriter.writeAttribute(inReader.getAttributeLocalName(i), inReader.getAttributeValue(i));
                            }
                        }
                    } else if (isOrigiCountry) {
                        if (isTarget) {
                            continue;
                        }
                        if ("province".equals(nodename)) {
                            if (provid.equals(inReader.getAttributeValue(null, "id"))) {
                                isTarget = true;
                                continue;
                            }
                            isProvince = true;
                            outWriter.writeStartElement(nodename);
                            for (int i = 0; i < inReader.getAttributeCount(); i++) {
                                outWriter.writeAttribute(inReader.getAttributeLocalName(i), inReader.getAttributeValue(i));
                            }
                        } else if ("population".equals(nodename) && !isProvince) {
                            String popyear = inReader.getAttributeValue(null, "year");
                            if (mapPopProvince.containsKey(popyear)) {
                                outWriter.writeStartElement(nodename);
                                for (int i = 0; i < inReader.getAttributeCount(); i++) {
                                    outWriter.writeAttribute(inReader.getAttributeLocalName(i), inReader.getAttributeValue(i));
                                }
                                String text = inReader.getElementText();
                                lPopCountry = Long.parseLong(text);
                                outWriter.writeCharacters(String.valueOf(lPopCountry - mapPopProvince.get(popyear)));
                                outWriter.writeEndElement();
                            } else {
                                inReader.getElementText();
                            }
                        } else if ((nodename.contains("gdp_") || "inflation".equals(nodename)) && !isProvince) {
                            outWriter.writeStartElement(nodename);
                            for (int i = 0; i < inReader.getAttributeCount(); i++) {
                                outWriter.writeAttribute(inReader.getAttributeLocalName(i), inReader.getAttributeValue(i));
                            }
                            String text = inReader.getElementText();
                            if ("gdp_total".equals(nodename)) {
                                dGdpCountry = Double.parseDouble(text);
                                text = new DecimalFormat("#.##").format(dGdpCountry - dGdpProvince);
                            } else {
                                Double oldGdpRate = Double.parseDouble(text) * dGdpCountry;
                                Double newGdpRate = (oldGdpRate - mapGdpProvince.get(nodename) * dGdpProvince);
                                text = new DecimalFormat("#.##").format(newGdpRate / (dGdpCountry - dGdpProvince));
                            }
                            outWriter.writeCharacters(text);
                            outWriter.writeEndElement();
                        } else if (("population_growth".equals(nodename) || "infant_mortality".equals(nodename)
                                || "unemployment".equals(nodename)) && !isProvince) {
                            outWriter.writeStartElement(nodename);
                            for (int i = 0; i < inReader.getAttributeCount(); i++) {
                                outWriter.writeAttribute(inReader.getAttributeLocalName(i), inReader.getAttributeValue(i));
                            }
                            String text = inReader.getElementText();
                            Double oldPoprate = Double.parseDouble(text) * lPopCountry;
                            Double newPoprate = oldPoprate - mapPoprateProvince.get(nodename) * lPopProvince;
                            text = new DecimalFormat("#.##").format(newPoprate / (lPopCountry - lPopProvince));
                            outWriter.writeCharacters(text);
                            outWriter.writeEndElement();
                        } else if (("ethnicgroup".equals(nodename) || "religion".equals(nodename) || "language".equals(nodename))
                                && !isProvince) {
                            outWriter.writeStartElement(nodename);
                            double poprate2 = 0;
                            for (int i = 0; i < inReader.getAttributeCount(); i++) {
                                if ("percentage".equals(inReader.getAttributeLocalName(i))) {
                                    poprate2 = Double.parseDouble(inReader.getAttributeValue(i));
                                    break;
                                } else {
                                    outWriter.writeAttribute(inReader.getAttributeLocalName(i), inReader.getAttributeValue(i));
                                }
                            }
                            String text = inReader.getElementText();
                            Double oldPoprate = poprate2 * lPopCountry;
                            Double newPoprate = oldPoprate - mapPoprateProvince.get(text) * lPopProvince;
                            String attr = new DecimalFormat("#.##").format(newPoprate / (lPopCountry - lPopProvince));
                            outWriter.writeAttribute("percentage", attr);
                            outWriter.writeCharacters(text);
                            outWriter.writeEndElement();
                        } else if ("border".equals(nodename)) {
                            if (addNewBorder) {
                                outWriter.writeEmptyElement(nodename);
                                outWriter.writeAttribute("country", carcodeNew);
                                outWriter.writeAttribute("length", mapBorder.get(carcodeOld));
                                addNewBorder = false;
                            }
                            String ctry = inReader.getAttributeValue(null, "country");
                            String length = inReader.getAttributeValue(null, "length");
                            if (mapBorder.containsKey(ctry)) {
                                Double l = Double.parseDouble(mapBorder.get(ctry));
                                length = new DecimalFormat("#.##").format(Double.parseDouble(length) - l);
                            }
                            outWriter.writeEmptyElement(nodename);
                            outWriter.writeAttribute("country", ctry);
                            outWriter.writeAttribute("length", length);
                        } else {
                            outWriter.writeStartElement(nodename);
                            for (int i = 0; i < inReader.getAttributeCount(); i++) {
                                outWriter.writeAttribute(inReader.getAttributeLocalName(i), inReader.getAttributeValue(i));
                            }
                        }
                    } else if (isBorderCountry) {
                        if ("border".equals(nodename)) {
                            if (addNewBorder) {
                                outWriter.writeEmptyElement(nodename);
                                outWriter.writeAttribute("country", carcodeNew);
                                outWriter.writeAttribute("length", mapBorder.get(carcodeCurrent));
                                addNewBorder = false;
                            }
                            String ctry = inReader.getAttributeValue(null, "country");
                            String length = inReader.getAttributeValue(null, "length");
                            if (mapBorder.containsKey(ctry)) {
                                Double l = Double.parseDouble(mapBorder.get(carcodeCurrent));
                                length = new DecimalFormat("#.##").format(Double.parseDouble(length) - l);
                            }
                            outWriter.writeEmptyElement(nodename);
                            outWriter.writeAttribute("country", ctry);
                            outWriter.writeAttribute("length", length);
                        } else {
                            outWriter.writeStartElement(nodename);
                            for (int i = 0; i < inReader.getAttributeCount(); i++) {
                                outWriter.writeAttribute(inReader.getAttributeLocalName(i), inReader.getAttributeValue(i));
                            }
                        }
                    } else if ("organization".equals(nodename)) {
                        String id = inReader.getAttributeValue(null, "id");
                        if (listMembersNew.contains(id)) {
                            isOrgMember = true;
                        }
                        outWriter.writeStartElement(nodename);
                        for (int i = 0; i < inReader.getAttributeCount(); i++) {
                            outWriter.writeAttribute(inReader.getAttributeLocalName(i), inReader.getAttributeValue(i));
                        }
                    } else if (isOrgMember && "members".equals(nodename)) { // organization
                        outWriter.writeEmptyElement(nodename);
                        for (int i = 0; i < inReader.getAttributeCount(); i++) {
                            String value = inReader.getAttributeValue(i);
                            if ("country".equals(inReader.getAttributeLocalName(i))) {
                                List<String> list = Arrays.asList(value.split(" "));
                                if (list.contains(carcodeOld)) {
                                    value += " " + carcodeNew;
                                }
                            }
                            outWriter.writeAttribute(inReader.getAttributeLocalName(i), value);
                        }
                    } else if (listNaturename.contains(nodename)) { // element: nature
                        isNature = true;
                        outWriter.writeStartElement(nodename);
                        natureid = inReader.getAttributeValue(null, "id");
                        String ctry = inReader.getAttributeValue(null, "country");
                        if (listNatureidSingle.contains(natureid)) {
                            List<String> list = new ArrayList<String>(0);
                            for (String str : ctry.split(" ")) {
                                if (carcodeOld.equals(str)) {
                                    list.add(carcodeNew);
                                } else {
                                    list.add(str);
                                }
                            }
                            ctry = String.join(" ", list);
                        } else if (listNatureidMulti.contains(natureid)) {
                            ctry += " " + carcodeNew;
                        }
                        for (int i = 0; i < inReader.getAttributeCount(); i++) {
                            if ("country".equals(inReader.getAttributeLocalName(i))) {
                                outWriter.writeAttribute("country", ctry);
                            } else {
                                outWriter.writeAttribute(inReader.getAttributeLocalName(i), inReader.getAttributeValue(i));
                            }
                        }
                    } else if (listNaturenameSub.contains(nodename)) { // element: source or estuary
                        isNatureSub = true;
                        outWriter.writeStartElement(nodename);
                        String ctry = inReader.getAttributeValue(null, "country");
                        if (mapNatureSubidSingle.containsKey(natureid) && mapNatureSubidSingle.get(natureid).contains(nodename)) {
                            List<String> list = new ArrayList<String>(0);
                            for (String str : ctry.split(" ")) {
                                if (carcodeOld.equals(str)) {
                                    list.add(carcodeNew);
                                } else {
                                    list.add(str);
                                }
                            }
                            ctry = String.join(" ", list);
                        } else if (mapNatureSubidMulti.containsKey(natureid) && mapNatureSubidMulti.get(natureid).contains(nodename)) {
                            ctry += " " + carcodeNew;
                        }
                        for (int i = 0; i < inReader.getAttributeCount(); i++) {
                            if ("country".equals(inReader.getAttributeLocalName(i))) {
                                outWriter.writeAttribute("country", ctry);
                            } else {
                                outWriter.writeAttribute(inReader.getAttributeLocalName(i), inReader.getAttributeValue(i));
                            }
                        }
                    } else if (isNature) { // nature && !isNatureSub
                        if ("located".equals(nodename)) {
                            String ctry = inReader.getAttributeValue(null, "country");
                            String prov = inReader.getAttributeValue(null, "province");
                            if (provid.equals(prov)) {
                                ctry = carcodeNew;
                                prov = null;
                            } else if (prov != null && prov.contains(provid)) {
                                prov = prov.replace(provid, "").replace("  ", " ");
                                outWriter.writeEmptyElement(nodename);
                                outWriter.writeAttribute("country", carcodeNew);
                            }
                            outWriter.writeEmptyElement(nodename);
                            if (ctry != null) {
                                outWriter.writeAttribute("country", ctry);
                            }
                            if (prov != null) {
                                outWriter.writeAttribute("province", prov);
                            }
                        } else {
                            outWriter.writeStartElement(nodename);
                            for (int i = 0; i < inReader.getAttributeCount(); i++) {
                                outWriter.writeAttribute(inReader.getAttributeLocalName(i), inReader.getAttributeValue(i));
                            }
                        }
                        // } else if (isNatureSub) { // source or estuary
                        // if ("located".equals(nodename)) {
                        // String ctry = inReader.getAttributeValue(null, "country");
                        // String prov = inReader.getAttributeValue(null, "province");
                        // if (provid.equals(prov)) {
                        // ctry = carcodeNew;
                        // prov = null;
                        // } else if (prov != null && prov.contains(provid)) {
                        // prov = prov.replace(provid, "").replace(" ", " ");
                        // outWriter.writeEmptyElement(nodename);
                        // outWriter.writeAttribute("country", carcodeNew);
                        // }
                        // outWriter.writeEmptyElement(nodename);
                        // if (ctry != null) {
                        // outWriter.writeAttribute("country", ctry);
                        // }
                        // if (prov != null) {
                        // outWriter.writeAttribute("province", prov);
                        // }
                        // } else {
                        // outWriter.writeStartElement(nodename);
                        // for (int i = 0; i < inReader.getAttributeCount(); i++) {
                        // outWriter.writeAttribute(inReader.getAttributeLocalName(i), inReader.getAttributeValue(i));
                        // }
                        // }
                    } else if ("airport".equals(nodename)) { // element: airport
                        outWriter.writeStartElement(nodename);
                        String ctyid = inReader.getAttributeValue(null, "city");
                        String ctrycode = inReader.getAttributeValue(null, "country");
                        for (int i = 0; i < inReader.getAttributeCount(); i++) {
                            String attrname = inReader.getAttributeLocalName(i);
                            String value = inReader.getAttributeValue(i);
                            if ("city".equals(attrname)) {
                                ctyid = value;
                            } else if ("country".equals(attrname)) {
                                ctrycode = value;
                            } else {
                                outWriter.writeAttribute(attrname, value);
                            }
                        }
                        if (listCityoldidProvince.contains(ctyid)) {
                            ctyid = ctyid.replace(countrynameOrigi, countrynameNew);
                            ctrycode = carcodeNew;
                        }
                        if (ctyid != null) {
                            outWriter.writeAttribute("city", ctyid);
                        }
                        outWriter.writeAttribute("country", ctrycode);
                    } else {
                        outWriter.writeStartElement(nodename);
                        for (int i = 0; i < inReader.getAttributeCount(); i++) {
                            outWriter.writeAttribute(inReader.getAttributeLocalName(i), inReader.getAttributeValue(i));
                        }
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    if (isOrigiCountry) {
                        if (isTarget) { // do nothing
                            continue;
                        }
                        outWriter.writeCharacters(inReader.getText());
                    } else if (isBorderCountry) {
                        outWriter.writeCharacters(inReader.getText());
                    } else if (isNature && !isNatureSub) { // nature
                        outWriter.writeCharacters(inReader.getText());
                    } else if (isNatureSub) { // source or estuary
                        outWriter.writeCharacters(inReader.getText());
                    } else {
                        outWriter.writeCharacters(inReader.getText());
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    nodename = inReader.getLocalName();
                    if ("country".equals(nodename)) {
                        outWriter.writeEndElement();
                        isOrigiCountry = false;
                        isBorderCountry = false;
                        isTarget = false;
                        isProvince = false;
                        addNewBorder = true;
                    } else if (isOrigiCountry) {
                        if (isTarget) {
                            if ("province".equals(nodename)) { // do nothing
                                isTarget = false;
                            }
                            continue;
                        }
                        if ("province".equals(nodename)) { // do nothing
                            isProvince = false;
                        } else if ("population".equals(nodename) && !isProvince) { // do nothing
                            continue;
                        } else if ("border".equals(nodename) && !isProvince) { // do nothing
                            continue;
                        }
                        outWriter.writeEndElement();
                    } else if (isBorderCountry) {
                        if ("border".equals(nodename) && !isProvince) { // do nothing
                            continue;
                        }
                        outWriter.writeEndElement();
                    } else if ("organization".equals(nodename)) {
                        outWriter.writeEndElement();
                        isOrgMember = false;
                    } else if (isOrgMember && "members".equals(nodename)) {
                        continue;
                    } else if (listNaturename.contains(nodename)) { // element: nature
                        outWriter.writeEndElement();
                        isNature = false;
                    } else if (listNaturenameSub.contains(nodename)) { // element: source or estuary
                        outWriter.writeEndElement();
                        isNatureSub = false;
                    } else if (isNature) { // nature && !isNatureSub
                        if ("located".equals(nodename)) {
                        } else {
                            outWriter.writeEndElement();
                        }
                        // } else if (isNatureSub) { // source or estuary
                        // if ("located".equals(nodename)) {
                        // } else {
                        // outWriter.writeEndElement();
                        // }
                    } else {
                        outWriter.writeEndElement();
                    }
                    break;
                }
                if (event == XMLStreamConstants.END_DOCUMENT) {
                    System.out.println("End Document");
                    break;
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * Validate the XML document against DTD.
     */
    public void validate() {
        System.out.println("\nValidate result:");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        SimpleErrorHandler errerHandler = new SimpleErrorHandler();
        try {
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setErrorHandler(errerHandler);
            reader.parse(new InputSource(ouptutPath));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (errerHandler.getResult()) {
            System.out.println("PASS");
        } else {
            System.out.println("ERROR!");
        }
    }

    /**
     * Error Handler to output the validation result.
     * 
     * @author Chenfeng Zhu
     *
     */
    public class SimpleErrorHandler implements ErrorHandler {
        private boolean pass = true;

        public void warning(SAXParseException e) throws SAXException {
            pass = false;
            System.out.println(e.getMessage());
        }

        public void error(SAXParseException e) throws SAXException {
            pass = false;
            System.out.println(e.getMessage());
        }

        public void fatalError(SAXParseException e) throws SAXException {
            pass = false;
            System.out.println(e.getMessage());
        }

        public boolean getResult() {
            return this.pass;
        }
    }

}
