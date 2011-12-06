
+!gui(orientation, N)
    <- .print("changing orientation to ",N);
        changeOrientationTo(N).

+!gui(special, "0") <- .print("teleport myself"); teleport(0).
+!gui(special, "1") <- .print("teleport another"); teleport(1).
+!gui(special, N) <- .print("absorve"); absorve.

+!gui(dropout) <- .print("drop out"); offer(2).
+!gui(dropin) <- .print("drop in"); offer(-2).

+!gui(ACTION)
    <- .print(ACTION); ACTION.

