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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {
	private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private DateFormat dateFormat = new SimpleDateFormat(DATETIME_FORMAT);

	public void setDateFormat(String dateFormat) {
		this.dateFormat = new SimpleDateFormat(dateFormat);
	}

	public List<String[]> readExcel(File file, String filename, int maxColumns, int maxRows, int headRows)
			throws IOException, InvalidFormatException {
		String suffix = filename.lastIndexOf(".") == -1 ? "" : filename.substring(filename.lastIndexOf(".") + 1);
		if ("xls".equalsIgnoreCase(suffix)) {
			return readXls(file, maxColumns, maxRows, headRows);
		} else if ("xlsx".equalsIgnoreCase(suffix)) {
			return readXlsx(file, maxColumns, maxRows, headRows);
		} else {
			throw new InvalidFormatException("invalid excel file format");
		}
	}

	public List<String[]> readXls(File file, int maxColumns, int maxRows, int headRows) throws IOException {
		try (FileInputStream in = new FileInputStream(file)) {
			HSSFWorkbook workbook = new HSSFWorkbook(in);
			return readExcel2003(workbook, maxColumns, maxRows, headRows);
		}
	}

	public List<String[]> readXlsx(File file, int maxColumns, int maxRows, int headRows)
			throws InvalidFormatException, IOException {
		try (OPCPackage pkg = OPCPackage.open(file.getAbsolutePath())) {
			XSSFWorkbook wb = new XSSFWorkbook(pkg);
			return readExcel2007(wb, maxColumns, maxRows, headRows);
		}
	}

	private List<String[]> readExcel2007(XSSFWorkbook workbook, int maxColumns, int maxRows, int headRows)
			throws IOException {
		List<String[]> result = new ArrayList<String[]>();
		int sheets = workbook.getNumberOfSheets();
		for (int st = 0; st < sheets && result.size() < maxRows; st++) {
			readExcel2007Sheet(workbook.getSheetAt(st), maxColumns, maxRows, headRows, result);
		}
		return result;
	}

	private void readExcel2007Sheet(XSSFSheet sheet, int maxColumns, int maxRows, int headRows, List<String[]> result) {
		XSSFRow row;
		XSSFCell cell;

		int minRowIx = sheet.getFirstRowNum();
		if (headRows > 0) {
			minRowIx += headRows; // 去掉第一行标题行
		}
		int maxRowIx = sheet.getLastRowNum();
		if (maxRowIx - minRowIx + 1 > maxRows - result.size()) {
			maxRowIx = minRowIx + (maxRows - result.size()) - 1;
		}

		boolean emptyLine = false;
		for (int rowIndex = minRowIx; rowIndex <= maxRowIx; rowIndex++) {
			row = sheet.getRow(rowIndex);
			int minColIx = row == null ? -1 : row.getFirstCellNum();
			if (row == null || minColIx == -1 || emptyLine) {
				// 1. 遇到不连续的行号，终止处理
				// 2. 遇到空行，终止处理
				// 3. 上一行是空行
				break;
			}

			int maxColIx = row.getLastCellNum();
			if (maxColIx - minColIx > maxColumns) {
				maxColIx = minColIx + maxColumns;
			}

			emptyLine = true;
			String[] values = new String[maxColumns == Integer.MAX_VALUE ? maxColIx - minColIx : maxColumns];
			for (int ix = 0, col = minColIx; col < maxColIx; ix++, col++) {
				cell = row.getCell(col);
				if (cell == null) {
					continue;
				}
				String value = getFormatValue2007(cell);
				value = StringUtils.trimToNull(value);
				if (value != null) {
					emptyLine = false;
					values[ix] = value;
				}
			}

			if (!emptyLine) {
				result.add(values);
			}
		}
	}

	private String getFormatValue2007(XSSFCell cell) {
		switch (cell.getCellType()) {
		case XSSFCell.CELL_TYPE_NUMERIC:
			return getNumericCellValue(cell);
		case XSSFCell.CELL_TYPE_STRING:
			return cell.getStringCellValue();
		case XSSFCell.CELL_TYPE_FORMULA:
			return getFormulaCellValue(cell);
		case XSSFCell.CELL_TYPE_BLANK:
			return "";
		case XSSFCell.CELL_TYPE_BOOLEAN:
			return cell.getBooleanCellValue() + "";
		case XSSFCell.CELL_TYPE_ERROR:
			return "";
		default:
			return cell.toString();
		}
	}

	private String getNumericCellValue(XSSFCell cell) {
		if (HSSFDateUtil.isCellDateFormatted(cell)) {
			Date date = cell.getDateCellValue();
			if (date != null) {
				return dateFormat.format(date);
			} else {
				return "";
			}
		} else {
			return new DecimalFormat("0").format(cell.getNumericCellValue());
		}
	}

	private String getFormulaCellValue(XSSFCell cell) {
		if (!"".equals(cell.getStringCellValue())) {
			return cell.getStringCellValue();
		} else {
			return cell.getNumericCellValue() + "";
		}
	}

	private List<String[]> readExcel2003(HSSFWorkbook workbook, int maxColumns, int maxRows, int headRows)
			throws IOException {
		HSSFSheet sheet;
		List<String[]> result = new ArrayList<String[]>();

		int sheets = workbook.getNumberOfSheets();
		for (int i = 0; i < sheets && result.size() < maxRows; i++) {
			sheet = workbook.getSheetAt(i);
			readExcel2003Sheet(sheet, maxColumns, maxRows, headRows, result);
		}
		return result;
	}

	private void readExcel2003Sheet(HSSFSheet sheet, int maxColumns, int maxRows, int headRows, List<String[]> result) {
		HSSFRow row;
		HSSFCell cell;

		int minRowIx = sheet.getFirstRowNum();
		if (headRows > 0) {
			minRowIx += headRows;
		}
		int maxRowIx = sheet.getLastRowNum();
		if (maxRowIx - minRowIx + 1 > maxRows - result.size()) {
			maxRowIx = minRowIx + (maxRows - result.size()) - 1;
		}

		boolean emptyRow = false;
		for (int rowIndex = minRowIx; rowIndex <= maxRowIx; rowIndex++) {
			row = sheet.getRow(rowIndex);
			if (row == null || emptyRow) {
				// 1. 遇到不连续的行号，终止处理
				// 2. 遇到空行，终止处理
				break;
			}

			int minColIx = row.getFirstCellNum();
			int maxColIx = row.getLastCellNum();
			if (maxColIx - minColIx > maxColumns) {
				maxColIx = minColIx + maxColumns;
			}

			emptyRow = true;
			String[] values = new String[maxColumns == Integer.MAX_VALUE ? maxColIx - minColIx : maxColumns];
			for (int ix = 0, col = minColIx; col < maxColIx; ix++, col++) {
				cell = row.getCell(col);
				if (cell == null) {
					continue;
				}
				String value = getFormatValue2003(cell);
				value = StringUtils.trimToNull(value);
				if (value != null) {
					emptyRow = false;
					values[ix] = value;
				}
			}

			if (!emptyRow) {
				result.add(values);
			}
		}
	}

	private String getFormatValue2003(HSSFCell cell) {
		switch (cell.getCellType()) {
		case XSSFCell.CELL_TYPE_NUMERIC:
			return getNumericCellValue(cell);
		case XSSFCell.CELL_TYPE_STRING:
			return cell.getStringCellValue();
		case XSSFCell.CELL_TYPE_FORMULA:
			return getFomulaCellValue(cell);
		case XSSFCell.CELL_TYPE_BLANK:
			return "";
		case XSSFCell.CELL_TYPE_BOOLEAN:
			return cell.getBooleanCellValue() + "";
		case XSSFCell.CELL_TYPE_ERROR:
			return "";
		default:
			return cell.toString();
		}
	}

	private String getNumericCellValue(HSSFCell cell) {
		if (HSSFDateUtil.isCellDateFormatted(cell)) {
			Date date = cell.getDateCellValue();
			if (date != null) {
				return dateFormat.format(date);
			} else {
				return "";
			}
		} else {
			return new DecimalFormat("0").format(cell.getNumericCellValue());
		}
	}

	private String getFomulaCellValue(HSSFCell cell) {
		if (!"".equals(cell.getStringCellValue())) {
			return cell.getStringCellValue();
		} else {
			return cell.getNumericCellValue() + "";
		}
	}

}
