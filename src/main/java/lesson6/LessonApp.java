package lesson6;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class LessonApp {

    public int[] newArr(int[] arr) {
        int position = position(arr);
        return Arrays.stream(arr).skip(position).toArray();
    }

    public int position(int[] arr) throws RuntimeException {
        if (arr == null) {
            return 0;
        }
        for (int i = arr.length - 1; i >= 0; i--) {
            if (arr[i] == 4) {
                return i + 1;
            }
        }
        throw new RuntimeException();
    }

    public boolean checkArr(int[] arr) {
        if (arr == null) {
            return false;
        }
        return Arrays.stream(arr).filter(e -> (e == 1 || e == 4)).toArray().length > 0;
    }

}
