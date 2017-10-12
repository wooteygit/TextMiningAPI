/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.assignmentscoring.clustering;

import com.assignmentscoring.config.*;
import static com.assignmentscoring.model.app.sid;
import java.lang.reflect.Array;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author wootey02
 */
public class calTFIDF extends com.assignmentscoring.model.app{
 
    public void addData(int is_model) throws SQLException, UserException, Exception{
        ResultSet rs = null;
        if(is_model == 0){
            rs = (ResultSet) new ConnectionDB().connectoDB("SELECT * FROM words");
        }else{
            rs = (ResultSet) new ConnectionDB().connectoDB("SELECT * FROM words3");
        }
        int count = 0;
        sid.clear();
        while (rs.next()) { 
            String id = rs.getString("ID");
            sid.add(id);
            words = new ArrayList<String>();
            words.add(id);
            String w = "w";
            for(int i=0;i<=numW;i++){
                if(rs.getString(w+i) != null){ 
                    words.add(rs.getString(w+i));
                }else{
                    break;
                }
            }
            Collections.sort(words);
            TFIDF[count] = new ListKeyWord(words);
            count++;
        }
    }

    public void insertToTFIDFNORMALIED() throws SQLException, ClassNotFoundException, Exception{
        //ArrayList<String> keyword = new ArrayList<String>();
        //keyword = KEYWORD;
        ArrayList<ArrayList<Double>> wij = new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<Long>> tf = new ArrayList<ArrayList<Long>>();
//        int maxrow = getMaxCol(TFIDF);
        int maxrow = getMaxTf(TFIDF)-1;
        for(int i=0;i<TFIDF.length;i++){
            ArrayList<Long> count = new ArrayList<Long>();
            String sql = "INSERT INTO tfidfNormalized(ID,";
            String sqlvalue = " VALUES('"+TFIDF[i].keyWord().get(0)+"',";
            ArrayList<String> tep = new ArrayList<String>();
            tep = TFIDF[i].keyWord();
            Map<String, Long> countedDup =  tep.stream().collect(Collectors.groupingBy(c -> c, Collectors.counting()));
            Map<String, Long> sortedMap = new TreeMap<String, Long>(countedDup);
            int cu = 0;
            String w = "",sqlw = "";
            for(Map.Entry j : sortedMap.entrySet()){ 
                if(cu > 0){
                    Long seq = (Long)j.getValue();
                    sql += "k"+(cu-1)+",";
                    sqlvalue += "'"+(double)seq+"',";
                    count.add(seq);
//                    w += "w"+(cu-1)+",";
//                    sqlw += "'"+j.getKey()+"',";
                }else{
//                    sqlw += "'"+j.getKey()+"',";
//                    w += "ID,";
                }
                cu++;
            }
//            w = w.substring(0, w.length()-1); 
//            sqlw = sqlw.substring(0, sqlw.length()-1);
//            new ConnectionDB().saveWordsToDB("INSERT INTO words4("+w+") VALUES("+sqlw+");"); 
            if(cu <= maxrow){
                for(int j=(cu-1);j<maxrow;j++){ 
                    Long seq = 0L;
                    sql += "k"+j+",";
                    sqlvalue += "'"+(double)seq+"',";
                    count.add(seq);
                }
            }
//             for(int j=1;j<maxrow;j++){ 
//                 int seq = (j >= TFIDF[i].keyWord().size())? 0 : countDF(keyword,TFIDF[i].keyWord().get(j));
//                 sql += "k"+(j-1)+",";
//                 sqlvalue += "'"+(double)seq+"',";
//                 count.add(seq);
//             }
            tf.add(count);
            String SQL = sql.substring(0, sql.length()-1); 
            SQL += ")";
            String SQLVALUE = sqlvalue.substring(0, sqlvalue.length()-1); 
            SQLVALUE += ");";
            String strSQL = SQL+SQLVALUE;
            new ConnectionDB().saveWordsToDB(strSQL); 
        }
        String sqldf = "INSERT INTO tfidfNormalized(ID,";
        String sqldfValue = " VALUES('df',";
        ArrayList<Double> df = new ArrayList<Double>();
        ArrayList<Double> ndf = new ArrayList<Double>();
        ArrayList<Double> idf = new ArrayList<Double>();
        for(int i=0;i<maxrow;i++){
            String sqlDF = "SELECT SUM(x) AS k FROM "+
                " ( SELECT CASE WHEN k"+i+" > 0  THEN 1 ELSE 0 END AS x FROM tfidfNormalized )DF";
            ResultSet rsDF = (ResultSet) new ConnectionDB().connectoDB(sqlDF);
            while (rsDF.next()) { 
                double n = rsDF.getDouble("k");
                if(n == 0.0){
                   n = 1.0;
                }
                df.add(n);
                sqldf += "k"+i+",";
                sqldfValue += "'"+n+"',";
            }
        }
        String SQLDF = sqldf.substring(0, sqldf.length()-1); 
        SQLDF += ")";
        String SQLDFVALUE = sqldfValue.substring(0, sqldfValue.length()-1); 
        SQLDFVALUE += ");";
        String strSQLDF = SQLDF+SQLDFVALUE;
        new ConnectionDB().saveWordsToDB(strSQLDF);

        String sqlndf = "INSERT INTO tfidfNormalized(ID,";
        String sqlndfvalue = " VALUES('ndf',";
        for(int i=0;i<maxrow;i++){
            sqlndf += "k"+i+",";
            sqlndfvalue += "'"+(tf.size())/df.get(i)+"',";
            ndf.add(tf.size()/df.get(i));
        }

        String SQLNDF = sqlndf.substring(0, sqlndf.length()-1); 
        SQLNDF += ")";
        String SQLNDFVALUE = sqlndfvalue.substring(0, sqlndfvalue.length()-1); 
        SQLNDFVALUE += ");";
        String strSQLNDF = SQLNDF+SQLNDFVALUE;
        new ConnectionDB().saveWordsToDB(strSQLNDF);

        String sqlidf = "INSERT INTO tfidfNormalized(ID,";
        String sqlodfvalue = " VALUES('idf',";
        for(int i=0;i<maxrow;i++){
            sqlidf += "k"+i+",";
            sqlodfvalue += "'"+Math.log10(ndf.get(i))+"',";
            idf.add(Math.log10(ndf.get(i)));
        }

        String SQLIDF = sqlidf.substring(0, sqlidf.length()-1); 
        SQLIDF += ")";
        String SQLIDFVALUE = sqlodfvalue.substring(0, sqlodfvalue.length()-1); 
        SQLIDFVALUE += ");";
        String strSQLIDF = SQLIDF + SQLIDFVALUE;
        new ConnectionDB().saveWordsToDB(strSQLIDF);

        ArrayList<Double> ij;
        for(int i =0;i<tf.size();i++){
            String sqlw = "INSERT INTO tfidfNormalized(ID,";
            String sqlWvalue = " VALUES('w"+i+"',";
            ij = new ArrayList<Double>();
            for(int j=0;j<maxrow;j++){
               sqlw += "k"+j+",";
               double seq = (j < tf.get(i).size())? (tf.get(i).get(j) * idf.get(j)) * 100 : 0;
               sqlWvalue += "'"+(double)seq +"',";
               ij.add(seq);
            }
            wij.add(ij);
            String SQLW = sqlw.substring(0, sqlw.length()-1); 
            SQLW += ")";
            String SQLWVALUE = sqlWvalue.substring(0, sqlWvalue.length()-1); 
            SQLWVALUE += ");";
            String strSQLW = SQLW+SQLWVALUE;
            new ConnectionDB().saveWordsToDB(strSQLW);
        }
        Wij = wij;
        SID = sid;
        TF = tf;
        int cu1 = 0,cu2 = 0;
        ResultSet rs1 = (ResultSet) new ConnectionDB().connectoDB("SELECT count(*) AS cu FROM center_param WHERE ID = 1");
        while (rs1.next()) { 
            cu1 = rs1.getInt("cu"); 
        }
        ResultSet rs2 = (ResultSet) new ConnectionDB().connectoDB("SELECT count(*) AS cu FROM center_param WHERE ID = 2");
        while (rs2.next()) { 
            cu2 = rs2.getInt("cu"); 
        }
        if(cu1 > 0){
            new ConnectionDB().saveWordsToDB("UPDATE center_param SET param_val = "+maxrow+" WHERE ID = 1");
        }else{
            new ConnectionDB().saveWordsToDB("INSERT INTO center_param(ID,param_val) VALUES(1,"+maxrow+")");
        }
        if(cu2 > 0){
            new ConnectionDB().saveWordsToDB("UPDATE center_param SET param_val = "+tf.size()+" WHERE ID = 2");
        }else{
            new ConnectionDB().saveWordsToDB("INSERT INTO center_param(ID,param_val) VALUES(2,"+tf.size()+")");
        }
    }

    private int countDF(ArrayList<String> word,String txt){
        ArrayList<String> w = new ArrayList<String>();
        w = word;
        String t = txt;
        int count = 0;
        for(int i=1;i<w.size();i++){
            if(w.get(i).equals(t.trim())){
                count++;
            }
        }
        return count;
    }

    private int countKeyword(){
        int sum=0;
        for(int i=0;i<=TFIDF.length-1;i++){
            sum += TFIDF[i].keyWord().size();
        }
        return sum;
    }
    
    public int getMaxTf(ListKeyWord[] tfidf){
        int maxrow = 0;
        ArrayList<Integer> row = new ArrayList<Integer>();
        for(int i =0;i<tfidf.length;i++){
            Map<String, Long> countedDup =  tfidf[i].keyWord().stream().collect(Collectors.groupingBy(c -> c, Collectors.counting()));
            int cu = countedDup.size();
            row.add(cu);
//            row.add(TFIDF[i].keyWord().size());
        }
        maxrow = Collections.max(row);
        return maxrow;
    }
}
