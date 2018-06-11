package scalapb

import com.google.protobuf.CodedInputStream
import com.google.protobuf.CodedOutputStream
import scalapb.descriptors.Descriptor
import scalapb.descriptors.FieldDescriptor
import scalapb.descriptors.PValue
import scalapb.descriptors.Reads

trait EmptyMessage[T] extends GeneratedMessage with Message[T] with GeneratedMessageCompanion [T] {
//  override def defaultInstance: T = ???
//  override def javaDescriptor: com.google.protobuf.Descriptors.Descriptor = ???
//  override def scalaDescriptor: Descriptor = ???
  override def writeTo(output: CodedOutputStream): Unit = ()
  override def getFieldByNumber(fieldNumber: Int): Any = throw new MatchError(fieldNumber)
  override def getField(field: FieldDescriptor): PValue = throw new MatchError(field)
  override def companion: GeneratedMessageCompanion[_] = this
  override def serializedSize: Int = 0
  override def toProtoString: String = ""
  override def mergeFrom(input: CodedInputStream): T = {
    var _done__ = false
    while (!_done__) {
      val _tag__ = input.readTag()
      _tag__ match {
        case 0 => _done__ = true
        case tag => input.skipField(tag)
      }
    }
    defaultInstance
  }
  override def fromFieldsMap(fields: Map[Any, Any]): T = defaultInstance
  override def nestedMessagesCompanions: Seq[GeneratedMessageCompanion[_]] = Nil
  override def messageReads: Reads[T] = Reads(_ => defaultInstance)
  override def messageCompanionForFieldNumber(field: Int): GeneratedMessageCompanion[_] = throw new MatchError(field)
  override def enumCompanionForFieldNumber(field: Int): GeneratedEnumCompanion[_] = throw new MatchError(field)
}
