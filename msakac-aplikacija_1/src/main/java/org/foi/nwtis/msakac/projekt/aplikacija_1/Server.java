package org.foi.nwtis.msakac.projekt.aplikacija_1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.foi.nwtis.msakac.vjezba_03.konfiguracije.Konfiguracija;
import org.foi.nwtis.msakac.vjezba_03.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.msakac.vjezba_03.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.podaci.Aerodrom;

import lombok.Getter;
import lombok.Setter;

// TODO: Auto-generated Javadoc
/**
 * Klasa Server
 */
public class Server {

	/** Port */
	private int port;
	
	/** Broj dretvi */
	private int brojDretvi;
	
	/** Veza */
	private Socket veza = null;
	
	/** Broj aktivnih dretvi */
	@Getter
	@Setter
	private int brojAktivnihDretvi = 0;
	
	/** Broj aktivnih dretvi */
	@Getter
	@Setter
	private int statusPosluzitelja = 0;

	/** Lista aerodroma */
	public List<Aerodrom> listaAerodroma = null;
	
	/**
	 * Kreira instacu klase Server
	 *
	 * @param int port 
	 * @param int broj dretvi
	 */
	public Server(int port, int brojDretvi) {
		this.port = port;
		this.brojDretvi = brojDretvi;
	}
	
	/**
	 * Metoda main čita konfiguracijske podatke, provjerava njihovu vrijednost, kreira novu instancu servera te pokreće obradu
	 *
	 * @param Argumenti
	 */
	public static void main(String[] args) {
		int port = -1;
		int brojDretvi = -1;
		
		if(args.length != 1) {
			System.out.println("ERROR 14: Parametar mora biti naziv konfiguracijske datoteke formata <naziv.ekstenzija>");
			return;
		}
		
		Konfiguracija konfig = ucitajKonfiguraciju(args[0]);
		try {
			port = Integer.parseInt(konfig.dajPostavku("port"));
			brojDretvi = Integer.parseInt(konfig.dajPostavku("broj.dretvi"));
		} catch (Exception e) {
			String[] pogresnaVrijednost = e.getMessage().split("\"");
			ispisiPogreskuIPrekiniRad("ERROR 14: Vrijednost postavke '" + pogresnaVrijednost[1] + "' je neispravna.");
		}
		
		provjeriVrijednosti(port, brojDretvi);
		
		System.out.println("\nServer se podigao na portu " + port);
		
		Server server = new Server(port, brojDretvi);
		
		server.pokreniObradu();
	}

	/**
	 * Metoda pokreće obradu. Za svaki novog korisnika kreira se nova dretvaZahtjeva gdje se dalje obraduje zahtjeva.
	 * Ukoliko je postignut maksimalan broj dretvi ispisuje se greška.
	 */
	private void pokreniObradu() {
		System.out.println("\nČekam korisnika...");
		try(ServerSocket server = new ServerSocket(this.port);){
			while(true) {
				this.veza = server.accept();
				
				while(this.brojAktivnihDretvi >= this.brojDretvi) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						System.out.println("ERROR 14: Greška kod jednodretvenog rada");
					}
					System.out.println("ERROR 14: Dostignut maksimalan broj aktivnih dretvi");
				}
				DretvaZahtjeva dz = new DretvaZahtjeva(veza, this);
				dz.start();
			
				this.brojAktivnihDretvi++;
				System.out.println("Dretva pokrenuta.. | Aktivno: " + this.brojAktivnihDretvi);

				
			}
			
		} catch (IOException e) {
			System.out.println("ERROR 14: Problem kod kreiranja dretve");
		}
		
	}

	/**
	 * Metoda učitava konfiguraciju te provjerava format i podatke konfiguracije.
	 *
	 * @param String nazviDatoteke
	 * @return Konfiguracija konfiguracija
	 */
	public static Konfiguracija ucitajKonfiguraciju(String nazivDatoteke) {
		Konfiguracija konfig = null;
		Pattern pNazivDatoteke = Pattern.compile(".{1,}[.]{1}[a-zA-Z]{1,}");
		Matcher mNazivDatoteke = pNazivDatoteke.matcher(nazivDatoteke);

		if (!mNazivDatoteke.matches()) {
			ispisiPogreskuIPrekiniRad("ERROR 14: Format konfiguracijske datoteke mora biti <naziv.ekstenzija>");
		}

		try {
			konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
		} catch (NeispravnaKonfiguracija e) {
			System.out.println("ERROR 14: " + e.getMessage());
			System.exit(0);
		}
		if (!konfig.postojiPostavka("port") || !konfig.postojiPostavka("broj.dretvi")) {
			ispisiPogreskuIPrekiniRad("ERROR 14: Konfiguracijska datoteka ne sadrži potrebne postavke");
		}
		return konfig;
	}
	
	/**
	 * Pomoćna metoda provjerava vrijednosti konfiguracije.
	 *
	 * @param int port
	 * @param int broj dretvi
	 */
	public static void provjeriVrijednosti(int port, int brojDretvi) {
		// Provjera da su port izmedu 8000 i 9999
		if (port < 8000 || port > 9999) {
			ispisiPogreskuIPrekiniRad("ERROR 14: Port servera mora biti između 8000 i 9999");
		}
		// Provjera da broj dretvi veci od 0
		if (brojDretvi < 1) {
			ispisiPogreskuIPrekiniRad("ERROR 14: Broj dertvi mora biti veći od 0");
		}
	}
	
	/**
	 * Metoda ispisuje pogrešku te prekida rad poslužitelja
	 *
	 * @param String poruka greske
	 */
	public static void ispisiPogreskuIPrekiniRad(String porukaGreske) {
		System.out.println(porukaGreske);
		System.exit(1);
	}
}
