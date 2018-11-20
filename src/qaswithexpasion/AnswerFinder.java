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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author AllSeven64
 */
public class AnswerFinder {
    List <String> ambilTag = new ArrayList<>();
    List <Integer> jumlahKata = new ArrayList<>();
    List <String> paragrafNER = new ArrayList<>();
    List <String[]> kalimat = new ArrayList<>();  
    List<Integer> keyOccurence = new ArrayList<>();
    String jawaban;
    String ayat;
       
    public String getAnswerFromDoc(String EAT, String expandTerm, List <String> docAnswer) throws IOException{
        //kaca kunci diubah ke bentuk string pattern agar mudah diproses regrex () dalam mencocokan kata
        String keyReg = "("+expandTerm.replaceAll(" ", "|")+")";
        String[] tokenKataKunci = expandTerm.split(" ");
        
        //ubah paragraf jadi kalimat
        for(String s: docAnswer){
             String[] ss = s.toLowerCase().split("\\. ");
             kalimat.add(ss);
        }
                
        //kasih tag setiap kalimat
        for (String[] ss : kalimat){
            String fixedInput;
            for (String s : ss){
                String regexLoc = "(maqam|baitullah|akhirat|shafaa|marwa|arafah|mesjid|masy'arilharam|surga|neraka)";
                String regexName = "(kamu|ibrahim|ismail|orang-orang|saudara|muslim|malaikat|syaitan|seorang|ibrahim|manusia|orang|allah|laki-laki|perempuan|yaitu mereka|yaitu orang)";
                String regexTime = "(hari-hari|selama|hari|waktu|apabila|sampai|sedang|ramadhan|malam|bulan sabit|musim|telah menyelesaikan|keadaan)";

                fixedInput = s.toLowerCase().replaceAll(regexName, "<person>$1</person>").replaceAll(regexTime, "<time>$1</time>").replaceAll(regexLoc, "<location>$1</location>");
                paragrafNER.add(fixedInput);
            }
        }
        
        ////ambil tagging yang sesuai dengan type pertanyaan 
        for (String ss : paragrafNER){
            if (ss.contains(EAT)){
                ambilTag.add(ss);
            }
        }
        
        //Hitung jumlah kemunculan kata kunci yang ada pada kalimat--------------->Langkah 4     
        int y=1;
        int total = 0;
        for (String kalimats: ambilTag){  
            for (String key : tokenKataKunci){
                if(kalimats.contains(key)){
                    total = total+1;
                }                
            }
            keyOccurence.add(total);
            total = 0;
            y++;     
        }       
     
       //hitung jumlah keseluruhan katakunci yang terdapat tiap paragraf ---------------> Langkah 5       
        for(String tag : ambilTag){
            int jumlah = hitungKata(tag, keyReg);
            jumlahKata.add(jumlah);
        }   
        
        jawaban = sortAnswer(jumlahKata, ambilTag, keyOccurence); 
        ayat = getNameDoc("D:\\Data\\ayatValid",jawaban);
        return (ayat+"\n\n"+jawaban.toUpperCase().charAt(0)+jawaban.substring(1,jawaban.length()).replaceAll("allah", "Allah").replaceAll("kami", "Kami").replaceAll("aku", "Aku")+".");        
    }
    
    
    /**
     * hitung banyaknya kata kunci/expandTerm dalam kalimat jawaban
     * @param y
     * @param b
     * @return 
     */
       
    public int hitungKata(String y , String b){
        int i = 0;
        Pattern p = Pattern.compile(b.toString());
        Matcher m = p.matcher(y);
        while (m.find()) {
            i++;
        }
        return i;
    }
    
    /**
     * Urutkan jawaban sesuai dengan jumlah katakunci/expandTerm terbanyak. urutan paling atas diindikasikan jawaban
     * @return 
     */
  
    public String sortAnswer(List<Integer> jumlahKata, List<String> ambilTag, List<Integer> keyOccurence){
        int[] banyakKata = new int[jumlahKata.size()] ;
        int[] mIsiDoc = new int[jumlahKata.size()];    
        int[] jumlahKey = new int[jumlahKata.size()];
        int g = 0;
        int k = 0;
        int i = 0;
        String hasilAkhir ="";    
        
        
        for (int d = 0; d < jumlahKata.size(); d++) {
            banyakKata[k] = jumlahKata.get(d);
            mIsiDoc[k] = g;
            jumlahKey[k] = keyOccurence.get(d);
            k++;
            g++;
        }   
           
        int temp = 0;
        int t = 0;  
        int te = 0;   
        
        for(i = 0; i < jumlahKata.size()-1; i ++)
        {
            for(int p=1; p<jumlahKata.size(); p++)
            {
                if(jumlahKey[p-1] < jumlahKey[p] || (jumlahKey[p-1] == jumlahKey[p] && banyakKata[p-1] < banyakKata[p]))
                {
                    temp = banyakKata[p-1];
                    t = mIsiDoc[p-1];
                    te = jumlahKey[p-1];
                    
                    banyakKata[p-1] = banyakKata[p];
                    mIsiDoc[p-1] = mIsiDoc[p];
                    jumlahKey[p-1] = jumlahKey[p];
                    
                    banyakKata[p] = temp;
                    mIsiDoc[p] = t;
                    jumlahKey[p] = te;
                }
            }
        }
               
       //sorting nilai katakuci terbanyak seluruh paragraf yang ada
        int c = 0;
        for(i = 0; i < jumlahKata.size(); i++)
        {
            c = mIsiDoc[i];
            System.out.print("Key yang ada : "+jumlahKey[i]);
            System.out.println(" == Banyak kata : " +banyakKata[i]);
            System.out.println("Dokumen : " +ambilTag.get(c));
            c++;
        }
       
        System.out.println("\nHasil Jawaban"); 
        try {
            c = mIsiDoc[0];
            int p = 0;

            if (!(jumlahKey[p]==0 && banyakKata[p]==0)){
                String hasil = ambilTag.get(c);
                System.out.println(hasil);

                hasilAkhir = hasil.replaceAll("(<person>|</person>|<location>|</location>|<time>|</time>)", "");
                System.out.println(hasilAkhir+".");
            }
            else{
                String hasil = ambilTag.get(c);
                System.out.println(hasil);
                hasilAkhir = "Jawaban tidak ditemukan!";
                System.out.println(hasilAkhir);

            }        
        }catch (ArrayIndexOutOfBoundsException e){
            hasilAkhir = "Jawaban tidak ditemukan!";
        }
           
        return hasilAkhir;
    }
    
    //get nama document
    public String getNameDoc(String filepath, String jawaban) throws FileNotFoundException, IOException{
        File[] allFiles = new File(filepath).listFiles();
        int z = 1;
        String[] dok = new String[120];
        String namaFile = "";
        String isiAyat = "";
        String ayat = "";
   
        //membaca file dokumen txt
        for (File f : allFiles ){
            if (f.getName().endsWith(".txt")){
                BufferedReader in = new BufferedReader(new FileReader (f));
                StringBuilder sb = new StringBuilder(); 
                String s; 
                
                while ((s = in.readLine()) != null){  
                    sb.append(s); 
                    dok[z] = s;
                    
                    if (dok[z].toLowerCase().contains(jawaban)){
                       namaFile = f.getName().replaceAll(".txt", "");
                       isiAyat = dok[z];
                    }
                }                              
            }
              z = z+1;       
        }      
      
        //System.out.println(dok.length);
        //System.out.println(namaFile);
        if(namaFile.startsWith("107")){
            ayat = "Surah Al-Maun[107] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("108")){
            ayat = "Surah Al-Kausar[108] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("10")){
            ayat = "Surah Yunus[10] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("11")){
            ayat = "Surah Hud[11] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("13")){
            ayat = "Surah Ar-Ra'd[13] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("14")){
            ayat = "Surah Ibrahim[14] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("17")){
            ayat = "Surah Al-Isra[17] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("19")){
            ayat = "Surah Maryam[19] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("20")){
            ayat = "Surah Taha[20] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("21")){
            ayat = "Surah Al-Anbiya'[21] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("22")){
            ayat = "Surah Al-Hajj[22] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("23")){
            ayat = "Surah Al-Mu'minun[23] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("24")){
            ayat = "Surah An-Nur[24] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("26")){
            ayat = "Surah Asy-Syu'ara[26] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("27")){
            ayat = "Surah An-Naml[27] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("29")){
            ayat = "Surah Al-Ankabut[29] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("30")){
            ayat = "Surah Ar-Rum[30] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("31")){
            ayat = "Surah Luqman[31] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("32")){
            ayat = "Surah Sajdah[32] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("33")){
            ayat = "Surah Al-Ahzab[33] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("35")){
            ayat = "Surah Fatir[35] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("39")){
            ayat = "Surah Az-Zumar[39] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("41")){
            ayat = "Surah Fussilat[41] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("42")){
            ayat = "Surah Asy-Syura[42] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("48")){
            ayat = "Surah Al-Fath[48] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("50")){
            ayat = "Surah Qaf[50] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("58")){
            ayat = "Surah Al-Mujadalah[58] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("62")){
            ayat = "Surah Al-Jumu'ah[62] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("70")){
            ayat = "Surah Al-Ma'arij[70] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("73")){
            ayat = "Surah Al-Muzzammil[73] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("74")){
            ayat = "Surah Al-Muddasir[74] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("75")){
            ayat = "Surah Al-Qiyamah[75] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("76")){
            ayat = "Surah Al-Insan[76] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("77")){
            ayat = "Surah Al-Mursalat[77] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("87")){
            ayat = "Surah Al-A'la[87] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("96")){
            ayat = "Surah Al-'Alaq[96] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("98")){
            ayat = "Surah Al-Bayyinah[98] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if (namaFile.startsWith("2")){
            ayat = "Surah Al-Baqarah[2] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("3")){
            ayat = "Surah Ali-Imran[3] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("4")){
            ayat = "Surah An-Nisa[4] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("5")){
            ayat = "Surah Al-Maidah[5] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("6")){
            ayat = "Surah Al-An'am[6] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("7")){
            ayat = "Surah Al-Araf[7] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("8")){
            ayat = "Surah Al-Anfal[8] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        else if(namaFile.startsWith("9")){
            ayat = "Surah At-Taubah[9] : "+namaFile.substring(namaFile.lastIndexOf("_") + 1);
            return ayat;
        }
        return ayat;
   }
}
