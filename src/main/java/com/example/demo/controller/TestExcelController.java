package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.Staff;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

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

}
