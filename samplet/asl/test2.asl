
!start.

+!start
    : not(ok) // se nao tiver
   <- +ok; .wait(1000); iam(ship); !!start.

+!start
    : not(treco)
   <- +treco; +agent("darkstar"); !!start.

+!start
    : not(coisa)
   <- +coisa; +agent("darkstar")[like(50)]; !!start.

+!start
    : not(remove)
   <- +remove; -agent("darkstar")[like(50)]; !!start. // isso funciona e remove so o like
//   <-   +remove; -agent("darkstar"); !!start. // nao funciona e nao gera evento, use o debaixo...
//    <- +remove; .abolish(agent("darkstar")); !!start. // funciona
// nota isso nao foi testado para as relacoes, mas a implementacao foi 'largada' la.

+!start
    <- .println("waiting..."); .wait(5000); .stopMAS.

+agent(X)[Y]
    <- .println(X, ": ", Y).
-agent(X)[Y]
    <- .println(X, "- ", Y).
