{ include("appraisal.asl") }
{ include("supportDiscoverLocation.asl") }
{ include("util.asl") }
//-----------------------------------------------------------------------------
!start.
//-----------------------------------------------------------------------------
+!start
    <- nope; // if have some preprocess do here!
       !!deliberation.
//-----------------------------------------------------------------------------
+!deliberation
    : myself[energy(0)] & .my_name(NAME)
    //: myself[hungry(H), social(S), energy(E)] & (H=0 | S=0 | E=0) & .my_name(NAME)
    //<- .println("agent ", NAME, " died by one of (hungry=", H,", social=",S,", energy=",E,")");
    <- .println("agent ", NAME, " died by one of (energy=",E,")");
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
       !planDiscoverLocation(RANDOM);
       !!deliberation.
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
    : myself[lookFor(ORIENTATION)] & not(perceived(ORIENTATION, _))
   <- .println("perceiving the around environment");
      .findall(OBJ, object(OBJ)[source(percept)], LISTA);
      +perceived(ORIENTATION, LISTA);
      !planDiscoverLocation(RANDOM).
//-----------------------------------------------------------------------------
+!planDiscoverLocation(RANDOM)
     : .findall(O, perceived(O, _), OS)
     & .difference(["N","E","S","W"], OS, LS)
     & .length(LS, LENGTH) & LENGTH > 0
    <- .println("look for a new orientation");
       POSITION=RANDOM mod LENGTH;
       .nth(POSITION, LS, NEWO);
       changeOrientation(NEWO);
       !planDiscoverLocation(RANDOM).
//-----------------------------------------------------------------------------
+!planDiscoverLocation(RANDOM)
     : .findall(O, perceived(_, O), OS)
     & .length(OS, LENGTH) & LENGTH > 2
    <- .println("calculate position");
       !calculatePosition(OS).
//-----------------------------------------------------------------------------
+!planRest(_, LastStep)
    :  step(LastStep)
    <- .println("forget plan rest");
       -target(_, _);
       !!deliberation.
//-----------------------------------------------------------------------------
+!planRest(RANDOM, LastStep)
    :  object(O)[utility(sleep),owner(K)] & not(rest(O))
    & .my_name(NAME)
    & (K=NAME | (.list(K) & .sublist([NAME], K)))
    <- .println("plan rest try to reach ", O);
       +rest(O);
       !!planRest(RANDOM, LastStep).
//-----------------------------------------------------------------------------
+!planRest(RANDOM, LastStep)
    :  object(O)[utility(sleep),owner(K)] & not(rest(O)) & not(~rest(O))
    <- .println("plan rest try to reach ", O, ": isnt my bed");
       +~rest(O);
       !!planRest(RANDOM, LastStep).
//-----------------------------------------------------------------------------
+!planRest(RANDOM, LastStep)
    : target(O, _) & ~rest(O)
    <- .println("plan rest forget target ", O, " because isnt my bed");
       -target(O, _);
       !!planRest(RANDOM, LastStep).
//-----------------------------------------------------------------------------
+!planRest(RANDOM, LastStep)
    :  target(O, []) & rest(O) & fixMeOrientation(O, NEWO)
    &  myself[lookFor(NEWO)]
    <- .println("plan rest try to reach ", O, ": try use");
        tryUseObject;
        !!planRest(RANDOM, LastStep).
//-----------------------------------------------------------------------------
+!planRest(RANDOM, LastStep)
    :  target(O, []) & rest(O) & fixMeOrientation(O, NEWO)
    <- .println("plan rest try to reach ", O, ": fix orientation to use");
        changeOrientation(NEWO);
        !!planRest(RANDOM, LastStep).
//-----------------------------------------------------------------------------
+!planRest(RANDOM, LastStep)
    :  target(O, []) & perceived(ORIENTATION, LISTA)
    & .sublist([O], LISTA) & myself[lookFor(ORIENTATION)]
    <- .println("plan rest try to reach ", O, ": approach");
        forward;
        !!planRest(RANDOM, LastStep).
//-----------------------------------------------------------------------------
+!planRest(RANDOM, LastStep)
    :  target(O, []) & perceived(ORIENTATION, LISTA)
    & .sublist([O], LISTA)
    <- .println("plan rest try to reach ", O, ": approach fix direction");
        changeOrientation(ORIENTATION);
        !!planRest(RANDOM, LastStep).
//-----------------------------------------------------------------------------
+!planRest(RANDOM, LastStep)
    :  target(O, [])
    <- .println("plan rest try to reach ", O, ": what?");
        nope; /// what I do?
        !!planRest(RANDOM, LastStep).
//-----------------------------------------------------------------------------
+!planRest(RANDOM, LastStep)
    :  target(O, [P|R]) & fixMeOrientation(P, NEWO) & myself[lookFor(NEWO)]
    <- .println("plan rest try to reach ", P, " perceived: use object");
       tryUseObject;
       .abolish(perceived(_,_));
       -target(_,_); +target(O, R);
       !changeRoom(P); !planDiscoverLocation(RANDOM);
       !!planRest(RANDOM, LastStep).
//-----------------------------------------------------------------------------
+!planRest(RANDOM, LastStep)
    :  target(O, [P|R]) & fixMeOrientation(P, NEWO)
    <- .println("plan rest try to reach ", P, " perceived: fix me orientation");
       changeOrientation(NEWO);
       !!planRest(RANDOM, LastStep).
//-----------------------------------------------------------------------------
+!planRest(RANDOM, LastStep)
    :  target(O, [P|R]) & perceived(ORIENTATION, LISTA) & .sublist([P], LISTA)
    &  myself[lookFor(ORIENTATION)]
    <- .println("plan rest try to reach ", P, " perceived: forward");
       forward;
       !!planRest(RANDOM, LastStep).
//-----------------------------------------------------------------------------
+!planRest(RANDOM, LastStep)
    :  target(O, [P|R]) & perceived(ORIENTATION, LISTA) & .sublist([P], LISTA)
    <- .println("plan rest try to reach ", P, " perceived: new direction");
       changeOrientation(ORIENTATION);
       !!planRest(RANDOM, LastStep).
//-----------------------------------------------------------------------------
+!planRest(RANDOM, LastStep)
    :  target(O, [P|R]) & object(P) & myself[lookFor(ORIENTATION)]
    <- .println("plan rest try to reach ", P, " not perceived: perceived");
       .findall(L, object(L), LIST);
       -perceived(ORIENTATION, _);
       +perceived(ORIENTATION, LIST);
       !!planRest(RANDOM, LastStep).
//-----------------------------------------------------------------------------
+!planRest(RANDOM, LastStep) // FIX LATER
    :  target(O, [P|R]) & .my_name(nina) & not(fixCycle)
    <- .println("plan rest try to reach ", P, " not perceived: direction");
       changeOrientation("W"); +fixCycle;
       !!planRest(RANDOM, LastStep).
//-----------------------------------------------------------------------------
+!planRest(RANDOM, LastStep)
    :  target(O, [P|R])
    <- .println("plan rest try to reach ", P, " not perceived: forward");
       forward;
       !!planRest(RANDOM, LastStep).
//-----------------------------------------------------------------------------
+!planRest(RANDOM, LastStep)
    :  not(target(_,_)) & not(rest(_))
    <- .println("planning rest");
       ?appraisal;
       ?room(ROOM);
       !planRoute("bed", ROOM, ROUTE);
       !nearRouteOf(ROUTE, _, _, route(O, DISTANCE, PLAN));
       +target(O, PLAN);
       !!planRest(RANDOM, LastStep).
//-----------------------------------------------------------------------------
+!planRest(RANDOM, LastStep)
    :  not(target(_,_)) & rest(REST)
    <- .println("planning rest");
       ?appraisal;
       ?room(ROOM);
       !planRoute(REST, ROOM, ROUTE);
       .member(route(REST, _, PLAN), ROUTE);
       +target(REST, PLAN);
       !!planRest(RANDOM, LastStep).
//-----------------------------------------------------------------------------
-!planRest(R,L)[code(forward),code_line(169),error(action_failed)]
     : myself[lookFor(O), positionX(MX), positionY(MY)]
     & ARRAY=["N","W","S","E","N"]
     & myNth(POS, ARRAY, O)
     & myNth(POS+1, ARRAY, NEWO)
    <- .println("plan rest changing direction: turn left");
       changeOrientation(NEWO);
       !!planRest(R,L).
//-----------------------------------------------------------------------------
-!planRest(R,L)[code(forward),error(action_failed)]
     : myself[lookFor(O)] & perceived(O,_)
    <- .println("plan rest erasing actual perceived list");
       -perceived(O,_);
       !!planRest(R,L).
//-----------------------------------------------------------------------------
-!planRest(R,L)[code(forward),error(action_failed)]
     : myself[lookFor(O)]
    <- .println("plan rest actual perceived list lost.. rebuild");
       !planDiscoverLocation(R);
       !!planRest(R,L).
//-----------------------------------------------------------------------------
-!planRest(R,L)[code(tryUseObject),error(action_failed)]
    // We only retry it because the another agent moved.
    <- !!planRest(R,L).
//-----------------------------------------------------------------------------
-!X <- .println("Handler failure: ", X);
       !!deliberation.
//-----------------------------------------------------------------------------
//+agent(NAME)[lastAction(ANAME,ASTEP)]
//    : step(STEP)
//    <- .println("agent ",NAME," previous action ", ANAME," on time ", ASTEP, " now ", STEP, " LK").
