package io.scalaland.chimney

import hello.*

@main def app(): Unit = {
//  val a = new SomeTrait {
//    override val name: String = ???
//
//    override def someMethod(): String = ???
//  }
  println(s"hello.User is POJO: ${Macros.isPOJOInline[User]}")
  println(s"hello.SomeTrait is POJO: ${Macros.isPOJOInline[SomeTrait]}")
  println(s"hello.World.User is POJO: ${Macros.isPOJOInline[hello.World.User]}")
  println(s"hello.World.SomeTrait is POJO: ${Macros.isPOJOInline[hello.World.SomeTrait]}")
  println(s"String is POJO: ${Macros.isPOJOInline[String]}")
  println(s"Long is POJO: ${Macros.isPOJOInline[Long]}")
}
