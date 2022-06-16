package org.foi.nwtis.msakac.aplikacija_2.dretve;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.foi.nwtis.msakac.aplikacija_2.podaci.AerodromDolasciDAO;
import org.foi.nwtis.msakac.aplikacija_2.podaci.AerodromPolasciDAO;
import org.foi.nwtis.msakac.aplikacija_2.podaci.AerodromPraceniDAO;
import org.foi.nwtis.msakac.aplikacija_2.podaci.AerodromProblemiDAO;
import org.foi.nwtis.msakac.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.klijenti.NwtisRestIznimka;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;

/**
 * Klasa PreuzimanjePodataka.
 */
public class PreuzimanjePodataka extends Thread {

	/** ciklus vrijeme. */
	private long ciklusVrijeme;
	
	/** ciklus korekcija. */
	private int ciklusKorekcija;
	
	/** preuzimanje odmak. */
	private long preuzimanjeOdmak;
	
	/** preuzimanje pauza. */
	private int preuzimanjePauza;
	
	/** preuzimanje od. */
	private long preuzimanjeOd;
	
	/** preuzimanje do. */
	private long preuzimanjeDo;
	
	/** preuzimanje vrijeme. */
	private int preuzimanjeVrijeme;
	
	/** openSkyNetwork korisnik. */
	private String osnKorisnik;
	
	/** openSkyNetwork lozinka. */
	private String osnLozinka;

	/** OpenSky klijent. */
	private OSKlijent osKlijent;

	/** postavke. */
	private PostavkeBazaPodataka postavke;
	
	/** radi. */
	private boolean radi = true;

	/**
	 * Instancira novu klasu odnosno dretvu PreuzimanjePodataka
	 *
	 * @param PostavkeBazaPodataka postavke
	 */
	public PreuzimanjePodataka(PostavkeBazaPodataka postavke) {
		super();
		this.postavke = postavke;
	}

	/**
	 * Metoda dohvaca sve postavke. Zatim kreira novog Open Sky klijenta te pokreće dretvu.
	 */
	@Override
	public synchronized void start() {
		postaviPostavke();
		this.osKlijent = new OSKlijent(osnKorisnik, osnLozinka);
		super.start();
	}

	/**
	 * Metoda preuzima podatake za dolaske i polaske aerodroma. 
	 * Svaki ciklus ima svoje trajanje i vrijeme od kad do kad se preuzimaju podaci
	 * te se nakon svakog preuzimanja dogada pauza.
	 * Nakon svakog ciklusa izracunava se efektivno vrijeme te se povecava ciklus i virtualni brojac po potrebi.
	 * Postoji ciklus korekcije u kojemu se radi korekcija vremena spavanja.
	 */
	@Override
	public void run() {
		while (radi) {
			AerodromPraceniDAO apdao = new AerodromPraceniDAO();
			long vrijemeOd = this.preuzimanjeOd;
			int brojacCiklusa = 0;
			int virtualniBrojacCiklusa = 0;
			long vrijemeDo = this.preuzimanjeOd + this.preuzimanjeVrijeme;
			long pocetakTimeStamp = System.currentTimeMillis();

			List<Aerodrom> aerodromiPraceni = apdao.dohvatiSvePraceneAerodrome(this.postavke);
			System.out.println("Broj pracenih aerodroma: " + aerodromiPraceni.size());

			while (vrijemeDo < this.preuzimanjeDo) {
				long pocetakCiklusa = 0;
				pocetakCiklusa = System.currentTimeMillis();
				System.out.println("...Pocetak ciklusa: "+System.currentTimeMillis());
				for (Aerodrom a : aerodromiPraceni) {
					Timestamp timestampVrijemeOd = new Timestamp(vrijemeOd);
					Timestamp timestampVrijemeDo = new Timestamp(vrijemeDo);
					// za vrijeme efektivnog rada

					System.out.println("Od: " + dohvatiDatum(vrijemeOd) + " Do: " + dohvatiDatum(vrijemeDo));

					dohvatiPolaske(a, timestampVrijemeOd, timestampVrijemeDo);
					dohvatiDolaske(a, timestampVrijemeOd, timestampVrijemeDo);

					spavaj(this.preuzimanjePauza);
				}
				// povecavam stvarni brojac
				brojacCiklusa++;
				// postavljam vrijednosti vremena za sljedeci ciklus-
				vrijemeOd = vrijemeOd + this.preuzimanjeVrijeme;
				vrijemeDo = vrijemeDo + this.preuzimanjeVrijeme;
				// izracunavam efektivno vrijeme rada
				long zavrsetakCiklusa = System.currentTimeMillis();
				System.out.println("...Zavrsetak ciklusa: "+System.currentTimeMillis());
				long vrijemeEfektivnogRada = zavrsetakCiklusa - pocetakCiklusa;
				System.out.println("...Izracun efektivnog: " + zavrsetakCiklusa + " - " + pocetakCiklusa + " = "
						+ vrijemeEfektivnogRada);
				long vrijemeSpavanja = 0;
				// Ako mi je vrijeme do vece od trenutog vremena, spavam jedan dani povecama
				// virtualni brojac
				if (vrijemeDo > System.currentTimeMillis() - this.preuzimanjeOdmak) {
					vrijemeSpavanja = 24 * 60 * 60 * 1000;
					virtualniBrojacCiklusa = virtualniBrojacCiklusa + (int) (vrijemeSpavanja / this.ciklusVrijeme);
					System.out.println("..Stigao sam do krajnjeg vremena");
				}
				// ako mi je efektivno vrijeme vece od vremena ciklusa onda trazim prvi
				// mnozitelj koji ce zadoovoljiti da je ciklus veci od rada
				else if (vrijemeEfektivnogRada > this.ciklusVrijeme) {
					for (int n = 2; n < 1000; n++) {
						if ((n * this.ciklusVrijeme) > vrijemeEfektivnogRada) {
							// povecavam virtualni brojac za n i izracunavam spavanje
							System.out.println("POVECAVAM VIRTUALNI");
							virtualniBrojacCiklusa = virtualniBrojacCiklusa + n;
							vrijemeSpavanja = (n * this.ciklusVrijeme) - vrijemeEfektivnogRada;
							break;
						}
					}
					// ako je vrijeme ciklusa vece od efektivnog samo povecam virtualni i izracunam
					// spavanje
				} else {
					virtualniBrojacCiklusa++;
					vrijemeSpavanja = ciklusVrijeme - vrijemeEfektivnogRada;
				}

				// korekcija vremenena
				if (brojacCiklusa % ciklusKorekcija == 0) {
					System.out.println("-------------------Korekcija");
					System.out.println("...Trebao sam spavati: " + vrijemeSpavanja);
					long korekcija = virtualniBrojacCiklusa * ciklusVrijeme;
					long trenutno = System.currentTimeMillis();
					long sljedeciCiklus = (long) pocetakTimeStamp + korekcija;
					System.out.println("...Sljedeci ciklus je trebao poceti: " + sljedeciCiklus);
					System.out.println("...Ja sam sad na:" + trenutno);
					vrijemeSpavanja = Math.abs(sljedeciCiklus - trenutno);
				}
				System.out.println("...Brojac: " + brojacCiklusa);
				System.out.println("...Virtualni brojac: " + virtualniBrojacCiklusa);
				System.out.println("...Efektivno sam radio: " + vrijemeEfektivnogRada);
				System.out.println("...Spavam: " + vrijemeSpavanja);
				spavaj(vrijemeSpavanja);

			}
		}
	}

	/**
	 * Interrupt
	 */
	@Override
	public void interrupt() {
		radi = false;
		super.interrupt();
	}

	/**
	 * Pomoćna metoda pokreće spavanje dretve
	 *
	 * @param long vrijemeSpavanja
	 */
	public void spavaj(long vrijemeSpavanja) {
		try {
			sleep(vrijemeSpavanja);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * Pomoćna metoda koja preuzima sve postavke te ih sprema u parametre instance
	 */
	public void postaviPostavke() {
		// vrijeme ciklusa pretvoreno iz s
		this.ciklusVrijeme = Integer.parseInt(postavke.dajPostavku("ciklus.vrijeme")) * 1000;
		// broj ciklusa nakon kojeg se radi korekcija
		this.ciklusKorekcija = Integer.parseInt(postavke.dajPostavku("ciklus.korekcija"));
		// odmak od trenutnog vremena u danima pretvoreno u ms
		this.preuzimanjeOdmak = Integer.parseInt(postavke.dajPostavku("preuzimanje.odmak")) * 24 * 60 * 60 * 1000;
		// pauza izmedu dva aerodrom u ms
		this.preuzimanjePauza = Integer.parseInt(postavke.dajPostavku("preuzimanje.pauza"));
		// datum od kojeg se pocine preuzimanje pretvoren u epoch vrijeme zatim u ms
		this.preuzimanjeOd = dohvatiEpoch(postavke.dajPostavku("preuzimanje.od"));
		// datum do kojeg se provodi preuzimanje pretvoren u epoch zatim u ms
		this.preuzimanjeDo = dohvatiEpoch(postavke.dajPostavku("preuzimanje.do"));
		// vrijeme za koje se preuzimaju podacu u jednom ciklusu u satima pretvoren u ms
		this.preuzimanjeVrijeme = Integer.parseInt(postavke.dajPostavku("preuzimanje.vrijeme")) * 60 * 60 * 1000;
		// podaci za opensky network
		this.osnKorisnik = postavke.dajPostavku("OpenSkyNetwork.korisnik");
		this.osnLozinka = postavke.dajPostavku("OpenSkyNetwork.lozinka");
	}

	/**
	 * Pomocna metoda dohvaca epoch vrijeme na temelju datuma.
	 *
	 * @param String datum
	 * @return long epoch vrijeme
	 */
	public long dohvatiEpoch(String datum) {
		datum += " 00:00:00";
		System.out.println("Datum " + datum);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		Date date = null;
		try {
			date = dateFormat.parse(datum);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long epoch = date.getTime();
		System.out.println("Epoch vrijeme: " + epoch);
		return epoch;
	}
	
	/**
	 * Pomocna metoda koja dohvaca datum iz epocha.
	 *
	 * @param epoch the epoch
	 * @return the string
	 */
	public String dohvatiDatum(long epoch) {
		Date datum = new Date(epoch);
		return datum.toString();
	}

	/**
	 * Metoda dohvaca sve polaske aerodroma a u vremenu od do pomoću open sky network klijenta.
	 * Polasci se dodaju u listu te se zatim te se na kraju iteracije poziva metoda klase DAO
	 * koja sve podatke sprema u bazu.
	 *
	 * @param Aerodrom a
	 * @param Timestamp timestampVrijemeOd
	 * @param Timestamp timestampVrijemeDo
	 */
	private void dohvatiPolaske(Aerodrom a, Timestamp timestampVrijemeOd, Timestamp timestampVrijemeDo) {
		AerodromPolasciDAO apdao = new AerodromPolasciDAO();
		List<AvionLeti> avioniPolasci;
		List<AvionLeti> polasciZaDodavanje = new ArrayList<AvionLeti>();
		try {
			avioniPolasci = osKlijent.getDepartures(a.getIcao(), timestampVrijemeOd, timestampVrijemeDo);
			if (avioniPolasci != null) {
				System.out.println("Polasci s aerodroma: " + a.getIcao() + " Broj letova: " + avioniPolasci.size());
				for (AvionLeti avion : avioniPolasci) {
					// ako je razlicit od null dodaje u novu listu koja ide u DAO
					if (avion.getEstArrivalAirport() != null) {
						polasciZaDodavanje.add(avion);
					}
				}
				apdao.dodajSvePolaske(polasciZaDodavanje, postavke);
			}
		} catch (NwtisRestIznimka e) {
			AerodromProblemiDAO aproblemiDao = new AerodromProblemiDAO();
			aproblemiDao.dodajProblem(a.getIcao(), e.getMessage(), postavke);
			//System.out.println("Poruka: " + e.getMessage());
		}

	}

	/**
	 * Metoda dohvaca sve dolaske aerodroma a u vremenu od do pomoću open sky network klijenta.
	 * Polasci se dodaju u listu te se zatim te se na kraju iteracije poziva metoda klase DAO
	 * koja sve podatke sprema u bazu.
	 *
	 * @param Aerodrom a
	 * @param Timestamp timestampVrijemeOd
	 * @param Timestamp timestampVrijemeDo
	 */
	private void dohvatiDolaske(Aerodrom a, Timestamp timestampVrijemeOd, Timestamp timestampVrijemeDo) {
		AerodromDolasciDAO addao = new AerodromDolasciDAO();
		List<AvionLeti> avioniDolasci;
		List<AvionLeti> dolasciZaDodavanje = new ArrayList<AvionLeti>();
		try {
			avioniDolasci = osKlijent.getArrivals(a.getIcao(), timestampVrijemeOd, timestampVrijemeDo);
			if (avioniDolasci != null) {
				System.out.println("Dolasci na aerodrom: " + a.getIcao() + " Broj letova: " + avioniDolasci.size());
				for (AvionLeti avion : avioniDolasci) {
					if (avion.getEstDepartureAirport() != null) {
						dolasciZaDodavanje.add(avion);
						
					}
				}
				addao.dodajSveDolaske(dolasciZaDodavanje, postavke);
			}
		} catch (NwtisRestIznimka e) {
			AerodromProblemiDAO aproblemiDao = new AerodromProblemiDAO();
			aproblemiDao.dodajProblem(a.getIcao(), e.getMessage(), postavke);
			System.out.println("Poruka: " + e.getMessage());
		}
	}

	/**
	 * Metoda gasi dretvu.
	 */
	public void kill() {
		radi = false;
	}
}
