/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmcastellano.eu.javavideoproducer.main;

import jmcastellano.eu.javavideoproducer.utils.Logger;

/**
 *
 * @author rpgdragon
 */
public class Main {
    
    private static boolean isProduction = true;
    
    public static void main(String[] args){
        if(args!=null && args.length > 0 && args[0].equals("false")){
            isProduction = false;
        }
       Logger.getInstance().outString("Iniciando Javavideoproducer:" + isProduction);
       ReproductorCanciones rc = new ReproductorCanciones();
       rc.iniciar();
    }

    public static boolean isIsProduction() {
        return isProduction;
    }

    public static void setIsProduction(boolean isProduction) {
        Main.isProduction = isProduction;
    }
    
    
    
    
   
}
