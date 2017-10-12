/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignmentscoring.StopWord;

import com.assignmentscoring.Tokenizer.LongLexTo;
import com.assignmentscoring.model.app;
import java.io.*;
import java.util.*;

/**
 *
 * @author wootey02
 */
public class stopWord extends app{
//    private  ArrayList<String> listStopWords=new ArrayList<String>();
     
    public  String checkWordsAuto() throws IOException{
        String wordstop = "";
        ClassLoader classLoader = getClass().getClassLoader();
        String dir_lexitron = classLoader.getResource("/").getFile()+"lexitron.txt";
        dir_lexitron = dir_lexitron.replaceAll("%20", " ");
        LongLexTo tokenizer = new LongLexTo(new File(dir_lexitron),dir_lexitron);

        String dir_unknown = classLoader.getResource("/").getFile()+"unknown.txt";
        dir_unknown = dir_unknown.replaceAll("%20", " ");
        File unknownFile = new File(dir_unknown);
        if(unknownFile.exists()){
          tokenizer.addDict(unknownFile,dir_unknown);
        }
        Vector typeList;
        String text = "", line = "";
        char ch;
        FileReader fr;
        BufferedReader br;  
        String dir_stopword = classLoader.getResource("/").getFile()+"stopword.txt";
        dir_stopword = dir_stopword.replaceAll("%20", " ");
        File files = new File(dir_stopword);
        int begin, end, type;
        if(files != null){
            wordstop += files;
            fr = new FileReader(files);
            br = new BufferedReader(new InputStreamReader(new FileInputStream(dir_stopword), "UTF8"));
            while((line = br.readLine()) != null) {
                line = line.trim();
                if(line.length() > 0) {
                    tokenizer.wordInstance(line);
                    typeList = tokenizer.getTypeList();
                    begin = tokenizer.first();
                    int i = 0;
                    while(tokenizer.hasNext()) {
                        String str = "";
                        end = tokenizer.next();
        //                str=line.substring(begin, end);
                        type = ((Integer)typeList.elementAt(i++)).intValue();
                        if(type == 0){ 
                            str = line.substring(begin, end);  
                            if(!str.trim().equals("")){
                                listStopWords.add(str.trim());
                            }
                        }else if(type == 1){
                            str = line.substring(begin, end);  
                            if(!str.trim().equals("")){
                                listStopWords.add(str.trim());
                            }
                        }else if(type == 2){
                            str = line.substring(begin, end);  
                            if(!str.trim().equals("")){
                                listStopWords.add(str.trim());
                            }
                        }else if(type == 3){
                           str = line.substring(begin, end);  
                            if(!str.trim().equals("")){
                                listStopWords.add(str.trim());
                            }
                        }else if(type == 4){
                            str = line.substring(begin, end);  
                            if(!str.trim().equals("")){
                                listStopWords.add(str.trim());
                            }
                        }
                        begin = end;
                    }    
                }
            } 
            fr.close();
        }
        return wordstop;
    } 
    
    public  ArrayList<String> listStopWords(){
        return listStopWords;
    }
}
