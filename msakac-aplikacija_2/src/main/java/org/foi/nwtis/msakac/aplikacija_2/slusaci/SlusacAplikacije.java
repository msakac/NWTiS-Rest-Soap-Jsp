package org.foi.nwtis.msakac.aplikacija_2.slusaci;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.foi.nwtis.msakac.aplikacija_2.dretve.PreuzimanjePodataka;
import org.foi.nwtis.msakac.vjezba_03.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.msakac.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.msakac.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

// TODO: Auto-generated Javadoc
/**
 * Klasa SlusacAplikacije.
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener{
	
	/** Preuzimanje podataka. */
	private PreuzimanjePodataka pp;

	/**
	 * Poziva se kad se web aplikacija deploya. Dohvaća postavke i pokreće dretvu PreuzimanjePodataka
	 *
	 * @param ServletContextEvent sce
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		for (int i = 0; i < 10; i++) {
			System.out.println("****************** Pokrecem App 2. ******************");
		}
		ServletContext context = sce.getServletContext();
		String nazivDatoteke = context.getInitParameter("konfiguracija");
		String putanja = context.getRealPath("/WEB-INF");
		nazivDatoteke = putanja + File.separator + nazivDatoteke;

		System.out.println("Datoteka konfiguracije: " + nazivDatoteke);

		KonfiguracijaBP konfig = new PostavkeBazaPodataka(nazivDatoteke);
		try {
			konfig.ucitajKonfiguraciju();
		} catch (NeispravnaKonfiguracija e) {
			e.printStackTrace();
			return;
		}
		System.out.println("Konfiguracija ucitana");
		
		if (!provjeriVrijednosti(konfig)) {
			return;
		}
		context.setAttribute("postavke", konfig);
		
		pp = new PreuzimanjePodataka(
				(PostavkeBazaPodataka) context.getAttribute("postavke"));
		context.setAttribute("dretva", pp);
		pp.start();

		
		ServletContextListener.super.contextDestroyed(sce);
	}

	/**
	 * Poziva se kad se metoda undeploya. Briše postavke iz konteksta
	 *
	 * @param ServletContextEvent sce
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		context.removeAttribute("postavke");
		System.out.println("Postavke obrisane");
		pp.kill();
		
		ServletContextListener.super.contextDestroyed(sce);
	}
	
	/**
	 * Metoda provjerava vrijednosti konfiguracije
	 *
	 * @param KonfiguracijaBP konfig
	 * @return true, ako je sve korektno
	 */
	public static boolean provjeriVrijednosti(KonfiguracijaBP konfig) {
		String preuzimanjeDo = konfig.dajPostavku("preuzimanje.do");
		String preuzimanjeOd = konfig.dajPostavku("preuzimanje.od");
		try {
			int ciklusVrijeme = Integer.parseInt(konfig.dajPostavku("ciklus.vrijeme"));
			int ciklusKorekcija = Integer.parseInt(konfig.dajPostavku("ciklus.korekcija"));
			int preuzimanjeOdmak = Integer.parseInt(konfig.dajPostavku("preuzimanje.odmak"));
			int preuzimanjePauza = Integer.parseInt(konfig.dajPostavku("preuzimanje.pauza"));
			int preuzimanjeVrijeme = Integer.parseInt(konfig.dajPostavku("preuzimanje.vrijeme"));
		} catch (NumberFormatException e) {
			String[] pogresnaVrijednost = e.getMessage().split("\"");
			System.out.println("ERROR: Vrijednost postavke '" + pogresnaVrijednost[1] + "' je neispravna.");
			return false;
		}

		// Provjera datuma
		Pattern pPreuzimanje = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4})$");
		Matcher mPreuzimanjeOd = pPreuzimanje.matcher(preuzimanjeOd);
		Matcher mPreuzimanjeDo = pPreuzimanje.matcher(preuzimanjeDo);
		if (!mPreuzimanjeOd.matches() || !mPreuzimanjeDo.matches()) {
			System.out.println("ERROR: Datum nije u valjanom formatu");
			return false;
		}
		return true;
	}

}
