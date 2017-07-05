## 2.0 装配Bean

在Sping中，对象无需自己查找或创建与其所关联的其他对象。相反，容器负责把需要相互协作的对象引用赋予各个对象。例如，一个订单管理组件需要信用卡认证组件，但它不需要自己创建信用卡认证组件。订单管理组件只需要表明自己两手空空，容器就会主动赋予它一个信用卡认证组件。

>创建对象之间协作关系的行为通常称为装配(wiring)，这也是依赖注入的本质。

### 2.1 Spring配置的可选方案

Spring具有非常大的灵活性，它提供了三种主要的装配机制：

+ 在XML中进行显式配置
+ 在Java中进行显式配置
+ 隐式的bean发现机制和自动装配

>显示配置越少越好。当必须要显式配置bean的时候（比如，有些源码不是由你来维护的，而当你需要为这些代码配置bean的时候），推荐使用类型安全并且比XML更加强大的JavaConfig。最后，只有想要使用便利的XML命名空间，并且在JavaConfig没有同样的实现时，才应该使用XML。

### 2.2 自动化装配bean

>尽管你会发现显示装配技术非常有用，但是在便利性方面，最强大的还是Spring的自动化配置。如果Spring能够自动化装配的话，那何苦还要显示地讲这些bean装配在一起呢？

Spring从两个角度来实现自动化装配：

+ 组件扫描(component scanning)：Spring会自动发现应用上下文所创建的bean
+ 自动装配(autowiring)：Spring自动满足bean之间的依赖

>组件扫描和自动装配组合在一起就能发挥出强大的威力，它们能够将你的显式配置降低到最少。

为了阐述组件扫描和自动装配，我们需要创建几个bean，它们代表了一个音响系统中的组件。首先，要创建CompactDisc类，Spring会自动发现它并将其创建为一个bean。然后，会创建一个CDPlayer类，让Spring发现它，并将CompactDisc bean注入进来。

#### 2.2.1 创建可被发现的bean

如果不将CD(compact disc)插入(注入)到CD播放器中，那么CD播放器其实是没有太大用处的。所以，可以这样说，CD播放器依赖于CD才能完成它的使命。

```java
package soundSystem;

public interface CompactDisc {
    void play();
}
```

CompactDisc的具体内容并不重要，重要的是你将其定义为一个接口。作为接口，它定义了CD播放器对一盘CD所能进行的操作。它将CD播放器的任意实现与CD本身的耦合降低了最小的程度。

我们还需要一个CompactDisc的实现，实际上，我们可以有CompactDisc接口的多个实现。

```java
package soundSystem;

import org.springframework.stereotype.Component;

@Component
public class SgtPeppers implements CompactDisc {

    private String title = "Sgt. Pepper's Lonely Hearts Club Band";
    private String artist = "The Beatles";

    @Override
    public void play() {
        System.out.println("Playing " + title + " by " + artist);
    }

}
```

和CompactDisc接口一样，SgtPeppers的具体内容并不重要。需要注意的是SgtPeppers类上使用了@Component注解。这个简单的注解表明该类会作为组件类，并告知Spring要为这个类创建bean。没有必要显式配置SgtPeppers bean，因为这个类使用了@Component注解，所以Spring会为你把事情处理妥当。

>组件扫描默认是不启用的。我们还需要显式配置一下Spring，从而命令它去寻找带有@Component注解的类，并为其创建bean。

```java
package soundSystem;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class CDPlayerConfig {

}
```

类CDPlayerConfig通过Java代码定义了Spring的装配规则。CDPlayerConfig类并没有显式地声明任何bean，只不过它使用了@ComponentScan注解，这个注解能够在Spring中启用组件扫描。

>如果没有其他配置的话，@ComponentScan默认会扫描与配置类相同的包以及这个包下的所有子包，查找带有@Component注解的类。

创建一个简单的JUnit测试，它会创建Spring上下文，并判断CompactDisc是否真的创建出来了。

```java
package soundSystem;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=CDPlayerConfig.class)
public class CDPlayerTest {

    @Autowired
    private CompactDisc cd;
    
    @Test
    public void cdShouldNotBeNull() {
        assertNotNull(cd);
    }

}
```

CDPlayerTest使用了Spring的SpringJUnit4ClassRunner，以便在测试开始的时候自动创建Spring的应用上下文。注解@ContextConfiguration会告诉它需要在CDPlayerConfig中加载配置。

在测试代码中有一个CompactDisc类型的属性，并且这个属性带有@Autowired注解，以便于将CompactDisc bean注入到测试代码中。最后，会有一个简单的测试方法断言cd属性不为null。如果它不为null的话，就意味着Spring能够发现CompactDisc类，自动在Spring上下文中将其创建为bean并将其注入到代码中来。

>在soundSystem包及其子包中，所有带有@Component注解的类都会被创建为bean。只添加一行@component注解就能自动创建无数个bean，这种权衡还是很划算的。

#### 2.2.2 为组件扫描的bean命名

Spring应用上下文中所有的bean都会给定一个ID。在前面的例子中，尽管我们没有明确地为SgtPeppers bean设置ID，但Spring会根据类名为其指定一个ID。具体来讲，这个bean所给定的ID为sgtPeppers，也就是将类名的第一个字母变为小写。

>如果想为这个bean设置不同的ID，只需要将期望的ID值传递给@Component注解。

```java
@Component("lonelyHeartClub")
public class SgtPeppers implements CompactDisc {
    ........
}
```

>还有另一种为bean命名的方式，这种方式不使用@Component注解，而是使用Java依赖注入规范(Java Dependency Injection)中所提供的@Named注解来为bean设置ID

```java
@Named("lonelyHeartClub")
public class SgtPeppers implements CompactDisc {
    ........
}
```

Spring支持将@Named作为@Component注解的替代方案。两者之间有一些细微的差异，但是在大多数场景中，它们是可以互相替换的。

>个人觉得@Component语义性强一点。

#### 2.2.3 设置组件扫描的基础包

按照默认规则，@Component注解会以配置类所在的包作为基础包(base package)来扫描组件。

>但是，如果想要扫描不同的包，或者扫描多个基础包，又或者是遇到配置类放在单独的包中，使其与其他的应用代码区分开来，那应该怎么配置？

为了指定不同的基础包，需要做的就是在@ComponScan的value属性中指明包的名称：

```java
@Configuration
@ComponentScan("soundSystem")
public class CDPlayerConfig{}
```

如果想要更加明确所设置的是基础包，那么可以通过basePackages属性进行配置：

```java
@Configuration
@ComponentScan(basePackages="soundSystem")
public class CDPlayerConfig{}
```

basePackages属性使用的是复数形式，可以设置多个基础包，只需要将basePackages属性设置为要扫描包的一个数组即可：

```java
@Configuration
@ComponentScan(basePackages={"soundSystem","video"})
public class CDPlayerConfig{}
```

上例中，基础包是以String类型表示的，这种方法是类型不安全(not type-safe)的。如果重构代码的时候，那么所指定的基础包可能就会出现错误了。

>除了将包设置为简单的String类型之外，@ComponentScan还提供了另外一种方法，那就是将其指定为包中所包含的类或接口：

```java
@Configuration
@ComponentScan(basePackageClasses={CDPlayer.class,DVDPlayer.class})
public class CDPlayerConfig{}
```

可以看到，basePackages属性被替换成了basePackageClasses。同时，不再使用String类型的名称来指定包，为basePackageClasses属性所配置的数组中包含了类。这些类所在的包将会作为组件扫描的基础包。

>尽管在样例中，为basePackageClasses设置的是组件类，但是可以在包中创建一个用来进行扫描的空标记接口。通过标记接口的方式，依然能够保持对重构友好的接口引用，但是可以避免引用任何实际的应用程序代码（因为，可能在重构的时候，这些应用代码可能会从想要扫描的包中删除）。

在应用程序中，如果所有的对象都是独立的，彼此之间都没有任何依赖，那么所需要的就是组件扫描了。但是，很多对象会依赖其他的对象才能完成任务。这样的话，我们就需要有一种方法能够将组件扫描得到的bean和它们的依赖装配在一起。

#### 2.2.4 通过为bean添加注解实现自动装配

简单来说，自动装配就是让Spring自动满足bean依赖的一种方法，在满足依赖的过程中，会在Spring应用上下文中寻找匹配某个bean需求的其他bean。为了声明要进行自动装配，可以借助Spring的@Autowired注解。

```java
package soundSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CDPlayer implements MediaPlayer {

    private CompactDisc cd;

    @Autowired
    public CDPlayer(CompactDisc cd) {
        this.cd = cd;
    }

    @Override
    public void play() {
        cd.play();
    }

}
```

CDPlayer类的构造器上添加了@Autowired注解，这表明当Spring创建CDPlayer bean的时候，会通过这个构造器进行实例化并传入一个可设置给成CompactDisc类型的bean。

@Autowired注解不仅能够用在构造器上，还能用在属性的Setter方法上。比如说，如果CDPlayer有一个setCompactDisc()，那么就可以采用如下的注解形式进行自动装配：

```java
@Autowired
public vodi setCompactDisc(CompactDisc cd) {
    this.cd = cd;
}
```

实际上，@Autowired注解可以用在类的任何方法上。假设，CDPlayer类有一个insertDisc()：

```java
@Autowired
public vodi insertDisc(CompactDisc cd) {
    this.cd = cd;
}
```

>不管是构造器、Setter方法还是其他的方法，Spring都会尝试满足方法参数上所声明的依赖。加入有且只有一个bean匹配依赖需求的话，那么这个bean就会被装配进来。

如果没有匹配的bean，那么在应用上下文创建的时候，Spring会抛出一个异常。为了避免异常的出现，你可以将@Autowired的required属性设置为false：

```java
@Autowired(required = false)
public CDPlayer(CompactDisc cd) {
    this.cd = cd;
}
```

将required属性设置为false，Spring会尝试执行自动装配，但是如果没有匹配的bean的时候，Spring将会让这个bean处于未装配的状态。但是，如果代码中没有进行null检查的话，这个处于未装配状态的属性有可能会出现NullPointerException。

>如果有多个bean都能满足依赖关系的话，Spring将会抛出一个异常，表明没有明确指定要选择哪个bean进行自动装配。自动装配中的歧义性。

@Inject注解来自Java依赖注入规范，@Inject和@Autowired之间有着细微差别，但是在大多数场景下，两者是可以替换的。

>还是那句话，@Autowired语义性更强。

#### 2.2.5 验证自动装配

```java
package soundSystem;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CDPlayerConfig.class)
public class CDPlayerTest {

    @Rule
    public final StandardOutputStreamLog log = new StandardOutputStreamLog();

    @Autowired
    private MediaPlayer player;

    @Autowired
    private CompactDisc cd;

    @Test
    public void cdShouldNotBeNull() {
        assertNotNull(cd);
    }

    @Test
    public void testPlay() {
        player.play();
        assertEquals("Playing Sgt. Pepper's Lonely Hearts Club Band by The Beatles\n", log.getLog());
    }

}
```

貌似单元测试不对。。比较的值明明是正确的。。。

>找不到哪里出错了，所以修改了下例子。。。有时间再慢慢拿时间跟它耗，再更正了。。。

### 2.3 通过代码装配bean

>尽管在很多场景下通过组件扫描和自动装配实现Spring的自动化配置是更为推荐的方式，但有时候，自动化配置的方案行不通，因此需要明确配置Spring。比如说，你想要将第三方库中的组件装配到你的应用中，在这种情况下，是没有办法在它的类上添加@Component和@Autowired注解的，因此就不能使用自动化装配的方案了。

在这种情况下，你必须采用显示装配的方式。显式配置有两种可选方案：Java和XML。

在进行显式配置的时候，JavaConfig是更好的方案，因为它更强大、类型安全并且对重构友好。因为它就是Java代码，就像应用程序中的其他Java代码一样。

同时，JavaConfig与其他Java代码的又有所区别。在概念上讲，它与应用程序中的业务逻辑和领域代码是不同的。尽管它与其他的组件一样都是用相同语言进行表述，但JavaConfig是配置代码。这意味着它不应该包含任何业务逻辑，JavaConfig也不应该侵入到业务逻辑代码中。

>尽管不是必须的，但通常将JavaConfig放到单独的包中，使它与其他的应用程序逻辑分离开来，这样对于它的意图就不会产生困惑了。

#### 2.3.1 创建配置类

```java
package soundSystem;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CDPlayerConfig {

}
```

创建JavaConfig类的关键在于为其添加@Configuration注解，@Configuration注解表明这是一个配置类，该类应该包含在Spring应用上下文中如何创建bean的细节。

到这里，都是依赖组件扫描发现Spring应该创建的bean。尽管可以同时使用组件扫描和显式配置，但是我们先关注于显式配置，因此把@ComponentScan注解去掉了。

去掉@ComponentScan注解之后，CDPlayerConfig类就没有任何作用了。运行测试，会发现出现异常BeanCreationExcption。测试的时候希望注入CDPlayer和CompactDisc，但是这些bean根本就没有创建，因为组件扫描不会发现它们。

所以我们要在JavaConfig中声明bean。

#### 2.3.2 声明简单的bean

要在JavaConfig中声明bean，需要编写一个方法，这个方法会创建所需类型的实例，然后给这个方法添加@Bean注解。

```java
package soundSystem;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CDPlayerConfig {

    @Bean
    public CompactDisc sgtPeppers() {
        return new SgtPeppers();
    }
}
```

@Bean注解会告诉Spring这个方法将会返回一个对象，该对象注册为Spring应用上下文中的bean。方法体中包含了最终产生bean实例的逻辑。

默认情况下，bean的ID与带有@Bean注解的方法名一样。但是可以通过name属性指定一个不同的名字：

```java
    @Bean(name = "lonelyHeartsClubBand")
    public CompactDisc sgtPeppers() {
        return new SgtPeppers();
    }
```

在一组CD中随机选择一个CompactDisc来播放：

```java
    @Bean
    public CompactDisc randomCD() {
        int choice = (int) Math.floor(Math.random() * 3);
        if (choice == 0) {
            return new SgtPeppers();
        } else if (choice == 1) {
            return new Actor();
        } else {
            return new LongTimeNoSee();
        }
    }
```

#### 2.3.3 借助JavaConfig实现注入

上例中，声明的CompactDisc bean是非常简单的，它自身没有其他的依赖。但现在，需要声明CDPlayer bean，它依赖于CompactDisc。

在JavaConfig中装配bean的最简单方式就是引用创建bean的方法。

```java
    @Bean
    public CDPlayer cdPlayer(){
        return new CDPlayer(sgtPeppers());
    }
```

cdPlayer()像sgtPeppers()一样，同样使用了@Bean注解，这表明这个方法会创建一个bean实例并将其注册到Spring应用上下文中。所创建的bean ID为cdPlayer，与方法的名字相同。

cdPlayer()的方法体与sgtPeppers()稍微有些区别。在这里并没有默认的构造器构建实例，而是调用了需要传入CompactDisc对象的构造器来创建CDPlayer实例。

看起来，CompactDisc是通过调用sgtPeppers()得到的，但是情况并非完全如此。因为sgtPeppers()添加了@Bean注解，Spring将会拦截所有对它的调用，并确保直接返回该方法所创建的bean，而不是每次都对其进行实际的调用。

```java
    @Bean
    public CDPlayer cdPlayer() {
        return new CDPlayer(sgtPeppers());
    }

    public CDPlayer anotherPlayer() {
        return new CDPlayer(sgtPeppers());
    }
```

假如对sgtPeppers()的调用就像其他的Java方法调用一样的话，那么每个CDPlayer实例都会有一个特有的SgtPeppers实例。

但是，默认情况下，Spring中的bean都是单例的，我们并没有必要为第二个CDPlayer bean创建完全相同的SgtPeppers实例。所以，Spring会拦截对sgtPeppers()的调用并确保返回的是Spring所创建的bean，也就是Spring本身在调用sgtPeppers()所创建的CompactDisc bean。因此，两个CDPlayer bean得到相同的SgtPeppers实例。

通过调用方法来引用bean的方式有点令人困惑，还有一种理解起来更为简单的方式：

```java
    @Bean
    public CDPlayer cdPlayer(CompactDisc cd) {
        return new CDPlayer(cd);
    }
```

可以看到，cdPlayer()请求一个CompactDisc作为参数。当Spring调用cdPlayer()创建CDPlayer bean的时候，它会自动装配一个CompactDisc到配置方法之中。然后，方法体就可以按照合适的方式来使用它。借助这种技术，cdPlayer()也能够将CompactDisc注入到CDPlayer的构造器中，而且不用明确引用CompactDisc的@Bean方法。

通常这种方式引用其他的bean是最佳的选择，因为它不会要求将CompactDisc声明到一个配置类中。在这里甚至没有要求CompactDisc必须要在JavaConfig中声明，实际它可以通过组件扫描功能自动发现或者通过XML来进行配置。你可以将配置分散到多个配置类、XML文件以及自动扫描和装配bean之中，只要功能完整健全即可。不管CompactDisc是采用什么方式创建出来的，Spring都会将其传入到配置方法中，并用来创建CDPlayer bean。