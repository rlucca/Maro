//initial believes.
//-----------------------------------------------------------------------------
!start.
+!start : step(STEP) & STEP < 4
    <- .println("Step: ", STEP);
       .findall(L, hasThresholdType(L,_), HTT); .println("List of hasThresholdType knowledge: ", HTT);
       .findall(L, hasThreshold(L,_), HT); .println("List of hasThreshold knowledge: ", HT);
       .findall(L, isSetupOf(L,_), ISO); .println("List of isSetupOf knowledge: ", ISO);
       .findall(L, hasSetup(L,_), HS); .println("List of hasSetup knowledge: ", HS);
       .findall(L, setup(L), S); .println("List of setup knowledge: ", S);
       .findall(pp(A,P), priority(attract,A,P), AT); .println("List of attract knowledge: ", AT);
       .findall(pn(A,P), priority(repulse,A,P), RT); .println("List of repulse knowledge: ", RT);
       .findall(L, L, ALL); .println("List of all knowledge: ", ALL);
       //.wait({+step(_)});
       nope;
       !!start.
+!start : step(X) & .random(Y) & NUMBER=math.round(Y*10)
    <- !behaviour(NUMBER); !!start.
+!start <- !!start.

+!behaviour(0) <- .println("looking for N"); changeOrientation("N").
+!behaviour(1) <- .println("looking for S"); changeOrientation("S").
+!behaviour(2) <- .println("looking for E"); changeOrientation("E").
+!behaviour(3) <- .println("looking for W"); changeOrientation("W").
+!behaviour(4) <- .println("walking forward"); forward.
+!behaviour(5) <- .println("walking forward"); forward.
+!behaviour(6) <- .println("walking forward"); forward.
+!behaviour(7) <- .println("walking forward"); forward.
+!behaviour(8) <- .println("walking forward"); forward.
+!behaviour(9) <- .println("walking forward"); forward.
+!behaviour(10) <- .println("walking forward"); forward.
-!X <- .println("common failure for ", X); nope.
