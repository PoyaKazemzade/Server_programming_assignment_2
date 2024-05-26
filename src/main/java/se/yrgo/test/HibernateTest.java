package se.yrgo.test;

import jakarta.persistence.*;

import se.yrgo.domain.Student;
import se.yrgo.domain.Subject;
import se.yrgo.domain.Tutor;

import java.util.List;

public class HibernateTest {
    public static EntityManagerFactory emf = Persistence.createEntityManagerFactory("databaseConfig");

    public static void main(String[] args) {
        setUpData();
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // ---- task 1 ----
        Subject programming = em.find(Subject.class, 3);
        List<Student> students = em.createQuery("select tutor.teachingGroup from Tutor as tutor where :subject member of tutor.subjectsToTeach")
                .setParameter("subject", programming).getResultList();
        for (Student s : students) {
            System.out.println(s);
        }

        // ---- task 2 ----
        List<Object[]> studentsAndTutors = em.createQuery("select student.name, tutor.name from Tutor as tutor join tutor.teachingGroup as student")
                .getResultList();
        for (Object[] obj : studentsAndTutors) {
            System.out.println("student: " + obj[0] + " ==> tutor: " + obj[1]);
        }

        // ---- task 3 -----
        double averageSemester = (double) em.createQuery("select avg(numberOfSemesters) from Subject subject")
                .getSingleResult();
        System.out.printf("Average length of semesters is %.2f%n", averageSemester);

        // ---- task 4 ----
        int maxSalary = (int) em.createQuery("select max(tutor.salary) from Tutor tutor")
                .getSingleResult();
        System.out.printf("Max salary is %d%n", maxSalary);

        // ---- task 5 ----
        List<Tutor> result = em.createNamedQuery("searchBySalary", Tutor.class).getResultList();
        for (Tutor tutor : result) {
            System.out.println(tutor);
        }


        tx.commit();
        em.close();
    }

    public static void setUpData() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();


        Subject mathematics = new Subject("Mathematics", 2);
        Subject science = new Subject("Science", 2);
        Subject programming = new Subject("Programming", 3);
        em.persist(mathematics);
        em.persist(science);
        em.persist(programming);

        Tutor t1 = new Tutor("ABC123", "Johan Smith", 40000);
        t1.addSubjectsToTeach(mathematics);
        t1.addSubjectsToTeach(science);


        Tutor t2 = new Tutor("DEF456", "Sara Svensson", 20000);
        t2.addSubjectsToTeach(mathematics);
        t2.addSubjectsToTeach(science);

        // This tutor is the only tutor who can teach History
        Tutor t3 = new Tutor("GHI678", "Karin Lindberg", 0);
        t3.addSubjectsToTeach(programming);

        em.persist(t1);
        em.persist(t2);
        em.persist(t3);


        t1.createStudentAndAddtoTeachingGroup("Jimi Hendriks", "1-HEN-2019", "Street 1", "city 2", "1212");
        t1.createStudentAndAddtoTeachingGroup("Bruce Lee", "2-LEE-2019", "Street 2", "city 2", "2323");
        t3.createStudentAndAddtoTeachingGroup("Roger Waters", "3-WAT-2018", "Street 3", "city 3", "34343");

        tx.commit();
        em.close();
    }


}
