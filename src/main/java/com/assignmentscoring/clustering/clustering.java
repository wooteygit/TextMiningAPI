/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignmentscoring.clustering;

import com.assignmentscoring.config.*;
import java.util.*;
import java.io.*;
import java.sql.*;
import weka.classifiers.*;
import weka.core.*;
import weka.clusterers.*;
import weka.experiment.InstanceQuery;
import weka.core.converters.ConverterUtils.DataSource;
import weka.clusterers.ClusterEvaluation;
import weka.experiment.DatabaseUtils;
import weka.experiment.InstanceQuery;
import weka.filters.Filter;
import weka.core.*;
import libsvm.*;

/**
 *
 * @author wootey02
 */
public class clustering extends com.assignmentscoring.model.app{
    public  void kmeans() throws Exception{
        int numcol = getMaxCol(TFIDF);
        int tf = TF.size();
        int custer = GROUP;
        SimpleKMeans kmeans;
        ArrayList<Double>SquaredError = new ArrayList<Double>();
        ArrayList<Double>TemSquaredError = new ArrayList<Double>();
        InstanceQuery query;
        String sql = sqlSelectdata(numcol,tf);
        query = new InstanceQuery();
        query.setDatabaseURL(url);
        query.setUsername(usrName);
        query.setPassword(pass);
        query.setQuery(sql);
        query.setSparseData(true);
        Instances data = query.retrieveInstances();
        double tem = 0;
        for(int i = 0;i < tf; i++){
            kmeans = new SimpleKMeans();
            kmeans.setSeed(i);
            kmeans.setPreserveInstancesOrder(true);
            kmeans.setNumClusters(custer);
            kmeans.buildClusterer(data);
            tem = kmeans.getSquaredError();
            SquaredError.add(tem);
            TemSquaredError.add(tem);
        }
//        String str1="ก่อนเรียง = ",str2="หลังเรียง = ";
//        for(int z = 0;z < SquaredError.size();z++){
//            str1 += SquaredError.get(z)+" , ";
//        }
//        Collections.sort(SquaredError);
//        for(int z = 0;z<SquaredError.size();z++){
//            str2 += SquaredError.get(z)+" , ";
//        }
        int index = TemSquaredError.indexOf(SquaredError.get(0));
        kmeans = new SimpleKMeans();
        kmeans.setSeed(index);
        kmeans.setPreserveInstancesOrder(true);
        kmeans.setNumClusters(custer);
        kmeans.buildClusterer(data); 
        int[] assignments = kmeans.getAssignments();
        int j=0;
        /*"#####################K-means Clustering#####################"*/
        for(int clusterNum : assignments) {
            String sqlupdate="UPDATE tfidfNormalized SET `group`="+clusterNum+" WHERE ID = '"+sid.get(j)+"';";
            new ConnectionDB().saveWordsToDB(sqlupdate);
            sqlupdate="UPDATE tfidfNormalized SET group_k="+clusterNum+" WHERE ID = '"+sid.get(j)+"';";
            new ConnectionDB().saveWordsToDB(sqlupdate);
            String sqlupdateClass="UPDATE tfidfNormalized SET `group`="+clusterNum+",ref_id = '"+sid.get(j)+"' WHERE ID = 'w"+j+"';";
            new ConnectionDB().saveWordsToDB(sqlupdateClass);
            sqlupdateClass="UPDATE tfidfNormalized SET group_k="+clusterNum+",ref_id = '"+sid.get(j)+"' WHERE ID = 'w"+j+"';";
            new ConnectionDB().saveWordsToDB(sqlupdateClass);
//            System.out.println("รหัส "+sid.get(j)+" ==> "+clusterNum);
            j++;
        }
        /*"#####################K-means Clustering#####################"*/
//        ClusterEvaluation eval = new ClusterEvaluation();
//        eval.setClusterer(kmeans);
//        eval.evaluateClusterer(data);
//        System.out.println(eval.clusterResultsToString());
        //new validation().sumgroup(custer,sid);
    }
    
    public String sqlSelectdata(int numcol,int tf){
        String sql = "SELECT ",w = "";
        for(int i=0;i<numcol;i++){
            sql += "k"+i+",";
        }
        for(int i=0;i<tf;i++){
            w += "'w"+i+"',";
        }
        sql = sql.substring(0, sql.length()-1);
        w = w.substring(0, w.length()-1);
        sql += " FROM tfidfnormalized WHERE ID IN("+w+")";
        return sql;
    }        
}
