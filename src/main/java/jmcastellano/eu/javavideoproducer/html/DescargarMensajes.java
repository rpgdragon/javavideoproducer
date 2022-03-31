/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmcastellano.eu.javavideoproducer.html;

import java.util.ArrayList;
import java.util.Random;
import jmcastellano.eu.javavideoproducer.modelo.Constantes;

/**
 *
 * @author rpgdragon
 */
public class DescargarMensajes extends DescargarTexto {
    
    private ArrayList<String> mensajes;
    
    public DescargarMensajes(){
        super(Constantes.URL_MENSAJES);
    }

   /**
   * Función que nos permite resetear
   */
   public void reset(){
       this.setEnEjecucion(false);
       this.mensajes = null;
   }
    
   @Override
	public boolean hayDatosPrevios() {
		if(mensajes==null || mensajes.isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public void tramitarDescarga(String linea) {
		if(linea==null || linea.isEmpty()) {
			return;
		}
		if(mensajes==null) {
			mensajes = new ArrayList<String>();
		}
        mensajes.add(linea);
	}

    /**
     * Permite recuperar un mensaje aleatorio para mostrarse
     * @return
     */
    public String dameMensajeAleatorio(){
        if(mensajes!=null && !mensajes.isEmpty()){
            Random r = new Random();
            int tam = mensajes.size();
            
           return mensajes.get(r.nextInt(tam));
        }
        return "";
    }
}
