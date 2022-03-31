/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmcastellano.eu.javavideoproducer.html;

import java.util.ArrayList;
import java.util.HashMap;

import jmcastellano.eu.javavideoproducer.modelo.Cancion;
import jmcastellano.eu.javavideoproducer.modelo.Constantes;

/**
 *
 * @author rpgdragon
 */
public class DescargarCancionesBan extends DescargarTexto {
    
    private HashMap<String,ArrayList<String>> canciones;
    
    public DescargarCancionesBan(){
        super(Constantes.URL_CANCIONES);
    }

	@Override
	public boolean hayDatosPrevios() {
		if(canciones==null || canciones.isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public void reset() {
		this.setEnEjecucion(false);
		this.canciones = null;
	}

	@Override
	public void tramitarDescarga(String linea) {
		ArrayList<String> cancionesalbum;
		if(linea==null || linea.isEmpty() || linea.split(";;;;").length!=2) {
			return;
		}
		if(canciones==null) {
			canciones = new HashMap<>();
		}
		String[] arr = linea.split(";;;;");
		//buscamos si dentro del hash existe un album con ese mismo nombre
		if(canciones.containsKey(arr[0])) {
			cancionesalbum = canciones.get(arr[0]);
		}
		else {
			cancionesalbum = new ArrayList<>();
			canciones.put(arr[0], cancionesalbum);
		}
		cancionesalbum.add(arr[1]);
	}
	
	/**
	 * Método que nos permite saber si la cancion suministrada esta baneada o no
	 * @param c
	 * @return
	 */
	public boolean esCancionProhibida(Cancion c) {
		//si no hay canciones, entonces no esta baneada
	   if(canciones==null || canciones.isEmpty()) {
		   return false;
	   }
	   
	   //Si el album no esta, no esta baneada
	   if(!canciones.containsKey(c.getNombre_album())) {
		   return false;
	   }
	   
	   
	   
	   //Si llega aqui es que el album existe, hay que mirar por canciones
	   for(String nombreCancion: canciones.get(c.getNombre_album())) {
		   if(c.getNombre_cancion().matches(nombreCancion)) {
			   return true;
		   }
	   }
	   return false;
			
	}

    
}
