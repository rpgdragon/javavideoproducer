/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmcastellano.eu.javavideoproducer.main;

/**
 *
 * @author rpgdragon
 */
public class Main {
    
    private static boolean isProduction = true;
    
    public static void main(String[] args){
        int tiempo = 3;
        if(args!=null && args.length > 0 && args[0].equals("false")){
            isProduction = false;
            if(args.length > 1){
                tiempo = Integer.parseInt(args[1]);
            }
        }
       ReproductorCanciones rc = new ReproductorCanciones(tiempo);
       rc.iniciar();
    }

    public static boolean isIsProduction() {
        return isProduction;
    }

    public static void setIsProduction(boolean isProduction) {
        Main.isProduction = isProduction;
    }
    
    
   
}
