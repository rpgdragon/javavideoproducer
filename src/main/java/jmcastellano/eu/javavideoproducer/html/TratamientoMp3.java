/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmcastellano.eu.javavideoproducer.html;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import jmcastellano.eu.javavideoproducer.modelo.Cancion;
import jmcastellano.eu.javavideoproducer.modelo.Constantes;
import jmcastellano.eu.javavideoproducer.utils.Logger;
import org.jsoup.Jsoup;

/**
 *
 * @author rpgdragon
 */
public class TratamientoMp3 {
    private static TratamientoMp3 instance;
    private final LinkedList<Cancion> proximacancion;
    private boolean busquedaactiva;
    private Thread hebra;
    
    private TratamientoMp3(){
        proximacancion= new LinkedList<>();
    }
    
    public static TratamientoMp3 getInstance(){
        if(instance==null){
            instance = new TratamientoMp3();
        }
        return instance;
    }
    
    public synchronized Cancion getProximacancion(){
        if(proximacancion!=null && proximacancion.size() > 0){
            Cancion cancion = proximacancion.removeFirst();
            Logger.getInstance().outString("Proxima canción:" + cancion.getNombre_cancion());
            return cancion;
        }
        return null;
    }
    
    public synchronized  boolean isBusquedaactiva(){
        return busquedaactiva;
    }
    
    public synchronized  void setBusquedaactiva(boolean busquedaactiva){
        this.busquedaactiva = busquedaactiva;
    }
    
    public synchronized int obtener_tamano_canciones(){
        return proximacancion.size();
    }
    
    private synchronized void anadir_cancion(Cancion cancion){
        proximacancion.add(cancion);
    }
    
    private synchronized Cancion obtener_ultima_cancion(){
        return proximacancion.peekLast();
    }
    
    public void iniciarBusqueda(){
        if(!isBusquedaactiva()){
            hebra = new Thread(() -> {
                setBusquedaactiva(true);
                while(obtener_tamano_canciones() < 3){
                    Cancion cancion = busqueda();
                    anadir_cancion(cancion);
                   Logger.getInstance().outString("Añadida Canción:" + cancion.getNombre_cancion());
                }
                setBusquedaactiva(false);
            });
            hebra.start();

        }
    }
    
    public Cancion busqueda(){
        while(true){
            try{
                String html = Jsoup.connect(Constantes.URL_VIDEOJUEGOS).get().html();
                //hay que buscar el nombre de la cancion y la url de descargar
                int posicion = html.indexOf(Constantes.CADENA_NOMBRE_ALBUM);
                html = html.substring(posicion);
                posicion = html.indexOf(Constantes.B);
                html = html.substring(posicion);
                int posicion2 = html.indexOf(Constantes.CIERRE_B);
                String nombrealbum = html.substring(3, posicion2);
                posicion = html.indexOf(Constantes.CADENA_NOMBRE_CANCION);
                html = html.substring(posicion);
                posicion = html.indexOf(Constantes.B);
                posicion2 = html.indexOf(Constantes.CIERRE_B);
                String nombrecancion = html.substring(posicion+3, posicion2);
                posicion = html.indexOf(Constantes.HREF);
                posicion2 = html.indexOf(Constantes.SPAN_CLASS);
                String url = html.substring(posicion+6, posicion2-2);
                if(nombrecancion==null || nombrecancion.isEmpty()){
                    int laposicionbarra = url.lastIndexOf("/");
                    nombrecancion=url.substring(laposicionbarra+1);
                }
                else{
                    nombrecancion+=".mp3";
                }
                descargar(url, nombrecancion);
                Cancion c = new Cancion(nombrealbum, nombrecancion);
                return c;
            }
            catch(Exception e){}
        }
    }
    
    private void descargar(String url, String nombre) throws IOException{
        URLConnection conn = new URL(url).openConnection();
        InputStream is = conn.getInputStream();
        
        String ruta;
        if(Constantes.windowsOrLinux()){
            ruta = Constantes.dameRutaWindows();
        }
        else{
            ruta = Constantes.RUTA_UNIX;
        }
        try (OutputStream outstream = new FileOutputStream(new File(ruta + nombre))) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = is.read(buffer)) > 0) {
                outstream.write(buffer, 0, len);
            }
        }
    }
    
}
