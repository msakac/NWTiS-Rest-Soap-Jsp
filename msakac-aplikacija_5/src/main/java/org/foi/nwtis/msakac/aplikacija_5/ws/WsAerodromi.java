package org.foi.nwtis.msakac.aplikacija_5.ws;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.foi.nwtis.rest.podaci.AvionLeti;

import com.google.gson.Gson;

import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.xml.ws.WebServiceContext;

/**
 * Klasa WsAerodromi.
 */
@WebService(serviceName = "aerodromi")
public class WsAerodromi {

	/** WebServiceContext wsContext */
	@Resource
	private WebServiceContext wsContext;

	/** String adresa rest servisa */
	private String adresa = "http://localhost:8080/msakac-aplikacija_3/api";

	/**
	 * Metoda SOAP web servisa resursa aerodromi. Prima argumente korisnik i zeton za autorizaciju. Metoda vraca listu polazaka 
	 * sa aerodrom icao na datume danOd danDo. Lista polazaka dobiva se od zahtjeva prema rest servisu resursa aerodromi.
	 *
	 * @param String korisnik
	 * @param String zeton
	 * @param String icao
	 * @param String danOd
	 * @param String danDo
	 * @return Lista polazaka 
	 */
	@WebMethod
	public List<AvionLeti> dajPolaskeDan(@WebParam(name = "korisnik") String korisnik,
			@WebParam(name = "zeton") String zeton, @WebParam(name = "icao") String icao,
			@WebParam(name = "danOd") String danOd, @WebParam(name = "danDo") String danDo) {
		Client client = ClientBuilder.newClient();
		String urlAerodromi = adresa + "/aerodromi/" + icao+"/polasci";
		WebTarget webResource = client.target(urlAerodromi).queryParam("od", danOd).queryParam("do", danDo)
				.queryParam("vrsta", 0);
		System.out.println(korisnik+" "+zeton+" "+icao+" "+danOd+" "+danDo);
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("zeton", zeton).get();
		String odgovor = restOdgovor.readEntity(String.class);
		System.out.println("REST: " + odgovor);
		List<AvionLeti> avioni = null;
		if (restOdgovor.getStatus() == 200) {
			Gson gson = new Gson();
			avioni = new ArrayList<>();
			avioni.addAll(Arrays.asList(gson.fromJson(odgovor, AvionLeti[].class)));
		}

		return avioni;
	}
	
	/**
	 * Metoda SOAP web servisa resursa aerodromi. Prima argumente korisnik i zeton za autorizaciju. Metoda vraca listu polazaka 
	 * sa aerodrom icao sa vremenom vrijemeDo vrijemeDo. Lista polazaka dobiva se od zahtjeva prema rest servisu resursa aerodromi.
	 *
	 * @param String korisnik
	 * @param String zeton
	 * @param String icao
	 * @param String vrijemeDo
	 * @param String vrijemeDo
	 * @return Lista polazaka 
	 */
	@WebMethod
	public List<AvionLeti> dajPolaskeVrijeme(@WebParam(name = "korisnik") String korisnik,
			@WebParam(name = "zeton") String zeton, @WebParam(name = "icao") String icao,
			@WebParam(name = "vrijemeOd") String vrijemeOd, @WebParam(name = "vrijemeDo") String vrijemeDo) {
		Client client = ClientBuilder.newClient();
		String urlAerodromi = adresa + "/aerodromi/" + icao + "/polasci";
		WebTarget webResource = client
				.target(urlAerodromi)
				.queryParam("od", vrijemeOd)
				.queryParam("do", vrijemeDo)
				.queryParam("vrsta", 1);
		System.out.println(korisnik+" "+zeton+" "+icao+" "+vrijemeOd+" "+vrijemeDo);
		Response restOdgovor = webResource
				.request()
				.header("Accept", "application/json")
				.header("korisnik", korisnik)
				.header("zeton", zeton).get();
		String odgovor = restOdgovor.readEntity(String.class);
		System.out.println("REST: " + odgovor);
		List<AvionLeti> avioni = null;
		if (restOdgovor.getStatus() == 200) {
			Gson gson = new Gson();
			avioni = new ArrayList<>();
			avioni.addAll(Arrays.asList(gson.fromJson(odgovor, AvionLeti[].class)));
		}

		return avioni;
	}

	/**
	 * Metoda SOAP web servisa na resursu aerodromi. Prima argumente korisnik i zeton za autorizaciju. Metoda dodaje aerodom
	 * za preuzimanje. Poziva rest servis resursa aerodromi kako bi se dodao aerodrom za preuzimanje u bazu podataka. Vraća odgovor
	 * rest servisa.
	 *
	 * @param String korisnik
	 * @param String zeton
	 * @param String icao
	 * @return String odgovor
	 */
	@WebMethod
	public String dodajAerodromPreuzimanje(@WebParam(name = "korisnik") String korisnik,
			@WebParam(name = "zeton") String zeton, @WebParam(name = "icao") String icao) {
		Client client = ClientBuilder.newClient();
		String urlAerodromi = adresa + "/aerodromi";
		WebTarget webResource = client.target(urlAerodromi);
		System.out.println(korisnik+" "+zeton+" "+icao);
		Response restOdgovor = webResource
				.request()
				.header("Accept", "application/json")
				.header("korisnik", korisnik)
				.header("zeton", zeton)
				.post(Entity.entity(icao, MediaType.APPLICATION_JSON),Response.class);
		String odgovor = restOdgovor.readEntity(String.class);
		return odgovor;
	}

}





























