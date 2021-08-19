/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmcastellano.eu.javavideoproducer.main;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import jmcastellano.eu.javavideoproducer.html.Marquesina;
import jmcastellano.eu.javavideoproducer.html.TratamientoMp3;
import jmcastellano.eu.javavideoproducer.modelo.Constantes;
import jmcastellano.eu.javavideoproducer.modelo.Constantes.Fichero;

/**
 *
 * @author rpgdragon
 */
public class ReproductorCanciones {
    
    private Date inicial;
    private Date fechafinal;
    private static final long MAX_EJECUCION_MS = 3600000;
    private JFrame ventana;
    private JLabel textocancion;
    private JLabel fondo;
    private Marquesina marquesina;
    private int mostrarmarquesina;
    
    public void iniciar(){
        try{
            mostrarmarquesina=0;
            TratamientoMp3 gmp3 = TratamientoMp3.getInstance();
            inicializarVentana();
            inicial = new Date();
            gmp3.iniciarBusqueda();
            if(Main.isProduction){
                inicializarFirefox();
            }
            Constantes.esperar(1000);
            if(Main.isProduction){
                inicializarOBS();
            }
            Constantes.esperar(3000);
            do{
                //todos los ciclos son igual, reproducir cancion y buscar siguiente cancion mientras se reproduce, al finalizar cancion, borrar cancion
                if(!gmp3.isBusquedaactiva()){
                    String cancionactual = gmp3.getProximacancion();
                    gmp3.iniciarBusqueda();
                    reproducirCancion(cancionactual);
                    borrarFichero(cancionactual);
                    cambiarFondo();
                }
                fechafinal = new Date();
            }
            while(fechafinal.getTime() - inicial.getTime() < MAX_EJECUCION_MS);
            if(Main.isProduction){
                pararTransmision();
            }
            apagarEquipo();
        }
        catch(AWTException | IOException e){
            e.printStackTrace();
            //apagarEquipo();
        }
    }
    
    
    private void reproducirCancion(String cancionactual){
        //iniciamos la reproduccion
        String ruta = dameRuta();
        FileInputStream stream;
        try {
            stream = new FileInputStream(ruta + cancionactual);
            Player player = new Player(stream);
            //vamos a cambiar el Label para mostrar el nombre de la cancion
            actualizarCancionVentana(cancionactual);
            player.play();
            player.close();
        } catch (FileNotFoundException | JavaLayerException ex) {}
    }
    
    private void borrarFichero(String cancionactual){
        String ruta = dameRuta();
        File fichero = new File(ruta + cancionactual);
        fichero.delete();
    }
    
    
    private void inicializarVentana(){
        ventana = new JFrame();
        ventana.setSize(1200, 675);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setLayout(new BorderLayout());
        ventana.setTitle("Reproductor VG");
        cambiarFondo();
        ventana.setVisible(true);
    }
    
    private void cambiarFondo(){
        String ruta = dameRuta();
        Fichero f1 = Constantes.randomFichero();
        fondo = new JLabel(new ImageIcon(ruta + f1.getRuta()));
        ventana.setContentPane(fondo);
    }
    
    private JLabel getTextocancion(){
        if(textocancion==null){
            textocancion=new JLabel("", SwingConstants.CENTER);
            Font font = new Font("SansSerif", Font.BOLD, 30);
            textocancion.setFont(font);
            textocancion.setBackground(Color.WHITE);
            textocancion.setOpaque(true);
            textocancion.setBounds(0, 575, 1200, 40);
        }
        return textocancion;
    }

    private String dameRuta() {
        String ruta = null;
        if(Constantes.windowsOrLinux()){
            ruta = Constantes.RUTA_WINDOWS;
        }
        else{
            ruta = Constantes.RUTA_UNIX;
        }
        return ruta;
    }

    private void apagarEquipo() {
       String shutdownCommand;
        if(Constantes.windowsOrLinux()){
            shutdownCommand = "shutdown.exe -s -t 0";   
        }
        else{
            shutdownCommand = "shutdown -h now";
        }
        try{
            Runtime.getRuntime().exec(shutdownCommand);
        }
        catch(IOException e){}
        System.exit(0);
    }

    private void inicializarFirefox() throws IOException {
        if(Constantes.windowsOrLinux()){
             Runtime.getRuntime().exec("C:\\Program Files\\Mozilla Firefox\\firefox.exe https://studio.youtube.com/channel/UCEEhD4GCgmqtzhpiKNcq0tQ/livestreaming/");
        }
        else{
            Runtime.getRuntime().exec("firefox https://studio.youtube.com/channel/UCEEhD4GCgmqtzhpiKNcq0tQ/livestreaming/");
        }
    }
    
    private void inicializarOBS() throws IOException {
        if(Constantes.windowsOrLinux()){
            Runtime.getRuntime().exec("cmd /c start /d \"C:\\Program Files\\obs-studio\\bin\\64bit\\\" obs64.exe --startstreaming --minimize-to-tray");
        }
        else{
             //TODO revisar
            Runtime.getRuntime().exec("obs");
        }
    }
    
    private void pararTransmision() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_F11);
    }

    private void actualizarCancionVentana(String cancionactual) {
        ventana.setLayout(null);
         if(mostrarmarquesina==0){
            ventana.add(getMarquesina());
            getMarquesina().setText("");
            getMarquesina().setText("No te olvides de suscribirte para seguir las nuevas novedades");
        }
        ventana.add(getTextocancion());
        getTextocancion().setText(cancionactual);
        mostrarmarquesina++;
        mostrarmarquesina=mostrarmarquesina%5;
    }
    
    private Marquesina getMarquesina(){
        if(marquesina==null){
            marquesina = new Marquesina("",SwingConstants.CENTER);
            Font font = new Font("SansSerif", Font.BOLD, 20);
            marquesina.setFont(font);
            marquesina.setBackground(Color.WHITE);
            marquesina.setOpaque(true);
            marquesina.setBounds(300, 60, 600, 40);
            marquesina.setText("");
        }
        return marquesina;
    }
}
