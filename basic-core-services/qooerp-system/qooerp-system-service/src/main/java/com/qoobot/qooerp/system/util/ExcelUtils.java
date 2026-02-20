package com.qoobot.qooerp.system.util;

import com.qoobot.qooerp.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel工具类
 */
@Slf4j
public class ExcelUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 导出Excel
     */
    public static <T> void exportExcel(HttpServletResponse response, String fileName,
                                         List<T> dataList, Class<T> clazz) throws IOException {
        // 获取表头
        ExcelHeader[] headers = getExcelHeaders(clazz);

        // 创建工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        // 创建表头行
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = createHeaderStyle(workbook);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i].name());
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, headers[i].width() * 256);
        }

        // 填充数据行
        CellStyle dataStyle = createDataStyle(workbook);
        for (int i = 0; i < dataList.size(); i++) {
            Row row = sheet.createRow(i + 1);
            T data = dataList.get(i);

            for (int j = 0; j < headers.length; j++) {
                Cell cell = row.createCell(j);
                Object value = getFieldValue(data, headers[j].field());
                setCellValue(cell, value);
                cell.setCellStyle(dataStyle);
            }
        }

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName);

        // 写入响应流
        try (OutputStream out = response.getOutputStream()) {
            workbook.write(out);
            out.flush();
        }

        workbook.close();
    }

    /**
     * 导入Excel
     */
    public static <T> List<T> importExcel(MultipartFile file, Class<T> clazz) throws IOException {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 获取表头
        ExcelHeader[] headers = getExcelHeaders(clazz);

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        List<T> dataList = new ArrayList<>();

        // 从第二行开始读取（第一行是表头）
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            try {
                T instance = clazz.getDeclaredConstructor().newInstance();

                for (int j = 0; j < headers.length; j++) {
                    Cell cell = row.getCell(j);
                    Object value = getCellValue(cell);
                    setFieldValue(instance, headers[j].field(), value);
                }

                dataList.add(instance);
            } catch (Exception e) {
                log.error("导入Excel失败，行号: {}, 错误: {}", i + 1, e.getMessage(), e);
                throw new BusinessException("导入Excel失败，行号: " + (i + 1) + "，错误: " + e.getMessage());
            }
        }

        workbook.close();
        return dataList;
    }

    /**
     * 创建表头样式
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

    /**
     * 创建数据样式
     */
    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

    /**
     * 设置单元格值
     */
    private static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setBlank();
            return;
        }

        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof LocalDateTime) {
            cell.setCellValue(((LocalDateTime) value).format(DATE_TIME_FORMATTER));
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * 获取单元格值
     */
    private static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> cell.getNumericCellValue();
            case BOOLEAN -> cell.getBooleanCellValue();
            case FORMULA -> cell.getCellFormula();
            default -> null;
        };
    }

    /**
     * 获取字段值
     */
    private static Object getFieldValue(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 设置字段值
     */
    private static void setFieldValue(Object obj, String fieldName, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            if (value == null) {
                field.set(obj, null);
            } else {
                Class<?> fieldType = field.getType();
                if (fieldType == String.class) {
                    field.set(obj, value.toString());
                } else if (fieldType == Integer.class || fieldType == int.class) {
                    field.set(obj, Integer.parseInt(value.toString()));
                } else if (fieldType == Long.class || fieldType == long.class) {
                    field.set(obj, Long.parseLong(value.toString()));
                } else if (fieldType == Double.class || fieldType == double.class) {
                    field.set(obj, Double.parseDouble(value.toString()));
                } else if (fieldType == Boolean.class || fieldType == boolean.class) {
                    field.set(obj, Boolean.parseBoolean(value.toString()));
                } else if (fieldType == LocalDateTime.class) {
                    field.set(obj, LocalDateTime.parse(value.toString(), DATE_TIME_FORMATTER));
                } else {
                    field.set(obj, value);
                }
            }
        } catch (Exception e) {
            log.error("设置字段值失败: fieldName={}, value={}, error={}", fieldName, value, e.getMessage());
        }
    }

    /**
     * 获取Excel表头（需要实体类添加@ExcelHeader注解）
     */
    private static <T> ExcelHeader[] getExcelHeaders(Class<T> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        ExcelHeader[] headers = new ExcelHeader[fields.length];

        for (int i = 0; i < fields.length; i++) {
            ExcelField excelField = fields[i].getAnnotation(ExcelField.class);
            if (excelField != null) {
                headers[i] = new ExcelHeader(excelField.name(), excelField.field(), excelField.width());
            } else {
                headers[i] = new ExcelHeader(fields[i].getName(), fields[i].getName(), 15);
            }
        }

        return headers;
    }

    /**
     * Excel表头
     */
    private record ExcelHeader(String name, String field, int width) {}

    /**
     * Excel字段注解
     */
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @java.lang.annotation.Target(java.lang.annotation.ElementType.FIELD)
    public @interface ExcelField {
        /**
         * 列名
         */
        String name() default "";

        /**
         * 字段名
         */
        String field() default "";

        /**
         * 列宽
         */
        int width() default 15;
    }
}
