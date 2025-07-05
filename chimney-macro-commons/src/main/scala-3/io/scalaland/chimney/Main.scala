package io.scalaland.chimney

object hello {

  object World {
    case class User(name: String, address: String)

    trait SomeTrait {
      val name: String

      def someMethod(): String
    }

  }

  export World.{SomeTrait, User}
}

case class UserName(name: String)
