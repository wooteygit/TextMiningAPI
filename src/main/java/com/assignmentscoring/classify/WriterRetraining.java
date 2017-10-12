/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignmentscoring.classify;

import com.assignmentscoring.config.ConnectionDB;
import com.assignmentscoring.config.UserException;
import static com.assignmentscoring.model.app.conn;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author wootey02
 */
public class WriterRetraining {
    private int numcol = 0;
    private String TestWriter = "",TrainWriter = "";
    
    public String setDataTrain() throws SQLException, UserException, Exception{
        ResultSet rs = (ResultSet) new ConnectionDB().connectoDB("SELECT * FROM center_param  WHERE ID = 1");
        int num = 0;
        while (rs.next()) { 
            num = rs.getInt("param_val");
        }
        numcol = num;
        String sql = sqlSelectdata();        
        String strq = "SELECT "+sql+" FROM tfidfnormalized tf"
                +" INNER JOIN veiw_report_score ans ON tf.ref_id = ans.sd_id " 
                +" WHERE tf.ID LIKE '%w%' and ans.new_num_score is not null ";
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
            if(cu > 0){
                str += ""+rsDF.getDouble("score")+" "+temp;
                str += "\n";
            }
            n++;
        }
        return str;
    }
    
    public String setDataTest() throws SQLException, UserException, Exception{
        ResultSet rs = (ResultSet) new ConnectionDB().connectoDB("SELECT * FROM center_param  WHERE ID = 1");
        int num = 0;
        while (rs.next()) { 
            num = rs.getInt("param_val");
        }
        numcol = num;
        String sql = sqlSelectdata();        
        String strq = "SELECT "+sql+" FROM tfidfnormalized tf"
                +" INNER JOIN veiw_report_score ans ON tf.ref_id = ans.sd_id " 
                +" WHERE tf.ID LIKE '%w%' and ans.new_num_score is not null ";
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
            if(cu > 0){
                str += "-1 "+temp;
                str += "\n";
            }
            n++;
        }
        if(conn != null)conn.close();
        return str;
    }
    
    public String sqlSelectdata() throws SQLException{
        String sqlfold = " tf.ref_id,IFNULL(tf.score,0) AS score,";
        for(int i=0;i<numcol;i++){
            sqlfold += "k"+i+",";
        }
        sqlfold = sqlfold.substring(0, sqlfold.length()-1);
        return sqlfold;
    }
}
