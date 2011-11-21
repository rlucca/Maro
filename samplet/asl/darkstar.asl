fullLife(100).
//-----------------------------------------------------------------------------
!start.
+!start <- iam(agent); !!reactive.
//-----------------------------------------------------------------------------
+!reactive
     : step(X) & .random(Y) & K=math.round(Y*10000) & population(P)
    <- !behavior(X, K, P);
       !!reactive.
-!reactive
    <- .println("reactive plan failed! Doing nope..."); nope;
       !!reactive.
//-----------------------------------------------------------------------------
+!behavior(STEP, RANDOM, POPULATION)
     : life(0)
    <- .println("morreu");
       death.
+!behavior(STEP, RANDOM, POPULATION)
    <- .println("nope");
       nope.
