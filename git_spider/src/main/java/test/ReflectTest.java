package test;

import java.lang.reflect.Field;

public class ReflectTest {
    public static void main(String[] args) {
        String str = "hello";
        //先创建一个Field对象
        try {
            Field field = String.class.getDeclaredField("value");
            field.setAccessible(true);
            char[] value = (char[])field.get(str);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
