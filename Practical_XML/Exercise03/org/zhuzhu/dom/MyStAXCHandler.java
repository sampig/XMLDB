/*
 * Copyright (c) 2016, Chenfeng Zhu. All rights reserved.
 * 
 */
package org.zhuzhu.dom;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Read data from a pipe.
 * 
 * @author Chenfeng Zhu
 *
 */
public class MyStAXCHandler implements Runnable {

    private InputStream inputStream;
    private OutputStream outputStream;

    private List<String> listOrg = new ArrayList<String>(0);
    private Map<String, Integer> mapCount = new HashMap<String, Integer>(0);

    private String filter = "city";

    public MyStAXCHandler(InputStream input, OutputStream output) {
        this.inputStream = input;
        this.outputStream = output;
    }

    @Override
    public void run() {
        this.log("Start reading...");
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader reader = inputFactory.createXMLStreamReader(inputStream);
            boolean goOn = true;
            String localname = null;
            while (goOn) {
                int event = 0;
                try {
                    event = reader.next();
                    switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        localname = reader.getLocalName();
                        if (!filter.equals(localname)) {
                            System.out.println("Read start element " + localname);
                            String attr = reader.getAttributeValue(null, "name");
                            if (attr != null && !"".equalsIgnoreCase(attr)) {
                                System.out.println("Read element attribute: " + attr);
                            }
                            if ("organization".equals(localname)) {
                                listOrg.add(attr);
                            }
                        }
                        Integer c = mapCount.get(localname);
                        mapCount.put(localname, (c == null) ? 1 : c + 1);
                        break;
                    case XMLStreamConstants.CHARACTERS: // all elements are empty
                        System.out.println("Read " + reader.getText());
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        localname = reader.getLocalName();
                        if (!filter.equals(localname)) {
                            System.out.println("Read end element " + localname);
                        }
                        break;
                    case XMLStreamConstants.END_DOCUMENT: // never happens!
                        this.log("Read end document");
                        goOn = false;
                    default:
                        System.out.println("Read something else. event: " + event);
                    }
                } catch (Exception e) {
                    reader.close();
                    goOn = false;
                }
            }
            reader.close();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        OutputStreamWriter oswriter = new OutputStreamWriter(outputStream);
        try {
            oswriter.write("Total organization: " + listOrg.size() + "\n");
            oswriter.write("Total element after filtering: " + mapCount + "\n");
            oswriter.flush();
            oswriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.log("Finish reading.");
    }

    private void log(String text) {
        System.out.println("MyStAXCHandler-Reader: " + text);
    }

}
