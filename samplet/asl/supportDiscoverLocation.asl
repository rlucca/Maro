{ include("routes.asl") }
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
+!planRoute(PREFIX, SOURCE, ROUTE)
    <- sims.ia.getItems(ITEMS);
       !filterListByPrefix(PREFIX, ITEMS, ITEMSFILTERED);
       !planRouteByItem(ITEMSFILTERED, SOURCE, ROUTE).
       //.println("Route found to all items: ", ROUTE, " LK").
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
+!planRouteByItem([ITEM|ITEMS], SOURCE, [route(ITEM,RL,R)|ROUTE])
    <- !planRouteByItem(ITEMS, SOURCE, ROUTE);
       sims.ia.getPlacesByItem(ITEM, [TARGET]);
       ?routeTo(SOURCE, TARGET, R);
       .length(R, RL).
//-----------------------------------------------------------------------------
