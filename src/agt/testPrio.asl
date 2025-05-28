!start.

a.
pre(b).
test(a,a).
test(a).
test(b,d).
pre(test(a,a)).
pre(test(a)).

+!start <-
    !goal.

+!goal : test(b,X) <-
    .print("X option").

+!goal : test(D,X) <-
    .print("X option").

+!goal : test(X) <-
    .print("X option").

+!goal : pre(test(X)) <-
    .print("poss(a) option").

+!goal : pre(test(D,X)) <-
    .print("poss(a) option").

+!goal : a <-
    .print("a option").

+!goal : X <-
    .print("a option").

+!goal : pre(X) <-
    .print("a option").

