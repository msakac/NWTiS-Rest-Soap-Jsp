package org.foi.nwtis.msakac.aplikacija_5.pomagala;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;

/**
 * Klasa Pomagala.
 */
public class Pomagala {

	/**
	 * Metoda prima komandu, adresu i port za posluzitelj. Kreira vezu prema adresi i portu te otvara citac i pisac. Pisac upisuje komandu te ju
	 * salje posluzitelju. Od posluzitelja se natrag dobiva odgovor koji se vraća.
	 *
	 * @param String komanda
	 * @param String adresa
	 * @param int port
	 * @return String odgovor
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
