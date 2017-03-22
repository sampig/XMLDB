/*
 * Copyright (c) 2017, Chenfeng Zhu. All rights reserved.
 *
 */
package org.zhuzhu.zzcalendar.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.zhuzhu.xml.jaxb.EntryType;
import org.zhuzhu.zzcalendar.util.CalendarUtils;
import org.zhuzhu.zzcalendar.util.FormatUtils;
import org.zhuzhu.zzcalendar.util.PropertyUtils;

/**
 * Servlet implementation class FreeSlotServlet
 */
public class FreeSlotServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final int WORK_HOUR = 10;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public FreeSlotServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String strStart = request.getParameter("startdate");
        if (strStart == null || "".equals(strStart) || !FormatUtils.checkDateFormat(strStart)) {
            return;
        }
        String strEnd = request.getParameter("enddate");
        if (strEnd == null || "".equals(strEnd) || !FormatUtils.checkDateFormat(strEnd)) {
            return;
        }
        String strDuration = request.getParameter("duration");
        if (strDuration != null && !FormatUtils.checkHourFormat(strDuration)) {
            return;
        }
        Document doc = CalendarUtils.getAllEntriesToDOM(strStart, strEnd, strDuration);
        TransformerFactory factory = TransformerFactory.newInstance();
        Source docSource = new DOMSource(doc);
        StreamResult result = new StreamResult(out);
        Transformer transformer;
        try {
            transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(docSource, result);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        PrintWriter out = response.getWriter();
        String strStart = request.getParameter("startdate");
        if (strStart == null || "".equals(strStart) || !FormatUtils.checkDateFormat(strStart)) {
            out.print(FormatUtils.getScript("The format of start date is incorrect!"));
            return;
        }
        String strEnd = request.getParameter("enddate");
        if (strEnd == null || "".equals(strEnd) || !FormatUtils.checkDateFormat(strEnd)) {
            out.print(FormatUtils.getScript("The format of end date is incorrect!"));
            return;
        }
        String strDuration = request.getParameter("duration");
        if (strDuration != null && !FormatUtils.checkHourFormat(strDuration)) {
            out.print(FormatUtils.getScript("The format of duration is incorrect!"));
            return;
        }
        Document docLocal = CalendarUtils.getAllEntriesToDOM(strStart, strEnd, strDuration);
        Document docRemote = null;

        String sourcetype = request.getParameter("sourcetype");
        if ("local".equalsIgnoreCase(sourcetype)) {
            ;
        } else if ("remote".equalsIgnoreCase(sourcetype)) {
            String webservice = request.getParameter("webservice");
            if (webservice == null || "".equals(webservice)) {
                webservice = PropertyUtils.getWebserviceSlot();
            }
            HttpURLConnection.setFollowRedirects(true);
            String strRequest = "startdate=" + URLEncoder.encode(strStart, "UTF-8") + "&enddate="
                    + URLEncoder.encode(strEnd, "UTF-8") + "&duration=" + URLEncoder.encode(strDuration, "UTF-8");
            URL url = new URL(webservice + "?" + strRequest);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "text/html");
            connection.setRequestProperty("charset", "utf-8");
            connection.connect();
            if (connection.getResponseCode() != HttpServletResponse.SC_OK) {
                connection.getResponseMessage();
                out.print(FormatUtils.getScript("Web service is incorrect!"));
                return;
            }
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
                docRemote = docBuilder.parse(connection.getInputStream());
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                out.print(FormatUtils.getScript("The content from web service is incorrect!"));
                return;
            }
        }
        Map<String, List<EntryType>> mapEntries = CalendarUtils.combineSchedule(docLocal, docRemote);
        // header
        out.print("<html>");
        out.print("<head>");
        out.print("<link rel=\"stylesheet\" href=\"css/calendar.css\" />");
        out.print("<title>Entry List</title>");
        out.print("</head>");
        out.print("<body>");
        // entry-list
        out.print("<div class=\"entry-list-container\"><h2>Entry List</h2>");
        out.print("<table>");
        out.print("<tr>");
        out.print("<th>Date</th>");
        out.print("<th>Start time</th>");
        out.print("<th>Name</th>");
        out.print("<th>Duration</th>");
        // out.print("<th>Type</th>");
        out.print("</tr>");
        for (String key : mapEntries.keySet()) {
            for (EntryType entry : mapEntries.get(key)) {
                out.print("<tr>");
                out.print("<td>" + key + "</td>");
                String st = new SimpleDateFormat("HH:mm:ss").format(entry.getStarttime().toGregorianCalendar()
                        .getTime());
                out.print("<td>" + st + "</td>");
                out.print("<td>" + entry.getName() + "</td>");
                out.print("<td>" + entry.getDuration() + "</td>");
                out.print("</tr>");
            }
        }
        out.print("</table></div>");
        // free-slots
        out.print("<div class=\"free-slot-container\"><h2>Free Slots</h2>");
        Calendar cStartdate = Calendar.getInstance();
        Calendar cEnddate = Calendar.getInstance();
        try {
            cStartdate.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(strStart));
            cEnddate.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(strEnd));
            cEnddate.add(Calendar.DAY_OF_MONTH, 1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int durHour = Integer.parseInt(strDuration);
        if (durHour < 1 || durHour > 10) {
            out.print("<div>Duration should be between 1 and 10.</div>");
            return;
        }
        Calendar firstFreeSlot = CalendarUtils.getFirstFreeSlot(cStartdate, cEnddate, durHour, mapEntries);
        if (firstFreeSlot == null) {
            out.print("<div>No available free slots.</div>");
            return;
        }
        out.print("<div class=\"first-free-slot\">");
        String firstStart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(firstFreeSlot.getTime());
        firstFreeSlot.add(Calendar.HOUR_OF_DAY, durHour);
        String firstEnd = new SimpleDateFormat("HH:mm:ss").format(firstFreeSlot.getTime());
        firstFreeSlot.add(Calendar.HOUR_OF_DAY, -durHour);
        out.print("First available: " + firstStart + "~" + firstEnd + " (" + durHour + " hours)");
        out.print("</div>");
        out.print("<div class=\"free-slots\"><table border=\"1\">");
        out.print("<tr>");
        out.print("<th>Date</th>");
        for (int i = 0; i < WORK_HOUR; i++) {
            // int s = i * durHour + 8;
            // String str = String.format("%02d", s) + ":00 - " + String.format("%02d", (s + durHour)) + ":00";
            String str = String.format("%02d", i + 8) + ":00 - " + String.format("%02d", (i + 1 + 8)) + ":00";
            out.print("<th>" + str + "</th>");
        }
        out.print("</tr>");
        int count = 1;
        for (Calendar c = (Calendar) cStartdate.clone(); c.before(cEnddate); c.add(Calendar.DAY_OF_MONTH, 1)) {
            out.print("<tr>");
            String date = new SimpleDateFormat("yyyyMMdd").format(c.getTime());
            out.print("<th>" + date + "</th>");
            if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                out.print("<td bgcolor=\"grey\" colspan=\"" + WORK_HOUR + "\">Weekends</td>");
            } else if (!mapEntries.containsKey(date)) {
                out.print("<td bgcolor=\"green\" colspan=\"" + WORK_HOUR + "\">" + (count++) + "</td>");
            } else {
                Calendar current = (Calendar) c.clone();
                current.set(Calendar.HOUR_OF_DAY, 8);
                current.set(Calendar.MINUTE, 0);
                current.set(Calendar.SECOND, 0);
                Calendar start = (Calendar) current.clone();
                Calendar end = (Calendar) start.clone();
                for (EntryType et : mapEntries.get(date)) {
                    Calendar etStart = (Calendar) c.clone();
                    etStart.set(Calendar.HOUR_OF_DAY, et.getStarttime().getHour());
                    etStart.set(Calendar.MINUTE, et.getStarttime().getMinute());
                    etStart.set(Calendar.SECOND, et.getStarttime().getSecond());
                    end = (Calendar) etStart.clone();
                    int d = end.get(Calendar.HOUR_OF_DAY) - start.get(Calendar.HOUR_OF_DAY);
                    if (d >= durHour) {
                        out.print("<td bgcolor=\"green\" colspan=\"" + d + "\">" + (count++) + "</td>");
                    } else if (d > 0) {
                        out.print("<td bgcolor=\"blue\" colspan=\"" + d + "\"> </td>");
                    }
                    int etDur = et.getDuration().getHours();
                    if (et.getDuration().getMinutes() > 0) {
                        etDur += 1;
                    }
                    out.print("<td bgcolor=\"red\" colspan=\"" + etDur + "\">" + et.getName() + "</td>");
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
                end.set(Calendar.HOUR_OF_DAY, 18);
                end.set(Calendar.MINUTE, 0);
                end.set(Calendar.SECOND, 0);
                int d = end.get(Calendar.HOUR_OF_DAY) - start.get(Calendar.HOUR_OF_DAY);
                if (d >= durHour) {
                    out.print("<td bgcolor=\"green\" colspan=\"" + d + "\">" + (count++) + "</td>");
                } else {
                    out.print("<td bgcolor=\"blue\" colspan=\"" + d + "\"> </td>");
                }
            }
            out.print("</tr>");
        }
        out.print("</table></div>");
        out.print("</div>");
        out.print("</body></html>");
    }

}
