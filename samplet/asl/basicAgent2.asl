{ include("appraisal.asl") }
{ include("supportDiscoverLocation.asl") }
//-----------------------------------------------------------------------------
!start.
//-----------------------------------------------------------------------------
+!start
    <- nope; // if have some preprocess do here!
       !!deliberation.
//-----------------------------------------------------------------------------
+!deliberation
    : myself[hungry(H), social(S), energy(E)] & (H=0 | S=0 | E=0) & .my_name(NAME)
    <- .println("agent ", NAME, " died by one of (hungry=", H,", social=",S,", energy=",E,")");
       hide;
       .kill_agent(NAME).
//-----------------------------------------------------------------------------
+!deliberation
     : .random(R) & NRANDOM=math.round(R*100000)
    <- ?appraisal;
       !behaviour(NRANDOM). // 0 to 100000
//-----------------------------------------------------------------------------
+!behaviour(RANDOM)
     : not(room(_))
    <- .println("doing discover location");
       !!planDiscoverLocation(RANDOM).
//-----------------------------------------------------------------------------
+!behaviour(N)
     : feeling(distress, DV) & DV > 0
    <- .println("doing rest");
       ?step(STEP);
       !!planRest(N, STEP+15).
//-----------------------------------------------------------------------------
+!behaviour(_)
    : myself[hungry(H), social(S), energy(E), cleaning(C)]
    <- .println("doing nope (hungry=", H,", social=",S,", energy=",E,", cleaning=",C,")");
       nope;
       !!deliberation.
//-----------------------------------------------------------------------------
+!planDiscoverLocation(RANDOM)
    : myself[lookFor(ORIENTATION)] & not(perceived(ORIENTATION, _, _))
   <- .println("perceiving the around environment");
      .findall(OBJ, object(OBJ)[source(percept)], LISTA);
      // this need be arity 3 because Dumper class not support list...
      +perceived(ORIENTATION, LISTA, 0);
      !!planDiscoverLocation(RANDOM).
//-----------------------------------------------------------------------------
+!planDiscoverLocation(RANDOM)
     : .findall(O, perceived(O, _, _), OS)
     & .difference(["N","E","S","W"], OS, LS)
     & .length(LS, LENGTH) & LENGTH > 0
    <- .println("look for a new orientation");
       POSITION=RANDOM mod LENGTH;
       .nth(POSITION, LS, NEWO);
       changeOrientation(NEWO);
       !!planDiscoverLocation(RANDOM).
//-----------------------------------------------------------------------------
+!planDiscoverLocation(RANDOM)
     : .findall(O, perceived(_, O, _), OS)
     & .length(OS, LENGTH) & LENGTH > 2
    <- .println("calculate position");
       !calculatePosition(OS);
       !!deliberation.
//-----------------------------------------------------------------------------
+!planRest(_, LastStep)
    :  step(LastStep)
    <- .println("forget plan rest");
       // TODO put disappointment emotion
       !!deliberation.
//-----------------------------------------------------------------------------
+!planRest(RANDOM, LastStep)
    <- .println("planning rest");
       ?appraisal;
       ?room(ROOM);
       !planRoute("bed", ROOM, ROUTE);
       nope;
       !!planRest(RANDOM, LastStep).
//-----------------------------------------------------------------------------
-!X <- .println("Handler failure: ", X);
       !!deliberation.
//-----------------------------------------------------------------------------
//+agent(NAME)[lastAction(ANAME,ASTEP)]
//    : step(STEP)
//    <- .println("agent ",NAME," previous action ", ANAME," on time ", ASTEP, " now ", STEP, " LK").
