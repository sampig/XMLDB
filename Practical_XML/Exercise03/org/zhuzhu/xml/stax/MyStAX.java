/*
 * Copyright (c) 2016, Chenfeng Zhu. All rights reserved.
 * 
 */
package org.zhuzhu.xml.stax;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Exercise - StAX.
 * 
 * @author Chenfeng Zhu
 *
 */
public class MyStAX {

    private final static String MONDIAL_FILEPATH = "/afs/informatik.uni-goettingen.de/course/xml-lecture/Mondial/mondial.xml";
    private static String OUTPUT_FILEPATH1 = "/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/ex03/ex03_04aa.html";
    private static String OUTPUT_FILEPATH2 = "/afs/informatik.uni-goettingen.de/user/c/chenfeng.zhu/public_html/xml/ex03/ex03_04ae.html";

    public static void main(String... strings) {
        String sourcePath = null;
        String outputFile1 = null;
        String outputFile2 = null;
        if (strings.length >= 1) {
            sourcePath = strings[0];
        }
        if (strings.length >= 2) {
            outputFile1 = strings[1];
        }
        if (strings.length >= 3) {
            outputFile2 = strings[2];
        }
        MyStAX myStAX = new MyStAX();

        System.out.println("================================");
        System.out.println("Exercise 03_4a");
        System.out.println("--------a");
        myStAX.exercise3_4aa(sourcePath, outputFile1);
        System.out.println("--------e");
        myStAX.exercise3_4ae(sourcePath, outputFile2);
        System.out.println("================================\n\n");

        System.out.println("================================");
        System.out.println("Exercise 03_4b");
        myStAX.exercise3_4b(sourcePath);
        System.out.println("================================\n\n");

        System.out.println("================================");
        System.out.println("Exercise 03_4c");
        myStAX.exercise3_4c(sourcePath);
        try {
            Thread.sleep(90 * 1000); // there are 81 organizations to be displayed.
            System.out.println("================================\n\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void exercise3_4aa(String source, String target) {
        // source:
        String filepath = MONDIAL_FILEPATH;
        if (source != null && !("".equalsIgnoreCase(source))) {
            filepath = source;
        }
        System.out.println("Source XML File: " + filepath);

        // target:
        if (target == null || "".equalsIgnoreCase(target)) {
            target = OUTPUT_FILEPATH1;
        }
        if (!(new File(target)).exists()) {
            try {
                (new File(target)).createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Output XML File: " + target);

        try {
            MyStAXAAHandler handler = new MyStAXAAHandler(new FileInputStream(filepath), new FileOutputStream(target));
            handler.parse();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void exercise3_4ae(String source, String target) {
        // source:
        String filepath = MONDIAL_FILEPATH;
        if (source != null && !("".equalsIgnoreCase(source))) {
            filepath = source;
        }
        System.out.println("Source XML File: " + filepath);

        // target:
        if (target == null || "".equalsIgnoreCase(target)) {
            target = OUTPUT_FILEPATH2;
        }
        if (!(new File(target)).exists()) {
            try {
                (new File(target)).createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Output XML File: " + target);

        try {
            MyStAXAEHandler handler = new MyStAXAEHandler(new FileInputStream(filepath), new FileOutputStream(target));
            handler.parse();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void exercise3_4b(String source) {
        // source:
        String filepath = MONDIAL_FILEPATH;
        if (source != null && !("".equalsIgnoreCase(source))) {
            filepath = source;
        }
        System.out.println("Source XML File: " + filepath);

        try {
            MyStAXBHandler handler = new MyStAXBHandler(new FileInputStream(filepath), System.out);
            handler.parse();
            handler.printListOrg();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void exercise3_4c(String source) {
        // source:
        String filepath = MONDIAL_FILEPATH;
        if (source != null && !("".equalsIgnoreCase(source))) {
            filepath = source;
        }
        System.out.println("Source XML File: " + filepath + "\n\n");

        PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream();
        try {
            pis.connect(pos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            MyStAXBHandler wHandler = new MyStAXBHandler(new FileInputStream(filepath), pos);
            MyStAXCHandler rHandler = new MyStAXCHandler(pis, System.out);
            new Thread(wHandler).start();
            new Thread(rHandler).start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
