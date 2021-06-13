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
import java.awt.Color

object SimpleTable:
  private val binggraeFileName = "BinggraeⅡ.ttf"

  def main(as: Array[String]): Unit = 
    // println("안녕? 여러분!")
    // pageWithHangle()
    // ioPageWithHangle().unsafeRunSync()
    // ioPageWithHangul2().unsafeRunSync()
    ioSimpleTable().unsafeRunSync()

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

  def ioPageWithHangul2() =
    mkResource(PDDocument()).use { pdoc =>
      IO.pure(PDPage(PDRectangle.A4)).flatMap { p1 =>
        pdoc.addPage(p1)
        IO(getClass.getResourceAsStream("BinggraeⅡ.ttf")).flatMap { fstream =>
          val font = PDType0Font.load(pdoc, fstream)
          val cs = PDPageContentStream(pdoc, p1)
          mkResource(cs).use({ cs =>
            for 
              _ <- IO.pure(println(font.getName))
              _ = cs.beginText()
              _ = cs.setFont(font, 20)
              _ = cs.newLineAtOffset(100, 700)
              _ = cs.showText("이봐!! 허서구!")            
            yield ()
          }).flatMap(_ => IO(pdoc.save("iohello2.pdf")))
        }
      }
    }

  def ioSimpleTable() = 
    mkResource(PDDocument()).use { pdoc =>
        IO.pure(PDPage(PDRectangle.A4)).flatMap { page =>
          pdoc.addPage(page)
          IO(getClass.getResourceAsStream(binggraeFileName)).flatMap { fstream =>
              IO(PDType0Font.load(pdoc, fstream)).flatMap({ bfont =>
                val contentStream = PDPageContentStream(pdoc, page)
                mkResource(contentStream).use({ contentStream =>
                val myTab = Table.builder().font(bfont).
                  addColumnsOfWidth(200, 200).padding(2).
                  addRow(Row.builder().
                    add(TextCell.builder().text("일 일").borderWidth(4).borderColorLeft(Color.MAGENTA).backgroundColor(Color.WHITE).build()).
                    add(TextCell.builder().text("하나 둘").borderWidth(0).backgroundColor(Color.YELLOW).build()).
                    build()).
                  addRow(Row.builder().padding(10).
                    add(TextCell.builder().text("둘 하나").textColor(Color.RED).build()).
                    add(TextCell.builder().text("이 이").borderWidthRight(1f).borderStyleRight(BorderStyle.DOTTED).horizontalAlignment(HorizontalAlignment.RIGHT).build()).
                    build()).
                  build()
              
                val tabDrawer = TableDrawer.builder().contentStream(contentStream).
                  startX(20f).startY(page.getMediaBox().getUpperRightY - 20f).
                  table(myTab).build()
                
                IO(tabDrawer.draw())
              })
            })
          }
        }.flatMap(_ => IO(pdoc.save("iotable.pdf")))
    }