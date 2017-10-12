/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignmentscoring.classify;
import com.assignmentscoring.config.*;
import static com.assignmentscoring.model.app.*;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author wootey02
 */
public class WriteTestByCluster extends com.assignmentscoring.model.app{
    private Writer writer = null;
    private String TestWriter = "",TrainWriter = "";
    private int numcol = 0;
    private ArrayList<String> ref_id;
    private int group = 0;
    private int rand = 0;
    private double prec = 0.0;
    private int cluster = 0;

    public void Writertest(int k,int c ,double per) throws SQLException, Exception{
        group = k;
        prec = per;
        cluster = c;
        ResultSet rs = (ResultSet) new ConnectionDB().connectoDB("SELECT * FROM center_param  WHERE ID = 1");
        int num = 0;
        while (rs.next()) { 
            num = rs.getInt("param_val");
        }
        setDataTest();
        String fileName = "";
        fileName = setFileName();
        if(!DEFAULT_PATH.equals("")){
            DEFAULT_PATH += "/";
        }
        try {
            String testByGroup = "Test_"+fileName+"_"+group+"_"+cluster+".csv";
            writer = new BufferedWriter(new OutputStreamWriter(
                  new FileOutputStream(DEFAULT_PATH+testByGroup), "utf-8"));
            writer.write(TestWriter);
            String sqlupdate = "INSERT INTO DATA_FILE(FILE_NAME,FILE_TYPE,FILE_DES,CLUSTER) VALUES('"+testByGroup+"',7,'"+TestWriter+"',"+cluster+");";
            new ConnectionDB().saveWordsToDB(sqlupdate);
            if(conn != null)conn.close();
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
        rand = (int)(numOfCluster(cluster) * prec)/100;
        String sql = sqlSelectdata();    
        String sqlInsert = sqlInsertData();
        //String strq = "Call random_rows('"+sql+"','tfidfnormalized',\" GROUP_K = "+cluster+" AND ID LIKE '%w%'\","+rand+");";
        String strq = "call RAND_PER ('"+sql+"','"+sqlInsert+"',"+prec+")";
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
//            if(cu == 0){
                str += "-1 "+temp;
                str += "\n";
//            }
            n++;
        }
        if(conn != null)conn.close();
        TestWriter = str;
    }
    
    public String sqlSelectdata() throws SQLException{
        String sqlfold = " tfidfnormalized.ref_id AS id,";
        for(int i=0;i<numcol;i++){
            sqlfold += "k"+i+",";
        }
        sqlfold += "IFNULL(tfidfnormalized.score,0) AS score,";
        sqlfold = sqlfold.substring(0, sqlfold.length()-1);
        return sqlfold;
    }
    
    public String sqlInsertData() throws SQLException{
        String sqlfold = "";
        for(int i=0;i<numcol;i++){
            sqlfold += "k"+i+",";
        }
        sqlfold = sqlfold.substring(0, sqlfold.length()-1);
        return sqlfold;
    }
}
