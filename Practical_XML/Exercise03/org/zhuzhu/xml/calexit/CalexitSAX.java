/*
 * Copyright (c) 2016, Chenfeng Zhu. All rights reserved.
 * 
 */
package org.zhuzhu.xml.calexit;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Practical XML Exercise 03.<br/>
 * Calexit with SAX.
 * 
 * @author Chenfeng Zhu
 *
 */
public class CalexitSAX {

    // User-defined file path
    private String sourcePath = "/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/lib/mondial.xml";
    private String ouptutPath = "/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/ex03/mondial_new_sax.xml";
    private String newinfoPath = "/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/ex03/calexit_new.xml";

    // User-defined static variables
    private String provid = "prov-United-States-6";
    private List<String> listOrgException = Arrays.asList("org-G-5", "org-G-7");
    private String goverment = "CA federal republic";

    private OutputStream outputStream;

    // global information
    private String carcodeNew = "carcodeNew";
    private String carcodeOld = "carcodeOld";
    private String countrynameNew = "countrynameNew";
    private String countrynameOrig = "countrynameOrig";

    private Double dAreaProvince;
    private Map<String, Long> mapPopProvince = new HashMap<String, Long>(0);
    private Long lPopProvince = 0l;
    private Long lPopCountry = 0l;
    private Map<String, Double> mapPoprateProvince = new HashMap<String, Double>(0);

    private Map<String, Double> mapGdpProvince = new HashMap<String, Double>(0);
    private Double dGdpProvince = 0d;
    private Double dGdpCountry = 0d;

    private List<String> listCityoldidProvince = new ArrayList<String>(0);

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

        CalexitSAX csax = new CalexitSAX(filepath, outputFile, newinforFile);
        csax.readMondial();
        try {
            csax.addDTD();
        } catch (IOException e) {
            e.printStackTrace();
        }
        csax.validate();
    }

    public CalexitSAX(String source, String target, String newinfor) {
        if (source != null && !("".equalsIgnoreCase(source))) {
            this.sourcePath = source;
        }
        System.out.println("Source XML File: " + sourcePath);
        if (target != null && !("".equalsIgnoreCase(target))) {
            this.ouptutPath = target;
        }
        System.out.println("Target XML File: " + ouptutPath);
        try {
            this.outputStream = new FileOutputStream(ouptutPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (newinfor != null && !("".equalsIgnoreCase(newinfor))) {
            this.newinfoPath = newinfor;
        }
        System.out.println("New Information XML File: " + newinfoPath);
    }

    public void readMondial() {
        MondialHandler handler = new MondialHandler();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            parser.parse(sourcePath, handler);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add DocType definition.
     * 
     * @throws IOException
     */
    public void addDTD() throws IOException {
        Path path = Paths.get(ouptutPath);
        Charset charset = StandardCharsets.UTF_8;
        String content = new String(Files.readAllBytes(path), charset);
        String start = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"; // Pattern.quote("?>"); //
        String dtd = "<!DOCTYPE mondial SYSTEM \"mondial.dtd\">";
        content = content.replace(start, start + dtd);
        // content = start + dtd + content;
        Files.write(path, content.getBytes(charset));
    }

    /**
     * Validate XML document.
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
     * The main handler to deal with the exit.
     * 
     * @author Chenfeng Zhu
     *
     */
    protected class MondialHandler extends DefaultHandler2 {

        // for document format
        private XMLEvent eNewline = null;
        private XMLEvent eTab = null;

        private XMLEventWriter writer;
        private XMLEventFactory eventFactory;
        private XMLEventWriter printer;

        private boolean isProvince = false;
        private boolean isOrigCountry = false;
        private boolean isBorderCountry = false;
        private boolean isCalif = false;
        private boolean isArea = false;
        private int posCountry = 0;

        private List<XMLEvent> listEventOrigCountry = new ArrayList<XMLEvent>(0);
        private List<XMLEvent> listEventBorderCountry = new ArrayList<XMLEvent>(0);
        private List<XMLEvent> listEventCalif = new ArrayList<XMLEvent>(0);

        private String membershipsNew;
        private List<String> listMembersNew = new ArrayList<String>(0);
        private String capitalNew;

        private boolean addNewinfor = true;
        private List<XMLEvent> listEventNewinfor = new ArrayList<XMLEvent>(0);
        private int posUnemployment = 0;
        private String carcodeCurrent = null;
        private Map<String, String> mapBorder = new HashMap<String, String>(0);
        private boolean addNewborder = true;

        private List<XMLEvent> listEventEncompassed = new ArrayList<XMLEvent>(0);

        // for population element of original country
        private boolean isPopulation = false;
        private String popyear = null;

        // for GDP element of original country
        private boolean isGdp = false;
        private String gdptype = null;
        private boolean isPoprate = false;
        private String popratetype = null;
        private boolean isPoprate2 = false;
        private String popratetype2 = null;
        private double poprate2 = 0;

        // for organization element
        private boolean isOrg = false;
        private String orgid = null;

        // for nature element
        private List<String> listNaturename = Arrays.asList("sea", "river", "lake", "island", "mountain", "desert");
        private List<String> listNaturenameSub = Arrays.asList("source", "estuary");
        private boolean isNature = false;
        private boolean isNatureSub = false;
        private List<XMLEvent> listEventNature = new ArrayList<XMLEvent>(0);
        private List<XMLEvent> listEventNatureSub = new ArrayList<XMLEvent>(0);
        private Attribute attrNature = null;
        private int posNatureAttr = 0;

        public MondialHandler() {
            super();
            try {
                XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
                eventFactory = XMLEventFactory.newInstance();
                writer = outputFactory.createXMLEventWriter(outputStream);
                printer = outputFactory.createXMLEventWriter(System.out);
                eNewline = eventFactory.createCharacters("\n"); // .createDTD("\n");
                eTab = eventFactory.createCharacters("    ");
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }

        public void startDocument() throws SAXException {
            System.out.println("startDocument");
            XMLEvent event = eventFactory.createStartDocument();
            try {
                writer.add(event);
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }

            // pre-work to get basic information
            this.readOriginalCountry();
            this.readNewinfor();
            countrynameNew = countrynameNew.replaceAll(" ", "-");
            countrynameOrig = countrynameOrig.replaceAll(" ", "-");
            System.out.println("\n\nNew car_code: " + carcodeNew);
            System.out.println("Orig car_code: " + carcodeOld);
            System.out.println("New country name: " + countrynameNew);
            System.out.println("Orig country name: " + countrynameOrig);
        }

        public void startDTD(String name, String publicId, String systemId) throws SAXException { // useless
            System.out.println("startDTD");
            System.out.println(name);
            System.out.println(publicId);
            System.out.println(systemId);
        }

        public void endDTD() throws SAXException { // useless
            System.out.println("endDTD");
        }

        public void startEntity(String name) throws SAXException { // useless
            System.out.println("startEntity");
            System.out.println(name);
        }

        public void processingInstruction(String target, String data) throws SAXException { // useless
            System.out.println("processingInstruction");
            System.out.println(target);
            System.out.println(data);
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            String elementName;
            if (qName == null || qName.equals("")) {
                elementName = localName;
            } else {
                elementName = qName;
            }
            XMLEvent event = eventFactory.createStartElement("", null, elementName);
            try {
                if ("country".equals(elementName)) { // element: country
                    listEventOrigCountry.add(event);
                    listEventBorderCountry.add(event);
                    for (int i = 0; i < attributes.getLength(); i++) {
                        listEventBorderCountry.add(eventFactory.createAttribute(attributes.getQName(i), attributes.getValue(i)));
                        if ("car_code".equals(attributes.getQName(i))) {
                            if (carcodeOld.equals(attributes.getValue(i))) {
                                isOrigCountry = true;
                            } else if (mapBorder.containsKey(attributes.getValue(i))) {
                                carcodeCurrent = attributes.getValue(i);
                                isBorderCountry = true;
                            }
                        }
                    }
                    if (isOrigCountry) {
                        for (int i = 0; i < attributes.getLength(); i++) {
                            String attrname = attributes.getQName(i);
                            String value = attributes.getValue(i);
                            if ("area".equals(attrname)) { // re-calculate the area.
                                Double p = Double.parseDouble(value);
                                value = new DecimalFormat("#.##").format(p - dAreaProvince);
                                // value = String.format("#.##", String.valueOf(p - provinceArea));
                            } else if ("memberships".equals(attrname)) {
                                membershipsNew = value;
                            }
                            listEventOrigCountry.add(eventFactory.createAttribute(attrname, value));
                        }
                    } else {
                        listEventOrigCountry = new ArrayList<XMLEvent>(0);
                    }
                    if (isBorderCountry) {
                    } else {
                        listEventBorderCountry = new ArrayList<XMLEvent>(0);
                    }
                    if (!isOrigCountry && !isBorderCountry) {
                        writer.add(eNewline);
                        writer.add(event);
                        for (int i = 0; i < attributes.getLength(); i++) {
                            writer.add(eventFactory.createAttribute(attributes.getQName(i), attributes.getValue(i)));
                        }
                    }
                } else if ("province".equals(elementName) && isOrigCountry) { // element: province
                    isProvince = true;
                    for (int i = 0; i < attributes.getLength(); i++) {
                        if (attributes.getQName(i).equals("id") && attributes.getValue(i).equals(provid)) {
                            isCalif = true;
                            // isOrigCountry = true;
                            break;
                        }
                    }
                    if (isCalif) { // if it is California
                        event = eventFactory.createStartElement("", null, "country"); // change to country
                        listEventCalif.add(event);
                        event = eventFactory.createAttribute("car_code", carcodeNew); // attr: car_code
                        listEventCalif.add(event);
                        posCountry = listEventCalif.size(); // record the position for area
                        for (int i = 0; i < attributes.getLength(); i++) {
                            if ("capital".equals(attributes.getQName(i))) { // attr: capital
                                capitalNew = attributes.getValue(i).replace(countrynameOrig, countrynameNew);
                                listEventCalif.add(eventFactory.createAttribute(attributes.getQName(i), capitalNew));
                            }
                        }
                        // remove org exception
                        for (String str : listOrgException) {
                            membershipsNew = membershipsNew.replace(str, "");
                        }
                        while (membershipsNew.contains("  ")) {
                            membershipsNew = membershipsNew.replace("  ", " ");
                        }
                        membershipsNew = membershipsNew.trim();
                        listEventCalif.add(eventFactory.createAttribute("memberships", membershipsNew)); // attr:
                                                                                                         // memberships
                        listMembersNew = Arrays.asList(membershipsNew.split(" "));
                    } else if (isOrigCountry) { // if it is a normal province
                        listEventOrigCountry.add(event);
                        for (int i = 0; i < attributes.getLength(); i++) {
                            listEventOrigCountry.add(eventFactory.createAttribute(attributes.getQName(i), attributes.getValue(i)));
                        }
                    }
                } else if (isCalif) { // new country
                    if ("city".equals(elementName)) {
                        if (addNewinfor) { // add new info before city
                            listEventNewinfor.addAll(posUnemployment, listEventEncompassed);
                            listEventNewinfor.addAll(posUnemployment, this.getGovernment());
                            listEventNewinfor.addAll(posUnemployment, this.getIndependentDate());
                            listEventCalif.addAll(listEventNewinfor);
                            addNewinfor = false;
                        }
                        listEventCalif.add(event);
                        for (int i = 0; i < attributes.getLength(); i++) { // deal with attributes of city.
                            String attrName = attributes.getQName(i);
                            if ("id".equals(attrName)) {
                                String value = attributes.getValue(i);
                                listCityoldidProvince.add(value);
                                value = value.replace(countrynameOrig, countrynameNew);
                                listEventCalif.add(eventFactory.createAttribute(attributes.getQName(i), value));
                                if (value.equals(capitalNew)) {
                                    listEventCalif.add(eventFactory.createAttribute("is_country_cap", "yes"));
                                }
                            } else if ("country".equals(attrName)) {
                                listEventCalif.add(eventFactory.createAttribute(attributes.getQName(i), carcodeNew));
                            } else if ("province".equals(attrName)) {
                            }
                        }
                        isArea = false;
                    } else if (!"area".equals(elementName)) {
                        listEventCalif.add(event);
                        for (int i = 0; i < attributes.getLength(); i++) {
                            listEventCalif.add(eventFactory.createAttribute(attributes.getQName(i), attributes.getValue(i)));
                        }
                        isArea = false;
                    } else {
                        isArea = true;
                    }
                } else if (isOrigCountry) { // original country
                    if ("encompassed".equals(elementName)) { // continent
                        boolean flag = false;
                        listEventOrigCountry.add(event);
                        for (int i = 0; i < attributes.getLength(); i++) {
                            listEventOrigCountry.add(eventFactory.createAttribute(attributes.getQName(i), attributes.getValue(i)));
                            if ("percentage".equals(attributes.getQName(i)) && "100".equals(attributes.getValue(i))) {
                                flag = true;
                            }
                        }
                        if (flag) {
                            listEventEncompassed.add(event);
                            for (int i = 0; i < attributes.getLength(); i++) {
                                listEventEncompassed.add(eventFactory.createAttribute(attributes.getQName(i), attributes.getValue(i)));
                            }
                            listEventEncompassed.add(eventFactory.createEndElement("", null, elementName));
                        }
                    } else if ("population".equals(elementName) && !isProvince) { // population
                        isPopulation = true;
                        for (int i = 0; i < attributes.getLength(); i++) {
                            if ("year".equals(attributes.getQName(i)) && mapPopProvince.containsKey(attributes.getValue(i))) {
                                popyear = attributes.getValue(i);
                                break;
                            }
                        }
                        if (popyear != null) {
                            listEventOrigCountry.add(event);
                            for (int i = 0; i < attributes.getLength(); i++) {
                                listEventOrigCountry.add(eventFactory.createAttribute(attributes.getQName(i), attributes.getValue(i)));
                            }
                        }
                    } else if ((elementName.contains("gdp_") || "inflation".equals(elementName)) && !isProvince) { // gdp
                        isGdp = true;
                        gdptype = elementName;
                        listEventOrigCountry.add(event);
                        for (int i = 0; i < attributes.getLength(); i++) {
                            listEventOrigCountry.add(eventFactory.createAttribute(attributes.getQName(i), attributes.getValue(i)));
                        }
                    } else if (("ethnicgroup".equals(elementName) || "religion".equals(elementName) || "language".equals(elementName))
                            && !isProvince) { // ethnicgroup, religion, language
                        isPoprate2 = true;
                        popratetype2 = elementName;
                        for (int i = 0; i < attributes.getLength(); i++) {
                            if ("percentage".equals(attributes.getQName(i))) {
                                poprate2 = Double.parseDouble(attributes.getValue(i));
                                break;
                            }
                        }
                    } else if (("population_growth".equals(elementName) || "infant_mortality".equals(elementName)
                            || "unemployment".equals(elementName)) && !isProvince) { // population rate
                        isPoprate = true;
                        popratetype = elementName;
                        listEventOrigCountry.add(event);
                        for (int i = 0; i < attributes.getLength(); i++) {
                            listEventOrigCountry.add(eventFactory.createAttribute(attributes.getQName(i), attributes.getValue(i)));
                        }
                    } else if ("border".equals(elementName)) { // border
                        if (addNewborder) {
                            listEventOrigCountry.add(eventFactory.createStartElement("", null, "border"));
                            listEventOrigCountry.add(eventFactory.createAttribute("country", carcodeNew));
                            listEventOrigCountry.add(eventFactory.createAttribute("length", mapBorder.get(carcodeOld)));
                            listEventOrigCountry.add(eventFactory.createEndElement("", null, "border"));
                            addNewborder = false;
                        }
                        listEventOrigCountry.add(event);
                        String bordername = null;
                        String length = null;
                        for (int i = 0; i < attributes.getLength(); i++) {
                            if ("country".equals(attributes.getQName(i))) {
                                bordername = attributes.getValue(i);
                            } else if ("length".equals(attributes.getQName(i))) {
                                length = attributes.getValue(i);
                            }
                        }
                        listEventOrigCountry.add(eventFactory.createAttribute("country", bordername));
                        if (mapBorder.containsKey(bordername)) {
                            Double l = Double.parseDouble(mapBorder.get(bordername));
                            length = new DecimalFormat("#.##").format(Double.parseDouble(length) - l);
                        }
                        listEventOrigCountry.add(eventFactory.createAttribute("length", length));
                    } else {
                        listEventOrigCountry.add(event);
                        for (int i = 0; i < attributes.getLength(); i++) {
                            listEventOrigCountry.add(eventFactory.createAttribute(attributes.getQName(i), attributes.getValue(i)));
                        }
                    }
                } else if (isBorderCountry) { // border country
                    if ("border".equals(elementName)) {
                        if (addNewborder) {
                            listEventBorderCountry.add(eventFactory.createStartElement("", null, "border"));
                            listEventBorderCountry.add(eventFactory.createAttribute("country", carcodeNew));
                            listEventBorderCountry.add(eventFactory.createAttribute("length", mapBorder.get(carcodeCurrent)));
                            listEventBorderCountry.add(eventFactory.createEndElement("", null, "border"));
                            addNewborder = false;
                        }
                        listEventBorderCountry.add(event);
                        String bordername = null;
                        String length = null;
                        for (int i = 0; i < attributes.getLength(); i++) {
                            if ("country".equals(attributes.getQName(i))) {
                                bordername = attributes.getValue(i);
                            } else if ("length".equals(attributes.getQName(i))) {
                                length = attributes.getValue(i);
                            }
                        }
                        listEventBorderCountry.add(eventFactory.createAttribute("country", bordername));
                        if (mapBorder.containsKey(bordername)) {
                            Double l = Double.parseDouble(mapBorder.get(carcodeCurrent));
                            length = new DecimalFormat("#.##").format(Double.parseDouble(length) - l);
                        }
                        listEventBorderCountry.add(eventFactory.createAttribute("length", length));
                    } else {
                        listEventBorderCountry.add(event);
                        for (int i = 0; i < attributes.getLength(); i++) {
                            listEventBorderCountry.add(eventFactory.createAttribute(attributes.getQName(i), attributes.getValue(i)));
                        }
                    }
                } else if ("organization".equals(elementName)) { // element: organization
                    isOrg = true;
                    writer.add(eNewline);
                    writer.add(event);
                    for (int i = 0; i < attributes.getLength(); i++) {
                        writer.add(eventFactory.createAttribute(attributes.getQName(i), attributes.getValue(i)));
                        if ("id".equals(attributes.getQName(i))) {
                            orgid = attributes.getValue(i);
                        }
                    }
                } else if (isOrg && "members".equals(elementName)) { // organization
                    writer.add(eNewline);
                    writer.add(event);
                    for (int i = 0; i < attributes.getLength(); i++) {
                        String value = attributes.getValue(i);
                        if ("country".equals(attributes.getQName(i)) && listMembersNew.contains(orgid)) {
                            List<String> list = Arrays.asList(value.split(" "));
                            if (list.contains(carcodeOld)) {
                                value += " " + carcodeNew;
                            }
                        }
                        writer.add(eventFactory.createAttribute(attributes.getQName(i), value));
                    }
                } else if (listNaturename.contains(elementName)) { // element: nature
                    isNature = true;
                    listEventNature.add(event);
                    for (int i = 0; i < attributes.getLength(); i++) {
                        String attrname = attributes.getQName(i);
                        String value = attributes.getValue(i);
                        if ("country".equals(attrname)) {
                            attrNature = eventFactory.createAttribute(attrname, value);
                            posNatureAttr = listEventNature.size();
                            listEventNature.add(attrNature);
                        } else {
                            listEventNature.add(eventFactory.createAttribute(attrname, value));
                        }
                    }
                } else if (listNaturenameSub.contains(elementName)) { // element: source or estuary
                    isNatureSub = true;
                    listEventNatureSub.add(event);
                    for (int i = 0; i < attributes.getLength(); i++) {
                        String attrname = attributes.getQName(i);
                        String value = attributes.getValue(i);
                        if ("country".equals(attrname)) {
                            attrNature = eventFactory.createAttribute(attrname, value);
                            posNatureAttr = listEventNatureSub.size();
                            listEventNatureSub.add(attrNature);
                        } else {
                            listEventNatureSub.add(eventFactory.createAttribute(attrname, value));
                        }
                    }
                } else if (isNature && !isNatureSub) { // nature
                    if ("located".equals(elementName)) {
                        String ctry = null;
                        String prov = null;
                        for (int i = 0; i < attributes.getLength(); i++) {
                            String attrname = attributes.getQName(i);
                            String value = attributes.getValue(i);
                            if ("country".equals(attrname)) {
                                ctry = value;
                            } else if ("province".equals(attrname)) {
                                prov = value;
                            }
                        }
                        if (provid.equals(prov)) { // only one province
                            ctry = carcodeNew;
                            prov = null;
                            listEventNature.remove(attrNature);
                            String cc = attrNature.getValue();
                            if (cc != null) {
                                List<String> listcc = new ArrayList<String>(0);
                                for (String c : cc.split(" ")) {
                                    if (carcodeOld.equals(c)) {
                                        listcc.add(carcodeNew);
                                    } else {
                                        listcc.add(c);
                                    }
                                }
                                attrNature = eventFactory.createAttribute("country", this.join(listcc, " "));
                                listEventNature.add(posNatureAttr, attrNature);
                            }
                        } else if (prov != null && prov.contains(provid)) {
                            prov = prov.replace(provid, "").replace("  ", " ");
                            listEventNature.add(eventFactory.createStartElement("", null, elementName));
                            listEventNature.add(eventFactory.createAttribute("country", carcodeNew));
                            listEventNature.add(eventFactory.createEndElement("", null, elementName));
                            listEventNature.remove(attrNature);
                            String cc = attrNature.getValue();
                            if (cc != null) {
                                attrNature = eventFactory.createAttribute("country", cc + " " + carcodeNew);
                                listEventNature.add(posNatureAttr, attrNature);
                            }
                        }
                        listEventNature.add(event);
                        if (ctry != null) {
                            listEventNature.add(eventFactory.createAttribute("country", ctry));
                        }
                        if (prov != null) {
                            listEventNature.add(eventFactory.createAttribute("province", prov));
                        }
                    } else {
                        listEventNature.add(event);
                        for (int i = 0; i < attributes.getLength(); i++) {
                            listEventNature.add(eventFactory.createAttribute(attributes.getQName(i), attributes.getValue(i)));
                        }
                    }
                } else if (isNatureSub) { // source or estuary
                    if ("located".equals(elementName)) {
                        String ctry = null;
                        String prov = null;
                        for (int i = 0; i < attributes.getLength(); i++) {
                            String attrname = attributes.getQName(i);
                            String value = attributes.getValue(i);
                            if ("country".equals(attrname)) {
                                ctry = value;
                            } else if ("province".equals(attrname)) {
                                prov = value;
                            }
                        }
                        if (provid.equals(prov)) { // only one province
                            ctry = carcodeNew;
                            prov = null;
                            listEventNatureSub.remove(attrNature);
                            String cc = attrNature.getValue();
                            if (cc != null) {
                                List<String> listcc = new ArrayList<String>(0);
                                for (String c : cc.split(" ")) {
                                    if (carcodeOld.equals(c)) {
                                        listcc.add(carcodeNew);
                                    } else {
                                        listcc.add(c);
                                    }
                                }
                                attrNature = eventFactory.createAttribute("country", this.join(listcc, " "));
                                listEventNatureSub.add(posNatureAttr, attrNature);
                            }
                        } else if (prov != null && prov.contains(provid)) {
                            prov = prov.replace(provid, "").replace("  ", " ");
                            listEventNatureSub.add(eventFactory.createStartElement("", null, elementName));
                            listEventNatureSub.add(eventFactory.createAttribute("country", carcodeNew));
                            listEventNatureSub.add(eventFactory.createEndElement("", null, elementName));
                            listEventNatureSub.remove(attrNature);
                            String cc = attrNature.getValue();
                            if (cc != null) {
                                attrNature = eventFactory.createAttribute("country", cc + " " + carcodeNew);
                                listEventNatureSub.add(posNatureAttr, attrNature);
                            }
                        }
                        listEventNatureSub.add(event);
                        if (ctry != null) {
                            listEventNatureSub.add(eventFactory.createAttribute("country", ctry));
                        }
                        if (prov != null) {
                            listEventNatureSub.add(eventFactory.createAttribute("province", prov));
                        }
                    } else {
                        listEventNatureSub.add(event);
                        for (int i = 0; i < attributes.getLength(); i++) {
                            listEventNatureSub.add(eventFactory.createAttribute(attributes.getQName(i), attributes.getValue(i)));
                        }
                    }
                } else if ("airport".equals(elementName)) { // element: airport
                    writer.add(eNewline);
                    writer.add(event);
                    String ctyid = null;
                    String ctrycode = null;
                    for (int i = 0; i < attributes.getLength(); i++) {
                        String attrname = attributes.getQName(i);
                        String value = attributes.getValue(i);
                        if ("city".equals(attrname)) {
                            ctyid = value;
                        } else if ("country".equals(attrname)) {
                            ctrycode = value;
                        } else {
                            writer.add(eventFactory.createAttribute(attributes.getQName(i), attributes.getValue(i)));
                        }
                    }
                    if (listCityoldidProvince.contains(ctyid)) {
                        ctyid = ctyid.replace(countrynameOrig, countrynameNew);
                        ctrycode = carcodeNew;
                    }
                    if (ctyid != null) {
                        writer.add(eventFactory.createAttribute("city", ctyid));
                    }
                    writer.add(eventFactory.createAttribute("country", ctrycode));
                } else { // other elements
                    writer.add(eNewline);
                    writer.add(event);
                    for (int i = 0; i < attributes.getLength(); i++) {
                        writer.add(eventFactory.createAttribute(attributes.getQName(i), attributes.getValue(i)));
                    }
                }
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }

        public void characters(char ch[], int start, int length) throws SAXException {
            String content = new String(ch, start, length);
            XMLEvent event = eventFactory.createCharacters(content);
            try {
                if (isCalif) { // new country
                    if (!isArea) {
                        listEventCalif.add(event);
                    } else {
                        event = eventFactory.createAttribute("area", content); // attr: area
                        listEventCalif.add(posCountry + 1, event);
                    }
                } else if (isOrigCountry) { // original country
                    if (isPopulation) { // re-calculate the population
                        if (popyear != null) {
                            lPopCountry = Long.parseLong(content);
                            content = String.valueOf(lPopCountry - mapPopProvince.get(popyear));
                            event = eventFactory.createCharacters(content);
                            listEventOrigCountry.add(event);
                        }
                    } else if (isPoprate) { //
                        Double oldPoprate = Double.parseDouble(content) * lPopCountry;
                        Double newPoprate = oldPoprate - mapPoprateProvince.get(popratetype) * lPopProvince;
                        content = new DecimalFormat("#.##").format(newPoprate / (lPopCountry - lPopProvince));
                        event = eventFactory.createCharacters(content);
                        listEventOrigCountry.add(event);
                        isPoprate = false;
                    } else if (isPoprate2) { // ethnicgroup, religion, language
                        listEventOrigCountry.add(eventFactory.createStartElement("", null, popratetype2));
                        Double oldPoprate = poprate2 * lPopCountry;
                        Double newPoprate = oldPoprate - mapPoprateProvince.get(content) * lPopProvince;
                        content = new DecimalFormat("#.##").format(newPoprate / (lPopCountry - lPopProvince));
                        listEventOrigCountry.add(eventFactory.createAttribute("percentage", content));
                        listEventOrigCountry.add(event);
                        isPoprate2 = false;
                    } else if (isGdp) {
                        if ("gdp_total".equals(gdptype)) {
                            dGdpCountry = Double.parseDouble(content);
                            content = new DecimalFormat("#.##").format(dGdpCountry - dGdpProvince);
                            event = eventFactory.createCharacters(content);
                        } else {
                            Double oldGdpRate = Double.parseDouble(content) * dGdpCountry;
                            Double newGdpRate = (oldGdpRate - mapGdpProvince.get(gdptype) * dGdpProvince);
                            content = new DecimalFormat("#.##").format(newGdpRate / (dGdpCountry - dGdpProvince));
                            event = eventFactory.createCharacters(content);
                        }
                        listEventOrigCountry.add(event);
                        isGdp = false;
                        gdptype = null;
                    } else {
                        listEventOrigCountry.add(event);
                    }
                } else if (isBorderCountry) { // border country
                    listEventBorderCountry.add(event);
                } else if (isNature && !isNatureSub) { // nature
                    listEventNature.add(event);
                } else if (isNatureSub) { // source or estuary
                    listEventNatureSub.add(event);
                } else {
                    writer.add(event);
                }
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            String elementName;
            if (qName == null || qName.equals("")) {
                elementName = localName;
            } else {
                elementName = qName;
            }
            XMLEvent event = eventFactory.createEndElement("", null, elementName);
            try {
                if ("country".equals(elementName)) { // element: country
                    if (isOrigCountry) {
                        listEventOrigCountry.add(event);
                        this.insertOrigCountry();
                        this.insertListEventFormat(listEventCalif);
                    } else if (isBorderCountry) {
                        listEventBorderCountry.add(event);
                        this.insertListEventFormat(listEventBorderCountry);
                    } else {
                        writer.add(event);
                    }
                    listEventOrigCountry = new ArrayList<XMLEvent>(0);
                    listEventBorderCountry = new ArrayList<XMLEvent>(0);
                    isOrigCountry = false;
                    isBorderCountry = false;
                    isProvince = false;
                    isCalif = false;
                    popyear = null;
                    isPopulation = false;
                    isGdp = false;
                    isPoprate = false;
                    isPoprate2 = false;
                    addNewborder = true;
                    carcodeCurrent = null;
                } else if ("province".equals(elementName)) { // element: province
                    if (isCalif) {
                        listEventCalif.add(eventFactory.createEndElement("", null, "country"));
                    } else if (isOrigCountry) {
                        listEventOrigCountry.add(event);
                    } else if (isBorderCountry) {
                        listEventBorderCountry.add(event);
                    } else {
                        writer.add(event);
                    }
                    isProvince = false;
                    isCalif = false;
                } else if (isCalif) {
                    if (!isArea) {
                        listEventCalif.add(event);
                    }
                } else if (isOrigCountry) {
                    if ("population".equals(elementName) && !isProvince) {
                        if (popyear != null) {
                            listEventOrigCountry.add(event);
                        }
                        popyear = null;
                        isPopulation = false;
                    } else {
                        listEventOrigCountry.add(event);
                    }
                } else if (isBorderCountry) {
                    listEventBorderCountry.add(event);
                } else if ("organization".equals(elementName)) { // element: organization
                    writer.add(event);
                    isOrg = false;
                    orgid = null;
                } else if (listNaturename.contains(elementName)) { // element: nature
                    listEventNature.add(event);
                    this.insertListEventFormat(listEventNature);
                    isNature = false;
                    listEventNature = new ArrayList<XMLEvent>(0);
                    attrNature = null;
                    posNatureAttr = 0;
                } else if (listNaturenameSub.contains(elementName)) { // element: source or estuary
                    listEventNatureSub.add(event);
                    listEventNature.addAll(listEventNatureSub);
                    isNatureSub = false;
                    listEventNatureSub = new ArrayList<XMLEvent>(0);
                    attrNature = null;
                    posNatureAttr = 0;
                } else if (isNature && !isNatureSub) { // nature
                    listEventNature.add(event);
                } else if (isNatureSub) { // source or estuary
                    listEventNatureSub.add(event);
                } else {
                    writer.add(event);
                }
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }

        public void endDocument() throws SAXException {
            XMLEvent event = eventFactory.createEndDocument();
            try {
                writer.add(event);
                printer.flush();
                printer.close();
                writer.flush();
                writer.close();
                // System.out.println("New car_code: " + carcodeNew);
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }

        /**
         * Change the element via events.
         */
        @Deprecated
        private void changeOrgiCountry() {
            for (int i = 0; i < listEventNewinfor.size() - 1; i++) {
                XMLEvent event = listEventNewinfor.get(i);
                if (event.isStartElement()) {
                    String nodename = event.asStartElement().getName().toString();
                    if ("ethnicgroup".equals(nodename)) {
                    } else if ("religion".equals(nodename)) {
                    } else if ("language".equals(nodename)) {
                    }
                    break;
                }
            }
        }

        private void insertOrigCountry() {
            this.changeOrgiCountry();
            this.insertListEventFormat(listEventOrigCountry);
        }

        /**
         * Insert events without indent.
         * 
         * @param list
         */
        protected void insertListEventFlat(List<XMLEvent> list) {
            try {
                for (XMLEvent e : list) {
                    if (e.isStartElement()) {
                        writer.add(eNewline);
                    }
                    writer.add(e);
                }
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }

        /**
         * Insert events with indent.
         * 
         * @param list
         */
        protected void insertListEventFormat(List<XMLEvent> list) {
            try {
                int start = 0;
                int end = 0;
                for (XMLEvent e : list) {
                    if (e.isStartElement()) {
                        writer.add(eNewline);
                        for (int i = 0; i < start; i++) {
                            writer.add(eTab);
                        }
                        start++;
                        end = 0;
                    } else if (e.isEndElement()) {
                        start--;
                        if (end != 0) {
                            writer.add(eNewline);
                            for (int i = 0; i < start; i++) {
                                writer.add(eTab);
                            }
                        }
                        end++;
                    }
                    writer.add(e);
                }
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }

        /**
         * Get a list event of indep_date element.
         * 
         * @return
         */
        private List<XMLEvent> getIndependentDate() {
            List<XMLEvent> list = new ArrayList<XMLEvent>(0);
            list.add(eventFactory.createStartElement("", null, "indep_date"));
            list.add(eventFactory.createAttribute("from", carcodeOld));
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            list.add(eventFactory.createCharacters(sdf.format(date)));
            list.add(eventFactory.createEndElement("", null, "indep_date"));
            return list;
        }

        /**
         * Get a list event of government element.
         * 
         * @return
         */
        private List<XMLEvent> getGovernment() {
            List<XMLEvent> list = new ArrayList<XMLEvent>(0);
            list.add(eventFactory.createStartElement("", null, "government"));
            list.add(eventFactory.createCharacters(goverment));
            list.add(eventFactory.createEndElement("", null, "government"));
            return list;
        }

        /**
         * Read the some information of original country.
         */
        public void readOriginalCountry() {
            OrigcountryHandler handler = new OrigcountryHandler();
            SAXParserFactory factory = SAXParserFactory.newInstance();
            try {
                SAXParser parser = factory.newSAXParser();
                parser.parse(sourcePath, handler);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Read the information of the new country.
         */
        public void readNewinfor() {
            NewinforHandler handler = new NewinforHandler();
            SAXParserFactory factory = SAXParserFactory.newInstance();
            try {
                SAXParser parser = factory.newSAXParser();
                parser.parse(newinfoPath, handler);
                listEventNewinfor = handler.getListEvent();
                posUnemployment = handler.getPosition();
                mapBorder = handler.getMapBorder();
                // get the GDP/population rate values.
                for (int i = 0; i < listEventNewinfor.size() - 2; i++) {
                    XMLEvent event = listEventNewinfor.get(i);
                    if (event.isStartElement()) {
                        String nodename = event.asStartElement().getName().toString();
                        if ("gdp_total".equals(nodename)) {
                            XMLEvent next = listEventNewinfor.get(i + 1);
                            dGdpProvince = Double.parseDouble(next.asCharacters().toString());
                            mapGdpProvince.put(nodename, dGdpProvince);
                        } else if (nodename.contains("gdp_")) {
                            XMLEvent next = listEventNewinfor.get(i + 1);
                            mapGdpProvince.put(nodename, Double.parseDouble(next.asCharacters().toString()));
                        } else if ("inflation".equals(nodename)) {
                            XMLEvent next = listEventNewinfor.get(i + 1);
                            mapGdpProvince.put(nodename, Double.parseDouble(next.asCharacters().toString()));
                        } else if ("population_growth".equals(nodename) || "infant_mortality".equals(nodename)
                                || "unemployment".equals(nodename)) {
                            XMLEvent next = listEventNewinfor.get(i + 1);
                            mapPoprateProvince.put(nodename, Double.parseDouble(next.asCharacters().toString()));
                        } else if ("ethnicgroup".equals(nodename) || "religion".equals(nodename) || "language".equals(nodename)) {
                            Attribute percent = (Attribute) listEventNewinfor.get(i + 1);
                            XMLEvent type = listEventNewinfor.get(i + 2);
                            mapPoprateProvince.put(type.asCharacters().toString(), Double.parseDouble(percent.getValue()));
                        }
                    }
                }
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String join(List<String> list, String d) {
            StringBuffer sb = new StringBuffer();
            for (String str : list) {
                sb.append(str + d);
            }
            return sb.substring(0, sb.length()-1);
        }
    }

    /**
     * Read the some information of original country.
     * 
     * @author Chenfeng Zhu
     *
     */
    protected class OrigcountryHandler extends DefaultHandler {

        private String countryname = null;
        private String carcode = null;
        private boolean isCountry = false;
        private boolean isCountryName = false;
        private boolean isProvince = false;
        private boolean isProvinceName = false;
        private boolean isTarget = false;
        private boolean flagDone = false;

        private boolean isArea = false;

        private String year;
        private boolean isPopulation;

        public OrigcountryHandler() {
            super();
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (flagDone) {
                return;
            }
            String elementName;
            if (qName == null || qName.equals("")) {
                elementName = localName;
            } else {
                elementName = qName;
            }
            if ("country".equals(elementName)) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    if ("car_code".equals(attributes.getQName(i))) {
                        this.carcode = attributes.getValue(i);
                    }
                }
                this.isCountry = true;
            } else if ("province".equals(elementName)) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    if (attributes.getQName(i).equals("id") && attributes.getValue(i).equals(provid)) {
                        this.isTarget = true;
                        carcodeOld = this.carcode; // get the car code of the original country
                        countrynameOrig = countryname;
                        break;
                    }
                }
                this.isProvince = true;
            } else if ("name".equals(elementName)) {
                if (isProvince && isTarget) {
                    this.isProvinceName = true;
                    this.isProvince = false;
                } else if (isCountry) {
                    this.isCountryName = true;
                    this.isCountry = false;
                }
            } else if (isTarget && "population".equals(elementName)) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    if (attributes.getQName(i).equals("year")) {
                        year = attributes.getValue(i);
                        break;
                    }
                }
                this.isPopulation = true;
            } else if (isTarget && "area".equals(elementName)) {
                this.isArea = true;
            } else if (isTarget && "city".equals(elementName)) {
                this.flagDone = true;
            }
        }

        public void characters(char ch[], int start, int length) throws SAXException {
            if (flagDone) {
                return;
            }
            String content = new String(ch, start, length);
            if (isCountryName) {
                countryname = content;
                this.isCountryName = false;
            } else if (isProvinceName) {
                countrynameNew = content;
                // this.flagDone = true;
                this.isProvinceName = false;
            } else if (isPopulation) {
                lPopProvince = Long.parseLong(content);
                mapPopProvince.put(year, lPopProvince);
                this.isPopulation = false;
            } else if (isArea) {
                dAreaProvince = Double.parseDouble(content);
            }
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (flagDone) {
                return;
            }
            String elementName;
            if (qName == null || qName.equals("")) {
                elementName = localName;
            } else {
                elementName = qName;
            }
            if ("country".equals(elementName)) {
                this.isCountry = false;
            } else if ("province".equals(elementName)) {
                this.isProvince = false;
                this.isTarget = false;
            }
        }

        public String getCarcode() {
            return this.carcode;
        }
    }

    /**
     * Read the information of the new country.
     * 
     * @author Chenfeng Zhu
     *
     */
    protected class NewinforHandler extends DefaultHandler {

        private XMLEventFactory eventFactory;
        private XMLEventWriter printer;
        private List<XMLEvent> listEvent = new ArrayList<XMLEvent>(0);
        private int position = 0;
        private Map<String, String> mapBorder = new HashMap<String, String>(0);

        public NewinforHandler() {
            super();
            try {
                XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
                eventFactory = XMLEventFactory.newInstance();
                printer = outputFactory.createXMLEventWriter(System.out);
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }

        public void startDocument() throws SAXException {
            XMLEvent event = eventFactory.createStartDocument();
            try {
                printer.add(event);
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            String elementName;
            if (qName == null || qName.equals("")) {
                elementName = localName;
            } else {
                elementName = qName;
            }
            XMLEvent event = eventFactory.createStartElement("", null, elementName);
            try {
                if ("newinfo".equals(elementName)) {
                    printer.add(event);
                } else if ("country".equals(elementName)) {
                    printer.add(event);
                    carcodeNew = attributes.getValue(0); // get the car code of the new country
                } else {
                    // printer.add(event);
                    listEvent.add(event);
                    String cname = null;
                    String clength = null;
                    for (int i = 0; i < attributes.getLength(); i++) {
                        event = eventFactory.createAttribute(attributes.getQName(i), attributes.getValue(i));
                        // printer.add(event);
                        listEvent.add(event);
                        if ("border".equals(elementName)) {
                            if ("country".equals(attributes.getQName(i))) {
                                cname = attributes.getValue(i);
                            } else if ("length".equals(attributes.getQName(i))) {
                                clength = attributes.getValue(i);
                            }
                        }
                    }
                    mapBorder.put(cname, clength);
                }
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }

        public void characters(char ch[], int start, int length) throws SAXException {
            String content = new String(ch, start, length);
            if ("".equals(content.trim())) {
                return;
            }
            XMLEvent event = eventFactory.createCharacters(content);
            listEvent.add(event);
            // try {
            // printer.add(event);
            // } catch (XMLStreamException e) {
            // e.printStackTrace();
            // }
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            String elementName;
            if (qName == null || qName.equals("")) {
                elementName = localName;
            } else {
                elementName = qName;
            }
            XMLEvent event = eventFactory.createEndElement("", null, elementName);
            try {
                if ("newinfo".equals(elementName)) {
                    printer.add(event);
                } else if ("country".equals(elementName)) {
                    printer.add(event);
                } else {
                    listEvent.add(event);
                    if ("unemployment".equals(elementName)) {
                        position = listEvent.size();
                    }
                    // printer.add(event);
                }
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }

        public void endDocument() throws SAXException {
            XMLEvent event = eventFactory.createEndDocument();
            try {
                printer.add(event);
                printer.flush();
                printer.close();
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }

        public List<XMLEvent> getListEvent() {
            return this.listEvent;
        }

        public int getPosition() {
            return this.position;
        }

        public Map<String, String> getMapBorder() {
            return this.mapBorder;
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
