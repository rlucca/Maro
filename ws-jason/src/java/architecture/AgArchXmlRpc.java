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

/**
	Bla.

	A ordem de execucao eh a seguinte para o caso da infraestrutura centralizada eh o seguinte para um agente:
		1 CentralisedAgArch.initAg/6 eh chamada
		2 Baseada no MAS2J eh criado o AgArch correspondente que no caso pode ser a nossa classe :)
		3 no AgArch correspondente eh chamado setArchInfraTier/1
		4 no AgArch correspondente eh chamado initAg/4
	Quando ha mais de um agente do mesmo "tipo":
		1 Eh criado um agente com os passos acima
		2 Baseada no MAS2J eh criado o AgArch correspondente para o segundo agente
		3 no AgArch correspondente eh chamado setArchInfraTier/1 para o segundo agente
		4 um agente eh criado apartir do primeiro chamando clone/1 no agente passando o AgArch correspondente para o segundo agente
		5 o agente novo informa o AgArch correspondente o seu TransationSystem chamando no AgArch corresponde  a funcao setTS/1

Can be util know this equivalent?
       A call of this method like
          TransitionSystem ts = ag.initAg(arch, bb, asSrc, stts)
       can be replaced by
          new TransitionSystem(ag, new Circumstance(), stts, arch);
          ag.setBB(bb); // only if you use a custom BB
          ag.initAg(asSrc);
          TransitionSystem ts = ag.getTS();

reasoningCycle in TransitionSystem use there
	ag.buf/1					agArch.perceive/0
	agArch.checkMail/0			agArch.act/2
	agArch.sleep/0				agArch.isRunning/0
*/
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
		if (str == null)	return null;
		val = stts.getUserParameter(str);
		if (val == null)	return null;
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

    /**
     * Cria o agente do tipo AgXmlRpc sem nenhum fonte ASL associado,
	 * porem guarda a url (protocolo+host+porta) e o secretAgent (quando informado)
	 * para ser usado nas comunicacoes WS
     */
	@Override
    public void initAg(String agClass, ClassParameters bbPars, String asSrc, Settings stts)
							throws JasonException
	{
        try {
			// agClass, bbPars, asSrc is not used :p
			Agent ag = new AgXmlRpc();
			TransitionSystem ts = new TransitionSystem(ag, new Circumstance(), stts, this);
			ag.setBB(null);
			ag.initAg();
			ag.setTS(ts);
        } catch (Exception e) {
            throw new JasonException("as2j: error creating the customised Agent class! - ", e);
		}
	}

    /**
     * A call-back method called by the infrastructure tier 
     * when the agent is about to be killed.
     */
    public void stopAg() {
        super.stopAg();
		try {
			getClient().logout();
		} catch (Exception e) {
			//throw new JasonException("as2j: error stop agent! - ", e);
			System.err.println("as2j: error stop agent! - " + e);
			e.printStackTrace();
		}
    }

    /** Gets the agent's perception as a list of Literals.
     *  The returned list will be modified by Jason. (why?)
     */
    public List<Literal> perceive() {
		try {
			List<Literal> p = super.perceive();
			String act = getClient().perceive(Perceive.dump(p));
			if (!act.isEmpty()) {
				addAction(act);
			}
		} catch (Exception e) {
			System.err.println("as2j: error in agent! - " + e);
			e.printStackTrace();
		}
		//System.err.println(getAgName()+" in "+ getCycleNumber() + ":"+" perceived executed");
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
				li
			);

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

    /**
     * Executes the action <i>action</i> and, when finished, add it back in
     * <i>feedback</i> actions.
     */
    public void act(ActionExec action, List<ActionExec> feedback) {
		super.act(action, feedback);
		sleeping = true;
    }

    /** Returns true if the agent can enter in sleep mode. */
    public boolean canSleep() {
		return sleeping && super.canSleep();
		//return super.canSleep();
    }

    /** Reads the agent's mailbox and adds messages into 
        the agent's circumstance
	*/
    public void checkMail() {
        super.checkMail();
		// Dump of one message: [<mid229->mid228,claustrophobe,achieve,porter,~locked(door)>]
		//  irt: "-> mid228"
		//  toString:  "<"+msgId+irt+","+sender+","+ilForce+","+receiver+","+propCont+">"

		try {
			Object []os = getTS().getC().getMailBox().toArray();
			String []ms = null;
			String []rs = null;

			if (os != null && os.length > 0) { // TODO direto via toArray nao funcionou :(
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
					} else {
						sendMsg(m);
					}
				}
			}
		} catch (Exception e) {
			//throw new JasonException("as2j: error checking Mailbox of agent! - ", e);
			System.err.println("as2j: error checking Mailbox of agent! - " + e);
			e.printStackTrace();
		}

		//System.err.println(getAgName()+" in "+ getCycleNumber() + ":"+" isrun " + isRunning() + " cn " + super.canSleep() + " " + canSleep());
    }
}
