/*
 * Copyright (c) 2017, Chenfeng Zhu. All rights reserved.
 *
 */
package org.zhuzhu.zzcalendar.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zhuzhu.zzcalendar.util.PropertyUtils;

/**
 * Servlet implementation class ConfigPropertiesServlet
 *
 * @author Chenfeng Zhu
 */
public class ConfigPropertiesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConfigPropertiesServlet() {
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
        String path = request.getPathInfo();
        if (path == null) {
            path = request.getServletPath();
        }
        if (path.startsWith("/changexml")) { // change the schedule xml.
            String xmlpath = request.getParameter("xmlpath");
            PropertyUtils pu = new PropertyUtils();
            pu.setValue(PropertyUtils.KEY_SCHEDULE_XML, xmlpath);
        } else if (path.startsWith("/resetxml")) { // change the schedule xml to default.
            PropertyUtils pu = new PropertyUtils();
            pu.setDefaultXML();
        }
    }

}
