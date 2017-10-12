/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignmentscoring.classify;

import com.assignmentscoring.model.*;
import static com.assignmentscoring.classify.AlgorithmClassify.featuresTesting;
import com.assignmentscoring.config.ConnectionDB;
import com.assignmentscoring.directive.*;
import com.assignmentscoring.model.app;
import static com.assignmentscoring.model.app.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import libsvm.*;

/**
 *
 * @author wootey02
 */
public class ClassifyModel extends app{
    static HashMap<Integer, HashMap<Integer, Double>> featuresTraining;
    static HashMap<Integer, Integer> labelTraining;
    static HashMap<Integer, HashMap<Integer, Double>> featuresTesting;
    static HashSet<Integer> features;
    static svm_problem prob;
    static svm_model model;
    static svm_parameter param;
    private Writer writer = null;
    
    public MapclassifyAns SVMclassifyTest(int sj_id,int p_sj_id,int sj_seq,int num_k,double perc,int ii) throws Exception,IOException,NullPointerException{
        int[] itemwrong = new int[2];
        int wrong = 0,corrcet=0;
        SetSomeID(ii);
        MapclassifyAns mcf = new MapclassifyAns();
        ArrayList<MapField> arrmp = new ArrayList<MapField>();
        MapField mp ;
        featuresTesting = new HashMap<Integer, HashMap<Integer, Double>>();
        BufferedReader reader = null;
        String strBestModel = "";
        String strBestModelPath = "";
        try{
            String sqlf = "SELECT FILE_NAME,FILE_DES FROM data_file WHERE FILE_TYPE = 7 AND CLUSTER = "+ii;
            ResultSet rsf = (ResultSet) new ConnectionDB().connectoDB(sqlf);
            while (rsf.next()) { 
                strBestModel = rsf.getString("FILE_DES");
                strBestModelPath = DEFAULT_PATH+rsf.getString("FILE_NAME");
            }
            reader = new BufferedReader(new FileReader(strBestModelPath));
            String line = null;
            int lineNum = 0;
            int count = 0;
            while((line = reader.readLine()) != null){ 
                featuresTesting.put(lineNum, new HashMap<Integer,Double>());
                String[] tokens = line.split("\\s+");
                for(int i=1;i<tokens.length;i++){
                    String[] fields = tokens[i].split(":");
                    int featureId = Integer.parseInt(fields[0]);
                    double featureValue = Double.parseDouble(fields[1]);
                    featuresTesting.get(lineNum).put(featureId, featureValue);
                }
                lineNum++;
                count++;
            }
            reader.close();
            
        /*Test Instance*/
            String bestModelPath = "";
            try {
                String sql = "SELECT bm_des,bm_name FROM best_model WHERE sj_id = "+sj_id+" AND p_sj_id = "+p_sj_id+" AND sj_seq = "+sj_seq+" AND num_k = "+num_k;
                ResultSet rs = (ResultSet) new ConnectionDB().connectoDB(sql);
                String strModel = "";
                while (rs.next()) { 
                    strModel = rs.getString("bm_des");
                    bestModelPath = DEFAULT_PATH+rs.getString("bm_name");
                }
                if(conn != null)conn.close();
                if(bestModelPath.equals(bestModelPath)){
                    writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(bestModelPath), "utf-8"));
                    writer.write(strModel); 
                }
            } 
            catch (IOException ex) {
                throw new IOException(ex);
            }catch(SQLException e){
                throw new SQLException(e);
            }
            finally {
                try {
                    writer.close();
                } catch (Exception ex) {
                    throw new Exception(ex);
                }
            }
            model = svm.svm_load_model(bestModelPath);
            BLANCE.clear();
            String sql = "SELECT tfidf.ID,tns.sd_ans AS ans_des " +
            " FROM  tfidf " +
            " INNER JOIN t_answer tns " +
            "  ON tfidf.id = tns.sd_id ";
            ResultSet rs = (ResultSet) new ConnectionDB().connectoDB(sql);
            int n = 0;
            while (rs.next()) {
                BLANCE.put(rs.getString("ID"),rs.getString("ans_des"));
            }
            if(conn != null) conn.close();
            
            int cu = 0;
            String str = "INSERT INTO t_student_score (sj_id,p_sj_id,sj_seq,num_k,cluster,sd_id,ans,num_score) VALUES ";
            for(Integer testInstance : featuresTesting.keySet()){                   
                HashMap<Integer, Double> tmp = new HashMap<Integer, Double>();
                tmp = featuresTesting.get(testInstance);
                int numFeatures = tmp.keySet().size();
                svm_node[] x = new svm_node[numFeatures];
                int featureIndx = 0;
                for(Integer feature : tmp.keySet()){
                    x[featureIndx] = new svm_node();
                    x[featureIndx].index = feature;
                    x[featureIndx].value = tmp.get(feature);
                    featureIndx++;
                }
                
                double d = svm.svm_predict(model, x);
                if(n <= sid.size()){
//                    mp = new MapField();
//                    mp.setName(sid.get(n));
//                    mp.setValue(""+d);
//                    mp.setDes(BLANCE.get(sid.get(n)).toString());
//                    arrmp.add(mp);
                    str += " ("+sj_id+","+p_sj_id+","+sj_seq+","+num_k+","+ii+",'"+sid.get(n)+"','"+BLANCE.get(sid.get(n)).toString()+"',"+d+"),";
                    cu++;
                }
                n++;
            }
            if(cu > 0){
                str = str.substring(0, str.length()-1);
                new ConnectionDB().saveWordsToDB(str);
            }
        }catch(NullPointerException en){
            throw new NullPointerException("SVMclassifyTest (Test Instance) : "+en);
        }catch(Exception e){
            throw new Exception("SVMclassifyTest (Test Instance) : "+e);
        }
        itemwrong[0] = corrcet;
        itemwrong[1] = wrong;
        mcf.setMp(arrmp);
        mcf.setItemwrong(itemwrong);
        return mcf;
    }
}
