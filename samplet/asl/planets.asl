
!start.

+!start <- iam(planet).

@step0[atomic]
+population(X)
    : X < 2000 & .random(Y) & K=math.round(Y*25) & K>23
   <- increasePopulation(K).

@step1[atomic]
+step(X)
     : .random(Y) & K=math.round(Y*10000) & K < 51 & population(P)
    <- .println("step ", X, " com populacao ", P); increasePopulation(2).

@step2[atomic]
+step(X)
     : population(P) & P > 6
    <- .println("step ", X, " com populacao ", P); increasePopulation(-4).

@step3[atomic]
+step(X)
    <- nope.
