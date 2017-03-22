/*
 * Copyright (c) 2017, Chenfeng Zhu. All rights reserved.
 *
 */
package org.zhuzhu.xml.jaxb;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.zhuzhu.xml.jaxb.TerminCalendar.Schedule;
import org.zhuzhu.xml.jaxb.TerminCalendar.Schedule.Year;
import org.zhuzhu.xml.jaxb.TerminCalendar.Schedule.Year.Month;
import org.zhuzhu.xml.jaxb.TerminCalendar.Schedule.Year.Month.Day;

/**
 * Application: Personal Schedule using JAXB.<br/>
 * Usage:
 * <ol>
 * <li>Generate the codes for models: <code>xjc -p org.zhuzhu.xml.jaxb [path_of_xsd] -d [output_directory]</code></li>
 * <li>Compile the codes: <code>javac -d . `find ./org/zhuzhu/xml/jaxb -name '*.java'`</code></li>
 * </ol>
 *
 * @author Chenfeng Zhu
 *
 */
public class PersonalSchedule {

    private String sourcePath = "/usr/workspace/xml/XMLDB/Practical_XML/Exercise04/personalschedule.xml";
    private String schemaPath = "/usr/workspace/xml/XMLDB/Practical_XML/Exercise04/personalschedule.xsd";
    private String outputPath = "/usr/workspace/xml/newschedule.xml";
    private String xsltPath = "/usr/workspace/xml/XMLDB/Practical_XML/Exercise04/personalschedule.xsl";
    private String outputDirectory = "/usr/workspace/xml/";

    private JAXBContext jaxbContext;
    private TerminCalendar tc;

    public static void main(String... strings) {
        String source = null;
        if (strings.length >= 1) {
            source = strings[0];
        }
        String schema = null;
        if (strings.length >= 2) {
            schema = strings[1];
        }
        String output = null;
        if (strings.length >= 3) {
            output = strings[2];
        }
        String xslt = null;
        if (strings.length >= 4) {
            xslt = strings[3];
        }
        String ym = null;
        if (strings.length >= 5) {
            ym = strings[4];
        }

        PersonalSchedule ps = new PersonalSchedule(source, schema, output, xslt);

        // print all schedule.
        ps.printAllSchedule();

        String dur = "PT01H"; // PnYnMnDTnHnMnS
        String title = "XML last course";

        // add a collision-free entry.
        System.out.println("**Add a collision-free entry:");
        Calendar c1 = Calendar.getInstance();
        c1.set(2017, Calendar.FEBRUARY, 6, 14, 0, 0);
        ps.insertEntry(c1, dur, title);
        System.out.println("**Add a collision-free entry:");
        c1.set(2017, Calendar.APRIL, 7, 11, 0, 0);
        ps.insertEntry(c1, dur, title);
        System.out.println("**Add a collision-free entry:");
        Calendar c0 = Calendar.getInstance();
        c0.set(2016, Calendar.DECEMBER, 24, 19, 0, 0);
        ps.insertEntry(c0, dur, "Weilnachten");

        // add a exactly collision entry.
        System.out.println("**Add a exactly collision entry:");
        Calendar c2 = Calendar.getInstance();
        c2.set(2017, Calendar.FEBRUARY, 14, 14, 00, 0);
        ps.insertEntry(c2, dur, title);

        // add a early collision entry.
        System.out.println("**Add a early collision entry:");
        Calendar c3 = Calendar.getInstance();
        c3.set(2017, Calendar.FEBRUARY, 14, 13, 30, 0);
        ps.insertEntry(c3, dur, title);

        // add a late collision entry.
        System.out.println("**Add a late collision entry:");
        Calendar c4 = Calendar.getInstance();
        c4.set(2017, Calendar.FEBRUARY, 14, 14, 30, 0);
        ps.insertEntry(c4, dur, title);
        System.out.println("**Add a late collision entry:");
        c4.set(2017, Calendar.MARCH, 2, 11, 30, 0);
        ps.insertEntry(c4, null, title);

        // transform the result.
        ps.transform();

        // validate the new output.
        ps.validateOutput();

        // transform with XSLT.
        ps.xslt(ym);
    }

    public PersonalSchedule(String source, String schema, String output, String xslt) {
        if (source != null && !("".equalsIgnoreCase(source))) {
            this.sourcePath = source;
        }
        if (schema != null && !("".equalsIgnoreCase(schema))) {
            this.schemaPath = schema;
        }
        if (output != null && !("".equalsIgnoreCase(output))) {
            this.outputPath = output;
            this.outputDirectory = output.substring(0, output.lastIndexOf("/") + 1);
        }
        if (xslt != null && !("".equalsIgnoreCase(xslt))) {
            this.xsltPath = xslt;
        }
        // System.out.println("Source XML File: " + sourcePath);
        // System.out.println("Schema File: " + schemaPath);
        // System.out.println("Output File: " + outputPath);
        // System.out.println("Output Directory: " + outputDirectory);
        // System.out.println("XSLT File: " + xsltPath);

        this.init();
    }

    /**
     * Initialization.
     */
    private void init() {
        try {
            jaxbContext = JAXBContext.newInstance("org.zhuzhu.xml.jaxb");
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL urlSchema = null;
            if (schemaPath.startsWith("http")) {
                urlSchema = new URL(schemaPath);
            }
            Schema schema = null;
            if (urlSchema != null) {
                schema = schemaFactory.newSchema(urlSchema);
            } else {
                schema = schemaFactory.newSchema(new File(schemaPath));
            }
            if (schema != null) {
                unmarshaller.setSchema(schema);
            }
            URL urlSource = null;
            if (sourcePath.startsWith("http")) {
                urlSource = new URL(sourcePath);
            }
            if (urlSource != null) {
                tc = (TerminCalendar) unmarshaller.unmarshal(urlSource);
            } else {
                tc = (TerminCalendar) unmarshaller.unmarshal(new File(sourcePath));
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all termins in one termin calendar.
     *
     * @return
     */
    public TerminCalendar getTerminCalendar() {
        return tc;
    }

    /**
     * Print all Schedules.
     */
    public void printAllSchedule() {
        System.out.println("List all schedules:\n");
        for (Schedule schedule : tc.getSchedule()) {
            System.out.println("============ " + schedule.getName() + "'s Schedule ============");
            for (Year year : schedule.getYear()) {
                for (Month month : year.getMonth()) {
                    for (Day day : month.getDay()) {
                        System.out.println("-------- " + year.getN() + "-" + month.getN() + "-" + day.getN()
                                + " --------");
                        for (EntryType et : day.getEntry()) {
                            System.out.println("Meeting: " + et.getName());
                            System.out.println("\tStart Time: " + et.getStarttime());
                            System.out.println("\tDuration: " + et.getDuration());
                            XMLGregorianCalendar endtime = (XMLGregorianCalendar) et.getStarttime().clone();
                            endtime.add(et.getDuration());
                            System.out.println("\tEnd Time: " + endtime);
                            if (et.getLocation() != null) {
                                System.out.println("\tLocation: " + et.getLocation());
                            }
                            System.out.println();
                        }
                    }
                }
            }
            System.out.println();
        }
    }

    /**
     * Insert a new Entry.
     *
     * @param calendar
     * @param strDuration
     * @param title
     * @return
     */
    public boolean insertEntry(Calendar calendar, String strDuration, String title) {
        return this.insertEntry(calendar, strDuration, title, null, null);
    }

    /**
     * Insert a new Entry.
     *
     * @param calendar
     * @param strDuration
     * @param title
     * @param description
     * @param location
     * @return
     */
    public boolean insertEntry(Calendar calendar, String strDuration, String title, String description, String location) {
        boolean flag = true;
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH) + 1; // month in calendar starts in 0.
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int mi = calendar.get(Calendar.MINUTE);
        int s = calendar.get(Calendar.SECOND);
        if (strDuration == null) {
            strDuration = "PT00S";
        }

        try {
            DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
            Duration newduration = datatypeFactory.newDuration(strDuration);
            // jaxbContext = JAXBContext.newInstance("org.zhuzhu.xml.jaxb");
            // Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            // tc = (TerminCalendar) unmarshaller.unmarshal(new File(sourcePath));
            // new entry
            XMLGregorianCalendar newstart = datatypeFactory.newXMLGregorianCalendarTime(h, mi, s,
                    DatatypeConstants.FIELD_UNDEFINED);
            EntryType newentry = new EntryType();
            newentry.setName(title);
            newentry.setDuration(newduration);
            newentry.setStarttime(newstart);
            if (description != null) {
                newentry.setDescription(description);
            }
            if (location != null) {
                newentry.setLocation(location);
            }
            for (Schedule schedule : tc.getSchedule()) {
                for (Year year : schedule.getYear()) {
                    if (year.getN().getYear() != y) {
                        continue;
                    }
                    for (Month month : year.getMonth()) {
                        if (month.getN() != m) {
                            continue;
                        }
                        for (Day day : month.getDay()) {
                            if (day.getN() != d) {
                                continue;
                            }
                            for (EntryType et : day.getEntry()) {
                                XMLGregorianCalendar starttime = et.getStarttime();
                                Duration duration = et.getDuration();
                                XMLGregorianCalendar endtime = (XMLGregorianCalendar) starttime.clone();
                                endtime.add(duration);
                                // if (newstart.equals(starttime)) { // if they are at the same time.
                                // flag = false;
                                // System.out.println("FALSE: There is already an meeting at this time: " + et);
                                // System.out.println("Existing(" + et.getName() + "): " + starttime + " ~ " + endtime);
                                // return flag;
                                // }
                                XMLGregorianCalendar newend = (XMLGregorianCalendar) newstart.clone();
                                newend.add(newduration);
                                if ((newstart.compare(starttime) == DatatypeConstants.LESSER && newend
                                        .compare(starttime) == DatatypeConstants.LESSER)
                                        || (newstart.compare(endtime) == DatatypeConstants.GREATER && newend
                                                .compare(endtime) == DatatypeConstants.GREATER)) {
                                } else {
                                    flag = false;
                                    System.out.println("FALSE: There is already an meeting during this time: " + et);
                                    System.out
                                            .println("Existing(" + et.getName() + "): " + starttime + " ~ " + endtime);
                                    System.out.println("New(" + newentry.getName() + "): " + newstart + " ~ " + newend
                                            + "\n");
                                    return flag;
                                }
                            }
                            if (flag) { // if there is another meeting on this day, add an entry to this day.
                                List<EntryType> list = day.getEntry();
                                int ie = 0;
                                for (; ie < list.size(); ie++) {
                                    EntryType et = list.get(ie);
                                    XMLGregorianCalendar starttime = et.getStarttime();
                                    if (newstart.compare(starttime) == DatatypeConstants.LESSER) {
                                        break;
                                    }
                                }
                                day.getEntry().add(ie, newentry);
                                System.out.println("Add a new Entry: " + newentry + "\n");
                                return flag;
                            }
                        }
                        if (flag) { // if there is no meetings on this day, add an entry to a new day.
                            int p = month.getDay().size();
                            for (Day day : month.getDay()) {
                                if (day.getN() < d) {
                                    continue;
                                } else {
                                    p = month.getDay().indexOf(day);
                                    break;
                                }
                            }
                            Day newday = new Day();
                            newday.setN(d);
                            newday.getEntry().add(newentry);
                            System.out.println("Add a new Entry: " + newentry);
                            month.getDay().add(p, newday);
                            System.out.println("Add a new Day: " + newday.getN() + "\n");
                            return flag;
                        }
                    }
                    if (flag) { // if there is no meetings on this month, add an entry to a new month.
                        int p = year.getMonth().size();
                        for (Month month : year.getMonth()) {
                            if (month.getN() < m) {
                                continue;
                            } else {
                                p = year.getMonth().indexOf(month);
                                break;
                            }
                        }
                        Month newmonth = new Month();
                        newmonth.setN(m);
                        Day newday = new Day();
                        newday.setN(d);
                        newday.getEntry().add(newentry);
                        System.out.println("Add a new Entry: " + newentry);
                        newmonth.getDay().add(newday);
                        System.out.println("Add a new Day: " + newday.getN());
                        year.getMonth().add(p, newmonth);
                        System.out.println("Add a new Month: " + newmonth.getN() + "\n");
                        return flag;
                    }
                }
                if (flag) { // if there is no meetings on this year, add an entry to a new year in a right position.
                    int p = schedule.getYear().size();
                    for (Year year : schedule.getYear()) {
                        if (year.getN().getYear() < y) {
                            continue;
                        } else {
                            p = schedule.getYear().indexOf(year);
                            break;
                        }
                    }
                    Year newyear = new Year();
                    newyear.setN(datatypeFactory.newXMLGregorianCalendarDate(y, DatatypeConstants.FIELD_UNDEFINED,
                            DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED));
                    Month newmonth = new Month();
                    newmonth.setN(m);
                    Day newday = new Day();
                    newday.setN(d);
                    newday.getEntry().add(newentry);
                    System.out.println("Add a new Entry: " + newentry);
                    newmonth.getDay().add(newday);
                    System.out.println("Add a new Day: " + newday.getN());
                    newyear.getMonth().add(newmonth);
                    System.out.println("Add a new Month: " + newmonth.getN());
                    schedule.getYear().add(p, newyear);
                    System.out.println("Add a new Year: " + newyear.getN() + "\n");
                    return flag;
                }
            }
            // } catch (JAXBException e) {
            // e.printStackTrace();
        } catch (DatatypeConfigurationException e1) {
            e1.printStackTrace();
        }
        return flag;
    }

    /**
     * Transform into JDOM and write into a file.
     */
    public void transform() {
        try {
            // transform the result into a DOM and write to an XML file:
            Marshaller m = jaxbContext.createMarshaller();
            DOMResult domResult = new DOMResult();
            m.marshal(tc, domResult);
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
    }

    /**
     * Validate the output.
     */
    public void validateOutput() {
        System.out.println("Validating the new output:");
        try {
            JAXBContext jc = JAXBContext.newInstance("org.zhuzhu.xml.jaxb");
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(new File(schemaPath));
            unmarshaller.setSchema(schema);
            unmarshaller.unmarshal(new File(outputPath));
            System.out.println("PASS: '" + outputPath + "' against '" + schemaPath + "'.\n");
        } catch (JAXBException e) {
            System.out.println("ERROR");
            // e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    /**
     * Apply an XSLT transformation.
     */
    public void xslt(String yearmonth) {
        if (yearmonth == null) {
            Calendar c = Calendar.getInstance();
            yearmonth = (new SimpleDateFormat("yyyyMM")).format(c.getTime());
        }
        System.out.println("Input month: " + yearmonth);
        String htmlPath = outputDirectory + "schedule" + yearmonth + ".html";
        System.out.println("Output into file: " + htmlPath);
        System.out.println("XSLT Transforming:");
        try {
            Marshaller m = jaxbContext.createMarshaller();
            DOMResult domResult = new DOMResult();
            m.marshal(tc, domResult);
            Document doc = (Document) domResult.getNode();
            Source docSource = new DOMSource(doc);

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(xsltPath)); // Source of XSLT document
            transformer.setParameter("yearmonth", yearmonth);
            transformer.transform(docSource, new StreamResult(System.out));
            transformer.transform(docSource, new StreamResult(new File(htmlPath)));
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

}
