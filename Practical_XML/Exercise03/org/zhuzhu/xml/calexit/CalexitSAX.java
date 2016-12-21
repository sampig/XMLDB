/*
 * Copyright (c) 2016, Chenfeng Zhu. All rights reserved.
 * 
 */
package org.zhuzhu.xml.calexit;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
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

    private String sourcePath = "/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/lib/mondial.xml";
    private String ouptutPath = "/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/ex03/mondial_new_sax.xml";
    private String newinfoPath = "/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/ex03/calexit_new.xml";

    private String provid = "prov-United-States-6";

    private OutputStream outputStream;

    private String carcodeNew = "CAL";
    private String membershipsNew;

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

    protected class MondialHandler extends DefaultHandler2 {

        private XMLEventWriter writer;
        private XMLEventFactory eventFactory;
        private XMLEventWriter printer;

        private boolean isCountry = false;
        private boolean isProvince = false;
        private boolean isCalif = false;
        private boolean isArea = false;
        private int posCountry = 0;

        private List<XMLEvent> listEventCountry = new ArrayList<XMLEvent>(0);
        private List<XMLEvent> listEventCalif = new ArrayList<XMLEvent>(0);

        public MondialHandler() {
            super();
            try {
                XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
                eventFactory = XMLEventFactory.newInstance();
                writer = outputFactory.createXMLEventWriter(outputStream);
                printer = outputFactory.createXMLEventWriter(System.out);
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }

//        public void startDocument() throws SAXException {
//            System.out.println("startDocument");
//            XMLEvent event = eventFactory.createStartDocument();
//            try {
//                writer.add(event);
//            } catch (XMLStreamException e) {
//                e.printStackTrace();
//            }
//        }
        
        public void startDTD(String name, String publicId, String systemId) throws SAXException {
            System.out.println("startDTD");
            System.out.println(name);
            System.out.println(publicId);
            System.out.println(systemId);
        }
        
        public void endDTD() throws SAXException {
            System.out.println("endDTD");
        }
        
        public void startEntity(java.lang.String name) throws org.xml.sax.SAXException {
            System.out.println("startEntity");
            System.out.println(name);
        }

        public void processingInstruction(String target, String data) throws SAXException {
            System.out.println("processingInstruction");
            System.out.println(target);
            System.out.println(data);
        }

        public void notationDecl(String name, String publicId, String systemId) throws SAXException {
            System.out.println("notationDecl");
            System.out.println(name);
            System.out.println(publicId);
            System.out.println(systemId);
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            System.out.println("startPrefixMapping");
            System.out.println(prefix);
            System.out.println(uri);
        }

        public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
            System.out.println("unparsedEntityDecl");
            System.out.println(name);
            System.out.println(publicId);
            System.out.println(systemId);
            System.out.println(notationName);
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
                if ("country".equals(elementName)) { // country element
                    listEventCountry.add(event);
                    for (int i = 0; i < attributes.getLength(); i++) {
                        event = eventFactory.createAttribute(attributes.getQName(i), attributes.getValue(i));
                        listEventCountry.add(event);
                        if ("memberships".equals(attributes.getQName(i))) {
                            membershipsNew = attributes.getValue(i);
                        }
                    }
                    isCountry = true;
                } else if ("province".equals(elementName)) { // province element
                    isProvince = true;
                    for (int i = 0; i < attributes.getLength(); i++) {
                        if (attributes.getQName(i).equals("id") && attributes.getValue(i).equals(provid)) {
                            isCalif = true;
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
                                event = eventFactory.createAttribute(attributes.getQName(i), attributes.getValue(i));
                                listEventCalif.add(event);
                            }
                        }
                        event = eventFactory.createAttribute("memberships", membershipsNew); // attr: car_code
                        listEventCalif.add(event);
                    } else { // if it is a normal province
                        listEventCountry.add(event);
                        for (int i = 0; i < attributes.getLength(); i++) {
                            event = eventFactory.createAttribute(attributes.getQName(i), attributes.getValue(i));
                            listEventCountry.add(event);
                        }
                    }
                } else {
                    if (isCalif) {
                        if (!"area".equals(elementName)) {
                            listEventCalif.add(event);
                            for (int i = 0; i < attributes.getLength(); i++) {
                                event = eventFactory.createAttribute(attributes.getQName(i), attributes.getValue(i));
                                listEventCalif.add(event);
                            }
                            isArea = false;
                        } else {
                            isArea = true;
                        }
                    } else if (isCountry) {
                        listEventCountry.add(event);
                        for (int i = 0; i < attributes.getLength(); i++) {
                            event = eventFactory.createAttribute(attributes.getQName(i), attributes.getValue(i));
                            listEventCountry.add(event);
                        }
                    } else { // other elements
                        writer.add(event);
                        for (int i = 0; i < attributes.getLength(); i++) {
                            event = eventFactory.createAttribute(attributes.getQName(i), attributes.getValue(i));
                            writer.add(event);
                        }
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
                if (isCalif) {
                    if (!isArea) {
                        listEventCalif.add(event);
                    } else {
                        event = eventFactory.createAttribute("area", content); // attr: area
                        listEventCalif.add(posCountry + 1, event);
                    }
                } else if (isCountry) {
                    listEventCountry.add(event);
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
                if ("country".equals(elementName)) {
                    listEventCountry.add(event);
                    for (XMLEvent e : listEventCountry) {
                        writer.add(e);
                    }
                    listEventCountry = new ArrayList<XMLEvent>(0);
                    // listEventCalif = new ArrayList<XMLEvent>(0);
                    isCountry = false;
                    isProvince = false;
                    isCalif = false;
                } else if ("province".equals(elementName)) {
                    if (isCalif) {
                        event = eventFactory.createEndElement("", null, "country");
                        listEventCalif.add(event);
                    } else {
                        listEventCountry.add(event);
                    }
                    isProvince = false;
                    isCalif = false;
                } else {
                    if (isCalif) {
                        if (!isArea) {
                            listEventCalif.add(event);
                        }
                    } else if (isCountry) {
                        listEventCountry.add(event);
                    } else {
                        writer.add(event);
                    }
                }
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }

        public void endDocument() throws SAXException {
            XMLEvent event = eventFactory.createEndDocument();
            try {
                for (XMLEvent e : listEventCalif) {
                    printer.add(e);
                }
                //printer.add(event);
                printer.flush();
                printer.close();
                writer.flush();
                writer.close();
                // System.out.println("New car_code: " + carcodeNew);
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }

        public void addNewinfor() {
            NewinforHandler handler = new NewinforHandler();
            SAXParserFactory factory = SAXParserFactory.newInstance();
            try {
                SAXParser parser = factory.newSAXParser();
                parser.parse(newinfoPath, handler);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected class NewinforHandler extends DefaultHandler {

        private XMLEventWriter writer;
        private XMLEventFactory eventFactory;
        private XMLEventWriter printer;

        public NewinforHandler() {
            super();
            try {
                XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
                eventFactory = XMLEventFactory.newInstance();
                writer = outputFactory.createXMLEventWriter(outputStream);
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
                    carcodeNew = attributes.getValue(0);
                } else {
                    writer.add(event);
                    for (int i = 0; i < attributes.getLength(); i++) {
                        event = eventFactory.createAttribute(attributes.getQName(i), attributes.getValue(i));
                        writer.add(event);
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
                writer.add(event);
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
                if ("newinfo".equals(elementName)) {
                    printer.add(event);
                } else if ("country".equals(elementName)) {
                    printer.add(event);
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
                System.out.println("New car_code: " + carcodeNew);
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }
    }

}
