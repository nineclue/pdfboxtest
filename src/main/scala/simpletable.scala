import org.apache.pdfbox.pdmodel.{PDDocument, PDPage, PDPageContentStream}
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.vandeseer.easytable.TableDrawer
import org.vandeseer.easytable.settings.{BorderStyle, HorizontalAlignment}
import org.vandeseer.easytable.structure.{Row, Table}
import org.vandeseer.easytable.structure.cell.TextCell
import cats.effect.kernel.Resource
import cats.effect.IO
import cats.effect.unsafe.implicits._
import org.apache.pdfbox.pdmodel.font.{PDType1Font, PDType0Font}
import scala.util.Using
import java.io.Closeable

object SimpleTable:
  def main(as: Array[String]): Unit = 
    // println("안녕? 여러분!")
    // pageWithHangle()
    ioPageWithHangle().unsafeRunSync()

  def page() = 
    Using(PDDocument()) { pdoc => 
      val p1 = PDPage(PDRectangle.A4)
      pdoc.addPage(p1)
      val font = PDType1Font.HELVETICA_BOLD
      Using (PDPageContentStream(pdoc, p1)) { cstream =>
        cstream.beginText()
        cstream.setFont(font, 12)
        cstream.newLineAtOffset(100, 700)
        cstream.showText("Hello, world!")
        cstream.endText()
        // 한글은 표시되지 않는다.
        cstream.newLineAtOffset(200, 700)
        cstream.showText("안녕? 여러분!!")
        cstream.endText()
      }
      pdoc.save("hello.pdf")
    }

  def pageWithHangle() = 
    Using(PDDocument()) { pdoc => 
      val p1 = PDPage(PDRectangle.A4)
      pdoc.addPage(p1)
      // does not work with otf font
      val fstream = getClass.getResourceAsStream("BinggraeⅡ-Bold.ttf")
      val font = PDType0Font.load(pdoc, fstream)
      Using (PDPageContentStream(pdoc, p1)) { cstream =>
        cstream.beginText()
        cstream.setFont(font, 20)
        cstream.newLineAtOffset(100, 700)
        cstream.showText("안녕? 여러분!! 빙그레!")
        // cstream.showText("Hello, world!")
        cstream.endText()
      }
      pdoc.save("hello.pdf")
    }

  def mkResource[A <: Closeable](init: => A): Resource[IO, A] = 
    Resource.make(IO(init))(a => IO(a.close()))

  def ioPageWithHangle() = 
    mkResource(PDDocument()).use { pdoc =>
        (for 
          p1 <- IO.pure(PDPage((PDRectangle.A4)))
          _ = pdoc.addPage(p1)
          fstream = getClass.getResourceAsStream("BinggraeⅡ-Bold.ttf")
          font = PDType0Font.load(pdoc, fstream)
        yield (p1, font)).flatMap({ case (p1, font) =>
          mkResource(PDPageContentStream(pdoc, p1)).use({ cstream =>
            (for 
              _ <- IO.pure(println(s"빙그레 폰트 : ${font.getName}"))
              _ = cstream.beginText()
              _ = cstream.setFont(font, 20)
              _ = cstream.newLineAtOffset(100, 700)
              _ = cstream.showText("안녕? 여러분!! 빙그레!")
              // cstream.showText("Hello, world!")
              _ = cstream.endText()
            yield ())
          }).map(_ => pdoc.save("iohello.pdf"))
        })
    }

  def ioSimpleTable() = 
    Using(PDDocument()) { pdoc =>
      val page = PDPage(PDRectangle.A4)
      pdoc.addPage(page)
    }