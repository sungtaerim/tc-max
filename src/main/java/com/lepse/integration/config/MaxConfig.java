package com.lepse.integration.config;

import com.lepse.integration.services.FileStorageService;
import com.lepse.integration.services.SshProcedureCallService;
import com.lepse.integrations.log.LogsDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@PropertySource({"classpath:application.properties"})
public class MaxConfig {
    final Environment environment;

    @Autowired
    public MaxConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public DataSource maxDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(this.environment.getProperty("spring.datasource.url"));
        dataSource.setDriverClassName((String) Objects.requireNonNull(this.environment.getProperty("spring.datasource.driver-class-name")));
        dataSource.setUsername(this.environment.getProperty("informix.login"));
        dataSource.setPassword(this.environment.getProperty("informix.password"));
        return dataSource;
    }

    @Bean
    public FileStorageService maxFileStorageService(){
        final String pathToStorage = environment.getProperty("file.upload-dir");

        return new FileStorageService(pathToStorage);
    }

    @Bean
    public SshProcedureCallService sshProcedureCallService(){
        final String sshConnectionPath = environment.getProperty("ssh.path");
        final String sshPassword = environment.getProperty("ssh.password");

        return new SshProcedureCallService(sshConnectionPath, sshPassword);
    }

    @Bean
    public LogsDAO maxLogsDAO() {
        final String logsRepositoryUrl = environment.getProperty("logs.repository.url");

        return new LogsDAO(logsRepositoryUrl);
    }
}
