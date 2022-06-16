package org.foi.nwtis.msakac.aplikacija_3.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.msakac.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;


/**
 * Klasa AerodromPraceniDAO.
 */
public class AerodromPraceniDAO {
	
	/**
	 * Dodaje aerodrom za pratiti u bazu podataka ako taj icao postoji i ako vec nije dodan u pracene aerodrome
	 *
	 * @param String icao
	 * @param PostavkeBazaPodataka pbp
	 * @return true, ako je dodan
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














