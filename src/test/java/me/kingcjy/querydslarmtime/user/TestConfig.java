package me.kingcjy.querydslarmtime.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

@TestConfiguration
public class TestConfig {

    private final Logger log = LoggerFactory.getLogger(TestConfig.class);

    private static final String CURRENT_OS_ARCHITECTURE = System.getProperty("os.arch");
    private static final String ARM_REPRESENTATION = "aarch64";

    @Bean(destroyMethod = "stop")
    public MySQLContainer<?> mySQLContainer() {

        DockerImageName fullImageName = DockerImageName.parse("784015586554.dkr.ecr.ap-northeast-1.amazonaws.com/mysql:5.7.35").asCompatibleSubstituteFor("mysql");

        if (CURRENT_OS_ARCHITECTURE.equals(ARM_REPRESENTATION)) {
            fullImageName = DockerImageName.parse("784015586554.dkr.ecr.ap-northeast-1.amazonaws.com/arm64-mysql:8.0.28").asCompatibleSubstituteFor("mysql");

        }

        try {
            var mySQLContainer =
                    new MySQLContainer<>(fullImageName)
                            .withUsername("username")
                            .withPassword("temp_password")
                            .withUrlParam("sslMode", "DISABLED")
                            .withUrlParam("serverTimezone", "UTC");
            log.info("### before mySQLContainer.start()");
            mySQLContainer.start();
            log.info("### after mySQLContainer.start()");
            mySQLContainer.waitingFor(Wait.forListeningPort());
            return mySQLContainer;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("", e);
            throw new RuntimeException(e);
        }
    }

    @Bean
    @Primary
    public DataSource dataSource(MySQLContainer<?> mySQLContainer) {
        return DataSourceBuilder.create()
                .url(mySQLContainer.getJdbcUrl())
                .username(mySQLContainer.getUsername())
                .password(mySQLContainer.getPassword())
                .build();
    }
}
