fullLife(100).
//-----------------------------------------------------------------------------
!start.
+!start <- iam(agent); !!reactive.
//-----------------------------------------------------------------------------
+!reactive
     : step(X) & .random(Y) & K=math.round(Y*10000)
    <- ?myself(_,_,_)[resource(P)];
       !behavior(X, K, P);
       !!reactive.
-!reactive
    <- .println("reactive plan failed! Doing nope..."); nope;
       !!reactive.
//-----------------------------------------------------------------------------
+!behavior(STEP, RANDOM, POPULATION)
     : myself(_,_,_)[quality(0)]
    <- .println("morreu");
       death.
+!behavior(STEP, RANDOM, POPULATION)
    <- .println("nope");
       nope.
//-----------------------------------------------------------------------------
//absorve maked, not tested!
