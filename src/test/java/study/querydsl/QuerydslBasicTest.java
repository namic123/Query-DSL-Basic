package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static study.querydsl.entity.QMember.*;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory jpaQueryFactory;


    @BeforeEach
    public void before(){
        jpaQueryFactory = new JPAQueryFactory(em);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    /*
    * JPQL
        ✅ 장점
        JPA 표준 쿼리 언어로, JPA를 사용하는 프로젝트에서 기본적으로 제공됨.
        SQL과 유사한 문법으로 직관적.
        *
        ❌ 단점
        문자열 기반이라 컴파일 타임에 오류를 잡을 수 없음.
        파라미터 바인딩이 번거로우며, 복잡한 동적 쿼리를 만들기 어려움.
        자동 완성이 안 되므로 오타 발생 가능.
    *
    * */

    /* JPQL 문법 */
    @Test
    public void startJPQL(){
        String qlString = "select m from Member m where m.username = :username";

        Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();
    }


/*
        * QueryDSL

        * ✅ 장점
        Type-safe: 컴파일 타임에 문법 오류를 잡을 수 있음.
        자동 완성 지원: IDE의 도움을 받아 개발 속도 향상.
        동적 쿼리 작성이 용이: BooleanBuilder 또는 Expressions 등의 기능을 활용해 복잡한 조건을 쉽게 조립 가능.
        가독성 및 유지보수성 우수: 코드 기반으로 작성되므로 유지보수 시 안정적.

        * ❌ 단점
        별도의 Q 클래스 생성 필요 (gradle build 또는 annotationProcessor 설정 필요).
        JPQL보다 러닝 커브가 존재함.
        프로젝트 규모가 작다면 굳이 사용할 필요가 없을 수도 있음.
* */

    /* QueryDsl 문법 */
    @Test
    public void startQuerydsl(){
        Member findMember = jpaQueryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1")) // 파라미터 바인딩
                .fetchOne();

        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
    }


    // 검색 조건 쿼리 기본 예제
    @Test
    public void search() {
        Member findMember = jpaQueryFactory
                .selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.eq(10)))
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void searchAndParam() {
        List<Member> result1 = jpaQueryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"), // and 조건은 , 쉼표로 간단하게 작성 가능
                        member.age.eq(10))
                .fetch();

        assertThat(result1.size()).isEqualTo(1);
    }

    /*
    JPQL이 제공하는 모든 검색 조건 제공
    * member.username.eq("member1") // username = 'member1'
    member.username.ne("member1") //username != 'member1'
    member.username.eq("member1").not() // username != 'member1'
    member.username.isNotNull() //이름이 is not null
    member.age.in(10, 20) // age in (10,20)
    member.age.notIn(10, 20) // age not in (10, 20)
    member.age.between(10,30) //between 10, 30
    member.age.goe(30) // age >= 30
    member.age.gt(30) // age > 30
    member.age.loe(30) // age <= 30
    member.age.lt(30) // age < 30
    member.username.like("member%") //like 검색
    member.username.contains("member") // like ‘%member%’ 검색
    member.username.startsWith("member") //like ‘member%’ 검색
    * */
}
