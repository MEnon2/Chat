package lesson7;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class MyTester {

    public static void main(String[] args) {
        start(Test1.class);
        start(Test2.class);
    }

    public static void start(Class testClass) {

        Method[] methods = testClass.getDeclaredMethods();
        Map<Class, Map<Integer, List<Method>>> mapTests = new HashMap<>();

        Map<Integer, List<Method>> mapBeforeSuitePriority = new HashMap<>();
        Map<Integer, List<Method>> mapTestsPriority = new HashMap<>();
        Map<Integer, List<Method>> mapAfterSuitePriority = new HashMap<>();


        for (Method o : methods) {

            if (o.getAnnotation(BeforeSuite.class) != null) {
                mapBeforeSuitePriority.computeIfAbsent(1, k -> new ArrayList<>()).add(o);
                mapTests.put(BeforeSuite.class, mapBeforeSuitePriority);
            }

            if (o.getAnnotation(AfterSuite.class) != null) {
                mapAfterSuitePriority.computeIfAbsent(1, k -> new ArrayList<>()).add(o);
                mapTests.put(AfterSuite.class, mapAfterSuitePriority);
            }

            if (o.getAnnotation(Test.class) != null) {
                mapTestsPriority.computeIfAbsent(o.getAnnotation(Test.class).priority(), k -> new ArrayList<>()).add(o);
                mapTests.put(Test.class, mapTestsPriority);
            }

        }

        Map<Integer, List<Method>> listBeforeSuiteMethods = new TreeMap<>();
        Map<Integer, List<Method>> listAfterSuiteMethods = new TreeMap<>();
        Map<Integer, List<Method>> listTestMethods = new TreeMap<>();

        if (mapTests.get(BeforeSuite.class) != null) {
            listBeforeSuiteMethods = mapTests.get(BeforeSuite.class);
        }
        if (mapTests.get(AfterSuite.class) != null) {
            listAfterSuiteMethods = mapTests.get(AfterSuite.class);
        }
        if (mapTests.get(Test.class) != null) {
            listTestMethods = mapTests.get(Test.class);
        }


        if (listBeforeSuiteMethods.size() > 1)
            throw new RuntimeException("Больше одной аннатации @BeforeSuite");

        listBeforeSuiteMethods.entrySet().forEach(integerListEntry -> methodInvoke(integerListEntry.getValue(), testClass));
        listTestMethods.entrySet().forEach(integerListEntry -> methodInvoke(integerListEntry.getValue(), testClass));
        listAfterSuiteMethods.entrySet().forEach(e -> methodInvoke(e.getValue(), testClass));

    }

    public static void methodInvoke(List<Method> methods, Class testClass) {
        Constructor testClassConstructor = testClass.getConstructors()[0];

        try {
            Object objectTestClass = testClassConstructor.newInstance();
            for (Method method : methods) {
                try {
                    method.invoke(objectTestClass);
                } catch (IllegalAccessException | InvocationTargetException illegalAccessException) {
                    illegalAccessException.printStackTrace();
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }


    }
}
