fullLife(100).

!start.

+life(L) <- .println("life ", L).

+!start <- iam(agent).

@step1[atomic]
+step(X)
    : life(0)
    <- .println("morreu"); death.

@step2[atomic]
+step(X) <- nope.
