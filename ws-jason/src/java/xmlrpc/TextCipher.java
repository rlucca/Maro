// This code is deprecated. We will use the other option.
package maro.xmlrpc;

import org.apache.commons.codec.binary.Base64;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;

class TextCipher {
	SecretKeySpec sks;
	Cipher c;

	public TextCipher() {
		sks = null;
		c = null;
	}

	public TextCipher(String algorithm, byte[] secret)
			throws Exception
	{
		if (secret.length != 16
				|| secret.length == 24
				|| secret.length == 32) {
			throw new Exception("Secret must be 16, 24 or 32 bytes");
		}
		sks = new SecretKeySpec(secret, algorithm);
		c = Cipher.getInstance(algorithm);
	}

	public String encode(String phrase)
				throws Exception
	{
		return encode(phrase.getBytes("UTF-8"));
	}

	public String decode(String phrase)
				throws Exception
	{
		return decode(
					Base64.decodeBase64(phrase)
				).trim();
	}

	public String encode(byte[] phrase)
				throws Exception
	{
		byte[] ret = phrase;
		if (c != null && sks != null) {
			// TODO: Testes foram sem criptografia
			// por simplicidade
			c.init(Cipher.ENCRYPT_MODE, sks);
			ret = c.doFinal(ret);
		}
		return Base64.encodeBase64String(ret);
	}

	// Base64 is removed in String param only
	public String decode(byte[] phrase)
				throws Exception
	{
		byte[] ret = phrase;
		if (c != null && sks != null) {
			// TODO: Testes foram sem criptografia
			// por simplicidade
			c.init(Cipher.DECRYPT_MODE, sks);
			ret = c.doFinal(ret);
		}
		return new String(ret);
	}
}
