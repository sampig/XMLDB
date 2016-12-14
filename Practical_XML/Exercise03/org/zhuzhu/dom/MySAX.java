/*
 * Copyright (c) 2016, Chenfeng Zhu. All rights reserved.
 * 
 */
package org.zhuzhu.dom;

import java.io.IOException;
// import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class MySAX {

    private static String MONDIAL_FILEPATH = "/usr/workspace/xml/mondial.xml";
    private static String OUTPUT_FILEPATH = "/usr/workspace/xml/ex03_03a.html";

    public static void main(String... strings) {
        String sourcePath = null;
        String outputFile = null;
        if (strings.length > 0) {
            sourcePath = strings[0];
        }
        if (strings.length >= 2) {
            outputFile = strings[1];
        }

        MySAX mySAX = new MySAX();

        System.out.println("================================");
        System.out.println("Exercise 03_3a");
        mySAX.exercise3_3a(sourcePath, outputFile);
        System.out.println("================================\n\n");

        System.out.println("================================");
        System.out.println("Exercise 03_3b");
        mySAX.exercise3_3b(sourcePath);
        System.out.println("================================\n\n");
    }

    public void exercise3_3a(String source, String target) {
        // source:
        String filepath = MONDIAL_FILEPATH;
        if (source != null && !("".equalsIgnoreCase(source))) {
            filepath = source;
        }
        System.out.println("Source XML File: " + filepath);

        // target:
        if (target == null || "".equalsIgnoreCase(target)) {
            target = OUTPUT_FILEPATH;
        }
        
        MySAXAHandler handler = new MySAXAHandler(target);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            parser.parse(filepath, handler);
            // List<String> list = handler.getAllCountriesName();
            // System.out.println("Countries: " + list.size());
            // System.out.println("<table border='1'>");
            // System.out.println("<tr><th>Name</th></tr>");
            // for (String name : list) {
            // System.out.println("<tr><td>" + name + "</td></tr>");
            // }
            // System.out.println("</table>");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void exercise3_3b(String source) {
        // source:
        String filepath = MONDIAL_FILEPATH;
        if (source != null && !("".equalsIgnoreCase(source))) {
            filepath = source;
        }
        System.out.println("Source XML File: " + filepath);
        
        MySAXBHandler handler = new MySAXBHandler();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            parser.parse(filepath, handler);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
