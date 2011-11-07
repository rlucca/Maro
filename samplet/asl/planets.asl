
!start.

+!start <- iam(planet).

+population(X)
    : X < 2000 & .random(Y) & K=math.round(Y*25) & K>23
   <- VAL=K-16;
      increasePopulation(VAL).

+step(X)
     : .random(Y) & K=math.round(Y*10000) & K < 51 & population(P)
    <- .println("step ", X, " com populacao ", P); increasePopulation(2).

+step(X)
     : population(P)
    <- .println("step ", X, " com populacao ", P); increasePopulation(-4).
