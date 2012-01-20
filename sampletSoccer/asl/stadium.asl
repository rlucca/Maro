!start.
//------------------------------------------------------------------------------
+!start
    : step(N) & timeR(P1, N1) & timeL(P2,N2)
    <- !step(N); ok; maro.example.soccer.ia.addPerception(points(N1,P1,N2,P2));
       !!start.
+!start
    <- !!start.
//------------------------------------------------------------------------------
+!step(0).
+!step(1)
    <- .print("inicia a partida!").
+!step(33)
     : timeL(C1, N1) & timeR(C2, N2)
    <- .print("intervalo");
       -timeL(C1, N1); -timeR(C2, N2);
       +timeL(C2, N2); +timeR(C1, N1);
       -timeAL(_); -timeAR(_).
+!step(34)
    <- .print("recomeca a partida!").
+!step(66)
     : timeL(C1, N1) & timeR(C2, N2)
    <- .print("final de partida.");
       !sendWinner.
//------------------------------------------------------------------------------
+!step(X) : X > 66.

+!step(X)
     : timeL(C1, N1) & timeR(C2, N2) & timeAL(C1) & timeAR(C2)
    <- .random(GOAL); G=math.round(GOAL*3000);
       !goal(G).

+!step(X)
     : timeL(C1, N1) & timeR(C2, N2) 
    <- .random(GOAL); G=math.round(GOAL*3000);
       !goal(G);
       .print(N1, " ", C1, " X ", C2, " ", N2);
       +timeAL(C1); +timeAR(C2).
//------------------------------------------------------------------------------
+!goal(G) : G >= 100.
+!goal(G) : G >= 50 & timeL(C1, NAME1)
    <- -timeL(C1, NAME1); +timeL(C1 + 1, NAME1);
       maro.example.soccer.ia.addPerception(goal(NAME1)).

+!goal(G) : G >= 0 & timeR(C2, NAME2)
    <- -timeR(C2, NAME2); +timeR(C2 + 1, NAME2);
       maro.example.soccer.ia.addPerception(goal(NAME2)).
//------------------------------------------------------------------------------
+name(NAME) : timeL(_, _) & timeR(_, _).
+name(NAME) : timeL(_,_) <- +timeR(0, NAME).
+name(NAME) : timeR(_,_) <- +timeL(0, NAME).
+name(NAME) <- +timeL(0, NAME).
//------------------------------------------------------------------------------
+!sendWinner
     : timeL(DRAW, N1) & timeR(DRAW, N2)
    <- maro.example.soccer.ia.addPerception(match(draw,"")).
//------------------------------------------------------------------------------
+!sendWinner
     : timeL(C1, N1) & timeR(C2, N2) & C1 > C2
    <- maro.example.soccer.ia.addPerception(match(win, N1));
       maro.example.soccer.ia.addPerception(match(lose, N2)).
//------------------------------------------------------------------------------
+!sendWinner
     : timeL(C1, N1) & timeR(C2, N2) & C1 < C2
    <- maro.example.soccer.ia.addPerception(match(win,N2));
       maro.example.soccer.ia.addPerception(match(lose,N1)).
