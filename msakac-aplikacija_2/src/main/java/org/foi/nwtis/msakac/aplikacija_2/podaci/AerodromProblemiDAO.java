package org.foi.nwtis.msakac.aplikacija_2.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.msakac.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;


/**
 * Klasa AerodromProblemiDAO.
 */
public class AerodromProblemiDAO {
	
	/**
	 * Metoda dodaje problem u bazu podataka
	 *
	 * @param String icao
	 * @param String poruka
	 * @param PostavkeBazaPodataka pbp
	 * @return true, ako je dodan
	 */
	public boolean dodajProblem(String icao, String poruka, PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String korisnik = pbp.getUserUsername();
		String lozinka = pbp.getUserPassword();
		String upit = "INSERT INTO AERODROMI_PROBLEMI(ident, description, `stored`) "
				+ "VALUES(?, ?, ?)";
		try {
			Class.forName(pbp.getDriverDatabase(url));
			
			try (Connection konekcija = DriverManager.getConnection(url, korisnik, lozinka);
					PreparedStatement s = konekcija.prepareStatement(upit)) {
				s.setString(1, icao);
				s.setString(2, poruka);
				s.setTimestamp(3, new Timestamp(System.currentTimeMillis()+7200000));
				
				int brojAzuriranja = s.executeUpdate();
				konekcija.close();
				return brojAzuriranja == 1;
				
			} catch (SQLException e) {
				Logger.getLogger(AerodromPolasciDAO.class.getName()).log(Level.SEVERE, null, e);
			}
		} catch (ClassNotFoundException e) {
			Logger.getLogger(AerodromPolasciDAO.class.getName()).log(Level.SEVERE, null, e);
		}
		return false;
	}
}
