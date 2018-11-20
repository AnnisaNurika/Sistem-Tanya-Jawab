/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package qaswithexpasion;

import java.util.List;

/**
 *
 * @author AllSeven64
 */
public class TfIdf {
    /**
     * Calculated the tf of term termToCheck
     * @param totalterms : Array of all the words under processing document
     * @param termToCheck : term of which tf is to be calculated.
     * @return tf(term frequency) of term termToCheck
     */
    public double tfCalculator(String[] totalterms, String termToCheck) {
        double count = 0;  //to count the overall occurrence of the term termToCheck
        for (String s : totalterms) {
            if (s.equalsIgnoreCase(termToCheck)) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Calculated idf of term termToCheck
     * @param listDoc : Array of all the words under processing document
     * @param termToCheck = allTerm
     * @return idf(inverse document frequency) score
     */
    public double idfCalculator(List<String[]> listDoc, String termToCheck) {
        double count = 0;
        for (String[] ss : listDoc) {
            for (String s : ss) {
                if (s.equalsIgnoreCase(termToCheck)) {
                    count++;
                    break;
                }
            }
        }
        //System.out.println("df "+count);
        //System.out.println("idf "+(Math.log(listDoc.size()/count)));
        return Math.log10(listDoc.size()/count);
    }
}
