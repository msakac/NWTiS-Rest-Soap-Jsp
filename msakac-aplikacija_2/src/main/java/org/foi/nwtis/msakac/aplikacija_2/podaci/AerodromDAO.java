package org.foi.nwtis.msakac.aplikacija_2.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.msakac.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.Lokacija;


/**
 * Klasa AerodromDAO
 */
public class AerodromDAO {
	

	/**
	 * Metoda dohvaća sve aerodrome iz baze podataka
	 *
	 * @param PostavkeBazePodataka pbp
	 * @return Lista aerodroma
	 */
	public List<Aerodrom> dohvatiSveAerodrome(PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String korisnik = pbp.getUserUsername();
		String lozinka = pbp.getUserPassword();
		String upit = "SELECT ident, name, iso_country, coordinates FROM airports;";

		try {
			Class.forName(pbp.getDriverDatabase(url));
			List<Aerodrom> aerodromi = new ArrayList<>();
			try (Connection konekcija = DriverManager.getConnection(url, korisnik, lozinka);
					Statement s = konekcija.createStatement();
					ResultSet rs = s.executeQuery(upit);) {
				while (rs.next()) {
					String icao = rs.getString("ident");
					String naziv = rs.getString("name");
					String drzava = rs.getString("iso_country");
					String lokacija = rs.getString("coordinates");
					String latitude = lokacija.split(",")[0];
					String longitude = lokacija.split(",")[1];
					Aerodrom a = new Aerodrom(icao, naziv, drzava, new Lokacija(latitude, longitude));
					aerodromi.add(a);
				}
				konekcija.close();
				return aerodromi;
			} catch (SQLException ex) {
				Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, ex);

			}
		} catch (ClassNotFoundException e) {
			Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}
	
	/**
	 * Metoda dohvaća aerodrom prema argumentu icao 
	 *
	 * @param String icao
	 * @param PostavkeBazePodataka pbp
	 * @return Instanca aerodroma
	 */
	public Aerodrom dohvatiAerodrom(String icao,PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String korisnik = pbp.getUserUsername();
		String lozinka = pbp.getUserPassword();
		
		String upit = "SELECT ident, name, iso_country, coordinates FROM airports WHERE ident = ?";
		
		try {
			Class.forName(pbp.getDriverDatabase(url));
			
			try (Connection konekcija = DriverManager.getConnection(url, korisnik, lozinka);
					PreparedStatement s = konekcija.prepareStatement(upit)) {
				
				s.setString(1, icao);
				ResultSet rs = s.executeQuery();
				
				while(rs.next()) {
					String ident = rs.getString("ident");
					String naziv = rs.getString("name");
					String drzava = rs.getString("iso_country");
					String lokacija = rs.getString("coordinates");
					String latitude = lokacija.split(",")[0];
					String longitude = lokacija.split(",")[1];
					
					Aerodrom a = new Aerodrom(ident, naziv, drzava, new Lokacija(latitude, longitude));
					konekcija.close();
					return a;
				}
			} catch (SQLException e) {
				Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, e);;
			}
		} catch (ClassNotFoundException e) {
			Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}
}






























