!start.
//------------------------------------------------------------------------------
+!start
    <- !buildTestName("", NAME); +team(NAME);
       .send(stadium, tell, name(NAME));
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
