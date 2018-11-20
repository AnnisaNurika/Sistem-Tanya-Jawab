/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package qaswithexpasion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author AllSeven64
 */
public class DocumentRetriever {
     //variabel ini untuk menampung seruluh term pada setiap dokumen ke dalam array
    private List <String[]> termsDocArrays = new ArrayList<>();
    private List <String> allTerms = new ArrayList<>();
    private List <double[]> tfidfDocsVector = new ArrayList<>();
    private List <String> validDocAnswer = new ArrayList<>();
    private String[] dok = new String[120];
    private String[] stopWord;    

    //methode untuk membaca file text
    public String readDokumenTeks(String bacateks) throws FileNotFoundException, IOException {
        File bacafile = new File(bacateks);
        FileReader inputDokumen = new FileReader(bacafile);
        BufferedReader bufferBaca = new BufferedReader(inputDokumen);
        StringBuilder content = new StringBuilder();
        String barisData;
        while ((barisData = bufferBaca.readLine()) != null) {
            content.append(barisData);
            content.append(System.getProperty("line.separator"));
        }
        return content.toString();
    }
    
     //baca kamus stopword.txt
    public void bacaKamusStopword() throws FileNotFoundException, IOException {
        String readKamusStopword;
        List <String> listKamusStopword = new ArrayList<>();
        readKamusStopword = readDokumenTeks("D:\\Data\\kamusStopword.txt");
        StringTokenizer strKamus = new StringTokenizer(readKamusStopword, ",");
        while (strKamus.hasMoreTokens()) {
            listKamusStopword.add(strKamus.nextToken());
        }
        stopWord = listKamusStopword.toArray(new String[0]);
    }
    
      /**
     * Method to read files and store in array.
     * @param filePath : source file path
     * @throws FileNotFoundException
     * @throws IOException
     */
     
    public void parseFile (String filepath, String querySearch) throws FileNotFoundException, IOException {
        File[] allFiles = new File(filepath).listFiles();
        int z = 1;
        List <String> hasilStemm = new ArrayList<>();
        bacaKamusStopword();
         
        String[] katakunci = querySearch.toString().toLowerCase().split(" ");
        for (String s : katakunci){
            if(!allTerms.contains(s)){
                allTerms.add(s);
            }
        }  
        termsDocArrays.add(katakunci);
        
        //membaca file dokumen txt
        for (File f : allFiles ){
            if (f.getName().endsWith(".txt")){
                BufferedReader in = new BufferedReader(new FileReader (f));
                StringBuilder sb = new StringBuilder(); 
                String s; 
                
                while ((s = in.readLine()) != null){  
                    sb.append(s); 
                    dok[z] = s; 
                }
                //System.out.println(sb.toString()); //untuk melihat hasilnya bahwa seluruh file dan isinya telah dimasukkan pada varibel sb
                String[] tokenizedTermsDoc = sb.toString().toLowerCase().replaceAll("-", " ").replaceAll("[\\W&&[^\\s]]","").split("\\W+");                
                //String[] tokenizedTermsDoc = sb.toString().toLowerCase().replaceAll("[\\W&&[^\\s]]","").split("\\W+");
                List <String> listTermDoc = new ArrayList<String>(Arrays.asList(tokenizedTermsDoc));
                listTermDoc.removeAll(Arrays.asList(stopWord));  //stopword pada TermDoc
                               
                for (String stemm : listTermDoc){
                    Stemming as = new Stemming(); //Stemm
                    stemm = as.KataDasar(stemm);
                    hasilStemm.add(stemm);
                       
                    if(!allTerms.contains(stemm)){ //menghindari duplikat entri
                        allTerms.add(stemm);
                    }
                }                
                String[] termDocAfterStopWord = hasilStemm.toArray(new String[0]);
                termsDocArrays.add(termDocAfterStopWord);
                hasilStemm.clear();    
            }
              z = z+1;       
        }
        
        // print list dokumen yang ada
        System.out.println("\n");          
        for (String[] listTermDoc : termsDocArrays){
            System.out.println("termDocArrays : " +Arrays.toString(listTermDoc));
        }
       
    }
    
      /**
     * Method to create termVector according to its tfidf score.
     */
    
    public void tfIdfCalculator() {
        double tf; //term frequency
        double idf; //inverse document frequency
        double tfidf; //term requency inverse document frequency        
        for (String[] docTermsArray : termsDocArrays) {
            double[] tfidfvectors = new double[allTerms.size()];
            int count = 0;
            for (String terms : allTerms) {
                tf = new TfIdf().tfCalculator(docTermsArray, terms);
                idf = new TfIdf().idfCalculator(termsDocArrays, terms);
                tfidf = tf * idf;
                tfidfvectors[count] = tfidf;
                count++;
            }
            tfidfDocsVector.add(tfidfvectors);  //storing document vectors;            
        }
        /*
        System.out.println("\n");
        for (double[] listTermDoc : tfidfDocsVector){
            System.out.println("tfidfDocsVector : " +Arrays.toString(listTermDoc));
        } 
        System.out.println("\n");
        */
    }
    
     /**
     * Method to calculate cosine similarity between all the documents.
    */
//     public void getCosineSimilarity() {
//        int i = 0;
//        for (int j = 1; j < tfidfDocsVector.size(); j++) {
//            System.out.println("between " + i + " and " + j + "  =  "
//            + new CosineSimilarity().getCosineSimilarity(tfidfDocsVector.get(i),tfidfDocsVector.get(j))
//            );    
//        }
//        System.out.println("\n");
//     }   
    
    
    /**
     * Method to calculate cosine similarity between all the documents.
    */
   public List<String> sortCosineSimilarity() {
        double[] aNilaiCos = new double[120] ;
        int[] mIsiDoc = new int[120];
        int b = 1;
        int k = 0;
        int i = 0;
        
        
        for (int j = 1; j < tfidfDocsVector.size(); j++) {
            aNilaiCos[k] = new CosineSimilarity().getCosineSimilarity(tfidfDocsVector.get(i),tfidfDocsVector.get(j));
            mIsiDoc[k] = b;
            k++;
            b++;
        }
        
        //sorting
        double temp = 0;
        int t = 0;       
        for(i = 0; i < tfidfDocsVector.size(); i ++)
        {
            for(int j=1; j<tfidfDocsVector.size(); j++)
            {
                if(aNilaiCos[j-1] < aNilaiCos[j])
                {
                    temp = aNilaiCos[j-1];
                    t = mIsiDoc[j-1];
                    aNilaiCos[j-1] = aNilaiCos[j];
                    mIsiDoc[j-1] = mIsiDoc[j];
                    aNilaiCos[j] = temp;
                    mIsiDoc[j] = t;
                }
            }
        }
        
        //sorting nilai cosine seluruh dokumen yang aja
        int c = 0;
        for(i = 0; i < tfidfDocsVector.size()-1; i++)
        {
            c = mIsiDoc[i];
            //System.out.print("Cosinus : " +aNilaiCos[i]);
            //System.out.println(" Dokumen : " +dok[c]);
            c++;
        }
        
        //minimal 5 dokumen teratas yang akan dijadikan dokumen penjawab
        for(i = 0; i < 5; i ++)
        {
            c = mIsiDoc[i];
            //System.out.print("Cosinus : "+a[i]);
            //System.out.println(" Dokumen : "+dok[c]);
            validDocAnswer.add(dok[c]);
            c++;
        }
                
        return validDocAnswer;
    }  
}
