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

import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.io.IOUtils;
import com.google.gson.*;
import com.sun.jersey.core.impl.provider.entity.XMLRootElementProvider.*;
import com.assignmentscoring.StopWord.stopWord;
import com.assignmentscoring.directive.*;
import static com.assignmentscoring.model.app.*;
import java.sql.*;
import java.util.Hashtable;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.ws.rs.WebApplicationException;
/**
 *
 * @author wootey02
 */
public class ctr_clustering {
    public ReturnTempTable setWordToDB(String fileAns,int k,int is_model){
        ReturnTempTable rt = new ReturnTempTable();
        String dir = DEFAULT_PATH+fileAns;
        GROUP = k;
        try {           
            LongLexTo lonlex = new LongLexTo();
            stopWord st = new stopWord();
            st.checkWordsAuto();
            Synonym syn = new Synonym();
            syn.checkWordsSynonym();
            
            new ConnectionDB().deleteAll("DELETE FROM words", "words");
            new ConnectionDB().deleteAll("DELETE FROM tfidfNormalized", "tfidfNormalized");
            new ConnectionDB().saveWordsToDB("CALL setCenterParam(3,NULL,'"+fileAns+"')");
            new ConnectionDB().saveWordsToDB("CALL setCenterParam(9,"+GROUP+",null)");
            new ConnectionDB().deleteAll("DELETE FROM model_temp","model_temp");
            lonlex.mainLongLexTo(dir,is_model);
            Set<String> keys = BLANCE.keySet();
            for(String key: keys){
                new ConnectionDB().saveWordsToDB("CALL setAnswer(null,'"+key+"',null,'"+BLANCE.get(key)+"',null)");
            }
            countWords = new ConnectionDB().countWords();
            TFIDF = new ListKeyWord[countWords];
            calTFIDF cal = new calTFIDF();
            cal.addData(is_model);
            cal.insertToTFIDFNORMALIED();
            clustering clus = new clustering();
            clus.kmeans();
//            if(conn != null) conn.close();
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
            errDesc = ctr_clustering.class.getName()+" Exception : "+ex;
        }
        rt.setErrCode(errCode);
        rt.setErrDesc(errDesc);
        return rt;
    }
}
