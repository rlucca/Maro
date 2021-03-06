agent(john).
agent(jose).
agent(dilu).
agent(millie).
object(television).

// ESSA LINHA EH IMPORTANTE PARA AS EMOCOES EM COMPOUND!!!!!!!!!!!!!!
~sameAs(john, jose, dilu, millie, television, milliesCar).

// likelihood
high(lhigh).
mid(lmid).
low(llow).
none(lnone).

// threshold
//   note: Essas crencas saoh removidas porque o conceito `Setup' eh carregado
//         para memoria e liberado da ontologia. Dessa forma, o agente naoh
//         conhece seu threshold do mesmo que nao conhece sua emocaoh.
hasSetup(millie, setup1).
hasSetup(millie, setup2).
hasSetup(millie, setup3).
hasSetup(millie, setup4).
hasSetup(millie, setup5).
hasSetup(millie, setup6).
hasSetup(millie, setup7).
hasSetup(millie, setup8).
hasSetup(millie, setup9).
hasSetup(millie, setup10).
hasSetup(millie, setup11).
hasSetup(millie, setup12).
hasSetup(millie, setup13).
hasSetup(millie, setup14).
hasSetup(millie, setup15).
hasSetup(millie, setup16).
hasSetup(millie, setup17).
hasSetup(millie, setup18).
hasSetup(millie, setup19).
hasSetup(millie, setup20).
hasSetup(millie, setup21).
hasSetup(millie, setup22).

hasThreshold(setup1, 10).
hasThresholdType(setup1, "Joy").
hasThreshold(setup2, 11).
hasThresholdType(setup2, "Distress").
hasThreshold(setup3, 12).
hasThresholdType(setup3, "Hope").
hasThreshold(setup4, 13).
hasThresholdType(setup4, "Fear").
hasThreshold(setup5, 14).
hasThresholdType(setup5, "FearsConfirmed").
hasThreshold(setup6, 15).
hasThresholdType(setup6, "Satisfaction").
hasThreshold(setup7, 16).
hasThresholdType(setup7, "Relief").
hasThreshold(setup8, 17).
hasThresholdType(setup8, "Disappointment").
hasThreshold(setup9, 18).
hasThresholdType(setup9, "HappyFor").
hasThreshold(setup10, 19).
hasThresholdType(setup10, "SorryFor").
hasThreshold(setup11, 20).
hasThresholdType(setup11, "Gloating").
hasThreshold(setup12, 21).
hasThresholdType(setup12, "Resentment").
hasThreshold(setup13, 22).
hasThresholdType(setup13, "Love").
hasThreshold(setup14, 23).
hasThresholdType(setup14, "Hate").
hasThreshold(setup15, 24).
hasThresholdType(setup15, "Admiration").
hasThreshold(setup16, 25).
hasThresholdType(setup16, "Pride").
hasThreshold(setup17, 26).
hasThresholdType(setup17, "Shame").
hasThreshold(setup18, 27).
hasThresholdType(setup18, "Reproach").
hasThreshold(setup19, 28).
hasThresholdType(setup19, "Gratitude").
hasThreshold(setup20, 29).
hasThresholdType(setup20, "Gratification").
hasThreshold(setup21, 30).
hasThresholdType(setup21, "Anger").
hasThreshold(setup22, 31).
hasThresholdType(setup22, "Remorse").
// threshold end

/////////////////////////////////////////////////////////////
// john's appraisals
hasAppraisal(john, john_relationsWith_millie). ////////////// BACKGROUND
hasAppraisal(john, john_relationsWith_jose).

hasNiceness(john_relationsWith_millie, 20).
hasPerson(john_relationsWith_millie, millie).
hasNiceness(john_relationsWith_jose, 5).
hasPerson(john_relationsWith_jose, jose).

hasAppraisal(john, john_inlove_millie).   ////////////////// ASPECTS OF OBJECTS
hasAppraisal(john, john_repulse_television).

hasFamiliarity(john_inlove_millie, 55).
hasPerson(john_inlove_millie, millie). // nao precisa ser dito...
hasFamiliarity(john_repulse_television, -11).

hasAppraisal(john, john_happyFor_millie). //// CONSEQUENCES OF EVENTS FOR OTHERS
hasPerson(john_happyFor_millie, millie).
hasDesireOther(john_happyFor_millie, 11).
hasDeserved(john_happyFor_millie, 30).

hasAppraisal(john, john_fears_demission). ////// CONSEQUENCES OF EVENTS FOR SELF
hasDesireSelf(john_fears_demission, -11).
hasLikelihood(john_fears_demission, lmid).
// pode ser colocado um hasSomething se desejado...

hasAppraisal(john, john_satisfaction_lunch).
hasEffort(john_satisfaction_lunch, 3).
hasPreviousIntensity(john_satisfaction_lunch, 19).
hasRealized(john_satisfaction_lunch, 40).

hasAppraisal(john, john_remorse_itself). //////////////////// COMPOUND
hasLikelihood(john_remorse_itself, lnone). ///////////// DISTRESS
hasDesireSelf(john_remorse_itself, -1).
hasJudge(john_remorse_itself, john). /////////////////// SHAME
hasJudgeness(john_remorse_itself, -7).

/////////////////////////////////////////////////////////////
// jose's appraisals
hasAppraisal(jose, jose_relationsWith_john). //////////////// BACKGROUND
hasAppraisal(jose, jose_relationsWith_millie).
hasAppraisal(jose, jose_relationsWith_dilu).

hasNiceness(jose_relationsWith_john, -33).
hasPerson(jose_relationsWith_john, john).
hasNiceness(jose_relationsWith_millie, 0).
hasPerson(jose_relationsWith_millie, millie).
hasNiceness(jose_relationsWith_dilu, 77).
hasPerson(jose_relationsWith_dilu, dilu).

isAppraisalOf(jose_admire_dilu, jose). ////////////////////// ACTIONS OF AGENTS
hasJudge(jose_admire_dilu, dilu).
hasJudgeness(jose_admire_dilu, 22).

hasAppraisal(jose, jose_resentment_john). //// CONSEQUENCES OF EVENTS FOR OTHERS
hasPerson(jose_resentment_john, john).
hasDesireOther(jose_resentment_john, 61).
hasDeserved(jose_resentment_john, -36).

hasAppraisal(jose, jose_frustation_lunch). ///// CONSEQUENCES OF EVENTS FOR SELF
hasEffort(jose_frustation_lunch, 12).
hasPreviousIntensity(jose_frustation_lunch, 40).
hasRealized(jose_frustation_lunch, -7).

hasAppraisal(jose, jose_hopes_promotion).
hasDesireSelf(jose_hopes_promotion, 44).
hasLikelihood(jose_hopes_promotion, lhigh).

hasAppraisal(jose, jose_gratification_itself). ////////////// COMPOUND
hasJudge(jose_gratification_itself, jose). ////////////// PRIDE
hasJudgeness(jose_gratification_itself, 33).
hasLikelihood(jose_gratification_itself, lnone). //////// JOY
hasDesireSelf(jose_gratification_itself, 88).

/////////////////////////////////////////////////////////////
// dilu's appraisals
hasAppraisal(dilu, dilu_relationsWith_jose). //////////////// BACKGROUND

hasNiceness(dilu_relationsWith_jose, 17).
hasPerson(dilu_relationsWith_jose, jose).

hasFamiliarity(dilu_attracted_television, 30). ////////////// ASPECTS OF OBJECTS
hasSomething(dilu_attracted_television, television).
isAppraisalOf(dilu_attracted_television, dilu).

hasAppraisal(dilu, dilu_sorryFor_jose). ////// CONSEQUENCES OF EVENTS FOR OTHERS
hasPerson(dilu_sorryFor_jose, jose).
hasDesireOther(dilu_sorryFor_jose, -11).
hasDeserved(dilu_sorryFor_jose, -30).

hasAppraisal(dilu, dilu_fearsc_lunch). ///////// CONSEQUENCES OF EVENTS FOR SELF
fearsConfirmed(dilu_fearsc_lunch).

hasAppraisal(dilu, dilu_anger_jose). //////////////////////// COMPOUND
hasLikelihood(dilu_anger_jose, lnone). ////////////////// DISTRESS
hasDesireSelf(dilu_anger_jose, -7).
hasJudge(dilu_anger_jose, jose). //////////////////////// REPROACH
hasJudgeness(dilu_anger_jose, -8).

/////////////////////////////////////////////////////////////
// millie's appraisals
hasAppraisal(millie, millie_relationsWith_dilu). //////////// BACKGROUND

hasNiceness(millie_relationsWith_dilu, -4).
hasPerson(millie_relationsWith_dilu, dilu).

isAppraisalOf(millie_reproach_john, millie). //////////////// ACTIONS OF AGENTS
hasJudge(millie_reproach_john, john).
hasJudgeness(millie_reproach_john, -14).

hasAppraisal(millie, millie_gloating_dilu). // CONSEQUENCES OF EVENTS FOR OTHERS
hasPerson(millie_gloating_dilu, dilu).
hasDesireOther(millie_gloating_dilu, -29).
hasDeserved(millie_gloating_dilu, 29).

relief(millie_relief_gas). ///////////////////// CONSEQUENCES OF EVENTS FOR SELF
isAppraisalOf(millie_relief_gas, millie).

isAppraisalOf(millie_enjoy_cooking, millie).
hasLikelihood(millie_enjoy_cooking, lnone).
hasDesireSelf(millie_enjoy_cooking, 11).

hasAppraisal(millie, millie_gratitudes_car). //////////////// COMPOUND
hasLikelihood(millie_gratitudes_car, lnone). //////////// JOY
hasDesireSelf(millie_gratitudes_car, 68).
hasJudgeness(millie_gratitudes_car, 3). ///////////////// Admiration
hasJudge(millie_gratitudes_car, milliesCar).

/////////////////////////////////////////////////////////////

!start.

+!start
    <-  !functions;
        !!emocoes.

+!relacoes
    <-  .findall(friend(X,Y), hasFriend(X,Y), LF); ?printBR(LF, "Background: ");
        LF=[friend("jose","dilu"), friend("dilu","jose"), friend("john","millie"), friend("john","jose")]; nope;
        .findall(enemy(X,Y), hasEnemy(X,Y), LE); ?printBR(LE, "Background: ");
        LE=[enemy("millie","dilu"), enemy("jose","john")]; nope;
        .findall(know(X,Y), hasKnow(X,Y), LB); ?printBR(LB, "Background: ");
        LB=[know("jose","john"), know("john","millie"), know("jose","dilu"), know("john","jose"), know("millie","john"), know("millie","jose"), know("jose","millie"), know("dilu","jose"), know("dilu","millie"), know("millie","dilu")]; nope.

+?printBR([], _).
+?printBR([H|R], PROMPT)
    <-  .println(PROMPT, H);
        ?printBR(R, PROMPT).


+!emocoes
    <-  !relacoes;
        !testeEmocoes(john);
        !testeEmocoes(jose);
        !testeEmocoes(dilu);
        !testeEmocoes(millie);
        !!perguntas.

+!testeEmocoes(john)
    <-  ?love("john_inlove_millie"); nope;
        ?hate("john_repulse_television"); nope;
        ?happyFor("john_happyFor_millie"); nope;
        ?fear("john_fears_demission"); nope;
        ?satisfaction("john_satisfaction_lunch"); nope;
        ?distress("john_remorse_itself"); nope;
        ?shame("john_remorse_itself"); nope;
        ?remorse("john_remorse_itself"); nope;
        .eval(false, feeling(remorse, _)); nope;
        .println("john tudo certo!").

+!testeEmocoes(jose)
    <-  ?admiration("jose_admire_dilu"); nope;
        ?resentment("jose_resentment_john"); nope;
        ?disappointment("jose_frustation_lunch"); nope;
        ?hope("jose_hopes_promotion"); nope;
        ?pride("jose_gratification_itself"); nope;
        ?joy("jose_gratification_itself"); nope;
        ?gratification("jose_gratification_itself"); nope;
        .eval(false, feeling(gratification, _)); nope;
        .println("jose tudo certo!").

+!testeEmocoes(dilu)
    <-  ?love("dilu_attracted_television"); nope;
        ?sorryFor("dilu_sorryFor_jose"); nope;
        ?fearsConfirmed("dilu_fearsc_lunch"); nope;
        ?distress("dilu_anger_jose"); nope;
        ?reproach("dilu_anger_jose"); nope;
        ?anger("dilu_anger_jose"); nope;
        .eval(false, feeling(anger, _)); nope;
        .println("dilu tudo certo!").

+!testeEmocoes(millie)
    <-  ?reproach("millie_reproach_john"); nope;
        .eval(false, feeling(reproach,_)); nope;
        ?gloating("millie_gloating_dilu"); nope;
        ?feeling(gloating, 38); nope;
        ?relief("millie_relief_gas"); nope;
        .eval(false, feeling(relief, _)); nope;
        ?joy("millie_enjoy_cooking"); nope;
        ?joy("millie_gratitudes_car"); nope;
        ?feeling(joy, 72); nope;
        ?admiration("millie_gratitudes_car"); nope;
        ?feeling(admiration, 47); nope;
        ?gratitude("millie_gratitudes_car"); nope;
        ?feeling(gratitude, 43); nope;
        .println("millie tudo certo!").

+!perguntas
    <-  .findall(X, hasKnow("jose", X), AJ);
        .println("Quem eh conhecido por jose? ", AJ);
         AJ=["john","dilu","millie"]; nope;
        .findall(X, hasKnow(X, "dilu"), AD);
        .println("Quem conhece a dilu?", AD);
        AD=["jose","millie"]; nope;
        .findall(X, love(Y) & isAppraisalOf(Y, X) & hasPerson(Y, "millie"), AL);
        .println("Quem ama a millie?", AL);
        AL=["john"]; nope;
        .findall(X, feeling(X, _), FL);
        .println("Que sentimentos millie tem?", FL);
        FL=[joy,gratitude,gloating,admiration]; nope;
        .findall(diff("millie",X), ~sameAs("millie", X), SMMX);
        .findall(diff("millie",X), ~sameAs(X, "millie"), SMXM);
        .difference(SMMX, SMXM, []);
        .difference(SMXM, SMMX, []); nope;
        !!summary.

+!functions
    <- !function(object);
       !function(data);
       !function(instance).

+!function(object)
    <- +hasLikelihood("test1", lnone);
       ?hasLikelihood("test1", "lnone");
       .println("insertion of relation ok"); nope;
       +hasLikelihood("test1", lnone)[foo];
       ?hasLikelihood("test1", "lnone")[foo];
       .println("changing the relation to add a annotation ok"); nope;
       +hasLikelihood("test1", lnone)[bar];
       ?hasLikelihood("test1", "lnone")[bar];
       .println("changing the relation to add another annotation ok"); nope;
       ?hasLikelihood("test1", "lnone")[foo,bar];
       .println("testing the relation to get the two annotation ok"); nope;
       .eval(false, hasLikelihood(test1, lnone)[foo,bar]); // I'll be not supported
       .println("testing the relation as string to get the two annotation ok"); nope;
       -hasLikelihood("test1", "lnone")[bar]; // imortant send as string all terms
       ?hasLikelihood("test1", "lnone")[foo];
       .eval(false, hasLikelihood("test1", "lnone")[bar]);
       .println("removing annot from relation ok"); nope;
       -hasLikelihood("test1", "lnone"); // because have anothers annots fail silence
       ?hasLikelihood("test1", "lnone")[foo];
       -hasLikelihood("test1", "lnone")[foo]; // remove the last annot and remove the literal after
       .eval(false, hasLikelihood("test1", "lnone"));
       .println("removing another annot from relation ok"); nope.

+!function(data)
    <- +hasDesireSelf("test2", 10);
       ?hasDesireSelf("test2", 10);
       .println("insertion of a data relation ok"); nope;
       +hasDesireSelf("test2", 10)[foo];
       ?hasDesireSelf("test2", 10)[foo];
       .println("changing the data relation to add a annotation ok"); nope;
       +hasDesireSelf("test2", 10)[bar];
       ?hasDesireSelf("test2", 10)[bar];
       .println("changing the data relation to add another annotation ok"); nope;
       ?hasDesireSelf("test2", 10)[foo,bar];
       .println("testing the data relation to get the two annotation ok"); nope;
       .eval(false, hasDesireSelf(test2, 10)[foo,bar]); // I'll be not supported
       .println("testing the relation as string to get the two annotation ok"); nope;
       -hasDesireSelf("test2", 10)[bar];
       ?hasDesireSelf("test2", 10)[foo];
       .eval(false, hasDesireSelf("test2", 10)[bar]);
       .println("removing annot from relation ok"); nope;
       -hasDesireSelf("test2", 10); // because have anothers annots fail silence
       ?hasDesireSelf("test2", 10)[foo];
       -hasDesireSelf("test2", 10)[foo]; // remove the last annot and remove the literal after
       .eval(false, hasDesireSelf("test2", 10));
       .println("removing another annot from relation ok"); nope;
       +hasDesireSelf("test4", -10);
       ?hasDesireSelf("test4", -10);
       .println("insertion of negative integers ok"); nope;
       +hasDesireSelf("test4", -10)[foo];
       ?hasDesireSelf("test4", -10);
       .println("insertion of negative integers with annot ok"); nope;
       -hasDesireSelf("test4", -10)[foo];
       .eval(false, hasDesireSelf("test4", -10));
       .println("removing negative integers with annot ok"); nope;
       +hasDesireSelf("test4", -10);
       ?hasDesireSelf("test4", -10);
       -hasDesireSelf("test4", -10);
       .eval(false, hasDesireSelf("test4", -10));
       .println("removing negative integers ok"); nope.

+!function(instance)
    <- +low("test3");
       ?low("test3");
       .println("insertion of concept ok"); nope;
       +low("test3")[foo];
       ?low("test3")[foo];
       .println("changing the concept to add a annotation ok"); nope;
       +low("test3")[bar];
       ?low("test3")[bar];
       .println("changing the concept to add another annotation ok"); nope;
       ?low("test3")[foo,bar];
       .println("testing the concept to get the two annotation ok"); nope;
       .eval(false, low(test3)[foo,bar]); // I'll be not supported
       .println("testing the concept as string to get the two annotation ok"); nope;
       -low("test3")[bar];
       ?low("test3")[foo];
       .eval(false, low("test3")[bar]);
       .println("removing annot from concept ok"); nope;
       -low("test3"); // because have anothers annots fail silence
       ?low("test3")[foo];
       -low("test3")[foo]; // remove the last annot and remove the literal after
       .eval(false, low("test3"));
       .println("removing another annot from concept ok"); nope.

+!summary
    <-
        +startUpOK; nope.

+step(X)
    : startUpOK
   <- .println("Tudo ok"); .stopMAS.

-!X
    <- .println("teste falhou: ", X); .stopMAS.

