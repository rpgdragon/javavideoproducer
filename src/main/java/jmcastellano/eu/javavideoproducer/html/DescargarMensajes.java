/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmcastellano.eu.javavideoproducer.html;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import jmcastellano.eu.javavideoproducer.modelo.Constantes;

/**
 *
 * @author rpgdragon
 */
public class DescargarMensajes {
    
    private ArrayList<String> mensajes;
    private boolean enEjecucion = false;
    
    public DescargarMensajes(){
        
    }
    
    public void obtenerMensajes(){
        if(mensajes==null || mensajes.isEmpty()){
            enEjecucion = true;
            mensajes = new ArrayList<String>();
            Thread t = new Thread(new Runnable(){
                @Override
                public void run() {
                    try { 
                        URL url = new URL(Constantes.URL_MENSAJES);
                        Scanner s = new Scanner(url.openStream(),"UTF-8");
                        while(s.hasNextLine()){
                            String cad = s.nextLine();
                            mensajes.add(cad);
                        }
                    } catch(Exception e){}
                    finally{
                        enEjecucion = false;
                    }
                }
            });
            t.start();
        }
    }

    public ArrayList<String> getMensajes() {
        return mensajes;
    }

    public boolean isEnEjecucion() {
        return enEjecucion;
    }
    
    public String dameMensajeAleatorio(){
        if(mensajes!=null && !mensajes.isEmpty()){
            Random r = new Random();
            int tam = mensajes.size();
            
           return mensajes.get(r.nextInt(tam));
        }
        return "";
    }
}
