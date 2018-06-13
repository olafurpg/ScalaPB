import com.trueaccord.proto.e2e.NoBox
import com.trueaccord.proto.e2e.sealed_oneof._
import org.scalatest._

class SealedOneofSpec extends FlatSpec with MustMatchers {
  val expr = Add(Lit(1), Add(Lit(2), Lit(3)))

  "sealed_oneof" should "serialize as supertype" in {
    val bytes  =expr.asExprMessage.toByteArray
  }

}
