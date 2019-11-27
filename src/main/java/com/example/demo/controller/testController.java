package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName testController
 * @Description: TODO
 * @Author Freemen
 * @Time 2019/11/19 11:49
 * @Version V1.0
 **/
@Controller
public class testController {
    private final static Logger logger = LoggerFactory.getLogger(testController.class);

    /**
     * 后台传递单个元素至前端页面
     * @param model
     * @return
     */
    @GetMapping("/hello")
    public String hello(Model model){
        model.addAttribute("name", "Freemen");
        return "hellohtml";
    }
    /**
     * 接收页面传递的参数--同步
     * 直接访问会报错
     * @param model
     * @param name
     * @return
     */
    @GetMapping("/hellohtml")
    public String hellohtml(Model model,@RequestParam String name){
        model.addAttribute("name", name);
        return "hellohtml";
    }


    /**
     * 1、传对象给后台,在html和js中使用
     * 2、导航栏
     * @param model
     * @return
     */
    @GetMapping("/show")
    public String show(Model model){

        Calendar cr = Calendar.getInstance();
        System.out.println("时间："+cr.getTime());


        User user = new User("Freemen",18);
        Date date = new Date();
        model.addAttribute("user",user );
        model.addAttribute("today",date );

        List<User> list = new ArrayList<>();
        User user1 = new User("tom",20,false);
        User user2 = new User("lily",12,true);
        User user3 = new User("bingbing",17,true);
        list.add(user1);
        list.add(user2);
        list.add(user3);
        model.addAttribute("list", list);

        return "userhtml";
    }

    //<<<<<<<<<<<<<<<<<<<<<<<<<<< start 实现表单 >>>>>>>>>>>>>>>>>>>>>>>>>
    /**上传地址*/
    @Value("${file.upload.path}")
    private String filePath;
    /**显示相对地址*/
    @Value("${file.upload.relative}")
    private String fileRelativePath;

    @GetMapping("/userIndex")
    public String userIndex(Model model){
        model.addAttribute("user", new User());
        return "userIndex";
    }
    @PostMapping("/postUserFrom")
    public String postUserFrom(Model model, @ModelAttribute(value="user") User user, HttpServletRequest request, HttpServletResponse response){
        System.out.println("用户信息:"+user.toString());
        MultipartFile file = user.getFile();

        // 上传到服务器
        File targetFile=null;
        //返回存储路径
        String url="";
        System.out.println(file);
        //获取文件名加后缀
        String fileName = file.getOriginalFilename();
        if(fileName != null && fileName != ""){
            //图片访问的URI
            String returnUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() +"/imgs/";
            //文件临时存储位置
            String path = request.getSession().getServletContext().getRealPath("") + File.separator + "imgs";

            //文件后缀
            String fileSuffix = fileName.substring(fileName.lastIndexOf("."), fileName.length());
            //新的文件名
            fileName = System.currentTimeMillis()+"_"+new Random().nextInt(1000) + fileSuffix;

            //先判断文件是否存在
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String fileAdd = sdf.format(new Date());
            //获取文件夹路径
            path = path + File.separator + fileAdd + File.separator;
            File file1 =new File(path);
            //如果文件夹不存在则创建
            if(!file1 .exists()  && !file1 .isDirectory()){
                file1 .mkdirs();
            }
            //将图片存入文件夹
            targetFile = new File(file1, fileName);
            try {
                //将上传的文件写到服务器上指定的文件。
                file.transferTo(targetFile);
                //文件复制
                String src = path + fileName;
                //根据自己系统的resource 目录所在位置进行自行配置
                String destDir = filePath + File.separator + fileAdd + File.separator;
                copyFile(src,destDir,fileName);

                url= returnUrl + fileAdd + "/"+ fileName;

            } catch (Exception e) {
                logger.error("exception:",e);
            }
        }

        model.addAttribute("filename", url);
        model.addAttribute("user", user);
        return "postUserFrom";
    }

    /**
     * 文件复制
     * @param src
     * @param destDir
     * @param fileName
     * @throws IOException
     */
    public void copyFile(String src,String destDir,String fileName) throws IOException{
        FileInputStream in=new FileInputStream(src);
        File fileDir = new File(destDir);
        if(!fileDir.isDirectory()){
            fileDir.mkdirs();
        }
        File file = new File(fileDir,fileName);

        if(!file.exists()){
            file.createNewFile();
        }
        FileOutputStream out=new FileOutputStream(file);
        int c;
        byte buffer[]=new byte[1024];
        while((c=in.read(buffer))!=-1){
            for(int i=0;i<c;i++){
                out.write(buffer[i]);
            }

        }
        in.close();
        out.close();
    }

    //<<<<<<<<<<<<<<<<<<<<<<<<<<< end 实现表单 >>>>>>>>>>>>>>>>>>>>>>>>>


    //<<<<<<<<<<<<<<<<<<<<<<<<<< start ajax提交信息 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    @GetMapping("/ajaxTest")
    public String toAjax(){
        return "ajaxTest/ajaxTest";
    }
    @PostMapping("/ajaxTest.action")
    public void ajaxTest(@RequestBody User user, HttpServletResponse resp)throws Exception{
        System.out.println(user.toString());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code",200);
        jsonObject.put("message","成功");
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().println(jsonObject.toJSONString());
        resp.getWriter().close();
    }
    //<<<<<<<<<<<<<<<<<<<<<<<<<< end ajax提交信息 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>
}
