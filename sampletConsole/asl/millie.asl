step(0)[source(percept), source(self)].

//-----------------------------------------------------------------------------
agent(millie).

high(lhigh).
mid(lmid).
low(llow).
none(lnone).

hasSetup(millie, setup1).
hasSetup(millie, setup2).
hasSetup(millie, setup3).
hasSetup(millie, setup4).
hasSetup(millie, setup5).
hasSetup(millie, setup6).
hasSetup(millie, setup7).
hasSetup(millie, setup8).
hasSetup(millie, setup9).
hasSetup(millie, setup10).
hasSetup(millie, setup11).
hasSetup(millie, setup12).
hasSetup(millie, setup13).
hasSetup(millie, setup14).
hasSetup(millie, setup15).
hasSetup(millie, setup16).
hasSetup(millie, setup17).
hasSetup(millie, setup18).
hasSetup(millie, setup19).
hasSetup(millie, setup20).
hasSetup(millie, setup21).
hasSetup(millie, setup22).

hasThreshold(setup1, 0). // threshold is zero to be valence equivalente to potence
hasThresholdType(setup1, "Joy").
hasThreshold(setup2, 0).
hasThresholdType(setup2, "Distress").
hasThreshold(setup3, 0).
hasThresholdType(setup3, "Hope").
hasThreshold(setup4, 0).
hasThresholdType(setup4, "Fear").
hasThreshold(setup5, 0).
hasThresholdType(setup5, "FearsConfirmed").
hasThreshold(setup6, 0).
hasThresholdType(setup6, "Satisfaction").
hasThreshold(setup7, 0).
hasThresholdType(setup7, "Relief").
hasThreshold(setup8, 0).
hasThresholdType(setup8, "Disappointment").
hasThreshold(setup9, 0).
hasThresholdType(setup9, "HappyFor").
hasThreshold(setup10, 0).
hasThresholdType(setup10, "SorryFor").
hasThreshold(setup11, 0).
hasThresholdType(setup11, "Gloating").
hasThreshold(setup12, 0).
hasThresholdType(setup12, "Resentment").
hasThreshold(setup13, 0).
hasThresholdType(setup13, "Love").
hasThreshold(setup14, 0).
hasThresholdType(setup14, "Hate").
hasThreshold(setup15, 0).
hasThresholdType(setup15, "Admiration").
hasThreshold(setup16, 0).
hasThresholdType(setup16, "Pride").
hasThreshold(setup17, 0).
hasThresholdType(setup17, "Shame").
hasThreshold(setup18, 0).
hasThresholdType(setup18, "Reproach").
hasThreshold(setup19, 0).
hasThresholdType(setup19, "Gratitude").
hasThreshold(setup20, 0).
hasThresholdType(setup20, "Gratification").
hasThreshold(setup21, 0).
hasThresholdType(setup21, "Anger").
hasThreshold(setup22, 0).
hasThresholdType(setup22, "Remorse").
//-----------------------------------------------------------------------------

!start.
+!start
    <- .at("now +1 s", {+!step}).

+!step
     : step(X)
    <- .abolish(step(_)); +step(X + 1)[source(percept), source(self)];
       .println("new step ",X+1);
       .at("now +1 s", {+!step}).

-!X <- .println(X).
