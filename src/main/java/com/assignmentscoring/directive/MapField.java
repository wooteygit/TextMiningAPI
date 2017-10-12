/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignmentscoring.directive;
/**
 *
 * @author wootey02
 */
public class MapField {
    String name ="";
    String value = "";
    String des = "";

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    
    @Override
    public String toString() {
            return "MapData [name=" + name + ", value=" + value + "]";
    }
}
