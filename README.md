# Spring in Action 阅读与学习笔记

PS：《Spring in Action》第四版，作者：Cragin Walls，译者：张卫滨

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
![DI](./images/1.1.2-1.png)  

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

