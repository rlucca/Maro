{ include("appraisal.asl") }
//-----------------------------------------------------------------------------
!start.
//-----------------------------------------------------------------------------
//rules?
//-----------------------------------------------------------------------------
+!start <- nope; !!deliberation. // if have some preprocess do here!
//-----------------------------------------------------------------------------
+!deliberation
    : myself[hungry(H), social(S), energy(E)] & (H=0 | S=0 | E=0) & .my_name(NAME)
    <- .println("agent ", NAME, " died by one of (hungry=", H,", social=",S,", energy=",E,")");
       hide;
       .kill_agent(NAME).
+!deliberation
     : .random(R) & NRANDOM=math.round(X*100000)
    <- !behaviour(NRANDOM); // 0 to 100000
       !!deliberation.
//-----------------------------------------------------------------------------
+!behaviour(_)
    <- //.findall(X, X, L); .println("all things: ", L);
       .println("doing nope "); nope.
//-----------------------------------------------------------------------------
-!X <- .println("Handler failure: ", X).
