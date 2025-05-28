!start.
test(ok).
test(a,b,c,d).
test(a,d,c,b).
test(a,b,b,b).
g.
h.
w.
z.
u.
v.

i :- w & z.
e :- g | h | i.
d :- g & h.



+!start : true <-
    example.priorityFunction([X, poss(X)]).
    !g1.

+!g1 : true <-
    .print("Im in g1 rn");
    +g2;
    !g2;
    .print("end of g1").

+!g2 : X & X \== g  & X \== h <-
    .print("Im in g2 poss(x) rn");
    !g3.

+!g2 : test(ok) <-
    .print("Im in g2 test(ok) rn");
    !g3.

+!g2 : test(X) <-
    .print("Im in g2 rn");
    !g3.

+!g2 : Y <-
    .print("Im in g2 rn");
    !g3.

+!g2 : X | e <-
    .print("Im in g2 rn");
    !g3.

+!g2 : u | v <-
    .print("Im in g2 rn");
    !g3.

+!g2 : test(a, X, c, Y) <-
    .print("Im in g2 test(ok) rn");
    !g3.

+!g2 : test(a, X, X, X) <-
    .print("Im in g2 rn");
    !g3.

+!g2 : test(a,b,c,d) <-
    .print("Im in g2 test(ok) rn");
    !g3.

+!g2 : g | h <-
    .print("Im in g2 rn");
    !g3.

+!g2 : g <-
    .print("Im in g2 a rn");
    !g3.

+!g2 : h <-
    .print("Im in g2  b rn");
    !g3.

+!g2 : e <-
    .print("Im in g2 e rn");
    !g3.

+!g2 : d <-
    .print("Im in g2 rn");
    !g3.

+!g3 <-
    .print("Im in g3 rn");
    .wait(2000);
    .print("end of g3").

