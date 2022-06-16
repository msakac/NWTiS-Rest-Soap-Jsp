package org.foi.nwtis.msakac.aplikacija_3.podaci;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Grupa {
	
	/** String naziv */
	private String naziv;
	
	/**
	 * Instancira novu grupu.
	 *
	 * @param String naziv
	 */
	public Grupa(String naziv) {
		this.naziv = naziv;
	}
	
}
