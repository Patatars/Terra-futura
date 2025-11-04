# Terra Futura

- Players are identified by their unique integer identifiers. 
- Cards in piles: 1 is newest, 4 is oldest
Hopefully, the rest of the interface is self-explanatory.


### GameObserver class

Just forwards the strings to correct observers identified by id of the player.
*Unit tests:* Yes

*Estimation:* S

### Game class

Tests if correct player does correct action. If the action is successfully performed, internal state changes. 
TakeCardNoCardDiscarded -> TakeCardCardDiscarded if discardLastCardFromDeck was successfully performed by player on turn
TakeCardNoCardDiscarded/TakeCardCardDiscarded -> ActivateCard if takeCard was successfully performed by player on turn
ActivateCard -> ActivateCard if activateCard not involving Assistance was successfully performed by player on turn and turn is 1..9.
ActivateCard -> SelectReward if activateCard involving Assistance was successfully performed by player on turn and turn is 1..9.
SelectReward -> ActivateCard if selectReward was successfully performed by player to whom belongs the reward.
Without detailed description, further transitions on successful endTurn. 
ActivateCard -> TakeCardNoCardDiscarded
ActivateCard -> SelectActivationPattern
ActivateCard -> SelectScoringMethod
Without detailed description, further transitions on successful setActivationPattern. 
SelectActivationPattern -> ActivateCard
Without detailed description, further transitions on successful setScoring. 
SelectScoringMethod -> SelectScoringMethod
SelectScoringMethod -> Finish
*Unit tests:* No. I believe that the code and the tests would be very similar, and I there was an error in the implementation, it is very likely that the same error will be in unit tests. We may postpone testing this class to integration tests. However, there is some merit in testing players taking turn and turnNumber increases. Feel free extract this responsibility from this class into one separate class and test it. 

*Estimation:* M / L (depending on whether you split of the turn changing functionality).

### MoveCard class

Class to perform a complex action. Takes card from Pile and Puts it to the Grid. Be careful not to change things until you are sure that everything is ok.
*Unit tests:* Yes
*Estimation:* M

### ProcessAction class

Class to perform a complex action. Be careful not to change things until you are sure that everything is ok.You may want to extract something to avoid repetition with ProcessActionAssistance.
*Unit tests:* Yes
*Estimation:* M / L

### ProcessActionAssistance class

Class to perform a complex action.  Be careful not to change things until you are sure that everything is ok.
*Unit tests:* Yes
*Estimation:* L

### SelectReward class

Which player can choose what rewards. Performs the action.
*Unit tests:* Yes
*Estimation:* M

### Pile Class

Players can either take one of four visibleCards or a player can remove the oldest card. In both cases one arbitrary card from the hodenCards is then added.
Make sure that you can control the randomness involved, so you control what is going on in the tests
*Unit tests:* Yes
*Estimation:* M

### ActivationPattern Class

At the end of the game, player can select one of the two ways to make additional round of activations.
*Unit tests:* Yes
*Estimation:* S

### ScorinmgMerhod Class

After final activations are done, player can chose the scoring method. This class calculates final point count.
*Unit tests:* Yes
*Estimation:* M

### Card Class

Manages resources on cards. From game logic perspective, the interesting part is dealing with pollution correctly according to the rules.
*Unit tests:* Yes
*Estimation:* M

### Effect Interface and classes implementing it

Composite pattern. Evaluates if it is possible to attain the effect using the card. Feel free to add classes as you wish to do everything.
*Unit tests:* Yes for the EffectOr class. Up to you for the, possibly very small leaf classes. 
*Estimation:* L (many leaf classes will be XS or S)

