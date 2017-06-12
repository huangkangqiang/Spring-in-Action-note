package soundSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:/soundSystem/app.properties")
public class ExpressiveConfig {

	@Autowired
	Environment environment;

	@Bean
	public BlankDisc disc() {
		return new BlankDisc(environment.getProperty("disc.title", "Rattle and Hum"),
				environment.getProperty("disc.artist", "U2"));
	}
}
