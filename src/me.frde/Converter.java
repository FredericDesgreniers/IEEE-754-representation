/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.frde;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author frede
 */
public class Converter {
    static PrintWriter writer;
    static boolean negative;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("Enter valid number: ");
            double num = scanner.nextDouble();
            System.out.println("Printing result to steps.txt");
            
            writer = new PrintWriter("steps.txt","UTF-8");
            if(num<0){
                negative = true;
                num*=-1;
                writer.write("Using positive for now: "+num+"\n");
            }
            convert(num);
            writer.flush();
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(ToBit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void convert(double num){
        int part = (int)num;
        writer.write("Before .\n");
        List<Byte> wholeBits = getBitsFromPart(part);
        System.out.print(".");
        double fract = num-part;
        writer.write("After .\n");
        List<Byte> fractBits = getBitsFromFraction(fract);
        
        getIEEERepresentation(wholeBits, fractBits);
        
        
    }
    public static void getIEEERepresentation(List<Byte> wholes, List<Byte> fracts){
        writer.write("We get: "+getBitListAsString(wholes)+"."+getBitListAsString(fracts)+"\n");
        
        int shift = 0;
        writer.write("shifting all bits to right of . \n");
        while(wholes.size()>0){
            shift++;
            fracts.add(0,wholes.get(wholes.size()-1));
            wholes.remove(wholes.size()-1);
        }
        writer.write("."+getBitListAsString(fracts)+"\n");
        writer.write("Getting 1.xxx representation ");
        
        while(fracts.get(0)!=1){
            shift--;
            wholes.remove(0);
        }
        shift--;
        fracts.remove(0);
        List<Byte> excess  = new ArrayList();
        while(fracts.size()>23){
            excess.add(0,fracts.remove(fracts.size()-1));
        }
        writer.write("and removing excess bits on the right: "+getBitListAsString(excess)+"\n");
        
        int bias = 127+shift;
        writer.write("1."+getBitListAsString(fracts)+"\n The bits were shifted at a position of 2^"+shift+". The bias is 127+shift = "+bias+"\n Or in binary: \n");
        List<Byte> binaryBias = getBitsFromPart(bias);
         writer.write(getBitListAsString(binaryBias)+"\n");
        
        writer.write("Representation for  IEEE-754 single precision: \n");
        
        writer.write((negative?"1":"0")+" "+getBitListAsString(binaryBias)+" "+getBitListAsString(fracts));
    }
    
    public static List<Byte> getBitsFromPart(int part){
        
        List<Byte> bits = new ArrayList();
        
        while(part>0)
        {
            byte remainder = (byte) (part % 2);
            bits.add(0,remainder);
            int tempPart = part / 2;
            writer.write(part+" / 2 = "+tempPart+"\tr "+remainder+"\n");
            
            part = tempPart;
            
        }
        return bits;
    }
    public static List<Byte> getBitsFromFraction(double fraction){
        
        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.CEILING);
        
        List<Byte> bits = new ArrayList();
        
        while(fraction!=0 && bits.size()<23){
            double tempFraction = (fraction * 2);
            writer.write(df.format(fraction)+" * 2 = \t"+df.format(tempFraction)+"\n");
            fraction = tempFraction;
            
            if(fraction>=1){
                fraction-=1;
                bits.add((byte)1);
            }else{
                bits.add((byte)0);
            }
        }
        
        return bits;
    }

    public static String getBitListAsString(List<Byte> bBytes){
        String s = "";
        for(Byte b:bBytes)
            s+=b;
        return s;
    }
}

