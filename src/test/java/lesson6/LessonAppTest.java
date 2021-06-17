package lesson6;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LessonAppTest {
    LessonApp lessonApp;

    private static Stream<Arguments> dataForPosition() {
        List<Arguments> out = new ArrayList<>();
        out.add(Arguments.arguments(new int[]{1, 2, 4, 4, 2, 3, 4, 1, 7}, 7));
        out.add(Arguments.arguments(new int[]{1, 2, 4, 4, 2, 3, 44, 1, 7}, 4));
        out.add(Arguments.arguments(null, 0));
        return out.stream();
    }

    private static Stream<Arguments> dataForCheckArr() {
        List<Arguments> out = new ArrayList<>();
        out.add(Arguments.arguments(new int[]{1, 2, 4, 4}));
        out.add(Arguments.arguments(new int[]{2, 2, 3}));
        out.add(Arguments.arguments(new int[]{2, 2, 1}));
        return out.stream();
    }

    @BeforeEach
    public void init() {
        lessonApp = new LessonApp();
    }

    @ParameterizedTest
    @MethodSource("dataForPosition")
    public void testPosition(int[] arr, int result) {
        Assertions.assertEquals(result, lessonApp.position(arr));
    }

    @Test
    public void testPositionThrow() {
        Assertions.assertThrows(RuntimeException.class, () -> lessonApp.position(new int[]{1, 2, 3}));
    }

    @ParameterizedTest
    @MethodSource("dataForCheckArr")
    public void testCheckArr(int[] arr) {
        Assertions.assertTrue(lessonApp.checkArr(arr));
    }

    @Test
    public void testCheckArrNull() {
        Assertions.assertEquals(false, lessonApp.checkArr(null));
    }
}
