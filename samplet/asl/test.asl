		/*
			/1
			concept KKK [isA KKK concept] => agent("ricardo")
			/2
			sameAs KKK YYY
			~sameAs KKK YYY [differentFrom KKK YYY]
			-- lembrar
			--   ~X,     eh o nao eh X
			--   not(X), eh X nao esta na base de crenca

			dataRelation KKK VAL [dataRelation != topDataRelation]
			objectRelation KKK YYY [objectRelation != topObjectRelation]

			-- coisas como equivalence e disjoints sao desencessarias aqui porque sao da classe
			-- e aqui estamos lidando com individuos e eles 'herdam' essas informacoes nas acima.

			-- negative relations sao interessantes para nos?
		*/

vida_cheia(-100).
~vida(49). // nao tem vida(49)
foo(41).

agent("rafael").
agent("lucca").
object("porta").
agent("ricardo").
agent("test")[asl("test.asl")].
sameAs("ricardo", "lucca")[foo("rodrigues")].
~sameAs("ricardo", "rafael")[notAnnoted].
hasAppraisal("test", "testfoo")[like("copacabana")].
hasFamiliarity("testfoo", -30)[zim(55)].

!start.

+!start // OK
    : not(firstAgent) & agent(X)[asl(Y),source(Z)]
   <- .println("agent ", X, " ", Y, " ", Z, " recovered!"); +firstAgent; !!start.
+!start // FAIL
    : not(secondAgent) & object(X)[asl(Y),source(Z)]
   <- .println("agent is a object", X, " ", Y, " ", Z, " recovered! (when print is a fail)");
      +secondAgent; !!start.
+!start // OK
    : not(secondAgent) & object(X)
   <- .println("agent is a object", X, " recovered!"); +secondAgent; !!start.

+!start // OK
    : not(firstAppraisal) & hasAppraisal(X, Y)
   <- .println("appraisal ", X, " ", Y, " recovered!"); +firstAppraisal; !!start.
+!start // OK
    : not(secondAppraisal) & hasAppraisal(X, Y)[like(Z)]
   <- .println("appraisal ", X, " ", Y, " ", Z, " recovered!"); +secondAppraisal; !!start.
+!start // OK
    : not(thirdAppraisal) & isAppraisalOf(X, Y)
   <- .println("isAppraisalOf ", X, " ", Y, " recovered!"); +thirdAppraisal; !!start.

+!start // OK
    : not(firstFamiliarity) & hasFamiliarity(X, Y)
   <- .println("familiarity ", X, " ", Y, " recovered!"); +firstFamiliarity; !!start.
+!start // OK
    : not(secondFamiliarity) & hasFamiliarity(X, Y)[zim(Z)]
   <- .println("familiarity ", X, " ", Y, " ", Z, " recovered!"); +secondFamiliarity; !!start.

+!start // OK
    : not(thirdFamiliarity) & hasFamiliarity(X, Y)[zim(Z)] & Y < Z
   <- .println("familiarity ", X, " ", Y, " ", Z, ", and Y < Z!"); +thirdFamiliarity; !!start.
+!start // OK
    : not(fourFamiliarity) & hasFamiliarity(X, Y)[zim(Z)] & K=Y+Z
   <- .println("familiarity ", X, " ", Y, " ", Z, " ", K); +fourFamiliarity; !!start.

+!start
    : not(firstSame) & sameAs(X,Y)[foo(Z)]
   <- .println(X, " same ", Y, " with annotation foo = ", Z); +firstSame; !!start.
+!start
    : not(secondSame) & ~sameAs(X,Y)[notAnnoted]
   <- .println(X, " different ", Y); +secondSame; !!start.

+!start
    : not(firstErase) & firstAgent & X="rafael" & agent(X)
   <- .println("Recovered agent ", X); -agent(X); +firstErase; !!start.
+!start
    : not(firstEraseOK) & firstAgent & not(agent("rafael"))
   <- .println("Agent recovered before, not localized!"); +firstEraseOK; !!start.
+!start
    : not(secondErase) & firstAgent & agent(_)
   <- .println("Erasing all agents"); .abolish(agent(_)); +secondErase; !!start.
+!start
    : not(secondEraseOK) & not(agent(_))
   <- .println("Erasing all agents was occurred correctly"); +secondEraseOK; !!start.

+!start
    : not(change) & X="ricardo" & sameAs(X,Y)[foo(K)]
   <- .println("Updating ", X, " same ", Y); +change; -sameAs(X,Y)[foo(_)];
       // +sameAs(X,Y)[foo("ricardo lucca")];
      !!start.

+!start
    <- .println("5 seconds to kill MAS"); .wait(5000);
       .stopMAS.

+sameAs(X,Y)[foo(K)]
    <- .println("triggered: ", X, " equals to ", Y, " annoted foo = ", K).
