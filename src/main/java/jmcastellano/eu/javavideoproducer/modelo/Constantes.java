/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmcastellano.eu.javavideoproducer.modelo;

import java.util.Random;

/**
 *
 * @author rpgdragon
 */
public class Constantes {
    public static final String URL_VIDEOJUEGOS="https://downloads.khinsider.com/random-song";
    public static final String CADENA_NOMBRE_CANCION="<br> Song name: <b>";
    public static final String B = "<b>";
    public static final String CIERRE_B = "</b>";
    public static final String HREF= "href=";
    public static final String SPAN_CLASS="<span class";
    public static final String CADENA_URL_CANCION="<p><a style=\"color: #21363f;\" href=\"https://vgmsite.com/soundtracks/";
    public static final String RUTA_UNIX="/home/pi/tmp/";
    public static final String RUTA_WINDOWS="C:\\Users\\rpgdragon\\tmp\\";
    public static final String URL_MENSAJES="https://raw.githubusercontent.com/rpgdragon/javavideoproducer/master/mensajes.txt";


    public enum Fichero{
        BG1("bg1.jpg"), BG2("bg2.jpg"), BG3("bg3.jpg"), BG4("bg4.jpg"), BG5("bg5.jpg"), BG6("bg6.jpg"), BG7("bg7.jpg"), BG8("bg8.jpg"), BG9("bg9.jpg"), BG10("bg10.jpg"), BG11("bg11.jpg");
        
        private Fichero(String ruta){
            this.ruta = ruta;
        }
        
        private String ruta;
        
        public String getRuta(){
            return ruta;
        }
        
        public void setRuta(String ruta){
            this.ruta = ruta;
        }
    }
    
    public static boolean windowsOrLinux(){
        String os = System.getProperty("os.name");
        if(os.toLowerCase().contains("windows")){
            return true;
        }
        return false;
    }
    
    public static Fichero randomFichero(){
        Random rand = new Random();
        int num = rand.nextInt(Fichero.values().length)+1;
        switch(num){
            case 1:
                return Fichero.BG1;
            case 2:
                return Fichero.BG2;
            case 3:
                return Fichero.BG3;
            case 4:
                return Fichero.BG4;
            case 5:
                return Fichero.BG5;
            case 6:
                return Fichero.BG6;
            case 7:
                return Fichero.BG7;
            case 8:
                return Fichero.BG8;
            case 9:
                return Fichero.BG9;
            case 10:
                return Fichero.BG10;
            case 11:
                return Fichero.BG11;
        }
        return Fichero.BG1;
    }
    
    public static void esperar(int i) {
        try{
            Thread.sleep(i);
        }
        catch(InterruptedException e){}
    }
}
