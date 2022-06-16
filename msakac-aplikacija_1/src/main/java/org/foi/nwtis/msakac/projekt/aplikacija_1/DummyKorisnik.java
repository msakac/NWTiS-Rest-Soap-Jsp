package org.foi.nwtis.msakac.projekt.aplikacija_1;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;

public class DummyKorisnik {

	public static void main(String[] args) {
		StringBuilder builder = new StringBuilder();
		for (String str : args) {
			builder.append(str);
			builder.append(" ");
		}
		builder.deleteCharAt(builder.length() - 1);
		System.out.println(posaljiKomandu(builder.toString()));

	}
	
	public static String posaljiKomandu(String komanda) {
		try {

			InetSocketAddress isa = new InetSocketAddress("localhost", 8000);
			Socket veza = new Socket();
			veza.connect(isa, 900);

			InputStreamReader isr = new InputStreamReader(veza.getInputStream(), Charset.forName("UTF-8"));
			OutputStreamWriter osw = new OutputStreamWriter(veza.getOutputStream(), Charset.forName("UTF-8"));

			osw.write(komanda);
			osw.flush();
			veza.shutdownOutput();
			StringBuilder tekst = new StringBuilder();
			while (true) {
				int i = isr.read();
				if (i == -1) {
					break;
				}
				tekst.append((char) i);
			}
			veza.shutdownInput();
			veza.close();
			return tekst.toString();
		} catch (SocketException e) {
			return "ERROR: Problem pri spajanju na server";
		} catch (IOException ex) {
			return "ERROR :Problem pri spajanju na server";
		}
	}

}
