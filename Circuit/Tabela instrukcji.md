## Tabela instrukcji

| Instrukcja | Rejestr Danych | Rejestr Docelowy | Przykład        |
|------------|----------------|------------------|-----------------|
| ADD        | ---            | REG A - REG D    | A + B           |
| SUB        | ---            | REG A - REG D    | A - B           |
| AND        | ---            | REG A - REG D    | A AND  B        |
| OR         | ---            | REG A - REG D    | A OR   B        |
| NOT        | REG A - REG B  | REG A - REG D    | NOT REG X       |
| MOV        | REG A - REG D  | REG A - REG D    | REG X  -> REG Y |
| IN         | ---            | REG A - REG D    | INPUT  -> REG Y |
| OUT        | REG A - REG D  | ---              | OUTPUT -> REG Y |
| LOAD       | ---            | REG A - REG D    | RAM    -> REG Y |
| STORE      | REG A - REG D  | ---              | REG X  -> RAM   |
