/*
 * Copyright (c) 2017, Chenfeng Zhu. All rights reserved.
 * 
 */
package org.zhuzhu.xml.digester;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.ObjectCreateRule;
import org.xml.sax.SAXException;

/**
 * Exercise - Digester.<br/>
 * Use 1 Digester to get Sea and RiverLake.
 * 
 * @author Chenfeng Zhu
 *
 */
public class MyDigester {

    private final static String INDENT = "    ";
    private final static int DEPTH = 20;

    private String sourcePath = "/usr/workspace/xml/mondial.xml"; // mondial_digester

    Map<String, SeaModel> mapSea = new HashMap<String, SeaModel>(0);
    Map<String, RiverLakeModel> mapRL = new HashMap<String, RiverLakeModel>(0);

    public static void main(String... strings) {
        String sourcePath = null;
        if (strings.length >= 1) {
            sourcePath = strings[0];
        }

        MyDigester digester = new MyDigester(sourcePath);
        digester.seaAllRiver();
    }

    public MyDigester(String source) {
        if (source != null && !("".equalsIgnoreCase(source))) {
            this.sourcePath = source;
        }
        System.out.println("Source XML File: " + sourcePath);
    }

    public void seaAllRiver() {
        File file = new File(sourcePath);
        Digester digester = new Digester();
        digester.push(this);

        digester.addRule("", new ObjectCreateRule(SeaModel.class) {
            public void end(String namespace, String name) throws Exception {
            };
        });
        digester.addObjectCreate("mondial/sea", SeaModel.class);
        digester.addSetProperties("mondial/sea", "id", "seaid");
        digester.addBeanPropertySetter("mondial/sea/name", "name");
        digester.addSetNext("mondial/sea", "addSea");

        digester.addRule("", new ObjectCreateRule(RiverLakeModel.class) {
            public void end(String namespace, String name) throws Exception {
            };
        });
        digester.addObjectCreate("mondial/river", RiverLakeModel.class);
        digester.addSetProperties("mondial/river", "id", "rlid");
        digester.addBeanPropertySetter("mondial/river/name", "name");
        digester.addBeanPropertySetter("mondial/river/length", "length");
        digester.addCallMethod("mondial/river/to", "addDestination", 1);
        digester.addCallParam("mondial/river/to", 0, "water");
        digester.addSetNext("mondial/river", "addRiverLake");
        digester.addObjectCreate("mondial/lake", RiverLakeModel.class);
        digester.addSetProperties("mondial/lake", "id", "rlid");
        digester.addBeanPropertySetter("mondial/lake/name", "name");
        digester.addCallMethod("mondial/lake/to", "addDestination", 1);
        digester.addCallParam("mondial/lake/to", 0, "water");
        digester.addSetNext("mondial/lake", "addRiverLake");

        try {
            digester.setValidating(false);
            digester.parse(file);
            // System.out.println(mapSea);
            // System.out.println(mapRL);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        this.reverseConnection();
        this.addSeaSource();

        printSea(mapSea.get("sea-Nordsee"));
        // print all sea.
        System.out.println("\n\nFinal Result:");
        for (String id : mapSea.keySet()) {
            SeaModel s = mapSea.get(id);
            printSea(s);
        }
    }

    public void addSea(SeaModel s) {
        mapSea.put(s.getSeaid(), s);
    }

    public void addRiverLake(RiverLakeModel rl) {
        mapRL.put(rl.getRlid(), rl);
    }

    public void reverseConnection() {
        for (RiverLakeModel rl : mapRL.values()) {
            for (String dest_id : rl.tos) {
                RiverLakeModel d = mapRL.get(dest_id);
                if (d != null)
                    d.addSource(rl);
            }
        }
    }

    public void addSeaSource() {
        for (SeaModel s : mapSea.values()) {
            if (s == null || s.getSeaid() == null) {
                continue;
            }
            for (String rlid : mapRL.keySet()) {
                RiverLakeModel rl = mapRL.get(rlid);
                if (rl.getRlid() != null && rl.getTos().contains(s.getSeaid())) {
                    s.addSource(rl);
                }
            }
        }
    }

    /**
     * Print the sea in a well-format.
     * 
     * @param sea
     */
    public void printSea(SeaModel sea) {
        System.out.println("-----------------------------------------");
        System.out.println(sea.toString() + " TotalLength: " + sea.getTransitiveLength());
        for (RiverLakeModel rl : sea.sources) {
            printRiverLake(rl, 1);
        }
        System.out.println();
    }

    /**
     * Print the river/lake in a well-format.
     * 
     * @param rl
     * @param depth
     */
    public void printRiverLake(RiverLakeModel rl, int depth) {
        if (depth > DEPTH) { // in case of endless recurse.
            return;
        }
        for (int i = 0; i < depth; i++) {
            System.out.print(INDENT);
        }
        System.out.println("-" + rl.toString() + " TotalLength: " + rl.getTransitiveLength(1));
        for (RiverLakeModel r : rl.sources) {
            printRiverLake(r, depth + 1);
        }

    }

}
