## 1. Spring之旅

### 1.1 简化Java开发

`Spring`是一个开源框架，最早是由`Rod Johnson`创建。Spring是为了解决企业级应用开发的复杂性而创建的，使用Spring可以让简单JavaBean实现之前只有EJB才能完成的事情。

但Spring不仅仅局限于服务器端开发，`任何Java应用都能在简单性、可测试性和松耦合等方面从Spring中获益`。

Spring的最根本使用是：`简化Java开发`。

为了降低Java开发的复杂性，Spring采取了一下4中关键策略：

+ 基于POJO的`轻量级`和`最小侵入性`（[入侵式和非侵入式编程的区别](http://www.myexception.cn/program/614076.html)）编程
+ 通过`依赖注入`和`面向接口`实现`松耦合`
+ 基于`切面`和`惯例`进行`声明式编程`
+ 通过`切面`和`模板`减少样式代码

#### 1.1.1 激发POJO的潜能

虽然Spring用`bean`或`JavaBean`来表示应用组件，但不意味着Spring组件必须要遵循`JavaBean规范`。一个Spring组件可以是任何形式的`POJO`（
[JAVABEAN EJB POJO区别](http://www.cnblogs.com/yw-ah/p/5795751.html)）。

很多框架通过强迫应用继承它们的类或实现它们的接口从而导致应用于框架绑死。这种`侵入式`的编程方式在早期版本的`Struts`、`WebWork`等其他Java规范和框架中都能看到。

Spring竭力避免因自身的API而弄乱应用代码。Spring不会强迫开发者实现Spring规范的接口或继承Spring规范的类，相反，在基于Spring构建的应用中，它的类通常没有任何痕迹表明使用了Spring。最坏的场景是，一个类或许会使用Spring注解，但它依旧是POJO。

如下例：

```java
public class HelloWorldBean{
    public String sayHello(){
        return "Hello World";
    }
}
```

可以看到，这是一个简单普通的Java类--`POJO`。没有任何地方表明它是一个Spring组件。Spring的`非侵入式编程模型`意味着这个类在Spring应用和非Spring应用中都可以发挥同样的作用。

尽管形式看起来很简单，但是`POJO`一样可以具有魔力。Spring赋予`POJO`魔力的方式之一就是通过`DI`来装配它们。

#### 1.1.2 依赖注入

在项目中使用`DI`，代码变得异常简单并且更容易理解和测试。

任何一个具有实际意义的应用都会由`两个或者更多的类`组成，这些类之间进行写作来完成特定的业务逻辑。按照传统做法，每个对象负责管理与自己相互协作的对象（即它所依赖的对象）的引用，这将会导致高度耦合和难以测试的代码。

如下例：

```java
package sia.knights;

public class DamselRescuingKnight implements Knight {

    private RescueDamselQuest quest;

    public DamselRescuingKnight() {
        this.quest = new RescueDamselQuest();// 与ResuceDamselQuest紧耦合
    }

    @Override
    public void embarOnQuest() {
        quest.embark();
    }

}
```

可以看到，`DamselRescuingKnight`在它的构造函数中自行创建了`RescueDamselQuest`。这使得DameselRescuingKnight与RescueDamselQuest`紧耦合`，这极大的`限制`了这个骑士执行探险的能力，仅仅只有救援少女的能力，不具备其他能力了。

更糟糕的是，为这个DamselRescuingKnight编写`单元测试将出奇困难`。在这样的测试中，必须保证当骑士的embarkOnQuest()被调用的时候，探险的embark()方法也要被调用。

耦合具有`两面性`。

+ 紧密耦合的代码`难以测试`、`难以复用`、`难以理解`，并且典型地出现`“打地鼠”`的bug特征（修复一个bug，将会出现一个或者更多新的bug）。
+ 一定程度耦合是`必须的`。完全没有耦合的代码什么也做不了。

为了完成具有实际意义的功能，不同的类必须以适当的方式进行交互。总而言之，`耦合是必须的，但应当被小心谨慎地管理`。

通过`DI`，对象的依赖关系将有系统中负责协调各对象的第三方组件在创建对象的时候进行设定。对象无需自行创建或管理它们的依赖关系。

如下图，依赖关系将自动注入到需要它们的对象当中去。

![DI](./images/1.1.2-1.PNG)

```java
package sia.knights;

public class BraveKnight implements Knight {

    private Quest quest;

    public BraveKnight(Quest quest) {// Quest注入
        this.quest = quest;
    }

    @Override
    public void embarOnQuest() {
        quest.embark();
    }

}
```

不同于之前的DamselRescuingKnighy，BraveKnight没有自行创建探险任务，而是在构造的时候把探险任务作为构造器参数传入。这是依赖注入的方式之一，即`构造器注入(constructor injection)`。

更重要的是传入的探险类型是`Quest`，也就是所有探险任务都必须实现的一个`接口`。所以，BraveKnight能够相应RescueDamselQuest，SlayDragonQuest等`任意的Quest实现`。

这里的要点是BraveKnight没有与任何特定的Quest实现发生`耦合`。对于BraveKnight而言，被要求挑战的探险任务只要实现了Quest`接口`，那么具体是哪种类型的探险就无关紧要了。这就是DI带来的最大收益--`松耦合`。

>如果一个对象只通过接口（而不是具体实现或初始化过程）来表明依赖关系，那么这种依赖就能够在对象毫不知情的情况下，用不同的具体实现进行替换。

对依赖进行替换的一个最常用方法就是在测试的时候使用`mock`实现。我们无法充分地测试DamselRescuingKnight，因为它是紧耦合的。但是可以轻松地测试BraveKnight，只需要给它一个Quest的mock实现即可。

```java
package sia.knights;

import static org.mockito.Mockito.*;

import org.junit.Test;

public class BraveKnightTest {

    @Test
    public void testEmbarOnQuest() {
        Quest mockQuest = mock(Quest.class);// Mock一个Quest
        BraveKnight knight = new BraveKnight(mockQuest);// 将Mock出来的Quest注入
        knight.embarOnQuest();
        verify(mockQuest, times(1)).embark();
    }

}
```

可以使用mock框架`Mcokito去`创建一个Quest接口的mock实现。通过这个mock对象，就可以创建一个新的BraveKnight实例，并通过`构造器注入`这个mock Quest。当调用embarkOnQuest()时，可以使用Mockito框架验证Quest的mock实现的embark()仅仅被调用了一次。

现在BraveKnight类可以接受传递任意一种Quest实现，但如何把特定的Quest实现传递给它呢？
假设，希望BraveKnight要进行的探险任务是杀死一只怪龙。

```java
package sia.knights;

import java.io.PrintStream;

public class SlayDragonQuest implements Quest {

    private PrintStream printStream;

    public SlayDragonQuest(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public void embark() {
        printStream.println("Embarking on quest to slay the dragon!");
    }

}
```

可以看到，SlayDragonQuest实现了Quest接口，这样就可以注入到BraveKnight中去了。SlayDragonQuest没有使用System.out.println()，而是在构造方法中请求一个更为通用的PrintStream。这里最大的问题是，我们如何将SlayDragonQuest交给BraveKnight？又如何将PrintStream交个SlayDragonQuest？

创建应用组件之间协作的行为通常称为`装配（wiring）`。

Spring有多种装配方式：

+ 基于XML配置
+ 基于注解配置
+ 基于Java类配置

[三种装配Bean方式比较](http://www.cnblogs.com/JsonShare/p/4630088.html)

以下是基于Java类配置：

```java
package sia.knights.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import sia.knights.BraveKnight;
import sia.knights.Knight;
import sia.knights.Quest;
import sia.knights.SlayDragonQuest;

@Configuration
public class KnightConfig {

    @Bean
    public Knight knight() {
        return new BraveKnight(quest());// 注入Quest
    }

    @Bean
    public Quest quest() {
        return new SlayDragonQuest(System.out);// 创建SlayDragonQuest
    }
}
```

在这里，BraveKnight和SlayDragonQuest被声明为spring中的bean。就BraveKnight而言，它在构造时传入了对SlayDragonQuest bean的引用，将其作为构造器参数。将System.out(这是一个PrintStream)传入到了SlayDragonQuest的构造器中。

尽管BraveKnight依赖于Quest，但是它并不知道传递给它的是什么类型的Quest，也不知道这个Quest来自哪里。与之类似，SlayDragonQuest依赖于PrintStream，但是在编码时它并不需要知道这个PrintStream是什么样子的。

>只有Spring通过它的配置，能够了解这些组成部分是如何装配起来的。这样的话，就可以在不改变所依赖的类的情况下，修改依赖关系。

如何工作？

Spring通过`应用上下文(Application Context)`装载bean的定义并把它们组装起来。Spring应用上下文全权负责对象的创建和组装。Spring自带了多种应用上下文的实现，它们之间主要的区别就是在于如何加载配置。

```java
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
```

main方法基于KnightConfig创建了Spring应用上下文。随后它调用该应用上下文获取一个`ID`为knight的bean。得到Knight对象的引用后，就可以调用方法。

>注意这个类完全不知道我们的英雄其实接收哪种探险任务，而且完全没有意识到这是由BraveKnight来执行的。只有KnightConfig文件知道哪个其实执行哪种探险任务。

#### 1.1.3 应用切面

DI能够让相互协作的软件组件保持`松耦合`，而`面向切面编程(aspect-oriented programming,AOP)`允许把遍布应用各处的功能分离出来形成`可重用`的组件。

面向切面编程往往被定义为促使软件系统实现关注点的分离的一项技术。系统由许多不同的组件组成，每一个组件各负责一块特定功能。除了实现自身核心的功能外，这些组件还经常承担着额外的职责。诸如日志、事务管理和安全这样的系统服务经常融入到自身具有核心业务逻辑的组件中去，这些系统服务通常被称为横切关注点，因为它们会跨越系统的多个组件。

如果将这些关注点分散到多个组件中去，代码会带来双重的复杂性。

+ 实现系统关注点功能的代码将会重复出现在多个组件中。这意味着如果你要改变这些关注点的逻辑，必须修改各个模块的相关实现。即使你把这些关注点抽象成一个独立的模块，其他模块只是调用它的方法，但方法的调用还是会重复出现在各个模块中。
+ 组件会因为那些与自身核心业务无关的代码而变得混乱。一个向地址簿增加地址条目的方法应该只关注如何添加地址，而不应该关注它是不是安全的或者是否需要支持事务。

![业务对象与系统级服务结合过于紧密](./images/1.1.3-1.PNG)

>左边的业务对象与系统级服务结合得过于紧密。每个对象不但要知道它需要记录日志、进行安全控制和参与事务，还要亲自执行这些服务。

AOP能够使这些服务`模块化`，并以声明的方式将它们应用到它们需要影响的组件中去。所造成的结果就是这些组件会具有更高的`内聚性`并且会更加`关注自身的业务`，完全不需要了解涉及系统服务所带来复杂性。

>总之，AOP能够确保POJO的简单性。

![安全、事务和日志关注点与核心业务逻辑相分离](./images/1.1.3-2.PNG)

我们可以把`切面`想象成为覆盖很多组件之上的一个外壳。应用是由那些实现各自业务功能的模块组成的。借助AOP，可以使用各种功能层去`包裹核心业务层`。这些层以声明的方式灵活地应用到系统中，你的核心应用设置根本不知道它们的存在。这是一个非常强大的理念，可以将`安全`、`事务`和`日志关注点`与核心业务逻辑`相分离`。

如何将AOP应用到上例中？

吟游诗人这个服务类负责记载其实的所有事迹。

```java
package sia.knights;

import java.io.PrintStream;

public class Minstrel {

    private PrintStream printStream;

    public Minstrel(PrintStream printStream) {
        this.printStream = printStream;
    }

    public void singBeforeQuest() {// 探险前调用
        printStream.println("探险前..............");
    }

    public void singAfterQuest() {// 探险后调用
        printStream.println("探险后..............");
    }
}
```

Minstrel是只有两个方法的简单类。在骑士执行每一个探险任务之前，singBeforeQuest()会被调用；在骑士完成探险任务后，singAfterQuest()会被调用。

BraveKnight和Minstrel组合，BraveKnight必须要调用Minstrel的方法：

```java
package sia.knights;

public class BraveKnight implements Knight {

    private Quest quest;
    private Minstrel minstrel;

    public BraveKnight(Quest quest, Minstrel minstrel) {// Quest注入
        this.quest = quest;
        this.minstrel = minstrel;
    }

    @Override
    public void embarOnQuest() {
        minstrel.singBeforeQuest();// 骑士需要管理吟游诗人吗？
        quest.embark();
        minstrel.singAfterQuest();
    }

}
```

>管理他的吟游诗人真的是骑士职责范围内的工作吗？

骑士需要知道吟游诗人的存在，所以就必须把Minstrel注入到BraveKnight中。这使得BraveKnight的代码复杂化了，也会让Minstrel和BraveKnight结合得过于紧密了。如果需要骑士不需要吟游诗人呢，那不是要新建一个类？如果Minstrel为null会怎样，是否需要引入一个空值校验逻辑来覆盖这个场景？

>简单的BraveKnight类开始变得复杂，如果你还需要应对没有吟游诗人时的场景，那代码会变得更加复杂。但利用AOP，你可以声明吟游诗人必须歌颂骑士的探险事迹，而骑士本身并不需要直接访问Minstrel的方法。

要将Minstrel抽象为一个切面，需要在Spring配置文件中声明它。

代码在学习AOP后再补充。。。

#### 1.1.4 使用模板消除样板式代码

`样板式代码(boilerplate code)`--通常为了实现通用和简单的任务，不得不一遍遍重复编写重复代码。

许多Java API，例如JDBC，会涉及编写大量的样板式代码

+ 首先要创建一个数据库连接
+ 再创建一个语句查询
+ 进行查询
+ 为了平息JDBC可能出现的怒火，必须捕捉SQLException，这是一个检查型异常，即使它抛出后也做不了太多事情
+ 关闭数据库连接、语句和结果集
+ 依然要捕捉SQLException

JDBC操作要编写的代码千篇一律。只有少量的代码是SQL查询，其他的代码都是JDBC的样板代码。

JDBC不是产生样板式代码的唯一场景。在许多编程场景中往往都会导致类似的样板式代码，JMS、JNDI和使用REST服务通常也涉及大量的重复代码。

>Spring旨在通过模板封装来消除样板式代码。

Spring的JdbcTemplate使得执行数据库操作时，避免传统的JDBC样板代码成为了可能。我们仅仅需要关注查询的`核心逻辑`，而不需要迎合JDBC API的需求。

例子在碰到的时候再补充。。。。

### 1.2 容纳你的Bean

在基于Spring的应用中，应用对象生存于Spring容器(Container)中。

[应用对象存在于Spring容器中](./iamges/1.3-1.PNG)

Spring容器负责创建对象，装配它们，配置它们并管理它们的整个生命周期，从生存到死亡。

>容器是Spring框架的核心。Spring容器使用DI管理构成应用的组件，它会创建相互协作的组件之间的关联。毫无疑问，这些对象更简单干净，更易于理解，更易于重用并且更易于进行单元测试。

Spring容器的实现类型

+ bean工厂是最简单的内容器，提供基本的DI支持。
+ 应用上下文基于BeanFactory构建，并提供应用框架级别的服务，例如从属性文件解析文本信息以及发布应用事件给感兴趣的事件监听者。

>bean工厂对大多数应用来说往往太低级，因此应用上下文要比bean工厂更受欢迎。

####　1.2.1 使用应用上下文

Spring自带多种类型的应用上下文： 

+ AnnotationConfigApplictionContext：从一个或多个`基于Java的配置类`中加载`Spring应用上下文`
+ AnnotationConfigWebApplicationContext：从一个或多个基于`Java的配置类`中加载`Spring Web应用上下文`
+ ClassPathXmlApplicationContext：从类路径下的一个或多个XML配置文件中加载上下文定义，把应用上下文的定义文件作为类资源
+ FileSystemXmlApplicationContext：从文件系统下的一个或多个XML配置文件中加载上下文定义。
+ XmlWebApplicationContext：从Web应用下的一个或多个XML配置文件中加载上下文定义

应用上下文准备就绪后，就可以调用上下文的getBean()从Spring容器中获取bean。

#### 1.2.2 bean的生命周期

在传统的Java应用中，bean的生命周期很简单。使用Java关键字new进行bean实例化，然后该bean就可以使用了。一旦该bean不再被使用了，则由Java自动进行垃圾回收。

>相比之下，Spring容器的bean的生命周期就显得相对复杂多了。正确理解Spring bean的生命周期非常重要，因为你或许要利用Spring提供的扩展点来自定义bean的创建过程（碰到的时候再添加例子）。

![bean在Spring容器中从创建到销毁的过程](./images/1.2.2-1.PNG)

1. Spring对bean进行实例化
2. Spring将值和bean的引用注入到bean对应的属性中
3. 如果bean实现了BeanNameAware接口，Spring将bean的ID传递给setBeanName()
4. 如果bean实现了BeanFactoryAware接口，Spring将调用setBeanFactory()，将BeanFactory容器实例传入
5. 如果bean实现了ApplicationContextAware接口，Spring将调用setApplicationContext()，将bean所在的应用上下文的引用传入进来
6. 如果bean实现了BeanPostProcessor接口，Spring将调用它们的postProccessBeforeInitializingBean()
7. 如果bean实现了InitializingBean接口，Spring将调用它们的afterPropertiesSet()。类似的，如果bean使用自定义初始化方法，该方法也会被调用
8. 如果bean实现了BeanPostProcessor接口，Spring将调用它们的postProcessAfterInitialization()
9. 此时，bean已经准备就绪，可被应用程序使用了。它们将一直驻留在应用上下文中，直到该应用上下文被销毁。
10. 如果bean实现了DisposableBean接口，Spring将调用它的destroy()。同样，如果bean使用了自定义销毁方法，该方法也会被调用。

>一个空的容器并没有太大的价值，在你把东西放进去之前，它里面什么都没有。为了从Spring的DI中收益，我们必须将应用对象装配进Spring容器中。

### 1.3 俯瞰Spring风景线

>Spring关注于通过DI、AOP和消除样板式代码来简化企业级Java开发。即使这是Spring能做的所有事情，那Spring也值得一用。但是，Spring实际上的功能超乎你的想象。

在Spring框架的范畴内，你会发现Spring简化Java开发的多种方式。但在Spring框架之外还存在一个构建在核心框架之上的庞大生态圈，它将Spring扩展到不同的领域，例如Web服务、REST、移动开发以及NoSQL。

#### 1.3.1 Spring模块

在Spring 4.0中，Spring框架的发布版本包括了20个不同的模块，每个模块会有3个JAR文件（二进制类库、源码的JAR文件以及JavaDoc的JAR文件）。

![Spring框架是由20个不同的模块组成](./images/1.3.1-1.PNG)

这些模块根据其所属的功能可以划分为6类不同的功能。

![Spring框架由6个定义良好的模块分类组成](./images/1.3.1-2.PNG)

>总体而言，这些模块为开发企业级应用提供了所需的一切。但是你也不必将应用建立在整个Spring框架之上，你可以自由地选择适合自身应用需求的Spring模块；当Spring不能满足需求时，完全可以考虑其他选择。事实上，Spring甚至提供了与其他第三方框架和类库的集成点，这样你就不需要自己编写这样的代码了。

##### Spring核心容器

容器是Spring框架最核心的部分，它管理着Spring应用中bean的创建、配置和管理。在该模块中，包括了Spring bean工厂，它为Spring提供了DI的功能。基于bean工厂，我们还会发现有多种Spring应用上下文的实现，每一种都提供了配置Spring的不同方式。

>除了bean工厂和应用上下文，该模块也提供了许多企业服务，例如E-mail、JNDI访问、EJB集成和调度。

所有的Spring模块都构建于核心容器之上。当你配置应用时，其实你隐式地使用了这些类。

##### Spring的AOP模块

在AOP模块中，Spring对面向切面编程提供了丰富的支持。这个模块是Spring应用系统中开发切面的基础。与DI一样，AOP可以帮助应用对象解耦。借助AOP，可以将遍布系统的关注点（例如事务和安全）从它们所应用的对象中解耦出来。

##### 数据访问与集成

使用JDBC编写代码通常会导致大量的样板式代码，例如获得数据库连接、创建语句、处理结果集到最后关闭数据库连接。Spring的JDBC和DAO(Data Access Object)模块抽象了这些样板式代码，使我们的数据库代码变得简单明了，还可以避免因为关闭数据库资源失败而引发的问题。该模块在多种数据库服务的错误信息之上构建了一个语义丰富的异常层，以后我们再也不用解释那些隐晦专有的SQL错误信息了。

而且，Spring提供了ORM模块。Spring的ORM模块建立在对DAO的支持之上，并未多个ORM框架提供了一种构建DAO的简便方式。Spring没有尝试去创建自己的ORM解决方案，而是对许多流行的ORM框架进行了集成，包括Hibernate、Java Persisternce API等。Spring的事务管理支持所有的ORM框架以及JDBC。

##### Web与远程调用

MVC(Model-View-Controller)模式是一种普遍被接受的构建Web应用的方法，它可以帮助用户将界面逻辑与应用逻辑分离。

虽然Spring能够与多种流行的MVC框架进行集成，但它的Web和远程调用模块自带了一个强大的MVC框架，有助于在Web层提升应用的松耦合。

#### 1.3.2 Spring Portfolio

>事实上，Spring远不止Spring框架所下载的那些。整个Spring Portfolio包括多个构建于核心Spring框架之上的框架和类库。概括地讲，整个Spring Portfolio几乎为每一个领域的Java开发都提供了Spring编程模型。

##### Spring Web Flow

>Spring Web Flow建立于Spring MVC框架之上，它为基于流程的会话式Web应用（购物车或者向导功能）提供了支持。

##### Spring Security

>安全对于许多应用都是一个非常关键的切面。利用Spring AOP，Spring Security为Spring应用提供了声明式的安全机制。

##### Spring Data

>Spirng Data使得在Spring中使用任何数据库都变得非常容易。尽管关系型数据库统治企业级应用多年，但是现代化的应在正在认识到并不是所有的数据都适合放在一张表中的行和列中。一种新的数据库种类，通常称之为NoSQL数据库，提供了使用数据的新方法，这些方法比传统的关系型数据库更为合适。

不管你使用文档数据库，如MongoDB，图数据库，如Neo4j，还是传统的关系型数据库，Spring Data都为持久化提供了一种简单的编程模型。这包括为多种数据库类型提供了一种自动化的Repository机制，它负责为你创建Repository的实现。

##### Spring Boot

>Spring极大地简化了众多的编程任务，减少甚至消除了很多样板式代码，如果没有Spring的话，在日常工作中你不得不编写这样的样板代码。Spring Boot是一个崭新的令人兴奋的项目，它以Spring的视角，致力于简化Spring本身。

Spring Boot大量依赖于自动配置技术，它能够消除大部分Spring配置。它还提供了多个Starter项目，不管你使用Maven还是Gradle，这都能减少Spring工程构建文件的大小。

>Spring Portfolio远不止这些项目。。。