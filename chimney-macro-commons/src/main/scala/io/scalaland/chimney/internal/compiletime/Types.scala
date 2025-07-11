package io.scalaland.chimney.internal.compiletime

import scala.collection.compat.Factory
import scala.collection.immutable.ListSet

import TypeAlias.<:<<

private[compiletime] trait Types { this: (Existentials & Results) =>

  /** Platform-specific type representation (`c.WeakTypeTag[A]` in 2, `scala.quoted.Type[A]` in 3) */
  protected type Type[A]
  protected val Type: TypeModule
  protected trait TypeModule { this: Type.type =>

    /** Summons `Type` instance */
    final def apply[A](implicit A: Type[A]): Type[A] = A

    // Interfaces for applying and extracting type parameters in shared code

    /** Allow applying and extracting some type `L <:< ? <:< U` */
    trait Ctor1Bounded[L, U >: L, F[_ >: L <: U]] {
      def apply[A >: L <: U: Type]: Type[F[A]]
      def unapply[A](A: Type[A]): Option[L >?< U]
    }
    trait Ctor1UpperBounded[U, F[_ <: U]] extends Ctor1Bounded[Nothing, U, F]
    trait Ctor1[F[_]] extends Ctor1Bounded[Nothing, Any, F]

    /** Allow applying and extracting some types `L1 <:< ? <:< U1, L2 <:< ? <:< U2` */
    trait Ctor2Bounded[L1, U1 >: L1, L2, U2 >: L2, F[_ >: L1 <: U1, _ >: L2 <: U2]] {
      def apply[A >: L1 <: U1: Type, B >: L2 <: U2: Type]: Type[F[A, B]]
      def unapply[A](A: Type[A]): Option[(L1 >?< U1, L2 >?< U2)]
    }
    trait Ctor2UpperBounded[U1, U2, F[_ <: U1, _ <: U2]] extends Ctor2Bounded[Nothing, U1, Nothing, U2, F]
    trait Ctor2[F[_, _]] extends Ctor2Bounded[Nothing, Any, Nothing, Any, F]

    /** Allow applying and extracting some types `L1 <:< ? <:< U1, L2 <:< ? <:< U2, L3 <:< ? <:< U3` */
    trait Ctor3Bounded[L1, U1 >: L1, L2, U2 >: L2, L3, U3 >: L3, F[_ >: L1 <: U1, _ >: L2 <: U2, _ >: L3 <: U3]] {
      def apply[A >: L1 <: U1: Type, B >: L2 <: U2: Type, C >: L3 <: U3: Type]: Type[F[A, B, C]]
      def unapply[A](A: Type[A]): Option[(L1 >?< U1, L2 >?< U2, L3 >?< U3)]
    }
    trait Ctor3UpperBounded[U1, U2, U3, F[_ <: U1, _ <: U2, _ <: U3]]
        extends Ctor3Bounded[Nothing, U1, Nothing, U2, Nothing, U3, F]
    trait Ctor3[F[_, _, _]] extends Ctor3Bounded[Nothing, Any, Nothing, Any, Nothing, Any, F]

    /** Allow applying and extracting some types `L1 <:< ? <:< U1, L2 <:< ? <:< U2, L3 <:< ? <:< U3, L4 <:< ? <:< U4` */
    trait Ctor4Bounded[L1, U1 >: L1, L2, U2 >: L2, L3, U3 >: L3, L4, U4 >: L4, F[
        _ >: L1 <: U1,
        _ >: L2 <: U2,
        _ >: L3 <: U3,
        _ >: L4 <: U4
    ]] {
      def apply[A >: L1 <: U1: Type, B >: L2 <: U2: Type, C >: L3 <: U3: Type, D >: L4 <: U4: Type]: Type[F[A, B, C, D]]
      def unapply[A](A: Type[A]): Option[(L1 >?< U1, L2 >?< U2, L3 >?< U3, L4 >?< U4)]
    }
    trait Ctor4UpperBounded[U1, U2, U3, U4, F[_ <: U1, _ <: U2, _ <: U3, _ <: U4]]
        extends Ctor4Bounded[Nothing, U1, Nothing, U2, Nothing, U3, Nothing, U4, F]
    trait Ctor4[F[_, _, _, _]] extends Ctor4Bounded[Nothing, Any, Nothing, Any, Nothing, Any, Nothing, Any, F]

    // Build-in types' definitions

    val Nothing: Type[Nothing]
    val Null: Type[Null]
    val Any: Type[Any]
    val AnyVal: Type[AnyVal]
    val Boolean: Type[Boolean]
    val Byte: Type[Byte]
    val Char: Type[Char]
    val Short: Type[Short]
    val Int: Type[Int]
    val Long: Type[Long]
    val Float: Type[Float]
    val Double: Type[Double]
    val Unit: Type[Unit]
    val String: Type[String]

    lazy val primitives: Set[??] = ListSet(
      Boolean.as_??,
      Byte.as_??,
      Char.as_??,
      Short.as_??,
      Int.as_??,
      Long.as_??,
      Float.as_??,
      Double.as_??,
      Unit.as_??
    )

    val Tuple2: Tuple2Module
    trait Tuple2Module extends Ctor2[Tuple2] { this: Tuple2.type => }

    val Function1: Function1Module
    trait Function1Module extends Ctor2[Function1] { this: Function1.type => }
    val Function2: Function2Module
    trait Function2Module extends Ctor3[Function2] { this: Function2.type => }

    val Array: ArrayModule
    trait ArrayModule extends Ctor1[Array] { this: Array.type => }

    val Option: OptionModule
    trait OptionModule extends Ctor1[Option] { this: Option.type =>
      val Some: SomeModule
      trait SomeModule extends Ctor1[Some] { this: Some.type => }
      val None: Type[scala.None.type]
    }

    val Either: EitherModule
    trait EitherModule extends Ctor2[Either] { this: Either.type =>
      val Left: LeftModule
      trait LeftModule extends Ctor2[Left] { this: Left.type => }

      val Right: RightModule
      trait RightModule extends Ctor2[Right] { this: Right.type => }
    }

    val Iterable: IterableModule
    trait IterableModule extends Ctor1[Iterable] { this: Iterable.type => }

    val Map: MapModule
    trait MapModule extends Ctor2[scala.collection.Map] { this: Map.type => }

    val Iterator: IteratorModule
    trait IteratorModule extends Ctor1[Iterator] { this: Iterator.type => }

    val Factory: FactoryModule
    trait FactoryModule extends Ctor2[Factory] { this: Factory.type => }

    trait Literal[U] {
      def apply[A <: U](value: A): Type[A]
      def unapply[A](A: Type[A]): Option[Existential.UpperBounded[U, Id]]
    }

    val BooleanLiteral: BooleanLiteralModule
    trait BooleanLiteralModule extends Literal[Boolean] { this: BooleanLiteral.type => }

    val IntLiteral: IntLiteralModule
    trait IntLiteralModule extends Literal[Int] { this: IntLiteral.type => }

    val LongLiteral: LongLiteralModule
    trait LongLiteralModule extends Literal[Long] { this: LongLiteral.type => }

    val FloatLiteral: FloatLiteralModule
    trait FloatLiteralModule extends Literal[Float] { this: FloatLiteral.type => }

    val DoubleLiteral: DoubleLiteralModule
    trait DoubleLiteralModule extends Literal[Double] { this: DoubleLiteral.type => }

    val CharLiteral: CharLiteralModule
    trait CharLiteralModule extends Literal[Char] { this: CharLiteral.type => }

    val StringLiteral: StringLiteralModule
    trait StringLiteralModule extends Literal[String] { this: StringLiteral.type => }

    val <:< : `<:<Module`
    trait `<:<Module` extends Ctor2[<:<<] { this: `<:<`.type => }

    class Cache[F[_]] {
      sealed private trait Entry {
        type Underlying
        val key: Type[Underlying]
        val value: F[Underlying]
      }
      private object Entry {
        def apply[A](key: Type[A], value: F[A]): Entry { type Underlying = A } = new Impl(key, value)
        final class Impl[A](val key: Type[A], val value: F[A]) extends Entry { type Underlying = A }
      }
      private val storage = scala.collection.mutable.ListBuffer.empty[Entry]

      def apply[A](key: Type[A])(newValue: => F[A]): F[A] =
        storage.find(_.key =:= key) match {
          case Some(found) => found.value.asInstanceOf[F[A]]
          case None        =>
            val value = newValue
            storage += Entry(key, value)
            value
        }
    }

    // You can `import Type.Implicits.*` in your shared code to avoid providing types manually, while avoiding conflicts
    // with implicit types seen in platform-specific scopes (which would happen if those implicits were always used).
    object Implicits {

      implicit val NothingType: Type[Nothing] = Nothing
      implicit val NullType: Type[Null] = Null
      implicit val AnyType: Type[Any] = Any
      implicit val AnyValType: Type[AnyVal] = AnyVal
      implicit val BooleanType: Type[Boolean] = Boolean
      implicit val ByteType: Type[Byte] = Byte
      implicit val CharType: Type[Char] = Char
      implicit val ShortType: Type[Short] = Short
      implicit val IntType: Type[Int] = Int
      implicit val LongType: Type[Long] = Long
      implicit val FloatType: Type[Float] = Float
      implicit val DoubleType: Type[Double] = Double
      implicit val UnitType: Type[Unit] = Unit
      implicit val StringType: Type[String] = String

      implicit def Tuple2Type[A: Type, B: Type]: Type[(A, B)] = Tuple2[A, B]

      implicit def Function1Type[A: Type, B: Type]: Type[A => B] = Function1[A, B]
      implicit def Function2Type[A: Type, B: Type, C: Type]: Type[(A, B) => C] = Function2[A, B, C]

      implicit def ArrayType[A: Type]: Type[Array[A]] = Array[A]

      implicit def OptionType[A: Type]: Type[Option[A]] = Option[A]
      implicit def SomeType[A: Type]: Type[Some[A]] = Option.Some[A]
      implicit val NoneType: Type[None.type] = Option.None

      implicit def EitherType[L: Type, R: Type]: Type[Either[L, R]] = Either[L, R]
      implicit def LeftType[L: Type, R: Type]: Type[Left[L, R]] = Either.Left[L, R]
      implicit def RightType[L: Type, R: Type]: Type[Right[L, R]] = Either.Right[L, R]

      implicit def IterableType[A: Type]: Type[Iterable[A]] = Iterable[A]
      implicit def MapType[K: Type, V: Type]: Type[scala.collection.Map[K, V]] = Map[K, V]
      implicit def IteratorType[A: Type]: Type[Iterator[A]] = Iterator[A]
      implicit def FactoryType[A: Type, C: Type]: Type[Factory[A, C]] = Factory[A, C]

      implicit def `<:<Type`[From: Type, To: Type]: Type[From <:<< To] = <:<[From, To]
    }

    // Implementations of extension methods

    def isTuple[A](A: Type[A]): Boolean

    def isSubtypeOf[A, B](A: Type[A], B: Type[B]): Boolean
    def isSameAs[A, B](A: Type[A], B: Type[B]): Boolean

    def prettyPrint[A: Type]: String
    def simplePrint[A: Type]: String

    private val AnsiControlCode = "\u001b\\[([0-9]+)m".r
    def extractObjectSingleton[ModuleSingleton: Type]: Option[ModuleSingleton] = {
      // based on https://github.com/MateuszKubuszok/MacroTypeclass ideas
      object ModuleSingleton {
        def unapply(className: String): Option[ModuleSingleton] =
          try
            scala.Option(Class.forName(className).getField("MODULE$").get(null).asInstanceOf[ModuleSingleton])
          catch {
            case _: Throwable => None
          }
      }

      // assuming this is "foo.bar.baz"...
      val name = AnsiControlCode.replaceAllIn(Type.prettyPrint[ModuleSingleton], "")

      scala.collection.Iterator
        .iterate(name + '$')(_.reverse.replaceFirst("[.]", "\\$").reverse)
        .take(name.count(_ == '.') + 1) // ...then this is: "foo.bar.baz$", "foo.bar$baz$", "foo$bar$baz$"...
        .toArray
        .reverse // ...and this is: "foo.bar.baz$", "foo.bar$baz$", "foo$bar$baz$"
        .collectFirst { case ModuleSingleton(value) => value } // attempts: top-level object, object in object, etc
    }
  }
  implicit final protected class TypeOps[A](private val tpe: Type[A]) {

    def <:<[B](another: Type[B]): Boolean = Type.isSubtypeOf(tpe, another)
    def =:=[B](another: Type[B]): Boolean = Type.isSameAs(tpe, another)

    def isPrimitive: Boolean = Type.primitives.exists(tpe <:< _.Underlying)

    def isTuple: Boolean = Type.isTuple(tpe)
    def isAnyVal: Boolean = tpe <:< Type.AnyVal
    def isOption: Boolean = tpe <:< Type.Option(Type.Any)
    def isEither: Boolean = tpe <:< Type.Either(Type.Any, Type.Any)
    def isLeft: Boolean = tpe <:< Type.Either.Left(Type.Any, Type.Any)
    def isRight: Boolean = tpe <:< Type.Either.Right(Type.Any, Type.Any)
    def isIterable: Boolean = tpe <:< Type.Iterable(Type.Any)
    def isMap: Boolean = tpe <:< Type.Map(Type.Any, Type.Any)

    def as_?? : ?? = ExistentialType[A](tpe)
    def as_>?<[L <: A, U >: A]: L >?< U = ExistentialType.Bounded[L, U, A](tpe)
    def as_?>[L <: A]: ?>[L] = ExistentialType.LowerBounded[L, A](tpe)
    def as_?<[U >: A]: ?<[U] = ExistentialType.UpperBounded[U, A](tpe)
  }
  implicit final protected class TypeStringOps[S <: String](private val S: Type[S]) {

    def extractStringSingleton: String = Type.StringLiteral
      .unapply(S)
      .getOrElse {
        // $COVERAGE-OFF$should never happen unless someone mess around with type-level representation
        assertionFailed(s"Invalid string literal type: ${Type.prettyPrint(S)}")
        // $COVERAGE-ON$
      }
      .value
  }
}
private[compiletime] object TypeAlias {
  // Workaround for "type <:< is not a member of object Predef":
  // apparently on 2.13/3 <:< is seen as defined in scala not scala.Predef.
  type <:<<[-From, +To] = From <:< To
}
