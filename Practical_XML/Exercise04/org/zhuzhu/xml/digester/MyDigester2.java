/*
 * Copyright (c) 2017, Chenfeng Zhu. All rights reserved.
 * 
 */
package org.zhuzhu.xml.digester;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
        digester.addRule("", new ObjectCreateRule(Sea.class) {
            public void end(String namespace, String name) throws Exception {
            };
        });
        digester.addObjectCreate("mondial/sea", Sea.class);
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
        digester2.addRule("", new ObjectCreateRule(RiverLake.class) {
            public void end(String namespace, String name) throws Exception {
            };
        });
        digester2.addObjectCreate("mondial/river", RiverLake.class);
        digester2.addSetProperties("mondial/river", "id", "rlid");
        digester2.addBeanPropertySetter("mondial/river/name", "name");
        digester2.addBeanPropertySetter("mondial/river/length", "length");
        digester2.addCallMethod("mondial/river/to", "addDestination", 1);
        digester2.addCallParam("mondial/river/to", 0, "water");
        digester2.addSetNext("mondial/river", "addRiverLake");
        digester2.addObjectCreate("mondial/lake", RiverLake.class);
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
            Sea s = seamap.get(id);
            printSea(s);
        }
    }

    /**
     * Print the sea in a well-format.
     * 
     * @param sea
     */
    public void printSea(Sea sea) {
        System.out.println(sea.toString() + " TotalLength: " + sea.getTransitiveLength());
        for (RiverLake rl : sea.sources) {
            printRiverLake(rl, 1);
        }
    }

    /**
     * Print the river/lake in a well-format.
     * 
     * @param rl
     * @param depth
     */
    public void printRiverLake(RiverLake rl, int depth) {
        if (depth > DEPTH) { // in case of endless recurse.
            return;
        }
        for (int i = 0; i < depth; i++) {
            System.out.print(INDENT);
        }
        System.out.println("-" + rl.toString() + " TotalLength: " + rl.getTransitiveLength(1));
        for (RiverLake r : rl.sources) {
            printRiverLake(r, depth + 1);
        }

    }

    /**
     * Model: Sea
     * 
     * @author Chenfeng Zhu
     *
     */
    public static class Sea {
        String seaid = null;
        String name = null;
        double length;
        Set<RiverLake> sources;

        public Sea() {
            sources = new HashSet<RiverLake>(0);
        }

        public String getSeaid() {
            return seaid;
        }

        public void setSeaid(String seaid) {
            this.seaid = seaid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getLength() {
            return length;
        }

        public void setLength(double length) {
            this.length = length;
        }

        public Set<RiverLake> getSources() {
            return sources;
        }

        public void setSources(Set<RiverLake> sources) {
            this.sources = sources;
        }

        public double getTransitiveLength() {
            double t_length = length;
            for (RiverLake r : sources) {
                t_length += r.getTransitiveLength(1);
            }
            return t_length;
        }

        public void addSource(RiverLake r) {
            sources.add(r);
        }

        public void addSources(RiverLakeMap map) {
            if (seaid == null) {
                return;
            }
            for (String rlid : map.keySet()) {
                RiverLake rl = map.get(rlid);
                if (rl.getRlid() != null && rl.getTos().contains(seaid)) {
                    this.addSource(rl);
                }
            }
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((seaid == null) ? 0 : seaid.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Sea other = (Sea) obj;
            if (seaid == null) {
                if (other.seaid != null)
                    return false;
            } else if (!seaid.equals(other.seaid))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return seaid + "(" + name + ")";
        }
    }

    /**
     * A map for sea.
     * 
     * @author Chenfeng Zhu
     *
     */
    public static class SeaMap extends HashMap<String, Sea> {

        private static final long serialVersionUID = -3383002236091475738L;

        public void addSea(Sea s) {
            this.put(s.getSeaid(), s);
        }

        public void addSources(RiverLakeMap map) {
            for (Sea s : values()) {
                s.addSources(map);
            }
        }
    }

    /**
     * Model: River|Lake.
     * 
     * @author Chenfeng Zhu
     *
     */
    public static class RiverLake {
        String rlid = null;
        String name = null;
        double length;
        Set<RiverLake> sources;
        Set<String> tos;

        public RiverLake() {
            this.sources = new HashSet<RiverLake>(0);
            this.tos = new HashSet<String>(0);
        }

        public String getRlid() {
            return rlid;
        }

        public void setRlid(String rlid) {
            this.rlid = rlid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getLength() {
            return length;
        }

        public void setLength(double length) {
            this.length = length;
        }

        public Set<RiverLake> getSources() {
            return sources;
        }

        public void setSources(Set<RiverLake> sources) {
            this.sources = sources;
        }

        public Set<String> getTos() {
            return tos;
        }

        public void setTos(Set<String> tos) {
            this.tos = tos;
        }

        public double getTransitiveLength(int depth) {
            if (depth > DEPTH) { // in case of endless recurse.
                return 0;
            }
            double t_length = length;
            for (RiverLake r : sources) {
                t_length += r.getTransitiveLength(depth + 1);
            }
            return t_length;
        }

        public void addSource(RiverLake r) {
            if (true && (r.getRlid() == null || tos.contains(r.getRlid()))) {
                return;
            }
            sources.add(r);
        }

        public void addDestination(String water) {
            tos.add(water);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((rlid == null) ? 0 : rlid.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            RiverLake other = (RiverLake) obj;
            if (rlid == null) {
                if (other.rlid != null) {
                    return false;
                }
            } else if (!rlid.equals(other.rlid)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return rlid + "(" + name + "," + length + ")";
        }

    }

    /**
     * A map for river/lake.
     * 
     * @author Chenfeng Zhu
     *
     */
    public static class RiverLakeMap extends HashMap<String, RiverLake> {

        private static final long serialVersionUID = 1314892391752832612L;

        public void addRiverLake(RiverLake rl) {
            this.put(rl.getRlid(), rl);
        }

        public void reverseConnection() {
            for (RiverLake rl : values()) {
                for (String dest_id : rl.tos) {
                    RiverLake d = get(dest_id);
                    if (d != null && !d.tos.contains(rl.rlid)) { // in case of endless recurse.
                        d.addSource(rl);
                    }
                }
            }
        }

    }

}
