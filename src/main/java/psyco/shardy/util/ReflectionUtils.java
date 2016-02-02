package psyco.shardy.util;

import com.google.common.collect.Lists;
import org.springframework.util.Assert;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by peng on 16/1/4.
 */
public class ReflectionUtils {

    public static Class getMethodGenericReturnType(Method method, int index) {
        return getGenericType(method.getGenericReturnType(), index);
    }

    public static Class getGenericType(Type returnType, int index) {
        if (returnType instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) returnType;
            Type[] typeArguments = type.getActualTypeArguments();
            if (index >= typeArguments.length || index < 0) {
                throw new RuntimeException("invalid index : " + index);
            }
            return (Class) typeArguments[index];
        }
        return (Class) returnType;
    }

    public static List<String> getParameterNames(Method method) {
        Parameter[] parameters = method.getParameters();
        List<String> parameterNames = new ArrayList<>();

        for (Parameter parameter : parameters) {
            if (!parameter.isNamePresent()) {
                return Lists.newLinkedList();
            }

            String parameterName = parameter.getName();
            parameterNames.add(parameterName);
        }

        return parameterNames;
    }

    public static Field getDeclaredField(Class<?> clazz, String propertyName) throws NoSuchFieldException {
        Assert.notNull(clazz);
        Assert.hasText(propertyName);
        Class superClass = clazz;

        while (superClass != Object.class) {
            try {
                Field f = superClass.getDeclaredField(propertyName);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException var4) {
                superClass = superClass.getSuperclass();
            }
        }

        throw new NoSuchFieldException("No such field: " + clazz.getName() + '.' + propertyName);
    }

    public static void setDeclaredFieldValue(Object object, String propertyName, Object newValue) throws NoSuchFieldException {
        Assert.notNull(object);
        Assert.hasText(propertyName);
        Field field = getDeclaredField(object.getClass(), propertyName);
        boolean accessible = field.isAccessible();
        field.setAccessible(true);

        try {
            field.set(object, newValue);
        } catch (IllegalAccessException var9) {
            throw new NoSuchFieldException("No such field: " + object.getClass() + '.' + propertyName);
        } finally {
            field.setAccessible(accessible);
        }

    }

    public static Object getFieldValue(Object object, String propertyName)
            throws IllegalAccessException, NoSuchFieldException {
        Assert.notNull(object);
        Assert.hasText(propertyName);
        Field field = object.getClass().getDeclaredField(propertyName);
        field.setAccessible(true);
        return field.get(object);
    }
}
