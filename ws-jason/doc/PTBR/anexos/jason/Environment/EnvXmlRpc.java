package maro.Environment;
import maro.xmlrpc.EnvironmentClient;
import maro.xmlrpc.Perceive;
import jason.environment.Environment;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import java.util.ArrayList;
import java.util.List;
public class EnvXmlRpc extends Environment {
  protected EnvironmentClient ec;
  protected String secret;
  protected String url;
  private EnvironmentClient getClient() throws Exception {
    if (ec == null) ec = new EnvironmentClient(url, secret);
    return ec;
  }
  public void init(String[] args) {
    url = null;
    secret = null;
    if (args.length == 1) {
      url = args[0];
    } else if (args.length == 2) {
      url = args[0];
      secret = args[1];
    } else {
      //throw new Exception("Please, inform a URL and, optionaly, a secret");
    }
    try {
      EnvironmentClient e = getClient();
      if (e == null) throw new Exception("Unknow error");
    } catch (Exception ex) {
      System.err.println("Error connecting to environment: " + ex);
      ex.printStackTrace();
    }
  }
  public List<Literal> getPercepts(String agName) {
    List<Literal> ll = new ArrayList<Literal>();
    try {
      Object[] lo = getClient().doPercepts(agName);
      if (lo == null) return null;
      ll.addAll( Perceive.undump(lo) );
    } catch (Exception ex) {
      System.err.println("Error in getPercepts: " + ex);
      ex.printStackTrace();
      return null;
    }
    return ll;
  }
  public List<Literal> consultPercepts(String agName) {
    List<Literal> ll = new ArrayList<Literal>();
    try {
      Object[] lo = getClient().getPercepts(agName);
      if (lo == null) return null;
      ll.addAll( Perceive.undump(lo) );
    } catch (Exception ex) {
      System.err.println("Error in consultPercepts: " + ex);
      ex.printStackTrace();
      return null;
    }
    return ll;
  }
  public void addPercept(Literal per) {
    try {
      boolean isOK = getClient().addPercept( Perceive.dumpLiteral(per), null );
      if (isOK == false) throw new Exception("Error in addPercept global");
    } catch (Exception ex) {
      System.err.println("Error in addPercept: " + ex);
      ex.printStackTrace();
    }
  }
  public void addPercept(String agName, Literal per) {
    try {
      boolean isOK = getClient().addPercept( Perceive.dumpLiteral(per), agName );
      if (isOK == false) throw new Exception("Error in addPercept local");
    } catch (Exception ex) {
      System.err.println("Error in addPercept: " + ex);
      ex.printStackTrace();
    }
  }
  public boolean removePercept(Literal per) {
    try {
      boolean isOK
        = getClient().removePercept( Perceive.dumpLiteral(per), null );
      if (isOK == false) throw new Exception("Error in removePercept global");
      return isOK;
    } catch (Exception ex) {
      System.err.println("Error in removePercept: " + ex);
      ex.printStackTrace();
    }
    return false;
  }
  public boolean removePercept(String agName, Literal per) {
    try {
      boolean isOK
        = getClient().removePercept( Perceive.dumpLiteral(per), agName );
      if (isOK == false) throw new Exception("Error in removePercept local");
      return isOK;
    } catch (Exception ex) {
      System.err.println("Error in removePercept: " + ex);
      ex.printStackTrace();
    }
    return false;
  }
  public void clearPercepts() {
    try {
      boolean isOK = getClient().clearPercepts( null );
      if (isOK == false) throw new Exception("Error in clearPercepts global");
    } catch (Exception ex) {
      System.err.println("Error in clearPercepts: " + ex);
      ex.printStackTrace();
    }
  }
  public void clearPercepts(String agName) {
    try {
      boolean isOK = getClient().clearPercepts( agName );
      if (isOK == false) throw new Exception("Error in clearPercepts local");
    } catch (Exception ex) {
      System.err.println("Error in clearPercepts: " + ex);
      ex.printStackTrace();
    }
  }
  public boolean executeAction(String agName, Structure act) {
    try {
      boolean isOK
        = getClient().executeAction(Perceive.dumpLiteral((Literal)act), agName);
      if (isOK == false) throw new Exception("Error in action from " + agName);
      return isOK;
    } catch (Exception ex) {
      System.err.println("Error in action from " + agName + ": " + ex);
      ex.printStackTrace();
    }
    return false;
  }
  /* As funcoes abaixo foram consideradas irelevantes para o TI
     e podem ser desenvolvidas no futuro sob-demanda. */
  public int removePerceptsByUnif(Literal per) {
    System.err.println("removePerceptsByUnif/1 do not use!!");
    return 0;
  }
  public int removePerceptsByUnif(String agName, Literal per) {
    System.err.println("removePerceptsByUnif/2 do not use!!");
    return 0;
  }
  public boolean containsPercept(Literal per) {
    System.err.println("containsPercept/2 do not use!!");
    return false;
  }
  public boolean containsPercept(String agName, Literal per) {
    System.err.println("containsPercept/2 do not use!!");
    return false;
  }
}
