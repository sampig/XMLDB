/*
 * Copyright (c) 2017, Chenfeng Zhu. All rights reserved.
 *
 */
package org.zhuzhu.zzcalendar.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zhuzhu.xml.jaxb.EntryType;
import org.zhuzhu.xml.jaxb.TerminCalendar.Schedule.Year.Month;
import org.zhuzhu.xml.jaxb.TerminCalendar.Schedule.Year.Month.Day;
import org.zhuzhu.zzcalendar.util.CalendarUtils;
import org.zhuzhu.zzcalendar.util.PropertyUtils;

/**
 * Servlet implementation class ShowCalendarServlet
 *
 * @author Chenfeng Zhu
 */
// @WebServlet(description = "This is a Servlet for showing calendar.", urlPatterns = { "/showcalendar" })
public class ShowCalendarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ShowCalendarServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String year = request.getParameter("year");
        String month = request.getParameter("month");
        String day = request.getParameter("day");
        boolean withday = false;
        Calendar calendar = Calendar.getInstance();
        if (year == null) {
            year = String.valueOf(calendar.get(Calendar.YEAR));
        } else {
            calendar.set(Calendar.YEAR, Integer.parseInt(year));
        }
        if (month == null) {
            month = String.format("%02d", calendar.get(Calendar.MONTH) + 1);
        } else {
            calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        }
        if (day != null) {
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
            withday = true;
        }
        PrintWriter out = response.getWriter();
        out.print(this.createDocumentHeader("My Calendar: " + year + "-" + month));
        out.print(this.createChangexml());
        out.print(this.createCalendar(calendar, withday));
        out.print(this.createAddEntry());
        out.print(this.createEntryList(calendar, withday));
        out.print(this.createDocumentFooter());
    }

    /**
     * Create the header of the HTML.
     *
     * @param title
     * @return
     */
    private String createDocumentHeader(String title) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE HTML PUBLIC ").append("\"-//W3C//DTD HTML 4.0 Transitional//EN\">\n");
        sb.append("<html>\n<head>");
        sb.append("<link rel=\"stylesheet\" href=\"css/calendar.css\">\n");
        sb.append("<link rel=\"stylesheet\" href=\"css/addentry.css\">\n");
        sb.append("<link rel=\"stylesheet\" href=\"css/changexml.css\">\n");
        sb.append("<title>").append(title).append("</title></head>\n").append("<body>\n");
        return sb.toString();
    }

    /**
     * Create the calendar div of the HTML.
     *
     * @param calendar
     * @param withday
     * @return
     */
    private String createCalendar(Calendar calendar, boolean withday) {
        int daySelected = calendar.get(Calendar.DAY_OF_MONTH);
        calendar = (Calendar) calendar.clone();
        Calendar prevMonth = (Calendar) calendar.clone();
        Calendar nextMonth = (Calendar) calendar.clone();
        Calendar preYear = (Calendar) calendar.clone();
        Calendar nextYear = (Calendar) calendar.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        prevMonth.add(Calendar.MONTH, -1);
        nextMonth.add(Calendar.MONTH, 1);
        preYear.add(Calendar.YEAR, -1);
        nextYear.add(Calendar.YEAR, 1);

        StringBuilder sb = new StringBuilder();
        sb.append("<div class='calendar'>");

        // month header
        sb.append("<div class='month'>");
        sb.append("<ul>");
        sb.append("<li class='prev'><a href='./showcalendar?year=" + prevMonth.get(Calendar.YEAR) + "&month="
                + (prevMonth.get(Calendar.MONTH) + 1) + "' class='previous'>&#10094;</a><br/>");
        sb.append("<a href='./showcalendar?year=" + preYear.get(Calendar.YEAR) + "&month="
                + (preYear.get(Calendar.MONTH) + 1) + "' class='previous'>&#10094;&#10094;</a></li>");
        sb.append("<li class='next'><a href='./showcalendar?year=" + nextMonth.get(Calendar.YEAR) + "&month="
                + (nextMonth.get(Calendar.MONTH) + 1) + "' class='next'>&#10095;</a><br/>");
        sb.append("<a href='./showcalendar?year=" + nextYear.get(Calendar.YEAR) + "&month="
                + (nextYear.get(Calendar.MONTH) + 1) + "' class='next'>&#10095;&#10095;</a></li>");
        sb.append("<li><a href='./showcalendar?year=" + calendar.get(Calendar.YEAR) + "&month="
                + (calendar.get(Calendar.MONTH) + 1) + "' class='calendar-month'>"
                + calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + "</a><br/>");
        sb.append("<span style='font-size: 18px'>" + calendar.get(Calendar.YEAR) + "</span>");
        sb.append("</li>");
        sb.append("</ul>");
        sb.append("</div>");

        // weekday header
        sb.append("<ul class='weekdays'>");
        sb.append("<li>Su</li>");
        sb.append("<li>Mo</li>");
        sb.append("<li>Tu</li>");
        sb.append("<li>We</li>");
        sb.append("<li>Th</li>");
        sb.append("<li>Fr</li>");
        sb.append("<li>Sa</li>");
        sb.append("</ul>");

        // table of days
        sb.append("<ul class='days'>");
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int dayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        // System.out.println("day of week: " + day);
        // System.out.println("day count of month: " + dayCount);
        for (int i = 1; i < day; i++) {
            sb.append("<li>&nbsp;</li>");
        }
        for (int i = 1; i <= dayCount; i++) {
            String str = "<a href='./showcalendar?year=" + calendar.get(Calendar.YEAR) + "&month="
                    + (calendar.get(Calendar.MONTH) + 1) + "&day=" + i + "' class='calendar-date'>" + i + "</a>";
            if (i == daySelected && withday) {
                sb.append("<li><span class='active'>" + str + "</span></li>");
            } else {
                sb.append("<li>" + str + "</li>");
            }
        }
        sb.append("</ul>");

        sb.append("</div>");
        return sb.toString();
    }

    /**
     * Create the entry list part of the HTML.
     *
     * @param calendar
     * @param withday
     * @return
     */
    private String createEntryList(Calendar calendar, boolean withday) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class='termin-list'>\n");

        int iYear = calendar.get(Calendar.YEAR);
        int iMonth = calendar.get(Calendar.MONTH) + 1;
        int iDay = calendar.get(Calendar.DAY_OF_MONTH);
        Month monthEntries = CalendarUtils.getMonthAllEntries(calendar);
        boolean foundTermin = false;
        if (monthEntries != null) {
            if (!withday) {
                foundTermin = true;
            } else {
                for (Day day : monthEntries.getDay()) {
                    if (day.getN() == iDay) {
                        foundTermin = true;
                        break;
                    }
                }
            }
        }

        if (!foundTermin) {
            if (withday) {
                sb.append("<h2>No Termins on this day.</h2>\n</div>");
            } else {
                sb.append("<h2>No Termins in this month.</h2>\n</div>");
            }
            return sb.toString();
        }

        sb.append("<table class='termin-table'>");
        sb.append("<tr>");
        sb.append("<th>Date</th>");
        sb.append("<th>Start time</th>");
        sb.append("<th>Name</th>");
        sb.append("<th>Location</th>");
        sb.append("<th>Duration</th>");
        sb.append("</tr>");

        if (withday) {
            for (Day day : monthEntries.getDay()) {
                if (day.getN() == iDay) {
                    String strDate = iYear + "-" + String.format("%02d", iMonth) + "-" + String.format("%02d", iDay);
                    sb.append(this.getEntryRow(strDate, day));
                    break;
                }
            }
        } else {
            for (Day day : monthEntries.getDay()) {
                String strDate = iYear + "-" + String.format("%02d", iMonth) + "-" + String.format("%02d", day.getN());
                sb.append(this.getEntryRow(strDate, day));
            }
        }

        sb.append("</table>");

        sb.append("</div>");
        return sb.toString();
    }

    /**
     * Create a row of an entry in a table.
     *
     * @param strDate
     * @param day
     * @return
     */
    private String getEntryRow(String strDate, Day day) {
        StringBuilder sb = new StringBuilder();
        for (EntryType et : day.getEntry()) {
            sb.append("<tr>");
            sb.append("<td>" + strDate + "</td>");
            sb.append("<td>" + et.getStarttime() + "</td>");
            sb.append("<td>" + et.getName() + "</td>");
            sb.append("<td>" + et.getLocation() + "</td>");
            String strDuration = et.getDuration().getHours() + " hours " + et.getDuration().getMinutes() + " minutes";
            sb.append("<td>" + et.getDuration() + " (" + strDuration + ")</td>");
            sb.append("</tr>");
        }
        return sb.toString();
    }

    /**
     * Create the form for adding a new entry.
     *
     * @return
     */
    private String createAddEntry() {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class='add-entry-div'>\n");

        PropertyUtils pu = new PropertyUtils();
        if (!pu.checkLocalfile()) {
            sb.append("<h2>The XML file is not local.</h2>");
            sb.append("</div>");
            return sb.toString();
        }

        try {
            Path pathAddentry = Paths.get(getServletContext().getRealPath("/addentry.html"));
            String contents = new String(Files.readAllBytes(pathAddentry));
            sb.append(contents);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sb.append("</div>");
        return sb.toString();
    }

    /**
     * Create the form for changing the xml.
     *
     * @return
     */
    private String createChangexml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class='change-xml-div'>\n");
        try {
            Path pathAddentry = Paths.get(getServletContext().getRealPath("/changexml.html"));
            String contents = new String(Files.readAllBytes(pathAddentry));
            sb.append(contents);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sb.append("</div>");
        return sb.toString();
    }

    /**
     * Create the footer of the HTML.
     *
     * @return
     */
    private String createDocumentFooter() {
        return "</body></html>\n";
    }
}
