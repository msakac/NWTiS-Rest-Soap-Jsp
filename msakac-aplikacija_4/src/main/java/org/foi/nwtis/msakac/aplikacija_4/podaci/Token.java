package org.foi.nwtis.msakac.aplikacija_4.podaci;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Token {

	/** int id */
	int id;
	
	/** String korIme */
	private String korIme;
	
	/** int vrijediDo */
	private int vrijediDo;
	
	/** boolean status */
	private boolean status;
	
	/**
	 * Instancira novi Token
	 *
	 * @param int id
	 * @param String korIme
	 * @param int vrijediDo
	 * @param boolean status
	 */
	public Token(int id, String korIme, int vrijediDo, boolean status) {
		this.id = id;
		this.korIme=korIme;
		this.vrijediDo = vrijediDo;
		this.status = status;
	}
}
