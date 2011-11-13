
!start.

+!start <- iam(planet); !!reactive.

+!reactive
     : step(X) & .random(Y) & K=math.round(Y*10000) & population(P)
    <- !behavior(X, K, P);
       !!reactive.

-!reactive
    <- .wait({+step(_)});
       !!reactive.

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
