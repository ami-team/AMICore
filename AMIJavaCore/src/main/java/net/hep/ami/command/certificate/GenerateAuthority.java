package net.hep.ami.command.certificate;

import java.util.*;
import java.security.*;
import java.security.cert.*;

import net.hep.ami.command.*;
import net.hep.ami.utility.*;

public class GenerateAuthority extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	private String m_country;
	private String m_locality;
	private String m_organization;
	private String m_organizationalUnit;
	private String m_commonName;

	private int m_validity;

	/*---------------------------------------------------------------------*/

	public GenerateAuthority(Map<String, String> arguments, int transactionID) {
		super(arguments, transactionID);

		m_country = arguments.containsKey("country") ? arguments.get("country")
		                                             : ""
		;

		m_locality = arguments.containsKey("locality") ? arguments.get("locality")
		                                               : ""
		;

		m_organization = arguments.containsKey("organization") ? arguments.get("organization")
		                                                       : ""
		;

		m_organizationalUnit = arguments.containsKey("organizationalUnit") ? arguments.get("organizationalUnit")
		                                                                   : ""
		;

		m_commonName = arguments.containsKey("commonName") ? arguments.get("commonName")
		                                                   : ""
		;

		if(arguments.containsKey("validity")) {

			try {
				m_validity = Integer.parseInt(arguments.get("validity"));

			} catch(NumberFormatException e) {
				m_validity = 10;
			}
		} else {
			m_validity = 10;
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {
		/*-----------------------------------------------------------------*/

		KeyPair keyPair = Cryptography.generateKeyPair(2048);

		X509Certificate certificate = Cryptography.generateCA(
			keyPair.getPrivate(),
			keyPair.getPublic(),
			String.format(
				"CN=%s, OU=%s, O=%s, L=%s, C=%s",
				m_commonName,
				m_organizationalUnit,
				m_organization,
				m_locality,
				m_country
			),
			m_validity
		);

		/*-----------------------------------------------------------------*/

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<Result><rowset><row>");

		/*-----------------------------------------------------------------*/

		result.append("<field name=\"CLIENT_DN\"><![CDATA[" + Cryptography.getAMIName(certificate.getSubjectX500Principal()) + "]]></field>");
		result.append("<field name=\"ISSUER_DN\"><![CDATA[" + Cryptography.getAMIName(certificate.getIssuerX500Principal()) + "]]></field>");

		result.append("<field name=\"PRIVATE_KEY\">");
		result.append("-----BEGIN PRIVATE KEY-----\n");
		result.append(Cryptography.byteArrayToBase64String(keyPair.getPrivate().getEncoded()));
		result.append("-----END PRIVATE KEY-----\n");
		result.append("</field>");

		result.append("<field name=\"PUBLIC_KEY\">");
		result.append("-----BEGIN PUBLIC KEY-----\n");
		result.append(Cryptography.byteArrayToBase64String(keyPair.getPublic().getEncoded()));
		result.append("-----END PUBLIC KEY-----\n");
		result.append("</field>");

		result.append("<field name=\"CERTIFICATE\">");
		result.append("-----BEGIN CERTIFICATE-----\n");
		result.append(Cryptography.byteArrayToBase64String(certificate.getEncoded()));
		result.append("-----END CERTIFICATE-----\n");
		result.append("</field>");

		/*-----------------------------------------------------------------*/

		result.append("</row></rowset></Result>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help() {

		return "Generate CA certificates.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage() {

		return "-country=\"value\" -locality=\"value\" -organization=\"value\" -organizationalUnit=\"value\" -commonName=\"value\"";
	}

	/*---------------------------------------------------------------------*/
}
