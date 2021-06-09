import org.apache.pdfbox.pdmodel.{PDDocument, PDPage, PDPageContentStream}
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.vandeseer.easytable.TableDrawer
import org.vandeseer.easytable.settings.{BorderStyle, HorizontalAlignment}
import org.vandeseer.easytable.structure.{Row, Table}
import org.vandeseer.easytable.structure.cell.TextCell
import cats.effect.kernel.Resource
import org.apache.pdfbox.pdmodel.font.{PDType1Font, PDType0Font}
import scala.util.Using

object SimpleTable:
  def main(as: Array[String]): Unit = 
    // println("안녕? 여러분!")
    pageWithHangle()

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
      // val fstream = Thread.currentThread().getContextClassLoader().getResource("BMEULJIROTTF.ttf")
      val fstream = getClass.getResourceAsStream("BMEULJIROTTF.ttf")
      val font = PDType0Font.load(pdoc, fstream)
      Using (PDPageContentStream(pdoc, p1)) { cstream =>
        cstream.beginText()
        cstream.setFont(font, 12)
        cstream.newLineAtOffset(100, 700)
        cstream.showText("안녕? 여러분!!")
        // cstream.showText("Hello, world!")
        cstream.endText()
        // 한글은 표시되지 않는다.
        cstream.newLineAtOffset(200, 700)
        cstream.showText("안녕? 여러분!!")
        cstream.endText()
      }
      pdoc.save("hello.pdf")
    }