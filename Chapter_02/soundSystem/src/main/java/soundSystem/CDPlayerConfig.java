package soundSystem;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CDPlayerConfig {

	@Bean
	public CompactDisc sgtPeppers() {
		return new SgtPeppers();
	}

	@Bean
	public CDPlayer cdPlayer() {
		return new CDPlayer(sgtPeppers());
	}

	public CDPlayer anotherPlayer() {
		return new CDPlayer(sgtPeppers());
	}

	// @Bean
	// public CompactDisc randomCD() {
	// int choice = (int) Math.floor(Math.random() * 3);
	// if (choice == 0) {
	// return new SgtPeppers();
	// } else if (choice == 1) {
	// return new Actor();
	// } else {
	// return new LongTimeNoSee();
	// }
	// }
}
