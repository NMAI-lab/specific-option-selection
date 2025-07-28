plant(tomato, garden).
plant(basil, balcony).
plant(rose, garden).

tool_is_available :- tool(X,available).
tool(shovel, available).
tool(scissors, unavailable).

!care_for_plants.

+!care_for_plants : plant(P,garden) & tool_is_available
  <- .print("I take care of the plant in the garden:", P).

+!care_for_plants : plant(PlantName, L) & tool_is_available
  <- .print("I take care of the plant :", PlantName, " at : ", L).

+!care_for_plants : plant(rose, L) & tool_is_available
  <- .print("I dig around the rose bush with the tool available.").

+!care_for_plants : plant(P,garden) & tool(shovel,available)
  <- .print("I use the shovel to care for", P, "in the garden").

