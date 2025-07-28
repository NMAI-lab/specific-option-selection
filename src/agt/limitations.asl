check1.
check2.


ok.

test(a).
dir(a).
obs(a).
has(a).
test(b).
dir(b).
obs(b).
has(b).
test(c).
dir(c).
obs(c).
has(c).

went(a).
went(b).
went(c).
went(d).
went(e).
went(f).
went(g).
went(h).
went(i).
went(j).
went(k).
went(l).
went(m).
went(n).
went(o).

test(d).
dir(d).
obs(d).
has(d).
test(e).
dir(e).
obs(e).
has(e).
test(f).
dir(f).
obs(f).
has(f).
test(g).
dir(g).
obs(g).
has(g).
test(h).
dir(h).
obs(h).
has(h).
test(i).
dir(i).
obs(i).
has(i).
test(j).
dir(j).
obs(j).
has(j).
test(k).
dir(k).
obs(k).
has(k).
test(l).
dir(l).
obs(l).
has(l).
test(m).
dir(m).
obs(m).
has(m).
test(n).
dir(n).
obs(n).
has(n).
test(o).
dir(o).
obs(o).
has(o).
/*
test(p).
dir(p).
obs(p).
has(p).
test(q).
dir(q).
obs(q).
has(q).
test(r).
dir(r).
obs(r).
has(r).
test(s).
dir(s).
obs(s).
has(s).
test(t).
dir(t).
obs(t).
has(t).
test(u).
dir(u).
obs(u).
has(u).
test(v).
dir(v).
obs(v).
has(v).
test(w).
dir(w).
obs(w).
has(w).
test(x).
dir(x).
obs(x).
has(x).
test(y).
dir(y).
obs(y).
has(y).

test(z).
dir(z).
obs(z).
has(z).

test(aa).
test(bb).
test(cc).
test(dd).

dir(aa).
dir(bb).
dir(cc).
dir(dd).

obs(aa).
obs(bb).
obs(cc).
obs(dd).

has(aa).
has(bb).
has(cc).
has(dd).
*/

!start.

+!start <-
    !init;
    !goal.


+!init : check1 <-
    .print("ok").

+!init : check2 <-
    .print("ok").


+!goal : test(X) & dir(Y) & obs(Z) & has(TE) & went(OP) <-
    .print("ok").

+!goal : ok <-
    .print("ok").

/*
+!goal : dir(X) <-
    .print("ok").

+!goal : obs(X) <-
    .print("ok").

/*
+!goal : has(X) <-
    .print("ok").

+!goal : at(shop) <-
    .print("ok").

+!goal : wants(toy) <-
    .print("ok").

+!goal : likes(sugar) <-
    .print("ok").

+!goal : is(tall) <-
    .print("ok").

+!goal : lives(ontario) <-
    .print("ok").

+!goal : drives(car) <-
    .print("ok").

+!goal : hungry(cat) <-
    .print("ok").

+!goal : owns(bike) <-
    .print("ok").

+!goal : located(home) <-
    .print("ok").

+!goal : prefers(coffee) <-
    .print("ok").

+!goal : knows(math) <-
    .print("ok").

+!goal : wears(hat) <-
    .print("ok").

+!goal : uses(phone) <-
    .print("ok").

+!goal : studies(ai) <-
    .print("ok").

+!goal : enjoys(music) <-
    .print("ok").

+!goal : visits(library) <-
    .print("ok").

+!goal : needs(rest) <-
    .print("ok").

+!goal : reads(book) <-
    .print("ok").

+!goal : bought(ticket) <-
    .print("ok").

+!goal : teaches(logic) <-
    .print("ok").

+!goal : fixes(bug) <-
    .print("ok").
/*
+!goal : cooks(pasta) <-
    .print("ok").

+!goal : builds(robot) <-
    .print("ok").

+!goal : loves(sunset) <-
    .print("ok").

+!goal : sees(stars) <-
    .print("ok").

+!goal : feels(happy) <-
    .print("ok").
*/

/* at(shop).
wants(toy).
likes(sugar).
is(tall).
lives(ontario).
drives(car).
hungry(cat).
owns(bike).
located(home).
prefers(coffee).
knows(math).
wears(hat).
uses(phone).
studies(ai).
enjoys(music).
visits(library).
needs(rest).
reads(book).
bought(ticket).
teaches(logic).
fixes(bug).
cooks(pasta).
builds(robot).
loves(sunset).
sees(stars).
feels(happy).s*/







