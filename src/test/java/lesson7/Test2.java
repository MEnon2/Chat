package lesson7;

public class Test2 {

    public void method1() {
        System.out.println("Test2 + @BeforeSuite + method1");
    }

    @AfterSuite
    public void method2() {
        System.out.println("Test2 + @AfterSuite + method2");
    }

    @Test
    public void method3() {
        System.out.println("Test2 + @Test + method3");
    }
}
