package maro.xmlrpc;
import java.util.List;
interface EM {
  public Integer handshake(String challenge);
  public boolean havePercepts(String agName);
  public List<Object> getPercepts(String agName, boolean onlyUpdate);
  public boolean addPerceptLocal(String agName, Object o);
  public boolean addPercept(Object o);
  public boolean removePerceptLocal(String agName, Object o);
  public boolean removePercept(Object o);
  public boolean clearPerceptsLocal(String agName);
  public boolean clearPercepts();
  public boolean performAction(String agName, Object s);
}
