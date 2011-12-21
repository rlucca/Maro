//initial believes.
//-----------------------------------------------------------------------------
//!start.
//+!start : .my_name(K) <- .println("my name is ", K).
//+!start <- iam(agent); !!reactive.
//-----------------------------------------------------------------------------
/*+!reactive
     : step(X) //& .random(Y) & K=math.round(Y*10000)
    <- //?myself(_,_,_)[resource(P)];
       nope;
       !!reactive.
-!reactive
    <- .println("reactive plan failed! Doing nope..."); nope;
       !!reactive.*/
//-----------------------------------------------------------------------------
/*+X <- .println("add/? ", X); !!foo.
-X <- .println("del/? ", X); !!foo.


+!foo : printed & .findall(L, agent(L), L)
    <- !printAgentP(L);
       foo.

+!foo : not(printed)
    <- !printPlaceView;
       !printItemView;
       +printed;
       !!foo.

+!printPlaceView
    <- sims.ia.getPlaces(K);
       !printPlace(K).

+!printPlace([]).
+!printPlace([H|R])
    <- !printPlace(R);
       sims.ia.getItemsAtPlace(H, K);
       .println(H,": ", K).

+!printItemView
    <- sims.ia.getItems(K);
       !printItem(K).

+!printItem([]).
+!printItem([H|R])
    <- !printItem(R);
       sims.ia.getPlacesByItem(H, K);
       .println(H, ": ", K).

+!printAgentP([]).
+!printAgentP([H|R]) : agent(H) & .findall(K, agent(H)[K], M)
    <- !printAgentP(R);
       .println(H, " ==> ", M). */

!start.
+!start : step(STEP) & STEP < 4
    <- .println("Step: ", STEP);
       .findall(L, hasThresholdType(L,_), HTT); .println("List of hasThresholdType knowledge: ", HTT);
       .findall(L, hasThreshold(L,_), HT); .println("List of hasThreshold knowledge: ", HT);
       .findall(L, isSetupOf(L,_), ISO); .println("List of isSetupOf knowledge: ", ISO);
       .findall(L, hasSetup(L,_), HS); .println("List of hasSetup knowledge: ", HS);
       .findall(L, setup(L), S); .println("List of setup knowledge: ", S);
       //.wait({+step(_)});
       nope;
       !!start.
+!start : step(X) & .random(Y) & NUMBER=math.round(Y*10)
    <- !behaviour(NUMBER); !!start.
+!start <- !!start.

+!behaviour(0) <- .println("looking for N"); changeOrientation("N").
+!behaviour(1) <- .println("looking for S"); changeOrientation("S").
+!behaviour(2) <- .println("looking for E"); changeOrientation("E").
+!behaviour(3) <- .println("looking for W"); changeOrientation("W").
+!behaviour(4) <- .println("walking forward"); forward.
+!behaviour(5) <- .println("walking forward"); forward.
+!behaviour(6) <- .println("walking forward"); forward.
+!behaviour(7) <- .println("walking forward"); forward.
+!behaviour(8) <- .println("walking forward"); forward.
+!behaviour(9) <- .println("walking forward"); forward.
+!behaviour(10) <- .println("walking forward"); forward.
-!X <- .println("common failure for ", X); nope.
