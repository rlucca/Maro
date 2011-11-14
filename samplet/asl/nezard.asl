fullLife(100).

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
    : die(X) & X < STEP
   <- nope.

+!behavior(STEP, RANDOM, POPULATION)
     : life(0) & not(die(_))
    <- .println("morreu");
       +die(STEP);
       death.

+!behavior(STEP, RANDOM, POPULATION)
    <- .println("nope");
       nope.

