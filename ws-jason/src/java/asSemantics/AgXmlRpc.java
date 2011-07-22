package maro.asSemantics;

import jason.asSemantics.Intention;
import jason.asSemantics.Agent;
import jason.asSyntax.Literal;
//import jason.JasonException;

import java.util.List;

/**
	TODO Esse agente esta com duas funcoes vazias porque nao deveria fazer nada.
		 A classe AgArchXmlRpc cria o agente desse tipo somente sem nenhum arquivo ASL.
		 Esse detalhe parece ser o suficiente para ele nao fazer nada...
*/
public class AgXmlRpc extends Agent {

	public void buf(List<Literal> percepts) {
	}

	public List<Literal>[] brf(Literal beliefToAdd, Literal beliefToDel,  Intention i) {
		return null;
	}
}
