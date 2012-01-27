{ include("routes.asl") }
//-----------------------------------------------------------------------------
+!calculatePosition([])
     : .findall(L, roomP(L), []).
//-----------------------------------------------------------------------------
+!calculatePosition([])
    <- .findall(L, roomP(_)[likelihood(L)], RRL);
       .max(RRL, AR);
       .findall(R, roomP(R)[likelihood(AR)], RR);
       .abolish(roomP(_));
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
     : roomP(H)[likelihood(N)]
    <- +roomP(H)[likelihood(N+1)];
       !computatePlaces(R).
//-----------------------------------------------------------------------------
+!computatePlaces([H|R])
    <- +roomP(H)[likelihood(1)];
       !computatePlaces(R).
//-----------------------------------------------------------------------------
+!planRoute(PREFIX, SOURCE, ROUTE)
    <- sims.ia.getItems(ITEMS);
       !filterListByPrefix(PREFIX, ITEMS, ITEMSFILTERED);
       //.println("internal prefix ", PREFIX, " filtered: ", ITEMSFILTERED);
       !planRouteByItem(ITEMSFILTERED, SOURCE, ROUTE).
//-----------------------------------------------------------------------------
+!filterListByPrefix(PrefixName, [], []).
//-----------------------------------------------------------------------------
+!filterListByPrefix(PrefixName, [Item|Items], NewItemsFiltered)
     : .substring(PrefixName, Item, 0)
    <- !filterListByPrefix(PrefixName, Items, ItemsFiltered);
       .concat([Item], ItemsFiltered, NewItemsFiltered).
//-----------------------------------------------------------------------------
+!filterListByPrefix(PrefixName, [Item|Items], ItemsFiltered)
    <- !filterListByPrefix(PrefixName, Items, ItemsFiltered).
//-----------------------------------------------------------------------------
+!planRouteByItem([], _, []).
//-----------------------------------------------------------------------------
+!planRouteByItem([ITEM|ITEMS], SOURCE, ROUTE)
     : ~rest(ITEM) // exist the information that I cannot rest on item
    <- !planRouteByItem(ITEMS, SOURCE, ROUTE).
//-----------------------------------------------------------------------------
+!planRouteByItem([ITEM|ITEMS], SOURCE, [route(ITEM,RL,R)|ROUTE])
    <- !planRouteByItem(ITEMS, SOURCE, ROUTE);
       sims.ia.getPlacesByItem(ITEM, [TARGET]);
       ?routeCompleteTo(SOURCE, TARGET, R);
       //.println("internal from ", SOURCE, " to ", TARGET, " = ", R);
       .length(R, RL).
//-----------------------------------------------------------------------------
+!nearRouteOf([], L, R, R) : .ground(L).
//-----------------------------------------------------------------------------
+!nearRouteOf([H|R], LESSER, _, ROUTE)
     : H =.. [route, [_, LESSER, _], _]
    <- !nearRouteOf(R, LESSER, H, ROUTE).
//-----------------------------------------------------------------------------
+!nearRouteOf([H|R], LESSER, _, ROUTE)
     : H =.. [route, [_, DISTANCE, _], _] & DISTANCE < LESSER
    <- !nearRouteOf(R, DISTANCE, H, ROUTE).
//-----------------------------------------------------------------------------
+!nearRouteOf([H|R], LESSER, K, ROUTE)
     : H =.. [route, [_, DISTANCE, _], _]
    <- !nearRouteOf(R, LESSER, K, ROUTE).
//-----------------------------------------------------------------------------
