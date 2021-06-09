import org.apache.pdfbox.pdmodel.{PDDocument, PDPage, PDPageContentStream}
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.vandeseer.easytable.TableDrawer
import org.vandeseer.easytable.settings.{BorderStyle, HorizontalAlignment}
import org.vandeseer.easytable.structure.{Row, Table}
import org.vandeseer.easytable.structure.cell.TextCell
import cats.effect.kernel.Resource

object SimpleTable:
  def main(as: Array[String]): Unit = 
    println("안녕? 여러분!")

  def pageWithHangul() = 
    val pdoc = PDDocument()
    val p1 = PDPage(PDRectangle.A4)
    pdoc.addPage(p1)
    val cstream = PDPageContentStream(pdoc, p1)
