
//-----------------------------------------------------------------------------
!start.
+!start <- iam(planet); !!reactive.
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
     : RANDOM < 51
    <- .println("step ", STEP, " com populacao ", POPULATION);
       increasePopulation(RANDOM).
+!behavior(STEP, RANDOM, POPULATION)
     : POPULATION > 51
    <- .println("step ", STEP, " com populacao ", POPULATION);
       increasePopulation(-4).
+!behavior(STEP, RANDOM, POPULATION)
    <- nope.
