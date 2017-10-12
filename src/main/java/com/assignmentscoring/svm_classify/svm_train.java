/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignmentscoring.svm_classify;
import com.assignmentscoring.config.ConnectionDB;
import com.assignmentscoring.controllers.AccessoriesFunction;
import static com.assignmentscoring.model.app.*;
import com.assignmentscoring.directive.*;
import libsvm.*;
import java.io.*;
import java.util.*;
/**
 *
 * @author wootey02
 */

public class svm_train extends com.assignmentscoring.model.app{
    private svm_parameter param;		// set by parse_command_line
    private svm_problem prob;		// set by read_problem
    private svm_model model;
    private String input_file_name;		// set by parse_command_line
    private String model_file_name;		// set by parse_command_line
    private String error_msg;
    private int cross_validation;
    private int nr_fold;
    private String argv[];
    private String fileName = "";
    
    public svm_model run(String argv[],String input_name,String model_name) throws IOException, Exception
    { 
        this.argv = argv;
        svm_param sv = new svm_param();
        param = sv.setParam(this.argv);
        fileName = setFileName();
        if(input_name.equals("")){
            input_file_name = DEFAULT_PATH+"Train_"+fileName+".csv";
        }else{
            input_file_name = DEFAULT_PATH+input_name;
        }
        if(model_name.equals("")){
            model_file_name = DEFAULT_PATH+"BestModel_"+fileName+".txt";
        }else{
            model_file_name = DEFAULT_PATH+model_name;
        }
        read_problem();
        
        error_msg = svm.svm_check_parameter(prob,param);

        if(error_msg != null)
        {
            errCode = -1;
            errDesc = "ERROR: "+error_msg+"\n";
        }

        if(cross_validation != 0)
        {
            do_cross_validation();
        }
        else
        {
            model = svm.svm_train(prob,param);
            svm.svm_save_model(model_file_name,model);
        }
        return model;
    }

    private void do_cross_validation()
    {
        int i;
        int total_correct = 0;
        double total_error = 0;
        double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
        double[] target = new double[prob.l];

        svm.svm_cross_validation(prob,param,nr_fold,target);
        if(param.svm_type == svm_parameter.EPSILON_SVR ||
           param.svm_type == svm_parameter.NU_SVR)
        {
            for(i=0;i<prob.l;i++)
            {
                double y = prob.y[i];
                double v = target[i];
                total_error += (v-y)*(v-y);
                sumv += v;
                sumy += y;
                sumvv += v*v;
                sumyy += y*y;
                sumvy += v*y;
            }
            System.out.print("Cross Validation Mean squared error = "+total_error/prob.l+"\n");
            System.out.print("Cross Validation Squared correlation coefficient = "+
                ((prob.l*sumvy-sumv*sumy)*(prob.l*sumvy-sumv*sumy))/
                ((prob.l*sumvv-sumv*sumv)*(prob.l*sumyy-sumy*sumy))+"\n"
            );
        }
        else
        {
            for(i=0;i<prob.l;i++)
                if(target[i] == prob.y[i])
                    ++total_correct;
            System.out.print("Cross Validation Accuracy = "+100.0*total_correct/prob.l+"%\n");
        }
    }

    private static double atof(String s)throws Exception
    {
        double d = Double.valueOf(s).doubleValue();
        if (Double.isNaN(d) || Double.isInfinite(d))
        {
            errCode = -11;
            errDesc = "NaN or Infinity in input";
        }
        return(d);
    }

    private static int atoi(String s)throws Exception
    {
        return Integer.parseInt(s);
    }

    private void read_problem() throws IOException, Exception
    {
        BufferedReader fp = new BufferedReader(new FileReader(input_file_name));
        Vector<Double> vy = new Vector<Double>();
        Vector<svm_node[]> vx = new Vector<svm_node[]>();
        int max_index = 0;

        while(true)
        {
            String line = fp.readLine();
            if(line == null) break;

            StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");

            vy.addElement(atof(st.nextToken()));
            int m = st.countTokens()/2;
            svm_node[] x = new svm_node[m];
            for(int j=0;j<m;j++)
            {
                    x[j] = new svm_node();
                    x[j].index = atoi(st.nextToken());
                    x[j].value = atof(st.nextToken());
            }
            if(m>0) max_index = Math.max(max_index, x[m-1].index);
            vx.addElement(x);
        }

        prob = new svm_problem();
        prob.l = vy.size();
        prob.x = new svm_node[prob.l][];
        for(int i=0;i<prob.l;i++)
            prob.x[i] = vx.elementAt(i);
        prob.y = new double[prob.l];
        for(int i=0;i<prob.l;i++)
            prob.y[i] = vy.elementAt(i);

        if(param.gamma == 0 && max_index > 0)
            param.gamma = 1.0/max_index;

        if(param.kernel_type == svm_parameter.PRECOMPUTED)
            for(int i=0;i<prob.l;i++)
            {
                if (prob.x[i][0].index != 0)
                {
                    errCode = -8;
                    errDesc = "Wrong kernel matrix: first column must be 0:sample_serial_number";
                    return;
                }
                if ((int)prob.x[i][0].value <= 0 || (int)prob.x[i][0].value > max_index)
                {
                    errCode = -9;
                    errDesc = "Wrong input format: sample_serial_number out of range";
                    return;
                }
            }
        fp.close();
    }
}

