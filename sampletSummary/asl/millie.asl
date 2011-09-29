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
    <-  iam(agent);
        !!relacoes.

+!relacoes
    <-  .findall(friend(X,Y), hasFriend(X,Y), LF); ?printBR(LF, "Background: ");
        .findall(enemy(X,Y), hasEnemy(X,Y), LE); ?printBR(LE, "Background: ");
        .findall(know(X,Y), hasKnow(X,Y), LB);
        .difference(LB, LF, LW);
        .difference(LW, LE, LK); ?printBR(LK, "Background: ");
        !!emocoes.

+?printBR([], _).
+?printBR([H|R], PROMPT)
    <-  .println(PROMPT, H);
        ?printBR(R, PROMPT).


+!emocoes
    <-  !testeEmocoes(john);
        !testeEmocoes(jose);
        !testeEmocoes(dilu);
        !testeEmocoes(millie);
        !!perguntas.

+!testeEmocoes(john)
    <-  ?love("john_inlove_millie");
        ?hate("john_repulse_television");
        ?happyFor("john_happyFor_millie");
        ?fear("john_fears_demission");
        ?satisfaction("john_satisfaction_lunch");
        ?distress("john_remorse_itself");
        ?shame("john_remorse_itself");
        ?remorse("john_remorse_itself");
        .println("john tudo certo!").

+!testeEmocoes(jose)
    <-  ?admiration("jose_admire_dilu");
        ?resentment("jose_resentment_john");
        ?disappointment("jose_frustation_lunch");
        ?hope("jose_hopes_promotion");
        ?pride("jose_gratification_itself");
        ?joy("jose_gratification_itself");
        ?gratification("jose_gratification_itself");
        .println("jose tudo certo!").

+!testeEmocoes(dilu)
    <-  ?love("dilu_attracted_television");
        ?sorryFor("dilu_sorryFor_jose");
        ?fearsConfirmed("dilu_fearsc_lunch");
        ?distress("dilu_anger_jose");
        ?reproach("dilu_anger_jose");
        ?anger("dilu_anger_jose");
        .println("dilu tudo certo!").

+!testeEmocoes(millie)
    <-  ?reproach("millie_reproach_john");
        ?gloating("millie_gloating_dilu");
        ?relief("millie_relief_gas");
        ?joy("millie_enjoy_cooking");
        ?joy("millie_gratitudes_car");
        ?admiration("millie_gratitudes_car");
        ?gratitude("millie_gratitudes_car");
        .println("millie tudo certo!").

+!perguntas
    <-  .findall(X, hasKnow("jose", X), AJ);
        .println("Quem eh conhecido por jose? ", AJ);
        .findall(X, hasKnow(X, "dilu"), AD);
        .println("Quem conhece a dilu?", AD);
        .findall(X, love(Y) & isAppraisalOf(Y, X) & hasPerson(Y, "millie"), AL);
        .println("Quem ama a millie?", AL);
        .findall(X, hasFeeling("millie", X, _), FL);
        .println("Que sentimentos millie tem?", FL);
        !!summary.

+!summary
    <-  // larguei de mao de fazer isso aqui...
        +startUpOK; nope.

+step(6) <- .println("passo 6 waiting "); .wait(5000).
+step(X)
     : startUpOK
    <- .println("passo ", X, " sending nope"); nope; .stopMAS.
+step(X)
    <- .println("passo ", X).
//-step(X)
//    <- .println("removendo ", X).

