## Numble 뱅킹 서버 챌린지

```kotlin

     +-------------+          +---------------+
     |   Member    |          |    Account    |
     +-------------+          +---------------+
     | id          | 1     *  | id            |
     | username    |          | name          |
     | password    |          | balance       |
     +-------------+          | owner_id (FK) |
            |                 +---------------+
            |                          |
           *  *                        |
    +-----------------+                *
    |    Friendship   |       +-----------------+
    +-----------------+       |    Transaction  |
    | id              |       +-----------------+
    | user_id (FK)    |       | id              |
    | friend_id (FK)  |       | from_account_id |
    +-----------------+       | to_account_id   |
                              | amount          |
                              +-----------------+
```


### API 명세

RestDocs API Documentation:
https://inudev5.github.io/NumbleBankingServerChallenge/


