package com.github.bilak.springclassloaderissue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.loader.launch.PropertiesLauncher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BootstrapApp {

    private static final Logger LOG = LoggerFactory.getLogger(BootstrapApp.class);

    private BootstrapApp() {
    }

    public static void main(String[] args) throws Exception {

        final SpringApplicationBuilder builder = new SpringApplicationBuilder(
                ThreadPoolInitializer.class, PluginLoader.class)
                .web(WebApplicationType.NONE);

        try (ConfigurableApplicationContext ctx = builder.run(args)) {
            LOG.info("BootstrapApp context loaded");
        }

        System.setProperty("loader.main", Application.class.getCanonicalName());
        PropertiesLauncher.main(args);
    }

    @Configuration
    @Import(DataSourceAutoConfiguration.class)
    static class PluginLoader implements InitializingBean {

        private final DataSource dataSource;

        PluginLoader(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            try (final Connection connection = dataSource.getConnection();
                 final PreparedStatement pstmt = connection.prepareStatement("select * from v$version");
                 final ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next()) {
                    LOG.info("Oracle Version {} ", resultSet.getString(1));
                }
            }
        }
    }

    @Configuration
    @AutoConfigureBefore(PluginLoader.class)
    static class ThreadPoolInitializer implements InitializingBean {

        @Override
        public void afterPropertiesSet() throws Exception {
            ThreadPoolUtil.init();
        }
    }
}
