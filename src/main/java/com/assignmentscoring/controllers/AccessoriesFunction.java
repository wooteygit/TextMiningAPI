/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignmentscoring.controllers;
import  com.assignmentscoring.directive.*;
import com.assignmentscoring.config.*;
import java.util.*;
import javafx.print.Collation;
import java.io.*;
import java.sql.*;
/**
 
 * @author SBD
 */
public class AccessoriesFunction  extends com.assignmentscoring.model.app{
    private Writer writer = null;
    
    public int getMax(SubSvmKernelFlot degree,SubSvmKernelFlot gamma,SubSvmKernelFlot coef0,SubSvmKernelFlot cast,SubSvmKernelFlot nu,
    SubSvmKernelFlot epsilon,SubSvmKernelFlot p){
        int max = 3;
        ArrayList<Integer> arr = new ArrayList<Integer>();
        arr.add( (int)((degree.getStop()-degree.getStart()+degree.getStep())/((degree.getStep()==0)? 1 : degree.getStep())) );
        arr.add( (int)((gamma.getStop()-gamma.getStart()+gamma.getStep())/((gamma.getStep()==0)? 1 : gamma.getStep())) );
        arr.add( (int)((coef0.getStop()-coef0.getStart()+coef0.getStep())/((coef0.getStep()==0)? 1 : coef0.getStep())) );
        arr.add( (int)((cast.getStop()-cast.getStart()+cast.getStep())/((cast.getStep()==0)? 1 : cast.getStep())) );
        arr.add( (int)((nu.getStop()-nu.getStart()+nu.getStep())/((nu.getStep()==0)? 1 : nu.getStep())) );
        arr.add( (int)((epsilon.getStop()-epsilon.getStart()+epsilon.getStep())/((epsilon.getStep()==0)? 1 : epsilon.getStep())) );
        arr.add( (int)((p.getStop()-p.getStart()+p.getStep())/((p.getStep()==0)? 1 : p.getStep())) );
        max = Collections.max(arr);
        return max;
    }

    public void genFileAll(String des,String filename,String fileType) throws IOException, Exception{
        if(!DEFAULT_PATH.equals("")){
            DEFAULT_PATH += "/";
        }
        try {
            if(fileType.equals(""))
                fileType = "utf-8";
            writer = new BufferedWriter(new OutputStreamWriter(
                  new FileOutputStream(DEFAULT_PATH+filename), fileType));
            writer.write(des);
        }catch (IOException ex) {
            throw new IOException(ex);
        } 
        finally {
           try {
               writer.close();
           } catch (Exception ex) {
               throw new Exception(ex);
           }
        }
    }   
}
