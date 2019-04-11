package lon.test.provider.config;

import lon.test.provider.annotation.ParamCheck;
import lon.test.provider.enums.ParamType;
import lon.test.provider.exceptions.ParamCheckException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * @author : zhangkaicheng
 * @date : 2018/11/12 17:58
 * description : 参数校验
 */
@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ParamCheckAspectConfig {

    @Pointcut("execution(* lon.test.provider.services..*.*O(..))")
    public void paramPointcut() {
    }

    @Before("paramPointcut()")
    public void before(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
        // 方法的参数
        Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            // 若参数未注解ParamCheck或, 忽略验证
            ParamCheck parameterAnnotation = parameterType.getAnnotation(ParamCheck.class);
            if (parameterAnnotation == null) {
                continue;
            }
            // 传入的参数
            Object arg = args[i];
            if (arg == null) {
                // 判断是否允许传空
                if (parameterAnnotation.require()) {
                    throw new ParamCheckException("parameter should not be null or empty");
                } else {
                    continue;
                }
            }
            // 获取注解
            for (Field field : arg.getClass().getDeclaredFields()) {
                // 获取注解
                ParamCheck fieldAnnotation = field.getAnnotation(ParamCheck.class);
                // 只检查@ParamCheck注解字段
                if (fieldAnnotation == null) {
                    continue;
                }
                // 设置字段可访问并取值
                field.setAccessible(true);
                Object argValue = null;
                try {
                    argValue = field.get(arg);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                // 字段类型
                Class type = field.getType();
                // 字段为null或空string
                if (argValue == null || (argValue instanceof String && "".equals(argValue))) {
                    // 若注解声明非空, 抛出异常, 否则忽略本字段
                    if (fieldAnnotation.require()) {
                        throw new ParamCheckException("parameter " + field.getName() + " should not be null or empty");
                    } else {
                        continue;
                    }
                }
                // 字段类型非基本/包装/String, 忽略本字段, 不深入检查
                if (!isPrimitiveOrWrapClzOrStr(type)) {
                    continue;
                }
                try {
                    // 字段类型为 String 或 数字, 走各自验证
                    if (String.class.equals(type)) {
                        checkString(String.valueOf(argValue), fieldAnnotation);
                    } else {
                        checkNumber(argValue, fieldAnnotation);
                    }
                } catch (ParamCheckException e) {
                    throw new ParamCheckException(String.format(e.getMessage(), field.getName()));
                }
            }
        }
    }

    /**
     * 检查String类型的参数
     * 包括 size/根据 type 检查 min/max/pattern
     *
     * @param value
     * @param paramCheck
     */
    private void checkString(String value, ParamCheck paramCheck) throws ParamCheckException {
        // 检查长度
        if (paramCheck.size() < value.length()) {
            throw new ParamCheckException("parameter %s is too long. (" + value.length() + " of " + paramCheck.size() + ")");
        }
        // 日期时
        if (ParamType.DATE.equals(paramCheck.type())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(paramCheck.pattern());
            try {
                formatter.parse(value);
            } catch (Exception e) {
                throw new ParamCheckException("parameter %s needs correct pattern. (" + paramCheck.pattern() + ")");
            }
        } else if (ParamType.BOOLEAN.equals(paramCheck.type())) {
            if (!"true".equals(value) && !"false".equals(value)) {
                throw new ParamCheckException("parameter %s is not a " + paramCheck.type());
            }
        } else if (isPrimitive(paramCheck.type().getTypeClass()) || isWrap(paramCheck.type().getTypeClass())) {
            checkNumber(value, paramCheck);
        }
    }

    /**
     * 检查基本类型以及字符类型的参数
     * 包括 min/max
     *
     * @param value
     * @param paramCheck
     */
    private void checkNumber(Object value, ParamCheck paramCheck) {
        BigDecimal numberValue;
        try {
            String stringValue = value.toString();
            if (ParamType.CHAR.equals(paramCheck.type()) || (value instanceof Character || value.getClass() == char.class)) {
                if (stringValue.length() != 1) {
                    throw new NumberFormatException();
                }
                numberValue = new BigDecimal(stringValue.toCharArray()[0]);
            } else {
                numberValue = new BigDecimal(stringValue);
            }
        } catch (NumberFormatException e) {
            throw new ParamCheckException("parameter %s is not a " + paramCheck.type());
        }
        // 检查最大值
        BigDecimal min = new BigDecimal(paramCheck.min());
        if (numberValue.compareTo(min) < 0) {
            throw new ParamCheckException("parameter %s is out of range. (min: " + paramCheck.min() + ")");
        }
        // 检查最小值
        BigDecimal max = new BigDecimal(paramCheck.max());
        if (numberValue.compareTo(max) > 0) {
            throw new ParamCheckException("parameter %s is out of range. (max: " + paramCheck.max() + ")");
        }
    }

    /**
     * 判断是否是字符串或基本类型或包装类型
     *
     * @param clz
     * @return
     */
    public static boolean isPrimitiveOrWrapClzOrStr(Class clz) {
        return clz != null && (isPrimitive(clz) || isWrap(clz) || isStr(clz));
    }

    /**
     * 判断是否是基本类型
     *
     * @param clz
     * @return
     */
    public static boolean isPrimitive(Class clz) {
        //是基本类型
        return clz.isPrimitive();
    }

    /**
     * 判断是否包装类型
     *
     * @param clz
     * @return
     */
    public static boolean isWrap(Class clz) {
        try {
            //是包装类型
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断是否是字符串
     *
     * @param clz
     * @return
     */
    public static boolean isStr(Class clz) {
        //是字符串类型
        return clz == String.class;
    }

}
