/*
data
    hasName ---------------- USELESS
    hasThresholdType ------- OK
    hasThreshold ----------- OK
    hasDesireSelf ---------- NO/i
    hasDesireOther --------- NO/i
    hasDeserved ------------ NO/i
    hasEffort -------------- NO/i
    hasFamiliarity --------- NO/i
    hasJudgeness ----------- NO/i
    hasNiceness ------------ NO/i
    hasPreviousIntensity --- NO/i
    hasRealized ------------ NO/i
object
    hasAppraisal ----------- DO NOT FORGET (or isAppraisalOf)
    hasSetup --------------- OK
    hasLikelihood ---------- NO/I
    hasKnowRelation -------- NO/I
    hasEnemy --------------- NO/I
    hasFriend -------------- NO/I
    hasJudgeMyself --------- NO/I
    hasJudgeOther ---------- NO/I
    hasPersonEnemy --------- NO/I
    hasPersonFriend -------- NO/I
emotion
    Love ------------------- USELESS      Hate ------------------- USELESS
    Joy -------------------- YES          Distress --------------- YES
    Hope ------------------- YES          Fear ------------------- NO  ........ goals not finished!
    Admiration ------------- NO           Reproach --------------- NO  ........ responsability of actions of other
    Pride ------------------ NO           Shame ------------------ NO  ........ responsability of actions of yourself
    Gratification ---------- NO           Remorse ---------------- NO  ........ responsability of actions of yourself + (joy/distress)
    Gratitude -------------- NO           Anger ------------------ NO  ........ responsability of actions of other + (joy/distress)
    Satisfaction ----------- YES          FearConfirmed ---------- NO  ........ goals finished! Happens...
    Relief ----------------- NO           Disappointment --------- YES ........ goals finished! Not happens...
    Happy-for -------------- YES          Gloating --------------- NO  ........ goals of other... happens...
    Resentment ------------- NO           Sorry-for -------------- YES ........ goals of other... not happens...

*/
//------------------------------------------------------------------------------
fib(3, 2). fib(4, 3). fib(5, 5). fib(6, 8). fib(7, 13). fib(_, 1).
myPoints(N,VAL,M,V) :- team(N) & (points(N,VAL,M,V) | points(M,V,N,VAL)).
myName(NAME) :- .my_name(ATOM) & .term2string(ATOM, NAME).
//------------------------------------------------------------------------------
+?appraisal
    <- ?background;
       ?appraisalGoal;
       ?appraisalTeam;
       ?appraisalEndMatch;
       ?appraisalEndMatchHappy;
       ?printEmotions.
//-- people will know your team with a niceness positive -----------------------
+?background
    :  not(myPoints(_,_,_,_)).
//------------------------------------------------------------------------------
+?background
    :  myPoints(MYTEAM,_, ENEMYTEAM, _) & not(hasNiceness(_,_))
    <- ?myName(NAME); INDIVIDUALMT="myteam_background";
       +hasAppraisal(NAME, INDIVIDUALMT);
       +hasPerson(INDIVIDUALMT, MYTEAM);
       +hasNiceness(INDIVIDUALMT, 10);
       INDIVIDUALET = "enemyteam_background";
       +hasAppraisal(NAME, INDIVIDUALET);
       +hasPerson(INDIVIDUALET, ENEMYTEAM);
       +hasNiceness(INDIVIDUALET, -10).
//------------------------------------------------------------------------------
+?background.
//-- people will be joy when your team does a point ----------------------------
+?appraisalGoal
    :  goal(TEAM) & myPoints(TEAM, PF, _, _)
    <- ?fib(8-(PF+1), VAL);
       ?updateEmotion(prospectIrrelevant, "goal_myteam_pi", VAL);
       +removeEmotion("goal_myteam_pi").
//------------------------------------------------------------------------------
+?appraisalGoal
    :  goal(TEAM) & myPoints(_, _, TEAM, PE)
    <- ?fib(PE+1, VAL);
       ?updateEmotion(prospectIrrelevant, "goal_enemyteam_pi", -VAL);
       +removeEmotion("goal_enemyteam_pi").
//------------------------------------------------------------------------------
+?appraisalGoal
    : removeEmotion(INDIVIDIDUAL)
   <- ?removeEmotion(prospectIrrelevant, INDIVIDIDUAL);
      -removeEmotion(INDIVIDIDUAL).
//------------------------------------------------------------------------------
+?appraisalGoal.
//-- All team have hope that they will win the match ---------------------------
+?appraisalTeam
    :  not(hasLikelihood("team_hope_prn", PROB)) & step(STEP) & STEP < 10
    <- ?updateEmotion(prospectNotRealized, "team_hope_prn", 50);
       +realizedCalculation(1).
//------------------------------------------------------------------------------
+?appraisalTeam
    :  goal(TEAM) & myPoints(_,_, TEAM, _) // ponto que nao eh meu
    &  realizedCalculation(CALC)
    <- -realizedCalculation(CALC);
       +realizedCalculation(CALC-10).
+?appraisalTeam
    :  goal(TEAM) & myPoints(TEAM, _, _,_) // ponto que eh meu
    &  realizedCalculation(CALC)
    <- -realizedCalculation(CALC);
       +realizedCalculation(CALC+10).
//------------------------------------------------------------------------------
+?appraisalTeam.
//-- In the end of the match, the satisfaction or disappointment appear --------
+?appraisalEndMatch
    :  realizedCalculation(CALC) & match(_,_)
    &  hasLikelihood("team_hope_prn", PROB)
    &  feeling(hope, HOPEVAL)
    <- ?removeEmotion(prospectNotRealized, "team_hope_prn");
       ?updateEmotion(prospectRealized, "team_satisfaction_pr", HOPEVAL, CALC);
       -realizedCalculation(CALC).
//------------------------------------------------------------------------------
+?appraisalEndMatch
    :  realizedCalculation(CALC) & match(_,_)
    &  hasLikelihood("team_hope_prn", PROB)
    <- ?removeEmotion(prospectNotRealized, "team_hope_prn");
       -realizedCalculation(CALC).
//------------------------------------------------------------------------------
+?appraisalEndMatch.
//-- In the end of the match, we can felt happy because my team win ------------
+?appraisalEndMatchHappy
     : match(win, TEAM) & myPoints(TEAM, _, _, _)
     & not(hasAppraisal(NAME, "team_happy_foo"))
    <- .random(N); DE=math.round(N*20)-10; DT=20;
       ?updateEmotion(fortunesOfOthers, "team_happy_foo", DE, DT).
//------------------------------------------------------------------------------
+?appraisalEndMatchHappy
     : match(lose, TEAM) & myPoints(TEAM, _, _, _)
     & not(hasAppraisal(NAME, "team_happy_foo"))
    <- .random(N); DE=math.round(N*20)-10;
       DT=-20; // is negative to try implicate the sorryFor
       ?updateEmotion(fortunesOfOthers, "team_happy_foo", DE, DT).
//------------------------------------------------------------------------------
+?appraisalEndMatchHappy
     : match(draw, _) & myPoints(TEAM, _, _, _)
     & not(hasAppraisal(NAME, "team_happy_foo"))
    <- .random(N); DE=math.round(N*20)-10; DT=10;
       ?updateEmotion(fortunesOfOthers, "team_happy_foo", DE, DT).
//------------------------------------------------------------------------------
+?appraisalEndMatchHappy.
//-- update JOY or DISTRESS ----------------------------------------------------
+?updateEmotion(prospectIrrelevant, INDIVIDUAL, VAL)
     : not(hasDesireSelf(INDIVIDUAL, ANY))
    <- ?myName(NAME);
       +hasAppraisal(NAME, INDIVIDUAL);
       +hasLikelihood(INDIVIDUAL, "lnone");
       +hasDesireSelf(INDIVIDUAL, VAL).
+?updateEmotion(prospectIrrelevant, INDIVIDUAL, VAL)
     : hasDesireSelf(INDIVIDUAL, OLDVAL)
    <- -hasDesireSelf(INDIVIDUAL, OLDVAL);
       +hasDesireSelf(INDIVIDUAL, OLDVAL+VAL).
//------------------------------------------------------------------------------
+?removeEmotion(prospectIrrelevant, INDIVIDUAL)
     : hasDesireSelf(INDIVIDUAL, OLDVAL)
    <- ?myName(NAME);
       -hasLikelihood(INDIVIDUAL, "lnone");
       -hasDesireSelf(INDIVIDUAL, OLDVAL);
       -hasAppraisal(NAME, INDIVIDUAL).
//------------------------------------------------------------------------------
+?removeEmotion(prospectIrrelevant, INDIVIDUAL).
//-- update HOPE or FEAR -------------------------------------------------------
+?updateEmotion(prospectNotRealized, INDIVIDUAL, VAL)
     : not(hasDesireSelf(INDIVIDUAL, ANY))
    <- ?myName(NAME);
       +hasAppraisal(NAME, INDIVIDUAL);
       +hasLikelihood(INDIVIDUAL, "lmid");
       +hasDesireSelf(INDIVIDUAL, VAL).
+?updateEmotion(prospectNotRealized, INDIVIDUAL, VAL)
     : hasDesireSelf(INDIVIDUAL, OLDVAL)
    <- -hasDesireSelf(INDIVIDUAL, OLDVAL);
       +hasDesireSelf(INDIVIDUAL, OLDVAL+VAL).
//------------------------------------------------------------------------------
+?removeEmotion(prospectNotRealized, INDIVIDUAL)
     : hasDesireSelf(INDIVIDUAL, OLDVAL) & hasLikelihood(INDIVIDUAL, PROBABILITY)
    <- ?myName(NAME);
       -hasLikelihood(INDIVIDUAL, PROBABILITY);
       -hasDesireSelf(INDIVIDUAL, OLDVAL);
       -hasAppraisal(NAME, INDIVIDUAL).
//------------------------------------------------------------------------------
+?removeEmotion(prospectNotRealized, INDIVIDUAL).
//-- update SATISFACTION or DISAPPOINTMENT or RELIEF or FEARCONFIRMED ----------
+?updateEmotion(prospectRealized, INDIVIDUAL, PREVFEEL, VAL)
     : not(hasEffort(INDIVIDUAL, ANY))
    <- ?myName(NAME);
       +hasAppraisal(NAME, INDIVIDUAL);
       +hasEffort(INDIVIDUAL, PREVFEEL); // yes, this is correct
       +hasPreviousIntensity(INDIVIDUAL, PREVFEEL);
       +hasRealized(INDIVIDUAL, VAL).
//-- dont have the update of a prospect realized emotion -----------------------
+?updateEmotion(prospectRealized, INDIVIDUAL, PREVFEEL, VAL).
//------------------------------------------------------------------------------
+?removeEmotion(prospectRealized, INDIVIDUAL)
    :  hasEffort(INDIVIDUAL, ANY) & hasRealized(INDIVIDUAL, ANOTHER)
    <- ?myName(NAME);
       -hasEffort(INDIVIDUAL, ANY);
       -hasPreviousIntensity(INDIVIDUAL, ANY);
       -hasRealized(INDIVIDUAL, ANOTHER);
       -hasAppraisal(NAME, INDIVIDUAL).
//------------------------------------------------------------------------------
+?removeEmotion(prospectRealized, INDIVIDUAL).
//-- update HAPPYFOR or SORRYFOR or GLOATING or RESENTMENT ---------------------
+?updateEmotion(fortunesOfOthers, INDIVIDUAL, DESERVED, DESIREOTHER)
     : team(TEAM) & not(hasPerson(INDIVIDUAL, TEAM))
    <- ?myName(NAME);
       +hasAppraisal(NAME, INDIVIDUAL);
       +hasPerson(INDIVIDUAL, TEAM);
       +hasDeserved(INDIVIDUAL, DESERVED);
       +hasDesireOther(INDIVIDUAL, DESIREOTHER).
//------------------------------------------------------------------------------
+?updateEmotion(fortunesOfOthers, INDIVIDUAL, DESERVED, DESIREOTHER)
     : hasDeserved(INDIVIDUAL, OLDDE) & hasDesireOther(INDIVIDUAL, OLDDO)
    <- -hasDeserved(INDIVIDUAL, OLDDE);
       +hasDeserved(INDIVIDUAL, DESERVED);
       -hasDesireOther(INDIVIDUAL, OLDDO);
       +hasDesireOther(INDIVIDUAL, DESIREOTHER).
//------------------------------------------------------------------------------
+?updateEmotion(fortunesOfOthers, INDIVIDUAL, DE, DO).
//------------------------------------------------------------------------------
+?removeEmotion(fortunesOfOthers, INDIVIDUAL)
     : hasDeserved(INDIVIDUAL, OLDDE) & hasDesireOther(INDIVIDUAL, OLDDO)
     & hasPerson(INDIVIDUAL, TEAM)
    <- ?myName(NAME);
       -hasPerson(INDIVIDUAL, TEAM);
       -hasDeserved(INDIVIDUAL, OLDDE);
       -hasDesireOther(INDIVIDUAL, OLDDO);
       -hasAppraisal(NAME, INDIVIDUAL).
//------------------------------------------------------------------------------
+?removeEmotion(fortunesOfOthers, INDIVIDUAL).
//-- update EMOTION ------------------------------------------------------------
//+?updateEmotion(TYPE, INDIVIDUAL, VAL).
//+?removeEmotion(TYPE, INDIVIDUAL)
//------------------------------------------------------------------------------
+?printEmotions
    <- .findall(feel(L,V), feeling(L,V), FS);
       ?myPoints(N,P,M,O); ?team(T);
       .print("Emotion: ", FS, "team ", T, "==",N, " ", P, " x ", O, " ", M).
//------------------------------------------------------------------------------
-?X <- .print("Error ",X).
-!X <- .print("Error ",X).
