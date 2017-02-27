/*
 * Copyright (c) 2017, Chenfeng Zhu. All rights reserved.
 *
 */
package org.zhuzhu.zzcalendar.util;

import java.util.Calendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.dom.DOMResult;

import org.w3c.dom.Document;
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
                            if (startHour < 8 || startHour >= 18) {
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

}
