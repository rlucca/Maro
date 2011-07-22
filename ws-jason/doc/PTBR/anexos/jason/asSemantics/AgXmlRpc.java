package maro.asSemantics;
import jason.asSemantics.Intention;
import jason.asSemantics.Agent;
import jason.asSyntax.Literal;
import java.util.List;
public class AgXmlRpc extends Agent {
  /* O agente do lado Jason nao deve realizar
     nada, por isso essas duas funcoes principais
     encontram-se vazias. */
  public void buf(List<Literal> percepts) { }
  public List<Literal>[] brf(Literal beliefToAdd, Literal beliefToDel,  Intention i) {
    return null;
  }
}
