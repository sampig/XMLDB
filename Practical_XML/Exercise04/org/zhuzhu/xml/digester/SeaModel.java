/*
 * Copyright (c) 2017, Chenfeng Zhu. All rights reserved.
 * 
 */
package org.zhuzhu.xml.digester;

import java.util.HashSet;
import java.util.Set;

/**
 * Model: Sea
 * 
 * @author Chenfeng Zhu
 *
 */
public class SeaModel {

    String seaid = null;
    String name = null;
    double length;
    Set<RiverLakeModel> sources;

    public SeaModel() {
        sources = new HashSet<RiverLakeModel>(0);
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

    public Set<RiverLakeModel> getSources() {
        return sources;
    }

    public void setSources(Set<RiverLakeModel> sources) {
        this.sources = sources;
    }

    public double getTransitiveLength() {
        double t_length = length;
        for (RiverLakeModel r : sources) {
            t_length += r.getTransitiveLength(1);
        }
        return t_length;
    }

    public void addSource(RiverLakeModel r) {
        sources.add(r);
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
        SeaModel other = (SeaModel) obj;
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
