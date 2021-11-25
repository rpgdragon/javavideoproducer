/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmcastellano.eu.javavideoproducer.modelo;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;
import jmcastellano.eu.javavideoproducer.utils.Utils;

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
    
    public int calcularDuracion() throws UnsupportedAudioFileException, IOException {
        String ruta = Utils.dameRuta();
        File file = new File(ruta + this.nombre_cancion);
        AudioFileFormat baseFileFormat = new MpegAudioFileReader().getAudioFileFormat(file);
        Map properties = baseFileFormat.properties();
        Long microseconds = (Long) properties.get("duration");
        int mili = (int) (microseconds / 1000);
        return (mili / 1000);
    } 
    
    public void borrarFichero(){
        String ruta = Utils.dameRuta();
        File fichero = new File(ruta + this.nombre_cancion);
        fichero.delete();
    }
}
