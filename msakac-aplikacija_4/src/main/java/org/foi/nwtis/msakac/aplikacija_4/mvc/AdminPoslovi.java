package org.foi.nwtis.msakac.aplikacija_4.mvc;

import java.util.List;

import org.foi.nwtis.msakac.aplikacija_4.podaci.Token;
import org.foi.nwtis.msakac.aplikacija_5.pomagala.Pomagala;
import org.foi.nwtis.msakac.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.Korisnik;

import com.google.gson.Gson;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

/**
 * Klasa AdminPoslovi.
 */
@Controller
@Path("admin_poslovi")
@RequestScoped
public class AdminPoslovi extends HttpServlet {
	
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	
	/** Models model za poglede */
	@Inject
	private Models model;

	/**
	 * Get metoda na putanji /pocetak vraća pogled index.jsp. 
	 * Metoda dohvaća token koji se zapisuje u model ili null ako nema tokena.
	 *
	 * @param ServletContext context
	 */
	@GET
	@Path("pocetak")
	@View("index.jsp")
	public void pocetak(@Context ServletContext context) {
		Token token = dohvatiToken(context);
		if (token != null) {
			model.put("token", token.getId());
		}else {
			model.put("token", null);
		}
		

	}

	/**
	 * Get metoda na putanji /registracija vraća pogled registacija.jsp.
	 *
	 * @param ServletContext context
	 */
	@GET
	@Path("registracija")
	@View("registracija.jsp")
	public void registracija(@Context ServletContext context) {

	}
	
	/**
	 * Post metoda na putanji /registracija vraća pogled registracija.jsp. 
	 * Metoda dobiva parametre koji su proslijedeni iz forme tokom post zahtjeva.
	 * Poziva se metoda RestKlijenta koja registrira novog korisnika. 
	 * Ako je odgovor statusa 200 prikazuje se poruka uspjeha, inače poruka greške.
	 *
	 * @param ServletContext context
	 * @param String korIme parametar forme
	 * @param String lozinka parametar forme
	 * @param String ime parametar forme
	 * @param String prezime parametar forme
	 * @param String email parametar forme
	 */
	@POST
	@Path("registracija")
	@View("registracija.jsp")
	public void registrirajKorisnika(@Context ServletContext context, @FormParam("korIme") String korIme,
			@FormParam("lozinka") String lozinka, @FormParam("ime") String ime, @FormParam("prezime") String prezime,
			@FormParam("email") String email) {
		PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("postavke");
		String sustavKorisnik = pbp.dajPostavku("sustav.korisnik").toString();
		String sustavLozinka = pbp.dajPostavku("sustav.lozinka").toString();
		System.out.println("Sustav korisnik: "+sustavKorisnik+"; Sustav lozinka: "+sustavLozinka);
		System.out.println("Registracija korisnika " + korIme + "; " + lozinka + "; " + ime + "; " + prezime + "; " + email+";");
		
		RestKlijent klijent = new RestKlijent();
		Korisnik noviKorisnik = new Korisnik(korIme, ime, prezime, sustavLozinka, email);
		Response odgovorServisa = klijent.registrirajKorisnika(noviKorisnik, sustavKorisnik, sustavLozinka);
		
		System.out.println(odgovorServisa);
		if(odgovorServisa.getStatus() == 200) {
			model.put("uneseno", odgovorServisa.readEntity(String.class));
			model.put("greska", null);
		}else {
			model.put("greska", odgovorServisa.readEntity(String.class));
			model.put("uneseno", null);
		}
	}

	/**
	 * Get metoda na putanji /prijava vraća pogled prijava.jsp ili pocetak.jsp ako je korisnik već prijavljen.
	 *
	 * @param ServletContext context
	 * @return String pogled
	 */
	@GET
	@Path("prijava")
	@View("prijava.jsp")
	public String prijava(@Context ServletContext context) {
		Token token = dohvatiToken(context);
		if (token != null) {
			return "redirect:admin_poslovi/pocetak";
		}
		return null;
	}
	
	/**
	 * Post metoda na putanji /prijava. Metoda za parametre forme korisnicko ime i 
	 * lozinka prijavljue korisnika pomoću metode rest klijenta.
	 * Ako je korisnik uspjesno prijavljen postavlja se atribut token u kontekstu i 
	 * vraća pogled pocetak.jsp. Inace se vraca pogled prijava.jsp sa porukom greške.
	 *
	 * @param ServletContext context
	 * @param String korIme parametar forme
	 * @param String lozinka parametar forme
	 * @return String pogled
	 */
	@POST
	@Path("prijava")
	@View("prijava.jsp")
	public String prijaviKorisnika(@Context ServletContext context, @FormParam("korIme") String korIme,
			@FormParam("lozinka") String lozinka) {
		System.out.println("Prijava korisnika " + korIme + "; " + lozinka + "; ");
		RestKlijent klijent = new RestKlijent();
		Token token = klijent.prijaviKorisnika(korIme, lozinka);
		
		if(token == null) {
			model.put("greska", "Korisničko ime ili lozinka nisu valjani!");
		}else {
			context.setAttribute("token", token);
			System.out.println("Token: "+token.getId());
			return "redirect:admin_poslovi/pocetak";
		}
		return null;
	}
	
	/**
	 * Get metoda na putanji /odjava. Metoda brise token iz konteksta odnosno odjavljuje korisnika te prikazuje pogled prijava.jsp
	 *
	 * @param ServletContext context
	 */
	@GET
	@Path("odjava")
	@View("prijava.jsp")
	public void odjavaKorisnika(@Context ServletContext context){
		context.setAttribute("token", null);
	}
	
	/**
	 * Get metoda na putanji /pregledKorisnika. Metoda prvo dohvaca aktivan token pomocu metode rest klijent. 
	 * Ako on ne postoji preusmjerava na prijavu. Ako postoji poziva se metoda rest klijenta koja dohvaca sve korisnike.
	 * Zatim se na temelju postavke sustav.administratori provjerava da li je korisnik clan te grupe. 
	 * Pomocu rest klijenta dohvacaju se sve grupe aktivnog korisnika. Ako je on clan grupe iz sustav.administratori
	 * tada se u model postavlja vrijednost clanGrupe na 1 odnosno korisnik ima pravo brisanja tudih token. 
	 * Metoda vraća pogled pregledKorisnika.jsp ili prijava.jsp ako korisnik nije prijavljen.
	 *
	 * @param ServletContext context
	 * @return String pogled
	 */
	@GET
	@Path("pregledKorisnika")
	@View("pregledKorisnika.jsp")
	public String pregledKorisnika(@Context ServletContext context) {
		Token token = dohvatiToken(context);
		if(token != null) {
			RestKlijent klijent = new RestKlijent();
			List<Korisnik> korisnici = klijent.dohvatiKorisnike(token);
			model.put("korisnici", korisnici);
			
			PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("postavke");
			String sustavAdministratori = pbp.dajPostavku("sustav.administratori").toString();
			boolean clanGrupe = klijent.provjeriGrupuKorisnika(token, sustavAdministratori);
			model.put("clanGrupe", 0);
			if(clanGrupe)
				model.put("clanGrupe", 1);
			return null;
		}
		return "redirect:admin_poslovi/prijava"; 
	}
	
	/**
	 * Get metoda na putanji /obrisiToken/{korIme}. Metoda prima parametar putanje korisnicko ime kojemu je potrebno obrisati token. 
	 * Metoda poziva metodu rest klijenta za brisanje tokene no prvo dohvaca aktivan token. 
	 * Od rest klijenta dobiva instacu Response te provjerava odgovor. 
	 * Ako je token izbrisan prikazuje se poruka uspjeha ili greške na pogledi obrisiToken.jsp.
	 *
	 * @param ServletContext context
	 * @param String korIme parametar forme
	 */
	@GET
	@Path("obrisiToken/{korIme}")
	@View("obrisiToken.jsp")
	public void obrisiToken(@Context ServletContext context, @PathParam("korIme") String korIme) {
		RestKlijent klijent = new RestKlijent();
		Response odgovorServisa = klijent.obrisiToken(dohvatiToken(context), korIme);
		if(odgovorServisa.getStatus() == 200) {
			model.put("obrisan", odgovorServisa.readEntity(String.class));
			model.put("greska", null);
		}else {
			model.put("obrisan", null);
			model.put("greska", odgovorServisa.readEntity(String.class));
		}
	}
	
	/**
	 * Get metode na putanji /upravljanjePosluziteljem. Vraća pogled upravljanjePosluziteljem.jsp ako je korisnik prijavljen ili prijava.jsp ako nije.
	 * Metoda pomoću statične metode pomagala na poslužitelj šalje naredbu STATUS te se odgovor poslužitelja prikazuje na pogledu.
	 *
	 * @param ServletContext context
	 * @return String pogled
	 */
	@GET
	@Path("upravljanjePosluziteljem")
	@View("upravljanjePosluziteljem.jsp")
	public String upravljanjePosluziteljem(@Context ServletContext context) {
		Token token = dohvatiToken(context);
		if(token != null) {
			PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("postavke");
			String adresa = pbp.dajPostavku("adresa");
			int port = Integer.parseInt(pbp.dajPostavku("port"));
			String odgovor = Pomagala.posaljiKomandu("STATUS", adresa, port);
			prikaziOdgovor(odgovor);
			return null;
		}
		return "redirect:admin_poslovi/prijava"; 
	}
	
	/**
	 * Post metode na putanji /upravljanjePosluziteljem.
	 * Metoda pomoću statične metode pomagala na poslužitelj koji ima adresu i port iz postavka
	 * šalje naredbu koja je dobivena iz parametra forme. Ako se radi o naredbi LOAD
	 * tada se lista aerodroma dobiva od rest klijenta te se ta lista pretvara u json kako bi posluzitelj
	 * mogao procitati podatke. Na pogledu prikazuje se odgovor posluzitelja.
	 *
	 * @param ServletContext context
	 * @param String naredba parametar forme
	 * @return String pogled
	 */
	@POST
	@Path("upravljanjePosluziteljem")
	@View("upravljanjePosluziteljem.jsp")
	public String posaljiNaredbu(@Context ServletContext context, @FormParam("naredba") String naredba) {
		Token token = dohvatiToken(context);
		if(token != null) {
			PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("postavke");
			String adresa = pbp.dajPostavku("adresa");
			int port = Integer.parseInt(pbp.dajPostavku("port"));
			if(naredba.equals("LOAD")) {
				RestKlijent klijent = new RestKlijent();
				List<Aerodrom> aerodromi = klijent.dohvatiAerodrome(token);
				Gson gson = new Gson();
				String json = gson.toJson(aerodromi);
				naredba = "LOAD "+json;
			}
			String odgovor = Pomagala.posaljiKomandu(naredba, adresa, port);
			prikaziOdgovor(odgovor);
			return null;
		}
		return "redirect:admin_poslovi/prijava"; 
	}
	
	/**
	 * Pomoćna metoda koja dohvaća token iz konteksta ukoliko on postoji u kontekstu i ukoliko vrijedi token.
	 *
	 * @param ServletContext context
	 * @return Token token
	 */
	private Token dohvatiToken(ServletContext context) {
		Token token = (Token) context.getAttribute("token");
		if(token != null && tokenVrijedi(token)) {
			return token;
		}
		return null;
	}
	
	/**
	 * Pomoćna metoda koja provjerava da li token vrijedi pomoću metode rest klijenta. Vraća true ako token vrijedi inače false.
	 *
	 * @param Token token
	 * @return true, ako vrijedi
	 */
	private boolean tokenVrijedi(Token t) {
		RestKlijent klijent = new RestKlijent();
		return klijent.provjeriVrijednostTokena(t);
	}
	
	/**
	 * Pomoćna metoda na metodama pogleda upravljanjePosluziteljem.jsp koja u model postavlja poruku greške ili poruku uspjesne obrade koja
	 * je dobivena od poslužitelja.
	 *
	 * @param String odgovor
	 */
	private void prikaziOdgovor(String odgovor) {
		String[] podaci = odgovor.split(" ");
		if(podaci[0].equals("OK")) {
			model.put("odgovor", odgovor);
			model.put("greska", null);
		}else {
			model.put("greska", odgovor);
			model.put("odgovor", null);
		}
	}
	
}
