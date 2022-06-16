package org.foi.nwtis.msakac.aplikacija_3.pomagala;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;

import org.foi.nwtis.msakac.aplikacija_3.podaci.Token;
import org.foi.nwtis.msakac.aplikacija_3.podaci.TokeniDAO;
import org.foi.nwtis.msakac.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import jakarta.ws.rs.core.Response;

/**
 * Klasa Pomagala.
 */
public class Pomagala {

	/**
	 * Metoda provjerava dali token koji je dosao kao argument još vrijedi odnosno 
	 * da li je vrijeme tokena još veće od trenutnog vremena sustava.
	 *
	 * @param Token token
	 * @return true, ako token vrijedi
	 */
	public static boolean tokenVrijedi(Token token) {
		long trenutnoVrijeme = (System.currentTimeMillis() + 7200000)/1000;
		long vrijemeTokena = token.getVrijediDo();
		System.out.println("Vrijeme posluzitelja: " + trenutnoVrijeme);
		System.out.println("Vrijeme tokena: " + token.getVrijediDo());
		if(token.isStatus() && trenutnoVrijeme < vrijemeTokena) {
			return true;
		}
		return false;
	}
	
	/**
	 * Metoda prima korisnicko ime i zeton te dohvaca token iz baze podataka. AKo je token dohvacen tada se provjerava da li token još vrijedi.
	 * Metoda vraća Response sa HTTP statusom koji može biti 401 ako token ne postoji ili 408 ako token ne vrijedi. U suprotnom se vraća prazan objekt odnosno null vrijednost.
	 *
	 * @param String korisnik
	 * @param int zeton
	 * @param PostavkeBazaPodataka pbp
	 * @return Response response ili null ako je autoriziran
	 */
	public static Response autorizacija(String korisnik, int zeton, PostavkeBazaPodataka pbp) {
		//Dohvacam token
		Token token = TokeniDAO.dohvatiTokenPremaTokenu(korisnik, zeton, pbp);
		//Ako je token null znaci da nije od korisnika ili ne postoji
		if(token == null)
			return Response.status(Response.Status.UNAUTHORIZED).entity("Token "+zeton+" nije od korisnika "+korisnik).build();
		//Provjera dali token još vrijedi
		if(!Pomagala.tokenVrijedi(token)) 
			return Response.status(Response.Status.REQUEST_TIMEOUT).entity("Token "+token.getId()+" nije vazeci").build();
		//Ako vrati null znaci da je korisnik autoriziran
		return null;
	}
	
	/**
	 * Metoda otvara pisac i citac prema adresi i portu te salje komandu te ceka odgovor posluzitelja.
	 *
	 * @param String komanda
	 * @param String adresa
	 * @param int port
	 * @return String odgovor posluzitelja
	 */
	public static String posaljiKomandu(String komanda, String adresa, int port) {
		try {
			InetSocketAddress isa = new InetSocketAddress(adresa, port);
			Socket veza = new Socket();
			veza.connect(isa, 900);

			InputStreamReader isr = new InputStreamReader(veza.getInputStream(), Charset.forName("UTF-8"));
			OutputStreamWriter osw = new OutputStreamWriter(veza.getOutputStream(), Charset.forName("UTF-8"));

			osw.write(komanda);
			osw.flush();
			veza.shutdownOutput();
			StringBuilder tekst = new StringBuilder();
			while (true) {
				int i = isr.read();
				if (i == -1) {
					break;
				}
				tekst.append((char) i);
			}
			veza.shutdownInput();
			veza.close();
			return tekst.toString();
			
			
		} catch (SocketException e) {
			return "ERROR: Problem pri spajanju na server";
		} catch (IOException ex) {
			return "ERROR :Problem pri spajanju na server";
		}
	}
	
}
