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
     : POPULATION <= 1 & onPlanet & myself(_,_,_)[quality(L)] & fullLife(LL)
        & L < LL
    <- recover.
+!behavior(STEP, RANDOM, POPULATION)
     : POPULATION <= 1
    <- nope.
+!behavior(STEP, RANDOM, POPULATION).
//    <- .println("nope");
//       nope.
//-----------------------------------------------------------------------------
{ include("gui.asl") }
