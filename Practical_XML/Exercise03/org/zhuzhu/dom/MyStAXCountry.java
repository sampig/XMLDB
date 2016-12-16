package org.zhuzhu.dom;

import java.util.ArrayList;
import java.util.List;

public class MyStAXCountry {
    private String name;
    private String capital;
    private List<String> listCity = new ArrayList<String>(0);
    private List<Integer> listPopulation = new ArrayList<Integer>(0);

    public MyStAXCountry() {
        super();
        listCity = new ArrayList<String>(0);
        listPopulation = new ArrayList<Integer>(0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public List<String> getListCity() {
        return listCity;
    }

    public void setListCity(List<String> listCity) {
        this.listCity = listCity;
    }

    public List<Integer> getListPopulation() {
        return listPopulation;
    }

    public void setListPopulation(List<Integer> listPopulation) {
        this.listPopulation = listPopulation;
    }

    public int getAllCitiesNumber() {
        return listCity.size();
    }

    public int getPopulationSum() {
        int sum = 0;
        for (int p : listPopulation) {
            sum += p;
        }
        return sum;
    }

    public int getPopulationAvg() {
        return getPopulationSum() / getAllCitiesNumber();
    }

    public int getCapitalIndex() {
        int index = 0;
        for (int i = 0; i < listCity.size(); i++) {
            String city = listCity.get(i);
            if (city.equals(capital)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public int getCityClosestIndex() {
        int index = 0;
        int avg = getPopulationAvg();
        int temp = Math.abs(listPopulation.get(0) - avg);
        for (int i = 1; i < listPopulation.size(); i++) {
            int diff = Math.abs(listPopulation.get(i) - avg);
            if (diff < temp) {
                temp = diff;
                index = i;
            }
        }
        return index;
    }

    public boolean withEnoughValid() {
        boolean flag = false;
        int count = 0;
        for (int p : listPopulation) {
            if (p > 0) {
                count++;
            }
        }
        if (count >= 10) {
            flag = true;
        }
        return flag;
    }

}

