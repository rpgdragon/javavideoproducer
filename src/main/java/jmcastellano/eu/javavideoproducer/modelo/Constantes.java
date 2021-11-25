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
    public static final String CADENA_NOMBRE_ALBUM="Album name:";
    public static final String CADENA_NOMBRE_CANCION="<br> Song name: <b>";
    public static final String B = "<b>";
    public static final String CIERRE_B = "</b>";
    public static final String HREF= "href=";
    public static final String SPAN_CLASS="<span class";
    public static final String CADENA_URL_CANCION="<p><a style=\"color: #21363f;\" href=\"https://vgmsite.com/soundtracks/";
    public static final String RUTA_UNIX="/home/pi/tmp/";
    private static final String RUTA_WINDOWS="C:\\Users\\%%usuario%%\\tmp\\";
    public static final String URL_STREAMING="https://studio.youtube.com/channel/UCEEhD4GCgmqtzhpiKNcq0tQ/livestreaming/";
    public static final String URL_MENSAJES="https://raw.githubusercontent.com/rpgdragon/javavideoproducer/master/mensajes.txt";
    public static final String FINALIZANDO_TRANSMISION="Finalizando programa de radio";
    public static final String INICIANDO_TRANSMISION="Iniciando programa de radio";
    public static final String SUSCRIBETE="No te olvides de suscribirte. Nos vemos.";
    public static final String SUSCRIBETEINICIO="Empezamos a emitir en breve, por favor aguarde";
    public static final String DETENIENDO = "Deteniendo Transmisión";
    public static final long MAX_EJECUCION_MS = 3600000;
    public static final long TIEMPO_DESCONEXION = 10000;
    public static final long TIEMPO_ARRANQUE = 20000;
    public static final int MIN_DURACION=10;
    public static final int MAX_DURACION=600;

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
        return os.toLowerCase().contains("windows");
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
    
    public static String dameRutaWindows(){
        return RUTA_WINDOWS.replaceAll("%%usuario%%",  System.getProperty("user.name"));
    }
}
