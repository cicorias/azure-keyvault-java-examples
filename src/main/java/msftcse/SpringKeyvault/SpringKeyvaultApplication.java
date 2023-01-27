package msftcse.SpringKeyvault;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringKeyvaultApplication
		implements CommandLineRunner {

	private static Logger LOG = LoggerFactory
			.getLogger(SpringKeyvaultApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringKeyvaultApplication.class, args);
	}

	@Override
	public void run(String... args) {
		LOG.info("EXECUTING : command line runner");

		for (int i = 0; i < args.length; ++i) {
			LOG.info("args[{}]: {}", i, args[i]);
		}
		Properties properties = System.getProperties();
        properties.forEach((k, v) -> System.out.println(k + ":" + v));

		var environment = System.getenv();
		environment.forEach((k, v) -> System.out.println(k + ":" + v));
		
	}

}
