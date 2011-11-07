fullLife(100).
life(40).
lowLife(25). // 25% de 100


!start.

+!start <- iam(ship).

+step(X)
    : life(0)
    <- .println("death not implement"); nope. // death

+step(X)
    : onPlanet & life(L) & fullLife(FL) & L < FL
   <- .println("recover not implement"); nope.

+step(X)
    :  not(qtyPlanets(0)) & lowLife(LL) & life(L) & L < LL
   <- .println("fuga not implement"); nope.

+step(X)
    : qtyShips(S) & S > 1 & .random(Y) & K=math.round(Y*10000) & K < 10
   <- .println("fuga not implement"); nope.

+step(X)
    : qtyShips(S) & S > 0
   <- .println("ataca mais proxima not implement"); nope.

+step(X)
    : true
   <- .println("escolher uma posicao dentro do alcance de 5 ladrinhos contando o meio"); nope.

