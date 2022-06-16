package org.foi.nwtis.msakac.projekt.aplikacija_1;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.foi.nwtis.podaci.Aerodrom;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

// TODO: Auto-generated Javadoc
/**
 * The Class DretvaZahtjeva.
 */
public class DretvaZahtjeva extends Thread {

	/** Socket veza */
	private Socket veza = null;

	/** Server server */
	private Server server = null;


	/** DecimalFormat df */
	private static DecimalFormat df = new DecimalFormat("0.0");

	/** Regex izrazi za provjeru naredbe */
	private final String[] regexIzrazi = { "^STATUS$", "^QUIT$", "^INIT$", "^LOAD [\\s\\S]*$",
			"^DISTANCE ([A-Z]{4}) ([A-Z]{4})$", "^CLEAR$", };

	/**
	 * Kreira instancu DretvaZahtjeva
	 *
	 * @param veza Socket veza od servera
	 * @param server Server objekt servera
	 */
	public DretvaZahtjeva(Socket veza, Server server) {
		super();
		this.veza = veza;
		this.server = server;
	}

	/**
	 * Start.
	 */
	@Override
	public synchronized void start() {
		super.start();
	}

	/**
	 * Metoda otvara čitač i pisač. Čitač prima naredbu koja je poslana te obrađuje tu naredbu te vraća odgovor. 
	 * Zatim se korisniku vraća odgovor ili se prekida rad poslužitelja.
	 */
	@Override
	public void run() {
		try (InputStreamReader isr = new InputStreamReader(this.veza.getInputStream(), Charset.forName("UTF-8"));
				OutputStreamWriter osw = new OutputStreamWriter(this.veza.getOutputStream(),
						Charset.forName("UTF-8"));) {
			StringBuilder naredba = new StringBuilder();
			while (true) {
				int i = isr.read();
				if (i == -1) {
					break;
				}
				naredba.append((char) i);
			}

			System.out.println("Primljena naredba: " + naredba.toString());
			this.veza.shutdownInput();

			String odgovor = obradiZahtjev(naredba.toString());

			if (odgovor == "QUIT") {
				osw.write("OK");
				osw.flush();
				this.veza.shutdownOutput();
				System.out.println("Prekidam rad poslužitelja..");
				System.exit(0);
			}

			osw.write(odgovor);
			osw.flush();
			this.veza.shutdownOutput();

			int brojAktivnihDretvi = this.server.getBrojAktivnihDretvi();
			this.server.setBrojAktivnihDretvi(brojAktivnihDretvi - 1);

		} catch (IOException e) {
			System.out.println("ERROR 14: Greška prilikom obrade zahtjeva");
		}
		super.run();
	}

	/**
	 * Metoda obrađuje zahtjev. 
	 * Provjerava format naredbe te ako je format ispravan šalje na obradu.
	 *
	 * @param naredba String naredba 
	 * @return String odgovor
	 */
	private String obradiZahtjev(String naredba) {
		String odgovor = "";
		Matcher matcher = provjeriNaredbu(naredba);
		if (matcher == null) {
			return "ERROR 14: Format komande nije ispravan";
		}
		odgovor = obradiNaredbu(matcher.group());
		return odgovor;
	}

	/**
	 * Metoda na temelju naredbe zove funkciju koja obrađuje tu naredbu.
	 *
	 * @param naredba String naredba
	 * @return String odgovor
	 */
	private String obradiNaredbu(String naredba) {
		String[] podaci = naredba.split(" ");
		String komanda = podaci[0];
		String odgovor = "";
		if (komanda.equals("STATUS")) {
			odgovor = "OK " + this.server.getStatusPosluzitelja();
		} else if (komanda.equals("QUIT")) {
			odgovor = "QUIT";
		} else if (komanda.equals("INIT")) {
			odgovor = obradiNaredbuInit();
		} else if (komanda.equals("LOAD")) {
			odgovor = obradiNaredbuLoad(naredba);
		} else if (komanda.equals("DISTANCE")) {
			odgovor = obradiNaredbuDistance(podaci);
		} else if (komanda.equals("CLEAR")) {
			odgovor = obradiNaredbuClear();
		}

		return odgovor;
	}

	/**
	 * Metoda obrađuje naredbu clear na način da briše sve iz liste aerodroma te postavlja status poslužitelja na 0.
	 *
	 * @return String odgovor
	 */
	private String obradiNaredbuClear() {
		String odgovor = statusPosluzitelja();
		if (odgovor == null) {
			this.server.listaAerodroma.clear();
			this.server.setStatusPosluzitelja(0);
			odgovor = "OK";
		}
		return odgovor;
	}

	/**
	 * Metoda obrađuje naredbu DISTANCE na način da izračuna udaljenost između tih aerodroma na temelju njihovih lokacija.
	 *
	 * @param String[] podaci
	 * @return String odgovor
	 */
	private String obradiNaredbuDistance(String[] podaci) {
		String odgovor = statusPosluzitelja();
		if (odgovor == null) {
			String icao1 = podaci[1];
			String icao2 = podaci[2];
			Aerodrom aerodrom1 = null;
			Aerodrom aerodrom2 = null;
			double udaljenost = 0;

			// Trazim podatke za oba dva aerodroma
			for (Aerodrom a : this.server.listaAerodroma) {
				if (a.getIcao().equals(icao1))
					aerodrom1 = a;
				if (a.getIcao().equals(icao2))
					aerodrom2 = a;
			}
			if (aerodrom1 == null) {
				odgovor = "ERROR 11: Aerodrom " + icao1 + " ne postoji u podacima!";
				if (aerodrom2 == null)
					odgovor = "ERROR 13: Aerodromi " + icao1 + " i " + icao2 + " ne postoji u podacima!";
			} else if (aerodrom2 == null) {
				odgovor = "ERROR 12: Aerodrom " + icao2 + " ne postoji u podacima!";
			}
			if (odgovor != null)
				return odgovor;

			udaljenost = izracunajUdaljenost(aerodrom1, aerodrom2);
			odgovor = "OK "+df.format(udaljenost);
		}
		return odgovor;
	}

	/**
	 * Metoda izvršava naredbu LOAD na način da deserijalizira JSON podatke te ih sprema u listuAerodroma.
	 *
	 * @param String naredba
	 * @return String odgovor
	 */
	private String obradiNaredbuLoad(String naredba) {
		if (this.server.getStatusPosluzitelja() == 0) {
			return "ERROR 01: Poslužitelj hibernira";
		} else if (this.server.getStatusPosluzitelja() == 2) {
			return "ERROR 03: Poslužitelj je aktivan!";
		}
		this.server.setStatusPosluzitelja(2);
		Gson gson = new Gson();
		String[] podaci = naredba.split("LOAD ");
		this.server.listaAerodroma = gson.fromJson(podaci[1], new TypeToken<List<Aerodrom>>() {
		}.getType());
		return "OK " + this.server.listaAerodroma.size();
	}

	/**
	 * Metoda obrađuje naredbu INIT na način da postavlja status poslužitelja na 1.
	 *
	 * @return String odgovor
	 */
	private String obradiNaredbuInit() {
		if (this.server.getStatusPosluzitelja() == 1) {
			return "ERROR 02: Poslužitelj je inicijaliziran!";
		} else if (this.server.getStatusPosluzitelja() == 2) {
			return "ERROR 03: Poslužitelj je aktivan!";
		}
		this.server.setStatusPosluzitelja(1);
		return "OK";
	}

	/**
	 * Pomoćna metoda koja provjerava status poslužitelja te vraća grešku ili null ako nema greške.
	 *
	 * @return String greška
	 */
	private String statusPosluzitelja() {
		int trenutniStatus = this.server.getStatusPosluzitelja();
		if (trenutniStatus == 0) {
			return "ERROR 01: Poslužitelj hibernira";
		} else if (trenutniStatus == 1) {
			return "ERROR 02: Poslužitelj je inicijaliziran";
		}
		return null;
	}

	/**
	 * Metoda sa provjerava dali se naredba podudara sa zadanim regexima. Ako se podudara vraća instacu Matcher-a.
	 *
	 * @param String naredba
	 * @return Matcher matcher
	 */
	private Matcher provjeriNaredbu(String naredba) {
		Pattern pattern = Pattern.compile("");
		Matcher matcher = pattern.matcher("");
		boolean naredbaIspravna = false;
		// Iteriram se kroz sve izraze i provjeram dal se koji podudara
		for (String regex : this.regexIzrazi) {
			if (!naredbaIspravna) {
				pattern = Pattern.compile(regex);
				matcher = pattern.matcher(naredba);
				if (matcher.matches()) {
					naredbaIspravna = true;
					return matcher;
				}
				matcher.reset();
			}

		}
		return null;
	}

	/**
	 * Metoda izračunava udaljenost između dva aerodroma te u obzir uzima zakrivljenost zemlje.
	 *
	 * @param Aerodrom aerodrom1
	 * @param Aerodrom aerodrom2
	 * @return Double udaljenost
	 */
	private double izracunajUdaljenost(Aerodrom aerodrom1, Aerodrom aerodrom2) {
		double icao1GpsGS = Double.parseDouble(aerodrom1.getLokacija().getLatitude());
		double icao1GpsGD = Double.parseDouble(aerodrom1.getLokacija().getLongitude());
		double icao2GpsGS = Double.parseDouble(aerodrom2.getLokacija().getLatitude());
		double icao2GpsGD = Double.parseDouble(aerodrom2.getLokacija().getLongitude());

		double RadIcao1GpsGS = Math.toRadians(icao1GpsGS);
		double RadIcao1GpsGD = Math.toRadians(icao1GpsGD);

		double RadIcao2GpsGS = Math.toRadians(icao2GpsGS);
		double RadIcao2GpsGD = Math.toRadians(icao2GpsGD);

		double dGS = RadIcao2GpsGS - RadIcao1GpsGS;
		double dGD = RadIcao2GpsGD - RadIcao1GpsGD;

		double a = Math.pow(Math.sin(dGD / 2), 2)
				+ Math.cos(RadIcao1GpsGD) * Math.cos(RadIcao2GpsGD) * Math.pow(Math.sin(dGS / 2), 2);

		double c = 2 * Math.asin(Math.sqrt(a));

		double radijusZemlje = 6371.137;

		double udaljenost = c * radijusZemlje;
		return udaljenost;
	}

	/**
	 * Interrupt.
	 */
	@Override
	public void interrupt() {
		// TODO Auto-generated method stub
		super.interrupt();
	}

}
