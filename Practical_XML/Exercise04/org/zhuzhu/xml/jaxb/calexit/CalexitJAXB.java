/*
 * Copyright (c) 2017, Chenfeng Zhu. All rights reserved.
 * 
 */
package org.zhuzhu.xml.jaxb.calexit;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Practical XML Exercise 04.<br/>
 * Calexit with JAXB.<br/>
 * Usage:
 * <ol>
 * <li>Generate the codes for models:
 * <code>xjc -p org.zhuzhu.xml.jaxb.calexit mondial_jaxb.xsd -d [output_directory]</code></li>
 * <li>Compile the codes: <code>javac -d . `find ./org/zhuzhu/xml/jaxb/calexit -name '*.java'`</code></li>
 * </ol>
 * 
 * @author Chenfeng Zhu
 *
 */
public class CalexitJAXB {

    // User-defined file path
    private String sourcePath = "/usr/workspace/xml/mondial.xml";
    private String outputPath = "/usr/workspace/xml/mondial_new.xml";
    private String newinfoPath = "/usr/workspace/xml/calexit_new_jaxb.xml";
    private String schemaPath = "/usr/workspace/xml/mondial_jaxb.xsd";

    // User-defined static variables
    private String provid = "prov-United-States-6";
    private List<String> listOrgException = Arrays.asList("org-G-5", "org-G-7");
    private String goverment = "CA federal republic";

    private JAXBContext jaxbContext;

    private Mondial mondial;
    private Mondial newinfor;
    private Country califor;

    // global information
    private String carcodeNew = "carcodeNew";
    private String carcodeOrig = "carcodeOld";
    private String countrynameNew = "countrynameNew";
    private String countrynameOrig = "countrynameOrig";

    private Map<String, Long> mapPopProvince = new HashMap<String, Long>(0);
    private Long lPopProvince = 0l;
    private Long lPopCountry = 0l;
    private List<String> listCityidOld = new ArrayList<String>(0);
    private List<String> listCityidNew = new ArrayList<String>(0);
    private Map<String, Double> mapBorder = new HashMap<String, Double>(0);

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
        String schema = null;
        if (strings.length >= 4) {
            schema = strings[3];
        }

        CalexitJAXB cjaxb = new CalexitJAXB(filepath, outputFile, newinforFile, schema);

        cjaxb.readNewinfor();
        cjaxb.readMondial();
        cjaxb.writeToFile();
        cjaxb.validateOutput();
    }

    public CalexitJAXB(String source, String target, String newinfor, String schema) {
        if (source != null && !("".equalsIgnoreCase(source))) {
            this.sourcePath = source;
        }
        System.out.println("Source XML File: " + sourcePath);
        if (target != null && !("".equalsIgnoreCase(target))) {
            this.outputPath = target;
        }
        System.out.println("Target XML File: " + outputPath);
        if (newinfor != null && !("".equalsIgnoreCase(newinfor))) {
            this.newinfoPath = newinfor;
        }
        System.out.println("New Information XML File: " + newinfoPath);
        if (schema != null && !("".equalsIgnoreCase(schema))) {
            this.schemaPath = schema;
        }
        System.out.println("Schema File: " + schemaPath);

        try {
            jaxbContext = JAXBContext.newInstance("org.zhuzhu.xml.jaxb.calexit");
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read information of new country.
     */
    public void readNewinfor() {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            newinfor = (Mondial) unmarshaller.unmarshal(new File(newinfoPath));
            for (Country c : newinfor.getCountry()) {
                califor = c;
            }
            carcodeNew = califor.getCarCode();
            System.out.println("Get new country: " + carcodeNew);
            // TODO: cannot get border country as it is a IDREF
            for (Border b : califor.getBorder()) {
                System.out.println(b.getCountry());
                // System.out.println(b.getLength());
                // mapBorder.put(b.getCountry().toString(), b.getLength().toString());
            }
            califor.getBorder().clear();
            mapBorder.put("MEX", 226.0);
            mapBorder.put("USA", 1681.0);
            System.out.println(mapBorder);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read Mondial information.
     */
    public void readMondial() {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            // unmarshaller.setProperty("javax.xml.accessExternalDTD", "all");
            // unmarshaller.setProperty(javax.xml.XMLConstants.ACCESS_EXTERNAL_DTD, "all");
            // SAXParserFactory spf = SAXParserFactory.newInstance();
            // spf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            // XMLReader xmlReader = spf.newSAXParser().getXMLReader();
            // InputSource inputSource = new InputSource(new FileReader(sourcePath));
            // SAXSource saxsource = new SAXSource(xmlReader, inputSource);
            // mondial = (Mondial) unmarshaller.unmarshal(saxsource);
            mondial = (Mondial) unmarshaller.unmarshal(new File(sourcePath));
            // Country
            Province origProv = null;
            Country origCountry = null;
            for (Country ctry : mondial.getCountry()) {
                for (Province prov : ctry.getProvince()) {
                    if (provid.equals(prov.getId())) {
                        carcodeOrig = ctry.getCarCode();
                        countrynameOrig = ctry.getName().get(0).replace(" ", "-");
                        countrynameNew = prov.getName().get(0).replace(" ", "-");
                        origProv = prov;
                        origCountry = ctry;
                        //////////////////////
                        // New Country
                        // attributes of new country
                        califor.setCarCode(carcodeNew);
                        califor.setArea(prov.getArea());
                        califor.setCapital(prov.getCapital());
                        for (Object obj : ctry.getMemberships()) {
                            Organization o = (Organization) obj;
                            if (!listOrgException.contains(o.getId())) {
                                califor.getMemberships().add(obj);
                            }
                        }
                        // children
                        for (String name : prov.getName()) {
                            califor.getName().add(name);
                        }
                        // califor.getPopulation().addAll(prov.getPopulation());
                        for (Population p : prov.getPopulation()) {
                            califor.getPopulation().add(p);
                            mapPopProvince.put(String.valueOf(p.getYear()), p.getValue().longValue());
                            lPopProvince = p.getValue().longValue();
                        }
                        IndepDate indepdate = new IndepDate();
                        indepdate.setFrom(carcodeOrig);
                        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
                        indepdate.setValue(datatypeFactory.newXMLGregorianCalendar((GregorianCalendar) Calendar.getInstance()));
                        califor.setIndepDate(indepdate);
                        califor.setGovernment(goverment);
                        Border bCalifor = new Border();
                        bCalifor.setCountry(ctry);
                        bCalifor.setLength(new BigDecimal(mapBorder.get(carcodeOrig)));
                        califor.getBorder().add(bCalifor);
                        for (Encompassed enc : ctry.getEncompassed()) {
                            if (enc.getPercentage().intValue() == 100) {
                                Encompassed e = new Encompassed();
                                e.setContinent(enc.getContinent());
                                e.setPercentage(enc.getPercentage());
                                califor.getEncompassed().add(e);
                            }
                        }
                        // cities
                        for (City city : prov.getCity()) {
                            listCityidOld.add(city.getId());
                            listCityidNew.add(city.getId().replace(countrynameOrig, countrynameNew));
                            city.setId(city.getId().replace(countrynameOrig, countrynameNew));
                            city.setProvince(null);
                            city.setCountry(califor);
                            if (city.equals(prov.getCapital())) {
                                city.setIsStateCap("yes");
                            }
                            califor.getCity().add(city);
                        }
                        //////////////////////
                        // original country
                        // original country: attributes
                        ctry.setArea(new BigDecimal(ctry.getArea().doubleValue() - califor.getArea().doubleValue()));
                        // original country: border
                        for (Border bor : ctry.getBorder()) {
                            Country c = (Country) bor.getCountry();
                            if (c == null) {
                                continue;
                            }
                            if (mapBorder.containsKey(c.getCarCode())) {
                                BigDecimal l = new BigDecimal(bor.getLength().doubleValue() - mapBorder.get(c.getCarCode()));
                                bor.setLength(l);
                            }
                        }
                        Border bNew = new Border();
                        bNew.setCountry(califor);
                        bNew.setLength(new BigDecimal(mapBorder.get(carcodeOrig)));
                        ctry.getBorder().add(bNew);
                        // original country: population
                        List<Population> listPopRemoved = new ArrayList<Population>(0);
                        for (Population pop : ctry.getPopulation()) {
                            if (mapPopProvince.containsKey(String.valueOf(pop.getYear()))) {
                                long newPop = pop.getValue().longValue() - mapPopProvince.get(String.valueOf(pop.getYear()));
                                pop.setValue(BigInteger.valueOf(newPop));
                                lPopCountry = pop.getValue().longValue();
                            } else {
                                listPopRemoved.add(pop);
                            }
                        }
                        for (Population pop : listPopRemoved) {
                            ctry.getPopulation().remove(pop);
                        }
                        System.out.println("Population of country: " + lPopCountry);
                        System.out.println("Population of province: " + lPopProvince);
                        long lPopCurrent = lPopCountry - lPopProvince;
                        double popgrowthNew = (ctry.getPopulationGrowth().doubleValue() * lPopCountry
                                - califor.getPopulationGrowth().doubleValue() * lPopProvince) / lPopCurrent;
                        ctry.setPopulationGrowth(new BigDecimal(String.format("%.2f", popgrowthNew)));
                        double infantNew = (ctry.getInfantMortality().doubleValue() * lPopCountry
                                - califor.getInfantMortality().doubleValue() * lPopProvince) / lPopCurrent;
                        ctry.setInfantMortality(new BigDecimal(String.format("%.2f", infantNew)));
                        double umemployNew = (ctry.getUnemployment().doubleValue() * lPopCountry
                                - califor.getUnemployment().doubleValue() * lPopProvince) / lPopCurrent;
                        ctry.setUnemployment(new BigDecimal(String.format("%.2f", umemployNew)));
                        // original country: GDP
                        Double dGdpProvince = califor.getGdpTotal().doubleValue();
                        Double dGdpCountry = ctry.getGdpTotal().doubleValue();
                        Double dGdpCurrent = dGdpCountry - dGdpProvince;
                        System.out.println("GDP of country: " + dGdpCountry);
                        System.out.println("GDP of province: " + dGdpProvince);
                        ctry.setGdpTotal(new BigDecimal(dGdpCurrent));
                        double gdpagriNew = (ctry.getGdpAgri().doubleValue() * dGdpCountry
                                - califor.getGdpAgri().doubleValue() * dGdpProvince) / dGdpCurrent;
                        ctry.setGdpAgri(new BigDecimal(String.format("%.2f", gdpagriNew)));
                        double gdpindNew = (ctry.getGdpInd().doubleValue() * dGdpCountry
                                - califor.getGdpInd().doubleValue() * dGdpProvince) / dGdpCurrent;
                        ctry.setGdpInd(new BigDecimal(String.format("%.2f", gdpindNew)));
                        double gdpservNew = (ctry.getGdpServ().doubleValue() * dGdpCountry
                                - califor.getGdpServ().doubleValue() * dGdpProvince) / dGdpCurrent;
                        ctry.setGdpServ(new BigDecimal(String.format("%.2f", gdpservNew)));
                        double inflationNew = (ctry.getInflation().doubleValue() * dGdpCountry
                                - califor.getInflation().doubleValue() * dGdpProvince) / dGdpCurrent;
                        ctry.setInflation(new BigDecimal(String.format("%.2f", inflationNew)));
                        // original country: population2 (ethnicgroup, religion, language)
                        for (PercentageProperty ppE : ctry.getEthnicgroup()) {
                            for (PercentageProperty pp : califor.getEthnicgroup()) {
                                if (ppE.getValue().equals(pp.getValue())) {
                                    double ppNew = (ppE.getPercentage().doubleValue() * lPopCountry
                                            - pp.getPercentage().doubleValue() * lPopProvince) / lPopCurrent;
                                    ppE.setPercentage(new BigDecimal(String.format("%.2f", ppNew)));
                                }
                            }
                        }
                        for (PercentageProperty ppR : ctry.getReligion()) {
                            for (PercentageProperty pp : califor.getReligion()) {
                                if (ppR.getValue().equals(pp.getValue())) {
                                    double ppNew = (ppR.getPercentage().doubleValue() * lPopCountry
                                            - pp.getPercentage().doubleValue() * lPopProvince) / lPopCurrent;
                                    ppR.setPercentage(new BigDecimal(String.format("%.2f", ppNew)));
                                }
                            }
                        }
                        for (PercentageProperty ppL : ctry.getLanguage()) {
                            for (PercentageProperty pp : califor.getLanguage()) {
                                if (ppL.getValue().equals(pp.getValue())) {
                                    double ppNew = (ppL.getPercentage().doubleValue() * lPopCountry
                                            - pp.getPercentage().doubleValue() * lPopProvince) / lPopCurrent;
                                    ppL.setPercentage(new BigDecimal(String.format("%.2f", ppNew)));
                                }
                            }
                        }
                    }
                }
                // border country
                if (mapBorder.containsKey(ctry.getCarCode()) && !ctry.getCarCode().equals(carcodeOrig)) { // border
                    for (Border b : ctry.getBorder()) {
                        Country c = (Country) b.getCountry();
                        if (c == null) {
                            continue;
                        }
                        if (mapBorder.containsKey(c.getCarCode())) {
                            BigDecimal l = new BigDecimal(b.getLength().doubleValue() - mapBorder.get(ctry.getCarCode()));
                            b.setLength(l);
                        }
                    }
                    Border b = new Border();
                    b.setCountry(califor);
                    b.setLength(new BigDecimal(mapBorder.get(ctry.getCarCode())));
                    ctry.getBorder().add(b);
                    b = new Border();
                    b.setCountry(ctry);
                    b.setLength(new BigDecimal(mapBorder.get(ctry.getCarCode())));
                    califor.getBorder().add(b);
                }
            }
            // Organization
            for (Organization org : mondial.getOrganization()) {
                for (Object obj : califor.getMemberships()) {
                    Organization o = (Organization) obj;
                    if (org.getId().equals(o.getId())) {
                        for (Members member : org.getMembers()) {
                            for (Object objMember : member.getCountry()) {
                                Country ctry = (Country) objMember;
                                if (ctry.getCarCode().equals(carcodeOrig)) {
                                    member.getCountry().add(califor);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            // Nature Sea
            for (Sea sea : mondial.getSea()) {
                for (Located located : sea.getLocated()) {
                    boolean flag = false;
                    for (Object obj : located.getProvince()) {
                        Province prov = (Province) obj;
                        if (prov.getId().equals(origProv.getId())) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        if (located.getProvince().size() > 1) {
                            located.getProvince().remove(origProv);
                            Located locatedNew = new Located();
                            locatedNew.setCountry(califor);
                            sea.getLocated().add(locatedNew);
                        } else {
                            located.getProvince().clear();
                            located.setCountry(califor);
                            sea.getCountry().remove(origCountry);
                        }
                        sea.getCountry().add(califor);
                        break;
                    }
                }
            }
            // Nature River: source, estuary.
            for (River river : mondial.getRiver()) {
                for (Located located : river.getLocated()) {
                    boolean flag = false;
                    for (Object obj : located.getProvince()) {
                        Province prov = (Province) obj;
                        if (prov.getId().equals(origProv.getId())) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        if (located.getProvince().size() > 1) {
                            located.getProvince().remove(origProv);
                            Located locatedNew = new Located();
                            locatedNew.setCountry(califor);
                            river.getLocated().add(locatedNew);
                        } else {
                            located.province = null;
                            located.setCountry(califor);
                            river.getCountry().remove(origCountry);
                        }
                        river.getCountry().add(califor);
                        org.zhuzhu.xml.jaxb.calexit.Source source = river.getSource();
                        for (Located loc : source.getLocated()) {
                            boolean f = false;
                            for (Object obj : loc.getProvince()) {
                                Province prov = (Province) obj;
                                if (prov.getId().equals(origProv.getId())) {
                                    f = true;
                                    break;
                                }
                            }
                            if (f) {
                                if (loc.getProvince().size() > 1) {
                                    loc.getProvince().remove(origProv);
                                    Located locatedNew = new Located();
                                    locatedNew.setCountry(califor);
                                    source.getLocated().add(locatedNew);
                                } else {
                                    loc.province = null;
                                    loc.setCountry(califor);
                                    source.getCountry().remove(origCountry);
                                }
                                source.getCountry().add(califor);
                                break;
                            }
                        }
                        Estuary estuary = river.getEstuary();
                        for (Located loc : estuary.getLocated()) {
                            boolean f = false;
                            for (Object obj : loc.getProvince()) {
                                Province prov = (Province) obj;
                                if (prov.getId().equals(origProv.getId())) {
                                    f = true;
                                    break;
                                }
                            }
                            if (f) {
                                if (loc.getProvince().size() > 1) {
                                    loc.getProvince().remove(origProv);
                                    Located locatedNew = new Located();
                                    locatedNew.setCountry(califor);
                                    estuary.getLocated().add(locatedNew);
                                } else {
                                    loc.province = null;
                                    loc.setCountry(califor);
                                    estuary.getCountry().remove(origCountry);
                                }
                                estuary.getCountry().add(califor);
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            // Nature Lake
            for (Lake lake : mondial.getLake()) {
                for (Located located : lake.getLocated()) {
                    boolean flag = false;
                    for (Object obj : located.getProvince()) {
                        Province prov = (Province) obj;
                        if (prov.getId().equals(origProv.getId())) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        if (located.getProvince().size() > 1) {
                            located.getProvince().remove(origProv);
                            Located locatedNew = new Located();
                            locatedNew.setCountry(califor);
                            lake.getLocated().add(locatedNew);
                        } else {
                            located.province = null;
                            located.setCountry(califor);
                            lake.getCountry().remove(origCountry);
                        }
                        lake.getCountry().add(califor);
                        break;
                    }
                }
            }
            // Nature Island
            for (Island island : mondial.getIsland()) {
                for (Located located : island.getLocated()) {
                    boolean flag = false;
                    for (Object obj : located.getProvince()) {
                        Province prov = (Province) obj;
                        if (prov.getId().equals(origProv.getId())) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        if (located.getProvince().size() > 1) {
                            located.getProvince().remove(origProv);
                            Located locatedNew = new Located();
                            locatedNew.setCountry(califor);
                            island.getLocated().add(locatedNew);
                        } else {
                            located.province = null;
                            located.setCountry(califor);
                            island.getCountry().remove(origCountry);
                        }
                        island.getCountry().add(califor);
                        break;
                    }
                }
            }
            // Nature Mountain
            for (Mountain mountain : mondial.getMountain()) {
                for (Located located : mountain.getLocated()) {
                    boolean flag = false;
                    for (Object obj : located.getProvince()) {
                        Province prov = (Province) obj;
                        if (prov.getId().equals(origProv.getId())) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        if (located.getProvince().size() > 1) {
                            located.getProvince().remove(origProv);
                            Located locatedNew = new Located();
                            locatedNew.setCountry(califor);
                            mountain.getLocated().add(locatedNew);
                        } else {
                            located.province = null;
                            located.setCountry(califor);
                            mountain.getCountry().remove(origCountry);
                        }
                        mountain.getCountry().add(califor);
                        break;
                    }
                }
            }
            // Nature Desert
            for (Desert desert : mondial.getDesert()) {
                for (Located located : desert.getLocated()) {
                    boolean flag = false;
                    for (Object obj : located.getProvince()) {
                        Province prov = (Province) obj;
                        if (prov.getId().equals(origProv.getId())) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        if (located.getProvince().size() > 1) {
                            located.getProvince().remove(origProv);
                            Located locatedNew = new Located();
                            locatedNew.setCountry(califor);
                            desert.getLocated().add(locatedNew);
                        } else {
                            located.province = null;
                            located.setCountry(califor);
                            desert.getCountry().remove(origCountry);
                        }
                        desert.getCountry().add(califor);
                        break;
                    }
                }
            }
            // Airport
            for (Airport airport : mondial.getAirport()) {
                City city = (City) airport.getCity();
                if (city == null) {
                    continue;
                }
                if (listCityidNew.contains(city.getId())) {
                    city.setId(city.getId().replace(countrynameOrig, countrynameNew));
                    airport.setCountry(califor);
                }
            }
            if (origProv != null) {
                origCountry.getProvince().remove(origProv);
                origProv = null;
            }
            mondial.getCountry().add(califor);
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
            // } catch (SAXNotRecognizedException e1) {
            // e1.printStackTrace();
            // } catch (SAXNotSupportedException e1) {
            // e1.printStackTrace();
            // } catch (ParserConfigurationException e1) {
            // e1.printStackTrace();
            // } catch (SAXException e1) {
            // e1.printStackTrace();
            // } catch (FileNotFoundException e1) {
            // e1.printStackTrace();
        }
    }

    /**
     * Write Mondial into a new file.
     */
    public void writeToFile() {
        System.out.println("Writing into file...");
        try {
            Marshaller m = jaxbContext.createMarshaller();
            // m.setProperty(javax.xml.XMLConstants.ACCESS_EXTERNAL_DTD, Boolean.FALSE);
            DOMResult domResult = new DOMResult();
            m.marshal(mondial, domResult);
            Document doc = (Document) domResult.getNode();
            // transformer stuff is only for writing DOM tree to file/stdout
            TransformerFactory factory = TransformerFactory.newInstance();
            Source docSource = new DOMSource(doc);
            StreamResult result = new StreamResult(outputPath);
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(docSource, result);
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        System.out.println("DONE.");
    }

    /**
     * Validate the output.
     */
    public void validateOutput() {
        System.out.println("Validating the new output:");
        try {
            JAXBContext jc = JAXBContext.newInstance("org.zhuzhu.xml.jaxb.calexit");
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(new File(schemaPath));
            unmarshaller.setSchema(schema);
            unmarshaller.unmarshal(new File(outputPath));
            System.out.println("PASS: '" + outputPath + "' against '" + schemaPath + "'.\n");
        } catch (JAXBException e) {
            System.out.println("ERROR");
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

}
