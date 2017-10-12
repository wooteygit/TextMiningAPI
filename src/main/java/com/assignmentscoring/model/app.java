/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignmentscoring.model;

import com.assignmentscoring.clustering.ListKeyWord;
import com.assignmentscoring.config.*;
import com.assignmentscoring.directive.*;
import com.assignmentscoring.synonym.ModelSynonym;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.Collectors;

/**
 *
 * @author wootey02
 */
public class app {
    public static String version = "1.0.0";
    public static Connection conn = null;
    public static String ipAddress = "";
    public static String dbName = "";
    public static String dbNameGen = "";
    public static final String driver = "com.mysql.jdbc.Driver";
    public static String usrName = "";
    public static String pass = "";
    public static String url = "";
    public static String urlGen = "";

    public static int GROUP = 0;
    public static int ROW = 0,COL = 0;
    public static String DEFAULT_PATH_TRAIN = "";
    public static String DEFAULT_PATH_TEST = "";
    public static String DEFAULT_PATH = "";
    
    public static int C_SVC = 0;
    public static int CLASS_SVM = 0;
    public static int NU_SVC = 0;
    public static int NU_SVR = 0;
    public static int EP_SVR = 0;
    
    public static int  LINEARS = 0;
    public static int  POLYNOMIAL = 0;
    public static int  RADIAL = 0;
    public static int  SIGMOID= 0;
    
    public static int DEGREE = 0;
    public static double GAMMA = 0.0;
    public static double COEF = 0.0;
    public static double COST = 0.0;
    public static double NU = 0.0;
    public static double EPSILON = 0.0;
    public static double P = 0.0;
    public static int CACH_SIZE = 0;
    
    public static int FOLD = 0;
    public static int START_CLASSIFY = 0;
    public static int END_CLASSIFY = 0;
    public static int ROWS = 0;
    public static int FOLD_CLASSIFY = 0;
    public static int NUMROW = 0;
    public static int REC = 0;
    public static int SUB_FOLD = 0;
    public static String Q_ID = "0";
    public static ArrayList<ArrayList<Double>> Wij = new ArrayList<ArrayList<Double>>();
    public static ArrayList<String> SID = new ArrayList<String>();
    public static ArrayList<ArrayList<Long>> TF = new ArrayList<ArrayList<Long>>();
    public static ArrayList<String> KEYWORD = new ArrayList<String>();
    public static ArrayList<Integer> SCORE = new ArrayList<Integer>();
    public static int GROUPCLASSIFY = 0;
    public static ArrayList<String>sid = new ArrayList<String>();    
    public static Hashtable ITEM_WRONG = new Hashtable();
    public static Hashtable BLANCE = new Hashtable();  
    public static String comboPerText = "";
    public static ArrayList<String> listStopWords = new ArrayList<String>();
    public static ArrayList<ModelSynonym> Msyn = new ArrayList<ModelSynonym>();
    
    public static int countWords ;
    public static ListKeyWord[] TFIDF ;
    public static ArrayList<String> words;
    public static int numK = 250;
    public static int numW = 250;
    
    public static MapWC BEAST_WC;
    public static MapBeAf BEAST_BeAf;
    public static ArrayList<String> BEAST_MODEL;
    

    public static int errCode = 1;
    public static String errDesc = "Successfull !!! ";
    
    public app(){
        ClassLoader classLoader = getClass().getClassLoader();
        String dir = classLoader.getResource("/").getFile();
        dir = dir.replaceAll("%20", " ");
        DEFAULT_PATH = dir;
    }
    
    public int getMaxCol(ListKeyWord[] TFIDF){
        int maxrow = 0;
        ArrayList<Integer> row = new ArrayList<Integer>();
        for(int i =0;i<TFIDF.length;i++){
//            Map<String, Long> countedDup =  TFIDF[i].keyWord().stream().collect(Collectors.groupingBy(c -> c, Collectors.counting()));
//            int cu = countedDup.size();
//            row.add(cu);
            row.add(TFIDF[i].keyWord().size());
        }
        maxrow = Collections.max(row);
        return maxrow;
    }
    
    public int sizeRows() throws IOException, UserException, Exception{
        int TF = 0;
        ResultSet rs = (ResultSet) new ConnectionDB().connectoDB("SELECT COUNT(*) AS CU FROM tfidfnormalized WHERE ID LIKE 'w%'");
        try {
            while (rs.next()) {
                TF = rs.getInt("CU");
            }
            if(conn != null) conn.close();
        } catch (SQLException ex) {
            throw new SQLException();
        }
        return TF;
    }
    
    public void SET_CENTER_PARAM() throws UserException, Exception{
        try { 
            ResultSet rs= (ResultSet) new ConnectionDB().connectoDB("SELECT * FROM center_param  WHERE ID = 2");
            int row = 0;
            while (rs.next()) {
                row = rs.getInt("param_val");
                ROW = row;
            }
            
            ResultSet rs2= (ResultSet) new ConnectionDB().connectoDB("SELECT * FROM center_param  WHERE ID = 1");
            int col = 0;
            while (rs2.next()) {
                col = rs2.getInt("param_val");
                COL = col;
            }
            if(conn != null) conn.close();
        } catch (SQLException ex) {
            throw new SQLException(ex);
        }
    }
    
    public void SetID(int t_b_s,int t_b_e,int t_a_s,int t_a_e) throws UserException, Exception{
        try {
            sid.clear();
            String sql = "CALL getFold("+GROUP+","+t_b_s+","+t_b_e+","+t_a_s+","+t_a_e+", 'ref_id')";
            ResultSet rs= (ResultSet) new ConnectionDB().connectoDB(sql);
            String id="";
            int n =0;
            while (rs.next()) {
                id=rs.getString("ref_id");
                sid.add(id);
            }
            if(conn != null) conn.close();
        } catch (SQLException ex) {
            throw new SQLException(ex);
        }
    } 
    
    public void SetAllID() throws UserException, Exception{
        try {
            sid.clear();
            String sql = "SELECT ref_id FROM tfidfnormalized  WHERE ID LIKE '%w%'";
            ResultSet rs = (ResultSet) new ConnectionDB().connectoDB(sql);
            String id = "";
            int n = 0;
            while (rs.next()) {
                id = rs.getString("ref_id");
                sid.add(id);
            }
            if(conn != null) conn.close();
        } catch (SQLException ex) {
            throw new SQLException(ex);
        }
    }
    
    public void SetSomeID(int group_k) throws UserException, Exception{
        try {
            sid.clear();
            //String sql = "SELECT ref_id FROM tfidfnormalized  WHERE ID LIKE '%w%' AND GROUP_K="+group_k;
            String sql = "SELECT ID FROM tfidf";
            ResultSet rs = (ResultSet) new ConnectionDB().connectoDB(sql);
            String id = "";
            int n = 0;
            while (rs.next()) {
                id = rs.getString("ID");
                sid.add(id);
            }
            if(conn != null) conn.close();
        } catch (SQLException ex) {
            throw new SQLException(ex);
        }
    }
    
    public ArrayList<String> idForGroup(String g) throws SQLException, UserException, Exception{
        ArrayList<String> itemID = new ArrayList<String>();
        try {
            ResultSet rs= (ResultSet) new ConnectionDB().connectoDB("SELECT `group`,ref_id FROM tfidfnormalized WHERE ID LIKE '%w%' AND `group` = "+g);
            while (rs.next()) {
                itemID.add(rs.getString("ref_id").trim());
            }
            if(conn != null) conn.close();
        } catch (SQLException ex) {
            throw new SQLException(ex);
        }catch (UserException ex) {
            throw new UserException(""+ex);
        }catch (Exception ex) {
            throw new Exception(""+ex);
        }
        return itemID;
    }
    
    public String getAns(String sql) throws UserException, Exception{
        String str = "";
        ResultSet rs= (ResultSet) new ConnectionDB().connectoDB(sql);
        while (rs.next()) {
            str = rs.getString("ans_des");
        }
        if(conn != null) conn.close();
        return str;
    } 
    
    public String setFileName() throws IOException, SQLException, Exception{
        String fileName = "";
        String sqlsj = "SELECT sj.sj_code,msj.p_sj_id,msj.seq FROM m_model_subject msj " +
        " LEFT JOIN m_subjects sj ON msj.sj_id = sj.sj_id " +
        " WHERE msj.edit_date = (SELECT MAX(edit_date) FROM m_model_subject) ";
        ResultSet rsf = (ResultSet) new ConnectionDB().connectoDB(sqlsj);
        while (rsf.next()) { 
            fileName = rsf.getString("sj_code")+"_"+rsf.getString("p_sj_id")+"_"+rsf.getString("seq");
        }
        if(conn != null)conn.close();
        return fileName; 
    }
    
    public int numOfCluster(int GROUP_K)throws SQLException,UserException,Exception{
        int col = 0;
        String sql = "SELECT COUNT(*) AS CU " +
        "FROM tfidfnormalized " +
        "WHERE ID LIKE '%w%' AND GROUP_K = "+GROUP_K;
        ResultSet rs = (ResultSet) new ConnectionDB().connectoDB(sql);
        while (rs.next()) {
            col = rs.getInt("CU");
        }
        if(conn != null) conn.close();
        return col;
    }
}
