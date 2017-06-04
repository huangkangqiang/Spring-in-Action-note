package sia.knights;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import sia.knights.config.KnightConfig;

public class KnightMain {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = // 加载Spring上下文
				new AnnotationConfigApplicationContext(KnightConfig.class);
		Knight knight = context.getBean(Knight.class); // 获取Knight bean
		knight.embarOnQuest(); // 使用Knight的方法
		context.close();
	}

}
