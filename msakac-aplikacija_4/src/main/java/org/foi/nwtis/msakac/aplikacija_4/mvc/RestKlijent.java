package org.foi.nwtis.msakac.aplikacija_4.mvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.foi.nwtis.msakac.aplikacija_4.podaci.Grupa;
import org.foi.nwtis.msakac.aplikacija_4.podaci.Token;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.Korisnik;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Klasa RestKlijent.
 */
public class RestKlijent {

	/** String adresa api-ja rest servisa */
	private String adresa = "http://localhost:8080/msakac-aplikacija_3/api";

	/**
	 * Metoda prima instacu Korisnika, string vrijednost korisnickog imena i lozinke. Kreira se klijent za api servis. Korisnicko ime i lozinka šalju se na autentikaciju.
	 * rest servisu te se zatim poziva metoda web servisa koja dodaje novog korisnika u bazu podataka. Vraća Response odgovor od rest servisa.
	 *
	 * @param Korisnik noviKorisnik
	 * @param String sustavKorisnik
	 * @param String sustavLozinka
	 * @return the response
	 */
	public Response registrirajKorisnika(Korisnik noviKorisnik, String sustavKorisnik, String sustavLozinka) {
		// Provjera korisnika sustava
		Client client = ClientBuilder.newClient();
		String urlProvjere = adresa + "/provjere";
		WebTarget webResource = client
				.target(urlProvjere)
				.queryParam("korisnik", sustavKorisnik)
				.queryParam("lozinka",sustavLozinka);
		
		Response restOdgovor = webResource
				.request()
				.header("Accept", "application/json")
				.get();
		
		//Ako dobimo 200 sustav.korisnik i sustav.lozinka su ispravni
		if (restOdgovor.getStatus() == 200) {
			
			//Čitam podatke odgovora i kreiram token na temelju toga
			Gson gson = new Gson();
			JsonObject jsonObjekt = gson.fromJson(restOdgovor.readEntity(String.class), JsonObject.class);
			int tokenId = Integer.parseInt(jsonObjekt.get("zeton").toString());
			int trajeDo = Integer.parseInt(jsonObjekt.get("vrijeme").toString());
			Token token = new Token(tokenId, sustavKorisnik, trajeDo, true);
			
			//Registracija korisnika
			String urlKorisnici = adresa +"/korisnici";
			webResource = client.target(urlKorisnici);
			restOdgovor = webResource
					.request()
					.header("Accept", "application/json")
					.header("korisnik", token.getKorIme())
					.header("zeton", token.getId())
					.post(Entity.entity(noviKorisnik, MediaType.APPLICATION_JSON),Response.class);
		}
		
		return restOdgovor;
	}
	
	/**
	 * Metoda šalje podatke korisnicko ime i lozinka rest web servisu na resurs /provjere za autentikaciju. Od rest servisa dobiva json podatke zeton i vrijeme koji
	 * se kasnije koriste za autorizaciju. Metoda vraća instancu Token.
	 *
	 * @param String korIme
	 * @param String lozinka
	 * @return Token token
	 */
	public Token prijaviKorisnika(String korIme, String lozinka) {
		Token token = null;
		Client client = ClientBuilder.newClient();
		String urlProvjere = adresa + "/provjere";
		WebTarget webResource = client
				.target(urlProvjere)
				.queryParam("korisnik", korIme)
				.queryParam("lozinka",lozinka);
		
		Response restOdgovor = webResource
				.request()
				.header("Accept", "application/json")
				.get();
		
		if (restOdgovor.getStatus() == 200) {
			//Čitam podatke odgovora i kreiram token na temelju toga
			Gson gson = new Gson();
			JsonObject jsonObjekt = gson.fromJson(restOdgovor.readEntity(String.class), JsonObject.class);
			int tokenId = Integer.parseInt(jsonObjekt.get("zeton").toString());
			int trajeDo = Integer.parseInt(jsonObjekt.get("vrijeme").toString());
			token = new Token(tokenId, korIme, trajeDo, true);
		}
		return token;
	}
	
	/**
	 * Metoda prima instacu Tokena koji se koristi za autentikaciju u rest web servisu resursa /provjere. Nakon autorizacije
	 * poziva se metode resursa /korisnici koja vraća sve korisnike u json formatu. Metoda zatim vraća listu korisnika.
	 *
	 * @param Token token
	 * @return Lista korisnika
	 */
	public List<Korisnik> dohvatiKorisnike(Token token){
		Client client = ClientBuilder.newClient();
		String urlKorisnici = adresa + "/korisnici";
		WebTarget webResource = client.target(urlKorisnici);
		Response restOdgovor = webResource
				.request()
				.header("Accept", "application/json")
				.header("korisnik", token.getKorIme())
				.header("zeton", token.getId())
				.get();
		
		List<Korisnik> korisnici= null;
		if(restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			korisnici = new ArrayList<>();
			korisnici.addAll(Arrays.asList(gson.fromJson(odgovor, Korisnik[].class)));
		}
		return korisnici;
	}
	
	/**
	 * Metoda prima argumente Token koji se koristi za autorizaiju i korisnicko ime ciji token je potrebno obrisati. Podaci se salju rest servisu resursa /provjere 
	 * na autorizaciju. Zatim poziva delete metoda koja mijenja status tokena korisnika koji ima korisnicko ime. Metoda vraća Response odgovor dobivem od rest servisa.
	 *
	 * @param Token token
	 * @param String korIme
	 * @return Response restOdgovor
	 */
	public Response obrisiToken(Token token, String korIme) {
		//prvo dohvacam lozinku od prijavljenog korisnika
		Client client = ClientBuilder.newClient();
		String urlKorisnici = adresa + "/korisnici/"+token.getKorIme();
		WebTarget webResource = client.target(urlKorisnici);
		Response restOdgovor = webResource
				.request()
				.header("Accept", "application/json")
				.header("korisnik", token.getKorIme())
				.header("zeton", token.getId())
				.get();
		if(restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			Korisnik korisnik = gson.fromJson(odgovor, Korisnik.class);
			
			String urlProvjere = null;
			
			//provjera dal briše svoj žeton
			if(korisnik.getKorIme() == korIme) {
				//briše svoj žeton
				urlProvjere = adresa +"/provjere/"+token.getId();
			}else {
				//briše nečiji žeton
				urlProvjere = adresa +"/provjere/korisnik/"+korIme;
			}
			
			webResource = client.target(urlProvjere)
					.queryParam("korisnik", korisnik.getKorIme())
					.queryParam("lozinka", korisnik.getLozinka());
			restOdgovor = webResource
					.request()
					.header("Accept", "application/json")
					.delete();
		}
		return restOdgovor;
	}
	
	/**
	 * Metoda prima Token koji se koristi za autorizaciju pomocu rest servisa resursa /provjere. Zatim se iz resursa /korisnici dohvaćaju
	 * sve grupe korisnika iz korisnickog imena tokena. Dobivena lista grupa tj atribut naziv usporeduje se sa sustavAdministratori argumentom. Ako
	 * se naziv grupe i argument podudaraju metoda vraća true inače false.
	 *
	 * @param Token token
	 * @param String sustavAdministratori
	 * @return true, ako je korisnik član grupe iz argumenta sustavAdministratori
	 */
	public boolean provjeriGrupuKorisnika(Token token, String sustavAdministratori) {
		Client client = ClientBuilder.newClient();
		String urlKorisnici = adresa + "/korisnici/"+token.getKorIme()+"/grupe";
		WebTarget webResource = client.target(urlKorisnici);
		Response restOdgovor = webResource
				.request()
				.header("Accept", "application/json")
				.header("korisnik", token.getKorIme())
				.header("zeton", token.getId())
				.get();
		List<Grupa> grupe = null;
		if(restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			grupe = new ArrayList<>();
			grupe.addAll(Arrays.asList(gson.fromJson(odgovor, Grupa[].class)));
			
			for (Grupa grupa : grupe) {
				if(grupa.getNaziv().equals(sustavAdministratori)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Metoda prvo dohvaća lozinku iz rest resursa /korisnici pomoću argumenta token. Zatim se poziva resurs rest servisa /provjere sa autentikacijskim podacima koji provjerava
	 * da li token vrijedi. Ako token vrijedi onda se vraća true inače false.
	 *
	 * @param Token token
	 * @return true, ako token vrijedi
	 */
	public boolean provjeriVrijednostTokena(Token token) {
		//dohvacam korisnika zbog lozinke
		Client client = ClientBuilder.newClient();
		String urlKorisnici = adresa + "/korisnici/"+token.getKorIme();
		WebTarget webResource = client.target(urlKorisnici);
		Response restOdgovor = webResource
				.request()
				.header("Accept", "application/json")
				.header("korisnik", token.getKorIme())
				.header("zeton", token.getId())
				.get();
		if(restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			Korisnik korisnik = gson.fromJson(odgovor, Korisnik.class);
			//dohvacam token
			String urlProvjere = adresa +"/provjere/"+token.getId();
			webResource = client.target(urlProvjere)
					.queryParam("korisnik", korisnik.getKorIme())
					.queryParam("lozinka", korisnik.getLozinka());
			restOdgovor = webResource
					.request()
					.header("Accept", "application/json")
					.get();
			if (restOdgovor.getStatus() == 200) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 *	Metoda dohvaća sve aerodrome pomoću rest servisa resursa /aerodromi. Metodi se šalju autorizacijski podaci iz argumenta Token. Metoda vraća
	 *	listu svih aerodroma.
	 *
	 * @param Token token
	 * @return Lista aerodroma
	 */
	public List<Aerodrom> dohvatiAerodrome(Token token){
		Client client = ClientBuilder.newClient();
		String urlAerodromi = adresa + "/aerodromi";
		WebTarget webResource = client.target(urlAerodromi);
		Response restOdgovor = webResource
				.request()
				.header("Accept", "application/json")
				.header("korisnik", token.getKorIme())
				.header("zeton", token.getId())
				.get();
		List<Aerodrom> aerodromi = null;
		if(restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			aerodromi = new ArrayList<>();
			aerodromi.addAll(Arrays.asList(gson.fromJson(odgovor, Aerodrom[].class)));
		}
		return aerodromi;
	}

}

































