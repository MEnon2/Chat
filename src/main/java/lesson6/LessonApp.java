package lesson6;

import java.util.Arrays;

public class LessonApp {
    public static void main(String[] args) {
        int[] arr = newArr(new int[]{1, 2, 44, 44, 2, 3, 44, 1, 7});
    }

    public static int[] newArr(int[] arr) {

        int position = position(arr);

        return Arrays.stream(arr).skip(position + 1).toArray();
    }

    public static int position(int[] arr) {
        int i=0;
        for (int i1 : arr) {
            if (i1 == 4) {
                i = i1;
                break;
            }
        }
        if(i == 0) {
            throw new RuntimeException();
        }
        return 0;
    }

}
