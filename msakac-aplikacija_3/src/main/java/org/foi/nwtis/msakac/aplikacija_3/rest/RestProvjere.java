package org.foi.nwtis.msakac.aplikacija_3.rest;

import org.foi.nwtis.msakac.aplikacija_3.podaci.KorisniciDAO;
import org.foi.nwtis.msakac.aplikacija_3.podaci.Token;
import org.foi.nwtis.msakac.aplikacija_3.podaci.TokeniDAO;
import org.foi.nwtis.msakac.aplikacija_3.pomagala.Pomagala;
import org.foi.nwtis.msakac.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Korisnik;

import jakarta.json.Json;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Klasa RestProvjere
 */
@Path("provjere")
public class RestProvjere {

	/**
	 * Metoda prvo dohvaća korisnika iz baze podatke prema atributima iz zaglavlja.
	 * Ukoliko korisnik ne postoji ili su pogrešni podaci vraća se 
	 * response sa statusom 401 (UNAUTHORIZED). Ako korisnik postoji dohvaća se token 
	 * prema korisničkom imenu. Zatim provjeravamo dal je iz baze došao neki objekt Token.
	 * Ukoliko je token == null odnosno token ne postoji, kreira se novi token. Ukoliko token postoji,
	 * provjerava se da li je token važeći. Ukoliko nije važeći, poziva se statična metoda iz TokeniDAO
	 * klase koja mijenja status tokena na nevažeći te se zatim kreira novi token.
	 * Na kraju se kreira JSON odgovor sa podaci "zeton" i "vrijeme" te se vraća response
	 * sa statusom 200 (OK)
	 *
	 * @param context - kontekst aplikacije
	 * @param korisnik - atribut zaglavlja
	 * @param lozinka - atribut zaglavlja
	 * @return response - vraća odgovor sa HTTP statusom i JSON podacima "zeton"
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response autentikacijaKorisnika(@Context ServletContext context, 
			@QueryParam("korisnik") String korisnik,
			@QueryParam("lozinka") String lozinka) {
		Response odgovor = null;
		
		PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("postavke");
		Korisnik k = KorisniciDAO.dohvatiKorisnika(pbp, korisnik, lozinka);
		//Ako korisnik nije pronaden, vraćam odgovor 401
		if(k==null)
			return Response.status(Response.Status.UNAUTHORIZED).entity("Korisnik nije pronaden!").build();
		Token token = TokeniDAO.dohvatiTokenPremaKorImenu(k.getKorIme(), pbp);
		//Ako token ne postoji, kreiram novi token. Inace provjeravam dal stari token još vrijedi
		if(token == null) {
			token = kreirajNoviToken(k, pbp);
		}else if(!Pomagala.tokenVrijedi(token)){
			System.out.println("Kreiram novi token!");
			TokeniDAO.promijeniStatusTokena(token, pbp);
			token = kreirajNoviToken(k, pbp);
		}
		System.out.println("ID tokena: " + token.getId());
		
		String json = Json.createObjectBuilder()
	            .add("zeton", token.getId())
	            .add("vrijeme", token.getVrijediDo())
	            .build()
                .toString();
		
		odgovor = Response.status(Response.Status.OK).entity(json).build();
		return odgovor;
	}
	
	
	/**
	 * Get metoda resursa provjere na putanji parametra {token}. Prima dva parametra upita korisnik i lozinka za autentikaciju. Dohvaca token prema tokenu iz parametra putanje.
	 * Vraća 401 ukoliko korisnik nije pronaden ili token nije od korisnika koji je u parametru upita. Vraća 408 ako token ne vrijedi ili vraća 200 ako token vrijedi.
	 *
	 * @param ServletContext context
	 * @param String token
	 * @param String korisnik parametar upita
	 * @param String zeton parametar upita
	 * @return Response response
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{token}")
	public Response dohvatiToken(@Context ServletContext context,
			@PathParam("token") String token,
			@QueryParam("korisnik") String korisnik,
			@QueryParam("lozinka") String lozinka
			) {
		Response odgovor = null;
		PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("postavke");
		Korisnik k = KorisniciDAO.dohvatiKorisnika(pbp, korisnik, lozinka);
		if(k==null)
			return Response.status(Response.Status.UNAUTHORIZED).entity("Korisnik nije pronaden!").build();
		Token t = TokeniDAO.dohvatiTokenPremaTokenu(k.getKorIme(), Integer.parseInt(token), pbp);
		if(t == null) {
			odgovor = Response.status(Response.Status.UNAUTHORIZED).entity("Token nije od korisnika ili nije pronaden").build();
		}else if(Pomagala.tokenVrijedi(t)) {
			odgovor = Response.status(Response.Status.OK).entity("Token "+t.getId()+" je vazeci").build();
		}else {
			odgovor = Response.status(Response.Status.REQUEST_TIMEOUT).entity("Token "+t.getId()+" nije vazeci").build();
		}
		return odgovor;	
	}
	
	
	/**
	 * Delete metoda na resursu provjere parametra putanje {token}. Prima dva parametra upita korisnik i lozinka za autentikaciju. Briše odnosno 
	 * stavlja status neaktivan na token iz parametra putanje. Vraća 401 ukoliko korisnik nije pronaden ili token nije od korisnika koji je u parametru upita. 
	 * Vraća 408 ako token ne vrijedi ili vraća 200 ako token je uspjesno deaktiviran.
	 *
	 * @param ServletContext context
	 * @param String token
	 * @param String korisnik parametar upita
	 * @param String zeton parametar upita
	 * @return Response response
	 */
	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{token}")
	public Response obrisiToken(@Context ServletContext context,
			@PathParam("token") String token,
			@QueryParam("korisnik") String korisnik,
			@QueryParam("lozinka") String lozinka
			) {
		Response odgovor = null;
		PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("postavke");
		Korisnik k = KorisniciDAO.dohvatiKorisnika(pbp, korisnik, lozinka);
		if(k==null)
			return Response.status(Response.Status.UNAUTHORIZED).entity("Korisnik nije pronaden!").build();
		Token t = TokeniDAO.dohvatiTokenPremaTokenu(k.getKorIme(), Integer.parseInt(token), pbp);
		if(t == null) {
			odgovor = Response.status(Response.Status.UNAUTHORIZED).entity("Token nije od korisnika ili nije pronaden").build();
		}else if(Pomagala.tokenVrijedi(t)) {
			TokeniDAO.promijeniStatusTokena(t, pbp);
			odgovor = Response.status(Response.Status.OK).entity("Token "+t.getId()+" je sada ne vazeci").build();
		}else {
			odgovor = Response.status(Response.Status.REQUEST_TIMEOUT).entity("Token "+t.getId()+" nije vazeci").build();
		}
		return odgovor;	
	}
	

	/**
	 * Delete metoda resursa provjere na putanji parametra korisnik/{korisnik}. Prima dva parametra upita korisnik i lozinka za autentikaciju. Briše odnosno 
	 * stavlja status neaktivan na token koje je od korisnika iz parametra putanje. Vraća 401 ukoliko korisnik nije pronaden ili korisnik iz upita nema ovlastenja za brisanje 
	 * tokena od drugih korisnika. Vraća 404 ako korisnik nema aktivnih tokena. Vraća 200 ako je token korisnika deaktiviran.
	 *
	 * @param ServletContext context
	 * @param korisnikPath parametar putanje
	 * @param String korisnik parametar upita
	 * @param String zeton parametar upita
	 * @return Response response
	 */
	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("korisnik/{korisnik}")
	public Response obrisiTokenKorisnika(@Context ServletContext context,
			@PathParam("korisnik") String korisnikPath,
			@QueryParam("korisnik") String korisnik,
			@QueryParam("lozinka") String lozinka
			) {
		System.out.println(korisnikPath+" "+korisnik+" "+lozinka);
		Response odgovor = null;
		PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("postavke");
		Korisnik k = KorisniciDAO.dohvatiKorisnika(pbp, korisnik, lozinka);
		if(k==null)
			return Response.status(Response.Status.UNAUTHORIZED).entity("Korisnik nije pronaden!").build();
		Token t = TokeniDAO.dohvatiTokenPremaKorImenu(korisnikPath, pbp);
		if(t == null) {
			odgovor = Response.status(Response.Status.NOT_FOUND).entity("Korisnik "+korisnikPath+" nema aktivnih tokena").build();
		}else if(korisnikPath.compareTo(korisnik) == 0 || 
				KorisniciDAO.dohvatiKorisnikaPremaGrupi(pbp, korisnik, lozinka) != null) {
			TokeniDAO.promijeniStatusTokena(t, pbp);
			odgovor = Response.status(Response.Status.OK).entity("Token "+t.getId()+" korisnika "+t.getKorIme()+" je sada ne vazeci").build();
		}else {
			odgovor = Response.status(Response.Status.UNAUTHORIZED).
					entity("Korisnik "+k.getKorIme()+" nema ovlastenje za brisanje tokena "+korisnikPath).build();
		}
		return odgovor;	
	}
	
	
	/**
	 * Pomoćna metodam koja kreira novi token za korisnika iz argumenta.
	 *
	 * @param Korisnik k
	 * @param PostavkeBazaPodataka pbp
	 * @return return token
	 */
	private Token kreirajNoviToken(Korisnik k, PostavkeBazaPodataka pbp) {
		int trajanje = Integer.parseInt(pbp.dajPostavku("zeton.trajanje"));
		Token token = TokeniDAO.kreirajNoviToken(k.getKorIme(), trajanje, pbp);
		return token;
	}
	

}
