package me.kingcjy.querydslarmtime.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestConfig {

    private final Logger log = LoggerFactory.getLogger(TestConfig.class);

    @Bean(destroyMethod = "stop")
    public MySQLContainer<?> mySQLContainer() {
        DockerImageName fullImageName = DockerImageName.parse("mysql:5.7").asCompatibleSubstituteFor("mysql");

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
}
