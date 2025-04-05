//package com.lbs.user.user;
//
//import com.lbs.user.user.infrastructure.entity.AdminEntity;
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
//        AdminEntity admin = new AdminEntity();
//        admin.setPassword("1234");
//        em.persist(admin);
//        System.out.println("✅ 저장됨");
//    }
//}
