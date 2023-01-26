package msftcse.SpringKeyvault;

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
	}

}
