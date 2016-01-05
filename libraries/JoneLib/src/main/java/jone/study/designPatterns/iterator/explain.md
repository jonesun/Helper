Iterator(迭代器模式)

模式的定义
迭代器（Iterator）模式，又叫做游标（Cursor）模式。
GOF给出的定义为：提供一种方法访问一个容器（container）对象中各个元素，而又不需暴露该对象的内部细节。

模式的使用场景
Java JDK 1.2 版开始支持迭代器。
每一个迭代器提供next()以及hasNext()方法，
同时也支持remove()(1.8的时候remove已经成为default throw new UnsupportedOperationException("remove"))。
对Android来说,集合Collection实现了Iterable接口,就是说,
无论是List的一大家子还是Map的一大家子,我们都可以使用Iterator来遍历里面的元素,可以使用Iterator的集合

角色介绍　　
迭代器接口Iterator：该接口必须定义实现迭代功能的最小定义方法集比如提供hasNext()和next()方法。
迭代器实现类：迭代器接口Iterator的实现类。可以根据具体情况加以实现。
容器接口：定义基本功能以及提供类似Iterator iterator()的方法。
容器实现类：容器接口的实现类。必须实现Iterator iterator()方法。

简单实现的介绍
我们有一个数组,对其遍历的过程我们希望使用者像ArrayList一样的使用,我们就可以用过iterator来实现.

Android源码中的模式实现
一个集合想要实现Iterator很是很简单的,需要注意的是每次需要重新生成一个Iterator来进行遍历.
且遍历过程是单方向的,HashMap是通过一个类似HashIterator来实现的,
我们为了解释简单,这里只是研究ArrayList(此处以Android L源码为例,其他版本略有不同)

优点与缺点

优点
面向对象设计原则中的单一职责原则，对于不同的功能,我们要尽可能的把这个功能分解出单一的职责，不同的类去承担不同的职责。Iterator模式就是分离了集合对象的遍历行为，抽象出一个迭代器类来负责，这样不暴露集合的内部结构，又可让外部代码透明的访问集合内部的数据。

缺点
会产生多余的对象，消耗内存；
遍历过程是一个单向且不可逆的遍历
如果你在遍历的过程中,集合发生改变,变多变少,内容变化都是算,就会抛出来ConcurrentModificationException异常.