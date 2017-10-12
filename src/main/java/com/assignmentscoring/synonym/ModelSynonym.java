/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignmentscoring.synonym;

/**
 *
 * @author wootey02
 */
public class ModelSynonym {
    public  String word = "";
    public  String means = "";
    
    public  ModelSynonym(String w,String m){
        word = w;
        means = m;
    }
    public  String getWord(){
        return word;
    }
    public  String getMeans(){
        return means;
    }
}
