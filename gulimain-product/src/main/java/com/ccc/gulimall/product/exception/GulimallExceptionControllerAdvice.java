package com.ccc.gulimall.product.exception;



import com.ccc.common.excrption.BizCodeEnume;
import com.ccc.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 集中处理所有异常
 */

@Slf4j
/*@RequestBody
@ControllerAdvice(basePackages = "com.ccc.gulimall.product.controller")*/

@RestControllerAdvice(basePackages = "com.ccc.gulimall.product.controller")
public class GulimallExceptionControllerAdvice {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R handleVaildException(MethodArgumentNotValidException e){
         BindingResult result = e.getBindingResult();
         Map<String ,String> map=new HashMap<>();
         result.getFieldErrors().forEach((item)->{
             map.put(item.getField(),item.getDefaultMessage());
         });

        return R.error(BizCodeEnume.VAILD_EXCRPTION.getCode(),BizCodeEnume.VAILD_EXCRPTION.getMsg()).put("data",map);
    }


    @ExceptionHandler(value = Throwable.class)
    public R handleExcption(Throwable throwable){
        return R.error(BizCodeEnume.UNKNOW_EXCEPTION.getCode(),BizCodeEnume.UNKNOW_EXCEPTION.getMsg());
    }
}
