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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import jmcastellano.eu.javavideoproducer.modelo.Constantes;
import jmcastellano.eu.javavideoproducer.utils.Utils;

/**
 *
 * @author pi
 */
public class DescargarImagenes {

    private boolean enEjecucion = false;
    
    public DescargarImagenes(){
        
    }
    
    public void obtenerImagenes(){
        enEjecucion = true;
        Thread t = new Thread(() -> {
        try {
            for(int i=1; i <= 10; i++){
                descargar(Constantes.URL_IMAGEN + "bg" + i + ".jpg",Utils.dameRuta() + "bg" + i + ".jpg");
            }
        } catch(Exception e){ e.printStackTrace();}
                finally{
                    enEjecucion = false;
                }
            });
            t.start();
    }
    
    private void descargar(String url, String nombre) throws MalformedURLException, IOException{
        URLConnection conn = new URL(url).openConnection();
        InputStream is = conn.getInputStream();
        
        String ruta = Utils.dameRuta();
        try (OutputStream outstream = new FileOutputStream(new File(nombre))) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = is.read(buffer)) > 0) {
                outstream.write(buffer, 0, len);
            }
        }
    }
    
    public boolean isEnEjecucion() {
        return enEjecucion;
    }
    
}
