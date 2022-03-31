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
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import jmcastellano.eu.javavideoproducer.modelo.Cancion;
import jmcastellano.eu.javavideoproducer.modelo.Constantes;
import jmcastellano.eu.javavideoproducer.modelo.Constantes.Fichero;
import jmcastellano.eu.javavideoproducer.utils.Logger;
import jmcastellano.eu.javavideoproducer.utils.Utils;
import jmcastellano.eu.javavideoproducer.html.DescargarCancionesBan;
import jmcastellano.eu.javavideoproducer.html.DescargarMensajes;
import jmcastellano.eu.javavideoproducer.html.Marquesina;
import jmcastellano.eu.javavideoproducer.html.TratamientoMp3;
import jmcastellano.eu.javavideoproducer.html.TratamientoMp3VideoJuegos;
import jmcastellano.eu.javavideoproducer.html.TratamientoMp3Ansiedad;

import org.jsoup.Jsoup;

/**
 *
 * @author rpgdragon
 */
public class ReproductorCanciones {
   
    private JFrame ventana;
    private JLabel textocancion;
    private JLabel textoalbum;
    private JLabel fondo;
    private Marquesina marquesina;
    private boolean cargainicial = false;
    private DescargarMensajes d = new DescargarMensajes();;
    private static DescargarCancionesBan dcb = new DescargarCancionesBan();
    private Thread hebraQuitaMarquesina;
    private static final LocalTime tiempofinal = LocalTime.parse("20:00:00");
    private boolean mostrarMarquesinas = true;

    public void iniciar(){
        int numcancion = 1;
        LocalTime lt;
        Random r = new Random();
        TratamientoMp3 gmp3 = TratamientoMp3VideoJuegos.getInstance();
        TratamientoMp3 gmp3ansiedad = TratamientoMp3Ansiedad.getInstance();
        try{
            comprobarCarpetaTemporal();
            d.obtener();
            dcb.obtener();
          
            gmp3.iniciarBusqueda();
            gmp3ansiedad.iniciarBusqueda();
            //vamos a esperar a que al menos este una cancion y los textos
            while(gmp3.obtener_tamano_canciones() <= 0 && gmp3ansiedad.obtener_tamano_canciones() <= 0){
                //esperamos 1 segundo
                Thread.sleep(1000);
            }
            if(Main.isIsProduction()){
            	inicializarFirefox();
                Constantes.esperar(15000);
                inicializarVentana();
                inicializarGrabacion();
            }
            else{
                inicializarVentana();
            }
            ventana.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                      forzarCierre();
                }
            });
           
            indicarEstadoTransmision(Constantes.INICIANDO_TRANSMISION,Constantes.SUSCRIBETEINICIO,Constantes.TIEMPO_ARRANQUE);
            //vamos a poner una ventana inicial que inicializa la emision y que dura unos 20 seg
            //se supone que con esto, dará tiempo a que se obtenga la hora actual
            do{
                try{
                        if(numcancion%Constantes.CANCIONES_ESPERA_MENSAJES==0){
                             d.reset();
                             int intentos = 0;
                             while(intentos < 20) {
                            	 d.obtener();
                            	 try {
                            		 Thread.sleep(500);
                            	 }
                            	 catch(InterruptedException e) {}
                            	 String cadena = d.dameMensajeAleatorio();
                            	 if(!d.isEnEjecucion() && cadena!=null && !cadena.isEmpty()) {
                            		 break;
                            	 }
                            	 intentos++;
                             }
                             
                             if(intentos >= 20) {
                            	 //posiblemente no haya Internet, forzamos un reinicio
                            	 forzarCierre();
                             } 
                        }
                        
                        if(numcancion%Constantes.CANCIONES_ESPERA_CANCIONES==0){
                            dcb.reset();
                            dcb.obtener();
                            numcancion = 0;
                       }
                        //todos los ciclos son igual, reproducir cancion y buscar siguiente cancion mientras se reproduce, al finalizar cancion, borrar cancion
                        if(gmp3.obtener_tamano_canciones() > 0 || gmp3ansiedad.obtener_tamano_canciones() > 0){
                            int valor = r.nextInt(10);
                            Cancion cancionactual = null;
                            if(valor > 8) {
                            	mostrarMarquesinas = false;
                            	cancionactual = gmp3ansiedad.getProximacancion();
                            	gmp3ansiedad.iniciarBusqueda();
                            }
                            else {
                            	cancionactual = gmp3.getProximacancion();
                            	gmp3.iniciarBusqueda();
                            	mostrarMarquesinas = true;
                            }
                            
                            reproducirCancion(cancionactual);
                            cancionactual.borrarFichero();
                            cambiarFondo();
                            numcancion++;
                        }
                    }
                    catch(Exception e){
                        Logger.getInstance().outString(e.getMessage());
                    }
                lt = LocalTime.now();
                }
            while(lt.isBefore(tiempofinal));
            indicarEstadoTransmision(Constantes.FINALIZANDO_TRANSMISION,Constantes.SUSCRIBETE,Constantes.TIEMPO_ARRANQUE);
        }
        catch(InterruptedException | IOException | AWTException e){
             Logger.getInstance().outString(e.getMessage());
        }
        finally{
           forzarCierre();
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
            Fichero f1 = Fichero.BG1;
            fondo = new JLabel(new ImageIcon(f1.getRuta()));
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
            textocancion.setBounds(0, 290, 320, 25);
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
            textoalbum.setBounds(0, 260, 320, 25);
            textoalbum.setHorizontalAlignment(SwingConstants.LEFT);
            Border border = BorderFactory.createLineBorder(Color.BLACK, 2);
            textoalbum.setBorder(border);
        }
        return textoalbum;
    }



    private void apagarEquipo() throws IOException, InterruptedException {
        Logger.getInstance().outString("Se procede a reiniciar el equipo");
        if(!Main.isIsProduction()){
            System.exit(0);
        }
        Process p;
        if(Constantes.windowsOrLinux()){
            p = Runtime.getRuntime().exec("shutdown /r");
        }
        else{
            p = Runtime.getRuntime().exec("sudo shutdown -r now");
        }
        p.waitFor(10, TimeUnit.SECONDS);
    }
 
    
    private int dimeTamanoPixelesTexto(String texto){
        if(texto==null || texto.isEmpty()){
            return 7;
        }
        if(texto.length() < 30){
            return 8;
        }
        if(texto.length() < 50){
            return 7;
        }
        if(texto.length() < 70){
            return 6;
        }
        return 4;
    }

    private void actualizarCancionVentana(Cancion cancionactual) {
        cancionactual.setNombre_album(Jsoup.parse(cancionactual.getNombre_album()).text().trim());
        cancionactual.setNombre_cancion(Jsoup.parse(cancionactual.getNombre_cancion()).text().trim());
        int cancion_tamano=dimeTamanoPixelesTexto(cancionactual.getNombre_cancion());
        int album_tamano=dimeTamanoPixelesTexto(cancionactual.getNombre_album());
        
        ventana.setLayout(null);
        ventana.add(getTextocancion());
        ventana.add(getTextoalbum());
        getTextocancion().setVisible(mostrarMarquesinas);
        getTextoalbum().setVisible(mostrarMarquesinas);
        getTextocancion().setHorizontalAlignment(SwingConstants.LEFT);
        String album = "<html><span style=\"font-family:Arial;font-size:11px;\"><b> &nbsp;A: </b></span><span style=\"font-size:" + album_tamano + "px\"> " + cancionactual.getNombre_album() + "</span></html>";
        getTextoalbum().setText(album);
        String cancion = "<html><span style=\"font-family:Arial;font-size:11px;\"><b> &nbsp;C: </b></span><span style=\"font-size:" + cancion_tamano + "px\"> " + cancionactual.getNombre_cancion() + "</span></html>";
        getTextocancion().setText(cancion);
        ventana.add(getMarquesina());
        String textomarquesina = d.dameMensajeAleatorio();
        if(textomarquesina==null || textomarquesina.isEmpty()) {
        	getMarquesina().setVisible(false);
        }
        else{
        	if(hebraQuitaMarquesina!=null && hebraQuitaMarquesina.isAlive()) {
        		hebraQuitaMarquesina.interrupt();
        	}
        	getMarquesina().setVisible(true);
        	getMarquesina().setText(d.dameMensajeAleatorio());
        	final Date fechainicio = new Date();
        	hebraQuitaMarquesina = new Thread(new Runnable() {
				
				@Override
				public void run() {
				    Date fechafin = new Date();
				    while(fechafin.getTime()-fechainicio.getTime() < Constantes.MAX_TIEMPO_MARQUESINA) {
				    	try {
				    		Thread.sleep(1000);
				    	}
				    	catch(InterruptedException e) {}
				    	fechafin = new Date();
				    }
				    getMarquesina().setVisible(false);
				}
			});
        	hebraQuitaMarquesina.start();
        	
        }
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
        getTextoalbum().setVisible(false);
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

    private void forzarCierre() {
         if(Main.isIsProduction()){
            try{
                apagarEquipo();
            }
            catch(Exception e1){}
        }
    }

    private Marquesina getMarquesina(){
        if(marquesina==null){
            marquesina = new Marquesina("",Marquesina.RIGHT_TO_LEFT,10);
            Font font = new Font("SansSerif", Font.BOLD, 15);
            marquesina.setFont(font);
            marquesina.setBackground(Color.WHITE);
            marquesina.setOpaque(true);
            marquesina.setBounds(50, 20, 540, 20);
        }
        return marquesina;
    }
    
    public static DescargarCancionesBan getDescargarCancionesBan() {
    	return dcb;
    }
    
    private void inicializarFirefox() throws IOException, InterruptedException {
        Process p;
        p = Runtime.getRuntime().exec("firefox " + Constantes.URL_STREAMING);
        p.waitFor(10, TimeUnit.SECONDS);
    }
    
    private void inicializarGrabacion() throws AWTException {
        Logger.getInstance().outString(Constantes.INICIANDO_TRANSMISION);  
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_F6);
        robot.delay(500);
        robot.keyRelease(KeyEvent.VK_F6);
    }

}
