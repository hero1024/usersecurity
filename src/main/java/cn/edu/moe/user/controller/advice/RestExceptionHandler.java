package cn.edu.moe.user.controller.advice;


import cn.edu.moe.user.model.CommonResult;
import cn.edu.moe.user.model.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.xml.bind.ValidationException;
import java.util.stream.Collectors;


/**
 * @author songpeijiang
 * @since 2024/4/10
 */
@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    /**
     * 默认全局异常处理。
     * @param e the e
     * @return CommonResult
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResult<String> exception(Exception e) {
        log.error("全局异常信息 ex={}", e.getMessage(), e);
        return CommonResult.failed(ResultCode.FAILED,e.getMessage());
    }

    @ExceptionHandler(value = {BindException.class, ValidationException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<CommonResult<String>> handleValidatedException(Exception e) {
        CommonResult<String> resp = null;

        if (e instanceof MethodArgumentNotValidException) {
            // BeanValidation exception
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            resp = CommonResult.failed(ResultCode.VALIDATE_FAILED,
                    ex.getBindingResult().getAllErrors().stream()
                            .map(ObjectError::getDefaultMessage)
                            .collect(Collectors.joining("; "))
            );
        } else if (e instanceof ConstraintViolationException) {
            // BeanValidation GET simple param
            ConstraintViolationException ex = (ConstraintViolationException) e;
            resp = CommonResult.failed(ResultCode.VALIDATE_FAILED,
                    ex.getConstraintViolations().stream()
                            .map(ConstraintViolation::getMessage)
                            .collect(Collectors.joining("; "))
            );
        } else if (e instanceof BindException) {
            // BeanValidation GET object param
            BindException ex = (BindException) e;
            resp = CommonResult.failed(ResultCode.VALIDATE_FAILED,
                    ex.getAllErrors().stream()
                            .map(ObjectError::getDefaultMessage)
                            .collect(Collectors.joining("; "))
            );
        }

        return new ResponseEntity<>(resp,HttpStatus.BAD_REQUEST);
    }

}
