package io.scalaland.chimney

object hello {

  object World {
    case class User(name: String, address: String)
  }

  export World.User
}

case class UserName(name: String)

