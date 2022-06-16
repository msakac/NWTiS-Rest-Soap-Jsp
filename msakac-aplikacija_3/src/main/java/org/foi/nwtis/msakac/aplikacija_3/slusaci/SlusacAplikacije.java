package org.foi.nwtis.msakac.aplikacija_3.slusaci;

import java.io.File;

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

	/**
	 * Inicijalizira kontekst tokom pokretanja web aplikacije odnosno redeplojanja.
	 *
	 * @param ServletContextEvent sce
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		for (int i = 0; i < 10; i++) {
			System.out.println("****************** Pokrecem RESTful Servis - App 3. ******************");
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
		
		context.setAttribute("postavke", konfig);
			
		ServletContextListener.super.contextDestroyed(sce);
	}

	/**
	 * Briše kontekst prilikom zatvaranja web aplikacije odnosno redeplojanja.
	 *
	 * @param ServletContextEvent sce
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		context.removeAttribute("postavke");
		System.out.println("Postavke obrisane");
		
		ServletContextListener.super.contextDestroyed(sce);
	}

}
