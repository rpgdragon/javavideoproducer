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
import java.net.URL;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import jmcastellano.eu.javavideoproducer.html.DescargarMensajes;
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
    private final int tiempo;
    private DescargarMensajes d;
    private boolean cargainicial = false;
    
    public ReproductorCanciones(int tiempo){
        this.tiempo = tiempo;
    }
    
    public void iniciar(){
        try{
            comprobarCarpetaTemporal();
            TratamientoMp3 gmp3 = TratamientoMp3.getInstance();
            gmp3.iniciarBusqueda();
            d = new DescargarMensajes();
            d.obtenerMensajes();
            //vamos a esperar a que al menos este una cancion y los textos
            while(gmp3.obtener_tamano_canciones() <= 0 || d.getMensajes()==null || d.getMensajes().size() <= 0){
                //esperamos 1 segundo
                Thread.sleep(1000);
            }
            inicial = new Date();
            
            if(Main.isIsProduction()){
                inicializarFirefox();
                Constantes.esperar(60000);
                inicializarVentana();
                inicializarOBS();
                Constantes.esperar(3000);
            }
            else{
                inicializarVentana();
            }
            do{
                //todos los ciclos son igual, reproducir cancion y buscar siguiente cancion mientras se reproduce, al finalizar cancion, borrar cancion
                if(gmp3.obtener_tamano_canciones() > 0){
                    String cancionactual = gmp3.getProximacancion();
                    gmp3.iniciarBusqueda();
                    reproducirCancion(cancionactual);
                    borrarFichero(cancionactual);
                    cambiarFondo();
                }
                fechafinal = new Date();
            }
            while(fechafinal.getTime() - inicial.getTime() < (MAX_EJECUCION_MS * tiempo));
            if(Main.isIsProduction()){
                pararTransmision();
            }
            apagarEquipo();
        }
        catch(AWTException | IOException | InterruptedException e){
            e.printStackTrace();
            try{
                apagarEquipo();
            }
            catch(Exception e1){}
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
        ventana.setSize(640, 360);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setLayout(new BorderLayout());
        ventana.setTitle("Reproductor VG");
        cambiarFondo();
        cargainicial=true;
        ventana.setVisible(true);
    }
    
    private void cambiarFondo(){
        if(!cargainicial){
            Fichero f1 = Constantes.randomFichero();
            URL url = this.getClass().getResource("/images/" + f1.getRuta());
            fondo = new JLabel(new ImageIcon(url));
            ventana.setContentPane(fondo);
        }
        else{
            cargainicial=false;
        }
    }
    
    private JLabel getTextocancion(){
        if(textocancion==null){
            textocancion=new JLabel("", SwingConstants.CENTER);
            Font font = new Font("SansSerif", Font.BOLD, 20);
            textocancion.setFont(font);
            textocancion.setBackground(Color.WHITE);
            textocancion.setOpaque(true);
            textocancion.setBounds(0, 290, 640, 25);
        }
        return textocancion;
    }

    private String dameRuta() {
        String ruta;
        if(Constantes.windowsOrLinux()){
            ruta = Constantes.dameRutaWindows();
        }
        else{
            ruta = Constantes.RUTA_UNIX;
        }
        return ruta;
    }

    private void apagarEquipo() throws IOException, InterruptedException {
        Process p;
        if(Constantes.windowsOrLinux()){
            p = Runtime.getRuntime().exec("shutdown /s");
        }
        else{
            p = Runtime.getRuntime().exec("sudo shutdown -h now");
        }
        p.waitFor(10, TimeUnit.SECONDS);
    }

    private void inicializarFirefox() throws IOException, InterruptedException {
        Process p;
        p = Runtime.getRuntime().exec("firefox " + Constantes.URL_STREAMING);
        p.waitFor(10, TimeUnit.SECONDS);
    }
    
    private void inicializarOBS() throws IOException,InterruptedException {
        Process p;
        if(Constantes.windowsOrLinux()){
            p = Runtime.getRuntime().exec("cmd /c start /d \"C:\\Program Files\\obs-studio\\bin\\64bit\\\" obs64.exe --startstreaming --minimize-to-tray");
        }
        else{
             //TODO revisar
            p = Runtime.getRuntime().exec("obs --startstreaming --minimize-to-tray");
        }
        p.waitFor(10, TimeUnit.SECONDS);
    }
    
    private void pararTransmision() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_F7);
    }

    private void actualizarCancionVentana(String cancionactual) {
        ventana.setLayout(null);
        ventana.add(getTextocancion());
        getTextocancion().setText(cancionactual);
        ventana.add(getMarquesina());
        getMarquesina().setText(d.dameMensajeAleatorio());
        
    }

    
    private Marquesina getMarquesina(){
        if(marquesina==null){
            marquesina = new Marquesina("",Marquesina.RIGHT_TO_LEFT,10);
            Font font = new Font("SansSerif", Font.BOLD, 15);
            marquesina.setFont(font);
            marquesina.setBackground(Color.WHITE);
            marquesina.setOpaque(true);
            marquesina.setBounds(50, 50, 540, 20);
        }
        return marquesina;
    }

    private void comprobarCarpetaTemporal() {
        //nos aseguramos de que el directorio tmp existe para el SO actual
        String rutatmp;
        if(Constantes.windowsOrLinux()){
            rutatmp = Constantes.dameRutaWindows();
        }
        else{
            rutatmp = Constantes.RUTA_UNIX;
        }
        File f = new File(rutatmp);
        if(!f.exists() || !f.isDirectory()){
            f.mkdir();
        }
    }
}
