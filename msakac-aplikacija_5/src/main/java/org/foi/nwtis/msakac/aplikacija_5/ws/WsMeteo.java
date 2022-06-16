package org.foi.nwtis.msakac.aplikacija_5.ws;

import org.foi.nwtis.msakac.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.klijenti.NwtisRestIznimka;
import org.foi.nwtis.rest.klijenti.OWMKlijent;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.foi.nwtis.rest.podaci.MeteoPodaci;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;

// TODO: Auto-generated Javadoc
/**
 * Klasa WsMeteo.
 */
@WebService(serviceName = "meteo")
public class WsMeteo {
	
	/** WebServiceContext wsContext */
	@Resource
	private WebServiceContext wsContext;
	
	/** String adresa rest servisa */
	private String adresa = "http://localhost:8080/msakac-aplikacija_3/api";
	
	/**
	 * Metoda SOAP web servisa na resursu meteo. Metoda prima argument icao. Iz konfiguracijski podataka
	 * prima uzima korisnika i lozinku te sa njima radi autentikaciju prema restful web servisu resursa korisnici.
	 * Zatim salje zahtjev rest web servisu resursa aerodromi kako bi dobio instacu aerodroma koji ima icao.
	 * Na temelju lokacije tog icao dohvacaju se meteto podaci pomoću open weather map klijenta.
	 *
	 * @param String icao
	 * @return MeteoPodaci meteopodaci
	 */
	@WebMethod
	public MeteoPodaci dajMeteo(@WebParam(name = "icao") String icao) {
		MeteoPodaci meteoPodaci = null;
		PostavkeBazaPodataka pbp = dohvatiKontekst();
		//Dohvacam token za sustav korisnika
		String korisnik = pbp.dajPostavku("sustav.korisnik");
		String lozinka = pbp.dajPostavku("sustav.lozinka");
		String urlProvjere = adresa + "/provjere";
		
		Client client = ClientBuilder.newClient();
		
		WebTarget webResource = client
				.target(urlProvjere)
				.queryParam("korisnik", korisnik)
				.queryParam("lozinka", lozinka);
		
		Response restOdgovor = webResource
				.request()
				.header("Accept", "application/json")
				.get();
		
		if(restOdgovor.getStatus() == 200) {
			//Čitam podatke odgovora i kreiram token na temelju toga
			Gson gson = new Gson();
			JsonObject jsonObjekt = gson.fromJson(restOdgovor.readEntity(String.class), JsonObject.class);
			int tokenId = Integer.parseInt(jsonObjekt.get("zeton").toString());
			//Dohvaćam aerodrom
			String urlAerodromi = adresa + "/aerodromi/"+icao;
			webResource = client.target(urlAerodromi);
			restOdgovor = webResource.request()
					.header("Accept", "application/json")
					.header("korisnik", korisnik)
					.header("zeton", tokenId)
					.get();
			if(restOdgovor.getStatus() == 200) {
				String odgovor = restOdgovor.readEntity(String.class);
				Aerodrom aerodrom = gson.fromJson(odgovor, Aerodrom.class);
				//Dohvacam meteo podatke
				String apiKljuc = pbp.dajPostavku("OpenWeatherMap.apikey");
				Lokacija l = aerodrom.getLokacija();
				OWMKlijent owk = new OWMKlijent(apiKljuc);
				try {
					meteoPodaci = owk.getRealTimeWeather(l.getLatitude(), l.getLongitude());
				} catch (NwtisRestIznimka e) {
					e.printStackTrace();
				}
			}
		}
		return meteoPodaci;
	}
	
	/**
	 * Dohvati kontekst.
	 *
	 * @return the postavke baza podataka
	 */
	private PostavkeBazaPodataka dohvatiKontekst() {
		ServletContext context = (ServletContext) wsContext.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
		PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("postavke");
		return pbp;
	}
}
