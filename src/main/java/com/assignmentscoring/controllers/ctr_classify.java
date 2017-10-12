/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignmentscoring.controllers;

import com.assignmentscoring.classify.*;
import com.assignmentscoring.config.ConnectionDB;
import com.assignmentscoring.directive.*;
import com.assignmentscoring.model.app;
import static com.assignmentscoring.model.app.*;
import java.util.*;

/**
 *
 * @author wootey02
 */
public class ctr_classify  extends app{
    public ReturnTestData TestBestModel(int sj_id,int p_sj_id,int sj_seq ,int num_k,double perc){ 
        ReturnTestData rt = new ReturnTestData();
        ArrayList<MapField> arrmf;
        ClassifyModel  fma = new ClassifyModel();
        MapclassifyAns mca ;
        WriteTestByCluster wri = new WriteTestByCluster();
        try{
            new ConnectionDB().deleteAll("DELETE FROM t_student_score","t_student_score");
            new ConnectionDB().deleteAll("DELETE FROM DATA_FILE WHERE FILE_TYPE = 7", "DATA_FILE");
            for(int i=0;i<num_k;i++){
                arrmf = new ArrayList<MapField>();
                wri.Writertest(num_k,i,perc);
                mca = fma.SVMclassifyTest(sj_id,p_sj_id,sj_seq,num_k,perc,i);
            }
            //new ConnectionDB().saveWordsToDB("call UP_RESCORE()");
        }catch(NullPointerException en){
            errCode = -7;
            errDesc = ""+en;
        }catch (Exception ex) {
            errCode = -3;
            errDesc = ""+ex;
        }
//        finally{
//            continue;
//        }
        rt.setErrCode(errCode);
        rt.setErrDesc(errDesc);
        return rt;
    }
}
