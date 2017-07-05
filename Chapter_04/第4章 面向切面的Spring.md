## 4. 面向切面的Spring

### 4.1 什么是面向切面编程

切面能帮助我们模块化横切关注点。简而言之，横切关注点可以被描述为影响应用多处的功能。例如，安全就是一个横切关注点，应用中的许多方法都会涉及到安全规则。

![切面实现了横切关注点(跨多个应用对象的逻辑)的模块化](./images/4.1-1.PNG)

每个模块的核心功能都是为特定业务领域提供服务，但是这些模块都需要类似的辅助功能，例如安全和事务管理。

如果要重用通用功能的话，最常见的面向对象技术是继承(inheritance)和委托(delegation)。但是，如果在整个应用中都使用相同的基类，继承往往会导致一个脆弱的对象体系；而使用委托可能需要对委托对象进行复杂的调用。

切面提供了取代继承和委托的另一种可选方案，而且在很多场景下更清晰简洁。在使用面向切面编程时，我们仍然需要在一个地方定义通用功能，但是可以通过声明的方式定义这个功能要以何种方式在何处应用，而无需修改受影响的类。横切关注点可以被模块化为特殊的类，这些类被称为切面(aspect)。这样做有两个好处：首先，现在每个关注点都集中在一个地方，而不是分散在多处代码中；其次，服务模块更加简洁，因为它们只包含主要关注点(或核心功能)的代码，而次要关注点的代码被转移到切面中了。

#### 4.1.1 定义AOP术语

与大多数技术一样，AOP有自己的术语。描述切面常用的术语有通知(advice)、切点([pointcut)和连接点(join point)。

![在一个或多个连接点上，可以把切面的功能(通知)织入到程序中的执行过程中](./images/4.1.1-1.PNG)

##### 通知(Advice)

切面有目标--它必须要完成的工作。在AOP术语中，切面的工作被称为通知。

通知定义了切面是什么以及何时使用。除了描述切面要完成的工作，通知还解决了何时执行这个工作的问题。它应该应用在某个方法被调用之前？之后？之前和之后都调用？还是只在方法抛出异常时调用？

Spring切面可以应用5种类型的通知：

+ 前置通知(Before)：在目标方法被调用之前调用通知功能。
+ 后置通知(After)：在目标方法完成之后调用通知，此时不会关心方法的输出是什么。
+ 返回通知(After-returning)：在目标方法成功执行之后调用通知。
+ 异常通知(After-throwing)：在目标方法抛出异常后调用通知。
+ 环绕通知(Around)：通知包裹了被通知的方法，在被通知的方法调用之前和调用之后执行自定义的行为。

#####　连接点(Join point)

应用可能有数以千计的时机需要应用通知。这些时机被称为连接点。连接点是在应用执行过程中能够插入切面的一个点。这个点可以是调用方法时、抛出异常时、甚至修改一个字段时。切面代码可以利用这些点插入到应用的正常流程之中，并添加新的行为。

##### 切点(Poincut)

一个切面并不需要通知应用的所有连接点。切点有助于缩小切面所通知的连接点的范围。

如果说通知定义了切面的"什么"和"何时"的话，那么切点就定义了"何处"。切点的定义会匹配通知所要织入的一个或多个连接点。我们通常使用明确的类和方法名称，或是利用正则表达式定义所匹配的类和方法名称来指定这些切点。有些AOP框架允许我们创建动态的切点，可以根据运行时的决策(比如方法的参数值)来决定是否应用通知。

##### 切面(Aspect)

切面是通知和切点的结合。通知和切点共同定义了切面的全部内容--它是什么，在何时和何处完成其功能。

##### 引入(Introduction)

引入允许向现有的类添加新方法或属性。例如，我们可以创建一个Auditable通知类，该类记录了对象最后一次修改时的状态。这很简单，只需一个方法，setLastModified(Date)，和一个实例变量来保存这个状态。然后，这个新方法和实例变量就可以被引入到现有的类中，从而可以在无需修改这些现有的类的情况下，让它们有新的状态和行为。

织入(Weaving)

织入是把切面应用到目标对象并创建新的代理对象的过程。切面在指定的连接点被织入到目标对象中。在目标对象的生命周期里有多个点可以进行织入：

- 编译器：切面在目标类编译时被织入。这种方式需要特殊的编译器。AspectJ的织入编译器就是以这种方式织入切面的。
- 类加载期：切面在目标类加载到JVM时被织入。这种方式需要特殊的类加载器(ClassLoader)，它可以在目标类被引入应用之前增强该目标类的字节码。
- 运行期：切面在应用运行的某个时刻被织入。一般情况下，在织入切面时，AOP容器会为目标对象动态地创建一个代理对象。Spring AOP就是以这种方式织入切面的。

#### 4.1.2 Sring对AOP的支持

并不是所有的AOP框架都是相同的，它们在连接点模型上可能有强弱之分。有些允许在字段修饰符级别应用通知，而另一些只支持与方法调用相关的连接点。它们织入切面的方式和时机也有所不同。但是，无论如何，创建切点来定义切面所织入的连接点是AOP框架的基本功能。

Spring提供了4种类型的AOP支持：

+ 基于代理的经典Spring AOP
+ 纯POJO切面
+ @AspectJ注解驱动的切面
+ 注入式AspectJ切面(适用于Spring各版本)

前三种都是Spring AOP实现的变体，Spring AOP构建在动态代理基础上，因此，Spring对AOP的支持局限于方法拦截。

##### Spring通知是Java编写的

Spring所创建的通知都是用标准的Java类来编写的，定义通知所应用的切点通常会使用注解或在Spring配置文件里采用XML来编写。

##### Spring在运行时通知对象

通过在代理类中包裹切面，Spring在运行期间把切面织入到Spring管理的bean中。

![Spring的切面由包裹了目标对象的代理类实现。代理类处理方法的调用，执行额外的切面逻辑，并调用目标方法](./images/4.1.2-1.PNG)

代理类封装了目标类，并拦截被通知方法的调用，再把调用转发给真正的目标bean。当代理拦截到方法调用时，在调用目标bean方法之前，会执行切面逻辑。

直到应用需要被代理的bean时，Spring才会创建代理对象。如果使用的是ApplicationContext，在ApplicationContext从BeanFactory中加载所有bean的时候，Spring才会创建被代理的对象。因为Spring；运行时才会创建代理对象，所以我们不需要特殊的编译器来织入Spring AOP的切面。

##### Spring只支持方法级别的连接点

因为Spring基于动态代理，所以Spring只支持方法连接点。Spring缺少对字段连接点的支持，无法创建细粒度的通知，例如拦截对象字段的修改。而且它不支持构造器连接点，我们就无法在bean创建时应用通知。

但是方法拦截可以满足绝大部分的需求。如果需要方法拦截以外的连接点拦截功能，那么可以使用AspectJ来补充。

### 4.2 通过切点来选择连接点

切点用于准确定位应该在什么地方应用切面的通知。通知和切点是切面的最基本的元素。

![Spring借助AspectJ的切点表达式语言来定义Spring切面](./iamges/4.2-1.PNG)

在Spring中尝试使用AspectJ其他指示器的时候，就会抛出IllegalArgumentException。

只有execution指示器是实际执行匹配的，而其他的指示器都是用来限制匹配的。这说明execution指示器是在编写切点定义时最主要使用的指示器。在此基础上，使用其他指示器来限制所匹配的切点。

#### 4.2.1 编写切点

```java
package springinaction.concert;

public interface Performance {
    public void perform();
}
```

Performance可以代表任何类型的现场表演，如舞台剧、电影或音乐会。假设我们想编写Performance的perform()触发的通知。以下是一个切点表达式，这个表达式能够设置当perform()执行时触发通知的调用。

![使用AspectJ切点表达式来选择Performance的perform()](./images/4.2.1-1.PNG)

我们使用execution()指示器选择Performance的perform()。方法表达式以*开始，表明我们并不关心方法返回值的类型。然后，指定全限定类名和方法名。对于方法参数列表，我们使用两个点号(..)表明切点要选择任意的perform()，无论该方法的参数是什么。

假设现在需要配置的切点仅存在concert包。在此场景下，可以使用within()指示器来限制匹配。

![使用within()指示器限制切点范围](./images/4.2.1-2.PNG)

注意：使用了&&操作符把execution()和within()指示器连接在一起形成与(and)关系。类似地，可以使用||、!。可以使用and代替&&，or代替||，not代替!。

#### 4.2.2 在切点中选择bean

Spring还引入了一个新的bean()指示器，它允许在切点表达式使用bean的ID来标识bean。bean()使用bean ID或bean名称作为参数来限制切点只匹配特定的bean。

例如：

```java
execution(* concert.Performance.perform()) and bean("woodstock")
```

在这里，希望在执行Performance的perform()应用通知，但限定bean的ID为woodstock。

在某些场景下，限定切点为指定的bean或许很有意义，但我们还可以使用非操作作为除了特定ID以外的bean应用通知：

```java
execution(* concert.Performance.perform()) and !bean("woodstock")
```

在此场景下，切面的通知会被编织到所有ID不为woodstock的bean中。

###　4.3　使用注解创建切面

#### 4.3.1 定义切面

如果一场演出没有观众的话，那不能称之为演出。从演出的角度看，观众是非常重要的，但是对演出本身的功能来讲，它并不是核心，这是一个单独的关注点。因此，将观众定义为一个切面。

```java
package springinaction.concert;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class Audience {

    @Before("execution(** springaction.concert.Performance.perform(..))")
    public void silenceCellPhones() {// 表演前
        System.out.println("Silencing cell phones");
    }

    @Before("execution(** springaction.concert.Performance.perform(..))")
    public void takeSeats() {// 表演前
        System.out.println("Taking seats");
    }

    @AfterReturning("execution(** springaction.concert.Performance.perform(..))")
    public void applause() {// 表演后
        System.out.println("CLAP CLAP CLAP!!!");
    }

    @AfterThrowing("execution(** springaction.concert.Performance.perform(..))")
    public void demandRefund() {// 表演失败后
        System.out.println("Demanding a refund");
    }
}
```

Audience类使用@AspectJ注解进行了标注。该注解表明Audience不仅仅是一个POJO，还是一个切面。Audience类中的方法都使用注解来定义切面的具体行为。

Audience有四个方法，定义了一个观众在观看演出时可能会做的事情。在演出之前，观众要就坐(takeSeats())并将手机调至静音状态(silenceCellPhones())。如果演出很精彩的话，观众应该会鼓掌喝彩(applause())。不过，如果演出没有达到观众预期，观众会要求退款(demandRefund())。

AspectJ提供了五个注解来定义通知：

![Spring使用AspectJ注解来声明通知方法](./images/4.3.1-1.PNG)

可以使用@Poincut注解能够在一个@AspectJ切面定义可重用的切点。

```java
package springinaction.concert;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class Audience {

    @Pointcut("execution(** springaction.concert.Performance.perform(..))")
    public void Performance() {

    }

    @Before("performance()")
    public void silenceCellPhones() {// 表演前
        System.out.println("Silencing cell phones");
    }

    @Before("performance()")
    public void takeSeats() {// 表演前
        System.out.println("Taking seats");
    }

    @AfterReturning("performance()")
    public void applause() {// 表演后
        System.out.println("CLAP CLAP CLAP!!!");
    }

    @AfterThrowing("performance()")
    public void demandRefund() {// 表演失败后
        System.out.println("Demanding a refund");
    }
}
```

performance()使用了@Pointcut注解。为@Poincut注解设置的值是一个切点表达式，就像之前在通知注解上所设置的那样。通过在performance()上添加@Poincut注解，我们实际上扩展了切点表达式语言，这样就可以在任何的切点表达式中使用performance()了，如果不这样做，需要在这些地方使用更长的切点表达式。

performance()的时机内容并不重要，其实该方法本身只是一个标识，供@Poincut注解依附。

在JavaConfig中启用AspectJ注解的自动代理：

```java
package springinaction.concert;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan
@EnableAspectJAutoProxy // 启用AspectJ自动代理
public class ConcertConfig {

    @Bean
    public Audience audience() {// 声明Audience bean
        return new Audience();
    }
}
```

AspectJ自动代理都会为使用@AspectJ注解的bean创建一个代理，这个代理会围绕着所有该切面的切点所匹配的bean。在这种情况下，将会为Concert bean创建一个代理，Audience类中的通知方法将会在perform()调用前后执行。

#### 4.3.2 创建环绕通知

环绕通知是最强大的通知类型。它能够让你所编写的逻辑将被通知的目标方法完全包装起来。实际上就像在一个通知方法中同时编写前置通知和后置通知。

```java
package springinaction.concert;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class Audience {

    @Pointcut("execution(** springaction.concert.Performance.perform(..))")
    public void Performance() {

    }

    @Around("performance")
    public void watchPerformance(ProceedingJoinPoint jp) {
        try {
            System.out.println("Silencing cell phones");
            System.out.println("Taking seats");
            jp.proceed();
            System.out.println("CLAP CLAP CLAP");
        } catch (Throwable e) {
            System.out.println("Demanding a refund");
        }
    }
}
```

@Around注解表明watchPerformance()会作为performance()切点的环绕通知。在这个通知中，观众在演出之前会将手机调至静音并就坐，演出结束后会鼓掌喝彩。演出失败，观众会要求退款。

这个通知所达到的效果与之前的前置通知和后置通知是一样的。但是，现在位于同一个方法中，不像之前那样分散在四个不同的通知方法中。

需要注意的是，它接受ProccedingJointPoint作为参数。这个对象必须要有的，因为你要在通知中通过它来调用被通知的方法。通知方法中可以做任何的事情，当要将控制权交给被通知的方法时，它需要调用ProceedingJoinPoint的proceed()。

如果不调用proceed()，那么通知实际上会阻塞对被通知方法的调用。

有意思的是，你可以不调用proceed()，从而阻塞对被通知方法的访问，与之类似，你也可以在通知中对它进行多次调用。要这样做的一个场景就是实现重试逻辑，也就是在被通知方法失败后，进行重复尝试。

#### 4.3.3 处理通知中的参数

如果切面所通知的方法确实有参数怎么办？切面能访问和使用传递给被通知方法的参数吗？

假设想要统计电影院播放的某部电影的次数。一种方法就是修改perform()，直接在每次播放的使用记录这个数量。但是，记录电影的播放次数与播放本身是不同的关注点，因此不应该属于perform()。这应该是切面完成的工作。

```java
package springinaction.concert;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class PerformanceCounter {

    private Map<String, Integer> performanceCounts = new HashMap<String, Integer>();

    @Pointcut("execution(* springaction.concert.Cinema.perform(String)) " + "&& args(name)") // 通知perform()
    public void performed(String name) {

    }

    @Before("performed(name)")
    public void countPerform(String name) {
        Integer curCount = getPlayCount(name);
        performanceCounts.put(name, curCount + 1);
    }

    private Integer getPlayCount(String name) {
        return performanceCounts.containsKey(name) ? performanceCounts.get(name) : 0;
    }
}
```

需要关注的是切点表达式中的args(name)限定符。它表明传递给perform()方法的int类型参数也会传递到通知中去。参数的名称name也与切点方法签名中的参数相匹配。

这个参数会传递到通知方法中，这个通知方法是通过@Before注解和命名切点performed(name)定义的。切点定义中的参数与切点方法中的参数名称是一样的，这样就完成了从命名切点到通知方法的参数转移。

```java
package springinaction.concert;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class PerformanceCounterConfig {

    @Bean
    public Performance cinema() {
        return new Cinema();
    }

    public PerformanceCounter counter() {
        return new PerformanceCounter();
    }
}
```

测试：

```java
package springinaction.concert;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PerformanceCounterConfig.class)
public class PerformanceCounterTest {

    @Autowired
    private Performance performance;

    @Autowired
    private PerformanceCounter counter;

    @Test
    public void test() {
        performance.perform("当幸福来敲门");
        performance.perform("当幸福来敲门");
        performance.perform("当幸福来敲门");
        performance.perform("摩登保镖");
        performance.perform("当幸福来敲门");
        performance.perform("当幸福来敲门");

        assertEquals(5, counter.getPlayCount("当幸福来敲门"));
        assertEquals(0, counter.getPlayCount("赌神"));
        assertEquals(1, counter.getPlayCount("摩登保镖"));
    }

}
```