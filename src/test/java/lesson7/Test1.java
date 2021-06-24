package lesson7;

public class Test1{
    @BeforeSuite
    public void method1() {
        System.out.println("Test1 + @BeforeSuite + method1");
    }
//    @BeforeSuite
    public void method5() {
        System.out.println("Test1 + @BeforeSuite + method5");
    }

    @AfterSuite
    public void method2() {
        System.out.println("Test1 + AfterSuite + method2");
    }

    @Test(priority = 9)
    public void method6() {
        System.out.println("Test1 + @Test + method3 + priority = 9");
    }

    @Test(priority = 8)
    public void method7() {
        System.out.println("Test1 + @Test + method4 + priority = 8");
    }

    @Test(priority = 5)
    public void method8() {
        System.out.println("Test1 + @Test + method3 + priority = 5");
    }

    @Test(priority = 1)
    public void method9() {
        System.out.println("Test1 + @Test + method4 + priority = 1");
    }
    @Test(priority = 1)
    public void method10() {
        System.out.println("Test1 + @Test + method3 + priority = 1");
    }

    @Test(priority = 2)
    public void method11() {
        System.out.println("Test1 + @Test + method4 + priority = 2");
    }
    @Test(priority = 5)
    public void method12() {
        System.out.println("Test1 + @Test + method3 + priority = 5");
    }

    @Test(priority = 5)
    public void method13() {
        System.out.println("Test1 + @Test + method4 + priority = 5");
    }
}
