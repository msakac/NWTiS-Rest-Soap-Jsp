package org.foi.nwtis.msakac.aplikacija_3.rest;

import java.util.List;

import org.foi.nwtis.msakac.aplikacija_3.podaci.AerodromDAO;
import org.foi.nwtis.msakac.aplikacija_3.podaci.AerodromDolasciDAO;
import org.foi.nwtis.msakac.aplikacija_3.podaci.AerodromPolasciDAO;
import org.foi.nwtis.msakac.aplikacija_3.podaci.AerodromPraceniDAO;
import org.foi.nwtis.msakac.aplikacija_3.pomagala.Pomagala;
import org.foi.nwtis.msakac.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Klasa RestAerodromi.
 */
@Path("aerodromi")
public class RestAerodromi {
	
	/**
	 * Get metoda resursa aerodromi na osnovnoj adresi koja vraća listu svih aerodrome. Metoda prima dva parametra zaglavlja, korisnik i zeton.
	 * Vraća listu aerodroma sa statusom 200 ili grešku sa statusom 404.
	 *
	 * @param ServletContext context
	 * @param String korisnik parametar zaglavlja
	 * @param String zeton parametar zaglavlja
	 * @return Response response
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response dajSveAerodrome(@Context ServletContext context, @HeaderParam("korisnik") String korisnik,
			@HeaderParam("zeton") String zeton) {
		Response odgovor = null;
		PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("postavke");

		odgovor = Pomagala.autorizacija(korisnik, Integer.parseInt(zeton), pbp);
		if (odgovor == null) {
			List<Aerodrom> aerodromi = null;
			AerodromDAO adao = new AerodromDAO();
			aerodromi = adao.dohvatiSveAerodrome(pbp);

			if (aerodromi != null) {
				odgovor = Response.status(Response.Status.OK).entity(aerodromi).build();
			} else {
				odgovor = Response.status(Response.Status.NOT_FOUND).entity("Nema aerodroma").build();
			}
		}
		return odgovor;
	}

	/**
	 * Post metoda resursa aerodromi na osnovnoj adresi. Prima application/json podataka icao i parametre zaglavlja zeton i korisnik. 
	 * Dodaje icao u praćene aerodrome. Vraća status 200 ako je aerodrom dodan ili 409 ako nije.
	 *
	 * @param ServletContext context
	 * @param String icao - application/json
	 * @param String korisnik parametar zaglavlja
	 * @param String zeton parametar zaglavlja
	 * @return Response response
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response dodajAerodromZaPratiti(@Context ServletContext context, String icao,
			@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") String zeton) {
		PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("postavke");
		Response odgovor = null;
		odgovor = Pomagala.autorizacija(korisnik, Integer.parseInt(zeton), pbp);
		if (odgovor == null) {

			AerodromPraceniDAO apdao = new AerodromPraceniDAO();
			boolean dodan = apdao.dodajAerodromZaPratiti(icao, pbp);

			if (dodan) {
				odgovor = Response.status(Response.Status.OK).entity("Aerodrom dodan").build();
			} else {
				odgovor = Response.status(Response.Status.CONFLICT).entity("Greska pri dodavanju aerodroma za pracenje")
						.build();
			}
		}
		return odgovor;
	}

	/**
	 * Get metoda resursa aerodromi na osnovnoj adresi sa parametrom putanje icao. Prima dva parametra zaglavlja zeton i korisnik. Dohvaca
	 * aerodrom na temelju icao koji je dan u putanji, Vraća 200 i instancu aerodroma u json formatu ako je aerodrom pronaden ili 404 sa porukom.
	 *
	 * @param ServletContext context
	 * @param String korisnik parametar zaglavlja
	 * @param String zeton parametar zaglavlja
	 * @param String icao parametar putanje
	 * @return Response response
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{icao}")
	public Response dajAerodrom(@Context ServletContext context, @HeaderParam("korisnik") String korisnik,
			@HeaderParam("zeton") String zeton, @PathParam("icao") String icao) {
		Response odgovor = null;
		PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("postavke");

		odgovor = Pomagala.autorizacija(korisnik, Integer.parseInt(zeton), pbp);
		if (odgovor == null) {

			AerodromDAO adao = new AerodromDAO();
			Aerodrom aerodrom = adao.dohvatiAerodrom(icao, pbp);
			if (aerodrom != null)
				odgovor = Response.status(Response.Status.OK).entity(aerodrom).build();
			else
				odgovor = Response.status(Response.Status.NOT_FOUND).entity("Nema aerodroma").build();
		}
		return odgovor;
	}

	/**
	 * Get metoda resursa aerodromi na adresi parametra putanje {icao}/polasci. Prima 3 parametra upita od, do i vrsta te 2 parametra zaglavlja zeton i korisnik.
	 * Dohvaca polaske aerodroma icao za datume od do ili sekunde od do ovisno o parametru vrsta(0-datum, 1-sekunde). Vraća status 200 sa listom u json formatu ili 404 ako nisu pronadeni podaci.
	 *
	 * @param ServletContext context
	 * @param String icao parametar putanje
	 * @param String korisnik parametar zaglavlja
	 * @param String zeton parametar zaglavlja
	 * @param String Od paramaetar upita
	 * @param String Do parametar upita
	 * @param int vrsta parametar upita
	 * @return Response response
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{icao}/polasci")
	public Response dajPolaskeAerodoma(@Context ServletContext context, @PathParam("icao") String icao,
			@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") String zeton, @QueryParam("od") String Od,
			@QueryParam("do") String Do, @QueryParam("vrsta") int vrsta) {
		Response odgovor = null;
		PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("postavke");
		System.out.println(korisnik+" "+zeton+" "+icao+" "+Do+" "+Od);
		odgovor = Pomagala.autorizacija(korisnik, Integer.parseInt(zeton), pbp);
		System.out.println(vrsta);
		if (odgovor == null) {
			List<AvionLeti> avioni = null;
			AerodromPolasciDAO apdao = new AerodromPolasciDAO();
			avioni = apdao.dohvatiPolaskeOdDo(icao, Od, Do, vrsta, pbp);

			if (avioni != null) {
				odgovor = Response.status(Response.Status.OK).entity(avioni).build();
			} else {
				odgovor = Response.status(Response.Status.NOT_FOUND).entity("Nema aerodroma").build();
			}

		}

		return odgovor;
	}

	/**
	 * Get metoda resursa aerodromi na adresi parametra putanje {icao}/dolasci. Prima 3 parametra upita od, do i vrsta te 2 parametra zaglavlja zeton i korisnik.
	 * Dohvaca dolakse aerodroma icao za datume od do ili sekunde od do ovisno o parametru vrsta(0-datum, 1-sekunde). Vraća status 200 sa listom u json formatu ili 404 ako nisu pronadeni podaci.
	 *
	 * @param ServletContext context
	 * @param String icao parametar putanje
	 * @param String korisnik parametar zaglavlja
	 * @param String zeton parametar zaglavlja
	 * @param String Od paramaetar upita
	 * @param String Do parametar upita
	 * @param int vrsta parametar upita
	 * @return Response response
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{icao}/dolasci")
	public Response dajDolaskeAerodroma(@Context ServletContext context, @PathParam("icao") String icao,
			@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") String zeton, @QueryParam("od") String Od,
			@QueryParam("do") String Do, @QueryParam("vrsta") int vrsta) {
		Response odgovor = null;
		PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("postavke");

		odgovor = Pomagala.autorizacija(korisnik, Integer.parseInt(zeton), pbp);
		if (odgovor == null) {
			List<AvionLeti> avioni = null;
			AerodromDolasciDAO addao = new AerodromDolasciDAO();
			avioni = addao.dohvatiDolaskeOdDo(icao, Od, Do, vrsta, pbp);

			if (avioni != null) {
				odgovor = Response.status(Response.Status.OK).entity(avioni).build();
			} else {
				odgovor = Response.status(Response.Status.NOT_FOUND).entity("Nema aerodroma").build();
			}

		}

		return odgovor;
	}

	/**
	 * Get metoda resursa aerodromi na parametrima putanje {icao1}/{icao2}. Metoda prima parametre zaglavlja korisnik i zeton za autorizaciju te salje naredbu
	 * posluzitelju za izracun udaljenosti izmedu dva aerodroma. Vraća status 200 sa odgovorom posluzitelja ili 404 ako je doslo do pogreske.
	 *
	 * @param ServletContext context
	 * @param String icao1 - parametar putanje
	 * @param String icao2 - parametar putanj
	 * @param String korisnik parametar zaglavlja
	 * @param String zeton parametar zaglavlja
	 * @return Response response
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{icao1}/{icao2}")
	public Response dohvatiUdaljenost(@Context ServletContext context, @PathParam("icao1") String icao1,
			@PathParam("icao2") String icao2, @HeaderParam("korisnik") String korisnik,
			@HeaderParam("zeton") String zeton) {
		Response odgovor = null;
		PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("postavke");

		odgovor = Pomagala.autorizacija(korisnik, Integer.parseInt(zeton), pbp);
		if (odgovor == null) {
			String adresa = pbp.dajPostavku("adresa");
			int port = Integer.parseInt(pbp.dajPostavku("port"));
			String naredba = "DISTANCE " + icao1 + " " + icao2;
			String odgovorPosluzitelja = Pomagala.posaljiKomandu(naredba, adresa, port);
			String[] podaci = odgovorPosluzitelja.split(" ");
			if (podaci[0] == "OK") {
				odgovor = Response.status(Response.Status.OK).entity(odgovorPosluzitelja).build();
			} else {
				odgovor = Response.status(Response.Status.NOT_FOUND).entity(odgovorPosluzitelja).build();
			}
		}

		return odgovor;
	}

}
