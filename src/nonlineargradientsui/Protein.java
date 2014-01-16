package nonlineargradientsui;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Set;
import java.util.LinkedHashSet;

/**
 * Class representing a protein 
 *
 * @author Luminita Moruz
 */
public class Protein {
    /**
     * Constructor 
     * @param name name of the protein
     * @param sequence amino acid sequence of the protein 
     */
    public Protein(String name, String sequence) {
        this.name = name;
        this.sequence = sequence;        
    }
    
    /**
     * Trypsin digestion of a protein sequence 
     * @param sequence sequence to be digested 
     * @param minLen the minimum length allowed for a peptide 
     * @param maxLen the maximum length allowed for a peptide
     * @return list of peptide sequences
     */
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
    
    /**
     * Load a fasta file 
     * @param fastaFile file containing the protein sequences 
     * @param fullName whether the full name given in the fasta should be considered 
     * or only the part until the first space 
     * @return list of proteins loaded from the fasta file 
     * @throws Exception 
     */
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
    
    /**
     * Digest all the protein in a fasta file 
     * @param fastaFile file including the protein sequences
     * @param fullName whether the full name given in the fasta should be considered 
     * or only the part until the first space 
     * @param minLen the minimum length allowed for a peptide 
     * @param maxLen the maximum length allowed for a peptide 
     * @return list of peptides (unique)
     * @throws Exception 
     */
    static List<String> digestProteinsFasta(File fastaFile, boolean fullName, 
            int minLen, int maxLen) throws Exception {
        List<Protein> proteins = Protein.loadFasta(fastaFile, fullName);        
        List<String> allPeptides = new ArrayList<String>();
        Set<String> set = new LinkedHashSet<String>();        
        
        for (Protein p: proteins) {
            allPeptides = Protein.digestProtein(p.sequence, minLen, maxLen);
            set.addAll(allPeptides);
        }
            
        List<String> uniqueList = new ArrayList<>(set);
        return uniqueList;
    }
    
    /**
     * Write a list of lines to a file 
     * @param lines list of lines 
     * @param filename file to which the lines should be written 
     * @throws Exception 
     */
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
   
    // name of the protein 
    private String name;
    // amino acid sequence of the protein 
    private String sequence;   
}
