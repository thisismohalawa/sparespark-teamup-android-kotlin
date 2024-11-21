package sparespark.teamup.data.exportApi

import android.os.Environment
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import sparespark.teamup.core.getCalendarDateTime
import sparespark.teamup.core.internal.getTotal
import sparespark.teamup.core.internal.toActiveText
import sparespark.teamup.core.internal.toFormatedString
import sparespark.teamup.core.internal.toTypeText
import sparespark.teamup.core.launchASuspendTaskScope
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.stock.Stock
import sparespark.teamup.data.model.transaction.Transaction
import java.io.File
import java.io.FileOutputStream

private const val DATE_EXPORT_FORMAT = "dd-MM-yyyy-HHmm"
private const val FILE_NAME = "teamup-backups"
private const val COL_WIDTH = 12800

class ExcelAPIImpl : ExcelAPI {
    private fun XSSFWorkbook.getHeaderCellStyle(): XSSFCellStyle {
        val headerStyle = this.createCellStyle()
        val font = this.createFont()
        font.bold = true
        font.color = IndexedColors.WHITE.getIndex()
        headerStyle.setAlignment(HorizontalAlignment.CENTER)
        headerStyle.fillForegroundColor = IndexedColors.BLUE_GREY.getIndex()
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)
        headerStyle.setFont(font)
        return headerStyle
    }

    private fun XSSFWorkbook.getSubHeaderCellStyle(): XSSFCellStyle {
        val headerStyle: XSSFCellStyle = this.createCellStyle()
        headerStyle.setAlignment(HorizontalAlignment.CENTER)
        return headerStyle
    }

    override suspend fun buildStockListFile(list: List<Stock>): Result<Exception, Unit> =
        Result.build {
            launchASuspendTaskScope {
                if (list.isEmpty()) return@launchASuspendTaskScope
                val strDate = getCalendarDateTime(DATE_EXPORT_FORMAT)
                val root = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                    FILE_NAME
                )

                if (!root.exists()) root.mkdirs()
                val path = File(root, "/stock-$strDate.xlsx")
                val workbook = XSSFWorkbook()
                val outputStream = FileOutputStream(path)
                val sheet: XSSFSheet = workbook.createSheet("Data-Backup")
                var row: XSSFRow = sheet.createRow(0)
                var cell: XSSFCell = row.createCell(0)

                cell.setCellValue("Creation Date")
                cell.cellStyle = workbook.getHeaderCellStyle()

                cell = row.createCell(1)
                cell.setCellValue("Product")
                cell.cellStyle = workbook.getHeaderCellStyle()


                cell = row.createCell(2)
                cell.setCellValue("Datasource")
                cell.cellStyle = workbook.getHeaderCellStyle()

                cell = row.createCell(3)
                cell.setCellValue("Size/Pair")
                cell.cellStyle = workbook.getHeaderCellStyle()

                cell = row.createCell(4)
                cell.setCellValue("Sell")
                cell.cellStyle = workbook.getHeaderCellStyle()

                cell = row.createCell(5)
                cell.setCellValue("Update By")
                cell.cellStyle = workbook.getHeaderCellStyle()

                cell = row.createCell(6)
                cell.setCellValue("Update Date")
                cell.cellStyle = workbook.getHeaderCellStyle()

                cell = row.createCell(7)
                cell.setCellValue("Note")
                cell.cellStyle = workbook.getHeaderCellStyle()

                for (i in list.indices) {
                    row = sheet.createRow(i + 1)

                    cell = row.createCell(0)
                    cell.setCellValue(list[i].creationDate)
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(0, COL_WIDTH)

                    cell = row.createCell(1)
                    cell.setCellValue(list[i].productEntry.name)
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(1, COL_WIDTH)


                    cell = row.createCell(2)
                    cell.setCellValue(list[i].datasourceId.toString())
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(2, COL_WIDTH)

                    cell = row.createCell(3)
                    cell.setCellValue("X")
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(3, COL_WIDTH)

                    cell = row.createCell(4)
                    cell.setCellValue(list[i].sell.toTypeText())
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(4, COL_WIDTH)

                    cell = row.createCell(5)
                    cell.setCellValue(list[i].updateBy)
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(5, COL_WIDTH)

                    cell = row.createCell(6)
                    cell.setCellValue(list[i].updateDate)
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(6, COL_WIDTH)

                    cell = row.createCell(7)
                    cell.setCellValue(list[i].note)
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(7, COL_WIDTH)
                }
                workbook.write(outputStream)
                outputStream.close()
            }
        }

    override suspend fun buildTransactionListFile(list: List<Transaction>): Result<Exception, Unit> =
        Result.build {
            launchASuspendTaskScope {
                if (list.isEmpty()) return@launchASuspendTaskScope
                val strDate = getCalendarDateTime(DATE_EXPORT_FORMAT)
                val root = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                    FILE_NAME
                )
                if (!root.exists()) root.mkdirs()
                val path = File(root, "/transaction-$strDate.xlsx")
                val workbook = XSSFWorkbook()
                val outputStream = FileOutputStream(path)
                val sheet: XSSFSheet = workbook.createSheet("Data-Backup")
                var row: XSSFRow = sheet.createRow(0)
                var cell: XSSFCell = row.createCell(0)

                cell.setCellValue("Creation Date")
                cell.cellStyle = workbook.getHeaderCellStyle()

                cell = row.createCell(1)
                cell.setCellValue("Client")
                cell.cellStyle = workbook.getHeaderCellStyle()

                cell = row.createCell(2)
                cell.setCellValue("Type")
                cell.cellStyle = workbook.getHeaderCellStyle()

                cell = row.createCell(3)
                cell.setCellValue("Price")
                cell.cellStyle = workbook.getHeaderCellStyle()

                cell = row.createCell(4)
                cell.setCellValue("Quantity")
                cell.cellStyle = workbook.getHeaderCellStyle()

                cell = row.createCell(5)
                cell.setCellValue("Total")
                cell.cellStyle = workbook.getHeaderCellStyle()

                cell = row.createCell(6)
                cell.setCellValue("Status")
                cell.cellStyle = workbook.getHeaderCellStyle()

                cell = row.createCell(7)
                cell.setCellValue("Update By")
                cell.cellStyle = workbook.getHeaderCellStyle()

                cell = row.createCell(8)
                cell.setCellValue("Update Date")
                cell.cellStyle = workbook.getHeaderCellStyle()

                cell = row.createCell(9)
                cell.setCellValue("Note")
                cell.cellStyle = workbook.getHeaderCellStyle()

                for (i in list.indices) {
                    row = sheet.createRow(i + 1)

                    cell = row.createCell(0)
                    cell.setCellValue(list[i].creationDate)
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(0, COL_WIDTH)

                    cell = row.createCell(1)
                    cell.setCellValue(list[i].clientEntry.name + "-" + list[i].clientEntry.city)
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(1, COL_WIDTH)

                    cell = row.createCell(2)
                    cell.setCellValue(list[i].sell.toTypeText())
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(2, COL_WIDTH)

                    cell = row.createCell(3)
                    cell.setCellValue(list[i].assetEntry.assetPrice ?: 0.0)
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(3, COL_WIDTH)

                    cell = row.createCell(4)
                    cell.setCellValue(list[i].assetEntry.quantity ?: 0.0)
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(4, COL_WIDTH)

                    cell = row.createCell(5)
                    cell.setCellValue(
                        getTotal(
                            list[i].assetEntry.assetPrice,
                            list[i].assetEntry.quantity
                        ).toFormatedString()
                    )
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(5, COL_WIDTH)

                    cell = row.createCell(6)
                    cell.setCellValue((list[i].active.toActiveText()))
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(6, COL_WIDTH)

                    cell = row.createCell(7)
                    cell.setCellValue((list[i].updateBy))
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(7, COL_WIDTH)

                    cell = row.createCell(8)
                    cell.setCellValue((list[i].updateDate))
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(8, COL_WIDTH)

                    cell = row.createCell(9)
                    cell.setCellValue((list[i].note))
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(9, COL_WIDTH)
                }

                workbook.write(outputStream)
                outputStream.close()
            }
        }

}