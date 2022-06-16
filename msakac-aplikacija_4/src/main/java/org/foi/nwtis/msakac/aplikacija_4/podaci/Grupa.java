package org.foi.nwtis.msakac.aplikacija_4.podaci;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Grupa {
	
	/** String naziv */
	String naziv;
	
	/**
	 * Kreira novu instancu Grupa.
	 *
	 * @param String naziv
	 */
	public Grupa(String naziv) {
		this.naziv = naziv;
	}
	
}
