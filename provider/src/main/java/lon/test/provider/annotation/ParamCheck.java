package lon.test.provider.annotation;


import lon.test.provider.enums.ParamType;

import java.lang.annotation.*;

/**
 * @author : wangzhiyong
 * @date : 2018/11/12 17:58
 * description : 参数校验
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
@Documented
@Inherited
public @interface ParamCheck {

    /**
     * 是否必填
     *
     * @return
     */
    boolean require() default true;

    /**
     * 参数类型
     * {@link ParamType}
     *
     * @return
     */
    ParamType type() default ParamType.STRING;

    /**
     * 长度
     *
     * @return
     */
    int size() default Integer.MAX_VALUE;

    /**
     * 最小值
     *
     * @return
     */
    double min() default Integer.MIN_VALUE;

    /**
     * 最大值
     *
     * @return
     */
    double max() default Integer.MAX_VALUE;

    /**
     * 格式化
     *
     * @return
     */
    String pattern() default "yyyy-MM-dd HH:mm:ss";

}
