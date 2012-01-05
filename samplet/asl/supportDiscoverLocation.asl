//-----------------------------------------------------------------------------
+!calculatePosition([])
     : .findall(L, room(L), []).
//-----------------------------------------------------------------------------
+!calculatePosition([])
    <- .findall(L, room(_)[likelihood(L)], RRL);
       .max(RRL, AR);
       .findall(R, room(R)[likelihood(AR)], RR);
       .abolish(room(_));
       .nth(0, RR, VAL);
       +room(VAL).
//-----------------------------------------------------------------------------
+!calculatePosition([H|R])
    <- !calculatePositionByItems(H);
       !calculatePosition(R).
//-----------------------------------------------------------------------------
+!calculatePositionByItems([]).
//-----------------------------------------------------------------------------
+!calculatePositionByItems([H|R])
    <- sims.ia.getPlacesByItem(H, Places);
       !computatePlaces(Places);
       !calculatePositionByItems(R).
//-----------------------------------------------------------------------------
+!computatePlaces([]).
//-----------------------------------------------------------------------------
+!computatePlaces([H|R])
     : room(H)[likelihood(N)]
    <- +room(H)[likelihood(N+1)];
       !computatePlaces(R).
//-----------------------------------------------------------------------------
+!computatePlaces([H|R])
    <- +room(H)[likelihood(1)];
       !computatePlaces(R).
//-----------------------------------------------------------------------------
