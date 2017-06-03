package sia.knights;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import sia.knights.config.KnightConfig;

public class KnightMain {

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(KnightConfig.class);
	}

}
