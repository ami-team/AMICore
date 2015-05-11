package net.hep.ami.command.certificate;

import java.io.*;
import java.util.*;
import java.security.*;
import java.security.cert.*;

import net.hep.ami.*;
import net.hep.ami.command.CommandAbstractClass;
import net.hep.ami.utility.*;
import net.hep.ami.utility.Cryptography.*;

public class GenerateCertificate extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	private String m_country;
	private String m_locality;
	private String m_organization;
	private String m_organizationalUnit;
	private String m_commonName;
	private String m_password;

	private int m_validity;

	/*---------------------------------------------------------------------*/

	public GenerateCertificate(Map<String, String> arguments, int transactionID) {
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

		m_password = arguments.containsKey("password") ? arguments.get("password")
		                                               : ""
		;

		if(arguments.containsKey("validity")) {

			try {
				m_validity = Integer.parseInt(arguments.get("validity"));

			} catch(NumberFormatException e) {
				m_validity = 1;
			}
		} else {
			m_validity = 1;
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		PrivateKey      caKey;
		X509Certificate caCrt;

		/*-----------------------------------------------------------------*/

		String fileName = ConfigSingleton.getConfigPathName() + File.separator + "ca.pem";

		/*-----------------------------------------------------------------*/

		try {
			InputStream inputStream = new FileInputStream(fileName);

			PEMTuple tuple = Cryptography.loadPEM(inputStream);

			if(tuple.privateKeys.length == 0) {
				throw new Exception("no private key in  `" + fileName + "`");
			}

			if(tuple.x509Certificates.length == 0) {
				throw new Exception("no certificate in  `" + fileName + "`");
			}

			caKey = tuple.privateKeys[0];
			caCrt = tuple.x509Certificates[0];

		} catch(Exception e) {
			throw new Exception("could not open `" + fileName + "`: " + e.getMessage());
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
