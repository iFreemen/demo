package com.example.demo.controller;

import com.example.demo.entity.fordatasources.Student;
import com.example.demo.entity.fordatasources.Student;
import com.example.demo.service.fordatasources.TestDataSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @ClassName TestDataSourceController
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/12/17 17:29
 * @Version V1.0
 **/
@Controller
public class TestDataSourceController {
    private static final Logger logger = LoggerFactory.getLogger(TestDataSourceController.class);

    @Autowired
    private TestDataSourceService testDataSourceService;

    @GetMapping("useDataSources")
    public String useDataSources() throws Exception{
        List<Student> students1 = testDataSourceService.findByName1("Freemen");
        List<Student> students2 = testDataSourceService.findByName2("Freemen");

        for (Student student : students1) {
            System.out.println("数据源1:"+student.toString());
        }

        for (Student student : students2) {
            System.out.println("数据源2:"+student.toString());
        }
        return "success";
    }
}

