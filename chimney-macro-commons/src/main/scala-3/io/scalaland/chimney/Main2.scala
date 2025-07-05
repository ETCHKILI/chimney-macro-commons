package io.scalaland.chimney

import hello.User

@main def app(): Unit = {
  val x: Boolean = Macros.isPOJOInline[User]
  println(s"hello.User is POJO: $x")
}
