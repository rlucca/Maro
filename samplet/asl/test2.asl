
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
   <- +remove; -agent("darkstar")[like(50),source(self)]; !!start. // isso funciona e remove so o like
//   <-   +remove; -agent("darkstar"); !!start. // nao funciona e nao gera evento, use o debaixo...
//    <- +remove; .abolish(agent("darkstar")); !!start. // funciona
// nota isso nao foi testado para as relacoes, mas a implementacao foi 'largada' la.

+!start
    :  not(laka) & not(agent("darkstar"))
    <- +laka; .println("darkstar removed!"); !!start.

+!start
    :  not(kaka)
    <- +kaka; +hasThresholdType("maka", "thresholdFear"); !!start.

+!start
    :  not(maka)
    <- +maka; .abolish(hasThresholdType(_,_)); !!start.

+!start
    :  not(taku) & not(hasThresholdType(_,_))
    <- +taku; .println("test of removed hasThresholdType ok"); !!start.

+!start
    <- .println("waiting..."); .wait(5000); .stopMAS.

+agent(X)
    <- .findall(Y, agent(X)[Y], L);
       .println(X, ": ", L).
-agent(X)[Y]
    <- .println(X, "- ", Y).
