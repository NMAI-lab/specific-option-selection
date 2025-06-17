!start.

a.
poss(b).
test(a,a).
test(a).
test(b,d).
poss(test(a,a)).
poss(test(a)).

+!start <-
    !goal.

+!goal : test(b,X) <-
    .print("X option").

+!goal : test(D,X) | poss(test(X)) <-
    .print("X option").

+!goal : test(X) & X \== b <-
    .print("X option").

+!goal : test(X) <-
    .print("X option").

+!goal : poss(test(X)) <-
    .print("poss(a) option").

+!goal : test(D,X) <-
    .print("poss(a) option").

+!goal : a <-
    .print("a option").

+!goal : X <-
    .print("a option").

+!goal : poss(X) <-
    .print("a option").

