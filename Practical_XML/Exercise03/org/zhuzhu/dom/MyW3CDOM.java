/*
 * Copyright (c) 2016, Chenfeng Zhu. All rights reserved.
 * 
 */
package org.zhuzhu.dom;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Practical XML Exercise 03.<br/>
 * DOM basic with W3C DOM.
 * 
 * @author Chenfeng Zhu
 *
 */
public class MyW3CDOM {

    private static String MONDIAL_FILEPATH = "/afs/informatik.uni-goettingen.de/course/xml-lecture/Mondial/mondial.xml";
    private static String OUTPUT_FILEPATH = "/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/ex03/ex03_02w.html";

    public static void main(String... strings) {
        String filepath = null;
        String outputFile = null;
        if (strings.length > 0) {
            filepath = strings[0];
        }
        if (strings.length >= 2) {
            outputFile = strings[1];
        }
        MyW3CDOM mydom = new MyW3CDOM();

        System.out.println("================================");
        System.out.println("Exercise 03_1");
        mydom.exercise3_1(filepath);
        System.out.println("================================\n\n");

        System.out.println("================================");
        System.out.println("Exercise 03_2");
        mydom.exercise3_2(filepath, outputFile);
        System.out.println("================================");
    }

    /**
     * DOM Basics.
     * 
     * @param source
     *            The path for mondial.xml file
     */
    public void exercise3_1(String source) {
        try {
            String filepath = MONDIAL_FILEPATH;
            if (source != null && !("".equalsIgnoreCase(source))) {
                filepath = source;
            }
            System.out.println("XML File: " + filepath);

            File xmlFile = new File(filepath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(xmlFile);

            // doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            System.out.println("----------------------------\n\n");

            NodeList orgList = doc.getElementsByTagName("organization");
            int count = 0;
            for (int oi = 0; oi < orgList.getLength(); oi++) {
                Node orgNode = orgList.item(oi);
                // System.out.println("" + orgNode.getNodeName() + ":");
                if (orgNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element orgElement = (Element) orgNode;
                    Element headqElement = null;
                    String hCarCode = null;
                    if (orgElement.hasAttribute("headq")) {
                        String headq = orgElement.getAttribute("headq");
                        headqElement = doc.getElementById(headq);
                        Node hNode = headqElement.getParentNode();
                        while (!"country".equalsIgnoreCase(hNode.getNodeName())) {
                            hNode = hNode.getParentNode();
                        }
                        Element hElement = (Element) hNode;
                        // System.out.print(!"country".equalsIgnoreCase(hNode.getNodeName()));
                        // System.out.println(hNode.getNodeName() + ":" +
                        // hElement.getAttribute("car_code"));
                        hCarCode = hElement.getAttribute("car_code");
                        if (!headq.equalsIgnoreCase(hElement.getAttribute("capital"))) {
                            continue;
                        }
                    }
                    NodeList membersList = orgElement.getElementsByTagName("members");
                    boolean flag = false;
                    String members = null;
                    for (int mi = 0; mi < membersList.getLength(); mi++) {
                        Node memNode = membersList.item(mi);
                        // System.out.print("" + memNode.getNodeName() + ": ");
                        if (memNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element memElement = (Element) memNode;
                            members = memElement.getAttribute("country");
                            // System.out.print(members + "\t");
                            // Element e = doc.getElementById(members);
                            // System.out.print(e + "\t");
                            if (members.startsWith(hCarCode + " ") || members.endsWith(" " + hCarCode)
                                    || members.equalsIgnoreCase(hCarCode) || members.contains(" " + hCarCode + " ")) {
                                flag = true;
                                count++;
                                break;
                            }
                        }
                    }
                    if (flag) {
                        System.out.println("Org ID: " + orgElement.getAttribute("id"));
                        String orgName = orgElement.getElementsByTagName("name").item(0).getTextContent();
                        System.out.println("Org Name: " + orgName);
                        String headq = headqElement.getElementsByTagName("name").item(0).getTextContent();
                        headq += " (" + headqElement.getAttribute("id") + ")";
                        System.out.println("Headquater: " + headq + " (" + hCarCode + ")");
                        System.out.println("Members: " + members);
                        System.out.println("\n");
                    }
                }
            }
            System.out.println("Total: " + count + "/" + orgList.getLength());
        } catch (Exception e) {
            e.printStackTrace();
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
            File xmlFile = new File(filepath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
            Document sourceDoc = docBuilder.parse(xmlFile);
            Element sourceRoot = sourceDoc.getDocumentElement();
            // System.out.println(sourceDoc);

            // target:
            if (target == null) {
                target = OUTPUT_FILEPATH;
            }
            if (!(new File(target)).exists()) {
                (new File(target)).createNewFile();
            }
            Document targetDoc = docBuilder.newDocument();
            // System.out.println(targetDoc);

            System.out.println("----------------------------\n\n");
            XPath xPath = XPathFactory.newInstance().newXPath();

            Element rootElement = targetDoc.createElement("table");
            Attr borderAttr = targetDoc.createAttribute("border");
            borderAttr.setValue("1");
            rootElement.setAttributeNode(borderAttr);
            targetDoc.appendChild(rootElement);

            // set header
            Element theadElement = targetDoc.createElement("tr");
            Element stepElement = targetDoc.createElement("th");
            stepElement.setTextContent("Steps");
            Element countElement = targetDoc.createElement("th");
            countElement.setTextContent("Count");
            Element totalElement = targetDoc.createElement("th");
            totalElement.setTextContent("Total");
            Element listElement = targetDoc.createElement("th");
            listElement.setTextContent("Some samples");
            theadElement.appendChild(stepElement);
            theadElement.appendChild(countElement);
            theadElement.appendChild(totalElement);
            theadElement.appendChild(listElement);
            rootElement.appendChild(theadElement);

            // steps of 100,000
            int step = 100000;
            int stepNum = 223;
            for (int i = 0; i <= stepNum; i++) {
                int start = i * step;
                int end = (i + 1) * step;
                String xpathCount = "count(//city[population[last()]>=" + start + " and population[last()]<" + end + "])";
                String count = xPath.evaluate(xpathCount, sourceRoot);
                String xpathSum = "sum(//city[population[last()]>=" + start + " and population[last()]<" + end + "]/population[last()])";
                String sum = xPath.evaluate(xpathSum, sourceRoot);
                Element trElement = targetDoc.createElement("tr");
                Element steptdElement = targetDoc.createElement("td");
                steptdElement.setTextContent(start + " ~ " + end);
                Element counttdElement = targetDoc.createElement("td");
                counttdElement.setTextContent(count);
                Element totaltdElement = targetDoc.createElement("td");
                totaltdElement.setTextContent(sum);
                Element listtdElement = targetDoc.createElement("td");
                String samples = "";
                StringBuffer sb = new StringBuffer();
                if (!("0".equalsIgnoreCase(count))) {
                    String xpathStr = "//city[population[last()]>=" + start + " and population[last()]<" + end + "]";
                    NodeList cityList = (NodeList) xPath.evaluate(xpathStr, sourceRoot, XPathConstants.NODESET);
                    // System.out.println(cityList.getLength());
                    for (int j = 0; j < ((cityList.getLength() >= 5) ? 5 : cityList.getLength()); j++) {
                        Node cityNode = cityList.item(j);
                        if (cityNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element cityElement = (Element) cityNode;
                            sb.append(cityElement.getElementsByTagName("name").item(0).getTextContent() + ",");
                        }
                    }
                }
                if (sb.length() > 1) {
                    samples = sb.substring(0, sb.length() - 1);
                } else {
                    samples = "NULL";
                }
                // System.out.println(samples);
                listtdElement.setTextContent(samples);
                trElement.appendChild(steptdElement);
                trElement.appendChild(counttdElement);
                trElement.appendChild(totaltdElement);
                trElement.appendChild(listtdElement);
                rootElement.appendChild(trElement);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            targetDoc.getDocumentElement().normalize();
            DOMSource targetDOM = new DOMSource(targetDoc);
            File targetFile = new File(target);
            StreamResult result = new StreamResult(targetFile);
            // StreamResult result = new StreamResult(System.out);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf8");
            transformer.transform(targetDOM, result);
            System.out.println("**Write to File: " + target);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
