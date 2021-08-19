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
import jmcastellano.eu.javavideoproducer.modelo.Constantes;
import org.jsoup.Jsoup;

/**
 *
 * @author rpgdragon
 */
public class TratamientoMp3 {
    private static TratamientoMp3 instance;
    private volatile String proximacancion;
    private volatile boolean busquedaactiva;
    private Thread hebra;
    
    private TratamientoMp3(){
        
    }
    
    public static TratamientoMp3 getInstance(){
        if(instance==null){
            instance = new TratamientoMp3();
        }
        return instance;
    }
    
    public String getProximacancion(){
        return proximacancion;
    }
    
    public boolean isBusquedaactiva(){
        return busquedaactiva;
    }
    
    public void setBusquedaactiva(boolean busquedaactiva){
        this.busquedaactiva = busquedaactiva;
    }
    
    public void iniciarBusqueda(){
        if(!busquedaactiva){
            hebra = new Thread(() -> {
                busquedaactiva = true;
                proximacancion= busqueda();
                busquedaactiva = false;
            });
            hebra.start();

        }
    }
    
    public String busqueda(){
        while(true){
            try{
                String html = Jsoup.connect(Constantes.URL_VIDEOJUEGOS).get().html();
                //hay que buscar el nombre de la cancion y la url de descargar
                int posicion = html.indexOf(Constantes.CADENA_NOMBRE_CANCION);
                html = html.substring(posicion);
                posicion = html.indexOf(Constantes.B);
                int posicion2 = html.indexOf(Constantes.CIERRE_B);
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
                return nombrecancion;
            }
            catch(IOException e){}
        }
    }
    
    private void descargar(String url, String nombre) throws IOException{
        URLConnection conn = new URL(url).openConnection();
        InputStream is = conn.getInputStream();
        
        String ruta = null;
        if(Constantes.windowsOrLinux()){
            ruta = Constantes.RUTA_WINDOWS;
        }
        else{
            ruta = Constantes.RUTA_UNIX;
        }
        OutputStream outstream = new FileOutputStream(new File(ruta + nombre));
        byte[] buffer = new byte[4096];
        int len;
        while ((len = is.read(buffer)) > 0) {
            outstream.write(buffer, 0, len);
        }
        outstream.close();
    }
    
}
