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

*Member* 엔티티는 `username`과 `password` 필드를 갖습니다. `password` 필드는 인코딩된 비밀번호를 담습니다.

*Friendship* 엔티티는 `Member` 엔티티의 다대다 자기참조 관계를 표현하기 위한 엔티티입니다.  2개의 외래키인 `user_id`, `friend_id`를 가지며, 각각 `Member` 엔티티의 `id`를 나타냅니다. Friendship 엔티티를 만들 때 가장 고민한 점은, 친구관계가 양방향 관계라는 점이었습니다. 즉 내가 어떤 친구를 친구로 추가했다면, 그 친구의 친구목록에도 내가 들어가야 하는데,  이는 마치 무방향 그래프와도 같아서 하나의 친구관계가 양쪽에서 모두 공유됩니다. 여러 사이트를 검색해보았을 때, 뚜렷한 해답은 없었지만 이 경우 마치 그래프의 양방향 간선을 양쪽 노드의 인접리스트에 둘 다 포함시키듯,  관계를 나타나는 엔티티를 양쪽 엔티티에 전부 생성하는 방안을 추천하는 글을 보고 사용했습니다.즉, 한 유저가 다른 유저를 친구로 추가하면, 그 유저를 `user_id` 로 갖고 추가한 친구를 `friend_id` 로 갖는 `Friendship` 엔티티가 1개, 추가된 친구를 `user_id`  로 갖고 추가한 친구를 `friend_id` 로 갖는 `Friendship` 엔티티가 1개 더 저장됩니다.

*Account* 엔티티는 `Member` 엔티티와 다대일 관계를 갖습니다. `Member` 엔티티의 `id`를 가리키는`owner_id` 외래키를 갖습니다. 그 외에 `name`은 계좌명을, `balance`는 계좌의 잔고를 나타냅니다. `balance` 필드의 타입은 AccountBalance로,  다음과 같이 코틀린의 `value class`  문법과 `@JvmInline`  어노테이션을 사용하였습니다.
```kotlin
@JvmInline  
value class AccountBalance (val balance:Long):Serializable
```

value class를 사용했을 때의 장점은 타입 안정성을 보다 강화하는 것이라고 느꼈습니다. 실수로 다른 엔티티에서 사용하던 원시값을 혼동한다던가 하는 문제가 일어나지 않도록 보장할 수 있었습니다.

*Transaction* 엔티티는 계좌에서 금액을 송금하는 `fromAccount`와 금액을 송금받는 `toAccount` 두 개의 `Account`  엔티티 간의 다대다 관계를 표현합니다. 앞서 `Friendship`  엔티티와 달리, Transaction Entity는 방향성이 존재합니다.  따라서  금액을 송금하는  `Accout`  엔티티에 대해서만 Transaction을 추가하도록 구현하였습니다. 또한 낙관적 락을 사용하기 위해 `@Version`  어노테이션이 적용된 versionNo 필드가 있는데, 이를 비롯한 Transaction의 동시성 문제를 해결한 과정에 대해서는 api 명세 부분에서 설명하도록 하겠습니다.

모든 JPA 엔티티 클래스는 `PrimaryKeyEntity`  클래스를 상속받도록 했습니다. 이 엔티티는 JPA의 `AudityEntityListener` 를 사용하는 CreatedDate, LastModifiedDate 등의 생명주기 필드를 가지며,  `@GeneratedValue`  로 영속화 시에 id가 생성되도록 하지 않고, Persistable 인터페이스를 구현해  객체 생성 시에 id가 생성되도록 하였습니다. 또한 Id로는 UUID 타입을 사용하였으며 `ulid-creator`  라이브러리를 사용해 UUID로도 정렬이 가능하도록 하였습니다. 이러한 구현은  https://spoqa.github.io/2022/08/16/kotlin-jpa-entity.html  해당 블로그 포스트의 영향을 받았습니다. 다만 UUID를 사용할 경우 기본키 칼럼의 데이터 크기가 커져 성능상의 불리함이 있으므로, 장단점을 잘 고려해야겠다고 생각했습니다.

### API 명세

RestDocs API Documentation:
https://inudev5.github.io/NumbleBankingServerChallenge/

**로그인 [POST] /login**

- 로그인 서비스는 최소한의 Spring Security 설정을 통해 구현하고, 마지막으로 인증 필터에서 세션에 로그인한 유저의 아이디 정보를 전달하도록 하였습니다.
- 로그인과 회원가입 엔드포인트를 제외하고는 전부 인증이 필요하도록 처리하였고, 로그인 엔드포인트는 컨트롤러에는 따로 만들지 않고 spring security의 UsernamePasswordAuthenticationFilter가 사용하는 엔드포인트를 그대로 사용했습니다.
- 요청용 객체인 LoginRequest 클래스를 만들어 api request body 로 사용하였습니다.  이러한 요청용 값 객체는 코틀린의 data class를 사용하였고 직렬화 인터페이스를 구현했습니다.
- 값 객체/ DTO의 역할 분리에 대해 고민을 하게 되었습니다.  레이어간 값을 전달하는 객체는 DTO로 작성하면 되므로 단순했지만, 서비스에서 전달받은 DTO를 컨트롤러에서 최종적으로 반환할 때 응답 값 객체를 따로 반환해야하는지가 첫번째 고민거리였습니다.
- 제가 내린 결정은 요청용 객체는 값 객체로써 VO나 Request로 별도로 네이밍하고, DTO가 응답에 필요한 데이터를 적절하게 담고 있는 경우에는 별도의 응답용 VO를 만들지 않았습니다.
- 이렇게 결정하고 나니 실제로 응답 VO는 단 한개도 사용하지 않게 되었습니다.
- 그 이유를 살펴보면 우선 DTO 자체도 전부 코틀린의 데이터 클래스로 작성했기 때문에 불변 객체임이 보장되었으므로 Serializable을 구현하는 것에 문제가 없었고, 응답에 필요없는 데이터가 거의 없었기 때문입니다.
- 요청 객체의 네이밍 또한 Request와 VO 중에서 고민이 있었는데, 챗 GPT의 의견을 구하니 Request가 좀 더 목적을 잘 드러내는 네이밍 같다는 답을 주었고 저도 그에 공감이 되어 Request를 사용했습니다.


**회원가입 [POST] /signup**

- 마찬가지로 요청용 SignUpRequest 객체를 사용했습니다.
-  `Member` 엔티티의 `username` 필드를 `unique`  로 설정하여, 기존 테이블에 동일한 회원명이 존재할 시에 `UserAlreadyExistsException` 이라는 커스텀 예외를 발생시키고 응답 코드로는 409 리소스 충돌을 반환했습니다.

로그인과 회원가입을 제외한 모든 엔드포인트들은 세션에 포함된 멤버id로부터 현재 로그인 중인 사용자 정보를 추출하며, 존재하지 않는 사용자의 경우 기본적으로 404 Not Found 상태를 표시합니다.

**친구목록 조회 [GET] /users/friends**

- 별도의 요청 body 나 파라미터 없이 세션 id만으로 친구목록을 조회합니다.


**친구 추가 [POST] /users/friends**

- FriendRequest 요청 객체를 사용하여, 추가할 친구의 id로 회원을 조회하고, 세션에 담긴 유저의 id로 회원을 조회하여, 두 회원간의 친구관계 엔티티 `Friendship`  을 양방향으로 생성하고 저장합니다.
- 회원을 조회하지 못하는 경우 404 Not Found, 조회한 두 회원 엔티티가 동일한 경우 400 Bad Request 상태를 표시합니다.

**계좌 단건 조회 [GET] /accounts/{accountId}**

- 세션으로부터 가져온 유저 id와, path variable로 가져오는 accountid를 사용해 유저의 account 중 해당 id를 가지는 account를 조회하고 AccountDto로 반환합니다. AccountDto는 유저의 id와 계좌 id, 계좌명, 잔액 필드를 가지고 있습니다. 계좌가 존재하지 않을 경우 `404` Not Found 상태를 표시합니다.

**계좌 목록 조회 [GET] /accounts**

- 세션으로부터 가져온 유저 id가 주인인 계좌를 컬렉션으로 조회하고, `AccountDto`를 반환합니다.

**계좌 생성 [POST] /account**

- 요청용 `AccountCreateReuqest` 객체를 사용하여 계좌를 생성하고, `AccountDto`를 반환합니다.

**계좌 이체 [POST] /account/transfer**

- 요청용 `TransactionRequest` 객체를 사용하여 Transaction을 수행합니다. 이 과정에서 TransactionService 빈의 `createTransaction` 메서드를 호출하는데, 해당 메서드는 다음과 같습니다.
```kotlin
@Transactional  
override fun createTransaction(memberId: UUID, transactionRequest: TransactionRequest): TransactionDTO {  
    val (fromAccountId, toAccountId, amount) = transactionRequest  
    //TransactionRequest 요청객체는 전송하는 계좌id, 수신할 계좌 id, 보내는 금액의 세가지 필드로 이루어집니다.
    val fromAccount =  
        accountRepository.findByOwnerAndId(memberId, fromAccountId,true)  
            ?: throw CustomException.AccountNotFoundException()  
	//fromAccount을 조회합니다. 존재하지 않는 계좌는 404 Not Found를 반환합니다.
	// accountRepository의 조회메서드는 QueryDSL을 사용해 락을 설정할 지 여부를 파라미터로 받아
	//적용하도록 되어있습니다.
    val toAccount =  
        accountRepository.findByIdJoinOwner(toAccountId, true) ?: throw CustomException.AccountNotFoundException()  
	//toAccount을 조회합니다. 존재하지 않는 계좌는 404 Not Found를 반환합니다.
	//락을 적용한 없는 조회쿼리 입니다.
    friendshipRepository.findFriend(fromAccount.owner.id, toAccount.owner.id)  
        ?: throw CustomException.BadRequestException()  
	//두 계좌 주인이 서로 친구인지 확인합니다. 만약 친구가 아니라면, 400 Bad Request를 표시합니다. 
    val transaction =  
        Transaction(fromAccount, toAccount, amount).let { transactionRepository.save(it) } //shared-lock  
    fromAccount.checkAmount(amount) // x-lock : deadlock  
    toAccount.addAmount(amount)  
    numbleAlarmService.notify(fromAccount.owner.id, "transaction completed.")  
    return TransactionDTO(fromAccountId, toAccountId, amount)  
}
```


기본적으로 JPA 엔티티의 @Version 어노테이션 필드를 사용하면 자동으로 JPA의 Optimistic lock을 적용합니다. 낙관적 락은 Repeatable Read 격리수준을 보장하지만, OptimisticLockException이 발생하여 트랜잭션이 중단되는 경우 이를 재처리 하는 오버헤드가 있습니다.
사실 제가 생각한 트랜잭션의 락 시나리오는 다음과 같습니다. 만약 제가 JPA의 @Version을 이용해 낙관적 락으 트랜잭션 메서드를 구현하고, OptimisticLockException 예외를 재처리하는 로직을 구현했다고 가정해보겠습니다. 동시성에 대한 테스트는 다음과 같습니다.
 ```kotlin
 @Test  
fun `should createTransaction in concurrent env`() {  
    lateinit var res: List<TransactionDTO>  
    friendshipRepository.save(Friendship(owner, friend1))  
    runBlocking {  
        val list = (1..100).map{  
			async { 
			 accountService.createTransaction(owner.id,
				TransactionRequest(account1.id,friendac.id,100L))  
            }  
        }
		res=list.awaitAll()  
    }  
    val res1 = accountRepository.findById(account1.id).orElse(null)  
    val res2 = accountRepository.findById(friendac.id).orElse(null)  
    assertThat(res1).isNotNull; assertThat(res2).isNotNull  
    assertThat(res1.balance.balance).isEqualTo(0L)  
    assertThat(res2.balance.balance).isEqualTo(10000L)  
    assertThat(res).extracting("fromAccountId").containsOnly(account1.id)  
    assertThat(res).extracting("toAccountId").containsOnly(friendac.id)  
}
```

코틀린 코루틴을 사용해 100개의 코루틴을 실행합니다. 쓰레드와 조금의 차이점은 있지만, 코루틴만으로도 적절한 동시성 환경을 테스트할 수 있습니다. 만약 낙관적 락을 사용하여 계좌이체 서비스 메서드를 아래 테스트처럼 실행하면, 데드락이 발생할 것이라고 생각했습니다.
낙관적 락을 사용했을 때 데드락이 발생할 것이라고 예상했던 이유는 다음과 같습니다. createTransaction 메서드에는 다음 부분이 존재합니다.
  ```kotlin
  fun createTransaction(...){
   val fromAccount =  
        accountRepository.findByOwnerAndId(memberId, fromAccountId,false)  
            ?: throw CustomException.AccountNotFoundException()  
    val toAccount =  
        accountRepository.findByIdJoinOwner(toAccountId, false) ?: throw CustomException.AccountNotFoundException()  //락을 사용하지 않고 조회
    ...

  val transaction =  
        Transaction(fromAccount, toAccount, amount).let { transactionRepository.save(it) } //shared-lock  
    fromAccount.checkAmount(amount) // x-lock : deadlock  
    toAccount.addAmount(amount)  
    numbleAlarmService.notify(fromAccount.owner.id, "transaction completed.")  
    return TransactionDTO(fromAccountId, toAccountId, amount)  
  }
```
새롭게 트랜잭션 엔티티를 update,insert,delete 하면서 외래키 제약조건을 포함한 데이터(fromAccount, toAccount, db에서 조회한 엔티티)를 사용할 경우에 해당 엔티티에 `shared lock`이 걸립니다. 서로 다른 트랜잭션이 동시에 insert한다고 해도, shared lock은 서로 호환가능하기 때문에 문제가 일어나지 않습니다. 이 때 트랜잭션 `A` 와 트랜잭션 `B` 가 모두 shared lock을 획득했다고 가정해보겠습니다.
그 다음 실행되는 fromAcount와 toAccount의 엔티티 변경 감지가 일어나면서, 트랜잭션 `A` 가 update 쿼리가 실행될 때 `exclusive lock`   획득하려고 시도합니다. 하지만,  트랜잭션 `B`가 이미 exclusive lock과 호환되지 않는 shared lock를 획득한 상태이므로 shared lock이 해제될 때까지 데드락이 발생합니다.
이 시나리오는 해당 블로그 포스트에서 읽게 되었습니다.
https://velog.io/@znftm97/%EB%8F%99%EC%8B%9C%EC%84%B1-%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0%ED%95%98%EA%B8%B0-V1-%EB%82%99%EA%B4%80%EC%A0%81-%EB%9D%BDOptimisitc-Lock-feat.%EB%8D%B0%EB%93%9C%EB%9D%BD-%EC%B2%AB-%EB%A7%8C%EB%82%A8
이 때, 데드락을 회피할 수 있는 방법으로는 fk를 포함하는 엔티티를 처음에 조회할 때 미리 exclusive lock를 획득하는 방법이 있습니다. 이 방법을 사용하면, 서로다른 트랜잭션이 서로 동시에 해당 `row` 를 조회할 수 없게 되고, 따라서 트랜잭션 하나가 먼저 커밋 되기 전에 다른 트랜잭션에 `row`에 접근하지 못합니다.
저는 이러한 시나리오를 트랜잭션에서 낙관적 락을 사용할 때와 / 비관적 락을 사용할 때 두 경우로 나누어 테스트에서 검증하려 해보았습니다. 하지만 코루틴으로 작성한 테스트는 두 경우 모두 아무 문제없이 통과합니다. 100개의 코루틴을 좀 더 많은 300개, 500개의 코루틴으로 늘려봐도 아무 문제가 없습니다. 혹시 코루틴이 충분히 동시성을 테스트하지 못하는 것인가 싶어 ExecutorService와 CountDownLatch를 사용해보았지만, 이 경우에는 `OptimisticLock` , `Pessimistic Lock` 에 관계없이 해당 테스트에서 무조건 데드락이 발생하였습니다. 즉 결과적으로 예상했던 동시성에서의 상황을 테스트로 검증해내는 데에는 실패했습니다.
결론적으로는, 비관적 락을 적용한 트랜잭션이 코루틴을 적용한 테스트에서는 문제없이 통과하는 것을 확인하고, 이를 서비스 메소드로 적용하게 되었습니다.

### 테스트

기타 다른 테스트에 대해서 얘기하기 전에,  api 문서를 생성하기 위해 적용한 RestDocs를 사용하면서 느낀점을 정리하도록 하겠습니다.

- Spring RestDocs를 사용해 각 엔드포인트에 대하여 RestDocs 테스트를 작성하고, asciidoctor 포맷으로 문서화하였습니다.
- 예외를 커스텀하고 `ControllerAdvice` 를 통해 처리하도록 구현하였습니다.
- 커스텀한 예외의 상태코드와 메세지는 `mockMvc` 를 사용한 테스트에서는 실제 어플리케이션 서블릿과 달리 모킹한 서블릿이 처리하지 않아 JSON 형태의 body에 포함되지 않는다는 점을 알게 되었습니다. 따라서 응답 본문만을 문서화하는 RestDocs 문서에는 포함시킬 수 없었던 것이 아쉬움이 남습니다.
- 응답 본문에 예외 메세지와 코드가 포함되는지를 테스트하기 위해서는 RestAssured나 TestRestTemplate 같이, 실제 서블릿을 사용하는 테스트 라이브러리를 사용해야 하나, 테스트에 의존성을 추가하는 것에 부담을 느껴서 RestDocs에는 정상 응답만을 문서화 하는 것에 만족하기로 했습니다.
- RestDocs를 사용하면서 느낀점은, 프로덕션 코드에 영향을 미치지 않는 점은 좋으나 별도의 테스트 설정/문법을 학습해야 하고, 설정이 생각보다 까다롭고 복잡하다는 점이 단점으로 다가왔습니다.
- 그러나 어느정도 러닝커브를 거치고 나니, asciidoc 문서나, 테스트 코드를 통해 문서 생성을 자동화할 수 있다는 점에 만족감을 느꼈습니다.


**컨트롤러 테스트**

- 컨트롤러는 앞서 정상응답을 테스트하기 위해 모킹 없이 모든 컴포넌트를 사용한 RestDocs 테스트를 제외하고, 각각의 컨트롤러마다 서비스 컴포넌트를 모킹한 Unit Test를 작성했습니다.
- 서비스 컴포넌트를 모킹하는 데는 코틀린의 모킹 라이브러리인 `MockK` 를 사용했습니다.
- @WebMvcTest를 사용하려고 했으나 , 프로젝트에 적용한 스프링 시큐리티 컨텍스트를 제대로 불러오지 못하는 문제가 있었습니다. 로그인을 스프링 시큐리티와 연동했기 때문에 스프링 시큐리티 컨텍스트가 필요했고, 따라서 `@WebMvcTest` 대신 @AutoConfigureMockMvc 를 사용해서 컨텍스트를 테스트에서 사용하게 되었습니다.
- 스프링 시큐리티 로그인에 대한 테스트는 `/login` 과 `/signup` 두 엔드포인트를 제외하면 전부 세션에 있는 인증정보를 가져와야 했기 때문에 모든 테스트 메서드에 @WithMockUser을 적용해서 인증된 상황을 가정했습니다.

**리포지토리 테스트**

- 레포지토리 테스트는 전부 @DataJpaTest를 사용했습니다.
- 직접 작성한 쿼리 메서드와 사용한 JPA 쿼리 메서드들의 동작을 검증하기 위해 TestEntityManager 를 주입받아 직접 영속성을 조작했습니다.
- 쿼리에서 락을 사용하는 테스트의 경우 코루틴으로 동시에 동일한 엔티티에 대한 update 쿼리를 수행하여, 예외가 발생하지 않는지 확인했습니다.
- 결과적으로 리포지토리 테스트는 JPA 의존성을 제외하고는 최대한 의존성을 제외한 유닛 테스트에 가깝게 작성하려 하였습니다.

**서비스 테스트**

- 서비스 테스트는 스프링 컨텍스트를 로드하여 사용하는 통합테스트로 진행하였습니다.
- 레포지토리 의존성을 사용하여, 서비스 단에서 레포지토리 메서드의 호출 결과에 따라 반환하는 각기 다른 예외 및 데이터를 테스트하는 데에 주력했습니다.


이 프로젝트를 진행하면서 정말 많은 의문점을 해소할 수 있었고, 새로운 시도를 해볼 수 있었습니다. 특히나, ORM과 데이터베이스 락을 사용하면서 가능한 여러가지 방안에 대해서 고민하고 직접 테스트로 검증해볼 수 있었던 점이 가장 즐거웠습니다. 그러나 직접 쿼리를 작성해본 경험이 적어 쿼리 성능에 대한 고민을 하고 이를 반영해보는 것까지는 해보지 못한 것이 아쉽습니다.


