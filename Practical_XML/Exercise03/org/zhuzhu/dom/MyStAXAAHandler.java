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

//import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;

/**
 * Output the names of all countries into a file.
 * 
 * @author Chenfeng Zhu
 *
 */
public class MyStAXAAHandler {

    private FileInputStream inputStream;
    private OutputStream outputStream;

    private List<String> listNames = new ArrayList<String>(0);

    public MyStAXAAHandler(FileInputStream input, OutputStream output) {
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

            listNames = new ArrayList<String>(0);
            boolean isCountry = false;
            int posName = 0;

            if (reader.getEventType() == XMLStreamConstants.START_DOCUMENT) {
                System.out.println("Start Document");
                writer.writeStartDocument("utf-8", "1.0");
                writer.writeStartElement("table");
                writer.writeAttribute("border", "1");
                writer.writeStartElement("tr");
                writer.writeStartElement("th");
                writer.writeCharacters("name");
                writer.writeEndElement(); // th
                writer.writeEndElement(); // tr
            }

            while (reader.hasNext()) {
                int event = reader.next();
                switch (event) {
                case XMLStreamConstants.END_DOCUMENT:
                    System.out.println("End Document: " + listNames.size());
                    writer.writeEndElement(); // table
                    listNames = new ArrayList<String>(0);
                    reader.close();
                    writer.flush();
                    writer.close();
                    break;
                case XMLStreamConstants.START_ELEMENT:
                    if ("country".equals(reader.getLocalName())) {
                        isCountry = true;
                        break;
                    }
                    if (isCountry && "name".equals(reader.getLocalName()) && posName == 0) {
                        String name = reader.getElementText();
                        listNames.add(name);
                        writer.writeStartElement("tr");
                        writer.writeStartElement("td");
                        writer.writeCharacters(name);
                        writer.writeEndElement(); // td
                        writer.writeEndElement(); // tr
                        posName++;
                        break;
                    }
                case XMLStreamConstants.END_ELEMENT:
                    if ("country".equals(reader.getLocalName())) {
                        isCountry = false;
                        posName = 0;
                        break;
                    }
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

}
