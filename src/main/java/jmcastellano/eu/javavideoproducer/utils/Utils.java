/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmcastellano.eu.javavideoproducer.utils;

import jmcastellano.eu.javavideoproducer.modelo.Constantes;

/**
 *
 * @author pi
 */
public class Utils {
    
    public static String dameRuta() {
        String ruta;
        if(Constantes.windowsOrLinux()){
            ruta = Constantes.dameRutaWindows();
        }
        else{
            ruta = Constantes.RUTA_UNIX;
        }
        return ruta;
    }
}
