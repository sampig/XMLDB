/*
 * Copyright (c) 2016, Chenfeng Zhu. All rights reserved.
 * 
 */
package org.zhuzhu.xml.calexit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

/**
 * Practical XML Exercise 03.<br/>
 * Calexit with JDOM-1.1.3.
 * 
 * @author Chenfeng Zhu
 *
 */
public class CalexitJDOM {

    public enum SearchBy {
        id, name
    }

    public enum SearchElement {
        province, city
    }

    private String dtdPath = "/usr/workspace/xml/mondial.dtd";
    private String sourcePath = "/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/lib/mondial.xml";
    private String ouptutPath = "/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/ex03/mondial_new_jdom.xml";
    private String newinfoPath = "/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/ex03/calexit_new.xml";

    private final static String CALIF_ID = "prov-United-States-6";

    private Document originalDocument;

    private Document newinforDocument;
    private Element newinforElement;

    private Document newDocument;
    private Element newRootElement;
    private Element newCountryElement;
    private String carcodeNew;

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
        cjdom.specifyCalifornia();
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

    /**
     * Read source XML file into DOM.
     */
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

    /**
     * Load the 10 top elements.
     */
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

    /**
     * Change the relative elements and attributes.
     * 
     * @param searche
     *            province or city
     * @param searchby
     *            id or name
     * @param keyword
     *            the values of id or name
     */
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
            System.out.println("XPath: " + strProvCalif);
            XPath xpathProvCalif = XPath.newInstance(strProvCalif);
            Element eProv = (Element) xpathProvCalif.selectSingleNode(newRootElement);

            String idOld = eProv.getAttributeValue("id");
            List<String> listCityidOld = new ArrayList<String>(0);
            String carcodeOld = eProv.getParentElement().getAttributeValue("car_code");
            carcodeNew = newinforElement.getChild("country").getAttributeValue("car_code");
            String countryname = eProv.getParentElement().getChildText("name").replace(" ", "-");
            String countrynameNew = eProv.getChildText("name").replace(" ", "-");

            newCountryElement = (Element) eProv.clone();
            newCountryElement.detach();

            // ======================
            // Create the new country.
            // ======================
            // Change its attributes and children.
            newCountryElement.setName("country");
            newCountryElement.setAttribute("car_code", carcodeNew);
            String areaNew = newCountryElement.getChildText("area");
            newCountryElement.setAttribute("area", areaNew);
            String capitalNew = newCountryElement.getAttributeValue("capital").replace(countryname, countrynameNew);
            newCountryElement.setAttribute("capital", capitalNew);
            String memberships = eProv.getParentElement().getAttributeValue("memberships");
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

                // Add "is_country_cap" for new capital city.
                if (capitalNew.equals(city.getAttributeValue("id"))) {
                    city.setAttribute("is_country_cap", "yes");
                }
            }

            // Add new information for it.
            int pos = newCountryElement.indexOf(newCountryElement.getChild("city"));
            @SuppressWarnings("unchecked")
            List<Element> newinforList = (List<Element>) newinforElement.getChild("country").getChildren();
            for (int i = 0; i <= newinforList.size() - 1; i++) {
                newCountryElement.addContent(pos + i, ((Element) newinforList.get(i).clone()).detach());
            }

            // add indep_date
            int posUnemployment = newCountryElement.indexOf(newCountryElement.getChild("unemployment"));
            Element dateElement = new Element("indep_date");
            dateElement.setAttribute("from", carcodeOld);
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateElement.addContent(sdf.format(date));
            newCountryElement.addContent(posUnemployment + 1, dateElement);

            // add government
            if (newCountryElement.getChild("government") == null) {
                Element govElement = new Element("government");
                govElement.addContent("CA federal republic");
                newCountryElement.addContent(posUnemployment + 2, govElement);
            }

            // add encompassed continent
            if (newCountryElement.getChild("encompassed") == null) {
                Element continentElement = (Element) eProv.getParentElement().getChild("encompassed");
                Element encompassedElement = new Element("encompassed");
                if ("100".equals(continentElement.getAttributeValue("percentage"))) {
                    encompassedElement.setAttribute("continent", continentElement.getAttributeValue("continent"));
                    encompassedElement.setAttribute("percentage", "100");
                    int posGovernment = newCountryElement.indexOf(newCountryElement.getChild("government"));
                    newCountryElement.addContent(posGovernment + 1, encompassedElement);
                } else {
                    ; // the situation is complicated if the country is in more than one country.
                }
            }

            // add new country into root before continent.
            int posContinent = newRootElement.indexOf(newRootElement.getChild("continent"));
            newRootElement.addContent(posContinent, newCountryElement);
            // System.out.println(idOld);
            // System.out.println(e);
            // System.out.println(newCountryElement);

            // ======================
            // Change the original country.
            // ======================
            // Change its area.
            Element origCountry = eProv.getParentElement();
            int countryArea = Integer.parseInt(origCountry.getAttributeValue("area")) - Integer.parseInt(areaNew);
            origCountry.setAttribute("area", String.valueOf(countryArea));

            // Change its population.
            // TODO: how about population_growth and infant_mortality?
            List<Element> listPopulation = this.transformToElements(newCountryElement.getChildren("population"));
            Map<String, Element> mapPopulation = new HashMap<String, Element>(0);
            List<Element> listPopulationRemove = new ArrayList<Element>(0);
            for (Element population : listPopulation) {
                mapPopulation.put(population.getAttributeValue("year"), population);
            }
            long oldPop = 0;
            long newPop = 0;
            long provPop = 0;
            for (Object obj : origCountry.getChildren("population")) {
                Element population = (Element) obj;
                if (mapPopulation.containsKey(population.getAttributeValue("year"))) {
                    long po = Long.parseLong(population.getText());
                    long pn = Long.parseLong(mapPopulation.get(population.getAttributeValue("year")).getText());
                    population.setText(String.valueOf(po - pn));
                    oldPop = po;
                    newPop = po - pn;
                    provPop = pn;
                } else {
                    listPopulationRemove.add(population);
                }
            }
            for (Element p : listPopulationRemove) {
                p.detach();
                // origCountry.removeContent(p);
            }

            // Change its GDP
            // TODO: how about inflation and unemployment?
            String[] strGdps = { "gdp_agri", "gdp_ind", "gdp_serv" };
            Element gdpTotal = origCountry.getChild("gdp_total");
            long oldGdpTotal = Long.parseLong(gdpTotal.getText());
            long provGdpTotal = Long.parseLong(newCountryElement.getChildText("gdp_total"));
            long newGdpTotal = oldGdpTotal - provGdpTotal;
            gdpTotal.setText(String.valueOf(newGdpTotal));
            for (String strGdp : strGdps) {
                Element gdpE = origCountry.getChild(strGdp);
                double oldGdp = Double.parseDouble(gdpE.getText()) * oldGdpTotal;
                double newGdp = oldGdp - Double.parseDouble(newCountryElement.getChildText(strGdp)) * provGdpTotal;
                gdpE.setText(String.format("%.2f", newGdp / newGdpTotal));// (String.valueOf(newGdp / newGdpTotal));//
            }

            // Change its ethnicgroup
            List<Element> listEthnic = this.transformToElements(origCountry.getChildren("ethnicgroup"));
            List<Element> listNewEthnic = this.transformToElements(newCountryElement.getChildren("ethnicgroup"));
            Map<String, Element> mapNewEthnic = new HashMap<String, Element>(0);
            for (Element en : listNewEthnic) {
                mapNewEthnic.put(en.getText(), en);
            }
            for (Element eo : listEthnic) {
                long po = Math.round(oldPop * Double.parseDouble(eo.getAttributeValue("percentage")));
                long pn = 0;
                if (mapNewEthnic.containsKey(eo.getText())) {
                    pn = Math.round(provPop * Double.parseDouble(mapNewEthnic.get(eo.getText()).getAttributeValue("percentage")));
                }
                eo.setAttribute("percentage", String.format("%.2f", (po - pn) * 1.0 / newPop));
            }

            // Change its religion
            List<Element> listReligion = this.transformToElements(origCountry.getChildren("religion"));
            List<Element> listNewReligion = this.transformToElements(newCountryElement.getChildren("religion"));
            Map<String, Element> mapNewReligion = new HashMap<String, Element>(0);
            for (Element en : listNewReligion) {
                mapNewReligion.put(en.getText(), en);
            }
            for (Element eo : listReligion) {
                long po = Math.round(oldPop * Double.parseDouble(eo.getAttributeValue("percentage")));
                long pn = 0;
                if (mapNewReligion.containsKey(eo.getText())) {
                    pn = Math.round(provPop * Double.parseDouble(mapNewReligion.get(eo.getText()).getAttributeValue("percentage")));
                }
                eo.setAttribute("percentage", String.format("%.2f", (po - pn) * 1.0 / newPop));
            }

            // Change its language
            List<Element> listLang = this.transformToElements(origCountry.getChildren("language"));
            List<Element> listNewLang = this.transformToElements(newCountryElement.getChildren("language"));
            Map<String, Element> mapNewLang = new HashMap<String, Element>(0);
            for (Element en : listNewLang) {
                mapNewLang.put(en.getText(), en);
            }
            for (Element eo : listLang) {
                long po = Math.round(oldPop * Double.parseDouble(eo.getAttributeValue("percentage")));
                long pn = 0;
                if (mapNewLang.containsKey(eo.getText())) {
                    pn = Math.round(provPop * Double.parseDouble(mapNewLang.get(eo.getText()).getAttributeValue("percentage")));
                }
                eo.setAttribute("percentage", String.format("%.2f", (po - pn) * 1.0 / newPop));
            }

            // Remove the province.
            origCountry.removeContent(eProv);

            // ======================
            // Change the other countries.
            // ======================
            // Change the relative border.
            @SuppressWarnings("unchecked")
            List<Element> listBorder = (List<Element>) newCountryElement.getChildren("border");
            Map<String, Element> mapBorder = new HashMap<String, Element>(0);
            for (Element border : listBorder) {
                mapBorder.put(border.getAttributeValue("country"), border);
            }
            // System.out.println(mapBorder);
            for (Element border : listBorder) {
                String cc = border.getAttributeValue("country");
                String strCountry = "//country[@car_code='" + cc + "']";
                XPath xpathCountry = XPath.newInstance(strCountry);
                Element eborderctry = (Element) xpathCountry.selectSingleNode(newRootElement);
                @SuppressWarnings("unchecked")
                List<Element> listBorderctryBorder = (List<Element>) eborderctry.getChildren("border");
                Element e = null;
                if (carcodeOld.equals(cc)) {
                    // for the original country, add a new border, change other borders.
                    for (int i = 0; i < listBorderctryBorder.size(); i++) {
                        e = listBorderctryBorder.get(i);
                        if (mapBorder.containsKey(e.getAttributeValue("country"))) {
                            int length = Integer.parseInt(e.getAttributeValue("length"));
                            int l2 = Integer.parseInt(mapBorder.get(e.getAttributeValue("country")).getAttributeValue("length"));
                            if (length <= l2) {
                                e.detach();
                            } else {
                                e.setAttribute("length", String.valueOf(length - l2));
                            }
                        }
                    }
                } else {
                    // for other border countries,
                    for (int i = 0; i < listBorderctryBorder.size(); i++) {
                        e = listBorderctryBorder.get(i);
                        if (carcodeOld.equals(e.getAttributeValue("country"))) {
                            int length = Integer.parseInt(e.getAttributeValue("length"));
                            int l2 = Integer.parseInt(mapBorder.get(cc).getAttributeValue("length"));
                            if (length <= l2) {
                                e.detach();
                            } else {
                                e.setAttribute("length", String.valueOf(length - l2));
                            }
                        }
                    }
                }
                Element newBorder = (Element) border.clone();
                newBorder.setAttribute("country", carcodeNew);
                if (e != null) {
                    int posBorder = eborderctry.indexOf(e);
                    eborderctry.addContent(posBorder + 1, newBorder);
                } else { // if there are no borders for this country before,
                    Element ePC = eborderctry.getChild("province");
                    if (ePC == null) {
                        ePC = eborderctry.getChild("city");
                    }
                    int posPC = eborderctry.indexOf(ePC);
                    eborderctry.addContent(posPC - 1, newBorder);
                }
            }

            // ======================
            // Change other elements.
            // ======================
            // Change IDREF: nature - located
            // //node()[count(./located[@country='USA'])>=1]
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
                    Element p = located.getParentElement();
                    if (province.equals(idOld)) { // if it is only located in this province,
                        located.setAttribute("country", carcodeNew);
                        located.removeAttribute("province");
                        String attr = p.getAttributeValue("country");
                        if (attr != null) {
                            if (carcodeOld.equals(attr)) {
                                attr = carcodeNew;
                            } else if (attr.startsWith(carcodeOld + " ")) {
                                attr = attr.replace(carcodeOld + " ", carcodeNew + " ");
                            } else if (attr.endsWith(" " + carcodeOld)) {
                                attr = attr.replace(" " + carcodeOld, " " + carcodeNew);
                            } else if (attr.contains(" " + carcodeOld + " ")) {
                                attr = attr.replace(" " + carcodeOld + " ", " " + carcodeNew + " ");
                            }
                        }
                        p.setAttribute("country", attr);
                    } else {
                        located.setAttribute("province", province.replace(idOld, "").replace("  ", " ").trim());
                        Element n = new Element("located");
                        n.setAttribute("country", carcodeNew);
                        p.addContent(p.indexOf(located) + 1, n);
                        p.setAttribute("country", p.getAttributeValue("country") + " " + carcodeNew);
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
            // }
            // }

            // Change IDREF: organization
            String strOrg = "//organization"; // count(//organization): 168
            XPath xpathOrg = XPath.newInstance(strOrg);
            @SuppressWarnings("unchecked")
            List<Element> listOrg = (List<Element>) xpathOrg.selectNodes(newRootElement);
            // List<String> listMemberships = Arrays.asList(memberships.split(" "));
            for (Element org : listOrg) {
                @SuppressWarnings("unchecked")
                List<Element> listMembers = (List<Element>) org.getChildren("members");
                for (Element members : listMembers) {
                    String c = members.getAttributeValue("country");
                    if (c == null) {
                        continue;
                    }
                    // if (listMemberships.contains(members.getAttributeValue("id"))) {
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

    /**
     * Transform List to List&lt;Element&gt;.
     * 
     * @param list
     * @return
     */
    @SuppressWarnings("rawtypes")
    private List<Element> transformToElements(List list) {
        List<Element> newlist = new ArrayList<Element>(0);
        for (Object obj : list) {
            if (obj instanceof Element) {
                newlist.add((Element) obj);
            }
        }
        return newlist;
    }

    /**
     * Special handler for California.
     */
    public void specifyCalifornia() {
        List<String> listOrgException = new ArrayList<String>(0);
        listOrgException.add("org-G-5");
        listOrgException.add("org-G-7");

        String memberships = newCountryElement.getAttributeValue("memberships");
        if (true) {
            for (String str : listOrgException) {
                memberships = memberships.replace(str, "");
            }
            memberships = memberships.replace("  ", " ").trim();
        }
        newCountryElement.setAttribute("memberships", memberships);

        for (String str : listOrgException) {
            str = "//organization[@id='" + str + "']";
            try {
                XPath xpath = XPath.newInstance(str);
                Element org = (Element) xpath.selectSingleNode(newDocument);
                @SuppressWarnings("unchecked")
                List<Element> listMembers = (List<Element>) org.getChildren("members");
                for (Element members : listMembers) {
                    String c = members.getAttributeValue("country");
                    if (c == null) {
                        continue;
                    }
                    c = c.replace(carcodeNew, "").replace("  ", " ").trim();
                    members.setAttribute("country", c);
                }
            } catch (JDOMException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Write the DOM into file.
     */
    public void writeToFile() {
        try {
            if (!(new File(ouptutPath)).exists()) {
                (new File(ouptutPath)).createNewFile();
            }
            XMLOutputter xmlOutputter = new XMLOutputter();
            xmlOutputter.setFormat(Format.getPrettyFormat()); // format.
            xmlOutputter.output(newCountryElement, System.out);
            // xmlOutputter.output(document, new FileWriter(ouptutPath));
            xmlOutputter.output(newDocument, new FileWriter(ouptutPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Validate the output XML file against the DTD.
     */
    public void validate() {
        System.out.println("\n\nValidation result: ");
        SAXBuilder builder = new SAXBuilder(true);
        try {
            Document d = builder.build(ouptutPath);
            System.out.println("PASS.");
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
