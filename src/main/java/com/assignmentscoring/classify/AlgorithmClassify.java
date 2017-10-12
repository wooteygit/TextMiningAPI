/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignmentscoring.classify;
import static com.assignmentscoring.classify.ClassifyModel.featuresTesting;
import com.assignmentscoring.config.*;
import com.assignmentscoring.directive.*;
import com.assignmentscoring.model.*;
import java.io.*;
import java.sql.ResultSet;
import java.util.*;
import libsvm.*;
/*import libsvm.LibSVM;

/**
 *
 * @author wootey02
 */
public class AlgorithmClassify extends com.assignmentscoring.model.app{
    static HashMap<Integer, HashMap<Integer, Double>> featuresTraining;
    static HashMap<Integer, Double> labelTraining;
    static HashMap<Integer, HashMap<Integer, Double>> featuresTesting;
    static HashSet<Integer> features;
    static svm_problem prob;
    static svm_model model;
    static svm_parameter param;
        
    public void SVMclassifyTrain() throws Exception{
        param = new svm_parameter();
        param.svm_type = 0;
        param.kernel_type = 2;
        if(C_SVC == 1){
            param.svm_type = svm_parameter.C_SVC;
        }else if(CLASS_SVM == 1){
            param.svm_type = svm_parameter.ONE_CLASS;
        }else if(NU_SVC == 1){
            param.svm_type = svm_parameter.NU_SVC;
        }else if(NU_SVR == 1){
            param.svm_type = svm_parameter.NU_SVR;
        }else if(NU_SVR == 1){
            param.svm_type = svm_parameter.EPSILON_SVR;
        }
        
        if(LINEARS == 1){
            param.kernel_type = svm_parameter.LINEAR;
        }else if(POLYNOMIAL == 1){
            param.kernel_type = svm_parameter.POLY;
        }else if(RADIAL == 1){
            param.kernel_type = svm_parameter.RBF;
        }else if(SIGMOID == 1){
            param.kernel_type = svm_parameter.SIGMOID;
        }
        
        param.gamma = GAMMA;
        param.nu = NU;
        param.degree = DEGREE;
        param.C = COST;
        param.coef0 = COEF;
        param.eps = EPSILON;  
        
        param.cache_size = CACH_SIZE;
        param.p = P;
        
        featuresTraining = new HashMap<Integer, HashMap<Integer, Double>>();
        labelTraining = new HashMap<Integer, Double>();
        features = new HashSet<Integer>();
        //Read in training data
        BufferedReader reader = null;
        try{
            String fr = DEFAULT_PATH+"Train_"+setFileName()+".csv";
            reader = new BufferedReader(new FileReader(fr));
            String line = null;
            int lineNum = 0;
            while((line = reader.readLine())!=null){
                featuresTraining.put(lineNum, new HashMap<Integer,Double>());
                String[] tokens = line.split("\\s+");
                Double label = Double.parseDouble(tokens[0]);
                labelTraining.put(lineNum, label);
                for(int i=1;i<tokens.length;i++){
                    String[] fields = tokens[i].split(":");
                    int featureId = Integer.parseInt(fields[0]);
                    double featureValue = Double.parseDouble(fields[1]);
                    features.add(featureId);
                    featuresTraining.get(lineNum).put(featureId, featureValue);
                }
                lineNum++;
            }
            reader.close();
         //Train the SVM model
            prob = new svm_problem();
            int numTrainingInstances = featuresTraining.keySet().size();
            prob.l = numTrainingInstances;
            prob.y = new double[prob.l];
            prob.x = new svm_node[prob.l][];

            for(int i=0;i<numTrainingInstances;i++){
                HashMap<Integer,Double> tmp = featuresTraining.get(i);
                prob.x[i] = new svm_node[tmp.keySet().size()];
                int indx = 0;
                for(Integer id:tmp.keySet()){
                    svm_node node = new svm_node();
                    node.index = id;
                    node.value = tmp.get(id);
                    prob.x[i][indx] = node;
                    indx++;
                }
                prob.y[i] = labelTraining.get(i);
            }
            model = svm.svm_train(prob,param);
            svm.svm_save_model(DEFAULT_PATH+"BestModel_"+setFileName()+".txt", model);
        }catch(Exception e){
            throw new Exception(" SVMclassifyTrain : "+e);
        }    
    }
    
    public MapclassifyAns SVMclassifyTest(svm_model mol) throws Exception,IOException,NullPointerException{
        //Read in test data
        model = mol;
        int start_classify = START_CLASSIFY;
        int[] itemwrong = new int[2];
        int wrong = 0,corrcet=0;
        SetAllID();
        MapclassifyAns mcf = new MapclassifyAns();
        ArrayList<MapField> arrmp = new ArrayList<MapField>();
        MapField mp ;
        featuresTesting = new HashMap<Integer, HashMap<Integer, Double>>();
        BufferedReader reader = null;
        try{
            String fr = DEFAULT_PATH+"Test_"+setFileName()+".csv";
            reader = new BufferedReader(new FileReader(fr));
            String line = null;
            int lineNum = 0;
            int count = 0;
            while((line = reader.readLine()) != null){ 
                featuresTesting.put(lineNum, new HashMap<Integer,Double>());
                String[] tokens = line.split("\\s+");
                for(int i=1; i<tokens.length;i++){
                    String[] fields = tokens[i].split(":");
                    int featureId = Integer.parseInt(fields[0]);
                    double featureValue = Double.parseDouble(fields[1]);
                    featuresTesting.get(lineNum).put(featureId, featureValue);
                }
                lineNum++;
                count++;
            }
            reader.close();
        /*Test Instance*/
//            BLANCE.clear();
//            for(int x=0;x<sid.size();x++){
//                String str = "SELECT ma.ans_des FROM m_student ms " +
//                    " INNER JOIN m_answer ma ON  ms.sd_id = ma.sd_id " +
//                    " WHERE TRIM(ms.sd_code) = TRIM('"+sid.get(x)+"')";
//                BLANCE.put(sid.get(x),new app().getAns(str));
//            }
            int CU = start_classify;
            int n = 0;
            for(Integer testInstance : featuresTesting.keySet()){                   
                HashMap<Integer, Double> tmp = new HashMap<Integer, Double>();
                tmp = featuresTesting.get(testInstance);
                int numFeatures = tmp.keySet().size();
                svm_node[] x = new svm_node[numFeatures];
                int featureIndx = 0;
                for(Integer feature:tmp.keySet()){
                    x[featureIndx] = new svm_node();
                    x[featureIndx].index = feature;
                    x[featureIndx].value = tmp.get(feature);
                    featureIndx++;
                }
                double d = svm.svm_predict(model, x);
                if(n < sid.size()){
//                    mp = new MapField();
//                    mp.setName(sid.get(n));
//                    mp.setValue(""+d);
//                    mp.setDes(BLANCE.get(sid.get(n)).toString());
//                    arrmp.add(mp);
                    if(Double.parseDouble(ITEM_WRONG.get(sid.get(n).trim()).toString()) == d){
                        corrcet++;
                    }else{
                        wrong++;
                    }
                }
//                System.out.println(testInstance+"\t"+d);
                n++;
            }
        }catch (NullPointerException ex){
            throw new Exception("SVMclassifyTest (NullPointerException) : "+ex);
        }catch(Exception e){
            throw new Exception("SVMclassifyTest (Exception) : "+e);
        }
        itemwrong[0] = corrcet;
        itemwrong[1] = wrong;
        //mcf.setMp(arrmp);
        mcf.setItemwrong(itemwrong);
        return mcf;
    }
    
    public String sqlSelectdata(int numcol,int tf){
        String sql = "SELECT ",w = "";
        for(int i=0;i<numcol;i++){
            sql += "k"+i+",";
        }
        for(int i=0;i<tf;i++){
            w += "'w"+i+"',";
        }
        sql = sql.substring(0, sql.length()-1);
        w = w.substring(0, w.length()-1);
        sql += " FROM tfidfnormalized WHERE ID IN("+w+")";
        return sql;
    }
    
    public void setItemWrong(String filename) throws IOException{
        ITEM_WRONG.clear();
        String line = "";
        BufferedReader br = null;
        String cvsSplitBy = ",";
        try {
            br = new BufferedReader(new FileReader(DEFAULT_PATH+filename));
            int cu = 0;
            while ((line = br.readLine()) != null ) {
                String[] count = line.split(cvsSplitBy);
                if(cu > 0){
                    ITEM_WRONG.put(count[0].toString(),count[1].trim().toString());
                }
                cu++;
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(""+e);
        } catch (IOException e) {
            throw new IOException(e);
        } 
    }
    
    public void setItemWrongAll() throws IOException, UserException, Exception{
        ITEM_WRONG.clear();
        String sql = "SELECT tfidfnormalized.ref_id,tfidfnormalized.score FROM `tfidfnormalized` "
        +" INNER JOIN t_answer ON tfidfnormalized.ref_id = t_answer.sd_id "
        +" WHERE tfidfnormalized.ID LIKE 'w%' AND ref_id is not null ";
        ResultSet rs = (ResultSet) new ConnectionDB().connectoDB(sql);
        while (rs.next()) {
            ITEM_WRONG.put(rs.getString("ref_id"),rs.getString("score"));
        }
        if(conn != null)  conn.close();
    }
}
