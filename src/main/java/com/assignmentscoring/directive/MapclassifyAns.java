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
public class MapclassifyAns {
    private int[] itemwrong = new int[2];
    ArrayList<MapField> mp;

    public int[] getItemwrong() {
        return itemwrong;
    }

    public void setItemwrong(int[] itemwrong) {
        this.itemwrong = itemwrong;
    }

    public ArrayList<MapField> getMp() {
        return mp;
    }

    public void setMp(ArrayList<MapField> mp) {
        this.mp = mp;
    }
    
    
}
