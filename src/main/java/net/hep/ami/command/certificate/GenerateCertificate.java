package net.hep.ami.command.certificate;

import java.io.*;
import java.util.*;
import java.security.*;
import java.security.cert.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;

public class GenerateCertificate extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	private String m_country = "";
	private String m_locality = "";
	private String m_organization = "";
	private String m_organizationalUnit = "";
	private String m_commonName = "";
	private String m_password = "";

	private int m_validity = 1;

	/*---------------------------------------------------------------------*/

	public GenerateCertificate(Map<String, String> arguments, long transactionID) {
		super(arguments, transactionID);

		if(arguments.containsKey("country")) {
			m_country = arguments.get("country");
		}

		if(arguments.containsKey("locality")) {
			m_locality = arguments.get("locality");
		}

		if(arguments.containsKey("organization")) {
			m_organization = arguments.get("organization");
		}

		if(arguments.containsKey("organizationalUnit")) {
			m_organizationalUnit = arguments.get("organizationalUnit");
		}

		if(arguments.containsKey("commonName")) {
			m_commonName = arguments.get("commonName");
		}

		if(arguments.containsKey("password")) {
			m_password = arguments.get("password");
		}

		if(arguments.containsKey("validity")) {

			try {
				m_validity = Integer.parseInt(arguments.get("validity"));

			} catch(NumberFormatException e) {
				/* IGNORE */
			}
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {
		/*-----------------------------------------------------------------*/

		PrivateKey      caKey;
		X509Certificate caCrt;

		try {
			caKey = Cryptography.loadPrivateKey(ConfigSingleton.class.getResourceAsStream("/ca.key"));

		} catch(Exception e) {
			throw new Exception("no CA crt provided");
		}

		try {
			caCrt = Cryptography.loadCertificate(ConfigSingleton.class.getResourceAsStream("/ca.crt"));

		} catch(Exception e) {
			throw new Exception("no CA key provided");
		}

		/*-----------------------------------------------------------------*/

		KeyPair keyPair = Cryptography.generateKeyPair(2048);

		X509Certificate certificate = Cryptography.generateCertificate(
				caKey,
				caCrt,
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

		KeyStore keyStore_JKS = Cryptography.generateKeyStore_JKS(keyPair.getPrivate(), certificate, m_password.toCharArray());
		KeyStore keyStore_PKCS12 = Cryptography.generateKeyStore_PKCS12(keyPair.getPrivate(), certificate, m_password.toCharArray());

		/*-----------------------------------------------------------------*/

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<Result><rowset><row>");

		/*-----------------------------------------------------------------*/

		ByteArrayOutputStream output;

		result.append("<field name=\"CLIENT_DN\"><![CDATA[" + Cryptography.getAMIShortDN(certificate.getSubjectX500Principal()) + "]]></field>");
		result.append("<field name=\"ISSUER_DN\"><![CDATA[" + Cryptography.getAMIShortDN(certificate.getIssuerX500Principal()) + "]]></field>");

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

		output = new ByteArrayOutputStream();

		try {
			keyStore_JKS.store(output, m_password.toCharArray());

			result.append("<field name=\"KEYSTORE_JKS\">");
			result.append(Cryptography.byteArrayToBase64String(output.toByteArray()));
			result.append("</field>");
		} finally {
			output.close();
		}

		output = new ByteArrayOutputStream();

		try {
			keyStore_PKCS12.store(output, m_password.toCharArray());

			result.append("<field name=\"KEYSTORE_P12\">");
			result.append(Cryptography.byteArrayToBase64String(output.toByteArray()));
			result.append("</field>");
		} finally {
			output.close();
		}

		/*-----------------------------------------------------------------*/

		result.append("</row></rowset></Result>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help() {
		return "Generate client or server certificates.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage() {
		return "-country=\"value\" -locality=\"value\" -organization=\"value\" -organizationalUnit=\"value\" -commonName=\"value\" -password=\"value\"";
	}

	/*---------------------------------------------------------------------*/
}
