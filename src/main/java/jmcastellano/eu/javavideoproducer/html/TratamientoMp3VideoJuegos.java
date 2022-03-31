/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmcastellano.eu.javavideoproducer.html;

import jmcastellano.eu.javavideoproducer.main.ReproductorCanciones;
import jmcastellano.eu.javavideoproducer.modelo.Cancion;
import jmcastellano.eu.javavideoproducer.modelo.Constantes;
import org.jsoup.Jsoup;

/**
 *
 * @author rpgdragon
 */
public class TratamientoMp3VideoJuegos extends TratamientoMp3 {
    private static TratamientoMp3VideoJuegos instance;
    
    private TratamientoMp3VideoJuegos() {
    	super();
    }

    public static TratamientoMp3VideoJuegos getInstance(){
        if(instance==null){
            instance = new TratamientoMp3VideoJuegos();
        }
        return instance;
    }
    
    @Override
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
                Cancion c = new Cancion(nombrealbum, nombrecancion);
                if(ReproductorCanciones.getDescargarCancionesBan().esCancionProhibida(c)) {
                	continue;
                }
                //solo descargamos si y solo si, no es una cancion baneada
                descargar(url, nombrecancion);
                //antes de devolver hay que ver lo que dura la cancion
                int duracion = c.calcularDuracion();
                if(duracion < Constantes.MIN_DURACION || duracion > Constantes.MAX_DURACION){
                    //canciones que duren menos de MIN DURACION segundos o más de MAX DURACION se descartan o que esten dentro del permaban no deben reproducirse
                    //aunque primero se borran
                    c.borrarFichero();
                    continue;
                }
                //la cancion dura entre 15seg y 10 min
                return c;
            }
            catch(Exception e){}
        }
    }
    
}
