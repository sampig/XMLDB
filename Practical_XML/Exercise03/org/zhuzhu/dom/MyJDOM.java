package org.zhuzhu.dom;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class MyJDOM {

    public static void main(String...strings) {

        String filepath = "/afs/informatik.uni-goettingen.de/course/xml-lecture/Mondial/mondial.xml";
        if (strings.length>0) {
            filepath = strings[0];
        }
        System.out.println("XML File: " + filepath);

        SAXBuilder builder = new SAXBuilder();
        File xmlFile = new File(filepath);

        try {
            Document document = (Document) builder.build(xmlFile);
            Element rootElement = document.getRootElement();
            
            System.out.println("Root element: " + rootElement.getName());
            System.out.println("----------------------------");

            List orgList = rootElement.getChildren("organization");
            for (Object obj: orgList) {
                Element orgElement = (Element) obj;
                    System.out.println("Org id : " + orgElement.getAttribute("id"));
                    System.out.println("Name: " + orgElement.getChildText("name"));
                    if (orgElement.getAttribute("headq") != null) {
                        System.out.println("Headquater: " + orgElement.getAttributeValue("headq"));
                    }
                    List membersList = orgElement.getChildren("members");
                    for (int mi = 0; mi < membersList.size(); mi++) {
                        Element memElement = (Element) membersList.get(mi);
                        System.out.print("" + memElement.getName() + ": ");
                            String members = memElement.getAttributeValue("country");
                            // System.out.print(members + "\t");
                            Object e = document.getProperty(members);
                            System.out.print(e);
                    }
                
                System.out.println("\n");
            }
        } catch (IOException io) {
            System.out.println(io.getMessage());
        } catch (JDOMException jdomex) {
            System.out.println(jdomex.getMessage());
        }
    }

}
