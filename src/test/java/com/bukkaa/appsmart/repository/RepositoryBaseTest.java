package com.bukkaa.appsmart.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@Import(RepositoryTestConfiguration.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:test-db",
        "spring.jpa.hibernate.ddl-auto=update",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=test",
        "spring.datasource.password=test",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"})
public abstract class RepositoryBaseTest<R extends PagingAndSortingRepository<T, ID>, T, ID> {

    @Autowired
    protected R repository;

    @Autowired
    protected TestEntityManager testEntityManager;
}
