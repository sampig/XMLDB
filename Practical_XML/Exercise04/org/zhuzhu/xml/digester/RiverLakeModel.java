/*
 * Copyright (c) 2017, Chenfeng Zhu. All rights reserved.
 * 
 */
package org.zhuzhu.xml.digester;

import java.util.HashSet;
import java.util.Set;

/**
 * Model: River|Lake.
 * 
 * @author Chenfeng Zhu
 *
 */
public class RiverLakeModel {

    private final static int DEPTH = 20;

    String rlid = null;
    String name = null;
    double length;
    Set<RiverLakeModel> sources;
    Set<String> tos;

    public RiverLakeModel() {
        this.sources = new HashSet<RiverLakeModel>(0);
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

    public Set<RiverLakeModel> getSources() {
        return sources;
    }

    public void setSources(Set<RiverLakeModel> sources) {
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
        for (RiverLakeModel r : sources) {
            t_length += r.getTransitiveLength(depth + 1);
        }
        return t_length;
    }

    public void addSource(RiverLakeModel r) {
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
        RiverLakeModel other = (RiverLakeModel) obj;
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
