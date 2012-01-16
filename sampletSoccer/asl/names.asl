
+!buildName(R)
    <- CS= ["q", "w", "y", "p", "s", "d", "f", "g", "h", "j",
            "k", "l", "z", "x", "c", "v", "b", "n", "m"];
       CM= ["r", "t"];
        V= ["a", "e", "i", "o", "u"];
       .random(RNV); RV=math.round(RNV*100);
       !buildName(CS, CM, V, RV, 0, R).

+!buildName(_, _, _, _, DEPTH, "")
     : DEPTH >= 4
    <- true.

+!buildName(_, _, _, RV, _, "")
     : RV >= 90
    <- true.

+!buildName(CS, CM, V, RV, DEPTH, RESULT)
     : RV >= 40
    <- .random(RNV); NRV=math.round(RNV*100);
       !buildName(CS, CM, V, NRV, DEPTH + 1, STR);
       .random(NVC); .random(NVV);
       .length(CS, VVC); .length(V, VVV);
       RVC=math.round(NVC*(VVC-1));
       RVV=math.round(NVV*(VVV-1));
       .nth(RVC, CS, CVC);
       .nth(RVV,  V, CVV);
       .concat(STR, CVC, CVV, RESULT).

+!buildName(CS, CM, V, _, DEPTH, RESULT)
    <- .random(RNV); NRV=math.round(RNV*100);
       !buildName(CS, CM, V, NRV, DEPTH + 1, STR);
       .random(NVM);
       .length(CM, VVM);
       RVM=math.round(NVM*(VVM-1));
       .nth(RVM, CM, CVM);
       .concat(STR, CVM, RESULT).
