anotherSource([], _, []).
anotherSource([S|R], S, FOUND) :- anotherSource(R, S, FOUND).
anotherSource([H|R], S, [H]).

routeCompleteTo(SOURCE, SOURCE, []).
routeCompleteTo(SOURCE, TARGET, [WAY|FOUND])
    :- routeTo(SOURCE, TARGET, ROUTE)
     & .random(N) & NUM=math.round(N*100000)
     & .length(ROUTE, LENGTH)
     & .nth(NUM mod LENGTH, ROUTE, WAY)
     & sims.ia.getPlacesByItem(WAY, LIST)
     & anotherSource(LIST, SOURCE, [NEWSOURCE])
     & routeCompleteTo(NEWSOURCE, TARGET, FOUND).

isAdjacent(NEXTTO, "W", MX, MY, X, Y, SX, SY) // right
    :- Y <= MY & (Y+SY) >= MY & ((MX = (X+SX+1) & NEXTTO=true) | (MX > (X+SX) & NEXTTO=false)).
isAdjacent(NEXTTO, "E", MX, MY, X, Y, SX, SY) // left
    :- Y <= MY & (Y+SY) >= MY & ((MX = (X-1) & NEXTTO=true) | (MX < X & NEXTTO=false)).
isAdjacent(NEXTTO, "N", MX, MY, X, Y, SX, SY) // down
    :- X <= MX & (X+SX) >= MX & ((MY = (Y+SY+1) & NEXTTO=true) | (MY > (Y+SY) & NEXTTO=false)).
isAdjacent(NEXTTO, "S", MX, MY, X, Y, SX, SY) // upper
    :- X <= MX & (X+SX) >= MX & ((MY = (Y-1) & NEXTTO=true) | (MY < Y & NEXTTO=false)).

fixMeOrientation(OBJECT, NEWO, NEXTTO)
    :- object(OBJECT)[positionX(X), positionY(Y), sizeX(SX), sizeY(SY)]
     & myself[positionX(MX), positionY(MY)]
     // size of 1 need be zero
     & isAdjacent(NEXTTO, NEWO, MX, MY, X, Y, SX-1, SY-1).


//LATER: put all route to reach the target.
routeTo("abstractHouseCorredor","abstractHouseCorredor",       []).
routeTo("abstractHouseCorredor","abstractHouseKitchen",        ["doorToCorredorOrToLivingRoom"]).
routeTo("abstractHouseCorredor","abstractHouseLivingRoom",     ["doorToCorredorOrToLivingRoom"]).
routeTo("abstractHouseCorredor","abstractHouseSocialBathroom", ["doorToCorredorOrToLivingRoom"]).
routeTo("abstractHouseCorredor","abstractHouseRoom2",          ["doorToCorredorToRoom2"]).
routeTo("abstractHouseCorredor","abstractHouseSuiteBathroom",  ["doorToCorredorToRoom2"]).
routeTo("abstractHouseCorredor","abstractHouseDeposit",        ["doorToDepositOrToCorredor"]).
routeTo("abstractHouseCorredor","abstractHouseBathroomMiddle", ["doorRightBathroomMiddle"]).
routeTo("abstractHouseCorredor","abstractHouseRoom1",          ["doorToCorredorToRoom1","doorRightBathroomMiddle"]).

routeTo("abstractHouseBathroomMiddle","abstractHouseBathroomMiddle", []).
routeTo("abstractHouseBathroomMiddle","abstractHouseCorredor",       ["doorRightBathroomMiddle"]).
routeTo("abstractHouseBathroomMiddle","abstractHouseDeposit",        ["doorRightBathroomMiddle"]).
routeTo("abstractHouseBathroomMiddle","abstractHouseKitchen",        ["doorRightBathroomMiddle"]).
routeTo("abstractHouseBathroomMiddle","abstractHouseLivingRoom",     ["doorRightBathroomMiddle"]).
routeTo("abstractHouseBathroomMiddle","abstractHouseRoom2",          ["doorRightBathroomMiddle"]).
routeTo("abstractHouseBathroomMiddle","abstractHouseSocialBathroom", ["doorRightBathroomMiddle"]).
routeTo("abstractHouseBathroomMiddle","abstractHouseSuiteBathroom",  ["doorRightBathroomMiddle"]).
routeTo("abstractHouseBathroomMiddle","abstractHouseRoom1",          ["doorToBathRoomMiddleToRoom1"]).

routeTo("abstractHouseDeposit","abstractHouseDeposit",        []).
routeTo("abstractHouseDeposit","abstractHouseBathroomMiddle", ["doorToDepositOrToCorredor"]).
routeTo("abstractHouseDeposit","abstractHouseCorredor",       ["doorToDepositOrToCorredor"]).
routeTo("abstractHouseDeposit","abstractHouseKitchen",        ["doorToDepositOrToCorredor"]).
routeTo("abstractHouseDeposit","abstractHouseLivingRoom",     ["doorToDepositOrToCorredor"]).
routeTo("abstractHouseDeposit","abstractHouseRoom1",          ["doorToDepositOrToCorredor"]).
routeTo("abstractHouseDeposit","abstractHouseRoom2",          ["doorToDepositOrToCorredor"]).
routeTo("abstractHouseDeposit","abstractHouseSocialBathroom", ["doorToDepositOrToCorredor"]).
routeTo("abstractHouseDeposit","abstractHouseSuiteBathroom",  ["doorToDepositOrToCorredor"]).

routeTo("abstractHouseKitchen","abstractHouseKitchen",        []).
routeTo("abstractHouseKitchen","abstractHouseBathroomMiddle", ["doorToKitchenToLivingRoom"]).
routeTo("abstractHouseKitchen","abstractHouseCorredor",       ["doorToKitchenToLivingRoom"]).
routeTo("abstractHouseKitchen","abstractHouseDeposit",        ["doorToKitchenToLivingRoom"]).
routeTo("abstractHouseKitchen","abstractHouseLivingRoom",     ["doorToKitchenToLivingRoom"]).
routeTo("abstractHouseKitchen","abstractHouseRoom1",          ["doorToKitchenToLivingRoom"]).
routeTo("abstractHouseKitchen","abstractHouseRoom2",          ["doorToKitchenToLivingRoom"]).
routeTo("abstractHouseKitchen","abstractHouseSocialBathroom", ["doorToKitchenToLivingRoom"]).
routeTo("abstractHouseKitchen","abstractHouseSuiteBathroom",  ["doorToKitchenToLivingRoom"]).

routeTo("abstractHouseLivingRoom","abstractHouseLivingRoom",     []).
routeTo("abstractHouseLivingRoom","abstractHouseBathroomMiddle", ["doorToCorredorOrToLivingRoom"]).
routeTo("abstractHouseLivingRoom","abstractHouseCorredor",       ["doorToCorredorOrToLivingRoom"]).
routeTo("abstractHouseLivingRoom","abstractHouseDeposit",        ["doorToCorredorOrToLivingRoom"]).
routeTo("abstractHouseLivingRoom","abstractHouseKitchen",        ["doorToKitchenToLivingRoom"]).
routeTo("abstractHouseLivingRoom","abstractHouseRoom1",          ["doorToCorredorOrToLivingRoom"]).
routeTo("abstractHouseLivingRoom","abstractHouseRoom2",          ["doorToCorredorOrToLivingRoom"]).
routeTo("abstractHouseLivingRoom","abstractHouseSocialBathroom", ["doorToSocialBathRoomToLivingRoom"]).
routeTo("abstractHouseLivingRoom","abstractHouseSuiteBathroom",  ["doorToCorredorOrToLivingRoom"]).

routeTo("abstractHouseRoom1","abstractHouseRoom1",          []).
routeTo("abstractHouseRoom1","abstractHouseBathroomMiddle", ["doorToBathRoomMiddleToRoom1"]).
routeTo("abstractHouseRoom1","abstractHouseCorredor",       ["doorToCorredorToRoom1", "doorRightBathroomMiddle"]).
routeTo("abstractHouseRoom1","abstractHouseDeposit",        ["doorToCorredorToRoom1"]).
routeTo("abstractHouseRoom1","abstractHouseKitchen",        ["doorToCorredorToRoom1"]).
routeTo("abstractHouseRoom1","abstractHouseLivingRoom",     ["doorToCorredorToRoom1"]).
routeTo("abstractHouseRoom1","abstractHouseRoom2",          ["doorToCorredorToRoom1"]).
routeTo("abstractHouseRoom1","abstractHouseSocialBathroom", ["doorToCorredorToRoom1"]).
routeTo("abstractHouseRoom1","abstractHouseSuiteBathroom",  ["doorToCorredorToRoom1"]).

routeTo("abstractHouseRoom2","abstractHouseRoom2",          []).
routeTo("abstractHouseRoom2","abstractHouseBathroomMiddle", ["doorToCorredorToRoom2"]).
routeTo("abstractHouseRoom2","abstractHouseCorredor",       ["doorToCorredorToRoom2"]).
routeTo("abstractHouseRoom2","abstractHouseDeposit",        ["doorToCorredorToRoom2"]).
routeTo("abstractHouseRoom2","abstractHouseKitchen",        ["doorToCorredorToRoom2"]).
routeTo("abstractHouseRoom2","abstractHouseLivingRoom",     ["doorToCorredorToRoom2"]).
routeTo("abstractHouseRoom2","abstractHouseRoom1",          ["doorToCorredorToRoom2"]).
routeTo("abstractHouseRoom2","abstractHouseSocialBathroom", ["doorToCorredorToRoom2"]).
routeTo("abstractHouseRoom2","abstractHouseSuiteBathroom",  ["doorToRoom2ToBathroom"]).

routeTo("abstractHouseSocialBathroom","abstractHouseSocialBathroom", []).
routeTo("abstractHouseSocialBathroom","abstractHouseBathroomMiddle", ["doorToSocialBathRoomToLivingRoom"]).
routeTo("abstractHouseSocialBathroom","abstractHouseCorredor",       ["doorToSocialBathRoomToLivingRoom"]).
routeTo("abstractHouseSocialBathroom","abstractHouseDeposit",        ["doorToSocialBathRoomToLivingRoom"]).
routeTo("abstractHouseSocialBathroom","abstractHouseKitchen",        ["doorToSocialBathRoomToLivingRoom"]).
routeTo("abstractHouseSocialBathroom","abstractHouseLivingRoom",     ["doorToSocialBathRoomToLivingRoom"]).
routeTo("abstractHouseSocialBathroom","abstractHouseRoom1",          ["doorToSocialBathRoomToLivingRoom"]).
routeTo("abstractHouseSocialBathroom","abstractHouseRoom2",          ["doorToSocialBathRoomToLivingRoom"]).
routeTo("abstractHouseSocialBathroom","abstractHouseSuiteBathroom",  ["doorToSocialBathRoomToLivingRoom"]).

routeTo("abstractHouseSuiteBathroom","abstractHouseSuiteBathroom",   []).
routeTo("abstractHouseSuiteBathroom","abstractHouseBathroomMiddle",  ["doorToRoom2ToBathroom"]).
routeTo("abstractHouseSuiteBathroom","abstractHouseCorredor",        ["doorToRoom2ToBathroom"]).
routeTo("abstractHouseSuiteBathroom","abstractHouseDeposit",         ["doorToRoom2ToBathroom"]).
routeTo("abstractHouseSuiteBathroom","abstractHouseKitchen",         ["doorToRoom2ToBathroom"]).
routeTo("abstractHouseSuiteBathroom","abstractHouseLivingRoom",      ["doorToRoom2ToBathroom"]).
routeTo("abstractHouseSuiteBathroom","abstractHouseRoom1",           ["doorToRoom2ToBathroom"]).
routeTo("abstractHouseSuiteBathroom","abstractHouseRoom2",           ["doorToRoom2ToBathroom"]).
routeTo("abstractHouseSuiteBathroom","abstractHouseSocialBathroom",  ["doorToRoom2ToBathroom"]).

//TODO: necessary when the agents go out of home...
//"abstractHome"    
//"abstractArcade"
//"abstractMarket"
//"abstractSchool" F
//"abstractWork"   F

