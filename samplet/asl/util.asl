beforeGo(5). // initial value of steps before go to a fixed place
//-----------------------------------------------------------------------------
myNth(POS, ARRAY, OUT) :- index(ARRAY, POS, 0, OUT).
//-----------------------------------------------------------------------------
index([], _, _, _) :- .fail.
//-----------------------------------------------------------------------------
index([H|R], IDX, IDX, H).
//-----------------------------------------------------------------------------
index([H|R], IDX, POS, OUT) :- index(R, IDX, POS+1, OUT).
//-----------------------------------------------------------------------------
+?updateBeforeGo(STEPINITIAL, STEPFINAL, true)
     : DIFF=(STEPFINAL-STEPINITIAL) & beforeGo(DIFF).
//-----------------------------------------------------------------------------
+?updateBeforeGo(STEPINITIAL, STEPFINAL, true)
     : DIFF=(STEPFINAL-STEPINITIAL) & beforeGo(VAL)
    <- NEWVAL=VAL-(DIFF div 2); -beforeGo(VAL); +beforeGo(NEWVAL).
//-----------------------------------------------------------------------------
+?updateBeforeGo(_, _, false)
    <- -beforeGo(VAL); +beforeGo(VAL*VAL).
//-----------------------------------------------------------------------------
+?updateBeforeGo(STEPINITIAL, STEPFINAL, REACH).
