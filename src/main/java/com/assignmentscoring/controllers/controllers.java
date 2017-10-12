/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignmentscoring.controllers;

import com.assignmentscoring.model.app;
import com.assignmentscoring.config.*;
import com.assignmentscoring.Tokenizer.*;
import com.assignmentscoring.synonym.Synonym;
import com.assignmentscoring.clustering.*;
import com.assignmentscoring.classify.*;
import com.assignmentscoring.svm_classify.*;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import org.apache.commons.io.IOUtils;
import com.google.gson.*;
import com.sun.jersey.core.impl.provider.entity.XMLRootElementProvider.*;
import com.assignmentscoring.StopWord.stopWord;
import com.assignmentscoring.directive.*;
import static com.assignmentscoring.model.app.*;
import java.sql.*;
import java.util.*;
import java.io.*;
import java.math.RoundingMode;
import java.text.*;
import java.util.logging.*;
import javax.ws.rs.*;

/**
 *
 * @author wootey02
 */
public class controllers extends app{
    ConnectionDB con = new ConnectionDB();
    Writer writer = null;

    /*
    *-----------------------------------------Clustering-----------------------
    */
    public ReturnCombo wordsDetail(int group){
        ReturnCombo rt = null;
        MapField  field;
        ArrayList<MapField> arrfield = new ArrayList<MapField>();
        ResultSet rs = null;
        setStatus();
        try {
            rt = new ReturnCombo();
            ArrayList<String> itemID = new ArrayList<String>();
            itemID = idForGroup(""+group);
            rs = (ResultSet) new ConnectionDB().connectoDB("SELECT PARAM_NAME FROM center_param WHERE ID = 3");
            String param_name = "",line = "";
            BufferedReader br;
            while (rs.next()) { 
                param_name = rs.getString("PARAM_NAME"); 
            }
            br = new BufferedReader(new InputStreamReader(new FileInputStream(DEFAULT_PATH+param_name), "UTF8"));
            String cvsSplitBy = ",";
            int cu = 0;
            while((line = br.readLine())!= null) {
                line = line.trim();
                if(line.length() > 0) {
                    String[] csvline = line.split(cvsSplitBy);
                    if(csvline.length == 3){  
                        if(itemID.indexOf((csvline[0].toString() + csvline[1].toString()).trim()) != -1){
                            cu++;
                            field = new MapField();
                            field.setName(csvline[0].toString() + csvline[1].toString());
                            field.setValue(csvline[2].toString());
                            arrfield.add(field);
                        }
                    }else if(csvline.length == 2){
                        if(itemID.indexOf((csvline[0].toString()).trim()) != -1){
                            cu++;
                            field = new MapField();
                            field.setName(csvline[0].toString());
                            field.setValue(csvline[1].toString());
                            arrfield.add(field);
                        }
                    }                  
                }
            }
            if(cu == 0){
                errCode = 0;
                errDesc = controllers.class.getName()+" CSV file incompatible "; 
            }
            if(conn != null) conn.close();
        } catch (IOException ex) {
            errCode = -1;
            errDesc = ConnectionDB.class.getName()+" IOException : "+ex; 
        } catch (Exception e) {
            errCode = -2;
            errDesc = ConnectionDB.class.getName()+" Exception "+e;
        }
        rt.setErrCode(errCode);
        rt.setErrDesc(errDesc);
        rt.setMap(arrfield);
        return rt;
    }
    
    public ReturnTempTable answer(int group ,double ans){
        ReturnTempTable rt = new ReturnTempTable();
        ArrayList<String> itemID = new ArrayList<String>();
        setStatus();
        try {
            itemID = idForGroup(""+group);
            if(itemID.size() > 0){
                String countV = "DELETE FROM label";
                new ConnectionDB().deleteAll(countV,"label");
                for(int i = 0; i < itemID.size(); i++){
                    new ConnectionDB().saveWordsToDB("INSERT INTO label(id,class_id)VALUES('"+itemID.get(i)+"',"+(ans)+" )");
                    new ConnectionDB().saveWordsToDB("UPDATE tfidfnormalized SET score = "+ans+" WHERE ID = '"+itemID.get(i)+"'");
                    new ConnectionDB().saveWordsToDB("UPDATE tfidfnormalized SET score = "+ans+" WHERE ref_id = '"+itemID.get(i)+"'");
                }
                errCode = 1;
                errDesc = "Successfull !!! ";
            }else{
                errCode = 0;
                errDesc = "No have candidate for this group";
            }
        } catch (UserException ex) {
            errCode = -4;
            errDesc = "UserException : "+ex;
        } catch (Exception ex) {
            errCode = -1;
            errDesc = "Exception : "+ex;
        }
        rt.setErrCode(errCode);
        rt.setErrDesc(errDesc);
        return rt;
    }
    
    public ReturnTempTable getClusterFile(InputStream uploadedInputStream,String fileName ){
        ReturnTempTable rt = new ReturnTempTable();
        String dir = DEFAULT_PATH+fileName;
        int cu = 0;
        setStatus();
        try {
            String str  = "CALL setCenterParam(3,NULL,'"+fileName+"')";
            new ConnectionDB().saveWordsToDB(str);
            OutputStream out = new FileOutputStream(new File(dir));
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }  
            out.flush();
            out.close();
        } catch (IOException ex) {
            errCode = -1;
            errDesc = controllers.class.getName()+" > getClusterFile > IOException : "+ex; 
        }catch (Exception ex) {
            errCode = -3;
            errDesc = controllers.class.getName()+" > getClusterFile > Exception : "+ex; 
        }
        rt.setErrCode(errCode);
        rt.setErrDesc(errDesc);
        return rt;
    }
    
    public ReturnTempTable loadScoreFromFile(String fileName ){
        ReturnTempTable rt = new ReturnTempTable();
        BufferedReader br;
        String dir = DEFAULT_PATH+fileName;
        setStatus();
        try {
            
            br = new BufferedReader(new InputStreamReader(new FileInputStream(dir), "UTF8"));
            new ConnectionDB().deleteAll("DELETE FROM m_student_label", "m_student_label");
            
            String cvsSplitBy = ",";
            String line = "";
            String str = "INSERT INTO m_student_label(SD_ID,SD_SCORE) VALUE ";
            int cu = 0;
            while((line = br.readLine())!= null) {
                if(cu > 0){
                    line = line.trim();
                    if(line.length()>0) {
                        String[] csvline = line.split(cvsSplitBy);
                        if(csvline.length == 3 || csvline.length == 2){ 
                            if(csvline.length == 3 ){
                                line = "('"+csvline[0].toString() + csvline[1].toString()+"' ,"+csvline[2].toString()+"),";
                            }else if(csvline.length == 2){
                                line = "('"+csvline[0].toString()+"' ,"+csvline[1].toString()+"),";
                            }
                        }
                    }
                    str += line;
                }
                cu++;
            }
            str = str.substring(0, str.length()-1); 
            new ConnectionDB().saveWordsToDB(str);
            new ConnectionDB().saveWordsToDB("CALL UP_SCOREFROM_FILE()");
        }catch(SQLException ex){
            errCode = -4;
            errDesc = controllers.class.getName()+" "+ex; 
        } catch (IOException ex) {
            errCode = -1;
            errDesc = controllers.class.getName()+" > getClusterFile > IOException : "+ex; 
        }catch (Exception ex) {
            errCode = -3;
            errDesc = controllers.class.getName()+" > getClusterFile > Exception : "+ex; 
        }
        rt.setErrCode(errCode);
        rt.setErrDesc(errDesc);
        return rt;
    }
    
    public ReturnTempTable clusterAns(String fileAns,int k,int is_model){
        setStatus();
        return setWordToDB(fileAns,k,is_model);
    }
    
    public ReturnTempTable setWordToDB(String fileName,int group,int is_model){
        ReturnTempTable rt = new ReturnTempTable();
        String dir = DEFAULT_PATH+fileName;
        GROUP = group;
        setStatus();
        try {           
            LongLexTo lonlex = new LongLexTo();
            stopWord st = new stopWord();
            st.checkWordsAuto();
            Synonym syn = new Synonym();
            syn.checkWordsSynonym();
            if(is_model == 0){
                new ConnectionDB().deleteAll("DELETE FROM words", "words");
                new ConnectionDB().deleteAll("DELETE FROM words1", "words1");
                
            }else if(is_model == 1){
                new ConnectionDB().deleteAll("DELETE FROM words3", "words3");
                new ConnectionDB().deleteAll("DELETE FROM words4", "words4");
            }
            new ConnectionDB().deleteAll("DELETE FROM tfidfNormalized", "tfidfNormalized");
            new ConnectionDB().saveWordsToDB("CALL setCenterParam(3,NULL,'"+fileName+"')");
            new ConnectionDB().saveWordsToDB("CALL setCenterParam(9,"+GROUP+",null)");
            new ConnectionDB().deleteAll("DELETE FROM model_temp","model_temp");
            new ConnectionDB().deleteAll("DELETE FROM t_answer","t_answer");
            lonlex.mainLongLexTo(dir,is_model);
            String str_ans = "INSERT INTO t_answer(sd_id,sd_ans,num_score,create_date) VALUES ";
            Set<String> keys = BLANCE.keySet();
            for(String key: keys){
                str_ans += "('"+key+"','"+BLANCE.get(key)+"',null,SYSDATE()),";                
            }
            str_ans = str_ans.substring(0, str_ans.length()-1);
            new ConnectionDB().saveWordsToDB(str_ans);
            if(is_model == 0){
                countWords = new ConnectionDB().countWords();
            }else if(is_model == 1){
                countWords = new ConnectionDB().countWords3();
            }
            TFIDF = new ListKeyWord[countWords];
            calTFIDF cal = new calTFIDF();
            cal.addData(is_model);
            cal.insertToTFIDFNORMALIED();
            clustering clus = new clustering();
            clus.kmeans();
            if(is_model == 1){
                new ConnectionDB().saveWordsToDB("CALL UP_SCOREFROM_FILE()");
            }
            
            if(conn != null) conn.close();
        }catch (UserException ex) {
            errCode = -5;
            errDesc = ConnectionDB.class.getName()+" UserException : "+ex;
        } catch (SQLException ex) {
            errCode = -1;
            errDesc = calTFIDF.class.getName()+" to SQLException : "+ex;
        } catch (IOException ex) {
            errCode = -2;
            errDesc = stopWord.class.getName()+" or "+Synonym.class.getName()+" IOException : "+ex;
        } catch (ClassNotFoundException ex) {
            errCode = -3;
            errDesc = LongLexTo.class.getName()+" ClassNotFoundException : "+ex;
        } catch (Exception ex) {
            errCode = -4;
            errDesc = controllers.class.getName()+" Exception : "+ex+ " ; "+group;
        }
        rt.setErrCode(errCode);
        rt.setErrDesc(errDesc);
        return rt;
    }
    
    public ReturnTempTable modelGroup(int k){
        ReturnTempTable rt = new ReturnTempTable();
        ArrayList<MapTempTable> arr_map = new ArrayList<MapTempTable>();
        MapTempTable map = new MapTempTable();
        setStatus();
        String sql = "SELECT tfidf.ID AS SF_ID,tfidf.`group` AS SF_GROUP,ans.sd_ans " +
        " FROM tfidfnormalized tfidf " +
        " LEFT JOIN t_answer ans ON tfidf.ID = ans.sd_id " +
        " WHERE ID NOT IN( " +
        " SELECT ID FROM tfidfnormalized WHERE ID LIKE '%w%' " +
        " UNION " +
        " SELECT ID FROM tfidfnormalized WHERE ID LIKE '%n%' " +
        " UNION " +
        " SELECT ID FROM tfidfnormalized WHERE ID LIKE '%f%' " +
        ")";
        if(k >= 0){
            sql += " AND `group` = "+k;
        }
        try {
            ResultSet rs = (ResultSet) new ConnectionDB().connectoDB(sql);
            while (rs.next()) {
                String id = (String)rs.getString("SF_ID");
                String group = rs.getString("SF_GROUP");
                map = new MapTempTable();
                map.setID(id);
                map.setGROUP(group);
                map.setDESC(rs.getString("sd_ans"));
                arr_map.add(map);                   
            } 
            if(conn != null) conn.close();
        }catch (Exception ex) {
            errCode = -4;
            errDesc = ConnectionDB.class.getName()+" UserException : "+ex;
        }
        rt.setErrCode(errCode);
        rt.setErrDesc(errDesc);
        rt.setMap(arr_map);
        return rt;
    }
    /*
    *------------------------------------Clustering---------------------------
    */
    
    /*
    *-------------------------------------Classify----------------------------
    */

    public ReturnTempTable writerClassiffy(String pertext,int k,int fold_clussify){
        ReturnTempTable rt = new ReturnTempTable();
        setStatus();
        try{
            new ConnectionDB().deleteAll("DELETE FROM DATA_FILE", "DATA_FILE");       
            new ConnectionDB().saveWordsToDB("CALL setCenterParam(4,"+k+",NULL)");
            new ConnectionDB().saveWordsToDB("CALL setCenterParam(5,NULL,'"+pertext+"')");
            new ConnectionDB().saveWordsToDB("CALL setCenterParam(6,"+fold_clussify+",NULL)");
            WriterClassify wri = new WriterClassify();
            wri.Writertrain(pertext,k,fold_clussify);
            wri.Writertest(pertext,k,fold_clussify);
        }catch (UserException ex) {
            errCode = -5;
            errDesc = ConnectionDB.class.getName()+" UserException : "+ex;
        } catch (SQLException ex) {
            errCode = -1;
            errDesc = calTFIDF.class.getName()+" to SQLException : "+ex;
        } catch (IOException ex) {
            errCode = -2;
            errDesc = stopWord.class.getName()+" or "+Synonym.class.getName()+" IOException : "+ex;
        } catch (ClassNotFoundException ex) {
            errCode = -3;
            errDesc = LongLexTo.class.getName()+" ClassNotFoundException : "+ex;
        } catch (Exception ex) {
            errCode = -4;
            errDesc = controllers.class.getName()+" Exception : "+ex+ " ; "+k;
        }
        rt.setErrCode(errCode);
        rt.setErrDesc(errDesc);
        return rt;
    }
    
    public ReturnTempTable writerPertextOnly(String pertext){
        ReturnTempTable rt = new ReturnTempTable();
        setStatus();
        try{
            new ConnectionDB().deleteAll("DELETE FROM DATA_FILE", "DATA_FILE");  
            WriteClassifyPertextOnly wri = new WriteClassifyPertextOnly();
            wri.Writertrain(pertext);
            wri.Writertest();
        }catch (UserException ex) {
            errCode = -5;
            errDesc = ConnectionDB.class.getName()+" UserException : "+ex;
        } catch (SQLException ex) {
            errCode = -1;
            errDesc = calTFIDF.class.getName()+" To SQLException : "+ex;
        } catch (IOException ex) {
            errCode = -2;
            errDesc = stopWord.class.getName()+" or "+Synonym.class.getName()+" IOException : "+ex;
        } catch (ClassNotFoundException ex) {
            errCode = -3;
            errDesc = LongLexTo.class.getName()+" ClassNotFoundException : "+ex;
        } catch (Exception ex) {
            errCode = -4;
            errDesc = controllers.class.getName()+" Exception : "+ex;
        }
        rt.setErrCode(errCode);
        rt.setErrDesc(errDesc);
        return rt;
    }
    
    public ReturnTestData classifyAns(int sj_id,int p_sj_id,int sj_seq,int num_k,double perc){
        setStatus();
        ReturnTestData rt = new ReturnTestData();
        ctr_classify ctr = new ctr_classify();
        rt = ctr.TestBestModel(sj_id,p_sj_id,sj_seq,num_k,perc);
        setScoreReport();
        return rt;
    } 
    
    public ReturnTempTable perScore(String sql){
        ReturnTempTable rt = new ReturnTempTable();
        setStatus();
        try{
            errDesc = new ConnectionDB().getJSONFromResultSet(sql, "dataset");  
        }catch (SQLException ex) {
            errCode = -5;
            errDesc = ConnectionDB.class.getName()+" SQLException : "+ex;
        } catch (Exception ex) {
            errCode = -4;
            errDesc = controllers.class.getName()+" Exception : "+ex;
        }
        rt.setErrCode(errCode);
        rt.setErrDesc(errDesc);
        return rt;
    }
    
    /*
    *--------------------------------------Classify------------------------------
    */

    /*
    *--------------------------------------Retraining------------------------------
    */
    public ReturnTestData Retraining(int sj_id,int p_sj_id,int sj_seq,int num_k){
        setStatus();
        ReturnTestData rt = new ReturnTestData();
        try{
            String sql = "SELECT * "+
            " FROM best_model "+
            " WHERE sj_id ="+sj_id+" AND p_sj_id ="+p_sj_id+" AND sj_seq ="+sj_seq+" AND num_k = "+num_k;
            String strargv = "",str_train = "";
            ResultSet rs = (ResultSet) new ConnectionDB().connectoDB(sql);
            while (rs.next()) {
                strargv = rs.getString("bm_param");
                str_train = rs.getString("train_des");
                //str_test = rs.getString("test_des");
            }
            if(!strargv.equals("")){
                new ConnectionDB().saveWordsToDB("CALL UP_APPROVE_RESCORE_RETRAIN()");
                WriterRetraining wri = new WriterRetraining();
                str_train += wri.setDataTrain();
                //str_test += wri.setDataTest();
                // retrain model
                svm_train t = new svm_train();
                AccessoriesFunction acc = new AccessoriesFunction();
                String fileName = setFileName(),retrain = "Retrain_"+fileName+".csv",model = "ModelRetrain_"+fileName+".csv";
                
                acc.genFileAll(str_train, retrain, "");
                t.run(strargv.split(","),retrain,model);
                BufferedReader reader = null;
                reader = new BufferedReader(new FileReader(DEFAULT_PATH+model));
                String line = null;
                String bm_des = "";
                while((line = reader.readLine()) != null){ 
                    bm_des += line+"\n";
                }
                String sqlup = "UPDATE best_model "+
                " SET  train_des = '"+str_train+"' , bm_des = '"+bm_des+"'"+
                " WHERE sj_id ="+sj_id+" AND p_sj_id ="+p_sj_id+" AND sj_seq ="+sj_seq+" AND num_k = "+num_k;
                new ConnectionDB().saveWordsToDB(sqlup);
                setScoreReport();
                if(conn != null) conn.close();
            }else{
                errCode = -4;
                errDesc = "get parameter : can't find parameter";
            }

        } catch (SQLException ex) {
            errCode = -1;
            errDesc = "SQLException : "+ex;
        } catch (IOException ex) {
            errCode = -2;
            errDesc = "IOException : "+ex;
        }catch (Exception ex) {
            errCode = -3;
            errDesc = "Exception : "+ex;
        }
        rt.setErrCode(errCode);
        rt.setErrDesc(errDesc);
        return rt;
    }
    
    public ReturnPreSVM svmMultiParam(ArrayList<String> strargv){
        ReturnPreSVM pre = new ReturnPreSVM();
        ArrayList<MapPreSVM> arRS = new ArrayList<MapPreSVM>();
        setStatus();
        try{
            MapPreSVM maprs ;
            svm_train t;
            MapclassifyAns mca ;
            AlgorithmClassify fma ;
            int[] itemwrong;
            String best_model = "Model_"+setFileName()+".txt";
            String sql_in = "INSERT INTO model_temp(bm_id,k,file_name,bm_des,bm_param,correct,wrong,create_date,edit_date) VALUES";
            ResultSet rsk = (ResultSet) new ConnectionDB().connectoDB("SELECT param_val FROM center_param WHERE ID = 9");
            String k = "";
            while (rsk.next()) {
                k = rsk.getString("param_val");
            }
            for(int i=0;i<strargv.size();i++){
                maprs = new MapPreSVM();
                t = new svm_train();
                mca = new MapclassifyAns();
                fma = new AlgorithmClassify();
                itemwrong = new int[2];
                String argv[] = strargv.get(i).split(",");
                String best_model2 = (i+"_"+best_model).toString();
                fma.setItemWrongAll();
                mca = fma.SVMclassifyTest(t.run(argv,"",best_model2));
                if(conn != null)  conn.close();
                itemwrong = mca.getItemwrong();
                maprs.setCorrcet(itemwrong[0]);
                maprs.setWrong(itemwrong[1]);
                BufferedReader reader = null;
                reader = new BufferedReader(new FileReader(DEFAULT_PATH+best_model2));
                String line = null;
                String bm_des = "";
                while((line = reader.readLine()) != null){ 
                   bm_des += line+"\n";
                }
                if(i == 0){
                    new ConnectionDB().deleteAll("DELETE FROM model_temp", "model_temp");
                }
                
                maprs.setK(k);
                maprs.setParam(strargv.get(i));
                maprs.setBm_id(i);
                double perC = ((double)itemwrong[0] * 100.00)/ (((double)itemwrong[0]+(double)itemwrong[1]) == 0 ? 1.0 : ((double)itemwrong[0]+(double)itemwrong[1]));
                double perW = ((double)itemwrong[1] * 100.00)/ (((double)itemwrong[0]+(double)itemwrong[1]) == 0 ? 1.0 : ((double)itemwrong[0]+(double)itemwrong[1]));
                sql_in += "("+i+","+k+",'"+best_model2+"','"+bm_des+"','"+strargv.get(i)+"',"+perC+","+perW+",SYSDATE(),SYSDATE()),";
                arRS.add(maprs);
            }
            sql_in = sql_in.substring(0, sql_in.length()-1);
            new ConnectionDB().saveWordsToDB(sql_in);
            pre.setRs(arRS);
            setScoreReport();
        } 
        catch (SQLException ex) {
           errCode = -1;
           errDesc = "SQLException : "+ex;
        } 
        catch (IOException ex) {
            errCode = -2;
            errDesc = "IOException : "+ex;
        }
         catch (Exception ex) {
            errCode = -3;
            errDesc = "Exception : "+ex;
        }
        pre.setErrCode(errCode);
        pre.setErrDesc(errDesc);
        return pre;
    }
    
    public ReturnTestData PerdictSVM(){
        ReturnTestData rt = new ReturnTestData();
        svm_predict p = new svm_predict();
        Level a = Level.SEVERE;
        setStatus();
        try {
            
            errDesc = p.run("Test_0003_1_1_1_0.csv","1_Model_0003_1_1.txt", 1);
            
        } catch (IOException ex) {
            errCode = -1;
            errDesc = "IOException : "+ex;
            Logger.getLogger(controllers.class.getName()).log(Level.SEVERE, null, ex);
        }
        rt.setErrCode(errCode);
        rt.setErrDesc(errDesc);
        return rt;
    }
    /*
    *--------------------------------------Retraining------------------------------
    */
    
    /*
    *--------------------------------------Report------------------------------
    */
    public ReturnTempTable setScoreReport(){
        ReturnTempTable rt = new ReturnTempTable();
        try{       
        String strq = " select sd_id,ans,num_score " +
        " from t_student_score order by sd_id asc ";
        ResultSet rsDF = (ResultSet) new ConnectionDB().connectoDB(strq);
        String str = "รหัสนักศึกษา,คำตอบ,คะแนน\n";
        
        while (rsDF.next()) { 
            str += (rsDF.getString("sd_id")+","+rsDF.getString("ans")+","+rsDF.getString("num_score")+"\n");
        }
        writer = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream(DEFAULT_PATH+"score.csv"), "UTF-8"));
        writer.write(str); 
        }catch (IOException ex) {
            errCode = -6;
            errDesc = ConnectionDB.class.getName()+" UserException : "+ex;
        }catch (UserException ex) {
            errCode = -5;
            errDesc = ConnectionDB.class.getName()+" UserException : "+ex;
        } catch (SQLException ex) {
            errCode = -1;
            errDesc = calTFIDF.class.getName()+" to SQLException : "+ex;
        } catch (Exception ex) {
            errCode = -4;
            errDesc = controllers.class.getName()+" Exception : "+ex;
        }finally {
           try {
               writer.close();
           } catch (Exception ex) {
               errCode = -4;
               errDesc = controllers.class.getName()+" Exception : "+ex;
           }
        }
        rt.setErrCode(errCode);
        rt.setErrDesc(errDesc);
        return rt;
    }
    /*
    *--------------------------------------Report------------------------------
    */
    
    /*
    *---------------------------------------Config--------------------------------
    */
    public ReturnTempTable getFileDownload(int id, String typeFile){
        ReturnTempTable rt = new ReturnTempTable();
        ResultSet rs = null;
        String pFilename = "";
        StringBuffer sb = null;
        String dir = "";
        setStatus();
        try{
            String sql = "SELECT ID,FILE_NAME,FILE_DES FROM DATA_FILE WHERE ID = "+id;
            rs = (ResultSet) new ConnectionDB().connectoDB(sql);
            while (rs.next()) { 
                sb = new StringBuffer(rs.getString("FILE_DES"));
                pFilename = rs.getString("FILE_NAME"); 
            }
            if(conn != null) conn.close();
            WriterClassify wri = new WriterClassify();
            dir = wri.writeToFile(pFilename, sb, typeFile);
            errCode = 1;
            errDesc = dir;
        }catch (UserException ex) {
            errCode = -5;
            errDesc = ConnectionDB.class.getName()+" UserException : "+ex;
        } catch (SQLException ex) {
            errCode = -1;
            errDesc = calTFIDF.class.getName()+" to SQLException : "+ex;
        } catch (IOException ex) {
            errCode = -2;
            errDesc = stopWord.class.getName()+" or "+Synonym.class.getName()+" IOException : "+ex;
        } catch (ClassNotFoundException ex) {
            errCode = -3;
            errDesc = LongLexTo.class.getName()+" ClassNotFoundException : "+ex;
        } catch (Exception ex) {
            errCode = -4;
            errDesc = controllers.class.getName()+" Exception : "+ex;
        }
        rt.setErrCode(errCode);
        rt.setErrDesc(errDesc);
        return rt;
    }
    
    public ReturnTempTable setExcSql(String sql){
        ReturnTempTable rt = new ReturnTempTable();
        setStatus();
        try{
            new ConnectionDB().saveWordsToDB(sql);
            if(conn != null) conn.close();
        }catch (UserException ex) {
            errCode = -5;
            errDesc = ConnectionDB.class.getName()+" UserException : "+ex;
        } catch (SQLException ex) {
            errCode = -1;
            errDesc = calTFIDF.class.getName()+" to SQLException : "+ex;
        } catch (Exception ex) {
            errCode = -4;
            errDesc = controllers.class.getName()+" Exception : "+ex;
        }
        rt.setErrCode(errCode);
        rt.setErrDesc(errDesc);
        return rt;
    }
    
    public ReturnMapTable getDataForSql(String sql){
        setStatus();
        ReturnMapTable rt = new ReturnMapTable();
        ArrayList<MapTable> arrmap;
        ArrayList<ArrayList<MapTable>> arr_map = new ArrayList<ArrayList<MapTable>>();
        ArrayList<MapField> arrmf = new ArrayList<MapField>();
        MapField mf;
        MapTable map;
        if(sql.equals("") || sql == null)return rt;
        try {
            ResultSet rs = (ResultSet) new ConnectionDB().connectoDB("CALL genCol('"+sql+"')");
            while (rs.next()) {
                mf = new MapField();
                mf.setName(rs.getString("Field"));
                mf.setValue(rs.getString("Type"));
                arrmf.add(mf);
            }
            ResultSet rsTb = (ResultSet) new ConnectionDB().connectoDB(sql);
            while (rsTb.next()) {
                arrmap = new ArrayList<MapTable>();
                for(int i=0;i<arrmf.size();i++){
                    map = new MapTable();
                    map.setField(arrmf.get(i).getName());
                    map.setType(arrmf.get(i).getValue());
                    map.setValue(rsTb.getString(arrmf.get(i).getName()));
                    arrmap.add(map);
                }
                arr_map.add(arrmap);                
            }
            if(conn != null) conn.close();
            rt.setMap(arr_map);
        } catch (IOException ex) {
            errCode = -1;
            errDesc = ""+ex;
            Logger.getLogger(controllers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UserException ex) {
            errCode = -2;
            errDesc = ""+ex;
            Logger.getLogger(controllers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            errCode = -3;
            errDesc = controllers.class.getName()+" --> "+ex;
            Logger.getLogger(controllers.class.getName()).log(Level.SEVERE, null, ex);
        }
        rt.setErrCode(errCode);
        rt.setErrDesc(errDesc);
        return rt;
    }
    
    public String getConfig() {
        setStatus();
    	String result = "",test="";
        ReturnTempTable rt = new ReturnTempTable();
    	ClassLoader classLoader = getClass().getClassLoader();
    	try {
                result = IOUtils.toString(classLoader.getResourceAsStream("config.json"));
        }catch(IOException e){
            result = "IOException : "+e;
        }      
        return  result;
    }
    
    public ReturnTempTable getConnect(){
        ReturnTempTable rt = new ReturnTempTable();
        setStatus();
        try {
            String status = con.ckConnect();
            rt.setErrDesc(status);
        } catch (Exception ex) {
            rt.setErrDesc(""+ex);
        } 
        return rt;
    }
    
    public ReturnTempTable mySqlNonResponse(String str){
        ReturnTempTable rt = new ReturnTempTable();
        ArrayList<MapTempTable> map = new ArrayList<MapTempTable>();
        MapTempTable temp;
        int errCode = 0;
        setStatus();
        String status = "Con't in to loop";
        try {
            new ConnectionDB().saveWordsToDB("CALL "+str);
            errCode = 1;
            status = "Success";
        }catch (SQLException ex) {
            errCode = -1;
            status = "Exception "+ex;
        } catch (Exception e) {
            errCode = -1;
            status = "Exception "+e;
        }
        
        rt.setErrCode(errCode);
        rt.setErrDesc(status);
        rt.setMap(map);
        return rt;
    }
    
    public ReturnCombo getComboData(String str){
        setStatus();
        ReturnCombo rt = null;
        MapField  field;
        ArrayList<MapField> arrfield = new ArrayList<MapField>();
        ResultSet rs = null;
        PreparedStatement per  = null;
        try {
            con.connectsql();
            rt = new ReturnCombo();
            Class.forName("com.mysql.jdbc.Driver");
            conn =  DriverManager.getConnection(url,usrName,pass);
            if(conn != null){
                per = conn.prepareStatement(str);
                rs = per.executeQuery();
                while (rs.next()) { 
                    field = new MapField();
                    field.setName(rs.getString("NAME"));
                    field.setValue(rs.getString("VALUE"));
                    arrfield.add(field);
                }
            } else {
                errCode = 0;
                errDesc = controllers.class.getName()+" getComboData : ";
            }
         } catch (IOException ex) {
            errCode = -1;
            errDesc = ConnectionDB.class.getName()+" IOException : "+ex; 
        } catch (Exception e) {
            errCode = -2;
            errDesc = ConnectionDB.class.getName()+" Exception "+e;
        }
        rt.setErrCode(errCode);
        rt.setErrDesc(errDesc);
        rt.setMap(arrfield);
        return rt;
    }
    
    public ReturnDataFile DataFile(String str){
        setStatus();
        ReturnDataFile rt = null;
        MapDataFile  field;
        ArrayList<MapDataFile> arrfield = new ArrayList<MapDataFile>();
        ResultSet rs = null;
        PreparedStatement per  = null;
        if(str.equals("") || str.equals("-1")){
            str = "SELECT ID ,FILE_NAME ,FILE_DES,CASE WHEN FILE_TYPE = 1 OR FILE_TYPE = 3 THEN 'Train' WHEN FILE_TYPE = 2 "
                    + " OR FILE_TYPE = 4 THEN 'Test' END AS FILE_TYPE_NAME FROM DATA_FILE";
        }
        try {
            con.connectsql();
            rt = new ReturnDataFile();
            Class.forName("com.mysql.jdbc.Driver");
            conn =  DriverManager.getConnection(url,usrName,pass);
            if(conn != null){
                per = conn.prepareStatement(str);
                rs = per.executeQuery();
                while (rs.next()) { 
                    field = new MapDataFile();
                    field.setID(rs.getInt("ID"));
                    field.setFileName(rs.getString("FILE_NAME"));
                    field.setFileType(rs.getString("FILE_TYPE_NAME"));
                    field.setFileDes(DEFAULT_PATH);
                    arrfield.add(field);
                }
            } else {
                errCode = 0;
                errDesc = controllers.class.getName()+" Connection failed : ";
            }
         } catch (IOException ex) {
            errCode = -1;
            errDesc = ConnectionDB.class.getName()+" IOException : "+ex; 
        } catch (Exception e) {
            errCode = -2;
            errDesc = ConnectionDB.class.getName()+" Exception "+e;
        }
        rt.setErrCode(errCode);
        rt.setErrDesc(errDesc);
        rt.setMap(arrfield);
        return rt;
    }

    public void setStatus(){
        errCode = 1;
        errDesc = "Successfull !!! ";
    }

    /*
    *----------------------------------Config-------------------------------------
    */
}
