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
 * The Class AerodromPolasciDAO.
 */
public class AerodromPolasciDAO {

	/**
	 * Metoda dohvaca polaske od datuma do datuma ili od sekunde do sekunde u epochu za neki icao. Vrsta dohvacanja odreduje se prema
	 * parametru vrsta 0 znaci u datumu 1 znaci u sekundama.
	 *
	 * @param String icao
	 * @param String Od
	 * @param String Do
	 * @param int vrsta
	 * @param PostavkeBazaPodataka pbp
	 * @return lista polazaka
	 */
	public List<AvionLeti> dohvatiPolaskeOdDo(String icao, String Od, String Do, int vrsta, PostavkeBazaPodataka pbp) {
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
				+ " lastSeen, arrivalAirportCandidatesCount,departureAirportCandidatesCount,"
				+ "estArrivalAirportHorizDistance, estArrivalAirportVertDistance,estDepartureAirportHorizDistance,"
				+ "estDepartureAirportVertDistance FROM AERODROMI_POLASCI WHERE estDepartureAirport = ? "
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
					a.setArrivalAirportCandidatesCount(rs.getInt("arrivalAirportCandidatesCount"));
					a.setDepartureAirportCandidatesCount(rs.getInt("departureAirportCandidatesCount"));
					a.setEstArrivalAirportHorizDistance(rs.getInt("estArrivalAirportHorizDistance"));
					a.setEstArrivalAirportVertDistance(rs.getInt("estArrivalAirportVertDistance"));
					a.setEstDepartureAirportHorizDistance(rs.getInt("estDepartureAirportHorizDistance"));
					a.setEstDepartureAirportVertDistance(rs.getInt("estDepartureAirportVertDistance"));
					avioni.add(a);
				}

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
	 * Metoda konvertira datum u sekunde u epoch vremenu.
	 *
	 * @param String datum
	 * @return vrijeme u sekundama
	 */
	public long dohvatiEpoch(String datum) {
		// datum += " 00:00:00";
		System.out.println("Datum: " + datum);
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
