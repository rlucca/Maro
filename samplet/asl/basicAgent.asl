//initial believes.
//-----------------------------------------------------------------------------
//!start.
//+!start : .my_name(K) <- .println("my name is ", K).
//+!start <- iam(agent); !!reactive.
//-----------------------------------------------------------------------------
/*+!reactive
     : step(X) //& .random(Y) & K=math.round(Y*10000)
    <- //?myself(_,_,_)[resource(P)];
       nope;
       !!reactive.
-!reactive
    <- .println("reactive plan failed! Doing nope..."); nope;
       !!reactive.*/
//-----------------------------------------------------------------------------
+X <- .println("add/? ", X); !!foo.
-X <- .println("del/? ", X); !!foo.

+!foo <- !printPlaceView;
         !printItemView;
         foo.

+!printPlaceView
    <- sims.ia.getPlaces(K);
       !printPlace(K).

+!printPlace([]).
+!printPlace([H|R])
    <- !printPlace(R);
       sims.ia.getItemsAtPlace(H, K);
       .println(H,": ", K).

+!printItemView
    <- sims.ia.getItems(K);
       !printItem(K).

+!printItem([]).
+!printItem([H|R])
    <- !printItem(R);
       sims.ia.getPlacesByItem(H, K);
       .println(H, ": ", K).

