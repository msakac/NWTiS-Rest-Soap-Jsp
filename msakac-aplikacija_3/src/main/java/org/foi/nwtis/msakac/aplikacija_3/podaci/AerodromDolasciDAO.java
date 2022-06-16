package org.foi.nwtis.msakac.aplikacija_3.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.msakac.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.rest.podaci.AvionLeti;

/**
 * Klasa AerodromDolasciDAO.
 */
public class AerodromDolasciDAO {
	/**
	 * Metoda dohvace dolaske od datum do datuma ili od sekunde do sekunde u epoch vremenu za neki icao. 
	 * Vrsta dohvacanja odreduje se prema parametru vrsta. 0 znaci da se dohvaca u datumu, 1 u sekundama.
	 *
	 * @param String icao
	 * @param String Od
	 * @param String Do
	 * @param int vrsta
	 * @param PostavkeBazaPodataka pbp
	 * @return Lista dolazaka
	 */
	public List<AvionLeti> dohvatiDolaskeOdDo(String icao, String Od, String Do, int vrsta, PostavkeBazaPodataka pbp){
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String korisnik = pbp.getUserUsername();
		String lozinka = pbp.getUserPassword();
		
		long danOdEpoch = -1;
		long danDoEpoch = -1;
		
		if (vrsta == 1) {
			// u sekundama
			danOdEpoch = Long.parseLong(Od);
			danDoEpoch = Long.parseLong(Do);
		} else {
			// u formatu dd.mm.gggg
			String danOd = Od + " 00:00:00";
			String danDo = Do + " 23:59:59";
			danOdEpoch = dohvatiEpoch(danOd);
			danDoEpoch = dohvatiEpoch(danDo);
		}
		
		String upit = "SELECT estDepartureAirport, firstSeen, estArrivalAirport,"
				+ " lastSeen FROM AERODROMI_DOLASCI WHERE estArrivalAirport = ? "
				+ "AND firstSeen > ? AND firstSeen < ?;";
		
		try {
			Class.forName(pbp.getDriverDatabase(url));
			List<AvionLeti> avioni = new ArrayList<>();
			
			try (Connection konekcija = DriverManager.getConnection(url, korisnik, lozinka);
					PreparedStatement s = konekcija.prepareStatement(upit)) {
				s.setString(1, icao);
				s.setLong(2, danOdEpoch);
				s.setLong(3, danDoEpoch);
				
				ResultSet rs = s.executeQuery();
				
				while (rs.next()) {
					AvionLeti a = new AvionLeti();
					a.setEstArrivalAirport(rs.getString("estArrivalAirport"));
					a.setEstDepartureAirport(rs.getString("estDepartureAirport"));
					a.setFirstSeen(rs.getInt("firstSeen"));
					a.setLastSeen(rs.getInt("lastSeen"));
					avioni.add(a);
				}
				konekcija.close();
				return avioni;
			} catch (SQLException e) {
				Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, e);
			}
			
		} catch (ClassNotFoundException e) {
			Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, e);
		}

		return null;
	}
	
	/**
	 * Metoda konvertira datum u epoch vrijeme u sekundama.
	 *
	 * @param String datum
	 * @return epoch vrijeme u sekundama
	 */
	public long dohvatiEpoch(String datum) {
		//datum += " 00:00:00";
		System.out.println("Datum " + datum);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		Date date = null;
		try {
			date = dateFormat.parse(datum);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long epoch = date.getTime() / 1000;
		System.out.println("Epoch vrijeme: " + epoch);
		return epoch;
	}
}
