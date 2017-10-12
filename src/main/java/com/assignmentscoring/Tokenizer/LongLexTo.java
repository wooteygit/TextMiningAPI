package com.assignmentscoring.Tokenizer;

import java.io.*;
import java.sql.SQLException;
import java.util.*;
import com.assignmentscoring.config.*;
import com.assignmentscoring.StopWord.*;
import com.assignmentscoring.synonym.*;

public class LongLexTo extends com.assignmentscoring.model.app{

  //Private variables
  private Trie dict;               //For storing words from dictionary
  private LongParseTree ptree;     //Parsing tree (for Thai words)
  //Returned variables
  private Vector indexList;  //List of word index positions
  private Vector lineList;   //List of line index positions
  private Vector typeList;   //List of word types (for word only)
  private Iterator iter;     //Iterator for indexList OR lineList (depends on the call)
  
  /*********************** Return index list *************************/
  public Vector getIndexList() {
    return indexList; }
  /*********************** Return type list *************************/
  public Vector getTypeList() {
    return typeList; }
  /******************** Iterator for index list **********************/
  //Return iterator's hasNext for index list 
  public boolean hasNext() {
    if(!iter.hasNext())
      return false;
    return true;
  } 
  //Return iterator's first index
  public int first() {
    return 0;
  }
  //Return iterator's next index
  public int next() {
    return((Integer)iter.next()).intValue();
  }
  /********************** Constructor (default) **********************/
  public LongLexTo() throws IOException {
    dict=new Trie();
    ClassLoader classLoader = getClass().getClassLoader();
    String dir = classLoader.getResource("/").getFile()+"lexitron.txt";
    dir = dir.replaceAll("%20", " ");
    File dictFile=new File(dir);
    if(dictFile.exists())
      addDict(dictFile,dir);
    else
      System.out.println(" !!! Error: Missing default dictionary file, lexitron.txt");
    indexList=new Vector();
    lineList=new Vector();
    typeList=new Vector();
    ptree=new LongParseTree(dict, indexList, typeList);
  } //Constructor
  /************** Constructor (passing dictionary file ) *************/
  public LongLexTo(File dictFile,String dir) throws IOException {
    dict=new Trie();
    if(dictFile.exists())
      addDict(dictFile,dir);
    else
      System.out.println(" !!! Error: The dictionary file is not found, " + dictFile.getName());
    indexList=new Vector();
    lineList=new Vector();
    typeList=new Vector();
    ptree=new LongParseTree(dict, indexList, typeList);
  } //Constructor
  /**************************** addDict ******************************/
  public void addDict(File dictFile,String dir) throws IOException {
    //Read words from dictionary
    String line, word, word2;
    int index;
    FileReader fr = new FileReader(dictFile);
    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dir), "UTF8"));
    
    while((line=br.readLine())!=null) {
      line=line.trim();
      if(line.length()>0)
        dict.add(line);
    }
  } //addDict
  /************************** wordInstance ************************/
  public void wordInstance(String text) {

    indexList.clear();
    typeList.clear();    
    int pos, index;
    String word;
    boolean found;
    char ch;

    pos=0;
    while(pos<text.length()) {

      //Check for special characters and English words/numbers
      ch=text.charAt(pos);

      //English
      if(((ch>='A')&&(ch<='Z'))||((ch>='a')&&(ch<='z'))) {
        while((pos<text.length())&&(((ch>='A')&&(ch<='Z'))||((ch>='a')&&(ch<='z'))))
          ch=text.charAt(pos++);
        if(pos<text.length())
          pos--;
        indexList.addElement(new Integer(pos));
        typeList.addElement(new Integer(3));
      }
      //Digits
      else if(((ch>='0')&&(ch<='9'))||((ch>='๐')&&(ch<='๙'))) {
        while((pos<text.length())&&(((ch>='0')&&(ch<='9'))||((ch>='๐')&&(ch<='๙'))||(ch==',')||(ch=='.')))
          ch=text.charAt(pos++);
        if(pos<text.length())
          pos--;
        indexList.addElement(new Integer(pos));
        typeList.addElement(new Integer(3));
      }
      //Special characters
      else if((ch<='~')||(ch=='ๆ')||(ch=='ฯ')||(ch=='“')||(ch=='”')||(ch==',')) {
        pos++;
        indexList.addElement(new Integer(pos));
        typeList.addElement(new Integer(4));
      }
      //Thai word (known/unknown/ambiguous)
      else
        pos=ptree.parseWordInstance(pos, text);
    } //While all text length
    iter=indexList.iterator();
  } //wordInstance
  /************************** lineInstance ************************/
  public void lineInstance(String text) {
    int windowSize=10; //for detecting parentheses, quotes
    int curType, nextType, tempType, curIndex, nextIndex, tempIndex;
    lineList.clear();
    wordInstance(text);
    int i;
    for(i=0; i<typeList.size()-1; i++) {
      curType=((Integer)typeList.elementAt(i)).intValue();
      curIndex=((Integer)indexList.elementAt(i)).intValue();

      if((curType==3)||(curType==4)) {
    	//Parenthesese
    	if((curType==4)&&(text.charAt(curIndex-1)=='(')) {
          int pos=i+1;
          while((pos<typeList.size())&&(pos<i+windowSize)) {
	    tempType=((Integer)typeList.elementAt(pos)).intValue();
    	    tempIndex=((Integer)indexList.elementAt(pos++)).intValue();  
 	    if((tempType==4)&&(text.charAt(tempIndex-1)==')')) {
                lineList.addElement(new Integer(tempIndex));
                i=pos-1;
                break;
    	    }
    	  }
        }    	  
        //Single quote
    	else if((curType==4)&&(text.charAt(curIndex-1)=='\'')) {
          int pos=i+1;
          while((pos<typeList.size())&&(pos<i+windowSize)) {
	    tempType=((Integer)typeList.elementAt(pos)).intValue();
    	    tempIndex=((Integer)indexList.elementAt(pos++)).intValue();  
 	    if((tempType==4)&&(text.charAt(tempIndex-1)=='\'')) {
    	      lineList.addElement(new Integer(tempIndex));
    	      i=pos-1;
              break;
    	    }
    	  } 	    
    	}
    	//Double quote
    	else if((curType==4)&&(text.charAt(curIndex-1)=='\"')) {
          int pos=i+1;
          while((pos<typeList.size())&&(pos<i+windowSize)) {
	    tempType=((Integer)typeList.elementAt(pos)).intValue();
    	    tempIndex=((Integer)indexList.elementAt(pos++)).intValue();  
 	    if((tempType==4)&&(text.charAt(tempIndex-1)=='\"')) {
    	      lineList.addElement(new Integer(tempIndex));
    	      i=pos-1;
              break;
    	    }
    	  } 	    
    	}    	  
        else
          lineList.addElement(new Integer(curIndex));
      }
      else {
        nextType=((Integer)typeList.elementAt(i+1)).intValue();
        nextIndex=((Integer)indexList.elementAt(i+1)).intValue();
        if((nextType==3)||
          ((nextType==4)&&((text.charAt(nextIndex-1)==' ')||(text.charAt(nextIndex-1)=='\"')||
                           (text.charAt(nextIndex-1)=='(')||(text.charAt(nextIndex-1)=='\''))))
          lineList.addElement(new Integer(((Integer)indexList.elementAt(i)).intValue()));
        else if((curType==1)&&(nextType!=0)&&(nextType!=4))
          lineList.addElement(new Integer(((Integer)indexList.elementAt(i)).intValue()));
      }
    }
    if(i<typeList.size())
      lineList.addElement(new Integer(((Integer)indexList.elementAt(indexList.size()-1)).intValue()));
    iter=lineList.iterator(); 
  } //lineInstance
  /*************************** Demo *******************************/
  public void mainLongLexTo(String dir,int is_model) throws IOException,SQLException, ClassNotFoundException, Exception{
    ClassLoader classLoader = getClass().getClassLoader();
    String dir_lexitron = classLoader.getResource("/").getFile() + "lexitron.txt";
    dir_lexitron = dir_lexitron.replaceAll("%20", " ");
    LongLexTo tokenizer = new LongLexTo(new File(dir_lexitron),dir_lexitron);
    
    String dir_unknown = classLoader.getResource("/").getFile() + "unknown.txt";
    dir_unknown = dir_unknown.replaceAll("%20", " ");
    File unknownFile = new File(dir_unknown);
    if(unknownFile.exists()){
        tokenizer.addDict(unknownFile,dir_unknown);
    }
    Vector typeList;
    String text = "", line = "";
    char ch;
    int begin, end, type; 
    FileReader fr;
    BufferedReader br;
    ArrayList<String> keyWord = new ArrayList<String>();
    ArrayList<String> keyWordSort;
    br = new BufferedReader(new InputStreamReader(new FileInputStream(dir), "UTF8"));
    String cvsSplitBy = ",";
    int cu = 0;
    int ans_id = 1;
    while((line = br.readLine())!= null) {
        line = line.trim();
        keyWordSort = new ArrayList<String>();
        if(line.length()>0) {
            String[] csvline = line.split(cvsSplitBy);
            String strw = "";
            if((csvline.length == 3 || csvline.length == 2) && cu > 0){ 
                if(csvline.length == 3 ){
                    line = csvline[0].toString() + csvline[1].toString()+" "+csvline[2].toString();
                    BLANCE.put(csvline[0].toString() + csvline[1].toString(), csvline[2].toString());
                }else if(csvline.length == 2){
                    line = csvline[0].toString()+" "+csvline[1].toString();
                    BLANCE.put(csvline[0].toString(), csvline[1].toString());
                }
                tokenizer.wordInstance(line);
                typeList = tokenizer.getTypeList();
                begin = tokenizer.first();
                int i = 0;
                int cn = 0,cn1 = 0;
                String sql = "",sql1 = "";
                String w = "",w1 = ""; 
                while(tokenizer.hasNext()) {   
                    String str = "";
                    int count = 0,count1 = 0;
                    end = tokenizer.next();
                    type = ((Integer)typeList.elementAt(i++)).intValue();
                    if(type == 0){

                    }else if(type == 1){
                        str = line.substring(begin, end);
                        if(!listStopWords.contains(str) && !str.trim().equals("")){
                            for(int s = 0;s<Msyn.size();s++){
                                if(Msyn.get(s).getWord().trim().equals(str.trim())){
                                    str = Msyn.get(s).getMeans();
//                                        System.out.println("กระดาษที่ถูกเปลี่ยน "+csvline[0].toString()+""+csvline[1].toString()+" คือ "+str);
                                    break;
                                }  
                            }
                            if(cn == 0){
                                w += "ID,";
                            }else{
                                w += "w"+(cn - 1)+",";
                            }
                            sql += "'"+str+"'"+",";
                            keyWordSort.add(str);
                            if(!keyWord.contains(str)){
                                keyWord.add(str);
                            }
                            count++;
                        }
                        if(!str.trim().equals("")){
                          if(cn1 == 0){
                              w1 += "ID,";
                          }else{
                              w1 += "w"+(cn1 - 1)+",";
                          }
                          sql1 += "'"+str+"'"+",";
                          count1++;
                        }
                    }else if(type == 2){ 
                        str = line.substring(begin, end);
                        if(!listStopWords.contains(str) && !str.trim().equals("")){
                            for(int s=0;s<Msyn.size();s++){
                                if(Msyn.get(s).getWord().trim().equals(str.trim())){
                                    str = Msyn.get(s).getMeans();
//                                        System.out.println("กระดาษที่ถูกเปลี่ยน "+csvline[0].toString()+""+csvline[1].toString()+" คือ "+str);
                                    break;
                                }
                            }
                            if(cn == 0){
                              w += "ID,";
                            }else{
                              w += "w"+(cn-1)+",";
                            }
                            sql += "'"+str+"'"+",";
                            keyWordSort.add(str);
                            if(!keyWord.contains(str)){
                                keyWord.add(str);
                            }
                            count++;
                        }
                        if(!str.trim().equals("")){
                          if(cn1 == 0){
                              w1 += "ID,";
                          }else{
                              w1 += "w"+(cn1 - 1)+",";
                          }
                          sql1 += "'"+str+"'"+",";
                          count1++;
                        }
                    }else if(type == 3){
                        str = line.substring(begin, end);
                        if(!listStopWords.contains(str) && !str.trim().equals("")){
                            for(int s = 0;s < Msyn.size();s++){
                                if(Msyn.get(s).getWord().trim().equals(str.trim())){
                                    str = Msyn.get(s).getMeans();
//                                        System.out.println("กระดาษที่ถูกเปลี่ยน "+csvline[0].toString()+""+csvline[1].toString()+" คือ "+str);
                                    break;
                                }
                            }
                            if(cn == 0){
                                w += "ID,";
                            }else{
                                w += "w"+(cn - 1)+",";
                            }
                            sql += "'"+str+"'"+",";
                            keyWordSort.add(str);
                            count++;
                        }
                        if(!str.trim().equals("")){
                          if(cn1 == 0){
                              w1 += "ID,";
                          }else{
                              w1 += "w"+(cn1 - 1)+",";
                          }
                          sql1 += "'"+str+"'"+",";
                          count1++;
                        }
                    }else if(type == 4){
                        str = line.substring(begin, end);
                        if(!listStopWords.contains(str) && !str.trim().equals("")){
                            for(int s=0;s<Msyn.size();s++){
                                if(Msyn.get(s).getWord().trim().equals(str.trim())){
                                    str = Msyn.get(s).getMeans();
                                    //System.out.println("กระดาษที่ถูกเปลี่ยน "+csvline[0].toString()+""+csvline[1].toString()+" คือ "+str);
                                    break;
                                }
                            }
                            if(cn == 0){
                                w += "ID,";
                            }else{
                                w += "w"+(cn - 1)+",";
                            }
                            sql += "'"+str+"'"+",";
                            keyWordSort.add(str);
                            if(!keyWord.contains(str)){
                                keyWord.add(str);
                            }
                            count++;
                        }
                        if(!str.trim().equals("")){
                          if(cn1 == 0){
                              w1 += "ID,";
                          }else{
                              w1 += "w"+(cn1 - 1)+",";
                          }
                          sql1 += "'"+str+"'"+",";
                          count1++;
                        }
                    }
                    begin = end;
                    if(count != 0){
                        cn++;
                    } 
                    if(count1 != 0){
                        cn1++;
                    } 
                }
                try{
                    if(!sql.trim().equals("")){
                        String W = w.substring(0, w.length()-1); 
                        String tempStr = setKeyWordSort(keyWordSort);
                        String SQL = tempStr.substring(0, tempStr.length()-1);
                        String SQL2 = sql.substring(0, sql.length()-1);
                        
                        String W1 = w1.substring(0, w1.length()-1); 
                        String SQL1 = sql1.substring(0, sql1.length()-1);
                        
                        // W + SQL = words after stopword and synonym , sorting when gen model
                        // W1 + SQL1 = words after stopword and synonym when send ans
                        // W + SQL2 = words after stopword and synonym but non sorting
                        
                        if(is_model == 0){
                            new ConnectionDB().saveWordsToDB("INSERT INTO words("+W+") VALUES("+SQL+");"); 
                            new ConnectionDB().saveWordsToDB("INSERT INTO words1("+W1+") VALUES("+SQL1+");");
                        }
                        
                        if(is_model == 1){
                            new ConnectionDB().saveWordsToDB("INSERT INTO words3("+W+") VALUES("+SQL+");"); 
                            new ConnectionDB().saveWordsToDB("INSERT INTO words4("+W1+") VALUES("+SQL1+");");
                        }
                    }else{
//                        JOptionPane.showMessageDialog(null, "ไม่มีข้อมูล");
                    }
                }catch(SQLException ex){
                   throw new SQLException(ex);
                }
                catch(Exception e){
                   throw new Exception(e);
                }
            }
            cu++;
        } 
    }//while(true);} 
    KEYWORD = keyWord; 
    } //main  
    
    public String setKeyWordSort(ArrayList<String> keywort){
        String str = "";
        Collections.sort(keywort);
        for(int i=0; i<keywort.size(); i++)
            str += "'"+keywort.get(i)+"',";
        return str;
    }
}
