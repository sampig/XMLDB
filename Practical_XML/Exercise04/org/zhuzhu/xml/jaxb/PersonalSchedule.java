/*
 * Copyright (c) 2017, Chenfeng Zhu. All rights reserved.
 * 
 */
package org.zhuzhu.xml.jaxb;

import java.io.File;
import java.util.Calendar;

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
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.zhuzhu.xml.jaxb.TerminCalendar.Schedule;
import org.zhuzhu.xml.jaxb.TerminCalendar.Schedule.Year;
import org.zhuzhu.xml.jaxb.TerminCalendar.Schedule.Year.Month;
import org.zhuzhu.xml.jaxb.TerminCalendar.Schedule.Year.Month.Day;

/**
 * Application: Personal Schedule.
 * 
 * @author Chenfeng Zhu
 *
 */
public class PersonalSchedule {

    private String sourcePath = "/usr/workspace/xml/XMLDB/Practical_XML/Exercise04/personalschedule.xml";
    private String schemaPath = "/usr/workspace/xml/XMLDB/Practical_XML/Exercise04/personalschedule.xsd";
    private String outputPath = "/usr/workspace/xml/newschedule.xml";

    private JAXBContext jaxbContext;
    private TerminCalendar tc;

    public static void main(String... strings) {
        String source = null;
        if (strings.length >= 1) {
            source = strings[0];
        }
        String schema = null;
        if (strings.length >= 2) {
            schema = strings[0];
        }
        String output = null;
        if (strings.length >= 3) {
            output = strings[0];
        }

        PersonalSchedule ps = new PersonalSchedule(source, schema, output);

        // print all schedule.
        ps.printAllSchedule();

        String dur = "PT01H"; // PnYnMnDTnHnMnS
        String title = "XML last course";

        // add a collision-free entry.
        System.out.println("**Add a collision-free entry:");
        Calendar c1 = Calendar.getInstance();
        c1.set(2017, 2, 6, 14, 0, 0);
        ps.insertEntry(c1, dur, title);
        c1.set(2017, 4, 7, 11, 0, 0);
        ps.insertEntry(c1, dur, title);

        // add a exactly collision entry.
        System.out.println("**Add a exactly collision entry:");
        Calendar c2 = Calendar.getInstance();
        c2.set(2017, 2, 14, 14, 00, 0);
        ps.insertEntry(c2, dur, title);

        // add a early collision entry.
        System.out.println("**Add a early collision entry:");
        Calendar c3 = Calendar.getInstance();
        c3.set(2017, 2, 14, 13, 30, 0);
        ps.insertEntry(c3, dur, title);

        // add a late collision entry.
        System.out.println("**Add a late collision entry:");
        Calendar c4 = Calendar.getInstance();
        c4.set(2017, 2, 14, 14, 30, 0);
        ps.insertEntry(c4, dur, title);
        c4.set(2017, 3, 2, 11, 30, 0);
        ps.insertEntry(c4, null, title);

        ps.transform();
    }

    public PersonalSchedule(String source, String schema, String output) {
        if (source != null && !("".equalsIgnoreCase(source))) {
            this.sourcePath = source;
        }
        if (schema != null && !("".equalsIgnoreCase(schema))) {
            this.schemaPath = schema;
        }
        if (output != null && !("".equalsIgnoreCase(output))) {
            this.outputPath = output;
        }
        System.out.println("Source XML File: " + sourcePath);
        System.out.println("Schema File: " + schemaPath);
        System.out.println("Output File: " + outputPath);
    }

    /**
     * Print all Schedules.
     */
    public void printAllSchedule() {
        try {
            jaxbContext = JAXBContext.newInstance("org.zhuzhu.xml.jaxb");
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(new File(schemaPath));
            unmarshaller.setSchema(schema);
            tc = (TerminCalendar) unmarshaller.unmarshal(new File(sourcePath));
            System.out.println("List all schedules:\n");
            for (Schedule schedule : tc.getSchedule()) {
                System.out.println("============ " + schedule.getName() + "'s Schedule ============");
                for (Year year : schedule.getYear()) {
                    for (Month month : year.getMonth()) {
                        for (Day day : month.getDay()) {
                            System.out.println("-------- " + year.getN() + "-" + month.getN() + "-" + day.getN() + " --------");
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
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
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
        boolean flag = true;
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
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
            XMLGregorianCalendar newstart = datatypeFactory.newXMLGregorianCalendarTime(h, mi, s, DatatypeConstants.FIELD_UNDEFINED);
            EntryType newentry = new EntryType();
            newentry.setName(title);
            newentry.setDuration(newduration);
            newentry.setStarttime(newstart);
            for (Schedule schedule : tc.getSchedule()) {
                for (Year year : schedule.getYear()) {
                    if (year.getN().getYear() != y) {
                        continue;
                    }
                    // used for ordering
                    // if (year.getN().getYear() < y) {
                    // continue;
                    // } else if (year.getN().getYear() > y) {
                    // }
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
                                if ((newstart.compare(starttime) == DatatypeConstants.LESSER
                                        && newend.compare(starttime) == DatatypeConstants.LESSER)
                                        || (newstart.compare(endtime) == DatatypeConstants.GREATER
                                                && newend.compare(endtime) == DatatypeConstants.GREATER)) {
                                } else {
                                    flag = false;
                                    System.out.println("FALSE: There is already an meeting during this time: " + et);
                                    System.out.println("Existing(" + et.getName() + "): " + starttime + " ~ " + endtime);
                                    System.out.println("New(" + newentry.getName() + "): " + newstart + " ~ " + newend);
                                    return flag;
                                }
                            }
                            if (flag) { // if there is another meeting on this day, add an entry to this day.
                                day.getEntry().add(newentry);
                                System.out.println("Add a new Entry: " + newentry);
                                return flag;
                            }
                        }
                        if (flag) { // if there is no meetings on this day, add an entry to a new day.
                            Day newday = new Day();
                            newday.setN(d);
                            newday.getEntry().add(newentry);
                            System.out.println("Add a new Entry: " + newentry);
                            month.getDay().add(newday);
                            System.out.println("Add a new Day: " + newday);
                            return flag;
                        }
                    }
                    if (flag) { // if there is no meetings on this month, add an entry to a new month.
                        Month newmonth = new Month();
                        newmonth.setN(m);
                        Day newday = new Day();
                        newday.setN(d);
                        newday.getEntry().add(newentry);
                        System.out.println("Add a new Entry: " + newentry);
                        newmonth.getDay().add(newday);
                        System.out.println("Add a new Day: " + newday);
                        year.getMonth().add(newmonth);
                        System.out.println("Add a new Month: " + newmonth);
                        return flag;
                    }
                }
                if (flag) { // if there is no meetings on this year, add an entry to a new year.
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
                    System.out.println("Add a new Day: " + newday);
                    newyear.getMonth().add(newmonth);
                    System.out.println("Add a new Month: " + newmonth);
                    schedule.getYear().add(newyear);
                    System.out.println("Add a new Year: " + newyear);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
