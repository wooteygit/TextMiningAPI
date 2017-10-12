/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignmentscoring.synonym;
import com.assignmentscoring.Tokenizer.LongLexTo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import com.assignmentscoring.model.app;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 *
 * @author wootey02
 */
public class Synonym extends app{
    ModelSynonym mod ;
    public void checkWordsSynonym() throws IOException{
        String  line;
        FileReader fr;
        BufferedReader br;   
        ClassLoader classLoader = getClass().getClassLoader();
        String dir = classLoader.getResource("/").getFile()+"synonym.csv";
        dir = dir.replaceAll("%20", " ");
        File files = new File(dir);
        String cvsSplitBy = ",";       
        if(files != null){
            fr = new FileReader(files);
            br = new BufferedReader(new InputStreamReader(new FileInputStream(dir), "UTF8"));
            while((line = br.readLine())!=null) {
                line = line.trim();
                if(line.length()>0) {
                    String[] csvline = line.split(cvsSplitBy);
                    if(csvline.length == 2){
                        if((!csvline[0].toString().trim().equals("word")) && (!csvline[1].toString().trim().equals("mean"))){
                            mod = new ModelSynonym(csvline[0].toString(),csvline[1].toString());  
                            Msyn.add(mod);
                        } 
                    }
                }
            } 
            fr.close();
        }
    } 
    public  ArrayList<ModelSynonym> listSynoney(){
        return Msyn;
    }
}
