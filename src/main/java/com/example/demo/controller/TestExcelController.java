package com.example.demo.controller;

import com.example.demo.entity.Staff;
import com.example.demo.utils.ExportExcelUtil;
import com.example.demo.utils.ExportExcelWrapper;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @ClassName TestExcelController
 * @Description: TODO 操作excel的读写操作
 * @Author Freemen
 * @Time 2019/11/28 17:10
 * @Version V1.0
 **/
@Controller
public class TestExcelController {

    private final static Logger logger = LoggerFactory.getLogger(TestExcelController.class);

    @Value("${file.poi.path}")
    private String path;

    @GetMapping("testReadExcel")
    public String testReadExcel() throws Exception {
        String readFile = "read.xlsx";
        String filePath = path + readFile;
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(filePath);
        // 读取第一章表格内容
        XSSFSheet sheet1 = xssfWorkbook.getSheetAt(0);

        // 定义 row
        XSSFRow row;
        String content = "";   // 单元格的内容，null被处理成 空字符串

        ArrayList<Staff> staffLists = new ArrayList<>();
        // 循环输出表格中的内容
        for (int i = sheet1.getFirstRowNum(); i < sheet1.getPhysicalNumberOfRows(); i++) {  // 操作行
            row = sheet1.getRow(i);
            if (i > 0) {
                Staff staff = new Staff();
                for (int j = row.getFirstCellNum(); j < sheet1.getRow(0).getPhysicalNumberOfCells(); j++) {  // 操作列

                    // for (int j = row.getFirstCellNum(); j < row.getPhysicalNumberOfCells(); j++)
                    // 1、在处理列的时候，不能使用 row.getPhysicalNumberOfCells() 因为，当这一行有 空值(null) 的时候,就会读取缺少一些数据
                    XSSFCell cell = row.getCell(j);
                    if (cell == null) {
                        content = "";   // 内容空了就不用处理了
                        continue;
                    }else {
                        // 2、此处一些处理方案可能BUG，在Excel中的整型数据读取后竟然有小数点  29 ==> 29.0，修正后如下处理：
                        // 其实是Excel的问题，在整型是默认的整型还是 Double
                        CellType cellTypeEnum = cell.getCellTypeEnum();   // 获取内容的类型
                        if (cellTypeEnum == CellType.NUMERIC) {
                            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                content = DateFormatUtils.format(cell.getDateCellValue(), "yyyy-MM-dd");   // 转换成只是日期（Excel中有时分秒的）
                            } else {
                                NumberFormat nf = NumberFormat.getInstance();
                                content = String.valueOf(nf.format(cell.getNumericCellValue())).replace(",", ""); // 处理了小数点问题
                            }
                        } else if (cell.getCellTypeEnum() == CellType.STRING) {
                            content = String.valueOf(cell.getStringCellValue());
                        } else if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
                            content = String.valueOf(cell.getBooleanCellValue());
                        } else if (cell.getCellTypeEnum() == CellType.ERROR) {
                            content = "错误类型";
                        } else {
                            content = "";
                        }

                        switch (j) {
                            case 0:
                                staff.setName(content);
                                break;
                            case 1:
                                staff.setAge(Integer.parseInt(content));
                                break;
                            case 2:
                                staff.setSalary(Double.parseDouble(content));
                                break;
                            case 3:
                                staff.setIdCard(content);
                                break;
                            case 4:
                                staff.setBirthday(content);  // cell.getDateCellValue() 是Date类型
                                break;
                            default:
                                break;
                        }
                    }
                }
                staffLists.add(staff);
            }
        }

        // 迭代器遍历
        Iterator<Staff> iter = staffLists.iterator();
        while (iter.hasNext()) {  //执行过程中会执行数据锁定，性能稍差，若在循环过程中要去掉某个元素只能调用iter.remove()方法。
            System.out.println(iter.next());
        }
        return "success";
    }

    /**
     * 主动下载，可以配合查询应用
     * @return
     * @throws Exception
     */
    @GetMapping("testSaveExcel")
    public String testSaveExcel(HttpServletResponse response) throws Exception {
        List<Staff> excelList = new ArrayList<>();
        for (int i = 0 ; i < 3;i++){
            Staff staff = new Staff();
            staff.setIdCard("身份证"+String.valueOf(i));
            staff.setSalary(1002.00+i);
            staff.setAge(i);
            staff.setName("名字"+String.valueOf(i));
            staff.setBirthday("生日"+String.valueOf(i));
            excelList.add(staff);
        }

        String fileName = "员工信息";
        // 这里的顺序不是随意的，而是有类本身（Staff）的反射决定的，
        // 所以:要非常注意一点 serialVersionUID 会影响反射的变量，进而影响列的数量（多了一行），所以要除掉 serialVersionUID
        String[] columnNames = {"名字","年龄","工资","身份证","生日"};
        ExportExcelWrapper<Staff> util = new ExportExcelWrapper<Staff>();
        util.exportExcel(fileName, fileName, columnNames, excelList, response, ExportExcelUtil.EXCEL_FILE_2003);
        return "success";
    }

}
