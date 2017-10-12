/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignmentscoring.classify;
import com.assignmentscoring.config.ConnectionDB;
import com.assignmentscoring.config.UserException;
import static com.assignmentscoring.model.app.*;

import java.io.*;
import java.sql.*;
import java.util.*;
/**
 *
 * @author wootey02
 */
public class WriteClassifyPertextOnly extends com.assignmentscoring.model.app{
    private Writer writer = null;
    private String TestWriter = "",TrainWriter = "";
    private int numcol = 0;
    public int ps = 0,suf = 0;
    public ArrayList<String> ref_id;
    ArrayList<Integer> max_att;
    
    public void Writertrain(String pertext) throws SQLException, Exception,IOException{ 
        try {
            switch(pertext.trim()){
                case "10:90": ps = 10; suf = 90; break;
                case "20:80": ps = 20; suf = 80; break;
                case "30:70": ps = 30; suf = 70; break; 
                case "40:60": ps = 40; suf = 60; break;
                case "50:50": ps = 50; suf = 50; break;
                case "60:40": ps = 60; suf = 40; break;
                case "70:30": ps = 70; suf = 30; break;
                case "80:20": ps = 80; suf = 20; break;
                case "90:10": ps = 90; suf = 10; break;
                default:
                    return;
            }
            ResultSet rsk = (ResultSet) new ConnectionDB().connectoDB("SELECT * FROM center_param  WHERE ID = 9");
            int k = 0;
            while (rsk.next()) { 
                k = rsk.getInt("param_val");
            }
            
            ResultSet rsr = (ResultSet) new ConnectionDB().connectoDB("SELECT * FROM center_param  WHERE ID = 2");
            int rows = 0;
            while (rsr.next()) { 
                rows = rsr.getInt("param_val");
            }
            
            Hashtable has_k = new Hashtable<Integer,Integer>();
            for(int i=0;i<k;i++){
                ResultSet rsn = (ResultSet) new ConnectionDB().connectoDB("select CASE WHEN COUNT(*) = 0 THEN 1 ELSE COUNT(*) END AS CU from tfidfnormalized WHERE ID LIKE '%w%' AND GROUP_K = "+i);
                while (rsn.next()) {
                    double cal = rsn.getInt("CU")*(rows * ps / 100) / rows;
                    has_k.put(i,(int)cal);
                }
            }
            ResultSet rsx = (ResultSet) new ConnectionDB().connectoDB("SELECT * FROM center_param  WHERE ID = 1");
            int numx = 0;
            while (rsx.next()) { 
                numx = rsx.getInt("param_val");
            }
            
            if(conn != null)conn.close();
            
            numcol = numx;
            TrainWriter = "";
            String sql = sqlSelectdata();
            ref_id = new ArrayList<String>();
            max_att = new ArrayList<Integer>();
            for(int i=0;i<has_k.size();i++){
                setDataTrain(sql,(int)has_k.get(i),i);
            }
            
            String fileName = "";
            fileName = setFileName();
            if(conn != null)conn.close();
            
            if(!DEFAULT_PATH.equals("")){
                DEFAULT_PATH += "/";
            }
        
            writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(DEFAULT_PATH+"Train_"+fileName+".csv"), "utf-8"));
            writer.write(TrainWriter);               
            String sqlupdate = "INSERT INTO DATA_FILE(FILE_NAME,FILE_TYPE,FILE_DES) VALUES('Train_"+fileName+".csv',3,'"+TrainWriter+"');";
            new ConnectionDB().saveWordsToDB(sqlupdate);
            if(conn != null)conn.close();
        } 
        catch (IOException ex) {
            throw new IOException(ex);
        }
        catch(SQLException ex){
            throw new SQLException(ex);
        }
        catch(Exception ex){
            throw new Exception(ex);
        }
        finally {
           try {
               writer.close();
           } catch (Exception ex) {
               throw new Exception(ex);
           }
        }
    }
    
    public void setDataTrain(String sql,int rand,int group_k) throws SQLException, UserException, Exception{
        ResultSet rs = (ResultSet) new ConnectionDB().connectoDB("SELECT * FROM center_param  WHERE ID = 1");
        int num = 0;
        
        while (rs.next()) { 
            num = rs.getInt("param_val");
        }
        numcol = num;
        String strq = "Call random_rows('"+sql+"','tfidfnormalized',\" GROUP_K = "+group_k+" AND ID LIKE '%w%'\","+rand+");";
        ResultSet rsDF = (ResultSet) new ConnectionDB().connectoDB(strq);
        String k = "k",str = "";
        int n = 0;
        
        while (rsDF.next()) { 
            int cu = 0;
            String temp = "";
            for(int i=0;i<numcol;i++){
                if(!(""+rsDF.getDouble(k+i)).trim().equals("0.0")){
                    temp += i+":"+rsDF.getDouble(k+i)+" ";
                    cu++;
                }
            }
//            if(cu > 0){
                max_att.add(cu);
                ref_id.add("'"+rsDF.getString("ref_id")+"'");
                str += ""+rsDF.getDouble("score")+" "+temp;
                str += "\n";
//            }
            n++;
        }
        if(max_att.size() > 0){
            String strAtt  = "CALL setCenterParam(10,"+Collections.max(max_att)+",'Rows ที่มี Att มากที่สุด')";
            new ConnectionDB().saveWordsToDB(strAtt);
            TrainWriter += str;
        }
    }
    
    public void Writertest() throws SQLException, Exception{
        setDataTest();
        String fileName = "";
        fileName = setFileName();
        if(!DEFAULT_PATH.equals("")){
            DEFAULT_PATH += "/";
        }
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                  new FileOutputStream(DEFAULT_PATH+"Test_"+fileName+".csv"), "utf-8"));
            writer.write(TestWriter);
            String sqlupdate = "INSERT INTO DATA_FILE(FILE_NAME,FILE_TYPE,FILE_DES) VALUES('Test_"+fileName+".csv',4,'"+TestWriter+"');";
            new ConnectionDB().saveWordsToDB(sqlupdate);
        }
        catch (SQLException ex) {
            throw new SQLException(ex);
        }
        catch (IOException ex) {
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
    
    public void setDataTest() throws SQLException, UserException, Exception{
        ResultSet rs= (ResultSet) new ConnectionDB().connectoDB("SELECT * FROM center_param  WHERE ID = 1");
        int num = 0;
        while (rs.next()) { 
            num = rs.getInt("param_val");
        }
        numcol = num;
        String strq = "";
        for(int i=0;i<ref_id.size();i++){
            strq += ref_id.get(i)+",";
        }
        strq = strq.substring(0, strq.length()-1);
        String sql = sqlSelectdata();
        ResultSet rsDF = (ResultSet) new ConnectionDB().connectoDB("SELECT "+sql+" FROM tfidfnormalized  WHERE ID LIKE '%w%' AND ref_id not in("+strq+")");
        String k = "k",str = "";
        int n = 0;
        while (rsDF.next()) { 
            int cu = 0;
            String temp = "";
            for(int i=0;i<numcol;i++){
                if(!(""+rsDF.getDouble(k+i)).trim().equals("0.0")){
                    temp += i+":"+rsDF.getDouble(k+i)+" ";
                    cu++;
                }
            }
//            if(cu > 0){
                str += "-1 "+temp;
                str += "\n";
//            }
            n++;
        }
        if(conn != null)conn.close();
        TestWriter = str;
    }
    
    public String sqlSelectdata() throws SQLException{
        String sqlfold = " tfidfnormalized.ref_id,IFNULL(tfidfnormalized.score,0) AS score,";
        for(int i=0;i<numcol;i++){
            sqlfold += "k"+i+",";
        }
        sqlfold = sqlfold.substring(0, sqlfold.length()-1);
        return sqlfold;
    }
}
