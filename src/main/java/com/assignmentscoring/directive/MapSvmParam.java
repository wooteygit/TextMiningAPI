/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignmentscoring.directive;

/**
 *
 * @author wootey02
 */
public class MapSvmParam {
    private int c_svc = 0;
    private int one_class_svm = 0;
    private int nu_svc = 0;
    private int epsilon_svr  = 0;       
    private int nu_svr = 0;       
    private int linear = 0;      
    private int polynomial = 0;
    private int radial_basis = 0;     
    private int sigmoid = 0;       
    
    private SubSvmKernelFlot degree = null;
    private SubSvmKernelFlot coef0 = null;
    private SubSvmKernelFlot gamma = null;
    private SubSvmKernelFlot cost = null;
    private SubSvmKernelFlot nu = null;
    private SubSvmKernelFlot epsilon = null;
    private SubSvmKernelFlot p = null;
    private int cachsize = 0;
    private String scoreFile = "";

    public String getScoreFile() {
        return scoreFile;
    }

    public void setScoreFile(String scoreFile) {
        this.scoreFile = scoreFile;
    }

    public int getC_svc() {
        return c_svc;
    }

    public void setC_svc(int c_svc) {
        this.c_svc = c_svc;
    }

    public int getOne_class_svm() {
        return one_class_svm;
    }

    public void setOne_class_svm(int one_class_svm) {
        this.one_class_svm = one_class_svm;
    }

    public int getNu_svc() {
        return nu_svc;
    }

    public void setNu_svc(int nu_svc) {
        this.nu_svc = nu_svc;
    }

    public int getEpsilon_svr() {
        return epsilon_svr;
    }

    public void setEpsilon_svr(int epsilon_svr) {
        this.epsilon_svr = epsilon_svr;
    }

    public int getNu_svr() {
        return nu_svr;
    }

    public void setNu_svr(int nu_svr) {
        this.nu_svr = nu_svr;
    }

    public int getLinear() {
        return linear;
    }

    public void setLinear(int linear) {
        this.linear = linear;
    }

    public int getPolynomial() {
        return polynomial;
    }

    public void setPolynomial(int polynomial) {
        this.polynomial = polynomial;
    }

    public int getRadial_basis() {
        return radial_basis;
    }

    public void setRadial_basis(int radial_basis) {
        this.radial_basis = radial_basis;
    }

    public int getSigmoid() {
        return sigmoid;
    }

    public void setSigmoid(int sigmoid) {
        this.sigmoid = sigmoid;
    }

    public SubSvmKernelFlot getDegree() {
        return degree;
    }

    public void setDegree(SubSvmKernelFlot degree) {
        this.degree = degree;
    }

    public SubSvmKernelFlot getCoef0() {
        return coef0;
    }

    public void setCoef0(SubSvmKernelFlot coef0) {
        this.coef0 = coef0;
    }

    public SubSvmKernelFlot getGamma() {
        return gamma;
    }

    public void setGamma(SubSvmKernelFlot gamma) {
        this.gamma = gamma;
    }

    public SubSvmKernelFlot getCost() {
        return cost;
    }

    public void setCost(SubSvmKernelFlot cost) {
        this.cost = cost;
    }

    public SubSvmKernelFlot getNu() {
        return nu;
    }

    public void setNu(SubSvmKernelFlot nu) {
        this.nu = nu;
    }

    public SubSvmKernelFlot getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(SubSvmKernelFlot epsilon) {
        this.epsilon = epsilon;
    }

    public SubSvmKernelFlot getP() {
        return p;
    }

    public void setP(SubSvmKernelFlot p) {
        this.p = p;
    }

    public int getCachsize() {
        return cachsize;
    }

    public void setCachsize(int cachsize) {
        this.cachsize = cachsize;
    }          
}
