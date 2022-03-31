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
import jmcastellano.eu.javavideoproducer.utils.Logger;
import jmcastellano.eu.javavideoproducer.utils.Utils;

/**
 *
 * @author rpgdragon
 */
public abstract class TratamientoMp3 {
    private final LinkedList<Cancion> proximacancion;
    private boolean busquedaactiva;
    private Thread hebra;
    
    public TratamientoMp3(){
        proximacancion= new LinkedList<>();
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
      
    public void iniciarBusqueda(){
        if(!isBusquedaactiva()){
            hebra = new Thread(() -> {
                setBusquedaactiva(true);
                while(obtener_tamano_canciones() < 5){
                    Cancion cancion = busqueda();
                    anadir_cancion(cancion);
                   Logger.getInstance().outString("Añadida Canción:" + cancion.getNombre_cancion());
                }
                setBusquedaactiva(false);
            });
            hebra.start();

        }
    }
    
    public abstract Cancion busqueda();
    
  
	protected void descargar(String url, String nombre) throws IOException{
        URLConnection conn = new URL(url).openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        conn.connect();
        InputStream is = conn.getInputStream();
        
        String ruta = Utils.dameRuta();
        try (OutputStream outstream = new FileOutputStream(new File(ruta + nombre))) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = is.read(buffer)) > 0) {
                outstream.write(buffer, 0, len);
            }
        }
    }
    
}
