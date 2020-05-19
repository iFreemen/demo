package com.example.demo.NotTry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @ClassName MyExceptionHandler
 * @Description: TODO 异常处理器
 * @Author Freemen
 * @Time 2020/4/30 15:57
 * @Version V1.0
 **/
@RestControllerAdvice
public class MyExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        logger.error(e.getMessage(), e);
        return Result.error();
    }


    /**
     * 自定义异常
     */
    @ExceptionHandler(MyException.class)
    public Result handleMyException(MyException e){
        Result r = new Result();
        r.put("code", e.getCode());
        r.put("msg", e.getMessage());
        return r;
    }
    @ExceptionHandler(DuplicateKeyException.class)
    public Result handleDuplicateKeyException(DuplicateKeyException e){
        logger.error(e.getMessage(), e);
        return Result.error("数据库中已存在该记录");
    }
}
