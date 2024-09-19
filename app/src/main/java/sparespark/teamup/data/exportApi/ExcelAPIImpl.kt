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
import sparespark.teamup.core.DATE_EXPORT_FORMAT
import sparespark.teamup.core.toFormatedString
import sparespark.teamup.core.getCalendarDateTime
import sparespark.teamup.core.getTotal
import sparespark.teamup.core.launchASuspendTaskScope
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.expense.Expense
import sparespark.teamup.data.model.item.Item
import java.io.File
import java.io.FileOutputStream


internal fun Boolean.toActiveText(): String = if (this) "Active/غير مكتمل" else "Inactive/مكتمل"

internal fun Boolean.toTypeText(): String = if (this) "Sell/بيع" else "Buy/شراء"

internal fun Boolean.toIncomeText(): String = if (this) "Income/دخل" else "Expenses/مصاريف"


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

    override suspend fun buildItemsFile(list: List<Item>?): Result<Exception, Unit> =
        Result.build {
            launchASuspendTaskScope {
                if (list.isNullOrEmpty()) return@launchASuspendTaskScope
                val strDate = getCalendarDateTime(DATE_EXPORT_FORMAT)
                val root = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                    "teamup-backups"
                )
                if (!root.exists()) root.mkdirs()
                val path = File(root, "/items-$strDate.xlsx")
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
                    sheet.setColumnWidth(0, (50) * 256)

                    cell = row.createCell(1)
                    cell.setCellValue(list[i].clientEntry.name + "-" + list[i].clientEntry.city)
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(1, (50) * 256)

                    cell = row.createCell(2)
                    cell.setCellValue(list[i].sell.toTypeText())
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(2, (30) * 256)

                    cell = row.createCell(3)
                    cell.setCellValue(list[i].assetEntry.assetPrice ?: 0.0)
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(3, (30) * 256)

                    cell = row.createCell(4)
                    cell.setCellValue(list[i].assetEntry.quantity ?: 0.0)
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(4, (30) * 256)

                    cell = row.createCell(5)
                    cell.setCellValue(
                        getTotal(
                            list[i].assetEntry.assetPrice,
                            list[i].assetEntry.quantity
                        ).toFormatedString()
                    )
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(5, (50) * 256)

                    cell = row.createCell(6)
                    cell.setCellValue((list[i].active.toActiveText()))
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(6, (30) * 256)

                    cell = row.createCell(7)
                    cell.setCellValue((list[i].updateBy))
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(7, (30) * 256)

                    cell = row.createCell(8)
                    cell.setCellValue((list[i].updateDate))
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(8, (50) * 256)

                    cell = row.createCell(9)
                    cell.setCellValue((list[i].note))
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(9, (50) * 256)
                }

                workbook.write(outputStream)
                outputStream.close()
            }
        }

    override suspend fun buildExpensesFile(list: List<Expense>?): Result<Exception, Unit> =
        Result.build {
            launchASuspendTaskScope {
                if (list.isNullOrEmpty()) return@launchASuspendTaskScope
                val strDate = getCalendarDateTime(DATE_EXPORT_FORMAT)
                val root = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                    "teamup-backups"
                )
                if (!root.exists()) root.mkdirs()
                val path = File(root, "/expenses-$strDate.xlsx")
                val workbook = XSSFWorkbook()
                val outputStream = FileOutputStream(path)
                val sheet: XSSFSheet = workbook.createSheet("Data-Backup")

                var row: XSSFRow = sheet.createRow(0)
                var cell: XSSFCell = row.createCell(0)

                cell.setCellValue("Creation Date")
                cell.cellStyle = workbook.getHeaderCellStyle()

                cell = row.createCell(1)
                cell.setCellValue("Created By")
                cell.cellStyle = workbook.getHeaderCellStyle()

                cell = row.createCell(2)
                cell.setCellValue("Cost")
                cell.cellStyle = workbook.getHeaderCellStyle()

                cell = row.createCell(3)
                cell.setCellValue("Note")
                cell.cellStyle = workbook.getHeaderCellStyle()

                cell = row.createCell(4)
                cell.setCellValue("Shared With")
                cell.cellStyle = workbook.getHeaderCellStyle()

                cell = row.createCell(5)
                cell.setCellValue("Income")
                cell.cellStyle = workbook.getHeaderCellStyle()
                for (i in list.indices) {
                    row = sheet.createRow(i + 1)
                    cell = row.createCell(0)
                    cell.setCellValue(list[i].creationDate)
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(0, (50) * 256)

                    cell = row.createCell(1)
                    cell.setCellValue(list[i].createdBy)
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(1, (50) * 256)

                    cell = row.createCell(2)
                    cell.setCellValue(list[i].cost.toFormatedString())
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(2, (30) * 256)

                    cell = row.createCell(3)
                    cell.setCellValue(list[i].note)
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(3, (30) * 256)

                    cell = row.createCell(4)
                    cell.setCellValue(list[i].name)
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(4, (30) * 256)

                    cell = row.createCell(5)
                    cell.setCellValue(list[i].income.toIncomeText())
                    cell.cellStyle = workbook.getSubHeaderCellStyle()
                    sheet.setColumnWidth(5, (50) * 256)
                }
                workbook.write(outputStream)
                outputStream.close()
            }
        }
}