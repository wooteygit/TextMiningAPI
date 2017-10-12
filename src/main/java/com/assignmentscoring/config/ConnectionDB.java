/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignmentscoring.config;

import java.io.*;
import java.sql.*;
import java.util.*;
import com.google.gson.*;
import com.assignmentscoring.controllers.controllers;
import com.assignmentscoring.model.app;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author wootey02
 */
public class ConnectionDB extends app{
    
    public String connectsql()throws IOException{
        String jsonStr = "",status  = "";		
	ClassLoader classLoader = getClass().getClassLoader();
	try {
	    jsonStr = IOUtils.toString(classLoader.getResourceAsStream("config.json"));
            Gson gson = new Gson();
            JsonElement element = gson.fromJson (jsonStr, JsonElement.class);
            JsonObject jsonObj = element.getAsJsonObject();
            ipAddress = jsonObj.get("host").getAsString();
            dbName = jsonObj.get("database_data").getAsString();
            dbNameGen = jsonObj.get("database_gen").getAsString();
            usrName = jsonObj.get("user").getAsString();
            pass = jsonObj.get("password").getAsString();
            urlGen="jdbc:mysql://"+ipAddress+"/"+dbNameGen+"?zeroDateTimeBehavior=convertToNull";
            url="jdbc:mysql://"+ipAddress+"/"+dbName+"?zeroDateTimeBehavior=convertToNull"; 
	} catch (IOException e) {
            status = "IOUtils IOException : "+e;
	}
        return status;
    }
    
    public String ckConnect()throws SQLException,Exception{
        String status = "";
        try {
            status = connectsql();
            if(status.length() == 0){
                Class.forName("com.mysql.jdbc.Driver");
                conn =  DriverManager.getConnection(url,usrName,pass);
                if(conn != null){
                    status = "Connection Succesfull!!!";
                } else {
                    status = "Connection faile";
                }
            }
        } catch (SQLException e) {
            status = "Exception "+e;
        }catch (Exception ex){
            status = "SQLException "+ex;
        }
        return status;
    }
    
    public String getFileWithUtil(String fileName) throws IOException{
	String result = "Reade file success !!";		
	ClassLoader classLoader = getClass().getClassLoader();
        result = IOUtils.toString(classLoader.getResourceAsStream(fileName));		
	return result;
    }
    
    public ResultSet connectoDB(String sql)throws SQLException,IOException,UserException, Exception{
        ResultSet rs = null;
//        Connection conn = null;
        PreparedStatement per  = null;
        connectsql();
        Class.forName(driver);
        conn = DriverManager.getConnection(url,usrName,pass);
        per = conn.prepareStatement(sql);
        rs = per.executeQuery();
        return rs;
    }
    public void saveWordsToDB(String str)throws IOException, Exception,UserException{
        if(str != "" && str != null && !str.equals("")){
            connectsql();
//            Connection conn = null;
            PreparedStatement per = null;
            try{
                String sql = str;
                Class.forName(driver);
                conn = DriverManager.getConnection(url,usrName,pass);
                per = conn.prepareStatement(sql);
                int re = per.executeUpdate();
                if(re>0){

                }else{
                    //throw new UserException("Can't "+sql); 
                }
                if(per != null) per.close(); 
                if(conn != null)  conn.close();
            }catch(SQLException ex){
                throw new SQLException(ex+"\nCan't "+str); 
            }catch(Exception e){
                throw new Exception(e+"\nCan't "+str); 
            }
        }else{
            throw new Exception("Sql query emty!"); 
        }
    }
    
    public int countWords()throws IOException, Exception{
        connectsql();
//        Connection conn = null;
        ResultSet rs = null;
        int count = 0;
        PreparedStatement per = null;
        try{
            Class.forName(driver);
            conn = DriverManager.getConnection(url,usrName,pass);
            per = conn.prepareStatement("SELECT COUNT(*) AS a FROM words");
            rs = per.executeQuery();
            while (rs.next()) {              
              count = Integer.parseInt(rs.getString("a").toString());
            }  
        }catch(Exception e){
            throw new Exception(e);  
        }
        return count;
    }
    
    public int countWords3()throws IOException, Exception{
        connectsql();
//        Connection conn = null;
        ResultSet rs = null;
        int count = 0;
        PreparedStatement per = null;
        try{
            Class.forName(driver);
            conn = DriverManager.getConnection(url,usrName,pass);
            per = conn.prepareStatement("SELECT COUNT(*) AS a FROM words3");
            rs = per.executeQuery();
            while (rs.next()) {              
              count = Integer.parseInt(rs.getString("a").toString());
            }  
        }catch(Exception e){
            throw new Exception(e);  
        }
        return count;
    }
    
    public void deleteAll(String sql,String tabelname)throws IOException,Exception{
        connectsql();
//        Connection conn = null;
        PreparedStatement per = null;
        try{
            int test = 0;
            ResultSet rs = connectoDB("SELECT COUNT(*) AS CU FROM "+tabelname);
            while (rs.next()) {
                test = rs.getInt("CU");
            }
            if(test != 0){
                Class.forName(driver);
                conn = DriverManager.getConnection(url,usrName,pass);
                per = conn.prepareStatement(sql);
                int re = per.executeUpdate();
                if(re>0){

                }else{
//                    throw new UserException("Can't "+sql); 
                }
            }
            if(conn != null)  conn.close();
        }catch(Exception e){
            throw new Exception(e); 
        }
         
    }
    
    public void deleteTFIDF()throws IOException,UserException,Exception{
        connectsql();
//        Connection conn = null;
        PreparedStatement per = null;
        try{
            Class.forName(driver);
            conn = DriverManager.getConnection(url,usrName,pass);
            per = conn.prepareStatement("DELETE FROM tfidf");
            int re = per.executeUpdate();
            if(re > 0){

            }
            else{
                throw new UserException("Can't DELETE FROM tfidf"); 
            }
        }catch(Exception e){
            throw new Exception(e); 
        }
        if(conn != null)  conn.close(); 
    }
    
    public void deleteTFIDFNormalized()throws IOException,SQLException,UserException, Exception{
        connectsql();
//        Connection conn = null;
        PreparedStatement per = null;
        try{
            int test = 0;
            ResultSet rs = connectoDB("SELECT COUNT(*) AS CU FROM tfidfNormalized");
            while (rs.next()) {
                test = rs.getInt("CU");
            }
            if(test != 0){
                Class.forName(driver);
                conn = DriverManager.getConnection(url,usrName,pass);
                per = conn.prepareStatement("DELETE FROM tfidfNormalized");
                int re = per.executeUpdate();
                if(re > 0){
                    
                }else{
                    throw new UserException("Can't DELETE FROM tfidfNormalized");
                }
            }
        }catch(Exception e){
            throw new Exception(e);
        }
        if(conn != null)  conn.close(); 
    }
    
    public String getJSONFromResultSet(String sql,String keyName) throws SQLException,Exception{
        Gson gson = new Gson();
        Map json = new HashMap(); 
        List list = new ArrayList();
        try {
            ResultSet rs = connectoDB(sql);
            if(rs != null){
                ResultSetMetaData metaData = rs.getMetaData();
                while(rs.next())
                {
                    Map<String,Object> columnMap = new HashMap<String, Object>();
                    for(int columnIndex=1;columnIndex<=metaData.getColumnCount();columnIndex++){
                        String val= rs.getString(metaData.getColumnName(columnIndex));
                        String key = metaData.getColumnLabel(columnIndex);
                        if(val== null)
                            columnMap.put(key, "");
                        else if (val.chars().allMatch(Character::isDigit))
                            columnMap.put(key,  Integer.parseInt(val));
                        else
                            columnMap.put(key,  val);
                    }
                    list.add(columnMap);
                }
                if(conn != null)  conn.close(); 
            } 
            json.put(keyName, list);
        }catch (SQLException e) {
            throw new SQLException(e);
        }catch (Exception ex) {
            throw new Exception(ex);
        }
        return gson.toJson(json);
//        return "444555";
    }
}
