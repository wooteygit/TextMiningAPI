/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignmentscoring.directive;

import java.util.ArrayList;

/**
 *
 * @author wootey02
 */
public class ReturnTempTable {
    public int errCode = 0;
    private String errDesc = "";   
    private ArrayList<MapTempTable> map = new ArrayList<MapTempTable>();

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrDesc() {
        return errDesc;
    }

    public void setErrDesc(String errDesc) {
        this.errDesc = errDesc;
    }

    public ArrayList<MapTempTable> getMap() {
        return map;
    }

    public void setMap(ArrayList<MapTempTable> map) {
        this.map = map;
    }
    
}
