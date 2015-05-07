# Options for Java/Python integration #

[Great overview slideshow](http://www.slideshare.net/onyame/mixing-python-and-java)

Since all the options are all more or less compatible with (different versions of) standard Python they are fairly interchangeable, except on some points like the style of imports and sub-classing, which is only supported by Jython, and object proxying. Different options could be useful at different stages. I would recommend Jepp or JPype over Jython for the development phase because the slow loading time of Jython is a show-stopper for quick development and testing.


**[Jython](http://www.jython.org)**

Jython is a reimplementation of Python in Java.

  * + Tight integration. Sub-classing of Java classes from Jython works. Java interfaces with proxies are used the other way.
  * + Well maintained and stable.
  * - Slow startup. Useless for quick iterative development. 6.5 seconds to start on my Core 2 1.66 GHz laptop.
  * - (from JPype): Jython (formerly known as JPython) is a great idea. However, it suffers from a large number of drawbacks, i.e. it always lags behind CPython, it is slow and it does not allow access to most Python extensions.

Jython may have its uses when loading time is insignificant compared to application usage time (i.e. in the finished version) or if its extended features like subclassing are really needed. If you need Jython I recommend you also invest the time to implement something like dynamic reloading of modules or fork()ing the JVM to be able to keep Jython in memory to avoid the long loading time. Or just buy a very fast computer. Strange that Jython developers haven't implemented faster loading by caching or similar already.

Edit: Seems like they're working on it. These parts may be useful to hack together a solution:
  * [Nailgun](http://www.martiansoftware.com/nailgun/index.html) Java server to avoid startup cost for Java apps in general.
  * [Mailing list discussion on multiple Python Interpreters](http://old.nabble.com/Multiple-PythonInterpreters--reloading-modules-td15755811.html)
  * [How to reload Jython modules under development](http://pyunit.sourceforge.net/notes/reloading.html)
  * [Jython ticket](http://bugs.jython.org/issue1411) on reloading Java classes and updating their Jython representations. There's a [simple example](http://bugs.jython.org/file751/ClassReloader.java) attached. [JRebel](http://www.zeroturnaround.com/jrebel/) reloads Java classes dynamically, even modifying existing instances to match, but I don't know if it reloads Jython representations, and it's not Open Source.
  * [clj-server](http://github.com/Neronus/clj-server). Similar to desired solution, but for [Clojure](http://clojure.org) instead of Jython.


**[JEPP](http://jepp.sourceforge.net/usage.html)**

Jepp embeds CPython in Java. It is safe to use in a heavily threaded environment, it is quite fast and its stability is a main feature and goal.

  * + Fast startup. Good for quick iterative development.
  * + Full access to CPython libraries.
  * + Seems more mature in its stability than JPype.
  * - Less tight integration than Jython. No sub-classing or the likes.

You could probably implement a lot of subclass-like/proxy functionality quite easily using dynamic python features and/or Java dynamic proxy in combination with Java interface. I'll post code for this if I do it.


**[JPype](http://jpype.sourceforge.net/)**

JPype is an effort to allow python programs full access to java class libraries. This is achieved not through re-implementing Python, as Jython/JPython has done, but rather through interfacing at the native level in both Virtual Machines.

  * + Fast startup. Good for quick iterative development.
  * + Full access to CPython libraries.
  * + Easily implement Java interfaces in Python using JProxy.
  * - Seems like a hack. For example it crashes if you do not load the JVM correctly.
  * - Error messages are not informative: Loads missing jars/classes as package objects and complains about missing methods/constructor on the package object, but once you know about these limitations you can deal.
  * - Less tight integration than Jython.


**Remote procedure techniques like XML-RPC, JSON-RPC, SOAP, Corba...**

Haven't investigated these much. Will probably have even less tight integration than JPype and JEPP. It is most definitely the case if you just use a generic remote procedure library. I don't see a strong reason to use them over JPype or JEPP if the application doesn't need to be networked to begin with.