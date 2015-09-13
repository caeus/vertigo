# Vertigo

Vertigo is a library tool for java, used to generate the mongo `Codec`s for your domain specific classes.
It uses annotation processing to generate the `Codec` for the annotated classes so you don't have to do it yourself.

## Installation

To use it, you just have to add the following dependencies to your build system (I'll assume you use sbt)

```
libraryDependencies ++= Seq("co.sanduche" % "vertigo" % "{version}" ,
  "co.sanduche" % "vertigo-processor" % "{version}" % "provided")
```

For a class to have it's own `Codec`generated it must be annotated with `@MongoEntity`.

## Capabilities

Vertigo supports the use of `List<?>` and `Map<String,?>` fields (included the support of nested generics such as `Map<String,List<List<ObjectId>>>`, if such gets to be needed any time) and can be extended to support more collection types such as `Set`.

## Limitations

Support for using different names for each field during de encoding/decoding proccess is still not present.
Classes with type variables are not supported out of the box, yet the codecs for them can be included.

