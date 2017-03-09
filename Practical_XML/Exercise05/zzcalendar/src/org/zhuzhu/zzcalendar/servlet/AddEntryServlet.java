/*
 * Copyright (c) 2017, Chenfeng Zhu. All rights reserved.
 *
 */
package org.zhuzhu.zzcalendar.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zhuzhu.zzcalendar.util.CalendarUtils;
import org.zhuzhu.zzcalendar.util.FormatUtils;

/**
 * Servlet implementation class AddEntryServlet
 *
 * @author Chenfeng Zhu
 */
public class AddEntryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddEntryServlet() {
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
        PrintWriter out = response.getWriter();
        String strDate = request.getParameter("date");
        if (strDate == null || "".equals(strDate) || !FormatUtils.checkDateFormat(strDate)) {
            out.print(FormatUtils.getScript("The format of date is incorrect!"));
            return;
        }
        int year = Integer.parseInt(strDate.substring(0, 4));
        int month = Integer.parseInt(strDate.substring(5, 7));
        int day = Integer.parseInt(strDate.substring(8, 10));
        String strStarttime = request.getParameter("starttime");
        if (strStarttime == null || "".equals(strStarttime) || !FormatUtils.checkTimeFormat(strStarttime)) {
            out.print(FormatUtils.getScript("The format of start time is incorrect!"));
            return;
        }
        int hour = Integer.parseInt(strStarttime.substring(0, 2));
        int minute = Integer.parseInt(strStarttime.substring(3, 5));
        int second = Integer.parseInt(strStarttime.substring(6, 8));
        String strDuration = request.getParameter("duration");
        if (!FormatUtils.checkDurationFormat(strDuration)) {
            out.print(FormatUtils.getScript("The format of duration is incorrect!"));
            return;
        }
        String durHour = strDuration.substring(0, 2);
        String durMinute = strDuration.substring(3, 5);
        String duration = "PT" + durHour + "H" + durMinute + "M"; // PnYnMnDTnHnMnS
        String strTitle = request.getParameter("title");
        String strLocation = request.getParameter("location");
        String strDescription = request.getParameter("description");
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, minute, second);
        CalendarUtils.addEntry(calendar, duration, strTitle, strDescription, strLocation);
        response.sendRedirect("showcalendar?year=" + year + "&month=" + month + "&day=" + day);
    }

}
