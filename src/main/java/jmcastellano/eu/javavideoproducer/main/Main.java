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
    
    public static boolean isProduction = true;
    
    public static void main(String[] args){
        if(args!=null && args.length > 0 && args[0].equals("false")){
            isProduction = false;
        }
       ReproductorCanciones rc = new ReproductorCanciones();
       rc.iniciar();
    }
   
}
