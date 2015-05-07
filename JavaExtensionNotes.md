# Edit: This is all obsolete. #
[Clojure](http://clojure.org) does everything I need and can [generate Java classes](http://clojure.org/compilation). I love it, love it, love it!

# Java Extension Design Notes #

# Introduction #

I'd like to extends Java with new features while remaining as compatible as possible with current tools and IDEs. Some of the features can be found in various Java extensions, all of which come a bit short of meeting my requirements. I really like Scala. It fixes a lot gone wrong in Java, but the IDE support is not quite there yet and it's also missing some extra features I'd like.

# Details #

## Desired features ##
  * Yield return [Simple threaded implementation](http://code.google.com/p/emh-gplware/wiki/JavaYieldReturn)
  * Typedefs
  * Delegation (sort of like Scala trait)
  * Automatic generic type propagation
  * Specialization of generics classes for performance (like in Scala)

## Evaluation of similar projects ##

### [Project lombok](http://projectlombok.org/) ###

Nice implementation, few features.

  * + Supports standard Java code generation through delombok.
  * - Requires specialized IDE plugins, which means more work to write new extensions.
  * - Just a few simple extensions like getters/setters. Not what I need.
  * - Not as easy as I'd like to extend.

### [Javadude annotations](http://code.google.com/p/javadude/wiki/Annotations) ###

Nice features, unsatisfactory implementation.

  * + Interesting features like delegation, interface extraction and default method parameters.
  * + Generates standard Java source code which is then sub-classed.
  * - I didn't like the look of the source code and I had trouble getting it to work.
  * - Source code needs to be compiled for Java 1.5.
  * - Not as easy as I'd like to extend.

### [Byte-code-based yield return](http://chaoticjava.com/posts/implementation-details-for-java-yielder/) ###
  * + Efficient due to byte-code engineering.
  * - Complex: Harder to maintain and debug.
  * - Less compatible due to byte-code engineering. Can't generate source code compatible with standard Java compilers, but a small less efficient compatible threaded stand-in resolves this.

Byte-code manipulation is needed for efficient yield return in Java, but sub-classing a simple 100-line threaded switching class can do the same, just slower. I'd like to be compatible with this tool so it's easy to switch when I need the performance upgrade.

### [Thread-based yield return](http://jimblackler.net/blog/?p=61) ###

  * + Looks good. Similar to my implementation, but I'd already made mine so I didn't look very closely at this.

### Other byte-code based projects ###
  * - Confuses IDEs, requires plug-in support.
  * - Harder to maintain, debug and extend.

Libraries:
[CGLib : Code Generation Library ](http://cglib.sourceforge.net/)
[BCEL : Byte-code Engineering Library ](http://jakarta.apache.org/bcel/)

### Reflection-based techniques ###
  * - Too inefficent for my purposes.

## Design goals ##
  * Remain compatible with standard Java compilers and IDEs without special effort like writing plugins
    * Solution: Generate intermediate source files, which can be edited and transformed back to pre-generated code. I want to have standard Java source code for compatibility but with the extra features present in the same file as annotations or in source code comments (whichever makes more sense and is compatible). There is a bijection between generated and generating code.
  * Easy to extend framework with new features and code generation to create your own annotation-based pseudo-DSLs (domain-specific language).
  * Code that interfaces directly to Java compiler/parser should be small to be easy to maintain and port. First version will be written hooking into internal unsupported Java-compiler APIs for quick initial development and I'll evolve a (preferably small and generic) standard API later.
  * My projects are not huge so code-generation doesn't have to be blazingly fast, so I'll prioritize flexibility.
  * I want to optionally use Python to generate code, calling it from the annotation processing framework. It's much more productive for meta-techniques and text-processing than Java.