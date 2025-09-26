# querydsl

## Q - Type 방법 2가지
1. new Qtyte(이름)
2. QEntity.QEntity (이걸 쓰자)

## 검색 조건 쿼리
1. queryFactory.select(entity, entity)
2. queryFactroy.selectFrom(entity)

.eq : =
.ne : !=
.eq().not() : .ne
.isNotNull() : null이 아니다.

.in(10,20) : in (10,20)
.notIn(10,20) : not in (10,20)
.between(10,30) : between 10 30 

t.goe(30) : t >= 30
t.gt(30)  : t > 30
t.loe(30) : t <= 30
t.lt(30)  : t < 30

t.like("member%") : like 검색
t.contain("member") : like `%member%`
t.startsWith("member") : like `member%`

## querydsl 벌크 연산
1. 영속성 컨텍스트를 거치지 않고 쿼리가 날라가니 벌크 연산 이후 flush와 clear를 적용해서 영속성 컨텍스트를 비워주자

## 기본 join
연관관계가 없는 entity 끼리도 join을 할 수 있다.   

## 조인 - on 절