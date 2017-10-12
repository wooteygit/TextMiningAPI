package com.assignmentscoring.directive;

/**
 *
 * @author wootey02
 */
public class MapPreSVM {
	private int wrong = 0;
    private int corrcet = 0;
    private  String k = "";
    private String param = "";
    private int bm_id = 0;    

    public int getBm_id() {
        return bm_id;
    }

    public void setBm_id(int bm_id) {
        this.bm_id = bm_id;
    }

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }
    
    public int getWrong() {
        return wrong;
    }

    public void setWrong(int wrong) {
        this.wrong = wrong;
    }

    public int getCorrcet() {
        return corrcet;
    }

    public void setCorrcet(int corrcet) {
        this.corrcet = corrcet;
    }
}