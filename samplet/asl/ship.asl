fullLife(100).
lowLife(25). // 25% de 100
//-----------------------------------------------------------------------------
!start.
+!start <- iam(ship); !!reactive.
//-----------------------------------------------------------------------------
+!reactive
     : step(X) & .random(Y) & K=math.round(Y*10000)
    <- !behavior(X, K);
       !!reactive.
-!reactive
    <- .println("reactive plan failed! Doing nope..."); nope;
       !!reactive.
//-----------------------------------------------------------------------------
+!behavior(STEP, RANDOM)
     : myself(_,_,_)[quality(0)]
    <- .println("morreu");
       death.
+!behavior(STEP, RANDOM)
    :  not(myself(_,_,_)[quality(0)]) & keep(TX, PX, PY) & not(onPlanet) & myself(_,_,_)[positionX(AX)] & myself(_,_,_)[positionY(AY)]
   <- .println("fuga to planet continue ", PX, "x", PY, " ", AX, AY);
      !orientationTo(PX, PY, NO);
      changeOrientationTo(NO);
      forward.
+!behavior(STEP, RANDOM)
    :  not(myself(_,_,_)[quality(0)]) & keep(TX, PX, PY) & onPlanet
   <- .println("fuga to planet terminou");
      -keep(TX, PX, PY);
      nope.
+!behavior(STEP, RANDOM)
    :  not(myself(_,_,_)[quality(0)]) & keep(TX, PX, PY) & STEP >= TX
   <- .println("fuga to planet last");
      -keep(TX, PX, PY);
      forward.
+!behavior(STEP, RANDOM)
    :  not(myself(_,_,_)[quality(0)]) & onPlanet & myself(_,_,_)[quality(L)] & fullLife(FL) & L < FL
   <- .println("recover");
      recover.
+!behavior(STEP, RANDOM)
    :  not(myself(_,_,_)[quality(0)]) & not(qtyPlanets(0)) & planet(PX,PY) & lowLife(LL) & myself(_,_,_)[quality(L)] & L < LL
   <- .println("fuga to planet");
      !orientationTo(PX, PY, NO);
      +keep(STEP+6, PX, PY);
      changeOrientationTo(NO);
      forward.
+!behavior(STEP, RANDOM)
    :  not(myself(_,_,_)[quality(0)]) & qtyShips(S) & S > 1 & notFight(NF) & STEP < NF
   <- .println("fuga away from atacante continue");
      forward.
+!behavior(STEP, RANDOM)
    :  not(myself(_,_,_)[quality(0)]) & qtyShips(0) & notFight(NF) & STEP < NF
   <- .println("fuga away terminou");
      -notFight(NF);
      nope.
+!behavior(STEP, RANDOM)
    :  not(myself(_,_,_)[quality(0)]) & notFight(STEP)
   <- .println("fuga away from atacante last");
      -notFight(STEP);
      forward.
+!behavior(STEP, RANDOM)
    :  not(myself(_,_,_)[quality(0)]) & qtyShips(S) & S > 1 & RANDOM < 10 & myself(_,_,_)[positionX(PX)] & myself(_,_,_)[positionY(PY)]
   <- .println("fuga away from atacante");
      ?findStriker(PX, PY, O, D);
      ?myself(_,_,_)[lookFor(OO)];
      +notFight(STEP+RANDOM);
      !goAway(OO, O).
+!behavior(STEP, RANDOM)
    :  not(myself(_,_,_)[quality(0)]) & qtyShips(S) & S > 0 & myself(_,_,_)[positionX(PX)] & myself(_,_,_)[positionY(PY)]
   <- .println("atacar");
      ?findStriker(PX, PY, O, D);
      ?myself(_,_,_)[lookFor(OO)];
      !attack(D, OO, O).
+!behavior(STEP, RANDOM)
    :  not(myself(_,_,_)[quality(0)]) & K=RANDOM mod 100
   <- .println("andarilho"); !newOrientation(K, L);
      changeOrientationTo(L);
      forward.
+!behavior(STEP, RANDOM)
   <- .println("nope");
      nope.
//-----------------------------------------------------------------------------
+!goAway(ActualOrientation, ActualOrientation)
    <- !oposite(ActualOrientation, L);
       changeOrientationTo(L);
       forward.
+!goAway(ActualOrientation, EnemyOrientation)
    <- forward.
//-----------------------------------------------------------------------------
+!oposite("N", "S").
+!oposite("E", "W").
+!oposite("S", "N").
+!oposite("W", "E").
//-----------------------------------------------------------------------------
+!newOrientation(K, "N") :  K < 25.
+!newOrientation(K, "E") :  K < 50.
+!newOrientation(K, "S") :  K < 75.
+!newOrientation(K, "W").
//-----------------------------------------------------------------------------
+!attack(2, Orientation, Orientation)
    <- forward.
+!attack(D, Orientation, Orientation)
    <- fire.
+!attack(_, OldOrientation, NewOrientation)
    <- changeOrientationTo(NewOrientation).
//-----------------------------------------------------------------------------
+!orientationTo(X,_, "W") : myself(_,_,_)[positionX(PX)] & PX > X.
+!orientationTo(X,_, "E") : myself(_,_,_)[positionX(PX)] & PX < X.
+!orientationTo(X,Y, "N") : myself(_,_,_)[positionX(X)] & myself(_,_,_)[positionY(PY)] & PY > Y.
+!orientationTo(X,Y, "S") : myself(_,_,_)[positionX(X)] & myself(_,_,_)[positionY(PY)] & PY < Y.
// Nao deveria acontecer...
+!orientationTo(X,Y, "S") : myself(_,_,_)[positionX(X)] & myself(_,_,_)[positionY(Y)].
//-----------------------------------------------------------------------------
// Se houver um atacante na mesma posicao entao a orientacao nao importa!
+?findStriker(PX, PY, O, 0)      : ship(PX, PY) & myself(_,_,_)[lookFor(O)].
// Verifica se na orientacao atual tem um agente a atacar!
+?findStriker(PX, PY, "N", 1)    : myself(_,_,_)[lookFor("N")] & ship(PX, PY-1).
+?findStriker(PX, PY, "E", 1)    : myself(_,_,_)[lookFor("E")] & ship(PX+1, PY).
+?findStriker(PX, PY, "S", 1)    : myself(_,_,_)[lookFor("S")] & ship(PX, PY+1).
+?findStriker(PX, PY, "W", 1)    : myself(_,_,_)[lookFor("W")] & ship(PX-1, PY).
+?findStriker(PX, PY, "N", 2)    : myself(_,_,_)[lookFor("N")] & ship(PX-1, PY-1).
+?findStriker(PX, PY, "E", 2)    : myself(_,_,_)[lookFor("E")] & ship(PX+1, PY-1).
+?findStriker(PX, PY, "S", 2)    : myself(_,_,_)[lookFor("S")] & ship(PX+1, PY+1).
+?findStriker(PX, PY, "W", 2)    : myself(_,_,_)[lookFor("W")] & ship(PX-1, PY+1).
// nova orientacao para inimigos proximos
+?findStriker(PX, PY, "N", 1)    : ship(PX, PY-1).
+?findStriker(PX, PY, "E", 1)    : ship(PX+1, PY).
+?findStriker(PX, PY, "S", 1)    : ship(PX, PY+1).
+?findStriker(PX, PY, "W", 1)    : ship(PX-1, PY).
// a dois de alcance...
+?findStriker(PX, PY, "N", 2) 
    : ship(PX, PY-2) | ship(PX-1, PY-2) | ship(PX+1, PY-2) | ship(PX-1, PY-1).
+?findStriker(PX, PY, "E", 2)
    : ship(PX+2, PY) | ship(PX+2, PY-1) | ship(PX+2, PY+1) | ship(PX+2, PY-2)
    | ship(PX+2, PY+2) | ship(PX+1, PY-1).
+?findStriker(PX, PY, "S", 2)
    : ship(PX, PY+2) | ship(PX-1, PY+2) | ship(PX+1, PY+2) | ship(PX+1, PY+1).
+?findStriker(PX, PY, "W", 2)
    : ship(PX-2, PY) | ship(PX-2, PY-1) | ship(PX-2, PY+1) | ship(PX-2, PY-2)
    | ship(PX-2, PY+2) | ship(PX-1, PY+1).
