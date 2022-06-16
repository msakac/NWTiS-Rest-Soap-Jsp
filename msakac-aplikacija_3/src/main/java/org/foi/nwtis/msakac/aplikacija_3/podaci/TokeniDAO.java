package org.foi.nwtis.msakac.aplikacija_3.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.msakac.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

/**
 * Klasa TokeniDAO.
 */
public class TokeniDAO {
	
	/**
	 * Dohvaca token iz baze podataka prema korisnickom imenu. Dohvaca se samo token koji ima status=1 odnosno token koji je aktivan.
	 *
	 * @param String korIme
	 * @param PostavkeBazaPodataka pbp
	 * @return Token token
	 */
	public static Token dohvatiTokenPremaKorImenu(String korIme, PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String korisnik = pbp.getUserUsername();
		String lozinka = pbp.getUserPassword();
		
		String upit = "SELECT id, vrijediDo, status FROM TOKENI WHERE korIme = ? AND status=1;";
		
		try {
			Class.forName(pbp.getDriverDatabase(url));
			
			try (Connection konekcija = DriverManager.getConnection(url, korisnik, lozinka);
					PreparedStatement s = konekcija.prepareStatement(upit)) {
				s.setString(1, korIme);
				ResultSet rs = s.executeQuery();
				
				while(rs.next()) {
					int id = rs.getInt("id");
					int vrijediDo = rs.getInt("vrijediDo");
					boolean status = rs.getBoolean("status");
					Token t = new Token(id, korIme, vrijediDo, status);
					konekcija.close();
					return t;
				}
			} catch (SQLException e) {
				Logger.getLogger(TokeniDAO.class.getName()).log(Level.SEVERE, null, e);
			}
			
		} catch (ClassNotFoundException e) {
			Logger.getLogger(TokeniDAO.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}
	
	/**
	 * Metoda kreira novi token u bazi podataka sa podacima korisnickom imena i vremena do kojeg traje. Token je po defaultu aktivan odnosno status je 1.
	 *
	 * @param String korisnicko ime
	 * @param int trajanje
	 * @param PostavkeBazaPodataka pbp
	 * @return Token token
	 */
	public static Token kreirajNoviToken(String korIme, int trajanje, PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String korisnik = pbp.getUserUsername();
		String lozinka = pbp.getUserPassword();
		String upit = "INSERT TOKENI(korIme, status, vrijediDo) VALUES(?,?,?);";
		
		try {
			Class.forName(pbp.getDriverDatabase(url));
			try (Connection konekcija = DriverManager.getConnection(url, korisnik, lozinka);
					PreparedStatement s = konekcija.prepareStatement(upit)) {
				s.setString(1, korIme);
				s.setInt(2,1);
				Long trajeDo = (System.currentTimeMillis()+7200000)/1000+trajanje;
				s.setInt(3, trajeDo.intValue());
				int brojAzuriranja = s.executeUpdate();
				konekcija.close();
				if(brojAzuriranja == 1) {
					Token token = TokeniDAO.dohvatiTokenPremaKorImenu(korIme, pbp);
					return token;
				}
			} catch (SQLException e) {
				Logger.getLogger(TokeniDAO.class.getName()).log(Level.SEVERE, null, e);
			}
			
		} catch (ClassNotFoundException e) {
			Logger.getLogger(TokeniDAO.class.getName()).log(Level.SEVERE, null, e);

		}
		return null;	
	}
	
	/**
	 * Metoda iz baze dohvaca token prema tokenu gdje je korisnicko ime trazeno i id tokena trazen.
	 *
	 * @param String korIme
	 * @param int token
	 * @param PostavkeBazaPodataka pbp
	 * @return Token token
	 */
	public static Token dohvatiTokenPremaTokenu(String korIme, int token, PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String korisnik = pbp.getUserUsername();
		String lozinka = pbp.getUserPassword();
		
		String upit = "SELECT id, vrijediDo, status FROM TOKENI WHERE korIme = ? AND id = ?;";
		
		try {
			Class.forName(pbp.getDriverDatabase(url));
			
			try (Connection konekcija = DriverManager.getConnection(url, korisnik, lozinka);
					PreparedStatement s = konekcija.prepareStatement(upit)) {
				s.setString(1, korIme);
				s.setInt(2, token);
				ResultSet rs = s.executeQuery();
				
				while(rs.next()) {
					int id = rs.getInt("id");
					int vrijediDo = rs.getInt("vrijediDo");
					boolean status = rs.getBoolean("status");
					Token t = new Token(id,korIme, vrijediDo, status);
					konekcija.close();
					return t;
				}
			} catch (SQLException e) {
				Logger.getLogger(TokeniDAO.class.getName()).log(Level.SEVERE, null, e);
			}
			
		} catch (ClassNotFoundException e) {
			Logger.getLogger(TokeniDAO.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}
	

	/**
	 * Metoda u bazi podataka mijenja status tokena iz status 1 u status 0 odnosno da token vise ne vrijedi.
	 *
	 * @param Token token
	 * @param PostavkeBazaPodataka pbp
	 * @return true, ako je status promijenjen
	 */
	public static boolean promijeniStatusTokena(Token token, PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "UPDATE TOKENI SET status = 0 WHERE id = ?";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                     Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                     PreparedStatement s = con.prepareStatement(upit)) {

                s.setInt(1, token.getId());

                int brojAzuriranja = s.executeUpdate();

                return brojAzuriranja == 1;

            } catch (SQLException ex) {
                Logger.getLogger(TokeniDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TokeniDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
	}


}
