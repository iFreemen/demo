package com.example.demo.service.fordatasources;

import com.example.demo.dao.mysql.ds1.StudentMapper1;
import com.example.demo.dao.mysql.ds2.StudentMapper2;
import com.example.demo.entity.fordatasources.Student;
import com.example.demo.entity.fordatasources.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName TestDataSourceService
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/12/17 17:33
 * @Version V1.0
 **/
@Service
public class TestDataSourceService {

    @Autowired
    private StudentMapper1 studentMapper1;

    @Autowired
    private StudentMapper2 studentMapper2;


    public List<Student> findByName1(String name) throws Exception{
        List<Student> students = studentMapper1.selectAll();
        return students;
    }

    public List<Student> findByName2(String name) throws Exception{
        List<Student> students = studentMapper2.selectAll();
        return students;
    }
}
