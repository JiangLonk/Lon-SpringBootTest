package lon.test.provider.bean;

import lon.test.provider.annotation.ParamCheck;
import lon.test.provider.enums.ParamType;

import java.io.Serializable;

@ParamCheck
public class TestBean implements Serializable {

    @ParamCheck(size = 5)
    private String testField;

    @ParamCheck(min = 65)
    private char testField2;

    @ParamCheck(min = 1, type = ParamType.INT)
    private String testField3;

    public String getTestField() {
        return testField;
    }

    public void setTestField(String testField) {
        this.testField = testField;
    }

    public char getTestField2() {
        return testField2;
    }

    public void setTestField2(char testField2) {
        this.testField2 = testField2;
    }

    public String getTestField3() {
        return testField3;
    }

    public void setTestField3(String testField3) {
        this.testField3 = testField3;
    }

    @Override
    public String toString() {
        return "TestBean2{" +
                "testField='" + testField + '\'' +
                ", testField2=" + testField2 +
                ", testField3=" + testField3 +
                '}';
    }
}
