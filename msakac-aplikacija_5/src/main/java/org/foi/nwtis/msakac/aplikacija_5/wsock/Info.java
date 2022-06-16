package org.foi.nwtis.msakac.aplikacija_5.wsock;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import jakarta.websocket.CloseReason;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

/**
 * KLasa Info.
 */
@ServerEndpoint("/info")
public class Info {
	
	/** Set sesija */
	private Set<Session> sesije = new HashSet<Session>();
	
	/**
	 * Metoda otvara sesiju
	 *
	 * @param sesija 
	 * @param konfig 
	 */
	@OnOpen
	public void otvori(Session sesija, EndpointConfig konfig) {
		sesije.add(sesija);
		System.out.println("Otvorena sesija: "+sesija.getId());
	}
	
	/**
	 * Metoda zatvara sesiju
	 *
	 * @param sesija
	 * @param razlog
	 */
	@OnClose
	public void zatvori(Session sesija, CloseReason razlog) {
		sesije.remove(sesija);
		System.out.println("Zatvorena sesija: "+sesija.getId()
							+" razlog: "+razlog.getReasonPhrase());
	}
	
	/**
	 * Metoda prikazuje da je stigla poruka
	 *
	 * @param sesija
	 * @param poruka 
	 */
	@OnMessage
	public void stiglaPoruka(Session sesija, String poruka) {
		System.out.println("Sesija: "+sesija.getId()+" poruka: "+poruka);
	}
	
	/**
	 * Metoda pokazuje da je doslo do pogreske
	 *
	 * @param sesija
	 * @param greska
	 */
	@OnError
	public void greska(Session sesija, Throwable greska) {
		System.out.println("Sesija: "+sesija.getId()+" greska: "+greska.getMessage());
	}
	
	/**
	 * Metoda informira korisnike
	 *
	 * @param poruka
	 */
	public void informiraj (String poruka) {
		for(Session s : sesije) {
			if(s.isOpen()) {
				try {
					s.getBasicRemote().sendText(poruka);
				} catch (IOException e) {
					System.out.println("Sesija: "+s.getId()+" greska: "+e.getMessage());
				}
			}
		}
	}
	
}
