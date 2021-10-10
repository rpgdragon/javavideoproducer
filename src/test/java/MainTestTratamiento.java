
import jmcastellano.eu.javavideoproducer.html.TratamientoMp3;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rpgdragon
 */
public class MainTestTratamiento {
    
    public static void main(String[] args){
        TratamientoMp3 gmp3 = TratamientoMp3.getInstance();
        while(true){
            System.out.println(gmp3.busqueda().getNombre_cancion());
        }
    }
    
}
