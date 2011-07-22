package maro.xmlrpc;
import redstone.xmlrpc.XmlRpcProxy;
import redstone.xmlrpc.XmlRpcFault;
import redstone.xmlrpc.XmlRpcArray;
import java.util.Random;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
public class EnvironmentClient {
  protected Random random;
  protected EM methods;
  private EnvironmentClient() { /* denied */ }
  public EnvironmentClient(String uri, String cipher) throws Exception {
    if (uri == null) throw new Exception("URI is nulled");
    methods
      = (EM)XmlRpcProxy.createProxy(new URL(uri), new Class[] {EM.class}, true);
    if (methods == null) throw new Exception("Cannot create proxy to methods...");
    random = new Random();
    connect();
  }
  protected void connect() throws Exception {
    // step 1 - handshake
    String challenge;
    int ret;
    int value;
    try {
      value = random.nextInt(32768); // n, par / 2 ou (impar * 3) + 1
      challenge = new String("i" + Integer.toString(value));
      ret = methods.handshake( challenge );
    } catch (Exception e) { // se falhou no decodificar desafio falhou!
      throw new Exception("Web Server is not trusted - " + e);
    }
    try {
      int resp = 0;
      if ((value % 2) == 0) resp = value / 2;
      else resp = 1 + 3 * value;

      // Quando a resposta do desafio veio errada falhou tb!
      // Entretanto, divide o try pra deixar melhor localizado
      if (ret < 0 || ret != resp) throw new Exception();
    } catch (Exception e) {
      throw new Exception("Web Server is not trusted - Response to Challenge is Wrong");
    }
    // step 2 - login
    login();
  }
  protected void login() throws Exception {
    return ; // the server not need this
  }
  public void logout() throws Exception {
    return ; // the server not need this
  }
  // [ [functor, [term], [annot]] ]
  private Object[] removeXRA(XmlRpcArray xra) throws Exception {
    List<Object> lo = null;
    if (xra == null) return null;
    lo = new ArrayList<Object>();
    for (Object o: xra) {
      if (o instanceof XmlRpcArray) {
        List<Object> elem = new ArrayList<Object>();
        for (Object e: ((XmlRpcArray) o)) {
          if (elem.size() >= 3)
            throw new Exception("Tuple perception is not correct!");
          if (e instanceof XmlRpcArray) {
            if (elem.size() == 2)
              elem.add( removeXRA((XmlRpcArray)e) );
            else if (elem.size() == 1)
              elem.add( ((XmlRpcArray)e).toArray() );
            else throw new Exception("Tuple percerption is invalid!");
          } else elem.add( e );
        }
        lo.add( elem.toArray() );
      } else
        throw new Exception("Tuple perception should be array or list");
    }
    return lo.toArray();
  }
  public Object[] doPercepts(String agName) throws Exception {
    try {
      Object[] ret = null;
      if ( methods.havePercepts(agName) ) {
        XmlRpcArray xra = (XmlRpcArray) methods.getPercepts(agName, true);
        if (xra != null) ret = removeXRA(xra);
      }
      return ret;
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception ("Internal Error - " + e);
    }
  }
  public Object[] getPercepts(String agName) throws Exception {
    try {
      Object[] ret = null;
      XmlRpcArray xra = (XmlRpcArray) methods.getPercepts(agName, false);
      if (xra != null) ret = removeXRA(xra);
      return ret;
    } catch (Exception e) {
      throw new Exception ("Internal Error - " + e);
    }
  }
  public boolean addPercept(Object ld, String agName) throws Exception {
    try {
      boolean ret;
      if (agName == null || agName.isEmpty())
        ret = methods.addPercept(ld);
      else
        ret = methods.addPerceptLocal(agName, ld);
      return ret;
    } catch (Exception e)
      throw new Exception ("Internal Error - " + e);
  }
  public boolean removePercept(Object ld, String agName) throws Exception {
    try {
      boolean ret;
      if (agName == null || agName.isEmpty())
        ret = methods.removePercept(ld);
      else
        ret = methods.removePerceptLocal(agName, ld);
      return ret;
    } catch (Exception e)
      throw new Exception ("Internal Error - " + e);
  }
  public boolean clearPercepts(String agName) throws Exception {
    try {
      boolean ret;
      if (agName == null || agName.isEmpty())
        ret = methods.clearPercepts();
      else
        ret = methods.clearPerceptsLocal(agName);
      return ret;
    } catch (Exception e)
      throw new Exception ("Internal Error - " + e);
  }
  public boolean executeAction(Object ld, String agName) throws Exception {
    try {
      boolean ret = methods.performAction(agName, ld);
      return ret;
    } catch (Exception e)
      throw new Exception ("Internal Error - " + e);
  }
}
