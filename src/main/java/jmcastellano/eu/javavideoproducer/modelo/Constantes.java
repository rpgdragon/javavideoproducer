/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmcastellano.eu.javavideoproducer.modelo;

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
    public static final String RUTA_UNIX="/home/ubuntu/tmp/";
    private static final String RUTA_WINDOWS="C:\\Users\\%%usuario%%\\tmp\\";
    public static final String URL_STREAMING="https://studio.youtube.com/channel/UCEEhD4GCgmqtzhpiKNcq0tQ/livestreaming/";
    public static final String URL_MENSAJES="https://raw.githubusercontent.com/rpgdragon/javavideoproducer/master/mensajes.txt";
    public static final String URL_CANCIONES="https://raw.githubusercontent.com/rpgdragon/javavideoproducer/master/canciones.txt";
    public static final String URL_IMAGEN="https://raw.githubusercontent.com/rpgdragon/javavideoproducer/master/img/";
    public static final String RUTA_UNIX_BASICA="/home/ubuntu/radio/";
    public static final String RUTA_WINDOWS_BASICA="C:\\Users\\%%usuario%%\\radio\\";
    public static final String FINALIZANDO_TRANSMISION="Finalizando programa de radio";
    public static final String INICIANDO_TRANSMISION="Iniciando programa de radio";
    public static final String SUSCRIBETE="No te olvides de suscribirte. Nos vemos.";
    public static final String SUSCRIBETEINICIO="Empezamos a emitir en breve, por favor aguarde";
    public static final String DETENIENDO = "Deteniendo Transmisión";
    public static final long MAX_EJECUCION_MS = 3600000;
    public static final long TIEMPO_DESCONEXION = 10000;
    public static final long TIEMPO_ARRANQUE = 20000;
    public static final int MIN_DURACION=10;
    public static final int MAX_DURACION=1200;
    public static final String OBSLINUX = "obs --profile \"RADIO\" --scene \"RADIO\" --startstreaming --minimize-to-tray ";
    public static String COMANDO_FFMPEG="ffmpeg -f x11grab -s \"1280x680\" -r \"20\" -i :0+0,80 -f alsa -ac 2 -i default -acodec libmp3lame -ab 128k -ar 44100 -threads 0 -b:v 2500K -f flv \"rtmp://a.rtmp.youtube.com/live2/";
    public static String NOMBRE_PROGRAMA="Programming and Games Radio";
    public static String TITULO_YOUTUBE="Radio de videojuegos/videogames 24/7";
    public static String PARAMETROS=" -f alsa -ac 2 -i default -acodec libmp3lame -ab 128k -ar 44100 -threads 0 -b:v 2500K ";
    public static final int CANCIONES_ESPERA_MENSAJES=10;
    public static final int CANCIONES_ESPERA_CANCIONES=200;
    public static final long MAX_TIEMPO_MARQUESINA=20000;

    public enum Fichero{
        BG1("./bg1.gif");
        
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
    
    public static void esperar(int i) {
        try{
            Thread.sleep(i);
        }
        catch(InterruptedException e){}
    }
    
    public static String dameRutaWindows(){
        return RUTA_WINDOWS.replaceAll("%%usuario%%",  System.getProperty("user.name"));
    }

    public static String dameRutaWindowsBasic(){
        return RUTA_WINDOWS_BASICA.replaceAll("%%usuario%%",  System.getProperty("user.name"));
    }
}
