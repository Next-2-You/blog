package com.chen.blog.exception;

import com.chen.blog.common.CodeEnum;
import com.chen.blog.vo.RespVo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * AOP对service层做统一的异常处理
 */
//@Aspect
//@Component
//@Slf4j
public class ServiceExceptionAspect {


//    @Around("execution(* com.chen.blog.service..*(..))")
//    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//        Object result;
//        try{
//            result = proceedingJoinPoint.proceed();
//        }catch (BlogException e){
//            log.error(e.getMessage(),e);
//            return RespVo.general(e.getErrCode()==null? CodeEnum.ABNORMAL.getCode():e.getErrData(),e.getMessage(),null,null);
//        }catch (Exception e){
//            log.error(e.getMessage(),e);
//            throw new Exception(ResMessage.SERVER_ERROR);
//        }
//        return  result;
//    }


}
