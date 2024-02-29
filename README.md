Experiment to learn about the Java module system

# Resources

* https://www.youtube.com/watch?v=3KP5YiKLkeo
* https://www.youtube.com/watch?v=UqnwQp1uHuY
* https://docs.gradle.org/current/userguide/java_library_plugin.html#sec:java_library_modular
* https://www.baeldung.com/java-modularity
* https://stackoverflow.com/a/48365974/282229
* https://openjdk.org/projects/jigsaw/spec/sotms/#the-unnamed-module (NOTE: Slightly outdated, see note at the beginning!)

# General details

## Java modules

The Java Platform Module System (JPMS) allows to declare modules and their dependencies, as well as an alternative to the Java Service Provider API.

https://www.baeldung.com/java-modularity

A Module is a group of closely related packages and resources along with a new module descriptor file.
Aside from organizing our code, packages are used to determine what code is publicly accessible outside of the module.

The module naming rules are similar to how we name packages (dots are allowed, dashes are not).
It’s very common to do either project-style (my.module) or Reverse-DNS (com.baeldung.mymodule) style names.

We need to list ("exports", "export to") all packages we want to be public because by default all packages are module private.
The same is true for reflection. By default, we cannot use reflection on classes we import from another module.
(Can be allowed for a module by using "open module", "opens", and "opens to".)

There are four types of modules in the new module system:

* System Modules – These are the modules listed when we run the list-modules command above. They include the Java SE and JDK modules.
* Application Modules – These modules are what we usually want to build when we decide to use Modules. They are named and defined in the compiled module-info.class file included in the assembled JAR.
* Automatic Modules – We can include unofficial modules by adding existing JAR files to the module path. The name of the module will be derived from the name of the JAR. Automatic modules will have full read access to every other module loaded by the path.
* Unnamed Module – When a class or JAR is loaded onto the classpath, but not the module path, it’s automatically added to the unnamed module. It’s a catch-all module to maintain backward compatibility with previously-written Java code.

We can only have one module per JAR file.

To set up a module, we need to put a special file at the root of our packages named module-info.java.

## On accessing the unnamed module

https://stackoverflow.com/a/48365974/282229

The classes present on the classpath during the execution are part of an unnamed module in JPMS.

> The unnamed module exports all of its packages. This enables flexible migration...
  It does not, however, mean that code in a named module can access types in the unnamed module.
  A named module cannot, in fact, even declare a dependence upon the unnamed module.

This is intentional to preserve the reliable configuration in the module system. As stated further :

> If a package is defined in both a named module and the unnamed module then the package in the unnamed module is ignored.
  This preserves reliable configuration even in the face of the chaos of the class path,
  ensuring that every module still reads at most one module defining a given package.

# cplace specifics

## Split packages

As of release 24.2, cplace depends on some libraries that provide different classes in the same package.

* `org.eclipse.equinox.common` and `org.eclipse.equinox.registry` both declare and export package `org.eclipse.core.runtime`.
  These are dependencies of `org.eclipse.core.runtime`, which is used by daisy-diff.

These "split packages" are disallowed in the Java module system.
There are crutches to mend the split, or to prevent these libraries from being considered as (automatic) modules.

## Duplicated classes and getting rid of unwanted transitive dependencies

A draft of modularizing the platform plugin lead to this compile error:

> `error: module org.apache.commons.lang3 reads package org.apache.commons.logging from both spring.jcl and org.apache.commons.logging`

In the Gradle dependencies, we exclude commons-logging and instead have the bridge from commons-logging to SJF4J (jcl-over-slf4j).
It seems that spring-jcl is intended to provide a minimal replacement for commons-logging which can delegate to SJF4J.
It is unclear why a conflict with commons-logging is even reported, because it should not be used at all.

> `error: module spring.boot.starter reads package jakarta.servlet from both jetty.servlet.api and jakarta.servlet`

Another duplicated package in our class/module path.
Seems we need to resolve https://base.cplace.io/pages/ifqei5329oxljqnc7zn0xnv34/PFM-ISSUE-11790-check-for-duplicate-class-files
and https://base.cplace.io/pages/zkdvp06dy7r6ynzkb80h369j3/PFM-ISSUE-25672-Find-and-avoid-duplicate-classes-on-classpath
before we can make significant progress here.

## uses and provides

The JPMS allows modules to provide services for use by other modules.
Similar to the Java SPI, a service class implements a service interface and is looked up in another module by that service interface.
As of now, cplace does not make use of Java SPI, and so won't benefit from this alternative.

## Accessing the unnamed module

In cplace, we have dependencies that are not yet (automatic) modules.

A prominent example is `jsr305`, which provides annotations like `@Nullable`, `@Nonnull`, and `@ParametersAreNonnullByDefault`.

An automatic module can access these classes, but a full module (with module-info.java) cannot.

Work-around:
Use `--add-reads cf.cplace.platform=ALL-UNNAMED` during compilation and start.
That must likely be used for every cplace plugin initially.
Adding the parameter to the various start tasks (runCplaceTest, ..., fixMessages) may require support from the Gradle plugin.
