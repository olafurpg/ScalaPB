import com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse
import java.io.File
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Paths
import org.scalatest.FunSuite
import scala.reflect.internal.util.BatchSourceFile
import scala.reflect.io.VirtualFile
import scala.tools.nsc
import scala.collection.JavaConverters._
import scala.tools.nsc.reporters.StoreReporter
import scalapb.compiler.ProtobufGenerator

class Compiler {
  private val settings = new nsc.Settings()
  private val reporter = new StoreReporter
  private val d        = Files.createTempDirectory("ogen")
  private val classpath =
    this.getClass.getClassLoader match {
      case u: URLClassLoader =>
        u.getURLs
          .map(u => Paths.get(u.toURI).toString)
          .mkString(File.pathSeparator)
    }
  d.toFile.deleteOnExit()
  settings.classpath.value = classpath
  settings.d.value = d.toString
  val global = new nsc.Global(settings, reporter)

  def compile(response: CodeGeneratorResponse): Unit = {
    reporter.reset()
    val files = response.getFileList.asScala.map { file =>
      val virtualFile = new VirtualFile(file.getName)
      new BatchSourceFile(virtualFile, file.getContent.toCharArray)
    }
    val run = new global.Run
    println(s"Compiling ${files.length} sources")
    run.compileSources(files.toList)
    reporter.infos.find(_.severity.id == reporter.ERROR.id).foreach { msg =>
      response.getFileList.asScala.find(_.getName == msg.pos.source.file.name).foreach { file =>
        file.getContent.lines.zipWithIndex.foreach {
          case (line, i) =>
            println(s"${i + 1}: $line")
        }
      }
      println(s"${msg.pos.source.file.name}:${msg.pos.line}:${msg.pos.column} error: ${msg.msg}")
      println(msg.pos.lineContent)
      println(msg.pos.lineCaret)
    }
    val errors = reporter.infos.count(_.severity.id == reporter.ERROR.id)
    println(s"$errors errors")
  }
}

class ProtobufGeneratorSuite extends FunSuite {
  val compiler = new Compiler
  test("basic") {
    val request = CodeGeneratorRequest
      .parseFrom(Files.readAllBytes(Paths.get("target/semanticdb")))
      .toBuilder
      .setParameter("sealed_oneof")
      .build()
    val response = ProtobufGenerator.handleCodeGeneratorRequest(request)
    compiler.compile(response)
  }

}
