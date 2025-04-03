//package com.lbs.user.user;
//
//import com.lbs.user.user.infrastructure.entity.TestEntity;
//import com.lbs.user.user.infrastructure.entity.UserType;
//import jakarta.persistence.EntityManager;
//import jakarta.transaction.Transactional;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class TestDataInitializer implements CommandLineRunner {
//
//    private final EntityManager em;
//
//    public TestDataInitializer(EntityManager em) {
//        this.em = em;
//    }
//
//    @Override
//    @Transactional
//    public void run(String... args) {
//        TestEntity user = new TestEntity();
////        user.setId(1L);
//        user.setUserType(UserType.lbs);
//        user.setName("병수");
//
//        TestEntity user2 = new TestEntity();
////        user2.setId(2L);
//        user2.setUserType(UserType.aju);
//        user2.setName("진우");
//        TestEntity user3 = new TestEntity();
////        user2.setId(2L);
//        user3.setUserType(UserType.addsion);
//        user3.setName("시온");
//        em.persist(user);
//        em.persist(user2);
//        em.persist(user3);
//        System.out.println("✅ 저장됨");
//    }
//}
