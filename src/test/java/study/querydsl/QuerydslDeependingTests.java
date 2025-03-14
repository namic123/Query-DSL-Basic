package study.querydsl;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import java.util.List;

import static study.querydsl.entity.QMember.*;

@SpringBootTest
@Transactional
public class QuerydslDeependingTests {

    @Autowired
    EntityManager em;

    JPAQueryFactory jpaQueryFactory;



    @BeforeEach
    public void setUp(){
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
    * 프로젝션
    * DB의 필요한 속성만을 조회하는 것을 프로젝션이라고 함
        즉 select 절의 지정 대상이라고 볼 수 있음
    */

    // 프로젝션 대상이 하나인 경우
    @Test
    public void oneProjection(){

        // 프로젝션 대상이 하나면 타입을 명확하게 지정할 수 있음
        List<String> result = jpaQueryFactory
                .select(member.username)
                .from(member)
                .fetch();
    }

    // 프로젝션 대상이 둘인 경우
    // 프로젝션 대상이 둘 이상이면 튜플이나 DTO로 조회
    // 튜플 조회
    @Test
    public void tupleProjection(){

        List<Tuple> tupleList = jpaQueryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();
    }

}
