package org.foi.nwtis.msakac.aplikacija_3.rest;

import java.util.List;

import org.foi.nwtis.msakac.aplikacija_3.podaci.AerodromDAO;
import org.foi.nwtis.msakac.aplikacija_3.pomagala.Pomagala;
import org.foi.nwtis.msakac.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;

import com.google.gson.Gson;

import jakarta.json.Json;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Klasa RestServeri.
 */
@Path("serveri")
public class RestServeri {

	/**
	 * Get metoda resursa serveri na inicijalnoj putanji. Metoda prima dva parametra zaglavlja korisnik i zeton za autorizaciju. Metoda šalje naredbu STATUS
	 * na poslužitelj iz aplikacije 1. Metoda vraća status 200 sa adresom i portom ako je komanda uspjesno obradena ili 400 ako je doslo do problema sa
	 * odgovorom posluzitelja.
	 *
	 * @param ServletContext context
	 * @param String korisnik parametar zaglavlja
	 * @param String zeton parametar zaglavlja
	 * @return Response response
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response posaljiKomanduStatus(@Context ServletContext context, @HeaderParam("korisnik") String korisnik,
			@HeaderParam("zeton") String zeton) {
		Response odgovor = null;
		PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("postavke");

		odgovor = Pomagala.autorizacija(korisnik, Integer.parseInt(zeton), pbp);
		if (odgovor == null) {
			String adresa = pbp.dajPostavku("adresa");
			int port = Integer.parseInt(pbp.dajPostavku("port"));
			String odgovorPosluzitelja = Pomagala.posaljiKomandu("STATUS", adresa, port);
			System.out.println("Odgovor servera: " + odgovorPosluzitelja);
			// ako posluzitelj odgovori sa OK x
			if (odgovorPosluzitelja.length() == 4) {
				String json = Json.createObjectBuilder().add("adresa", adresa).add("port", port).build().toString();
				odgovor = Response.status(Response.Status.OK).entity(json).build();
			} else {
				odgovor = Response.status(Response.Status.BAD_REQUEST).entity(odgovorPosluzitelja).build();
			}
		}
		return odgovor;
	}

	/**
	 * Get metoda resursa serveri na putanji parametra {komanda}. Metoda prima dva parametra zaglavlja korisnik i zeton za autorizaciju. Metoda šalje 
	 * naredbu iz parametra putanje poslužitelju iz prve aplikacije. Metoda vraća status 200 ili 400 ako je doslo do problema sa
	 * odgovorom posluzitelja.
	 *
	 * @param ServletContext context
	 * @param String korisnik parametar zaglavlja
	 * @param String zeton parametar zaglavlja
	 * @return Response response
	 * @param String komanda
	 */
	@GET
	@Path("{komanda}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response posaljiKomanduStatus(@Context ServletContext context, @HeaderParam("korisnik") String korisnik,
			@HeaderParam("zeton") String zeton, @PathParam("komanda") String komanda) {
		Response odgovor = null;
		PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("postavke");

		odgovor = Pomagala.autorizacija(korisnik, Integer.parseInt(zeton), pbp);
		if (odgovor == null) {
			String adresa = pbp.dajPostavku("adresa");
			int port = Integer.parseInt(pbp.dajPostavku("port"));
			String odgovorPosluzitelja = Pomagala.posaljiKomandu(komanda, adresa, port);
			System.out.println("Odgovor servera: " + odgovorPosluzitelja);
			if (odgovorPosluzitelja == "OK") {
				odgovor = Response.status(Response.Status.OK).entity(odgovorPosluzitelja).build();
			} else {
				odgovor = Response.status(Response.Status.BAD_REQUEST).entity(odgovorPosluzitelja).build();
			}
		}
		return odgovor;
	}

	/**
	 * Post metoda resursa serveri na putanji /LOAD. Metoda prima dva parametra zaglavlja korisnik i zeton za autorizaciju. Metoda 
	 * šalje naredbu LOAD sa json podacima svih aerodroma na poslužitelj aplikacije 1.Metoda vraća status 200 ili 409 ako je doslo do problema sa
	 * odgovorom posluzitelja.
	 *
	 * @param ServletContext context
	 * @param String korisnik parametar zaglavlja
	 * @param String zeton parametar zaglavlja
	 * @return Response response
	 */
	@POST
	@Path("/LOAD")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response posaljiKomanduLoad(@Context ServletContext context, @HeaderParam("korisnik") String korisnik,
			@HeaderParam("zeton") String zeton) {
		Response odgovor = null;
		PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("postavke");

		odgovor = Pomagala.autorizacija(korisnik, Integer.parseInt(zeton), pbp);
		if (odgovor == null) {
			String adresa = pbp.dajPostavku("adresa");
			int port = Integer.parseInt(pbp.dajPostavku("port"));

			AerodromDAO adao = new AerodromDAO();
			List<Aerodrom> aerodromi = adao.dohvatiSveAerodrome(pbp);

			Gson gson = new Gson();
			String json = gson.toJson(aerodromi);

			String odgovorPosluzitelja = Pomagala.posaljiKomandu("LOAD " + json, adresa, port);
			System.out.println("Odgovor servera: " + odgovorPosluzitelja);
			String[] podaci = odgovorPosluzitelja.split(" ");
			if (podaci[0] == "OK") {
				odgovor = Response.status(Response.Status.OK).entity(odgovorPosluzitelja).build();
			} else {
				odgovor = Response.status(Response.Status.CONFLICT).entity(odgovorPosluzitelja).build();
			}
		}
		return odgovor;
	}

}
