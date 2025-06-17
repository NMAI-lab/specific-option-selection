!start.

pred(a,b,c).
d.
e :- pred(a,Y,X) & d.

+!start <-
    !goal.

+!goal : pred(a,X,Y) <-
    .print("poss(a) option").

+!goal : e <-
    .print("poss(a) option").

