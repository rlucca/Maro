high("lhigh").
mid("lmid").
low("llow").
none("lnone").

/*
data
    hasName ---------------- USELESS
    hasThresholdType ------- OK
    hasThreshold ----------- OK
    hasDesireSelf ---------- OK (joy/distress)
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
    hasLikelihood ---------- OK (none) / NO (other)
    hasKnowRelation -------- NO/I
    hasEnemy --------------- NO/I
    hasFriend -------------- NO/I
    hasJudgeMyself --------- NO/I
    hasJudgeOther ---------- NO/I
    hasPersonEnemy --------- NO/I
    hasPersonFriend -------- NO/I
emotion
    Joy -------------------- YES          Distress --------------- YES
    Hope ------------------- NO           Fear ------------------- NO ......... goals not finished!
    Admiration ------------- NO           Reproach --------------- NO ......... responsability of actions of other
    Pride ------------------ NO           Shame ------------------ NO ......... responsability of actions of yourself
    Gratification ---------- NO           Remorse ---------------- NO ......... responsability of actions of yourself + (joy/distress)
    Gratitude -------------- NO           Anger ------------------ NO ......... responsability of actions of other + (joy/distress)
    Love ------------------- NO           Hate ------------------- NO ......... ???
    Satisfaction ----------- NO           FearConfirmed ---------- NO ......... goals finished! Happens...
    Relief ----------------- NO           Disappointment --------- NO ......... goals finished! Not happens...
    Happy-for -------------- NO           Gloating --------------- NO ......... goals of other... happens...
    Resentment ------------- NO           Sorry-for -------------- NO ......... goals of other... not happens...
actors
    John,     Nina
    Millie,   Albert

// TODO:
// John and Nina in love by each other
// put some actos in love and repulsion by objects too
*/


+?appraisal
    <- // startin' get the priorities!
       .findall(prRe(A,P), priority(repulse, A, P), LRE);
       .findall(prAt(A,P), priority(attract, A, P), LAT);
       .findall(OBJ, OBJ[source(percept)], PERCEPTS);
       ?appraisalPercept(PERCEPTS, LRE, LAT).

+?appraisalPercept([], LRE, LAT).
+?appraisalPercept([PERCEPT|R], LRE, LAT)
     : sims.ia.deconstructionAsList(PERCEPT, [FUNCTOR|TERMS])
    <- ?appraisalPercept(R, LRE, LAT);
       .findall(X, PERCEPT[X], ANNOTS);
       ?appraisalOnePercept(PERCEPT, ANNOTS, LRE, VALR);
       ?appraisalOnePercept(PERCEPT, ANNOTS, LAT, VALP);
       .println("percept ", PERCEPT, " == ", FUNCTOR, "(", TERMS, ") = ",VALR+VALP).
       // Actually, VALR or VALP represents attraction and repulse. Attraction when the value is positive and Repulse when the value is negative.
       // But, if you think about attention's focus the positive values do not need attention. Only negative value need atention to protect yourself.

+?appraisalOnePercept(_, _, [], 0).
+?appraisalOnePercept(PERCEPT, ANNOTS, [PR|L], NEWVAL)
    <- ?appraisalOnePercept(PERCEPT, ANNOTS, L, VAL);
       ?appraisalOneAnnotation(ANNOTS, PR, VAL, NEWVAL);
       ?evalAppraisal(PERCEPT, PR, VAL, NEWVAL).

+?appraisalOneAnnotation([], _, VAL, VAL).
+?appraisalOneAnnotation([H|R], PR, OLDVAL, NEWVAL)
     : sims.ia.deconstructionAsList(H, [NAMEPRI,VALTER|TERMS])
     & sims.ia.deconstructionAsList(PR,[FUNCPRI,NAMEPRI,PARTPRI])
    <- ?appraisalOneAnnotationValue(VALTER, PARTPRI, FUNCPRI, OLDVAL, VAL);
       ?appraisalOneAnnotation(R, PR, VAL, NEWVAL).
+?appraisalOneAnnotation([H|R], PR, OLDVAL, NEWVAL)
    <- ?appraisalOneAnnotation(R, PR, OLDVAL, NEWVAL).
       //.println("       OneAnnot ", H, " priority ", PR).


//appraisalOneAnnotationValue----NUMBER-----------------------------------
+?appraisalOneAnnotationValue(VALT, VALP, _, OLDV, NEWV)
     : .number(VALT) & .number(VALP) & VALT = VALP
    <- NEWV = OLDV.
+?appraisalOneAnnotationValue(VALT, VALP, prRe, OLDV, NEWV)
     : .number(VALT) & .number(VALP) & VALT < VALP
    <- NEWV = OLDV+(VALP-VALT).
+?appraisalOneAnnotationValue(VALT, VALP, prRe, OLDV, NEWV)
     : .number(VALT) & .number(VALP) & VALT > VALP
    <- NEWV = OLDV+(VALP-VALT).
+?appraisalOneAnnotationValue(VALT, VALP, prAt, OLDV, NEWV)
     : .number(VALT) & .number(VALP) & VALT > VALP
    <- NEWV = OLDV+(VALT-VALP).
+?appraisalOneAnnotationValue(VALT, VALP, prAt, OLDV, NEWV)
     : .number(VALT) & .number(VALP) & VALT < VALP
    <- NEWV = OLDV+(VALT-VALP).
//appraisalOneAnnotationValue----ATOM-------------------------------------
+?appraisalOneAnnotationValue(VALT, VALT, FUNCTOR, OLDV, OLDV+1)
     : .atom(VALT).
+?appraisalOneAnnotationValue(VALT, VALP, FUNCTOR, OLDV, OLDV)
     : .atom(VALT) & .atom(VALP).
//appraisalOneAnnotationValue----NOT MARK POINT---------------------------
//+?appraisalOneAnnotationValue(VALT, VALP, FUNCTOR, OLDV, NEWV)
//    <- .println("        annot ",VALT," against ", VALP, " prior ", FUNCTOR, " not mark point");
//       NEWV=OLDV.




//evalAppraisal-----------------------------------------------------------
+?evalAppraisal(_, _, OLDVAL, OLDVAL).
+?evalAppraisal(PCP, PREF, OLDVAL, NEWVAL)
     : sims.ia.deconstructionAsList(PCP,  [FUNCP, TERM1P | [] ])
     & sims.ia.deconstructionAsList(PREF, [FUNCF, TERM1F | TERMF])
     & NEWVAL > 0
    <- .println("   (",FUNCP,",",TERM1P,") against ", TERM1F, " oldval ", OLDVAL, " newval ", NEWVAL);
       .concat("", FUNCP, "_", TERM1P, "_", TERM1F, INDIVIDUAL);
       ?updateAppraisal(joy, INDIVIDUAL, NEWVAL).
+?evalAppraisal(PCP, PREF, OLDVAL, NEWVAL)
     : sims.ia.deconstructionAsList(PCP,  [FUNCP, TERM1P | [] ])
     & sims.ia.deconstructionAsList(PREF, [FUNCF, TERM1F | TERMF])
     & NEWVAL < 0
    <- .println("   (",FUNCP,",",TERM1P,") against ", TERM1F, " oldval ", OLDVAL, " newval ", NEWVAL);
       .concat("", FUNCP, "_", TERM1P, "_", TERM1F, INDIVIDUAL);
       ?updateAppraisal(distress, INDIVIDUAL, NEWVAL).
+?evalAppraisal(PCP, PREF, OLDVAL, NEWVAL)
     : sims.ia.deconstructionAsList(PCP,  [FUNCP         | []   ])
     & sims.ia.deconstructionAsList(PREF, [FUNCF, TERM1F | TERMF])
     & NEWVAL > 0
    <- .println("   (",FUNCP,",[]) against ", TERM1F, " oldval ", OLDVAL, " newval ", NEWVAL);
       .concat("", FUNCP, "_", TERM1F, INDIVIDUAL);
       ?updateAppraisal(joy, INDIVIDUAL, NEWVAL).
+?evalAppraisal(PCP, PREF, OLDVAL, NEWVAL)
     : sims.ia.deconstructionAsList(PCP,  [FUNCP         | []   ])
     & sims.ia.deconstructionAsList(PREF, [FUNCF, TERM1F | TERMF])
     & NEWVAL < 0
    <- .println("   (",FUNCP,",[]) against ", TERM1F, " oldval ", OLDVAL, " newval ", NEWVAL);
       .concat("", FUNCP, "_", TERM1F, INDIVIDUAL);
       ?updateAppraisal(distress, INDIVIDUAL, NEWVAL).

//updateAppraisal-Joy-----------------------------------------------------
+?updateAppraisal(joy, INDIVIDUAL, VALUE)
     : not(hasLikelihood(INDIVIDUAL, "lnone")) & .my_name(NAME)
    <- +isAppraisalOf(INDIVIDUAL, NAME);
       +hasLikelihood(INDIVIDUAL, "lnone");
       +hasDesireSelf(INDIVIDUAL, VALUE).
+?updateAppraisal(joy, INDIVIDUAL, VALUE)
    <- +hasDesireSelf(INDIVIDUAL, VALUE).
//updateAppraisal-Distress------------------------------------------------
+?updateAppraisal(distress, INDIVIDUAL, VALUE)
     : not(hasLikelihood(INDIVIDUAL, "lnone")) & .my_name(NAME)
    <- +isAppraisalOf(INDIVIDUAL, NAME);
       +hasLikelihood(INDIVIDUAL, "lnone");
       +hasDesireSelf(INDIVIDUAL, VALUE).
+?updateAppraisal(distress, INDIVIDUAL, VALUE)
    <- +hasDesireSelf(INDIVIDUAL, VALUE).
//updateAppraisal-Emotion-------------------------------------------------
//+?updateAppraisal(EMOTION, INDIVIDUAL, VALUE).
