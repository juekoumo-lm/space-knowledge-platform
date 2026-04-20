import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;

public class TestExcelImport {
    public static void main(String[] args) {
        try {
            System.out.println("测试Excel文件读取...");
            
            // 检查文件是否存在
            File file = new File("d:\\1\\space-knowledge-platform\\航天知识题目.xlsx");
            System.out.println("文件存在: " + file.exists());
            System.out.println("文件大小: " + file.length() + " bytes");
            
            if (!file.exists()) {
                System.out.println("错误: 文件不存在！");
                return;
            }
            
            // 尝试读取文件
            System.out.println("开始读取文件...");
            try (FileInputStream fis = new FileInputStream(file);
                 Workbook wb = new XSSFWorkbook(fis)) {
                System.out.println("Workbook创建成功！");
                
                // 检查工作表
                int sheetCount = wb.getNumberOfSheets();
                System.out.println("工作表数量: " + sheetCount);
                
                if (sheetCount > 0) {
                    Sheet sheet = wb.getSheetAt(0);
                    System.out.println("第一个工作表名称: " + sheet.getSheetName());
                    System.out.println("总行数: " + (sheet.getLastRowNum() + 1));
                }
                
                System.out.println("Excel文件读取成功！");
            }
        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}