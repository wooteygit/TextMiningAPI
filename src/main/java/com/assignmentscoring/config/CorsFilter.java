/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignmentscoring.config;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author wootey02
 */
public class CorsFilter implements Filter{
    @Override
    public void init(FilterConfig arg0) throws ServletException {}
 
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain chain) throws IOException, ServletException {
        // TODO Auto-generated method stub
        HttpServletResponse response=(HttpServletResponse) resp;
 
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT ,OPTIONS, DELETE, HEAD");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
 
        chain.doFilter(req, resp);
    }
 
    @Override
    public void destroy() {}
}
