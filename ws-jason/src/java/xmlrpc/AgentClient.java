package maro.xmlrpc;

import redstone.xmlrpc.XmlRpcProxy;
import redstone.xmlrpc.XmlRpcFault;

import java.util.Random;
import java.net.URL;

import java.util.List;

public class AgentClient {
	protected Random random;
	protected PM methods;
	protected String myName;
	protected int id;

	private AgentClient() {
		// denied use default constructor
	}

	public AgentClient(String ag, String uri, String cipher) throws Exception {
		if (uri == null) throw new Exception("URI is nulled");
		//if (cipher == null) tc = new TextCipher();
		//else                tc = new TextCipher("AES", cipher.getBytes("UTF-8"));
		methods = (PM) XmlRpcProxy.createProxy(new URL(uri), new Class[] { PM.class }, true);
		if (methods == null) throw new Exception("Cannot create proxy to methods...");
		random = new Random();
		myName = ag;
		connect();
	}

	protected void connect() throws Exception {
		// step 1 - handshake
		String challenge;
		int ret;
		int value;


		try {
			value = random.nextInt(32768); // 2 ** 15 ou 2 ^ 15
			challenge = new String("i" + Integer.toString(value));
			ret = methods.handshake( challenge );
		} catch (Exception e) { // se falhou no decodificar desafio falhou!
			throw new Exception("Web Server is not trusted - " + e);
		}

		try {
			// Quando a resposta do desafio veio errada falhou tb!
			// Entretanto, divide o try pra deixar melhor localizado
			if (ret != (value * value)) {
				throw new Exception();
			}
		} catch (Exception e) {
			throw new Exception("Web Server is not trusted - Response to Challenge is Wrong");
		}

		// step 2 - login
		login();
	}

	protected void login() throws Exception {
		Integer ret;

		try {
			ret = methods.login( myName );
		} catch (Exception e) { // se falhou no decodificar desafio falhou!
			throw new Exception("Internal Error - " + e);
		}

		id = ret;
	}

	public void logout() throws Exception {
		try {
			methods.logout( id );
		} catch (Exception e) {
			throw new Exception("Internal Error - " + e);
		}
	}

	public String perceive(List<Object> ll) throws Exception {
		String act = null;

		try {
			if (ll == null)
				act = methods.act( id, false );
			else if (ll.isEmpty())
				act = methods.act( id, true );
			else
				act = methods.perceive( id, ll.toArray() );
		} catch (Exception e) {
			throw new Exception("Internal Error - " + e);
		}

		return act;
	}

	public String[] checkMail(String []ms) throws Exception {
		List<String> ret = null;

		try {
			if (ms == null || ms.length == 0) {
				ret = methods.getMessages(id);
			} else {
				ret = methods.checkMail(id, ms);
			}
		} catch (Exception e) {
			throw new Exception("Internal Error - " + e);
		}

		if (ret == null) return null;
		return ret.toArray(new String[0]);
	}

}
