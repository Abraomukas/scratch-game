# Scratch Game #

## Description ##

Problem statement: You need to build a scratch game, that will generate a matrix (for example 3x3) from symbols(based on probabilities for each individual cell) and based on winning combintations user either will win or lost.
User will place a bet with any amount which we call *betting amount* in this assignment.


There are two types of symbols: Standard Symbols, Bonus Symbols.

**Standard Symbols**: identifies if user won or lost the game based on winning combinations (combination can be X times repeated symbols or symbols that follow a specific pattern)
Bonus symbols are described the table below:


| Symbol Name | Reward Multiplier |
|-------------|-------------------|
| A           | 50                |
| B           | 25                |
| C           | 10                |
| D           | 5                 |
| E           | 3                 |
| F           | 1.5               |


**Bonus Symbols**: Bonus symbols are only effective when there are at least one winning combinations matches with the generated matrix. 
Bonus symbols are described the table below:

| Symbol Name | Action                       |
|-------------|------------------------------|
| 10x         | mutiply final reward to 10   |
| 5x          | mutiply final reward to 5    |
| +1000       | add 1000 to the final reward |
| +500        | add 500 to the final reward  |
| MISS        | none                         |

Input format:

```
    "bet_amount": 100
```

| field name | description    |
|------------|----------------|
| bet_amount | betting amount |

Output format:

```json
{
    "matrix": [
        ["A", "A", "B"],
        ["A", "+1000", "B"],
        ["A", "A", "B"]
    ],
    "reward": 6600,
    "applied_winning_combinations": {
        "A": ["same_symbol_5_times", "same_symbols_vertically"],
        "B": ["same_symbol_3_times", "same_symbols_vertically"]
    },
    "applied_bonus_symbol": "+1000"
}
```

| field name                   | description                                            |
|------------------------------|--------------------------------------------------------|
| matrix                       | generated 2D matrix                                    |
| reward                       | final reward which user won                            |
| applied_winning_combinations | Map of Symbol and List of applied winning combinations  |
| applied_bonus_symbol         | applied bonus symbol (can be null if the bonus is MISS |

Rewards breakdown:

| reward name             | reward details                    |
|-------------------------|-----------------------------------|
| symbol_A                | bet_amount x5                     |
| symbol_B                | bet_amount x3                     |
| same_symbol_5_times     | (reward for a specific symbol) x5 |
| same_symbol_3_times     | (reward for a specific symbol) x1 |
| same_symbols_vertically | (reward for a specific symbol) x2 |
| +1000                   | add 1000 extra to final reward    |

Calculations: (bet_amount x reward(symbol_A) x reward(same_symbol_5_times) x reward(same_symbols_vertically)) + (bet_amount x reward(symbol_B) x reward(same_symbol_3_times) x reward(same_symbols_vertically)) (+/x) reward(+1000) = (100 x5 x5 x2) + (100 x3 x1 x2) +1000 = 5000 + 600 + 1000 = 6600

Examples (with a winning combination [same symbols should be repeated at least 3 / reward x2]):

Lost game:

| input             | output                                                                                                                      |
|-------------------|-----------------------------------------------------------------------------------------------------------------------------|
| "bet_amount": 100 | { </br> "matrix": [ </br> ["A", "B", "C"], </br> ["E", "B", "5x"], </br> ["F", "D", "C"] </br> ], </br> "reward": 0 </br> } |

Description: The game is settled as LOST, so bonus symbol has not been applied because the reward is 0.

Won game:

| input             | output                                                                                                                                                                                                                                                   |
|-------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| "bet_amount": 100 | { </br> "matrix": [ </br> ["A", "B", "C"], </br> ["E", "B", "10x"], </br> ["F", "D", "B"] </br> ], </br> "reward": 50000, </br> "applied_winning_combinations": {</br> "B": ["same_symbol_5_times"] </br> }, </br> "applied_bonus_symbol": "10x" </br> } |

Description: user placed a bet with 100 betting amount and generated matrix has 3 same symbols which matches with the winning combination, also 10x bonus also will be applied.
Formula: 100(betting amount) x 25(symbol B) x2(at least 3 same symbols winning combination) x10(x10 bonus symbol) = 50000 (winning amount)
