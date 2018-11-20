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
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * @author AllSeven64
 */
public class Stemming {
    private String kata;
    private String akarKata;
    private ArrayList<String> listKamus;
    private String readDokumen;
    public boolean cekPrefiksSuffiks;
    private String bersikan = null;
    
    public Stemming() throws FileNotFoundException, IOException {
        listKamus = new ArrayList<String>();
        bacaKamus();
    }
     
    public String readDokumenTeks(String bacateks) throws FileNotFoundException, IOException {
        File bacafile = new File(bacateks);
        FileReader inputDokumen = new FileReader(bacafile);
        BufferedReader bufferBaca = new BufferedReader(inputDokumen);
        StringBuffer content = new StringBuffer();
        String barisData = "";
        while ((barisData = bufferBaca.readLine()) != null) {
            content.append(barisData);
            content.append(System.getProperty("line.separator"));
        }
        return content.toString();
    }
    
    /**
     * Membaca isi kamus dan memasukkan kata-kata di dalam kamus ke dalam sebuah list
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public void bacaKamus() throws FileNotFoundException, IOException {
        readDokumen = readDokumenTeks("D:\\Data\\kamus.txt");
        StringTokenizer strKamus = new StringTokenizer(readDokumen, "|");
        while (strKamus.hasMoreTokens()) {
            listKamus.add(strKamus.nextToken());
        }
    }
    
    /**
     * Kata diinputkan
     * @param kata 
     */
    public void setKata(String kata) {
        this.kata = kata;
        this.akarKata = kata;
        bersikan = "";
    }
    
    /**
     * Kata yang diinputkan dicek, apakah sesuai di dalam kamus, jika kata yang diinputkan
     * sesuai dengan yang ada pada kamus maka kata tersebut merupakan akar kata (root word),
     * @param kata
     * @return 
     */
    public boolean cekKamus(String kata) {
        if (listKamus.contains(kata)) {
            return true;
        } else {
            return false;
        }
    }
    
    
     /**
     * Untuk mrndapatkan kata dasar setlah mengalami proses stemming dengan menggunakan 
     * algoritma Nazief-Andriani
     * @param kata
     * @return 
     */
    public String KataDasar(String kata) throws FileNotFoundException {
        setKata(kata);
        if (cekKamus(kata)) {
            return akarKata;
        } else {
           hapusInfleksionalSuffiks();
           hapusDerivationPrefiks();
        }
        
        //alternatif kata yang tidak bisa distemming
        akarKata = cekKata(kata);
        
        return akarKata;
    }
    
     /**
     * proses pada Infleksional Suffixes merupakan proses menghapus partikel suffiks
     * -lah,-kah,-nya,-tah,-pun,-ku,-mu
     */
    public void hapusInfleksionalSuffiks() {
        if (kata.endsWith("lah") || kata.endsWith("kah") || kata.endsWith("nya") || kata.endsWith("pun") ||kata.endsWith("ku") || kata.endsWith("mu")|| kata.endsWith("tah")) {
            kata = kata.replaceAll("(kah|lah|pun|tah|ku|mu|nya)$", "");
            //System.out.println("kah|lah|pun|tah|ku|mu|nya dihapus "+kata);   
            
            if (kata.endsWith("ku") || kata.endsWith("mu")|| kata.endsWith("nya")){
                kata = kata.replaceAll("(ku|mu|nya)$", "");
                //System.out.println("ku|mu|nya dihapus "+kata);
            }              
        } 
    }
       
    public void hapusDerivationPrefiks() {
        if(cekKamus(kata)){
            akarKata = kata;    
        }
        else if ((kata.endsWith("i") || kata.endsWith("an") ||kata.endsWith("kan")) 
                && !((kata.startsWith("di") || kata.startsWith("ke") || kata.startsWith("se") || kata.startsWith("pe") || kata.startsWith("me") ||kata.startsWith("be")||kata.startsWith("te")))){
                String hasil = null;
                hasil = kata.replaceAll("(i|an)$", ""); 
                //System.out.println("oi akhiran i, an dihapus "+ hasil);
                
                if (cekKamus(hasil)){
                    akarKata = hasil;
                }
                else{
                    if (hasil.endsWith("k")){
                        akarKata = hasil.replaceAll("(k)$", "");
                    }else{
                        akarKata = kata;
                    }            
                }
        }
        //tipe awalan ke-1 : -di,-ke,-se
        else if ((kata.startsWith("di") || kata.startsWith("ke") || kata.startsWith("se")) && kata.length() > 2) {
            String kata2 = null;
            kata2 = kata.substring(2, kata.length());
            
            //System.out.println("barusan masuk if, diindikasikan ada awalan -di, -ke, dan -se");
            if(cekKamus(kata2)){
                akarKata = kata2;
            }else{
                bersikan = kata2;
                hapusDerivationSuffiks();
            }              
        } 
        //tipe awalan ke-2 : -te,-me,-be,-pe
        else if ((kata.startsWith("te") || kata.startsWith("me") || kata.startsWith("be") || kata.startsWith("pe"))) {
            //jika tipe prefiks bukan none digunakan rabel 2.
            String kata2 = kata.substring(2, kata.length());
            String hasil = null;
      
            //System.out.println("me-, pe-, te-, be-, dipotong "+kata2);
            if(cekKamus(kata2)){
                akarKata = kata2;
            }
            else if (kata2.length() > 4) {
                //n{c|d|j|z|s} 
                if(((kata2.charAt(0) == 'n')&&(kata2.charAt(1) == 'c')) || ((kata2.charAt(0) == 'n')&&(kata2.charAt(1) == 'j')) || ((kata2.charAt(0) == 'n')&&(kata2.charAt(1) == 'd')) || ((kata2.charAt(0) == 'n')&&(kata2.charAt(1) == 'z')) || ((kata2.charAt(0) == 'n')&&(kata2.charAt(1) == 's'))){   
                    hasil = kata2.substring(1);
                }//rCAP -> C!=r, P!=er 
                else if ((kata2.charAt(0) == 'r') && !(kata2.charAt(1) == 'r') && !(kata2.charAt(3) == 'e') && !(kata2.charAt(4) == 'r')) {
                    hasil = kata2.substring(1);
                }//nV
                else if ((kata2.charAt(0) == 'n') && (vowel(kata2.charAt(1)))) {
                    hasil = 't'+kata2.substring(1);
                }//nyV 
                else if ((kata2.charAt(0) == 'n') && (kata2.charAt(1) == 'y')){
                    hasil = 's'+kata2.substring(2);
                }//ngV    
                else if ((kata2.charAt(0) == 'n') && (kata2.charAt(1) == 'g') && (vowel(kata2.charAt(2)))){
                    hasil = kata2.substring(2);               
                }//ng{g|h|q|k}
                else if (kata2.startsWith("ng") && ((kata2.charAt(2) == 'g') || (kata2.charAt(2) == 'h') || (kata2.charAt(2) == 'q') || (kata2.charAt(1) == 'k') )){
                    hasil = kata2.substring(2);               
                }
                else if (kata.startsWith("pem")&& (vowel(kata2.charAt(1)))){
                    hasil = 'p'+kata2.substring(1);
                } 
                else if ((kata2.charAt(0) == 'm')&& !(vowel(kata2.charAt(1)))){
                    hasil = kata2.substring(1);
                }
                else if ((kata2.charAt(0) == 'm')&& (vowel(kata2.charAt(1)))){
                    hasil = kata2;
                }//me-{l|r|w|y}V 
                else if (kata.startsWith("me") && (vowel(kata2.charAt(1))) && (kata2.charAt(0) == 'l') || (kata2.charAt(0) == 'r') || (kata2.charAt(0) == 'w') || (kata2.charAt(0) == 'y')){
                    hasil = kata2.substring(0);
                }//m{b|f|v}
                else if ((kata.startsWith("me")||kata.startsWith("pe")) && (kata2.charAt(0) == 'm') && ((kata2.charAt(1) == 'b') || (kata2.charAt(1)== 'f') || (kata2.charAt(1)== 'v'))){
                    hasil = kata2.substring(1);
                }//mpe
                else if (kata.startsWith("me") && kata2.startsWith("mpe")){
                    hasil = kata2.substring(1);
                }//memrV
                else if ((kata.startsWith("me") || (kata.startsWith("pe"))) && (kata2.charAt(0) == 'm') && (kata2.charAt(1) == 'r') && (vowel(kata2.charAt(1)))){
                    hasil = kata2.substring(0);
                }//mempA !A=e
                else if (kata2.startsWith("mp") && !(kata2.charAt(2) == 'e')){
                    hasil = kata2.substring(1);
                }//pe{w|y}
                else if (kata.startsWith("pe") && (vowel(kata2.charAt(1))) && ((kata2.charAt(0) == 'w') || (kata2.charAt(0) == 'y'))){
                    hasil = kata2.substring(0);
                }//rCAerV -> C!=r
                else if ((kata2.charAt(0) == 'r') && !(kata2.charAt(1) == 'r') && (kata2.charAt(3) == 'e') && (kata2.charAt(4) == 'r') && (vowel(kata2.charAt(5)))) {
                    hasil = kata2.substring(1);
                }//lV
                else if (kata.startsWith("pe") && (kata2.charAt(0) == 'l') && (vowel(kata2.charAt(1)))){
                    if (kata.startsWith("pelajar")){
                        hasil = "ajar";
                    }else{
                        hasil = kata2.substring(0);
                    }
                }//peCP
                else if (kata.startsWith("pe") && !((kata2.charAt(0) == 'r') || (kata2.charAt(0) == 'w') || (kata2.charAt(0) == 'y') 
                        || (kata2.charAt(0) == 'm') || (kata2.charAt(0) == 'n')) && !(kata2.charAt(1) == 'e') && !(kata2.charAt(2) == 'r') ){
                    hasil = kata2.substring(0);
                }//peCerC
                else if (kata.startsWith("pe") && !((kata2.charAt(0) == 'r') || (kata2.charAt(0) == 'w') || (kata2.charAt(0) == 'y') 
                        || (kata2.charAt(0) == 'm') || (kata2.charAt(0) == 'n')) && (kata2.charAt(1) == 'e') && (kata2.charAt(2) == 'r') && !(vowel(kata2.charAt(3)))){
                    hasil = kata2.substring(0);
                }//belajar
                else if (kata.startsWith("belajar")){
                    hasil = "ajar";
                }//teCerC
                else if (kata.startsWith("te") && !((kata2.charAt(0) == 'r') || (kata2.charAt(0) == 'l')) && (kata2.charAt(1) == 'e') && (kata2.charAt(2) == 'r') && !(vowel(kata2.charAt(3)))){
                    hasil = kata2.substring(0);
                }//terCerV
                else if (kata.startsWith("te") && (kata2.charAt(0) == 'r') && !(kata2.charAt(1) == 'r') && (kata2.charAt(2) == 'e') && (kata2.charAt(3) == 'r') && (vowel(kata2.charAt(3)))){
                    hasil = kata2.substring(1);
                }//terCP
                else if (kata.startsWith("te") && (kata2.charAt(0) == 'r') && !(kata2.charAt(1) == 'r') && !(kata2.charAt(2) == 'e') && !(kata2.charAt(3) == 'r')){
                    hasil = kata2.substring(1);
                }//teCerC
                else if (kata.startsWith("te") && !(kata2.charAt(0) == 'r') && (kata2.charAt(1) == 'e') && (kata2.charAt(2) == 'r') && !(vowel(kata2.charAt(3)))){
                    hasil = kata2.substring(0);
                }
                else if (kata.startsWith("me") && (kata2.charAt(0) == 'm') && (kata2.charAt(1) == 'p') && !(kata2.charAt(2) == 'e')){
                    hasil = kata2.substring(1);
                }
                else if (kata.startsWith("pe") && (kata2.charAt(0) == 'n') && (kata2.charAt(1) == 'g')&& !(vowel(kata2.charAt(2)))){
                    hasil = kata2.substring(2);
                }
                else if (kata.startsWith("be") && (kata2.charAt(0) == 'r') && (vowel(kata2.charAt(1)))) {
                    hasil = kata2.substring(1);
                 //contoh: terevolusi-->evolusi
                }
                else{
                    hasil = kata2;
                } 
              
                if(cekKamus(hasil)){
                    akarKata = hasil;
                }
                else{
                    bersikan = hasil;
                    hapusDerivationSuffiks(); 
                }                                  
            }                
        }
    }
    
     /**
     * hapus derivation suffixes -i,-an,-kan, jika kata ditemukan didalam kamus maka algoritma berhenti
     * 
     */
    public void hapusDerivationSuffiks(){
        String hasil1 = null;
        if ((bersikan.endsWith("i")) || (bersikan.endsWith("kan")) ||(bersikan.endsWith("an"))) {
                hasil1 = bersikan.replaceAll("(i|an)$", ""); 
                
                if (cekKamus(hasil1)){
                    akarKata = hasil1;
                }
                else{
                    if (hasil1.endsWith("k")){
                        akarKata = hasil1.replaceAll("(k)$", "");
                    }else{
                        akarKata = kata;
                    }            
                }
            } 
    }

    /**
     * seleksi vokal a,i,u,e,o
     */
    private boolean vowel(char huruf) {
        if (huruf == 'a' || huruf == 'i' || huruf == 'u' || huruf == 'e' || huruf == 'o') {
            return true;
        }
        return false;
    }  
    
     /**
     * Alternatif untuk kata yang tidak dapat distemming
     * @param kata
     * @return 
     */
    public String cekKata(String kata) throws FileNotFoundException{
        //baca data stemming
        Scanner sc = new Scanner(new File("D:\\Data\\dataStemming.txt"));
        List<String> lines = new ArrayList<String>();
        List<String[]> bacaStemming = new ArrayList<>();
        while (sc.hasNextLine()) {
            lines.add(sc.nextLine());
        }
  
        String[] arr = lines.toArray(new String[0]);
        //System.out.println("get Stemming : "+Arrays.toString(arr));
        
        for(String ss : arr){
            bacaStemming.add(ss.split(" "));
        }
        
        //get hasil stem
        String stem[] = kata.split(" ");
        for(String w : stem){
            for(String[] ss : bacaStemming){
                String kata1 = ss[0];
                String kata2 = ss[1];
                
                if (w.equalsIgnoreCase(kata1)){
                    akarKata = kata2;
                }
            }
        }
               
        return akarKata;
    }  
}
