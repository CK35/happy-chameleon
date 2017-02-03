package de.ck35.raspberry.happy.chameleon;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import com.pi4j.io.gpio.GpioFactory;

import de.ck35.raspberry.happy.chameleon.configuration.GpioConfiguration;
import de.ck35.raspberry.happy.chameleon.configuration.RootConfiguration;

public class Application extends SpringApplication {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public Application() {
        super(RootConfiguration.class);
    }
    
    public static void main(String[] args) {
        new Application().run(args);
    }

    @Override
    protected void postProcessApplicationContext(ConfigurableApplicationContext context) {
        super.postProcessApplicationContext(context);
        try {
            context.getBeanFactory().registerSingleton("gpioController", GpioFactory.getInstance());
            context.getEnvironment().addActiveProfile(GpioConfiguration.PROFILE);
            LOG.info("GpioConfiguration profile activated.");
        } catch (UnsatisfiedLinkError e) {
            LOG.warn("Could not create gpioController. Skipping further gpio configuration.");
        }
        
        configureDatasourceUrl(context.getEnvironment());
    }
    
    public static void configureDatasourceUrl(ConfigurableEnvironment env) {
        if(env.containsProperty("spring.datasource.url")) {
            return;
        }
        Properties databaseProperties = new Properties();
        String databaseFile = env.getProperty("application.database.file");
        StringBuilder databaseUrl = new StringBuilder();
        if(databaseFile == null) {
            LOG.info("Using in memory database without persistence.");
            databaseUrl.append("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1;TRACE_LEVEL_SYSTEM_OUT=3;");
        } else {
            databaseUrl.append("jdbc:h2:file:").append(databaseFile).append(";");
        }
        databaseUrl.append("MODE=MSSQLServer;").append("INIT=RUNSCRIPT FROM 'classpath:create_schema.sql'");
        databaseProperties.put("spring.datasource.url", databaseUrl);
        
        env.getPropertySources().addFirst(new PropertiesPropertySource("databaseProperties", databaseProperties));
    }
    
}