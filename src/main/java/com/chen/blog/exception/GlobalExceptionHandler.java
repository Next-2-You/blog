package com.chen.blog.exception;

import com.chen.blog.common.CodeEnum;
import com.chen.blog.vo.RespVo;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.lang.reflect.Method;
import java.util.*;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {


    /**
     * 处理系统异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public RespVo handleException(Exception e) {
        return RespVo.general(CodeEnum.ABNORMAL, e.getMessage(), null);
    }



    /**
     * BindException(实体类参数绑定异常)
     */
    @ExceptionHandler(BindException.class)
    public RespVo handleConstraintViolationException(BindException e) {
        BindingResult result = e.getBindingResult();
        Map<String, String> fieldErrorMap = new HashMap<>();
        if (result.hasErrors()) {
            for (FieldError fieldError : result.getFieldErrors()) {
                fieldErrorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
        }
        return RespVo.general(CodeEnum.PARAMETER_ERROR, fieldErrorMap, null);
    }


    /**
     * 参数校验异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public RespVo handleConstraintViolationException(ConstraintViolationException e) {
        Map<String, String> fieldError = parseConstraint(e.getConstraintViolations());
        return RespVo.general(CodeEnum.PARAMETER_ERROR, fieldError, null);
    }

    private Map<String, String> parseConstraint(Set<ConstraintViolation<?>> constraintViolationSet) {
        ParameterNameDiscoverer parameterNameDiscoverer = null;
        String[] parameterNames = null; // 获得方法的参数名称
        Map<String, String> fieldMap = new HashMap<>();
        try {
            for (ConstraintViolation<?> cons : constraintViolationSet) {
                if (null == parameterNameDiscoverer) {
                    parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
                    Path path = cons.getPropertyPath();
                    Class<?>[] clss = parseParameterTypes(path);
                    Method mm = cons.getRootBeanClass()
                            .getMethod(path.toString().substring(0, path.toString().indexOf(".")), clss);
                    parameterNames = parameterNameDiscoverer.getParameterNames(mm);
                }
                PathImpl pathImpl = (PathImpl) cons.getPropertyPath(); // 获得校验的参数路径信息
                int paramIndex = pathImpl.getLeafNode().getParameterIndex(); // 获得校验的参数位置
                String paramName = parameterNames[paramIndex]; // 获得参数名
                fieldMap.put(paramName, cons.getMessage());
            }
            return fieldMap;
        } catch (Exception e) {
//            log.error("获取参数名及错误信息失败", e);
            return null;
        }
    }

    private Class<?>[] parseParameterTypes(Path path) {
        List<Class<?>> parameterTypes = null;
        for (Iterator<Path.Node> it = path.iterator(); it.hasNext();) {
            Path.Node node = it.next();
            if (node instanceof Path.MethodNode) {
                Path.MethodNode methodNode = (Path.MethodNode) node;
                parameterTypes = methodNode.getParameterTypes();
                break;
            }
        }
        return parameterTypes.toArray(new Class<?>[] {});
    }


}
