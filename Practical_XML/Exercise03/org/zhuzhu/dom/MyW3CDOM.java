package org.zhuzhu.dom;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

public class MyW3CDOM {

    public static void main(String... strings) {
        String filepath = null;
        if (strings.length > 0) {
            filepath = strings[0];
        }

        MyW3CDOM mydom = new MyW3CDOM();
        mydom.exercise3_1(filepath);

    }

    public void exercise3_1(String string) {
        try {
            String filepath = "/afs/informatik.uni-goettingen.de/course/xml-lecture/Mondial/mondial.xml";
            if (string != null && !("".equalsIgnoreCase(string))) {
                filepath = string;
            }
            System.out.println("XML File: " + filepath);

            File xmlFile = new File(filepath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(xmlFile);

            // doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            System.out.println("----------------------------\n");

            NodeList orgList = doc.getElementsByTagName("organization");
            int count = 0;
            for (int oi = 0; oi < orgList.getLength(); oi++) {
                Node orgNode = orgList.item(oi);
                // System.out.println("" + orgNode.getNodeName() + ":");
                if (orgNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element orgElement = (Element) orgNode;
                    String hCarCode = null;
                    if (orgElement.hasAttribute("headq")) {
                        String headq = orgElement.getAttribute("headq");
                        Element h = doc.getElementById(headq);
                        Node hNode = h.getParentNode();
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
                    for (int mi = 0; mi < membersList.getLength(); mi++) {
                        Node memNode = membersList.item(mi);
                        // System.out.print("" + memNode.getNodeName() + ": ");
                        if (memNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element memElement = (Element) memNode;
                            String members = memElement.getAttribute("country");
                            // System.out.print(members + "\t");
                            // Element e = doc.getElementById(members);
                            // System.out.print(e + "\t");
                            if (members.startsWith(hCarCode + " ") || members.endsWith(" " + hCarCode)
                                    || members.equalsIgnoreCase(hCarCode) || members.contains(" " + hCarCode + " ")) {
                                System.out.println("members: " + members);
                                flag = true;
                                count++;
                                break;
                            }
                        }
                    }
                    if (flag) {
                        System.out.println("Org id : " + orgElement.getAttribute("id"));
                        System.out.println("Name: " + orgElement.getElementsByTagName("name").item(0).getTextContent());
                        System.out.println("Headquater: " + orgElement.getAttribute("headq") + " (" + hCarCode + ")");
                        System.out.println("\n");
                    }
                }
            }
            System.out.println("Total: " + count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
