package com.lis.aspect;

import com.lis.annotation.OperationLog;
import com.lis.service.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class OperationLogAspect {

    @Autowired
    private SystemService systemService;

    @Around("@annotation(com.lis.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            OperationLog opLog = method.getAnnotation(OperationLog.class);
            if (opLog == null) return result;

            String sm = opLog.value();
            String czmk = opLog.module();

            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            String czip = "";
            String czydm = "";
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                czip = request.getRemoteAddr();
                if ("0:0:0:0:0:0:0:1".equals(czip)) czip = "127.0.0.1";
                czydm = request.getHeader("X-User-Code");
                if (czydm == null || czydm.isEmpty()) {
                    Object[] args = joinPoint.getArgs();
                    for (Object arg : args) {
                        if (arg == null) continue;
                        if (arg instanceof java.util.Map) {
                            Object code = ((java.util.Map<?, ?>) arg).get("czydm");
                            if (code == null) code = ((java.util.Map<?, ?>) arg).get("username");
                            if (code != null) { czydm = code.toString(); break; }
                        } else {
                            try {
                                java.lang.reflect.Field f = arg.getClass().getDeclaredField("username");
                                f.setAccessible(true);
                                Object val = f.get(arg);
                                if (val != null) { czydm = val.toString(); break; }
                            } catch (Exception ignored) {}
                            try {
                                java.lang.reflect.Field f = arg.getClass().getDeclaredField("czydm");
                                f.setAccessible(true);
                                Object val = f.get(arg);
                                if (val != null) { czydm = val.toString(); break; }
                            } catch (Exception ignored) {}
                        }
                    }
                }
            }

            systemService.saveLog(czydm, sm, null, null, czip, czmk);
        } catch (Exception e) {
            log.warn("记录操作日志失败: {}", e.getMessage());
        }
        return result;
    }
}
