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
/**
 *
 * @author wootey02
 */
public class WriteClassifyWithoutFold extends com.assignmentscoring.model.app{
    private Writer writer = null;
    private String TestWriter = "",TrainWriter = "";
    private int numcol = 0;
    
    public void Writertrain() throws SQLException, Exception{    
        ResultSet rsx= (ResultSet) new ConnectionDB().connectoDB("SELECT * FROM center_param  WHERE ID = 1");
        int numx = 0;
        while (rsx.next()) { 
            numx = rsx.getInt("param_val");
        }
        if(conn != null)conn.close();
        numcol = numx;
        String sql = sqlSelectdata();
        setDataTrain(sql);
        if(!DEFAULT_PATH.equals("")){
            DEFAULT_PATH += "/";
        }
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                  new FileOutputStream(DEFAULT_PATH+"TrainModel.csv"), "utf-8"));
            writer.write(TrainWriter);               
            String sqlupdate = "INSERT INTO DATA_FILE(FILE_NAME,FILE_TYPE,FILE_DES) VALUES('TrainModel.csv',5,'"+TrainWriter+"');";
            new ConnectionDB().saveWordsToDB(sqlupdate);
        } 
        catch (IOException ex) {
            throw new IOException(ex);
        }
        catch(SQLException ex){
            throw new SQLException(ex);
        }
        finally {
           try {
               writer.close();
           } catch (Exception ex) {
               throw new Exception(ex);
           }
        }
    }
    
    public void setDataTrain(String sql) throws SQLException, UserException, Exception{
        ResultSet rs = (ResultSet) new ConnectionDB().connectoDB("SELECT * FROM center_param  WHERE ID = 1");
        int num = 0;
        while (rs.next()) { 
            num = rs.getInt("param_val");
        }
        numcol = num;
        ResultSet rsDF = (ResultSet) new ConnectionDB().connectoDB("SELECT "+sql+" FROM FROM tfidfnormalized  WHERE ID LIKE '%w%'");
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
            if(cu > 0){
                str += ""+rsDF.getDouble("score")+" "+temp;
                str += "\n";
            }
            n++;
        }
        TrainWriter = str;
    }
    
    public void Writertest() throws SQLException, Exception{
        setDataTest();
        if(!DEFAULT_PATH.equals("")){
            DEFAULT_PATH += "/";
        }
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(DEFAULT_PATH+"TestModel.csv"), "utf-8"));
            writer.write(TestWriter);
            String sqlupdate = "INSERT INTO DATA_FILE(FILE_NAME,FILE_TYPE,FILE_DES) VALUES('TestModel.csv',6,'"+TestWriter+"');";
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
        ResultSet rs = (ResultSet) new ConnectionDB().connectoDB("SELECT * FROM center_param  WHERE ID = 1");
        int num = 0;
        while (rs.next()) { 
            num = rs.getInt("param_val");
        }
        numcol = num;
        String sql = sqlSelectdata();
        ResultSet rsDF = (ResultSet) new ConnectionDB().connectoDB("SELECT "+sql+" FROM tfidfnormalized  WHERE ID LIKE '%w%'");
        String k = "k",str = "";
        int n = 0;
        while (rsDF.next()) {
            int cu = 0;
            String temp = "";
            for(int i=0;i<numcol;i++){
                if((""+rsDF.getDouble(k+i)).trim().equals("0.0")){
                    temp += i+":"+rsDF.getDouble(k+i)+" ";
                    cu++;
                }
            }
            if(cu > 0){
                str += "-1 "+temp;
                str += "\n";
            }
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
