package maro.architecture;

import maro.asSemantics.AgXmlRpc;

import jason.architecture.AgArch;
import jason.mas2j.ClassParameters;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Circumstance;
import jason.asSemantics.ActionExec;
import jason.asSemantics.Message;
import jason.asSemantics.Agent;
import jason.runtime.Settings;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import jason.JasonException;

import javax.management.ObjectName;
import java.util.LinkedList;
import java.util.List;

public class Baka extends AgArch {

    public void checkMail() {
		System.out.println("before checkMail: " + getTS().getC().getMailBox());
        super.checkMail();
		System.out.println("after checkMail: " + getTS().getC().getMailBox());
	}
}
