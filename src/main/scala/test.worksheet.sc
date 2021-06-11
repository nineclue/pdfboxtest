import cats.Apply
import cats.Semigroupal
import cats.FlatMap
import cats.Applicative
import cats.Functor
import cats.kernel.Semigroup
import cats.implicits._

"123".combine("456")
Semigroup[String].combine("hello", "world")
// Functor[Seq[_]].map(Seq("hello", "world"))(_.length)
Applicative[Seq].pure(123)
FlatMap[Seq].flatMap(Seq("hello", "w"))(s => s * s.length)
Semigroupal[Seq].product(Seq(1,2,3), Seq("hello", "world"))
Apply[Seq].ap(Seq((str:String) => str * str.length))(Seq("hello", "world", "허서구"))
Apply[Option].ap(Option.empty)(Some("hello"))
Apply[Option].ap(Some((s: String) => s.length))(Some("hello"))
Apply[Option].map2(Some(2), Some("hello"))({ case ((n, s)) => s * n })