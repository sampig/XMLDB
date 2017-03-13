/*
 * Copyright (c) 2017, Chenfeng Zhu. All rights reserved.
 *
 */
package org.zhuzhu.zzcalendar.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.dom.DOMResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.zhuzhu.xml.jaxb.EntryType;
import org.zhuzhu.xml.jaxb.PersonalSchedule;
import org.zhuzhu.xml.jaxb.TerminCalendar;
import org.zhuzhu.xml.jaxb.TerminCalendar.Schedule;
import org.zhuzhu.xml.jaxb.TerminCalendar.Schedule.Year;
import org.zhuzhu.xml.jaxb.TerminCalendar.Schedule.Year.Month;
import org.zhuzhu.xml.jaxb.TerminCalendar.Schedule.Year.Month.Day;

/**
 * Some utilities for calendar.
 *
 * @author Chenfeng Zhu
 *
 */
public class CalendarUtils {

    private final static int START_HOUR = 8;
    private final static int END_HOUR = 18;

    /**
     * Get all entries in a given month.
     *
     * @param calendar
     * @return
     */
    public static Month getMonthAllEntries(Calendar calendar) {
        PropertyUtils pu = new PropertyUtils();
        String source = pu.getValue(PropertyUtils.KEY_SCHEDULE_XML);
        String schema = pu.getValue(PropertyUtils.KEY_SCHEDULE_XSD);
        PersonalSchedule ps = new PersonalSchedule(source, schema, null, null);
        TerminCalendar tc = ps.getTerminCalendar();
        for (Schedule schedule : tc.getSchedule()) {
            for (Year year : schedule.getYear()) {
                if (year.getN().getYear() != calendar.get(Calendar.YEAR)) {
                    continue;
                }
                for (Month month : year.getMonth()) {
                    if (month.getN() == calendar.get(Calendar.MONTH) + 1) {
                        return month;
                    }
                }
            }
            System.out.println();
        }
        return null;
    }

    /**
     * Add a new entry.
     *
     * @param calendar
     * @param strDuration
     * @param title
     * @param description
     * @param location
     * @return
     */
    public static boolean addEntry(Calendar calendar, String strDuration, String title, String description,
            String location) {
        PropertyUtils pu = new PropertyUtils();
        String source = pu.getValue(PropertyUtils.KEY_SCHEDULE_XML);
        String schema = pu.getValue(PropertyUtils.KEY_SCHEDULE_XSD);
        PersonalSchedule ps = new PersonalSchedule(source, schema, source, null);
        boolean flag = ps.insertEntry(calendar, strDuration, title, description, location);
        ps.transform();
        return flag;
    }

    /**
     * Get all entries from start date to end date.
     *
     * @param startdate
     * @param enddate
     * @param duration
     * @return
     */
    public static Schedule getAllEntries(String startdate, String enddate, String duration) {
        int startYear = Integer.parseInt(startdate.substring(0, 4));
        int startMonth = Integer.parseInt(startdate.substring(5, 7));
        int startDay = Integer.parseInt(startdate.substring(8, 10));
        int endYear = Integer.parseInt(enddate.substring(0, 4));
        int endMonth = Integer.parseInt(enddate.substring(5, 7));
        int endDay = Integer.parseInt(enddate.substring(8, 10));
        PropertyUtils pu = new PropertyUtils();
        String source = pu.getValue(PropertyUtils.KEY_SCHEDULE_XML);
        String schema = pu.getValue(PropertyUtils.KEY_SCHEDULE_XSD);
        PersonalSchedule ps = new PersonalSchedule(source, schema, null, null);
        TerminCalendar tc = ps.getTerminCalendar();
        Schedule schedule = new Schedule();
        for (Schedule sch : tc.getSchedule()) {
            for (Year year : sch.getYear()) {
                // if it is not between the start year and the end year
                if (year.getN().getYear() < startYear || year.getN().getYear() > endYear) {
                    continue;
                }
                Year y = new Year();
                y.setN(year.getN());
                for (Month month : year.getMonth()) {
                    // if it is not between the start month and the end month
                    if ((year.getN().getYear() == startYear && month.getN() < startMonth)
                            || (year.getN().getYear() == endYear && month.getN() > endMonth)) {
                        continue;
                    }
                    Month m = new Month();
                    m.setN(month.getN());
                    for (Day day : month.getDay()) {
                        // if it is not between the start day and the end day
                        if ((year.getN().getYear() == startYear && month.getN() == startMonth && day.getN() < startDay)
                                || (year.getN().getYear() == endYear && month.getN() == endMonth && day.getN() > endDay)) {
                            continue;
                        }
                        Calendar c = Calendar.getInstance();
                        c.set(year.getN().getYear(), month.getN() - 1, day.getN());
                        // if it is not weekday
                        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                                || c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                            continue;
                        }
                        Day d = new Day();
                        d.setN(day.getN());
                        for (EntryType et : day.getEntry()) {
                            int startHour = et.getStarttime().getHour();
                            // if it is not between 8 and 18
                            if (startHour < START_HOUR || startHour >= END_HOUR) {
                                continue;
                            }
                            // TODO: if it is not starting at full hours
                            // TODO: if it is not a given duration
                            d.getEntry().add(et);
                        }
                        if (d.getEntry().size() > 0) {
                            m.getDay().add(d);
                        }
                    }
                    if (m.getDay().size() > 0) {
                        y.getMonth().add(m);
                    }
                }
                if (y.getMonth().size() > 0) {
                    schedule.getYear().add(y);
                }
            }
        }
        return schedule;
    }

    /**
     * Get all entries and put them in to a DOM.
     *
     * @param startdate
     * @param enddate
     * @param duration
     * @return
     */
    public static Document getAllEntriesToDOM(String startdate, String enddate, String duration) {
        Document doc = null;
        TerminCalendar tc = new TerminCalendar();
        Schedule schedule = getAllEntries(startdate, enddate, duration);
        tc.getSchedule().add(schedule);
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance("org.zhuzhu.xml.jaxb");
            Marshaller m = jaxbContext.createMarshaller();
            DOMResult domResult = new DOMResult();
            m.marshal(tc, domResult);
            doc = (Document) domResult.getNode();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return doc;
    }

    /**
     * Get all schedule entries from local and remote. Ordered by date and time.
     *
     * @param docLocal
     * @param docRemote
     * @return
     */
    public static Map<String, List<EntryType>> combineSchedule(Document docLocal, Document docRemote) {
        Map<String, List<EntryType>> map = new TreeMap<String, List<EntryType>>(); // ordered by date
        XPath xPath = XPathFactory.newInstance().newXPath();
        String strEntry = "//entry";
        // add local
        Element rootLocal = docLocal.getDocumentElement();
        try {
            NodeList entryList = (NodeList) xPath.evaluate(strEntry, rootLocal, XPathConstants.NODESET);
            for (int i = 0; i < entryList.getLength(); i++) {
                Node entryNode = entryList.item(i);
                if (entryNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element entry = (Element) entryNode;
                    EntryType et = elementToEntry(entry);
                    String day = ((Element) entry.getParentNode()).getAttribute("n");
                    String month = ((Element) entry.getParentNode().getParentNode()).getAttribute("n");
                    String year = ((Element) entry.getParentNode().getParentNode().getParentNode()).getAttribute("n");
                    String date = year + String.format("%02d", Integer.parseInt(month))
                            + String.format("%02d", Integer.parseInt(day));
                    if (map.containsKey(date)) {
                        map.get(date).add(et);
                    } else {
                        List<EntryType> list = new ArrayList<EntryType>();
                        list.add(et);
                        map.put(date, list);
                    }
                }
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        // add remote
        if (docRemote != null) {
            try {
                Element rootRemote = docRemote.getDocumentElement();
                NodeList entryList = (NodeList) xPath.evaluate(strEntry, rootRemote, XPathConstants.NODESET);
                for (int i = 0; i < entryList.getLength(); i++) {
                    Node entryNode = entryList.item(i);
                    if (entryNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element entry = (Element) entryNode;
                        EntryType et = elementToEntry(entry);
                        String day = ((Element) entry.getParentNode()).getAttribute("n");
                        String month = ((Element) entry.getParentNode().getParentNode()).getAttribute("n");
                        String year = ((Element) entry.getParentNode().getParentNode().getParentNode())
                                .getAttribute("n");
                        String date = year + String.format("%02d", Integer.parseInt(month))
                                + String.format("%02d", Integer.parseInt(day));
                        if (map.containsKey(date)) {
                            List<EntryType> list = map.get(date);
                            // ordered by time
                            for (int j = 0; j < list.size(); j++) {
                                EntryType e = list.get(j);
                                if (e.getStarttime().toGregorianCalendar()
                                        .after(et.getStarttime().toGregorianCalendar())) {
                                    list.add(j, et);
                                    break;
                                }
                                if (j == list.size() - 1) {
                                    list.add(et);
                                }
                            }
                        } else {
                            List<EntryType> list = new ArrayList<EntryType>();
                            list.add(et);
                            map.put(date, list);
                        }
                    }
                }
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * Transform an element of "entry" into the EntryType object.
     *
     * @param e
     * @return
     */
    private static EntryType elementToEntry(Element e) {
        if (!"entry".equals(e.getLocalName()) && !"entry".equals(e.getNodeName())) {
            return null;
        }
        EntryType et = new EntryType();
        et.setName(e.getElementsByTagName("name").item(0).getTextContent());
        String starttime = e.getAttribute("starttime");
        GregorianCalendar gc = new GregorianCalendar();
        try {
            gc.setTime(new SimpleDateFormat("HH:mm:ss").parse(starttime));
        } catch (ParseException e2) {
            e2.printStackTrace();
        }
        try {
            XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
            et.setStarttime(xmlCalendar);
        } catch (DatatypeConfigurationException e1) {
            e1.printStackTrace();
        }
        String duration = e.getAttribute("duration");
        try {
            et.setDuration(DatatypeFactory.newInstance().newDuration(duration));
        } catch (DatatypeConfigurationException e1) {
            e1.printStackTrace();
        }
        return et;
    }

    /**
     * Get the first free slot.
     *
     * @param cStart
     * @param cEnd
     * @param durHour
     * @param mapEntries
     * @return
     */
    public static Calendar getFirstFreeSlot(Calendar cStart, Calendar cEnd, int durHour,
            Map<String, List<EntryType>> mapEntries) {
        Calendar calendar = Calendar.getInstance();
        for (Calendar c = (Calendar) cStart.clone(); c.before(cEnd); c.add(Calendar.DAY_OF_MONTH, 1)) {
            if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                continue;
            }
            String date = new SimpleDateFormat("yyyyMMdd").format(c.getTime());
            Calendar current = (Calendar) c.clone();
            current.set(Calendar.HOUR_OF_DAY, START_HOUR);
            current.set(Calendar.MINUTE, 0);
            current.set(Calendar.SECOND, 0);
            Calendar start = (Calendar) current.clone();
            Calendar end = (Calendar) start.clone();
            // end.add(Calendar.HOUR, durHour);
            if (mapEntries.containsKey(date)) {
                for (EntryType et : mapEntries.get(date)) {
                    Calendar etStart = (Calendar) c.clone();
                    etStart.set(Calendar.HOUR_OF_DAY, et.getStarttime().getHour());
                    etStart.set(Calendar.MINUTE, et.getStarttime().getMinute());
                    etStart.set(Calendar.SECOND, et.getStarttime().getSecond());
                    end = (Calendar) etStart.clone();
                    if ((end.get(Calendar.HOUR_OF_DAY) - start.get(Calendar.HOUR_OF_DAY)) >= durHour) {
                        calendar = (Calendar) start.clone();
                        return calendar;
                    }
                    Calendar etEnd = (Calendar) etStart.clone();
                    etEnd.add(Calendar.HOUR, et.getDuration().getHours());
                    etEnd.add(Calendar.MINUTE, et.getDuration().getMinutes());
                    etEnd.add(Calendar.SECOND, et.getDuration().getSeconds());
                    if (et.getDuration().getMinutes() > 0) {
                        start.set(Calendar.HOUR_OF_DAY, etEnd.get(Calendar.HOUR_OF_DAY) + 1);
                    } else {
                        start.set(Calendar.HOUR_OF_DAY, etEnd.get(Calendar.HOUR_OF_DAY));
                    }
                }
                end.set(Calendar.HOUR_OF_DAY, END_HOUR);
                end.set(Calendar.MINUTE, 0);
                end.set(Calendar.SECOND, 0);
                if ((end.get(Calendar.HOUR_OF_DAY) - start.get(Calendar.HOUR_OF_DAY)) >= durHour) {
                    calendar = (Calendar) start.clone();
                    return calendar;
                }
            } else {
                calendar = (Calendar) start.clone();
                return calendar;
            }
        }
        return null;
    }

}
