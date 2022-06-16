package org.foi.nwtis.msakac.aplikacija_3.podaci;

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
import org.foi.nwtis.podaci.Korisnik;

/**
 * Klasa KorisniciDAO.
 */
public class KorisniciDAO {

	/**
	 * Metoda iz baze podataka dohvaca korisnika prema korisnickom imenu i lozinki ako su iste.
	 *
	 * @param PostavkeBazaPodataka pbp
	 * @param String korIme
	 * @param String lozinka
	 * @return Korisnik korisnik
	 */
	public static Korisnik dohvatiKorisnika(PostavkeBazaPodataka pbp, String korIme, String lozinka) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String user = pbp.getUserUsername();
		String password = pbp.getUserPassword();
		String upit = "SELECT korIme, ime, prezime, lozinka, email FROM KORISNICI WHERE korIme = ? AND lozinka= ?";

		try {
			Class.forName(pbp.getDriverDatabase(url));
			try (Connection konekcija = DriverManager.getConnection(url, user, password);
					PreparedStatement s = konekcija.prepareStatement(upit)) {
				s.setString(1, korIme);
				s.setString(2, lozinka);
				ResultSet rs = s.executeQuery();

				while (rs.next()) {
					String korisnickoIme = rs.getString("korIme");
					String pw = rs.getString("lozinka");
					String ime = rs.getString("ime");
					String prezime = rs.getString("prezime");
					String email = rs.getString("email");
					Korisnik k = new Korisnik(korisnickoIme, ime, prezime, pw, email);
					konekcija.close();
					return k;
				}

			} catch (SQLException ex) {
				Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, ex);

			}
		} catch (ClassNotFoundException e) {
			Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}

	/**
	 * Metoda iz baze podataka dohvaca korisnika koji pripada trazenoj grupi koja je zapisana u konfiguraciji.
	 *
	 * @param PostavkeBazaPodataka pbp
	 * @param String korIme
	 * @param String lozinka
	 * @return Korisnik korisnik
	 */
	public static Korisnik dohvatiKorisnikaPremaGrupi(PostavkeBazaPodataka pbp, String korIme, String lozinka) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String user = pbp.getUserUsername();
		String password = pbp.getUserPassword();
		String grupaKorisnika = pbp.dajPostavku("sustav.administratori");
		String upit = "SELECT k.korIme, k.ime, k.prezime, k.lozinka, k.email "
				+ "FROM KORISNICI k, ULOGE u WHERE k.korIme = ? AND k.lozinka= ? "
				+ "AND u.grupa = ? AND u.korisnik = k.korIme;";

		try {
			Class.forName(pbp.getDriverDatabase(url));
			try (Connection konekcija = DriverManager.getConnection(url, user, password);
					PreparedStatement s = konekcija.prepareStatement(upit)) {
				s.setString(1, korIme);
				s.setString(2, lozinka);
				s.setString(3, grupaKorisnika);
				ResultSet rs = s.executeQuery();

				while (rs.next()) {
					String korisnickoIme = rs.getString("korIme");
					String ime = rs.getString("ime");
					String prezime = rs.getString("prezime");
					String email = rs.getString("email");
					Korisnik k = new Korisnik(korisnickoIme, ime, prezime, lozinka, email);
					konekcija.close();
					return k;
				}

			} catch (SQLException ex) {
				Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, ex);

			}
		} catch (ClassNotFoundException e) {
			Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}

	/**
	 * Metoda dohvaca sve korisnike iz baze podataka.
	 *
	 * @param PostavkeBazaPodataka pbp
	 * @return Lista korisnika
	 */
	public static List<Korisnik> dohvatiSveKorisnike(PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String korisnik = pbp.getUserUsername();
		String lozinka = pbp.getUserPassword();
		String upit = "SELECT korIme, ime, prezime, lozinka, email FROM KORISNICI;";

		try {
			Class.forName(pbp.getDriverDatabase(url));
			List<Korisnik> korisnici = new ArrayList<>();
			try (Connection konekcija = DriverManager.getConnection(url, korisnik, lozinka);
					Statement s = konekcija.createStatement();
					ResultSet rs = s.executeQuery(upit);) {
				while (rs.next()) {
					String korIme = rs.getString("korIme");
					String ime = rs.getString("ime");
					String prezime = rs.getString("prezime");
					String pw = rs.getString("lozinka");
					String email = rs.getString("email");
					Korisnik k = new Korisnik(korIme, ime, prezime, pw, email);
					korisnici.add(k);
				}
				konekcija.close();
				return korisnici;
			} catch (SQLException ex) {
				Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, ex);

			}
		} catch (ClassNotFoundException e) {
			Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}

	/**
	 * Metoda dohvaca korisnika iz baze podataka prema korisnickom imenu.
	 *
	 * @param PostavkeBazaPodataka pbp
	 * @param String korIme
	 * @return Korisnik korisnik
	 */
	public static Korisnik dohvatiKorisnikaPremaImenu(PostavkeBazaPodataka pbp, String korIme) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String user = pbp.getUserUsername();
		String password = pbp.getUserPassword();
		String upit = "SELECT korIme, ime, prezime, lozinka, email FROM KORISNICI WHERE korIme = ?;";

		try {
			Class.forName(pbp.getDriverDatabase(url));
			try (Connection konekcija = DriverManager.getConnection(url, user, password);
					PreparedStatement s = konekcija.prepareStatement(upit)) {
				s.setString(1, korIme);
				ResultSet rs = s.executeQuery();

				while (rs.next()) {
					String korisnickoIme = rs.getString("korIme");
					String pw = rs.getString("lozinka");
					String ime = rs.getString("ime");
					String prezime = rs.getString("prezime");
					String email = rs.getString("email");
					Korisnik k = new Korisnik(korisnickoIme, ime, prezime, pw, email);
					konekcija.close();
					return k;
				}

			} catch (SQLException ex) {
				Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, ex);

			}
		} catch (ClassNotFoundException e) {
			Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}

	/**
	 * Metoda dodaje korisnika u bazu podataka ako korisnicko ime nije zauzeto.
	 *
	 * @param PostavkeBazaPodataka pbp
	 * @param Korisnik k
	 * @return true, ako je korisnik dodan
	 */
	public static boolean dodajKorisnika(PostavkeBazaPodataka pbp, Korisnik k) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String user = pbp.getUserUsername();
		String password = pbp.getUserPassword();
		String upit = "INSERT INTO KORISNICI(korIme, ime, prezime, lozinka, email) VALUES(?,?,?,?,?);";

		try {
			Class.forName(pbp.getDriverDatabase(url));
			try (Connection konekcija = DriverManager.getConnection(url, user, password);
					PreparedStatement s = konekcija.prepareStatement(upit)) {
				s.setString(1, k.getKorIme());
				s.setString(2, k.getIme());
				s.setString(3, k.getPrezime());
				s.setString(4, k.getLozinka());
				s.setString(5, k.getEmail());
				int brojAzuriranja = s.executeUpdate();
				konekcija.close();
				return brojAzuriranja == 1;
			}

		} catch (SQLException ex) {
			Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ClassNotFoundException e) {
			Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, e);
		}
		return false;
	}
	
	/**
	 * Metoda dohvaca sve grupe nekog korisnika.
	 *
	 * @param PostavkeBazaPodataka pbp
	 * @param String korIme
	 * @return Lista grupa korisnika
	 */
	public static List<Grupa> dohvatiGrupeKorisnika(PostavkeBazaPodataka pbp, String korIme) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String user = pbp.getUserUsername();
		String password = pbp.getUserPassword();
		String upit = "SELECT grupa FROM ULOGE WHERE korisnik = ?";

		try {
			Class.forName(pbp.getDriverDatabase(url));
			List<Grupa> grupe = new ArrayList<>();
			try (Connection konekcija = DriverManager.getConnection(url, user, password);
					PreparedStatement s = konekcija.prepareStatement(upit)) {
				s.setString(1, korIme);
				ResultSet rs = s.executeQuery();

				while (rs.next()) {
					String naziv = rs.getString("grupa");
					Grupa g = new Grupa(naziv);
					grupe.add(g);
				}
				konekcija.close();
				return grupe;

			} catch (SQLException ex) {
				Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, ex);

			}
		} catch (ClassNotFoundException e) {
			Logger.getLogger(AerodromDAO.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}

}
