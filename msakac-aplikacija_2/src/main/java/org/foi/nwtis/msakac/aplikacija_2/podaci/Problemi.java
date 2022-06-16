package org.foi.nwtis.msakac.aplikacija_2.podaci;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Problemi {

	/** ident */
	private String ident;
	
	/** description */
	private String description;
	
	/** stored */
	private Timestamp stored;
	
	/**
	 * Kreiran instancu klase problemi
	 *
	 * @param String ident
	 * @param String description
	 * @param Timestamp stored
	 */
	public Problemi(String ident, String description, Timestamp stored) {
		this.ident = ident;
		this.description = description;
		this.stored = stored;
	}
}
