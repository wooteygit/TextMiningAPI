/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignmentscoring.svm_classify;

import static com.assignmentscoring.model.app.*;

import libsvm.*;

/**
 *
 * @author wootey02
 */
public class svm_param {
    private svm_parameter param;
    private int nr_fold;
    
    public svm_parameter setParam(String argv[])throws Exception{
        param = new svm_parameter();
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.RBF;
        param.degree = 3;
        param.gamma = 0;	// 1/num_features
        param.coef0 = 0;
        param.nu = 0.5;
        param.cache_size = 100;
        param.C = 1;
        param.eps = 1e-3;
        param.p = 0.1;
        param.shrinking = 1;
        param.probability = 0;
        param.nr_weight = 0;
        param.weight_label = new int[0];
        param.weight = new double[0];
        
        int i = 0;
        svm_print_interface print_func = null;	// default printing to stdout
        for(i=0;i<argv.length;i++)
        {
            if(argv[i].charAt(0) != '-') break;
            if(++i>=argv.length){
                errCode = -4;
                errDesc = exit_with_help();
            }
            switch(argv[i-1].charAt(1))
            {
                case 's':
                    param.svm_type = atoi(argv[i]);
                    break;
                case 't':
                    param.kernel_type = atoi(argv[i]);
                    break;
                case 'd':
                    param.degree = atoi(argv[i]);
                    break;
                case 'g':
                    param.gamma = atof(argv[i]);
                    break;
                case 'r':
                    param.coef0 = atof(argv[i]);
                    break;
                case 'n':
                    param.nu = atof(argv[i]);
                    break;
                case 'm':
                    param.cache_size = atof(argv[i]);
                    break;
                case 'c':
                    param.C = atof(argv[i]);
                    break;
                case 'e':
                    param.eps = atof(argv[i]);
                    break;
                case 'p':
                    param.p = atof(argv[i]);
                    break;
                case 'h':
                    param.shrinking = atoi(argv[i]);
                    break;
                case 'b':
                    param.probability = atoi(argv[i]);
                    break;
                case 'q':
                    print_func = svm_print_null;
                    i--;
                    break;
                case 'v':
                    //cross_validation = 1;
                    nr_fold = atoi(argv[i]);
                    if(nr_fold < 2)
                    {
                        errCode = -1;
                        errDesc = "n-fold cross validation: n must >= 2 ==> "+exit_with_help();
                    }
                    break;
                case 'w':
                    ++param.nr_weight;
                    {
                        int[] old = param.weight_label;
                        param.weight_label = new int[param.nr_weight];
                        System.arraycopy(old,0,param.weight_label,0,param.nr_weight-1);
                    }

                    {
                        double[] old = param.weight;
                        param.weight = new double[param.nr_weight];
                        System.arraycopy(old,0,param.weight,0,param.nr_weight-1);
                    }

//                    param.weight_label[param.nr_weight-1] = atoi(argv[i-1].substring(2));
//                    param.weight[param.nr_weight-1] = atof(argv[i]);
                    break;
                default:
                    errCode = -2;
                    errDesc = "Unknown option: " + argv[i-1] + " ==> "+exit_with_help();
            }
        }
        svm.svm_set_print_string_function(print_func);

//        if(i >= argv.length){
//            errCode = -3;
//            errDesc = exit_with_help();
//        }
        return param;
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
    
    private String exit_with_help()
    {
        String res =
         "Usage: svm_train [options] training_set_file [model_file]\n"
        +"options:\n"
        +"-s svm_type : set type of SVM (default 0)\n"
        +"	0 -- C-SVC		(multi-class classification)\n"
        +"	1 -- nu-SVC		(multi-class classification)\n"
        +"	2 -- one-class SVM\n"
        +"	3 -- epsilon-SVR	(regression)\n"
        +"	4 -- nu-SVR		(regression)\n"
        +"-t kernel_type : set type of kernel function (default 2)\n"
        +"	0 -- linear: u'*v\n"
        +"	1 -- polynomial: (gamma*u'*v + coef0)^degree\n"
        +"	2 -- radial basis function: exp(-gamma*|u-v|^2)\n"
        +"	3 -- sigmoid: tanh(gamma*u'*v + coef0)\n"
        +"	4 -- precomputed kernel (kernel values in training_set_file)\n"
        +"-d degree : set degree in kernel function (default 3)\n"
        +"-g gamma : set gamma in kernel function (default 1/num_features)\n"
        +"-r coef0 : set coef0 in kernel function (default 0)\n"
        +"-c cost : set the parameter C of C-SVC, epsilon-SVR, and nu-SVR (default 1)\n"
        +"-n nu : set the parameter nu of nu-SVC, one-class SVM, and nu-SVR (default 0.5)\n"
        +"-p epsilon : set the epsilon in loss function of epsilon-SVR (default 0.1)\n"
        +"-m cachesize : set cache memory size in MB (default 100)\n"
        +"-e epsilon : set tolerance of termination criterion (default 0.001)\n"
        +"-h shrinking : whether to use the shrinking heuristics, 0 or 1 (default 1)\n"
        +"-b probability_estimates : whether to train a SVC or SVR model for probability estimates, 0 or 1 (default 0)\n"
        +"-wi weight : set the parameter C of class i to weight*C, for C-SVC (default 1)\n"
        +"-v n : n-fold cross validation mode\n"
        +"-q : quiet mode (no outputs)\n";
        return res;
    }
    
    private static svm_print_interface svm_print_null = new svm_print_interface()
    {
        public void print(String s) {}
    };
}

