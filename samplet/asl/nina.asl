
//initial believes.
//-----------------------------------------------------------------------------
!start.
+!start <- iam(agent); !!reactive.
//-----------------------------------------------------------------------------
+!reactive
     : step(X) //& .random(Y) & K=math.round(Y*10000)
    <- //?myself(_,_,_)[resource(P)];
       nope;
       !!reactive.
-!reactive
    <- .println("reactive plan failed! Doing nope..."); nope;
       !!reactive.
//-----------------------------------------------------------------------------
