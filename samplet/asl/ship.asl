fullLife(100).
lowLife(25). // 25% de 100


!start.

+!start <- iam(ship).

+life(L) <- .println("life ", L).

@step1[atomic]
+step(X)
    : life(0)
    <- .println("morreu"); death.

@step10[atomic]
+step(X)
    :  keep(TX, PX, PY) & not(onPlanet) & position(AX, AY)
   <- .println("fuga to planet continue ", PX, "x", PY, " ", AX, AY); !orientationTo(PX, PY, NO);
      changeOrientationTo(NO);
      forward.

@step11[atomic]
+step(X)
    :  keep(TX, PX, PY) & onPlanet
   <- .println("fuga to planet terminou"); -keep(TX, PX, PY); nope.

@step12[atomic]
+step(X)
    :  keep(TX, PX, PY) & X >= TX
   <- .println("fuga to planet last"); -keep(X, PX, PY); forward.

@step2[atomic]
+step(X)
    : onPlanet & life(L) & fullLife(FL) & L < FL
   <- .println("recover"); recover.

@step3[atomic]
+step(X)
    :  not(qtyPlanets(0)) & planet(PX,PY) & lowLife(LL) & life(L) & L < LL
   <- .println("fuga to planet"); !orientationTo(PX, PY, NO); +keep(X+6, PX, PY);
      changeOrientationTo(NO);
      forward.

@step7[atomic]
+step(X)
    : qtyShips(S) & S > 1 & notFight(NF) & X < NF
   <- .println("fuga away from atacante continue");
      forward.

@step8[atomic]
+step(X)
    : qtyShips(0) & notFight(NF) & X < NF
   <- .println("fuga away terminou");
      -notFight(X);
      nope.

@step9[atomic]
+step(X)
    : notFight(X)
   <- .println("fuga away from atacante last");
      -notFight(X);
      forward.

@step4[atomic]
+step(X)
    : qtyShips(S) & S > 1 & .random(Y) & K=math.round(Y*10000) & K < 10 & position(PX,PY)
   <- .println("fuga away from atacante"); ?findStriker(PX, PY, O, D);
      ?orientation(OO); +notFight(X+K);
      !goAway(D, OO, O).

@step5[atomic]
+step(X)
    : qtyShips(S) & S > 0 & position(PX, PY)
   <- .println("atacar"); ?findStriker(PX, PY, O, D);
      ?orientation(OO);
      !attack(D, OO, O).

@step6[atomic]
+step(X)
    : .random(Y) & K=math.round(Y*100)
   <- .println("andarilho"); !newOrientation(K, L);
      changeOrientationTo(L);
      forward.


@ot1[atomic]
+!orientationTo(X,_, "W")
    : position(PX,_) & PX > X.
@ot2[atomic]
+!orientationTo(X,_, "E")
    : position(PX,_) & PX < X.
@ot3[atomic]
+!orientationTo(X,Y, "N")
    : position(X,PY) & PY > Y.
@ot4[atomic]
+!orientationTo(X,Y, "S")
    : position(X,PY) & PY < Y.
// Nao deveria acontecer...
@ot5[atomic]
+!orientationTo(X,Y, "S")
    : position(X,Y).

@ga1[atomic]
+!goAway(D, ActualOrientation, ActualOrientation)
    <- !oposite(ActualOrientation, L);
       changeOrientationTo(L);
       forward.
@ga2[atomic]
+!goAway(D, ActualOrientation, EnemyOrientation)
    <- forward.


+!oposite("N", "S").
+!oposite("E", "W").
+!oposite("S", "N").
+!oposite("W", "E").

+!newOrientation(K, "N") :  K < 25.
+!newOrientation(K, "E") :  K < 50.
+!newOrientation(K, "S") :  K < 75.
+!newOrientation(K, "W").

+!attack(2, Orientation, Orientation)
    <- forward.
+!attack(D, Orientation, Orientation)
    <- fire.
+!attack(_, OldOrientation, NewOrientation)
    <- changeOrientationTo(NewOrientation).


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
+?findStriker(PX, PY, "N", 2)    : ship(PX, PY-2) | ship(PX-1, PY-2) | ship(PX+1, PY-2) | ship(PX-1, PY-1).
+?findStriker(PX, PY, "E", 2)    : ship(PX+2, PY) | ship(PX+2, PY-1) | ship(PX+2, PY+1) | ship(PX+2, PY-2) | ship(PX+2, PY+2) | ship(PX+1, PY-1).
+?findStriker(PX, PY, "S", 2)    : ship(PX, PY+2) | ship(PX-1, PY+2) | ship(PX+1, PY+2) | ship(PX+1, PY+1).
+?findStriker(PX, PY, "W", 2)    : ship(PX-2, PY) | ship(PX-2, PY-1) | ship(PX-2, PY+1) | ship(PX-2, PY-2) | ship(PX-2, PY+2) | ship(PX-1, PY+1).
+?findStriker(_, _, O, 0)        : orientation(O).

