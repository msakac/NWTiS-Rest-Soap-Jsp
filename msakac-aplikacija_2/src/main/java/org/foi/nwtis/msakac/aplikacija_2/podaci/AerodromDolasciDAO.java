package org.foi.nwtis.msakac.aplikacija_2.podaci;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
	 * Dodaje sve dolaske u bazu podataka. Otvara konekciju te za svaki dolazak poziva metodu za dodavanje u bazu.
	 *
	 * @param List<AvionLeti> a
	 * @param PostavkeBazaPodataka pbp
	 * @return Int broj dodanih podataka
	 */
	public int dodajSveDolaske(List<AvionLeti> a, PostavkeBazaPodataka pbp) {
		int brojacKreiranja = 0;
		
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String korisnik = pbp.getUserUsername();
		String lozinka = pbp.getUserPassword();
		
		try {
			Class.forName(pbp.getDriverDatabase(url));
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromPolasciDAO.class.getName()).log(Level.SEVERE, null, ex);
			ex.printStackTrace();
		}
		
	
		try (Connection konekcija = DriverManager.getConnection(url, korisnik, lozinka)) {
			
			for (AvionLeti avionLeti : a) {
				if(dodajDolazak(avionLeti, konekcija));
					brojacKreiranja++;
			}
			konekcija.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Logger.getLogger(AerodromDolasciDAO.class.getName()).log(Level.SEVERE, null, e);
		}
		return brojacKreiranja;
	}
	
	/**
	 * Dodaje jedan dolazak u bazu podataka
	 *
	 * @param AvionLeti a
	 * @param Connection konekcija
	 * @return True ako je dodan
	 */
	public boolean dodajDolazak(AvionLeti a, Connection konekcija) {

		String upit = "INSERT INTO AERODROMI_DOLASCI(icao24, firstSeen, estDepartureAirport, "
				+ "lastSeen, estArrivalAirport, callsign, estDepartureAirportHorizDistance, "
				+ "estDepartureAirportVertDistance, estArrivalAirportHorizDistance, estArrivalAirportVertDistance, "
				+ "departureAirportCandidatesCount, arrivalAirportCandidatesCount, `stored`) "
				+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			try (PreparedStatement s = konekcija.prepareStatement(upit)) {
				s.setString(1, a.getIcao24());
				s.setInt(2, a.getFirstSeen());
				s.setString(3, a.getEstDepartureAirport());
				s.setInt(4, a.getLastSeen());
				s.setString(5, a.getEstArrivalAirport());
				s.setString(6, a.getCallsign());
				s.setInt(7, a.getEstDepartureAirportHorizDistance());
				s.setInt(8, a.getEstDepartureAirportVertDistance());
				s.setInt(9, a.getEstArrivalAirportHorizDistance());
				s.setInt(10, a.getEstArrivalAirportVertDistance());
				s.setInt(11,a.getDepartureAirportCandidatesCount());
				s.setInt(12, a.getArrivalAirportCandidatesCount());
				s.setTimestamp(13, new Timestamp(System.currentTimeMillis()+7200000));
				
				int brojAzuriranja = s.executeUpdate();
				return brojAzuriranja == 1;
				
			} catch (SQLException e) {
				if(e.getErrorCode() == 1062) {
					//System.out.println("ERROR: Podatak vec postoji u tablici");
				}else {
					Logger.getLogger(AerodromDolasciDAO.class.getName()).log(Level.SEVERE, null, e);
				}
			}
		return false;
	}

	/**
	 * Metoda dohvaća dolaske na neki dan.
	 *
	 * @param String icao
	 * @param String datum
	 * @param PostavkeBazaPodataka pbp
	 * @return Lista dolazaka avionleti
	 */
	public List<AvionLeti> dohvatiDolaskeNaDan(String icao, String datum, PostavkeBazaPodataka pbp){
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String korisnik = pbp.getUserUsername();
		String lozinka = pbp.getUserPassword();
		String danPocetak = datum +" 00:00:00";
		String danKraj = datum + " 23:59:59";
		
		long pocetakEpoch = dohvatiEpoch(danPocetak);
		long krajEpoch = dohvatiEpoch(danKraj);
		
		String upit = "SELECT estDepartureAirport, firstSeen, estArrivalAirport,"
				+ " lastSeen FROM AERODROMI_DOLASCI WHERE estArrivalAirport = ? "
				+ "AND firstSeen > ? AND firstSeen < ?;";
		
		try {
			Class.forName(pbp.getDriverDatabase(url));
			List<AvionLeti> avioni = new ArrayList<>();
			
			try (Connection konekcija = DriverManager.getConnection(url, korisnik, lozinka);
					PreparedStatement s = konekcija.prepareStatement(upit)) {
				s.setString(1, icao);
				s.setLong(2, pocetakEpoch);
				s.setLong(3, krajEpoch);
				
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
	 * Dohvaća epoch na temelju datuma
	 *
	 * @param String datum
	 * @return Long epoch vrijeme u sekundama
	 */
	public long dohvatiEpoch(String datum) {
		datum += " 00:00:00";
		System.out.println("Datum " + datum);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		Date date = null;
		try {
			date = dateFormat.parse(datum);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long epoch = date.getTime() / 1000;
		System.out.println("Epoch vrijeme: " + epoch);
		return epoch;
	}
}
