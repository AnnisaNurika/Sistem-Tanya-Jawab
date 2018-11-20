/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package qaswithexpasion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author AllSeven64
 */
public class QuestionAnalyzerNotQE {
    String[] questionWords = new String [] {"siapa", "siapakah", "dimana", "dimanakah", "kapan", "kapankah"};
    String kueri = null; 
    String kata;
    String EAT;
    String katakunci;
    String jawaban;
    List <String> docAnswer = new ArrayList<>();
    
       
   public void getQuery(String kuery) throws IOException{    
       kueri = kuery.toLowerCase().replaceAll("[+.^:,?!]", ""); 
       EAT = setQuestionEAT(kueri);
       kata = setKeywords(kueri).replaceAll("(maksud|dengan|namun|atau|bagi|kami|dia|kepada|untuk|ke|itu|pada|dari|yang|dan|sebagai|ketika|itu|kita)"+" ", "");
       katakunci = getStem(kata);
               
       System.out.println("kueri : " +kueri);
       System.out.println("EAT : "+EAT);
       System.out.println("kata : "+ kata);
       System.out.println("kuery stem : " +katakunci);
       
      
       //pencarian dokumen
       DocumentRetriever dr = new DocumentRetriever();
       dr.parseFile("D:\\Data\\ayatValid", katakunci);
       dr.tfIdfCalculator(); 
       docAnswer = dr.sortCosineSimilarity();
       
       System.out.println("\n");
       for (String s : docAnswer) {
           System.out.println("Valid Doc Answer: "+s);
       }  
       
       //pencarian jawaban
       AnswerFinder af = new AnswerFinder();
       jawaban = af.getAnswerFromDoc(EAT, katakunci, docAnswer);
              
       System.out.println("\nJawaban :\n"+jawaban);  
           
   } 
     
   public String setQuestionEAT(String query){
       String questionEAT = null;
       String[] words;
       
       //keyword of factoid question
       String[] keywordOfPerson = new String[]{"adalah", "ialah","yaitu", "nama","bernama"};
       String[] keywordOfLocation = new String[]{"di", "ke", "dari", "tempat"};
       String[] keywordOfTime = new String[]{"pada", "hari", "bulan", "tahun", "lama"};
       
       words = query.toLowerCase().split(" ");
       
       //looping untuk pencarian kata tanya didalam query
       for (String word : words){
           //jika list didalam questionWord terdapat kata word maka...
           if(Arrays.asList(questionWords).contains(word)){
               if(word.equals("siapa") || word.equals("siapakah")){
                   questionEAT = "person";
               }
               else if (word.equals("dimana") || word.equals ("dimanakah")){
                   questionEAT = "location";
               }
               else if (word.equals("kapan") || word.equals("kapankah")){
                   questionEAT = "time";
               }
               else if (Arrays.asList(keywordOfLocation).contains(word)){
                   questionEAT = "location";
               }
               else if (Arrays.asList(keywordOfPerson).contains(word)){
                   questionEAT = "person";
               }
               else if (Arrays.asList(keywordOfTime).contains(word)){
                   questionEAT = "time";
               }   
           }
       }     
       return questionEAT;
   }
    
   public String setKeywords (String query) {
       String katakunci = null;
       String[] words = query.split(" ");
       
       for(String word: words){
           if(Arrays.asList(questionWords).contains(word)){
               //hapus kata tanya
               katakunci = query.replaceAll(word+" ", "");
               return katakunci;
           }
       }
      return katakunci;
   }
   
   public String getStem (String query) throws IOException{       
       List <String> hasilStemm = new ArrayList<>();
       String queryStem;
       String[] termQueryAfterStemming;
       String[] words = query.toString().toLowerCase().split(" ");
       
       for (String stemm : words){
            Stemming as = new Stemming(); //Stemm
            stemm = as.KataDasar(stemm);
            hasilStemm.add(stemm);  
        }       
       termQueryAfterStemming = hasilStemm.toArray(new String[0]);  
       
       queryStem = String.join(",", termQueryAfterStemming).replaceAll(",", " "); 
       return queryStem;
   }
   
    public String getAnswer(){
        return jawaban;
    }
    
    public String getEAT(){
        return EAT;
    }
        
    public String getKeyword(){
        return katakunci;
    }
}
