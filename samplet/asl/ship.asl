fullLife(100).
life(40).
lowLife(25). // 25% de 100


!start.

+!start <- iam(ship).

@step1[atomic]
+step(X)
    : life(0)
    <- death.

@step2[atomic]
+step(X)
    : onPlanet & life(L) & fullLife(FL) & L < FL
   <- nope.

@step3[atomic]
+step(X)
    :  not(qtyPlanets(0)) & planet(PX,PY) & lowLife(LL) & life(L) & L < LL
   <- !orientationTo(PX, PY, NO);
      changeOrientationTo(NO);
      forward.

@step4[atomic]
+step(X)
    : qtyShips(S) & S > 1 & .random(Y) & K=math.round(Y*10000) & K < 10 & position(PX,PY)
   <- ?findStriker(PX, PY, O, D);
      ?orientation(OO);
      !goAway(D, OO, O).

@step5[atomic]
+step(X)
    : qtyShips(S) & S > 0 & position(PX, PY)
   <- ?findStriker(PX, PY, O, D);
      ?orientation(OO);
      !attack(D, OO, O).

@step6[atomic]
+step(X)
    : .random(Y) & K=math.round(Y*100)
   <- !newOrientation(K, L);
      changeOrientationTo(L);
      forward.


@ot1[atomic]
+!orientationTo(X,Y, "E")
    : position(PX,PY) & PX > X & PY > Y.
@ot2[atomic]
+!orientationTo(X,Y, "E")
    : position(PX,PY) & PX > X & PY < Y.
@ot3[atomic]
+!orientationTo(X,Y, "E")
    : position(PX,Y) & PX > X.
@ot4[atomic]
+!orientationTo(X,Y, "W")
    : position(PX,PY) & PX < X & PY > Y.
@ot5[atomic]
+!orientationTo(X,Y, "W")
    : position(PX,PY) & PX < X & PY < Y.
@ot6[atomic]
+!orientationTo(X,Y, "W")
    : position(PX,Y) & PX < X.
@ot7[atomic]
+!orientationTo(X,Y, "S")
    : position(X,PY) & PY > Y.
@ot8[atomic]
+!orientationTo(X,Y, "N")
    : position(X,PY) & PY < Y.
// Se acontecer a outra regra de step antes pega...
@ot9[atomic]
+!orientationTo(X,Y, "N")
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
    <- .println("avancar!");
       forward.
+!attack(D, Orientation, Orientation)
    <- .println("fogo!");
       fire.
+!attack(_, OldOrientation, NewOrientation)
    <- .println("ataca virando de ", OldOrientation, " para ", NewOrientation);
       changeOrientationTo(NewOrientation).


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

@fire1[atomic]
+!fire
     : life(X) & .random(Y) & K=X-math.round(Y*3) & K > 0
    <- -life(X); +life(K).
@fire2[atomic]
+!fire
     : life(X) & K=X-1 & K > 0
    <- -life(X); +life(K).
@fire3[atomic]
+!fire
     : life(X)
    <- -life(X); +life(0).
