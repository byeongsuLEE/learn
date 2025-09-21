# jpa 실전 및 성능 최적화

## entity를 response로 바로 반환하면 안되는 이유는?
양방향 관계의 entity를 반환하면 참조로 인해 무한 로딩이 발생한다.

### 해결방법
이를 해결하기 위해 안좋은 방법으로는 @jsonignore,하이버네이트 모듈 를 사용하는 방법과
적절한 방법으로 dto로 치환하는 방법이 있다.
entity를 dto로 치환하는 방법을 사용하자.


## 쿼리로 dto로 직접 조회
장점 
- dto 변환 작업의 생략으로 성능이 좋다.(미비)

단점
- 쿼리문에 dto 패키지명까지 작성해야함으로 코드가 복잡해진다.
- 최적화 하기가 어렵다.
- dto로 바로 조회하기 때문에 재사용성이 떨어진다.

## entity를 dto로 변환
장점
- 재사용성이 좋다.
- 실전에서 많이 쓰이고 
단점
- 성능이 쿼리로 dto 조회보단 좋지 않다.

## jpa dto 정리
쿼리로 dto를 바로 조회보단 dto 변환을 이용하자.
성능이 정말로 중요하면 복잡한 쿼리를 사용하는 repository를 하나 만들어서 사용하자.

![img.png](img.png)

## JPA distinct
- JPA distinct는 sql에 distinct + 같은 엔티티를 기준으로 중복을 제거합니다.
- 일대다 관계에서 fetch join 시 엔티티가 중복된 row 행들을 jpa distinct를 이용해서 해결할 수 있다.
- db의 distinct와 다르게 모든 속성들이 다 같아야 되는게 아니라 entity가 같은 행들을 제거한다.
- 단점
  - 페이징 쿼리(getFirst 등 페이징 쿼리와 관련된 함수를 포함)와 같이 쓰지 못한다. (limit와 offset이 적용 x)
  - 페이징이 안되는 이유는?
    - collection fetch join과 페이징 쿼리가 들어가면 메모리에서 정렬을 해버립니다.(데이터 많으면 out of memory 발생)
    - 일대다라서 데이터가 뻥튀기됩니다. 그래서 엔티티 기준으로 페이징이 불가능하다

## JPA 페이징 해결법
1. ToOne 관계에서는 fetchjoin을 때리자. 데이터가 join 시 뻥튀기 되지 않는다.
2. ToMany와 같은 컬렉션들을 가져올 때는 지연로딩으로 가져온다.
- 지연 로딩을 최적화 하기 위해 hibernate.default.batch_fetch_size, @BatchSize 을 적용한다.
- hibernate.default_batch_fetch_size : 글로 벌 설정
- @BatchSize : 개별 최적화
- 이 옵션은 컬렉션이나 프록시 객체를 한번에 설정한 size 만큼 in쿼리로 조회한다.


