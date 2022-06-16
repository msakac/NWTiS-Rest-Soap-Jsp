package org.foi.nwtis.msakac.aplikacija_3.rest;

import java.util.List;

import org.foi.nwtis.msakac.aplikacija_3.podaci.Grupa;
import org.foi.nwtis.msakac.aplikacija_3.podaci.KorisniciDAO;
import org.foi.nwtis.msakac.aplikacija_3.pomagala.Pomagala;
import org.foi.nwtis.msakac.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Korisnik;

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
 * Klasa RestKorisnici.
 */
@Path("korisnici")
public class RestKorisnici {

	/**
	 * Get metoda resursa korisnici na inicijalnoj putanji. Prima parametre zaglavlja korisnik i zeton. Dohvaca sve korisnike iz baze podataka
	 * pomoću DAO klase. Vraća status 200 ako su pronadeni korisnici
	 *
	 * @param ServletContext context
	 * @param String korisnik parametar zaglavlja
	 * @param String zeton parametar zaglavlja
	 * @return Response response
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response dohvatiKorisnike(@Context ServletContext context, @HeaderParam("korisnik") String korisnik,
			@HeaderParam("zeton") String zeton) {
		Response odgovor = null;
		PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("postavke");

		odgovor = Pomagala.autorizacija(korisnik, Integer.parseInt(zeton), pbp);
		if (odgovor == null) {
			List<Korisnik> korisnici = KorisniciDAO.dohvatiSveKorisnike(pbp);
			odgovor = Response.status(Response.Status.OK).entity(korisnici).build();
		}

		return odgovor;
	}

	/**
	 * Post metoda resursa korisnici na inicijalnoj putanji. Prima parametre zaglavlja korisnik i zeton za autorizaciju. Prima application/json objekt Korisnika koji 
	 * se treba dodati u bazu podataka. Vraća status 200 ako je korisnik dodan ili 409 ako je doslo do pogreske.
	 *
	 * @param ServletContext context
	 * @param String korisnik parametar zaglavlja
	 * @param String zeton parametar zaglavlja
	 * @param Korisnik korisnik application/json
	 * @return Response response
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response dodajKorisnika(@Context ServletContext context, @HeaderParam("korisnik") String korisnik,
			@HeaderParam("zeton") String zeton, Korisnik k) {
		Response odgovor = null;
		PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("postavke");

		odgovor = Pomagala.autorizacija(korisnik, Integer.parseInt(zeton), pbp);
		if (odgovor == null) {
			// Korisnik k = new Korisnik(korisnickoIme, ime, prezime, lozinka, email);
			boolean dodan = KorisniciDAO.dodajKorisnika(pbp, k);
			if (dodan)
				odgovor = Response.status(Response.Status.OK).entity("Korisnik " + k.getKorIme() + " dodan").build();
			else
				odgovor = Response.status(Response.Status.CONFLICT).entity("Greška prilikom dodavanja korisnika")
						.build();
		}

		return odgovor;
	}

	/**
	 * Get metoda resursa korisnici na putanji parametra {korisnik}. Prima parametre zaglavlja korisnik i zeton za autorizaciju. Dohvaća korisnika iz putanje parametra
	 * iz baze podataka. Vraća status 200 sa json korisnik objektom.
	 *
	 * @param ServletContext context
	 * @param tring trazeniKorisnik parametar putanje
	 * @param String korisnik parametar zaglavlja
	 * @param String zeton parametar zaglavlja
	 * @return Response response
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{korisnik}")
	public Response dohvatiKorisnika(@Context ServletContext context, @PathParam("korisnik") String trazeniKorisnik,
			@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") String zeton) {
		Response odgovor = null;
		PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("postavke");
		System.out.println(trazeniKorisnik);
		odgovor = Pomagala.autorizacija(korisnik, Integer.parseInt(zeton), pbp);
		if (odgovor == null) {
			Korisnik k = KorisniciDAO.dohvatiKorisnikaPremaImenu(pbp, trazeniKorisnik);
			odgovor = Response.status(Response.Status.OK).entity(k).build();
		}

		return odgovor;
	}

	/**
	 * Get metoda resursa korisnici na putanji parametra {korisnik}/grupe. Prima parametre zaglavlja korisnik i zeton za autorizaciju. Dohvaća  grupe korisnika iz putanje parametra
	 * iz baze podataka. Vraća status 200 sa json podaci objekta Grupa.
	 * @param ServletContext context
	 * @param tring trazeniKorisnik parametar putanje
	 * @param String korisnik parametar zaglavlja
	 * @param String zeton parametar zaglavlja
	 * @return Response response
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{korisnik}/grupe")
	public Response dohvatiGrupeKorisnika(@Context ServletContext context, @HeaderParam("korisnik") String korisnik,
			@HeaderParam("zeton") String zeton, @PathParam("korisnik") String trazeniKorisnik) {
		Response odgovor = null;
		PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("postavke");

		odgovor = Pomagala.autorizacija(korisnik, Integer.parseInt(zeton), pbp);
		if (odgovor == null) {
			List<Grupa> grupe = KorisniciDAO.dohvatiGrupeKorisnika(pbp, trazeniKorisnik);
			odgovor = Response.status(Response.Status.OK).entity(grupe).build();
		}

		return odgovor;
	}

}
