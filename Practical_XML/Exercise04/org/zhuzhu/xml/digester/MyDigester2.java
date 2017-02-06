/*
 * Copyright (c) 2017, Chenfeng Zhu. All rights reserved.
 * 
 */
package org.zhuzhu.xml.digester;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.ObjectCreateRule;
import org.xml.sax.SAXException;

/**
 * Exercise - Digester.<br/>
 * Use 2 Digesters to get Sea and RiverLake.
 * 
 * @author Chenfeng Zhu
 *
 */
public class MyDigester2 {

    private String sourcePath = "/usr/workspace/xml/mondial.xml"; // mondial_digester

    private final static String INDENT = "    ";
    private final static int DEPTH = 20;

    public static void main(String... strings) {
        String sourcePath = null;
        if (strings.length >= 1) {
            sourcePath = strings[0];
        }

        MyDigester2 digester = new MyDigester2(sourcePath);
        digester.seaAllRiver();
    }

    public MyDigester2(String source) {
        if (source != null && !("".equalsIgnoreCase(source))) {
            this.sourcePath = source;
        }
        System.out.println("Source XML File: " + sourcePath);
    }

    /**
     * Get all sea and river.
     */
    public void seaAllRiver() {
        File file = new File(sourcePath);
        SeaMap seamap = null;
        RiverLakeMap riverlakemap = null;

        // read all sea.
        Digester digester = new Digester();
        digester.push(new SeaMap());
        digester.addRule("", new ObjectCreateRule(SeaModel.class) {
            public void end(String namespace, String name) throws Exception {
            };
        });
        digester.addObjectCreate("mondial/sea", SeaModel.class);
        digester.addSetProperties("mondial/sea", "id", "seaid");
        digester.addBeanPropertySetter("mondial/sea/name", "name");
        digester.addSetNext("mondial/sea", "addSea");
        try {
            digester.setValidating(false);
            seamap = digester.parse(file);
            // System.out.println(seamap);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        // read all rivers or lakes.
        Digester digester2 = new Digester();
        digester2.push(new RiverLakeMap());
        digester2.addRule("", new ObjectCreateRule(RiverLakeModel.class) {
            public void end(String namespace, String name) throws Exception {
            };
        });
        digester2.addObjectCreate("mondial/river", RiverLakeModel.class);
        digester2.addSetProperties("mondial/river", "id", "rlid");
        digester2.addBeanPropertySetter("mondial/river/name", "name");
        digester2.addBeanPropertySetter("mondial/river/length", "length");
        digester2.addBeanPropertySetter("mondial/river/estuary/elevation", "elevationEstuary");
        digester2.addCallMethod("mondial/river/to", "addDestination", 1);
        digester2.addCallParam("mondial/river/to", 0, "water");
        digester2.addSetNext("mondial/river", "addRiverLake");
        digester2.addObjectCreate("mondial/lake", RiverLakeModel.class);
        digester2.addSetProperties("mondial/lake", "id", "rlid");
        digester2.addBeanPropertySetter("mondial/lake/name", "name");
        digester2.addCallMethod("mondial/lake/to", "addDestination", 1);
        digester2.addCallParam("mondial/lake/to", 0, "water");
        digester2.addSetNext("mondial/lake", "addRiverLake");
        try {
            digester2.setValidating(false);
            riverlakemap = digester2.parse(file);
            riverlakemap.reverseConnection();
            // System.out.println(riverlakemap);
            printRiverLake(riverlakemap.get("river-Rhein"), 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        // add river to sea's source.
        seamap.addSources(riverlakemap);

        printSea(seamap.get("sea-Nordsee"));
        // print all sea.
        for (String id : seamap.keySet()) {
            SeaModel s = seamap.get(id);
            printSea(s);
        }
    }

    /**
     * Print the sea in a well-format.
     * 
     * @param sea
     */
    public void printSea(SeaModel sea) {
        System.out.println(sea.toString() + " TotalLength: " + sea.getTransitiveLength());
        for (RiverLakeModel rl : sea.sources) {
            printRiverLake(rl, 1);
        }
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

    /**
     * A map for sea.
     * 
     * @author Chenfeng Zhu
     *
     */
    public static class SeaMap extends HashMap<String, SeaModel> {

        private static final long serialVersionUID = -3383002236091475738L;

        public void addSea(SeaModel s) {
            this.put(s.getSeaid(), s);
        }

        public void addSources(RiverLakeMap map) {
            for (SeaModel s : values()) {
                if (s == null || s.getSeaid() == null) {
                    continue;
                }
                for (String rlid : map.keySet()) {
                    RiverLakeModel rl = map.get(rlid);
                    if (rl.getRlid() != null && rl.getTos().contains(s.getSeaid())) {
                        s.addSource(rl);
                    }
                }
            }
        }
    }

    /**
     * A map for river/lake.
     * 
     * @author Chenfeng Zhu
     *
     */
    public static class RiverLakeMap extends HashMap<String, RiverLakeModel> {

        private static final long serialVersionUID = 1314892391752832612L;

        public void addRiverLake(RiverLakeModel rl) {
            this.put(rl.getRlid(), rl);
        }

        public void reverseConnection() {
            for (RiverLakeModel rl : values()) {
                for (String dest_id : rl.tos) {
                    RiverLakeModel d = get(dest_id);
                    if (d != null && !d.tos.contains(rl.rlid)) { // in case of endless recurse.
                        d.addSource(rl);
                    }
                }
            }
        }

    }

}
