package lon.test.provider.enums;

import java.util.Date;

/**
 * @author : wangzhiyong
 * @date : 2018/11/12 17:46
 * description : 参数类型
 */
public enum ParamType {
    /**
     * 基本参数类型
     */
    BYTE(Byte.class), SHORT(Short.class), INT(Integer.class), LONG(Long.class), FLOAT(Long.class), DOUBLE(Double.class), BOOLEAN(Boolean.class), CHAR(Character.class),
    /**
     * 拓展参数类型
     */
    STRING(String.class), DATE(Date.class);


    private Class typeClass;

    ParamType(Class typeClass) {
        this.typeClass = typeClass;
    }

    public Class getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(Class typeClass) {
        this.typeClass = typeClass;
    }
}
