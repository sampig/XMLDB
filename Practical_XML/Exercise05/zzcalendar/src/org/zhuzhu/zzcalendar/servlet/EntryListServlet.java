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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.zhuzhu.zzcalendar.util.CalendarUtils;
import org.zhuzhu.zzcalendar.util.FormatUtils;
import org.zhuzhu.zzcalendar.util.PropertyUtils;

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
            Document doc = CalendarUtils.getAllEntriesToDOM(strStart, strEnd, strDuration);
            Element root = doc.getDocumentElement();
            // add stylesheet declaration
            Node pi = doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"css/entrylist.xsl\"");
            doc.insertBefore(pi, root);

            String sourcetype = request.getParameter("sourcetype");
            if ("local".equalsIgnoreCase(sourcetype)) {
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
            } else if ("remote".equalsIgnoreCase(sourcetype)) {
                String webservice = request.getParameter("webservice");
                if (webservice == null || "".equals(webservice)) {
                    webservice = PropertyUtils.getWebserviceEntry();
                }
                // System.out.println("webservice:" + webservice);
                HttpURLConnection.setFollowRedirects(true);
                String strRequest = "startdate=" + URLEncoder.encode(strStart, "UTF-8") + "&enddate="
                        + URLEncoder.encode(strEnd, "UTF-8") + "&duration=" + URLEncoder.encode(strDuration, "UTF-8")
                        + "&sourcetype=local";
                URL url = new URL(webservice + "?" + strRequest);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Content-Type", "text/html");
                connection.setRequestProperty("charset", "utf-8");
                connection.connect();
                // OutputStream outputStream = connection.getOutputStream();
                // outputStream.write(requestBytes);
                // outputStream.flush();
                // outputStream.close();
                if (connection.getResponseCode() != HttpServletResponse.SC_OK) {
                    connection.getResponseMessage();
                    out.print(FormatUtils.getScript("Web service is incorrect!"));
                    return;
                }
                try {
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
                    Document docRemote = docBuilder.parse(connection.getInputStream());
                    // Node child = docRemote.getDocumentElement().getFirstChild();
                    Node scheduleRemote = docRemote.getDocumentElement().getElementsByTagName("schedule").item(0);
                    Node scheduleNew = doc.importNode(scheduleRemote, true);
                    // Attr typeAttr = doc.createAttribute("type");
                    // typeAttr.setValue("remote");
                    ((Element) scheduleNew).setAttribute("type", "remote");
                    ((Element) root.getElementsByTagName("schedule").item(0)).setAttribute("type", "local");
                    root.appendChild(scheduleNew);
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    // e.printStackTrace();
                    out.print(FormatUtils.getScript("The content from web service is incorrect!"));
                    return;
                }
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
                // BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),
                // "UTF-8"));
                // StringBuilder sb = new StringBuilder();
                // String line = null;
                // try {
                // while ((line = reader.readLine()) != null) {
                // sb.append(line + "\n");
                // }
                // } catch (IOException e) {
                // e.printStackTrace();
                // }
                // System.out.println(sb.toString());
                connection.getInputStream().close();
                out.flush();
                out.close();
            }
        }
    }

}
