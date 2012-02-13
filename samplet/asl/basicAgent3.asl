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
     : .random(R) & NRANDOM=math.round(R*100000)
    <- ?appraisal;
       !behaviour(NRANDOM). // 0 to 100000
//-----------------------------------------------------------------------------
+!behaviour(RANDOM)
     : not(room(_))
    <- .println("doing discover location");
       !planDiscoverLocation(RANDOM);
       ?room(ROOM);
       .println("I am on room ", ROOM);
       !!deliberation.
//-----------------------------------------------------------------------------
+!behaviour(N)
     : feeling(distress, DV) & DV > 0
    <- .println("doing rest");
       ?step(STEP);
       !!planRest(N, STEP+15).
//-----------------------------------------------------------------------------
+!behaviour(N)
    : not(feeling(distress, PV))
    & fixedPlace(_)[opening(OP),closing(CL),name(LOCATION)]
    & step(STEP) & beforeGo(BEFORE) & GO = OP - BEFORE
    & STEP >= GO & STEP <= CL
    <- .println("going to ", LOCATION);
       !!planGoOut(LOCATION, N, STEP, OP).
//-----------------------------------------------------------------------------
+!behaviour(RANDOM)
    : myself[energy(E)]
    & ARRAY=[changeOrientation("N"), changeOrientation("S"), //0 and 1
             changeOrientation("E"), changeOrientation("W"), //2 and 3
             forward, forward, forward, forward, forward, nope]
    & .nth(RANDOM mod 10, ARRAY, ACTION)
    <- .println("doing ", ACTION, " (energy=",E,")");
       ACTION;
       !!deliberation.
//-----------------------------------------------------------------------------
+!behaviour(_) // probably not reach here...
    : myself[energy(E)]
    <- .println("doing nope (energy=",E,")");
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
       !calculatePosition(OS);
       .abolish(perceived(_,_)).
//-----------------------------------------------------------------------------
+!planRest(RANDOM, LastStep)
    : step(LastStep) | (myself[energy(E)] & E >= 100)
    <- .println("planning rest: forget plan");
       .abolish(target(_,_));
       !!deliberation.
//-----------------------------------------------------------------------------
+!planRest(RANDOM, LastStep)
    :  not(target(_,_)) & room(ROOM)
    <- .println("planning rest: find a goal from room ", ROOM);
       if (not rest(_)) {
          !planRoute("bed", ROOM, ROUTE);
          !nearRouteOf(ROUTE, _, _, route(O, DISTANCE, PLAN));
        } else {
          ?rest(O);
          !planRoute(O, ROOM, ROUTE);
          .member(route(O, _, PLAN), ROUTE);
        }
       +target(O, PLAN);
       .println("planning rest: found object ", O, " and route ", PLAN);
       !!planReach(RANDOM, LastStep).
//-----------------------------------------------------------------------------
+!planReach(RANDOM, LastStep)
    : (step(LastStep) & NEWRANDOM=RANDOM)
    | (feeling(joy, X) & X > 5 & (RANDOM mod 10) >= 7
        & .random(R) & NEWRANDOM=math.round(R*100000))
    <- !!planRest(NEWRANDOM, LastStep). // only one place
//-----------------------------------------------------------------------------
+!planReach(RANDOM, LastStep)
     : target(O, _) & ~rest(O)
    <- .println("planning reach object ", O, " canceled because is not my bed");
       .abolish(target(_,_));
        !planDiscoverLocation(RANDOM);
       !!planRest(RANDOM, LastStep).
//-----------------------------------------------------------------------------
+!planReach(RANDOM, LastStep)
    :  target(O, [])
    &  fixMeOrientation(O, NEWO, true) // perceived object and is near (true)
    &  myself[lookFor(NEWO), energy(E)]
    <- .println("planning reach object ", O, ": try use (energy=",E,")");
        ?appraisal;
        tryUseObject;
        !!planReach(RANDOM, LastStep).
//-----------------------------------------------------------------------------
+!planReach(RANDOM, LastStep)
    :  target(O, [])
    &  fixMeOrientation(O, NEWO, _)
    &  myself[lookFor(NEWO)]
    <- .println("planning reach object ", O, ": approach");
        ?appraisal;
        forward;
        !!planReach(RANDOM, LastStep).
//-----------------------------------------------------------------------------
+!planReach(RANDOM, LastStep)
    :  target(O, [])
    &  fixMeOrientation(O, NEWO, _)
    <- .println("planning reach object ", O, ": fix orientation");
        ?appraisal;
        changeOrientation(NEWO);
        !!planReach(RANDOM, LastStep).
//-----------------------------------------------------------------------------
+!planReach(RANDOM, LastStep)
    :  target(O, [P|R]) & fixMeOrientation(P, NEWO, true) & myself[lookFor(NEWO)]
    <- .println("planning reach object ", P, ": use gate");
       ?appraisal;
       tryUseObject;
       .abolish(target(_,_)); +target(O, R);
       !changeRoom(P); !planDiscoverLocation(RANDOM);
       !!planReach(RANDOM, LastStep).
//-----------------------------------------------------------------------------
+!planReach(RANDOM, LastStep)
    :  target(O, [P|R]) & fixMeOrientation(P, NEWO, _)
    &  myself[lookFor(NEWO)]
    <- .println("planning reach object ", P, ": approach to gate");
       ?appraisal;
       forward;
       !!planReach(RANDOM, LastStep).
//-----------------------------------------------------------------------------
+!planReach(RANDOM, LastStep)
    :  target(O, [P|R]) & fixMeOrientation(P, NEWO, _)
    <- .println("planning reach object ", P, ": fix orientation to gate");
       ?appraisal;
       changeOrientation(NEWO);
       !!planReach(RANDOM, LastStep).
//-----------------------------------------------------------------------------
+!planReach(RANDOM, LastStep)
     : target(O, []) | target(_,[O|_])
    <- .println("planning reach object ", O, ": meet object");
       sims.ia.planRoute(O, PLAN);
       //.println("plan to reach object ", O, " = ", PLAN);
       !!planReachObject(RANDOM, LastStep, PLAN).
//-----------------------------------------------------------------------------
+!planReachObject(R, L, _)
    : step(L)
    <- !!planReach(R, L).
//-----------------------------------------------------------------------------
+!planReachObject(R, L, [STEP|[]])
     : myself[lookFor(STEP)]
    <- .println("planning reach object finish");
       !!planReach(R, L).
//-----------------------------------------------------------------------------
+!planReachObject(R, L, [STEP|PLAN])
     : myself[lookFor(STEP)]
    <- .println("planning reach object: approach");
       forward;
       !!planReachObject(R, L, PLAN).
//-----------------------------------------------------------------------------
+!planReachObject(R, L, [STEP|PLAN])
    <- .println("planning reach object: fix orientation");
       changeOrientation(STEP);
       !!planReachObject(R, L, [STEP|PLAN]).
//-----------------------------------------------------------------------------
-!planReachObject(R, L, _)
    <- .println("plan reach object: fail on route");
       .abolish(target(_,_)); nope;
       !!deliberation.
//-----------------------------------------------------------------------------
-!planReach(RANDOM,LASTSTEP)[code(sims.ia.planRoute(_,_))]
    <- .println("plan reach object: fail on locate a route");
       .abolish(target(_,_));
       ?room(ROOM);
       .broadcast(achieve, freedomMy(ROOM)); nope;
       !!deliberation.
//-----------------------------------------------------------------------------
-!planReach(RANDOM,LASTSTEP)
    <- .println("plan reach object: retry");
       nope; // stop now and try again
       !!planReach(RANDOM,LASTSTEP).
//-----------------------------------------------------------------------------
// se estou no lugar tenho que esperar o tempo de fechamento                                    
// se estou no lugar e meu tempo para chegar la terminou...                                     
// se nao estou no lugar e meu tempo para chegar la terminou...                                 OK
// se nao estou no lugar e ainda tenho tempo para chegar la e ja tenho uma lista de salas       OK
// se nao estou no lugar e ainda tenho tempo para chegar la                                     OK
//-----------------------------------------------------------------------------
//+!planGoOut(LOCATION, N, STEP, FINISH)
//     : false
//    <- true.
//-----------------------------------------------------------------------------
//+!planGoOut(LOCATION, N, STEP, FINISH)
//     : false
//    <- true.
//-----------------------------------------------------------------------------
+!planGoOut(LOCATION, N, STEP, FINISH)
     : step(FINISH)
    <- .println("plan go out: failed to reach point");
       ?appraisal;
       ?updateBeforeGo(STEP, FINISH, false);
       .abolish(goOutPlan(_,_));
       .abolish(goOutSubPlan(_));
       nope;
       !!deliberation.
//-----------------------------------------------------------------------------
+!planGoOut(LOCATION, N, STEP, FINISH)
     : goOutPlan(OBJ,[_|R]) & goOutSubPlan([ELEM|[]]) & myself[lookFor(ELEM)]
    <- .println("plan go out: using object");
       ?appraisal;
       tryUseObject;
       .abolish(room(_)); !planDiscoverLocation(N);
       .abolish(goOutSubPlan(_));
       .abolish(goOutPlan(_,_)); +goOutPlan(OBJ,R);
       !!planGoOut(LOCATION, N, STEP, FINISH).
//-----------------------------------------------------------------------------
+!planGoOut(LOCATION, N, STEP, FINISH)
     : goOutPlan(_,_) & goOutSubPlan([ELEM|R]) & myself[lookFor(ELEM)]
    <- .println("plan go out: approach");
       ?appraisal;
       forward;
       .abolish(goOutSubPlan(_)); +goOutSubPlan(R);
       !!planGoOut(LOCATION, N, STEP, FINISH).
//-----------------------------------------------------------------------------
+!planGoOut(LOCATION, N, STEP, FINISH)
     : goOutPlan(_,_) & goOutSubPlan([ELEM|R])
    <- .println("plan go out: fixing orientation to ", ELEM);
       ?appraisal;
       changeOrientation(ELEM);
       !!planGoOut(LOCATION, N, STEP, FINISH).
//-----------------------------------------------------------------------------
+!planGoOut(LOCATION, N, STEP, FINISH)
     : goOutPlan(ELEM, []) | goOutPlan(_, [ELEM|R])
    <- .println("plan go out: reach ", ELEM);
       ?appraisal;
       sims.ia.planRoute(ELEM, PLAN);
       +goOutSubPlan(PLAN);
       .println("plan go out: reach ", ELEM, " using route ", PLAN);
       !!planGoOut(LOCATION, N, STEP, FINISH).
//-----------------------------------------------------------------------------
+!planGoOut(LOCATION, N, STEP, FINISH)
    <- .println("plan go out: planning route");
       ARRAY = ["doorToHome1", "doorToHome2"];
       POS = (N mod 2);
       .nth(POS, ARRAY, ELEM);
       ?room(ROOM);
       ?appraisal;
       !planRoute(ELEM, ROOM, ROUTE);
       .member(route(ELEM, _, PLAN), ROUTE);
       +goOutPlan(ELEM, PLAN);
       !!planGoOut(LOCATION, N, STEP, FINISH).
//-----------------------------------------------------------------------------
-!planGoOut(LOCATION, N, STEP, FINISH)[code(sims.ia.planRoute(_,_))]
    <- .println("plan go out: no route found");
       ?room(ROOM);
       .broadcast(achieve, freedomMy(ROOM));
       .abolish(goOutPlan(_,_)); .abolish(goOutSubPlan(_));
       !!deliberation.
//-----------------------------------------------------------------------------
//?updateBeforeGo(InitialStep, FinishStep, true/false of reach the place).
//fixedPlace("abstractWork")[closing(17),name("abstractWork"),opening(8),source(percept)].
//-----------------------------------------------------------------------------
+!freedomMy(ROOM)
    :  room(ROOM)
    <- .findall(D, .desire(D), DS);
       .findall(CI, .current_intention(CI), IS);
       .drop_all_desires; .drop_all_intentions;
       .abolish(goOutSubPlan(_)); .abolish(goOutPlan(_, _));
       .abolish(perceived(_,_)); .abolish(target(_,_));
       !!freedomRetry(0).
//-----------------------------------------------------------------------------
+!freedomMy(ROOM).
//-----------------------------------------------------------------------------
+!freedomRetry(QTD)
    <- ?room(ROOM);
       sims.ia.getItemsAtPlace(ROOM, LIST);
       .length(LIST, LISTLEN);
       .random(N); RANDOM = math.round(N * 100000);
       .nth(RANDOM mod LISTLEN, LIST, POS);
       sims.ia.planRoute(POS, PLAN);
       +goOutPlan(POS, []); +goOutSubPlan(PLAN);
       !!planGoOut("freedom", RANDOM, -1, -1).
//-----------------------------------------------------------------------------
-!freedomRetry(QTD)
    : room(ROOM) & sims.ia.getItemsAtPlace(ROOM, LIST)
    & .length(LIST, LISTLEN) & QTD > (2*LISTLEN)
    <- .abolish(goOutSubPlan(_)); .abolish(goOutPlan(_, _));
       nope;
       !!deliberation.
//-----------------------------------------------------------------------------
-!freedomRetry(QTD)
    <- !!freedomRetry(RANDOM, LASTSTEP, QTD+1).
//-----------------------------------------------------------------------------
-!X <- .println("Handler failure: ", X);
       !!deliberation.
//-----------------------------------------------------------------------------
+object(O)[utility(sleep),owner(K)]
    : not(rest(O)) & not(~rest(O))
    & .my_name(NAME)
    & (K=NAME | (.list(K) & .sublist([NAME], K)))
    <- .println("object ",O," is my bed");
       +rest(O).
//-----------------------------------------------------------------------------
+object(O)[utility(sleep),owner(K)]
    : not(rest(O)) & not(~rest(O))
    <- +~rest(O).
//-----------------------------------------------------------------------------
