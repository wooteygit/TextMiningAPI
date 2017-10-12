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
public class ReturnDataFile {
    private int errCode = 0;
    private String errDesc = "";   
    private ArrayList<MapDataFile> map = new ArrayList<MapDataFile>();

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

    public ArrayList<MapDataFile> getMap() {
        return map;
    }

    public void setMap(ArrayList<MapDataFile> map) {
        this.map = map;
    }
}
