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
     : life(0)
    <- .println("morreu");
       death.
+!behavior(STEP, RANDOM)
    :  not(life(0)) & keep(TX, PX, PY) & not(onPlanet) & position(AX, AY)
   <- .println("fuga to planet continue ", PX, "x", PY, " ", AX, AY);
      !orientationTo(PX, PY, NO);
      changeOrientationTo(NO);
      forward.
+!behavior(STEP, RANDOM)
    :  not(life(0)) & keep(TX, PX, PY) & onPlanet
   <- .println("fuga to planet terminou");
      -keep(TX, PX, PY);
      nope.
+!behavior(STEP, RANDOM)
    :  not(life(0)) & keep(TX, PX, PY) & STEP >= TX
   <- .println("fuga to planet last");
      -keep(TX, PX, PY);
      forward.
+!behavior(STEP, RANDOM)
    :  not(life(0)) & onPlanet & life(L) & fullLife(FL) & L < FL
   <- .println("recover");
      recover.
+!behavior(STEP, RANDOM)
    :  not(life(0)) & not(qtyPlanets(0)) & planet(PX,PY) & lowLife(LL) & life(L) & L < LL
   <- .println("fuga to planet");
      !orientationTo(PX, PY, NO);
      +keep(STEP+6, PX, PY);
      changeOrientationTo(NO);
      forward.
+!behavior(STEP, RANDOM)
    :  not(life(0)) & qtyShips(S) & S > 1 & notFight(NF) & STEP < NF
   <- .println("fuga away from atacante continue");
      forward.
+!behavior(STEP, RANDOM)
    :  not(life(0)) & qtyShips(0) & notFight(NF) & STEP < NF
   <- .println("fuga away terminou");
      -notFight(NF);
      nope.
+!behavior(STEP, RANDOM)
    :  not(life(0)) & notFight(STEP)
   <- .println("fuga away from atacante last");
      -notFight(STEP);
      forward.
+!behavior(STEP, RANDOM)
    :  not(life(0)) & qtyShips(S) & S > 1 & RANDOM < 10 & position(PX,PY)
   <- .println("fuga away from atacante");
      ?findStriker(PX, PY, O, D);
      ?orientation(OO);
      +notFight(STEP+RANDOM);
      !goAway(OO, O).
+!behavior(STEP, RANDOM)
    :  not(life(0)) & qtyShips(S) & S > 0 & position(PX, PY)
   <- .println("atacar");
      ?findStriker(PX, PY, O, D);
      ?orientation(OO);
      !attack(D, OO, O).
+!behavior(STEP, RANDOM)
    :  not(life(0)) & K=RANDOM mod 100
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
+!orientationTo(X,_, "W") : position(PX,_) & PX > X.
+!orientationTo(X,_, "E") : position(PX,_) & PX < X.
+!orientationTo(X,Y, "N") : position(X,PY) & PY > Y.
+!orientationTo(X,Y, "S") : position(X,PY) & PY < Y.
// Nao deveria acontecer...
+!orientationTo(X,Y, "S") : position(X,Y).
//-----------------------------------------------------------------------------
// Se houver um atacante na mesma posicao entao a orientacao nao importa!
+?findStriker(PX, PY, O, 0)      : ship(PX, PY) & orientation(O).
// Verifica se na orientacao atual tem um agente a atacar!
+?findStriker(PX, PY, "N", 1)    : orientation("N") & ship(PX, PY-1).
+?findStriker(PX, PY, "E", 1)    : orientation("E") & ship(PX+1, PY).
+?findStriker(PX, PY, "S", 1)    : orientation("S") & ship(PX, PY+1).
+?findStriker(PX, PY, "W", 1)    : orientation("W") & ship(PX-1, PY).
+?findStriker(PX, PY, "N", 2)    : orientation("N") & ship(PX-1, PY-1).
+?findStriker(PX, PY, "E", 2)    : orientation("E") & ship(PX+1, PY-1).
+?findStriker(PX, PY, "S", 2)    : orientation("S") & ship(PX+1, PY+1).
+?findStriker(PX, PY, "W", 2)    : orientation("W") & ship(PX-1, PY+1).
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
