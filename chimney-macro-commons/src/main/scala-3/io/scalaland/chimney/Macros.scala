package io.scalaland.chimney

import io.scalaland.chimney.internal.compiletime.DefinitionsPlatform
import io.scalaland.chimney.internal.compiletime.datatypes.ProductTypesPlatform

import scala.quoted.{Expr, Quotes, Type}

object Macros {
  final class Ctx(quotes: Quotes) extends DefinitionsPlatform(using quotes) with ProductTypesPlatform

  def isPOJO[A](using quotes: Quotes, tpeA: Type[A]): Expr[Boolean] =
    Expr.apply(Ctx(quotes).ProductTypeOps(tpeA).isPOJO)

  inline def isPOJOInline[A]: Boolean = ${ isPOJO[A] }
}
