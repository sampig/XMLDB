/*
 * Copyright (c) 2016, Chenfeng Zhu. All rights reserved.
 * 
 */
package org.zhuzhu.dom;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

/**
 * Practical XML Exercise 03.<br/>
 * DOM basic with JDOM-1.1.3.
 * 
 * @author Chenfeng Zhu
 *
 */
public class MyJDOM {

    private static String MONDIAL_FILEPATH = "/afs/informatik.uni-goettingen.de/course/xml-lecture/Mondial/mondial.xml";
    private static String OUTPUT_FILEPATH = "/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/ex03/ex03_02j.html";

    public static void main(String... strings) {
        String filepath = null;
        String outputFile = null;
        if (strings.length > 0) {
            filepath = strings[0];
        }
        if (strings.length >= 2) {
            outputFile = strings[1];
        }

        MyJDOM jdom = new MyJDOM();

        System.out.println("================================");
        System.out.println("Exercise 03_1");
        jdom.exercise3_1(filepath);
        System.out.println("================================\n\n");

        System.out.println("================================");
        System.out.println("Exercise 03_2");
        jdom.exercise3_2(filepath, outputFile);
        System.out.println("================================");

    }

    /**
     * DOM Basics.
     * 
     * @param source
     *            The path for mondial.xml file
     */
    public void exercise3_1(String source) {
        String filepath = MONDIAL_FILEPATH;
        if (source != null && !("".equalsIgnoreCase(source))) {
            filepath = source;
        }
        System.out.println("Source XML File: " + filepath);

        SAXBuilder builder = new SAXBuilder();
        File xmlFile = new File(filepath);

        try {
            Document document = builder.build(xmlFile);
            Element rootElement = document.getRootElement();

            System.out.println("Root element: " + rootElement.getName());
            System.out.println("----------------------------\n\n");

            @SuppressWarnings("unchecked")
            List<Element> orgList = rootElement.getChildren("organization");
            @SuppressWarnings("unchecked")
            List<Element> countryList = (List<Element>) rootElement.getChildren("country");
            int count = 0;
            for (Element orgElement : orgList) {
                // Element orgElement = (Element) obj;
                String head = null;
                String hCarcode = null;
                String headq = null;
                if (orgElement.getAttribute("headq") != null) {
                    headq = orgElement.getAttributeValue("headq");
                } else {
                    continue;
                }
                boolean flag = false;
                @SuppressWarnings("unchecked")
                List<Element> membersList = orgElement.getChildren("members");
                for (Element country : countryList) {
                    String cap = country.getAttributeValue("capital");
                    String carcode = country.getAttributeValue("car_code");
                    if (!headq.equalsIgnoreCase(cap)) {
                        continue;
                    }
                    for (Element memElement : membersList) {
                        // System.out.print("" + memElement.getName() + ": ");
                        String membersAttr = memElement.getAttributeValue("country");
                        String[] mArr = membersAttr.split(" ");
                        for (String member : mArr) {
                            // System.out.print(member + " ");
                            if (carcode.equalsIgnoreCase(member)) {
                                flag = true;
                                count++;
                                hCarcode = carcode;
                                @SuppressWarnings("unchecked")
                                List<Element> cities = country.getChildren("city");
                                for (Element city : cities) {
                                    String cityID = city.getAttributeValue("id");
                                    if (headq.equalsIgnoreCase(cityID)) {
                                        head = city.getChildText("name");
                                        break;
                                    }
                                }
                                if (head != null) {
                                    break;
                                }
                                @SuppressWarnings("unchecked")
                                List<Element> provinces = country.getChildren("province");
                                for (Element province : provinces) {
                                    @SuppressWarnings("unchecked")
                                    List<Element> pcities = province.getChildren("city");
                                    for (Element city : pcities) {
                                        String cityID = city.getAttributeValue("id");
                                        if (headq.equalsIgnoreCase(cityID)) {
                                            head = city.getChildText("name");
                                            break;
                                        }
                                    }
                                    if (head != null) {
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        if (flag) {
                            break;
                        }
                    }
                    if (flag) {
                        break;
                    }
                }
                if (flag) {
                    System.out.println("Org ID: " + orgElement.getAttributeValue("id"));
                    System.out.println("Org Name: " + orgElement.getChildText("name"));
                    head += " (" + headq + ") (" + hCarcode + ")";
                    System.out.println("Headquater: " + head);
                    System.out.println("\n");
                }
            }
            System.out.println("Total: " + count + "/" + orgList.size());
            // System.out.println(countryList.size());
            // System.out.println(orgList.size());
        } catch (IOException io) {
            System.out.println(io.getMessage());
        } catch (JDOMException jdomex) {
            System.out.println(jdomex.getMessage());
        }
    }

    /**
     * DOM: Creation of a Statistics Table.
     * 
     * @param source
     *            The path for mondial.xml file
     * @param target
     *            The path for the output file
     */
    public void exercise3_2(String source, String target) {
        try {
            // source:
            String filepath = MONDIAL_FILEPATH;
            if (source != null && !("".equalsIgnoreCase(source))) {
                filepath = source;
            }
            System.out.println("Source XML File: " + filepath);
            File sourceFile = new File(filepath);
            SAXBuilder builder = new SAXBuilder();
            // "com.sun.org.apache.xerces.internal.parsers.SAXParser"
            Document sourceDoc = builder.build(sourceFile);
            Element sourceElement = sourceDoc.getRootElement();

            // target:
            if (target == null || "".equalsIgnoreCase(target)) {
                target = OUTPUT_FILEPATH;
            }
            if (!(new File(target)).exists()) {
                (new File(target)).createNewFile();
            }
            Element rootElement = new Element("table");
            rootElement.setAttribute("border", "1");
            Document targetDoc = new Document(rootElement);

            // set header
            Element theadElement = new Element("tr");
            Element stephElement = new Element("th");
            stephElement.setText("Steps");
            Element counthElement = new Element("th");
            counthElement.setText("Count");
            Element totalhElement = new Element("th");
            totalhElement.setText("Total");
            Element listElement = new Element("th");
            listElement.setText("Some samples");
            theadElement.addContent(stephElement);
            theadElement.addContent(counthElement);
            theadElement.addContent(totalhElement);
            theadElement.addContent(listElement);

            targetDoc.getRootElement().addContent(theadElement);

            // steps of 100,000
            int step = 100000;
            int stepNum = 223;
            XPath xpathCount;
            for (int i = 0; i <= stepNum; i++) {
                int start = i * step;
                int end = (i + 1) * step;
                String strCount = "count(//city[population[last()]>=" + start + " and population[last()]<" + end + "])";
                // "count(//city[population[last()]>=$start and population[last()]<$end])";
                // start and end cannot be used as variable names
                xpathCount = XPath.newInstance(strCount);
                // xpathCount.setVariable("start", start);
                // xpathCount.setVariable("end", end);
                String count = String.valueOf(((Double) xpathCount.selectSingleNode(sourceElement)).intValue());
                // ((Element) xpathCount.selectSingleNode(sourceElement)).getText();
                String strSum = "sum(//city[population[last()]>=$vstart and population[last()]<$vend]/population[last()])";
                XPath xpathSum = XPath.newInstance(strSum);
                xpathSum.setVariable("vstart", String.valueOf(start));
                xpathSum.setVariable("vend", String.valueOf(end));
                String sum = String.valueOf(((Double) xpathSum.selectSingleNode(sourceElement)).intValue());
                // ((Element) xpathSum.selectSingleNode(sourceElement)).getText();
                Element trElement = new Element("tr");
                trElement.addContent((new Element("td")).setText(start + " ~ " + end));
                trElement.addContent((new Element("td")).setText(count));
                trElement.addContent((new Element("td")).setText(sum));
                String samples = "";
                StringBuffer sb = new StringBuffer();
                if (!("0".equalsIgnoreCase(count))) {
                    String xpathStr = "//city[population[last()]>=$vstart and population[last()]<$vend]";
                    XPath xpathList = XPath.newInstance(xpathStr);
                    xpathList.setVariable("vstart", String.valueOf(start));
                    xpathList.setVariable("vend", String.valueOf(end));
                    @SuppressWarnings("unchecked")
                    List<Element> cityList = (List<Element>) xpathList.selectNodes(sourceElement);
                    for (int j = 0; j < ((cityList.size() >= 5) ? 5 : cityList.size()); j++) {
                        Element cityElement = cityList.get(j);
                        sb.append(cityElement.getChildText("name") + ",");
                    }
                }
                if (sb.length() > 1) {
                    samples = sb.substring(0, sb.length() - 1);
                } else {
                    samples = "NULL";
                }
                trElement.addContent((new Element("td")).setText(samples));
                targetDoc.getRootElement().addContent(trElement);
            }

            XMLOutputter xmloutputter = new XMLOutputter();
            xmloutputter.setFormat(Format.getPrettyFormat());
            xmloutputter.output(targetDoc, new FileWriter(target));
            // xmloutputter.output(targetDoc, System.out);
            System.out.println("**Write to File: " + target);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
