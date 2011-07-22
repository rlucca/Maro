package maro.xmlrpc;

import java.util.List;

interface PM {
	public Integer handshake(String challenge);
	public Integer login(String agName);
	public boolean logout(Integer id);

	public String perceive(Integer id, Object[] percept);
	public String act(Integer id, boolean eraseAll);

	public List<String> checkMail(Integer id, String[] news);
	public List<String> getMessages(Integer id);
}
