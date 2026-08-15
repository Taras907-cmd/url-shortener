package org.example;

import org.example.entity.Client;
import org.example.entity.Planet;
import org.example.entity.Ticket;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtils {

    private static final HibernateUtils INSTANCE = new HibernateUtils();

    private final SessionFactory sessionFactory;

    private HibernateUtils() {
        runFlywayMigration();

        this.sessionFactory = new Configuration()
                .addAnnotatedClass(Client.class)
                .addAnnotatedClass(Planet.class)
                .addAnnotatedClass(Ticket.class)
                .buildSessionFactory();
    }

    public static HibernateUtils getInstance() {
        return INSTANCE;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    private void runFlywayMigration() {
        Flyway flyway = Flyway.configure()
                .dataSource(
                        "jdbc:mysql://localhost:3307/untitled18_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                        "root",
                        "root1234")
                .locations("classpath:migration")
                .load();

        flyway.migrate();
    }
}