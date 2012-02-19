!start.
//------------------------------------------------------------------------------
+!start
     : .my_name(NAME)
    <- !buildTestName("", TEAM);
       +agent(TEAM); +agent(NAME); +~sameAs(TEAM, NAME);
       +team(TEAM);
       .send(stadium, tell, name(TEAM));
       !!deliberation.
//------------------------------------------------------------------------------
+!deliberation
    <- ?appraisal; ok; !!deliberation.
//------------------------------------------------------------------------------
+!buildTestName("", NAME)
  <- !buildName(N);
     !buildTestName(N, NAME).

+!buildTestName(NAME, NAME).
//------------------------------------------------------------------------------
