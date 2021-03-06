package de.adorsys.psd2.sandbox;

import de.adorsys.psd2.sandbox.migration.MigrationRunner;
import de.adorsys.psd2.sandbox.portal.PortalConfig;
import de.adorsys.psd2.sandbox.xs2a.Xs2aConfig;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * Parent Spring Context which only does feature configuration.
 */
@Configuration
@PropertySource(
    value = {
        "classpath:sandbox-application.properties",
        "classpath:sandbox-application-${spring.profiles.active}.properties"
    },
    ignoreResourceNotFound = true
)
@EnableAutoConfiguration(exclude = {
    // TODO no persistence for now
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
    // TODO disable security for now
    SecurityAutoConfiguration.class,
    ManagementWebSecurityAutoConfiguration.class
})
@Import(ContextHolder.class)
public class SandboxApplication {

  /**
   * Starts our spring boot app.
   *
   * @param args CLI args
   */
  public static void main(String[] args) {
    if (args.length != 0 && args[0].matches("migrate|generate-schema")) {
      new SpringApplicationBuilder()
          .parent(MigrationRunner.class).web(false)
          .run(args);
    } else {
      new SpringApplicationBuilder()
          .parent(SandboxApplication.class).web(false)
          .child(PortalConfig.class).listeners(new StartFailedListener()).web(true)
          .sibling(Xs2aConfig.class).listeners(new StartFailedListener()).web(true)
          .run(args);
    }
  }

  private static class StartFailedListener implements ApplicationListener<ApplicationFailedEvent> {

    @Override
    public void onApplicationEvent(ApplicationFailedEvent applicationFailedEvent) {
      SpringApplication.exit(applicationFailedEvent.getApplicationContext().getParent(),
          (ExitCodeGenerator) () -> 1);
    }
  }
}
