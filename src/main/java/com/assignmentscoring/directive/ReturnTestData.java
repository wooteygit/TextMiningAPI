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
public class ReturnTestData {
    public int errCode = 0;
    private String errDesc = ""; 
    private ArrayList<ArrayList<MapField>> mf ;
    private MapTestData mtd;

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

    public ArrayList<ArrayList<MapField>> getMf() {
        return mf;
    }

    public void setMf(ArrayList<ArrayList<MapField>> mf) {
        this.mf = mf;
    }

    public MapTestData getMtd() {
        return mtd;
    }

    public void setMtd(MapTestData mtd) {
        this.mtd = mtd;
    }
    
}
