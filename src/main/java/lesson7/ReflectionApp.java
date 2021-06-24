package lesson7;

import chatjfx.ClientHandler;
import chatjfx.MyServer;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.util.Arrays;

public class ReflectionApp {
    public static void main(String[] args) {

        Class clazz = "JAVA".getClass();
        System.out.println(clazz.getSimpleName());

        Class integerClass = Integer.class;
        Class stringClass = String.class;
        Class intClass = int.class;
        Class voidClass = void.class;
        Class charArrayClass = char[].class;


        int modifiers = clazz.getModifiers();
        if (Modifier.isPublic(modifiers)) {
            System.out.println(clazz.getSimpleName() + " - public");
        }
        if (Modifier.isStatic(modifiers)) {
            System.out.println(clazz.getSimpleName() + " - static");
        }
        if (Modifier.isAbstract(modifiers)) {
            System.out.println(clazz.getSimpleName() + " - abstract");
        }
        if (Modifier.isFinal(modifiers)) {
            System.out.println(clazz.getSimpleName() + " - final");
        }

        System.out.println("--------------");
        Field[] publicFields = ClientHandler.class.getDeclaredFields();
        for (Field f : publicFields) {
            System.out.println("Тип_поля Имя_поля : " + f.getType().getName() + " " + f.getName());
        }


        Method[] methods = ClientHandler.class.getDeclaredMethods();
        for (Method o : methods) {
            System.out.println(o.getReturnType() + " ||| " + o.getName() + " ||| " + Arrays.toString(o.getParameterTypes()));
        }
        System.out.println("--------------");
        try {
            Method m1 = ClientHandler.class.getMethod("readMessages", null);
            Method m2 = ClientHandler.class.getMethod("sendMsg", String.class);
            System.out.println(m1 + " | " + m2);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        System.out.println("--------------");
        Constructor[] constructors = ClientHandler.class.getConstructors();
        for (Constructor c : constructors) {
            System.out.println(c);
        }
        System.out.println("--------------");
        try {
            System.out.println(ClientHandler.class.getConstructor(new Class[] {MyServer.class, Socket.class, Logger.class}));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }
}
