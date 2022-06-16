package org.foi.nwtis.msakac.aplikacija_2.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.msakac.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.Lokacija;

/**
 * Klasa AerodromPraceniDAO.
 */
public class AerodromPraceniDAO {
	
	/**
	 * Metoda dohvaća sve praćene aerodroma
	 *
	 * @param PostavkeBazaPodataka pbp
	 * @return Lista aerodroma
	 */
	public List<Aerodrom> dohvatiSvePraceneAerodrome(PostavkeBazaPodataka pbp){
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String korisnik = pbp.getUserUsername();
		String lozinka = pbp.getUserPassword();
		String upit = "SELECT a.ident, a.name, a.iso_country, a.coordinates FROM airports a INNER JOIN AERODROMI_PRACENI b ON a.ident = b.ident;";
		
		try {
			Class.forName(pbp.getDriverDatabase(url));
			
			List<Aerodrom> praceniAerodromi = new ArrayList<>();

			try (Connection konekcija = DriverManager.getConnection(url, korisnik, lozinka);
					Statement s = konekcija.createStatement();
					ResultSet rs = s.executeQuery(upit);) {
				while(rs.next()) {
					String icao = rs.getString("ident");
					String naziv = rs.getString("name");
					String drzava = rs.getString("iso_country");
					String lokacija = rs.getString("coordinates");
					String latitude = lokacija.split(",")[0];
					String longitude = lokacija.split(",")[1];
					Aerodrom a = new Aerodrom(icao, naziv, drzava, new Lokacija(latitude, longitude));
					praceniAerodromi.add(a);
				}
				konekcija.close();
				return praceniAerodromi;
			} catch (SQLException e) {
				Logger.getLogger(AerodromPraceniDAO.class.getName()).log(Level.SEVERE, null, e);
				System.out.println("Greska jedan "+ e.getMessage());
			}
		} catch (ClassNotFoundException e) {
			Logger.getLogger(AerodromPraceniDAO.class.getName()).log(Level.SEVERE, null, e);
			System.out.println("Greska dva");
		}
		return null;
	}
	
	/**
	 * Metoda dodaje aerodrome za pratiti u bazu podataka
	 *
	 * @param String icao
	 * @param PostavkeBazaPodataka pbp
	 * @return True ako je dodan
	 */
	public boolean dodajAerodromZaPratiti(String icao, PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String korisnik = pbp.getUserUsername();
		String lozinka = pbp.getUserPassword();
		String upit = "INSERT INTO AERODROMI_PRACENI(ident, `stored`) VALUES(?,?);";
		
		//provjera dal postoji icao
		AerodromDAO adao = new AerodromDAO();
		Aerodrom a = adao.dohvatiAerodrom(icao, pbp);
		if(a==null) return false;
		
		//treba provjera dal je vec u tablici
		
		try {
			Class.forName(pbp.getDriverDatabase(url));
			try (Connection konekcija = DriverManager.getConnection(url, korisnik, lozinka);
					PreparedStatement s = konekcija.prepareStatement(upit)) {
				s.setString(1, icao);
				s.setTimestamp(2, new Timestamp(System.currentTimeMillis()+7200000));
				int brojAzuriranja = s.executeUpdate();
				konekcija.close();
				return brojAzuriranja == 1;
			} catch (SQLException e) {
				Logger.getLogger(AerodromDolasciDAO.class.getName()).log(Level.SEVERE, null, e);
			}
			
		} catch (ClassNotFoundException e) {
			Logger.getLogger(AerodromDolasciDAO.class.getName()).log(Level.SEVERE, null, e);

		}
		return false;	
	}
}














