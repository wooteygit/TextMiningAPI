package com.assignmentscoring.directive;

import java.util.ArrayList;

/**
 *
 * @author wootey02
 */
public class ReturnPreSVM {
    public int errCode = 0;
    private String errDesc = "";   
    private ArrayList<MapPreSVM> rs = new ArrayList<MapPreSVM>();

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

     public ArrayList<MapPreSVM> getRs() {
        return rs;
    }

    public void setRs(ArrayList<MapPreSVM> rs) {
        this.rs = rs;
    }  
}