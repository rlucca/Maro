
!start.

+!start <- iam(planet).

+population(X)
    : X < 100 & .random(Y) & K=math.round(Y*25) & K>23
   <- VAL=K-23;
      increasePopulation(VAL).

+step(X) <- .println("mandando nope para step ", X); nope.
