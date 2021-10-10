/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmcastellano.eu.javavideoproducer.modelo;

/**
 *
 * @author pi
 */
public class Cancion {
    
    private String nombre_album;
    private String nombre_cancion;
    
    public Cancion(String nombre_album, String nombre_cancion){
        this.nombre_album = nombre_album;
        this.nombre_cancion = nombre_cancion;
    }

    public String getNombre_album() {
        return nombre_album;
    }

    public void setNombre_album(String nombre_album) {
        this.nombre_album = nombre_album;
    }

    public String getNombre_cancion() {
        return nombre_cancion;
    }

    public void setNombre_cancion(String nombre_cancion) {
        this.nombre_cancion = nombre_cancion;
    }
    
    
    
}
