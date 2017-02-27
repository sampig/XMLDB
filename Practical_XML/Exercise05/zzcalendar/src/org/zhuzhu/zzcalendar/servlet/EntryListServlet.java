/*
 * Copyright (c) 2017, Chenfeng Zhu. All rights reserved.
 *
 */
package org.zhuzhu.zzcalendar.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.zhuzhu.zzcalendar.util.CalendarUtils;

/**
 * Servlet implementation class EntryListServlet
 */
public class EntryListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EntryListServlet() {
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
        if (path.startsWith("/entrylist")) {
            String strStart = request.getParameter("startdate");
            String strEnd = request.getParameter("enddate");
            String strDuration = request.getParameter("duration");
            Document doc = CalendarUtils.getAllEntriesToDOM(strStart, strEnd, strDuration);
            Element root = doc.getDocumentElement();
            // add stylesheet declaration
            Node pi = doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"css/entrylist.xsl\"");
            doc.insertBefore(pi, root);
            PrintWriter out = response.getWriter();
            TransformerFactory factory = TransformerFactory.newInstance();
            Source docSource = new DOMSource(doc);
            StreamResult result = new StreamResult(out);
            // result = new StreamResult(System.out);
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
    }

}
