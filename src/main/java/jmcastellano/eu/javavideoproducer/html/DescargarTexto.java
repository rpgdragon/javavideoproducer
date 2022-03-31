package jmcastellano.eu.javavideoproducer.html;

import java.net.URL;
import java.util.Scanner;

public abstract class DescargarTexto {

	private final String urlLink;

	private boolean enEjecucion;

	public DescargarTexto(String urlLink) {
		this.urlLink = urlLink;
	}

	public void obtener() {
		if(!hayDatosPrevios()){
			enEjecucion = true;
			Thread t = new Thread(() -> {
				try {
					URL url = new URL(urlLink);
					Scanner s = new Scanner(url.openStream(),"UTF-8");
					while(s.hasNextLine()){
						String cad = s.nextLine();
						tramitarDescarga(cad);
					}
				} catch(Exception e){}
				finally{
					enEjecucion = false;
				}
			});
			t.start();
		}
	}

	public abstract boolean hayDatosPrevios();

	public abstract void reset();

	public abstract void tramitarDescarga(String linea);

	public boolean isEnEjecucion() {
		return enEjecucion;
	}

	public void setEnEjecucion(boolean enEjecucion) {
		this.enEjecucion = enEjecucion;
	}



}
