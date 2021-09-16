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
import jmcastellano.eu.javavideoproducer.modelo.Constantes;
import org.jsoup.Jsoup;

/**
 *
 * @author rpgdragon
 */
public class TratamientoMp3 {
    private static TratamientoMp3 instance;
    private LinkedList<String> proximacancion;
    private boolean busquedaactiva;
    private Thread hebra;
    
    private TratamientoMp3(){
        proximacancion= new LinkedList<String>();
    }
    
    public static TratamientoMp3 getInstance(){
        if(instance==null){
            instance = new TratamientoMp3();
        }
        return instance;
    }
    
    public synchronized String getProximacancion(){
        if(proximacancion!=null && proximacancion.size() > 0){
            String cancion = proximacancion.removeFirst();
            System.out.println("Proxima canción:" + cancion);
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
    
    private synchronized void anadir_cancion(String cancion){
        proximacancion.add(cancion);
    }
    
    private synchronized String obtener_ultima_cancion(){
        return proximacancion.peekLast();
    }
    
    public void iniciarBusqueda(){
        if(!isBusquedaactiva()){
            hebra = new Thread(() -> {
                setBusquedaactiva(true);
                while(obtener_tamano_canciones() < 3){
                    String cancion = busqueda();
                    anadir_cancion(cancion);
                    System.out.println("Añadida Canción:" + obtener_ultima_cancion());
                }
                setBusquedaactiva(false);
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
