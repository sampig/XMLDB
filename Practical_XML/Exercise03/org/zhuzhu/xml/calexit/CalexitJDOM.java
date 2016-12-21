package org.zhuzhu.xml.calexit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class CalexitJDOM {

    public enum SearchBy {
        id, name
    }

    public enum SearchElement {
        province, city
    }

    private String dtdPath = "/usr/workspace/xml/mondial.dtd";
    private String sourcePath = "/usr/workspace/xml/mondial.xml";
    private String ouptutPath = "/usr/workspace/xml/mondial_new.xml";
    private String newinfoPath = "/usr/workspace/xml/calexit_new.xml";

    private final static String CALIF_ID = "prov-United-States-6";

    private Document originalDocument;

    private Document newinforDocument;
    private Element newinforElement;

    private Document newDocument;
    private Element newRootElement;
    private Element newCountryElement;

    private List<String> listTopElementName = new ArrayList<String>(0);

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

        CalexitJDOM cjdom = new CalexitJDOM(filepath, outputFile, newinforFile);
        cjdom.read();
        cjdom.loadTopElementNames();
        cjdom.change(SearchElement.province, SearchBy.id, null);
        // cjdom.change(SearchElement.province, SearchBy.name, "California");
        // cjdom.specifyCalifornia();
        cjdom.writeToFile();
        cjdom.validate();
    }

    public CalexitJDOM() {
    }

    public CalexitJDOM(String source, String target, String newinfor) {
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
    }

    public void read() {
        SAXBuilder builder = new SAXBuilder();
        try {
            originalDocument = builder.build(new File(sourcePath));
            newDocument = (Document) originalDocument.clone();
            newRootElement = newDocument.getRootElement();
            System.out.println("Root element: " + newRootElement.getName());
            newinforDocument = builder.build(new File(newinfoPath));
            newinforElement = (Element) newinforDocument.getRootElement().clone();
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadTopElementNames() {
        // listTopElementName.add("country");
        // listTopElementName.add("continent");
        // listTopElementName.add("organization");
        listTopElementName.add("sea");
        listTopElementName.add("river");
        listTopElementName.add("lake");
        listTopElementName.add("island");
        listTopElementName.add("mountain");
        listTopElementName.add("desert");
        // listTopElementName.add("airport");
        // Expected node-type
        // this doesn't work.
        // String str = "distinct-values(/child::node()/name())";
        // try {
        // XPath xpath = XPath.newInstance(str);
        // @SuppressWarnings("unchecked")
        // List<Element> list = (List<Element>) xpath.selectNodes(newDocument);
        // for (Element e:list) {
        // System.out.println(e);
        // }
        // } catch (JDOMException e) {
        // e.printStackTrace();
        // }
    }

    public void change(SearchElement searche, SearchBy searchby, String keyword) {
        try {
            if (keyword == null || "".equals(keyword)) {
                keyword = CALIF_ID;
            }
            String strProvCalif = "//province[@id='" + CALIF_ID + "']";
            switch (searche) {
            case province:
                strProvCalif = "//province[";
                break;
            case city:
                strProvCalif = "//city[";
                break;
            default:
                strProvCalif = "//province[";
                break;
            }
            switch (searchby) {
            case id:
                strProvCalif += "@id='" + keyword + "']";
                break;
            case name:
                strProvCalif += "name='" + keyword + "']";
                break;
            default:
                strProvCalif += "@id='" + keyword + "']";
                break;
            }
            System.out.println(strProvCalif);
            XPath xpathProvCalif = XPath.newInstance(strProvCalif);
            Element e = (Element) xpathProvCalif.selectSingleNode(newRootElement);

            String idOld = e.getAttributeValue("id");
            List<String> listCityidOld = new ArrayList<String>(0);
            String carcodeOld = e.getParentElement().getAttributeValue("car_code");
            String carcodeNew = newinforElement.getChild("country").getAttributeValue("car_code");
            String countryname = e.getParentElement().getChildText("name").replace(" ", "-");
            String countrynameNew = e.getChildText("name").replace(" ", "-");

            newCountryElement = (Element) e.clone();
            newCountryElement.detach();

            // Change its attributes and children.
            newCountryElement.setName("country");
            newCountryElement.setAttribute("car_code", carcodeNew);
            newCountryElement.setAttribute("area", newCountryElement.getChildText("area"));
            String capitalNew = newCountryElement.getAttributeValue("capital").replace(countryname, countrynameNew);
            newCountryElement.setAttribute("capital", capitalNew);
            String memberships = e.getParentElement().getAttributeValue("memberships");
            if (true) {
                memberships = memberships.replace("org-G-5", "");
                memberships = memberships.replace("org-G-7", "");
                memberships = memberships.replace("  ", " ").trim();
            }
            newCountryElement.setAttribute("memberships", memberships);
            newCountryElement.removeAttribute("id");
            newCountryElement.removeAttribute("country");
            newCountryElement.removeChild("area");

            // Change its cities.
            @SuppressWarnings("unchecked")
            List<Element> cityList = (List<Element>) newCountryElement.getChildren("city");
            for (Element city : cityList) {
                listCityidOld.add(city.getAttributeValue("id"));
                city.removeAttribute("province");
                city.setAttribute("id", city.getAttributeValue("id").replace(countryname, countrynameNew));
                city.setAttribute("country", carcodeNew);
            }

            // Add new information for it.
            int pos = newCountryElement.indexOf(newCountryElement.getChild("city"));
            @SuppressWarnings("unchecked")
            List<Element> newinforList = (List<Element>) newinforElement.getChild("country").getChildren();
            for (int i = 0; i <= newinforList.size() - 1; i++) {
                newCountryElement.addContent(pos + i, ((Element) newinforList.get(i).clone()).detach());
            }

            System.out.println(idOld);
            // System.out.println(e);
            // System.out.println(newCountryElement);
            int posContinent = newRootElement.indexOf(newRootElement.getChild("continent"));
            newRootElement.addContent(posContinent, newCountryElement);

            // Remove the province.
            e.getParent().removeContent(e);

            // Change IDREF: located
            // ID and IDREF don't work.
            // //idref("prov-United-States-6")/ancestor::sea
            // //idref('prov-United-States-6')/parent::node()[name()!='city']
            String strNature = "//located"; // count(//located): 2287
            XPath xpathNature = XPath.newInstance(strNature);
            @SuppressWarnings("unchecked")
            List<Element> listLocated = (List<Element>) xpathNature.selectNodes(newRootElement);
            for (Element located : listLocated) {
                String province = located.getAttributeValue("province");
                if (province != null && province.contains(idOld)) {
                    if (province.equals(idOld)) { // if it is only located in this province,
                        located.setAttribute("country", carcodeNew); // located.getAttributeValue("country") + " " +
                        located.removeAttribute("province");
                    } else {
                        located.setAttribute("province", province.replace(idOld, "").replace("  ", " ").trim());
                        Element p = located.getParentElement();
                        Element n = new Element("located");
                        n.setAttribute("country", carcodeNew);
                        p.addContent(p.indexOf(located), n);
                    }
                    // System.out.println(located);
                }
            }
            // for (String ename : listTopElementName) {
            // // String strXPath = "//idref('" + idOld + "')/ancestor::" + ename;
            // @SuppressWarnings("unchecked")
            // List<Element> listTopElement = (List<Element>) newRootElement.getChildren(ename);
            // for (Element topElement : listTopElement) {
            // // @SuppressWarnings("unchecked")
            // // List<Element> listLocated = (List<Element>) topElement.getChildren("located");
            // }
            // for (String cname : listCityidOld) {
            // ;
            // }
            // }

            // Change IDREF: organization
            String strOrg = "//organization"; // count(//organization): 168
            XPath xpathOrg = XPath.newInstance(strOrg);
            @SuppressWarnings("unchecked")
            List<Element> listOrg = (List<Element>) xpathOrg.selectNodes(newRootElement);
            for (Element org : listOrg) {
                @SuppressWarnings("unchecked")
                List<Element> listMembers = (List<Element>) org.getChildren("members");
                for (Element members : listMembers) {
                    String c = members.getAttributeValue("country");
                    if (c == null) {
                        continue;
                    }
                    if (c.startsWith(carcodeOld + " ") || c.endsWith(" " + carcodeOld) || c.equalsIgnoreCase(carcodeOld)
                            || c.contains(" " + carcodeOld + " ")) {
                        members.setAttribute("country", c + " " + carcodeNew);
                    }
                }
            }

            // Change IDREF: airport
            String strAirport = "//airport"; // count(//airport): 1318
            XPath xpathAirport = XPath.newInstance(strAirport);
            @SuppressWarnings("unchecked")
            List<Element> listAirport = (List<Element>) xpathAirport.selectNodes(newRootElement);
            for (Element airport : listAirport) {
                String city = airport.getAttributeValue("city");
                if (city != null && listCityidOld.contains(city)) {
                    airport.setAttribute("city", city.replace(countryname, countrynameNew));
                    airport.setAttribute("country", carcodeNew);
                }
            }

        } catch (JDOMException e) {
            e.printStackTrace();
        }
    }

    public void specifyCalifornia() {
        List<String> listOrgException = new ArrayList<String>(0);
        listOrgException.add("org-G-5");
        listOrgException.add("org-G-7");

    }

    public void writeToFile() {
        try {
            if (!(new File(ouptutPath)).exists()) {
                (new File(ouptutPath)).createNewFile();
            }
            XMLOutputter xmlOutputter = new XMLOutputter();
            xmlOutputter.setFormat(Format.getPrettyFormat());
            xmlOutputter.output(newCountryElement, System.out);
            // xmlOutputter.output(document, new FileWriter(ouptutPath));
            xmlOutputter.output(newDocument, new FileWriter(ouptutPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void validate() {
        SAXBuilder builder = new SAXBuilder(true);
        try {
            Document d = builder.build(ouptutPath);
            System.out.println(d + " passes.");
        } catch (JDOMException e) {
            System.out.println(ouptutPath + " does NOT pass.");
            e.printStackTrace();
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

    /**
     * Use W3C DOM.
     */
    @Deprecated
    public void validate1() {
        Schema schema = null;
        try {
            String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;// ACCESS_EXTERNAL_DTD;XML_DTD_NS_URI;
            SchemaFactory factory = SchemaFactory.newInstance(language);
            schema = factory.newSchema(new File(dtdPath));
            Validator validator = schema.newValidator();
            validator.validate(new DOMSource());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public void validate2() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new ErrorHandler() {
                public void warning(SAXParseException e) throws SAXException {
                    System.out.println("WARNING : " + e.getMessage()); // do nothing
                }

                public void error(SAXParseException e) throws SAXException {
                    System.out.println("ERROR : " + e.getMessage());
                    throw e;
                }

                public void fatalError(SAXParseException e) throws SAXException {
                    System.out.println("FATAL : " + e.getMessage());
                    throw e;
                }
            });
            builder.parse(new InputSource(newinfoPath));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public void validate3() {
        SAXBuilder builder = new SAXBuilder(false);
        File file1 = new File("/usr/workspace/xml/test.xml");
        File file2 = new File("/usr/workspace/xml/test2.xml");
        File file3 = new File("/usr/workspace/xml/calexit_new.xml");
        try {
            Document d3 = builder.build(file3);
            System.out.println(d3 + " passes.");
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        builder = new SAXBuilder(true);
        try {
            Document d1 = builder.build(file1);
            System.out.println(d1 + " passes.");
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Document d2 = builder.build(file2);
            System.out.println(d2 + " passes.");
        } catch (JDOMException e) {
            System.out.println(file2 + " does NOT pass.");
            // e.printStackTrace();
        } catch (IOException e) {
            // e.printStackTrace();
        }
        try {
            Document d = builder.build(ouptutPath);
            System.out.println(d + " passes.");
        } catch (JDOMException e) {
            System.out.println(ouptutPath + " does NOT pass.");
            // e.printStackTrace();
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

}
