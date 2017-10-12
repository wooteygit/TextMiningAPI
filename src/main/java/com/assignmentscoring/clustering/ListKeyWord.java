/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.assignmentscoring.clustering;

import java.util.ArrayList;

/**
 *
 * @author wootey02
 */
public class ListKeyWord {
    public ArrayList<String> keyword=new ArrayList<String>();
    public ListKeyWord(ArrayList<String> word){
        keyword=word;
    }
    public ArrayList<String> keyWord(){
        return keyword;
    }
}
