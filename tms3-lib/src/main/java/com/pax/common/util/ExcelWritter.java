/*
 * ============================================================================
 * = COPYRIGHT				
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *   	Copyright (C) 2009-2020 PAX Technology, Inc. All rights reserved.		
 * ============================================================================		
 */
package com.pax.common.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;

public class ExcelWritter implements Closeable {

	private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String ARIAL = "Arial";
	private static final XSSFColor theadColor = new XSSFColor(new byte[] { (byte) 0, (byte) 176, (byte) 240 });
	private static final XSSFColor theadFontColor = new XSSFColor(new byte[] { (byte) 238, (byte) 236, (byte) 225 });
	private static final XSSFColor blackColor = new XSSFColor(new byte[] { (byte) 0, (byte) 0, (byte) 0 });

	private String dateFormat = DATETIME_FORMAT;
	private OutputStream outputStream;

	private SXSSFWorkbook wb = new SXSSFWorkbook(-1);
	private Sheet sh = wb.createSheet();
	private int rowNum = 0;

	private XSSFFont bodyCellFont;
	private CellStyle bodyCellStyle;
	private CellStyle textCellStyle;
	private CellStyle dateCellStyle;

	public ExcelWritter() {
		super();
	}

	public ExcelWritter(OutputStream outputStream) {
		super();
		this.outputStream = outputStream;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public void open() {
		wb = new SXSSFWorkbook(-1);
		sh = wb.createSheet();
		rowNum = 0;

		bodyCellFont = createBodyCellFont(wb);
		bodyCellStyle = createBodyCellStyle(wb, bodyCellFont);
		textCellStyle = createTextCellStyle(wb, bodyCellFont);
		dateCellStyle = createDateCellStyle(wb, bodyCellFont);
	}

	public void writeCaption(String caption, int columns) {
		Row row = sh.createRow(rowNum);
		CellRangeAddress captionAddress = new CellRangeAddress(rowNum, rowNum, 0, columns - 1);
		sh.addMergedRegion(captionAddress);
		Cell cell = row.createCell(0);
		cell.setCellValue(caption);
		CellStyle cellStyle = createCaptionCellStyle(wb);
		cell.setCellStyle(cellStyle);
		rowNum++;
	}

	public void writeTitle(String[] titles) {
		Row row = sh.createRow(rowNum);
		CellStyle cellStyle = createHeadCellStyle(wb);
		for (int colNum = 0; colNum < titles.length; colNum++) {
			Cell cell = row.createCell(colNum);
			cell.setCellValue(titles[colNum]);
			cell.setCellStyle(cellStyle);
		}
		rowNum++;
	}

	public void writeContent(Object... values) throws IOException {
		Row row = sh.createRow(rowNum);
		for (int cellnum = 0; cellnum < values.length; cellnum++) {
			Cell cell = row.createCell(cellnum);
			Object value = values[cellnum];
			if (value == null) {
				cell.setCellValue("");
				cell.setCellStyle(bodyCellStyle);
			} else if (value instanceof String) {
				cell.setCellValue((String) value);
				cell.setCellStyle(textCellStyle);
			} else if (value instanceof Date) {
				cell.setCellValue((Date) value);
				cell.setCellStyle(dateCellStyle);
			} else if (value instanceof Number) {
				cell.setCellValue(((Number) value).doubleValue());
				cell.setCellStyle(bodyCellStyle);
			} else if (value instanceof Boolean) {
				cell.setCellValue((Boolean) value);
				cell.setCellStyle(bodyCellStyle);
			} else {
				cell.setCellValue(value.toString());
				cell.setCellStyle(textCellStyle);
			}
		}
		rowNum++;
		if (rowNum % 100 == 0) {
			((SXSSFSheet) sh).flushRows();
		}
	}

	public void writeLongContent(Object longContent, Object... values) throws IOException {
		Row row = sh.createRow(rowNum);
		int colNum = 0;
		while (colNum < values.length) {
			Cell cell = row.createCell(colNum);
			Object value = values[colNum];
			if (value == null) {
				cell.setCellValue("");
				cell.setCellStyle(bodyCellStyle);
			} else if (value instanceof String) {
				cell.setCellValue((String) value);
				cell.setCellStyle(textCellStyle);
			} else if (value instanceof Date) {
				cell.setCellValue((Date) value);
				cell.setCellStyle(dateCellStyle);
			} else if (value instanceof Number) {
				cell.setCellValue(((Number) value).doubleValue());
				cell.setCellStyle(bodyCellStyle);
			} else if (value instanceof Boolean) {
				cell.setCellValue((Boolean) value);
				cell.setCellStyle(bodyCellStyle);
			} else {
				cell.setCellValue(value.toString());
				cell.setCellStyle(textCellStyle);
			}
			colNum++;
		}
		Cell cell = row.createCell(colNum);
		cell.setCellStyle(bodyCellStyle);
		if (longContent != null) {
			cell.setCellValue(longContent.toString());
		} else {
			cell.setCellValue("");
		}
		rowNum++;
		if (rowNum % 100 == 0) {
			((SXSSFSheet) sh).flushRows();
		}
	}

	public void setWidths(int[] widths) {
		for (int cellNum = 0; cellNum < widths.length; cellNum++) {
			if (widths[cellNum] <= 0) {
				sh.autoSizeColumn(cellNum);
			} else {
				sh.setColumnWidth(cellNum, widths[cellNum] * 256);
			}
		}
	}

	@Override
	public void close() throws IOException {
		// wirte and close
		if (wb != null) {
			wb.write(outputStream);
			wb.dispose();
			wb = null;
		}
	}

	private CellStyle createCaptionCellStyle(Workbook wb) {
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

		Font font = wb.createFont();
		font.setFontName(ARIAL);
		font.setColor(blackColor.getIndexed());
		font.setFontHeightInPoints((short) 14);
		cellStyle.setFont(font);
		return cellStyle;
	}

	private CellStyle createHeadCellStyle(Workbook wb) {
		XSSFCellStyle cellStyle = (XSSFCellStyle) createCellStyle(wb);
		cellStyle.setFillForegroundColor(theadColor);
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

		XSSFFont font = (XSSFFont) wb.createFont();
		font.setFontName(ARIAL);
		font.setFontHeightInPoints((short) 12);
		font.setBold(true);
		font.setColor(theadFontColor);
		cellStyle.setFont(font);
		return cellStyle;
	}

	private CellStyle createBodyCellStyle(Workbook wb, XSSFFont font) {
		XSSFCellStyle cellStyle = (XSSFCellStyle) createCellStyle(wb);
		cellStyle.setFont(font);
		cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
		cellStyle.setWrapText(true);
		return cellStyle;
	}

	private CellStyle createTextCellStyle(Workbook wb, XSSFFont font) {
		XSSFCellStyle stringStyle = (XSSFCellStyle) createCellStyle(wb);
		stringStyle.setFont(font);
		stringStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("text"));
		stringStyle.setAlignment(CellStyle.ALIGN_LEFT);
		stringStyle.setWrapText(true);//自动换行
//		stringStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);//垂直居中
		return stringStyle;
	}

	private CellStyle createDateCellStyle(Workbook wb, XSSFFont font) {
		XSSFCellStyle dateStyle = (XSSFCellStyle) createCellStyle(wb);
		dateStyle.setFont(font);

		CreationHelper createHelper = wb.getCreationHelper();
		dateStyle.setDataFormat(createHelper.createDataFormat().getFormat(dateFormat));
		dateStyle.setAlignment(CellStyle.ALIGN_LEFT);
		return dateStyle;
	}

	private XSSFFont createBodyCellFont(Workbook wb) {
		XSSFFont font = (XSSFFont) wb.createFont();
		font.setFontName(ARIAL);
		font.setFontHeightInPoints((short) 12);
		return font;
	}

	private CellStyle createCellStyle(Workbook wb) {
		CellStyle cellStyle = wb.createCellStyle();
		// 设置一个单元格边框颜色
		cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
		cellStyle.setBorderTop(CellStyle.BORDER_THIN);
		cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
		cellStyle.setBorderRight(CellStyle.BORDER_THIN);
		// 设置一个单元格边框颜色
		cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
		
		return cellStyle;
	}
}
