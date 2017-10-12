/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignmentscoring.classify;
//import assignmentscoring.AssignmentScoring.*;

import com.assignmentscoring.config.*;

import java.io.*;
import java.sql.*;
/**
 *
 * @author wootey02
 */
public class WriterClassify extends com.assignmentscoring.model.app{
    Writer writer = null;
    String TestWriter = "",TrainWriter = "";
    int numcol = 0;
    int temp_for_start = 0;
    int numrow = 0;
    
    public void Writertrain(String pretext,int k,int fold_clussify) throws SQLException, Exception{
        comboPerText = pretext;
        FOLD_CLASSIFY = fold_clussify;
        String wheregroup = "SELECT COUNT(*) AS CU FROM tfidfnormalized  WHERE ID LIKE '%w%'";
        if(k != -1){
            GROUP = k;
            wheregroup += " AND `group` = "+GROUP;
        }       
        ResultSet rsn= (ResultSet) new ConnectionDB().connectoDB(wheregroup);
        while (rsn.next()) {
            numrow = rsn.getInt("CU");
        }
        if(numrow < 5){
            FOLD_CLASSIFY = numrow;
        }
        int[] num_fold = new int[FOLD_CLASSIFY];
        int temp_num = numrow;
        do{
            for(int i=0;i<num_fold.length;i++){
                num_fold[i] = num_fold[i] + 1;
                temp_num = temp_num - 1;
                if(temp_num == 0){
                    break;
                }
            }
        }while(temp_num > 0);
        int startrow = 1;
        int ps = 1;
        switch(comboPerText.trim()){
            case "10:90": ps = 10; break;
            case "20:80": ps = 20; break;
            case "30:70": ps = 30; break; 
            case "40:60": ps = 40; break;
            case "50:50": ps = 50; break;
            case "60:40": ps = 60; break;
            case "70:30": ps = 70; break;
            case "80:20": ps = 80; break;
            case "90:10": ps = 90; break;
            default:
                return;
        }
        ResultSet rsx= (ResultSet) new ConnectionDB().connectoDB("SELECT * FROM center_param  WHERE ID = 1");
        int numx = 0;
        while (rsx.next()) { 
            numx = rsx.getInt("param_val");
        }
        numcol=numx;
//        double  preS = 0.00;
        double  fold_tran = 0.0;
        fold_tran = (double)Math.round(((double)FOLD_CLASSIFY * (double)ps)/100.00) == 0 ? 1 : Math.round(((double)FOLD_CLASSIFY * (double)ps)/100.00);
//        fold_tran = Math.round(preS);
        int tr_b_s = 0;// train befor start
        int tr_b_e = 0;// train befor end
        int tr_a_s = 0;// train befor start
        int tr_a_e = 0;// train befor end
        int te_b_s = 0;// test befor start
        int te_b_e = 0;// test befor end
        int te_a_s = 0;// test after start
        int te_a_e = 0;// test after end
        int[] flod= num_fold;
        int numflod = (int)fold_tran;
        int maxnum = 0;
        for(int i=0;i<flod.length;i++){
                maxnum+=flod[i];
        }
        int num = 0;
        for(int i=0;i<num_fold.length;i++){
            if(i == 0){
                num = 0;
                tr_a_s = 1;
                for(int j=0;j<numflod;j++){
                    num += flod[j];
                }
                tr_a_e = num;
                te_b_s = -1;
                te_b_e = -1;
                te_a_s = tr_a_e+1;
                num = 0;
                for(int j=numflod;j<flod.length;j++){
                    num += flod[(int)j];
                }
                te_a_e = ((te_a_s-1) + num);
                tr_b_s = -1;
                tr_b_e = -1;
            }else if(i == flod.length-1){
                num = 0;
                for(int j=0;j<i;j++){
                    num += flod[j];
                }
                tr_a_s = num+1;
                tr_a_e = maxnum;
                int n = 0;
                int cu = 0;
                tr_b_s = -1;
                tr_b_s = -1;
                if((numflod+i) > flod.length){
                    n = (numflod+i) - flod.length;
                    int jj = 0;
                    do{ 
                        cu += flod[jj];
                        jj++;
                    }while(jj < n);
                    tr_b_s = 1;
                    tr_b_e = cu;
                }
                te_b_s = 1;
                te_b_e = num;
                te_a_s = -1;
                te_a_e = -1;
            }else{
                num = 0;
                for(int j=0;j<i;j++){
                    num += flod[j];
                }
                tr_a_s = num+1;
                num = 0;
                int count = 0;
                do{
                    int j =i;
                    num += flod[j];
                    count ++;
                    j++;
                }while(count < numflod);
                tr_a_e = (tr_a_s-1)+num;
                tr_b_s = -1;
                tr_b_s = -1;
                int n = 0;
                int cu = 0;
                if((numflod+i) > flod.length){
                    tr_a_e = maxnum;
                    n = (numflod+i) - flod.length;
                    int jj = 0;
                    do{
                        cu += flod[jj];
                        jj++;
                    }while(jj < n);
                    tr_b_s = 1;
                    tr_b_s = cu;
                }
                te_b_s = 1;
                num = 0;
                for(int j=0;j<i;j++){
                    num += flod[j];
                }
                te_b_e = num;
                num = 0;
                te_a_s = tr_a_e + 1;
                for(int j=(i+numflod);j<flod.length;j++){
                    num += flod[j];
                }
                te_a_e = tr_a_e+num;
                if(te_a_s > maxnum){
                    te_a_s = -1;
                    te_a_e = -1;
                }	
            }
            String sql=sqlSelectdata();
            setDataTrain(tr_b_s,tr_b_e,tr_a_s,tr_a_e,sql);
            if(!DEFAULT_PATH.equals("")){
                DEFAULT_PATH += "/";
            }
            try {
                writer = new BufferedWriter(new OutputStreamWriter(
                      new FileOutputStream(DEFAULT_PATH+"Train"+i+".csv"), "utf-8"));
                writer.write(TrainWriter);               
                String sqlupdate = "INSERT INTO DATA_FILE(FILE_NAME,FILE_TYPE,FILE_DES) VALUES('Train"+i+".csv',1,'"+TrainWriter+"');";
                new ConnectionDB().saveWordsToDB(sqlupdate);
//                System.out.println("เขียนไฟล์ Train.csv เสร็จสิ้น");
            } 
            catch (IOException ex) {
                throw new IOException(ex);
//                JOptionPane.showMessageDialog(null, "เขียนไฟล์ Trian ล้มเหลว");
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
    }
    
    public void setDataTrain(int tr_b_s,int tr_b_e,int tr_a_s,int tr_a_e,String sql) throws SQLException, UserException, Exception{
        ResultSet rs= (ResultSet) new ConnectionDB().connectoDB("SELECT * FROM center_param  WHERE ID = 1");
        int num = 0;
        while (rs.next()) { 
            num = rs.getInt("param_val");
        }
        numcol=num;
//        System.out.println("CALL getFold("+GROUP+","+tr_b_s+","+tr_b_e+","+tr_a_s+","+tr_a_e+",'"+sql+"')");
        ResultSet rsDF= (ResultSet) new ConnectionDB().connectoDB("CALL getFold("+GROUP+","+tr_b_s+","+tr_b_e+","+tr_a_s+","+tr_a_e+",'"+sql+"')");
        String k="k",str = "";
        int n = 0;
        while (rsDF.next()) { 
//            if(n < temp_for_start){
                str += ""+rsDF.getInt("score")+" ";
                for(int i=0;i<numcol;i++){
                    str += i+":"+rsDF.getDouble(k+i)+" ";
                }
                str += "\n";
                n++;
//            }else{
//                break;
//            }
        }
//        System.out.println(str);
        TrainWriter = str;
    }
    
    public void Writertest(String pretext,int k,int fold_clussify) throws SQLException, Exception{
        comboPerText = pretext;
        FOLD_CLASSIFY = fold_clussify;
        String wheregroup = "SELECT COUNT(*) AS CU FROM tfidfnormalized  WHERE ID LIKE '%w%'";
        if(k != -1){
            GROUP = k;
            wheregroup += " AND `group` = "+GROUP;
        }       
        ResultSet rsn= (ResultSet) new ConnectionDB().connectoDB(wheregroup);
        
        while (rsn.next()) {
            numrow = rsn.getInt("CU");
        }
        if(numrow < 5){
            FOLD_CLASSIFY = numrow;
        }
        int[] num_fold = new int[FOLD_CLASSIFY];
        int temp_num = numrow;
        do{
            for(int i=0;i<num_fold.length;i++){
                num_fold[i] = num_fold[i] + 1;
                temp_num = temp_num - 1;
                if(temp_num == 0){
                    break;
                }
            }
        }while(temp_num > 0);
        int startrow = 1;
        int ps = 0;
        switch(comboPerText.trim()){
            case "10:90": ps = 10; break;
            case "20:80": ps = 20; break;
            case "30:70": ps = 30; break; 
            case "40:60": ps = 40; break;
            case "50:50": ps = 50; break;
            case "60:40": ps = 60; break;
            case "70:30": ps = 70; break;
            case "80:20": ps = 80; break;
            case "90:10": ps = 90; break;
            default:
//                JOptionPane.showMessageDialog(null, "กรุณาเลือก Test & Tran ใหม่");
                return;
        }
//        double  preS = 0.00;
        double  fold_tran = 0.0;
        fold_tran = Math.round(((double)FOLD_CLASSIFY * (double)ps)/100.00) == 0 ? 1 : Math.round(((double)FOLD_CLASSIFY * (double)ps)/100.00);
//        fold_tran = Math.round(preS);
        int tr_b_s = 0;// train befor start
        int tr_b_e = 0;// train befor end
        int tr_a_s = 0;// train befor start
        int tr_a_e = 0;// train befor end
        int te_b_s = 0;// test befor start
        int te_b_e = 0;// test befor end
        int te_a_s = 0;// test after start
        int te_a_e = 0;// test after end
        int[] flod= num_fold;
        int numflod = (int)fold_tran;
        int maxnum = 0;
        for(int i=0;i<flod.length;i++){
                maxnum+=flod[i];
        }
        int num = 0;
        for(int i=0;i<num_fold.length;i++){    
            if(i == 0){
                num = 0;
                tr_a_s = 1;
                for(int j=0;j<numflod;j++){
                    num += flod[j];
                }
                tr_a_e = num;
                te_b_s = -1;
                te_b_e = -1;
                te_a_s = tr_a_e+1;
                num = 0;
                for(int j=numflod;j<flod.length;j++){
                    num += flod[(int)j];
                }
                te_a_e = ((te_a_s-1) + num);
                tr_b_s = -1;
                tr_b_e = -1;
            }else if(i == flod.length-1){
                num = 0;
                for(int j=0;j<i;j++){
                    num += flod[j];
                }
                tr_a_s = num+1;
                tr_a_e = maxnum;
                int n = 0;
                int cu = 0;
                tr_b_s = -1;
                tr_b_s = -1;
                if((numflod+i) > flod.length){
                    n = (numflod+i) - flod.length;
                    int jj = 0;
                    do{ 
                        cu += flod[jj];
                        jj++;
                    }while(jj < n);
                    tr_b_s = 1;
                    tr_b_e = cu;
                }
                te_b_s = 1;
                te_b_e = num;
                te_a_s = -1;
                te_a_e = -1;
            }else{
                num = 0;
                for(int j=0;j<i;j++){
                    num += flod[j];
                }
                tr_a_s = num+1;
                num = 0;
                int count = 0;
                do{
                    int j =i;
                    num += flod[j];
                    count ++;
                    j++;
                }while(count < numflod);
                tr_a_e = (tr_a_s-1)+num;
                tr_b_s = -1;
                tr_b_s = -1;
                int n = 0;
                int cu = 0;
                if((numflod+i) > flod.length){
                    tr_a_e = maxnum;
                    n = (numflod+i) - flod.length;
                    int jj = 0;
                    do{
                        cu += flod[jj];
                        jj++;
                    }while(jj < n);
                    tr_b_s = 1;
                    tr_b_s = cu;
                }
                te_b_s = 1;
                num = 0;
                for(int j=0;j<i;j++){
                    num += flod[j];
                }
                te_b_e = num;
                num = 0;
                te_a_s = tr_a_e + 1;
                for(int j=(i+numflod);j<flod.length;j++){
                    num += flod[j];
                }
                te_a_e = tr_a_e+num;
                if(te_a_s > maxnum){
                    te_a_s = -1;
                    te_a_e = -1;
                }	
            }
            String sql = sqlSelectdata();
            setDataTest(te_b_s,te_b_e,te_a_s,te_a_e,sql);
            if(!DEFAULT_PATH.equals("")){
                DEFAULT_PATH += "/";
            }
            try {
                writer = new BufferedWriter(new OutputStreamWriter(
                      new FileOutputStream(DEFAULT_PATH+"Test"+i+".csv"), "utf-8"));
                writer.write(TestWriter);
                String sqlupdate = "INSERT INTO DATA_FILE(FILE_NAME,FILE_TYPE,FILE_DES) VALUES('Test"+i+".csv',2,'"+TestWriter+"');";
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
    }
    
    public void setDataTest(int te_b_s,int te_b_e,int te_a_s,int te_a_e,String sql) throws SQLException, UserException, Exception{
        ResultSet rs= (ResultSet) new ConnectionDB().connectoDB("SELECT * FROM center_param  WHERE ID = 1");
        int num = 0;
        while (rs.next()) { 
            num = rs.getInt("param_val");
        }
        numcol = num;
        ResultSet rsDF = (ResultSet) new ConnectionDB().connectoDB("CALL getFold("+GROUP+","+te_b_s+","+te_b_e+","+te_a_s+","+te_a_e+",'"+sql+"')");
        String k = "k",str = "";
        int n = 0;
        while (rsDF.next()) {
            str += "-9 ";
            for(int i=0;i<numcol;i++){
                str += i+":"+rsDF.getDouble(k+i)+" ";
            }
            str += "\n";
        }
        TestWriter = str;
    }
    
    public String sqlSelectdata() throws SQLException{
        String sqlfold = " tfidfnormalized.ref_id,ifnull(tfidfnormalized.score,0) AS score,";
        for(int i=0;i<numcol;i++){
            sqlfold += "k"+i+",";
        }
        sqlfold = sqlfold.substring(0, sqlfold.length()-1);
        return sqlfold;
    }
    
    public  String writeToFile(String pFilename, StringBuffer sb, String typeFile) throws IOException {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File tempFile = File.createTempFile(pFilename, "."+typeFile, tempDir);
        FileWriter fileWriter = new FileWriter(tempFile, true);
        BufferedWriter bw = new BufferedWriter(fileWriter);
        bw.write(sb.toString());
        bw.close();
        return System.getProperty("java.io.tmpdir");
    }
    
}