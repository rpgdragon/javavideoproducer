/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmcastellano.eu.javavideoproducer.html;

import jmcastellano.eu.javavideoproducer.main.ReproductorCanciones;
import jmcastellano.eu.javavideoproducer.modelo.Cancion;
import jmcastellano.eu.javavideoproducer.modelo.Constantes;

import java.util.Random;

/**
 *
 * @author rpgdragon
 */
public class TratamientoMp3Ansiedad extends TratamientoMp3 {
    private static TratamientoMp3Ansiedad instance;
    private static final int MINIMO_NUMERO=50;
    private static final int MAXIMO_NUMERO=2700;
    private static final String URL="https://www.fesliyanstudios.com/download-link.php?src=i&id=";
    
    private TratamientoMp3Ansiedad() {
    	super();
    }

    public static TratamientoMp3Ansiedad getInstance(){
        if(instance==null){
            instance = new TratamientoMp3Ansiedad();
        }
        return instance;
    }
    
    @Override
    public Cancion busqueda(){
        while(true){
            try{
                Random r = new Random();
                int numero = r.nextInt(MAXIMO_NUMERO-MINIMO_NUMERO) + MINIMO_NUMERO;
                String nombrecancion = cadenaAleatoria();
                String nombrealbum = cadenaAleatoria();
                descargar(URL +  numero, nombrecancion);
                Cancion c = new Cancion(nombrealbum, nombrecancion);
                //antes de devolver hay que ver lo que dura la cancion
                int duracion = c.calcularDuracion();
                if(duracion < Constantes.MIN_DURACION || duracion > Constantes.MAX_DURACION || ReproductorCanciones.getDescargarCancionesBan().esCancionProhibida(c)){
                    //canciones que duren menos de MIN DURACION segundos o más de MAX DURACION se descartan o que esten dentro del permaban no deben reproducirse
                    //aunque primero se borran
                    c.borrarFichero();
                    continue;
                }
                //la cancion dura entre 15seg y 10 min
                return c;
            }
            catch(Exception e){
            	e.printStackTrace();
            }
        }
    }
    
    public String cadenaAleatoria() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
          .limit(targetStringLength)
          .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
          .toString();

        return generatedString;
    }
    
}
