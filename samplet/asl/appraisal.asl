/*
data
    hasName ---------------- USELESS
    hasThresholdType ------- OK
    hasThreshold ----------- OK
    hasDeserved ------------ NO/i
    hasDesireSelf ---------- NO/i
    hasDesireOther --------- NO/i
    hasEffort -------------- NO/i
    hasFamiliarity --------- NO/i
    hasJudgeness ----------- NO/i
    hasNiceness ------------ NO/i
    hasPreviousIntensity --- NO/i
    hasRealized ------------ NO/i
object
    hasAppraisal ----------- NO/I
    hasKnowRelation -------- NO/I
    hasKnow ---------------- NO/I
        hasEnemy ----------- NO/I
        hasFriend ---------- NO/I
    hasLikelihood ---------- NO/I
    hasSetup --------------- NO/I
    hasJudgeMyself --------- NO/I
    hasJudgeOther ---------- NO/I
    hasPersonEnemy --------- NO/I
    hasPersonFriend -------- NO/I
actors
    John,     Nina
    Millie,   Albert
*/

// John and Nina in love by each other
// put some actos in love and repulsion by objects too


// 
+?appraisal
     : object("wardrobe2")
    <- .findall(X, object("wardrobe2")[X], L);
       .println("wardrobe2: ", L).

// 
+?appraisal
     : object("doorToBathRoomMiddleToRoom1")
    <- .findall(X, object("doorToBathRoomMiddleToRoom1")[X], L);
       .println("doorToBathRoomMiddleToRoom1: ", L).

// This is necessary to not give error.
+?appraisal.

