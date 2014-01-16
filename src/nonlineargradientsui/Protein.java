/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nonlineargradientsui;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;
import java.util.LinkedHashSet;

public class Protein {
    public Protein(String name, String sequence) {
        this.name = name;
        this.sequence = sequence;        
    }
    
    static List<String> digestProtein(String sequence, int minLen, int maxLen) {
        List<String> peptides = new ArrayList<String>();
        String peptide;
        int i = 0, idx;
        
        while (i < sequence.length()) {
            idx = i; 
            while ( (idx < sequence.length()-1) && 
                    (((sequence.charAt(idx)=='K' || sequence.charAt(idx)=='R') && 
                    sequence.charAt(idx+1)=='P') ||
                    (sequence.charAt(idx) != 'K' && sequence.charAt(idx) != 'R')) ) {
                idx += 1;
            }
            peptide = sequence.substring(i, idx+1);
            if ((peptide.length() >= minLen) && (peptide.length() <= maxLen)) {
                peptides.add(peptide);
            }
            
            i = idx+1;
        }
        return peptides;
    }
    
    static List<Protein> loadFasta(File fastaFile, boolean fullName) throws Exception {
        List<Protein> proteins = new ArrayList<Protein>();
        BufferedReader br = new BufferedReader(new FileReader(fastaFile));
        String line, name, sequence;
        int i = 0;
        
        line = br.readLine();
        while (line != null) {
            if (line.startsWith(">")) {
                if (fullName) {
                   name = line.substring(1);
                } else {
                   name = line.substring(1).split(" ")[0]; 
                }
                sequence = "";
                while (((line = br.readLine()) != null) && (!line.startsWith(">"))) {
                    sequence += line.trim();
                }
                proteins.add(new Protein(name, sequence));
            }
            else {
                return proteins;
            }
        }   
            
        br.close();        
        return proteins;
    }
    
    static List<String> digestProteinsFasta(File fastaFile, boolean fullName, 
            int minLen, int maxLen) throws Exception {
        List<Protein> proteins = Protein.loadFasta(fastaFile, fullName);        
        List<String> allPeptides = new ArrayList<String>();
        Set<String> set = new LinkedHashSet<String>();        
        
        for (Protein p: proteins) {
            allPeptides = Protein.digestProtein(p.sequence, minLen, maxLen);
            set.addAll(allPeptides);
        }
            
        List<String> uniqueList = new ArrayList<String>(set);
        return uniqueList;
    }
    
    /* Write all the lines to a file filename */
    static void writeLinesToFile(List<String> lines, File filename) throws Exception {
        FileWriter fileHandler;
        
        fileHandler = new FileWriter(filename);
        for (String line:lines) {
            fileHandler.write(line + "\n");
        }
        fileHandler.close();    
    }
    
    @Override public String toString() {
        String result = "";
        
        result += name + "\n";
        result += sequence;
        return result;
    }
        
    private String name;
    private String sequence; 
    
    public static void main(String [] args) throws Exception
    {
        /*
        ArrayList<String> peptides = Protein.digestProteinsFasta(new File("/scratch/tmp/fasta.txt"), true, 
                0, 1000);
        for (String p:peptides) {
            System.out.println(p);
        }
        
        Collections.sort(peptides);
        
        String tmpDir = System.getProperty("java.io.tmpdir");
        String fname = "my-peptides.txt";
        File outFile = new File(tmpDir, fname);
        
        System.out.println(outFile.getAbsolutePath());
        Protein.writeLinesToTempFile(peptides, outFile);
       
        String protein = "KAAARRBBRRPCCKKPDDKEEEREEE";
        ArrayList<String> peptides = Protein.digestProtein(protein, 0, 1000);
        for (String p:peptides) {
            System.out.print(p + " ");
        }
        System.out.println("\n");*/
        List<Protein> l = Protein.loadFasta(new File("/scratch/tmp/fasta.txt"), false);
        for (Protein p:l) {
            System.out.println(p + "\n");
        }
        
    }
}
