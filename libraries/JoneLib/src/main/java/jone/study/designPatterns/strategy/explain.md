Strategy(策略模式)

模式的定义
策略模式定义了一系列的算法，并将每一个算法封装起来，而且使它们还可以相互替换。
策略模式让算法独立于使用它的客户而独立变化。
注：针对同一类型操作，将复杂多样的处理方式分别开来，有选择的实现各自特有的操作。

模式的使用场景
针对同一类型问题的多种处理方式，仅仅是具体行为有差别时。
需要安全的封装多种同一类型的操作时。
出现同一抽象多个子类，而又需要使用if-else 或者 switch-case来选择时。

角色介绍
Context：用来操作策略的上下文环境。
Strategy : 策略的抽象。
ConcreteStrategyA、ConcreteStrategyB : 具体的策略实现。

简单实现的介绍
通常如果一个问题有多个解决方案或者稍有区别的操作时，
最简单的方式就是利用if-else or switch-case方式来解决，
对于简单的解决方案这样做无疑是比较简单、方便、快捷的，
但是如果解决方案中包括大量的处理逻辑需要封装，
或者处理方式变动较大的时候则就显得混乱、复杂，而策略模式则很好的解决了这样的问题，
它将各种方案分离开来，让操作者根据具体的需求来动态的选择不同的策略方案。 

Android源码中的模式实现
日常的Android开发中经常会用到动画，Android中最简单的动画就是Tween Animation了，
当然帧动画和属性动画也挺方便的，但是基本原理都类似，
毕竟动画的本质都是一帧一帧的展现给用户的，只不要当fps小于60的时候，
人眼基本看不出间隔，也就成了所谓的流畅动画。
（注：属性动画是3.0以后才有的，低版本可采用NineOldAndroids来兼容。
而动画的动态效果往往也取决于插值器Interpolator不同，
我们只需要对Animation对象设置不同的Interpolator就可以实现不同的效果

策略模式主要用来分离算法，根据相同的行为抽象来做不同的具体策略实现。

通过以上也可以看出策略模式的优缺点：

优点：
结构清晰明了、使用简单直观。
耦合度相对而言较低，扩展方便。
操作封装也更为彻底，数据更为安全。

缺点：
随着策略的增加，子类也会变得繁多。