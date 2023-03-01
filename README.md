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


### 위키 페이지 / 회고록
[https://github.com/inudev5/NumbleBankingServerChallenge.wiki.git](https://github.com/inudev5/NumbleBankingServerChallenge/wiki/Numble-%EB%B1%85%ED%82%B9-%EC%84%9C%EB%B2%84-%EC%B1%8C%EB%A6%B0%EC%A7%80)
