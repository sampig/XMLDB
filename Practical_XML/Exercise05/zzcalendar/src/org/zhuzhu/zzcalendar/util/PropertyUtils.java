/*
 * Copyright (c) 2017, Chenfeng Zhu. All rights reserved.
 *
 */
package org.zhuzhu.zzcalendar.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Some utilities for properties file.
 *
 * @author Chenfeng Zhu
 *
 */
public class PropertyUtils {

    /**
     * Key name for the xml file of schedule.
     */
    public final static String KEY_SCHEDULE_XML = "schedule.xml";
    /**
     * Key name for the xsd file of schedule.
     */
    public final static String KEY_SCHEDULE_XSD = "schedule.xsd";

    private final static String PROPERTY_FILE_NAME = "config.properties";

    private Properties properties;
    private InputStream inputStream;
    private FileOutputStream fileOutputStream;

    public PropertyUtils() {
        properties = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        inputStream = classLoader.getResourceAsStream(PROPERTY_FILE_NAME);
        if (inputStream != null) {
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("property file '" + PROPERTY_FILE_NAME + "' not found in the classpath");
        }
    }

    /**
     * Get the value of a given key.
     *
     * @param keyName
     * @return
     */
    public String getValue(String keyName) {
        if (properties == null) {
            return null;
        }
        return properties.getProperty(keyName);
    }

    /**
     * Change the value of a given key.
     *
     * @param keyName
     * @param value
     */
    public void setValue(String keyName, String value) {
        if (properties == null) {
            return;
        }
        try {
            properties.load(inputStream);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        properties.setProperty(keyName, value);
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            fileOutputStream = new FileOutputStream(classLoader.getResource(PROPERTY_FILE_NAME).getPath());
            properties.store(fileOutputStream, null);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the schedule xml with the default value.
     */
    public void setDefaultXML() {
        setValue(KEY_SCHEDULE_XML, DefaultConstants.SCHEDULE_XML_DEFAULT);
    }

    /**
     * Check whether the schedule xml is local.
     *
     * @return
     */
    public boolean checkLocalfile() {
        if (properties == null || properties.getProperty(KEY_SCHEDULE_XML) == null
                || properties.getProperty(KEY_SCHEDULE_XML).startsWith("http")) {
            return false;
        }
        return true;
    }

    /**
     * Get the default value of web service url for entry list.
     *
     * @return
     */
    public static String getWebserviceEntry() {
        return DefaultConstants.WEBSERVICE_ENTRY_URL_DEFAULT;
    }

    /**
     * Get the default value of web service url for free slot.
     *
     * @return
     */
    public static String getWebserviceSlot() {
        return DefaultConstants.WEBSERVICE_SLOTS_URL_DEFAULT;
    }

    /**
     * Some default constants.
     *
     * @author Chenfeng Zhu
     *
     */
    class DefaultConstants {
        protected final static String SCHEDULE_XML_DEFAULT = "/usr/workspace/xml/newschedule.xml";
        protected final static String WEBSERVICE_ENTRY_URL_DEFAULT = "http://localhost:8089/zzcalendar/entrylist";
        protected final static String WEBSERVICE_SLOTS_URL_DEFAULT = "http://localhost:8089/zzcalendar/freeslot";
    }

}
