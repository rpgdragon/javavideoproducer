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
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import jmcastellano.eu.javavideoproducer.html.DescargarImagenes;
import jmcastellano.eu.javavideoproducer.html.DescargarMensajes;
import jmcastellano.eu.javavideoproducer.html.Marquesina;
import jmcastellano.eu.javavideoproducer.html.TratamientoMp3;
import jmcastellano.eu.javavideoproducer.modelo.Cancion;
import jmcastellano.eu.javavideoproducer.modelo.Constantes;
import jmcastellano.eu.javavideoproducer.modelo.Constantes.Fichero;
import jmcastellano.eu.javavideoproducer.utils.Logger;
import jmcastellano.eu.javavideoproducer.utils.Utils;
import org.jsoup.Jsoup;

/**
 *
 * @author rpgdragon
 */
public class ReproductorCanciones {
    
    private Date inicial;
    private Date fechafinal;

    private JFrame ventana;
    private JLabel textocancion;
    private JLabel textoalbum;
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
            Logger.getInstance().outString("MAX_EJECUCION_MS: " + Constantes.MAX_EJECUCION_MS );
            comprobarCarpetaTemporal();
            TratamientoMp3 gmp3 = TratamientoMp3.getInstance();
            gmp3.iniciarBusqueda();
            d = new DescargarMensajes();
            d.obtenerMensajes();
            DescargarImagenes di = new DescargarImagenes();
            di.obtenerImagenes();
            //vamos a esperar a que al menos este una cancion y los textos
            while(gmp3.obtener_tamano_canciones() <= 0 || d.getMensajes()==null || d.getMensajes().size() <= 0){
                //esperamos 1 segundo
                Thread.sleep(1000);
            }
           
            
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
            
            indicarEstadoTransmision(Constantes.INICIANDO_TRANSMISION,Constantes.SUSCRIBETEINICIO,Constantes.TIEMPO_ARRANQUE);
            //vamos a poner una ventana inicial que inicializa la emision y que dura unos 20 seg
            //se supone que con esto, dará tiempo a que se obtenga la hora actual
            inicial = new Date(); 
            do{
                try{
                        //todos los ciclos son igual, reproducir cancion y buscar siguiente cancion mientras se reproduce, al finalizar cancion, borrar cancion
                        if(gmp3.obtener_tamano_canciones() > 0){
                            Cancion cancionactual = gmp3.getProximacancion();
                            gmp3.iniciarBusqueda();
                            reproducirCancion(cancionactual);
                            cancionactual.borrarFichero();
                            cambiarFondo();
                        }
                        fechafinal = new Date();
                    }
                    catch(Exception e){
                        Logger.getInstance().outString(e.getMessage());
                    }
                }
                while(fechafinal.getTime() - inicial.getTime() < (Constantes.MAX_EJECUCION_MS * tiempo));
            
            indicarEstadoTransmision(Constantes.FINALIZANDO_TRANSMISION,Constantes.SUSCRIBETE,Constantes.TIEMPO_DESCONEXION);
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
    
    
    private void reproducirCancion(Cancion cancionactual){
        //iniciamos la reproduccion
        String ruta = Utils.dameRuta();
        FileInputStream stream;
        try {
            stream = new FileInputStream(ruta + cancionactual.getNombre_cancion());
            Player player = new Player(stream);
            //vamos a cambiar el Label para mostrar el nombre de la cancion
            actualizarCancionVentana(cancionactual);
            player.play();
            player.close();
        } catch (FileNotFoundException | JavaLayerException ex) {}
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
        getTextoalbum();
        getTextocancion();
    }
    
    private void cambiarFondo(){
        if(!cargainicial){
            Fichero f1 = Constantes.randomFichero();
            fondo = new JLabel(new ImageIcon(Utils.dameRuta() + f1.getRuta()));
            ventana.setContentPane(fondo);
        }
        else{
            cargainicial=false;
        }
    }
    
    private JLabel getTextocancion(){
        if(textocancion==null){
            textocancion=new JLabel("", SwingConstants.CENTER);
            Font font = new Font("SansSerif", Font.BOLD, 10);
            textocancion.setFont(font);
            textocancion.setBackground(Color.WHITE);
            textocancion.setOpaque(true);
            textocancion.setBounds(0, 305, 638, 25);
            textocancion.setHorizontalAlignment(SwingConstants.LEFT);
            Border border = BorderFactory.createLineBorder(Color.BLACK, 2);
            textocancion.setBorder(border);
        }
        return textocancion;
    }
    
    private JLabel getTextoalbum(){
        if(textoalbum==null){
            textoalbum=new JLabel("", SwingConstants.CENTER);
            Font font = new Font("SansSerif", Font.BOLD, 10);
            textoalbum.setFont(font);
            textoalbum.setBackground(Color.WHITE);
            textoalbum.setOpaque(true);
            textoalbum.setBounds(0, 265, 638, 25);
            textoalbum.setHorizontalAlignment(SwingConstants.LEFT);
            Border border = BorderFactory.createLineBorder(Color.BLACK, 2);
            textoalbum.setBorder(border);
        }
        return textoalbum;
    }


    private void apagarEquipo() throws IOException, InterruptedException {
        Logger.getInstance().outString("Se procede apagar equipo");
        if(!Main.isIsProduction()){
            System.exit(0);
        }
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
    
    private void inicializarOBS() throws AWTException {
        Logger.getInstance().outString(Constantes.INICIANDO_TRANSMISION);  
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_F6);
        robot.delay(500);
        robot.keyRelease(KeyEvent.VK_F6);
    }
    
    private void pararTransmision() throws AWTException {
        Logger.getInstance().outString(Constantes.DETENIENDO);  
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_F7);
        robot.delay(500);
        robot.keyRelease(KeyEvent.VK_F7);
    }
    
    private int dimeTamanoPixelesTexto(String texto){
        if(texto==null || texto.isEmpty()){
            return 10;
        }
        if(texto.length() < 30){
            return 11;
        }
        if(texto.length() < 50){
            return 10;
        }
        if(texto.length() < 70){
            return 9;
        }
        return 7;
    }

    private void actualizarCancionVentana(Cancion cancionactual) {
        cancionactual.setNombre_album(Jsoup.parse(cancionactual.getNombre_album()).text().trim());
        cancionactual.setNombre_cancion(Jsoup.parse(cancionactual.getNombre_cancion()).text().trim());
        int cancion_tamano=dimeTamanoPixelesTexto(cancionactual.getNombre_cancion());
        int album_tamano=dimeTamanoPixelesTexto(cancionactual.getNombre_album());
        
        ventana.setLayout(null);
        ventana.add(getTextocancion());
        ventana.add(getTextoalbum());
        getTextocancion().setHorizontalAlignment(SwingConstants.LEFT);
        String album = "<html><span style=\"font-family:Arial;font-size:13px;\"><b> &nbsp;ALBUM: </b></span><span style=\"font-size:" + album_tamano + "px\"> " + cancionactual.getNombre_album() + "</span></html>";
        getTextoalbum().setText(album);
        String cancion = "<html><span style=\"font-family:Arial;font-size:13px;\"><b> &nbsp;CANCION: </b></span><span style=\"font-size:" + cancion_tamano + "px\"> " + cancionactual.getNombre_cancion() + "</span></html>";
        getTextocancion().setText(cancion);
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
        //ahora vamos a proceder a borrar todos los ficheros que haya en la carpeta temporal
        for(File file: f.listFiles()){
            if(!file.isDirectory()){
                file.delete();
            }
        }
    }

    private void indicarEstadoTransmision(String estadotransmision, String marquesina, long tiempo) {
        ventana.setLayout(null);
        ventana.add(getTextocancion());
        getTextocancion().setHorizontalAlignment(SwingConstants.CENTER);
        getTextocancion().setText(estadotransmision);
        ventana.add(getMarquesina());
        getMarquesina().setText(marquesina);
        Calendar c1 = Calendar.getInstance();
        long t2 = 0;
        long t1 = c1.getTimeInMillis();
        do{
            try{ Thread.sleep(100); } catch(Exception e){}
            Calendar c2 = Calendar.getInstance();
            t2 = c2.getTimeInMillis();
        }while(t2-t1 < tiempo);
    }

}
