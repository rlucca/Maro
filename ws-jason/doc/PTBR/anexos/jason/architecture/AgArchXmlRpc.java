package maro.architecture;
import maro.asSemantics.AgXmlRpc;
import maro.xmlrpc.AgentClient;
import maro.xmlrpc.Perceive;
import jason.architecture.AgArch;
import jason.mas2j.ClassParameters;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.IntendedMeans;
import jason.asSemantics.Circumstance;
import jason.asSemantics.ActionExec;
import jason.asSemantics.Intention;
import jason.asSemantics.Message;
import jason.asSemantics.Unifier;
import jason.asSemantics.Option;
import jason.asSemantics.Agent;
import jason.runtime.Settings;
import jason.asSyntax.PlanBodyImpl;
import jason.asSyntax.PlanBody;
import jason.asSyntax.Literal;
import jason.asSyntax.Trigger;
import jason.asSyntax.Plan;
import jason.JasonException;
import javax.management.ObjectName;
import java.util.List;
public class AgArchXmlRpc extends AgArch {
  protected String secretAgent;
  private AgentClient client; // ws handler
  private boolean sleeping;
  public AgArchXmlRpc () {
    super();
    client = null;
    sleeping = true;
  }
  private String unquoteP(Settings stts, String str) {
    String val;
    if (str == null)  return null;
    val = stts.getUserParameter(str);
    if (val == null)  return null;
    return ObjectName.unquote(val);
  }
  private AgentClient getClient() throws Exception {
    if (client == null) {
      Settings stts = getTS().getSettings();
      String url = unquoteP(stts, "url");
      if (url == null) {
        throw new Exception("URL is requeried to identify WS' Server!");
      }

      client = new AgentClient(getAgName(), url, unquoteP(stts, "secretAgent"));
    }
    return client;
  }
  @Override
  public void initAg(String agClass, ClassParameters bbPars, String asSrc, Settings stts)
      throws JasonException {
    try {
      // agClass, bbPars, asSrc is not used :p
      Agent ag = new AgXmlRpc();
      TransitionSystem ts = new TransitionSystem(ag, new Circumstance(), stts, this);
      ag.setBB(null);
      ag.initAg();
      ag.setTS(ts);
    } catch (Exception e)
      throw new JasonException("as2j: error creating the customised Agent class! - ", e);
  }
  public void stopAg() {
    super.stopAg();
    try {
      getClient().logout();
    } catch (Exception e) {
      System.err.println("as2j: error stop agent! - " + e);
      e.printStackTrace();
    }
  }
  public List<Literal> perceive() {
    try {
      List<Literal> p = super.perceive();
      String act = getClient().perceive(Perceive.dump(p));
      if (!act.isEmpty()) addAction(act);
    } catch (Exception e) {
      System.err.println("as2j: error in agent! - " + e);
      e.printStackTrace();
    }
    return null;
  }
  private void addAction(String act) {
    Intention intention = new Intention();
    IntendedMeans im;
    PlanBody bd;
    Trigger te;
    Literal li;
    Unifier un;
    Option opt;
    Plan plan;
    // configuration... literal
    li = Literal.parseLiteral(act);
    // configuration... trigger
    te = new Trigger(
        Trigger.TEOperator.add,
        Trigger.TEType.achieve,
        Literal.parseLiteral("rpc[source(self)]")
      );
    // configuration... plan
    bd = new PlanBodyImpl(
        PlanBody.BodyType.action,
        li);
    plan = new Plan(null, te, null, bd);
    // configuration... option
    un = new Unifier();
    opt = new Option(plan, un);
    // configuration... IntendedMeans
    im = new IntendedMeans(opt, te);
    // configuration... done!
    intention.push(im);
    getTS().getC().setAction( new ActionExec(li, intention) );
    sleeping = false;
  }
  public void act(ActionExec action, List<ActionExec> feedback) {
    super.act(action, feedback);
    sleeping = true;
  }
  public boolean canSleep() {
    return sleeping && super.canSleep();
  }
  public void checkMail() {
    super.checkMail();
    try {
      Object []os = getTS().getC().getMailBox().toArray();
      String []ms = null;
      String []rs = null;
      if (os != null && os.length > 0) {
        int i = 0;
        ms = new String[ os.length];
        for (Object o: os) {
          Message m = (Message) o;
          ms[i] = m.toString();
          i++;
        }
        getTS().getC().getMailBox().clear();
      }
      rs = getClient().checkMail(ms);
      if (rs != null && rs.length > 0) {
        for (String s : rs) { // r = a message
          if (s.isEmpty())
            continue;
          Message m = Message.parseMsg( s );
          // Quando enviado vazio, ele nao preenche automatico
          // por isso reconfiguramos para nulo
          if (m.getSender() != null && m.getSender().isEmpty())
            m.setSender(null);
          if (m.getReceiver() == null || m.getReceiver().isEmpty()) {
            broadcast(m);
          } else sendMsg(m);
        }
      }
    } catch (Exception e) {
      System.err.println("as2j: error checking Mailbox of agent! - " + e);
      e.printStackTrace();
    }
  }
}
