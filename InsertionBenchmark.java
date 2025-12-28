import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InsertionBenchmark {
    private static final int a = 1_000_000;

    public static void main(String[] args) {
        List<Integer> arrayList = new ArrayList<>();
        long arrayListStart = System.currentTimeMillis();

        for (int i = 0; i < a; i++) {
            arrayList.add(0, i); 
        }

        long arrayListEnd = System.currentTimeMillis();
        System.out.println("ArrayList insertion time: " + (arrayListEnd - arrayListStart) + " ms");

        List<Integer> linkedList = new LinkedList<>();
        long linkedListStart = System.currentTimeMillis();

        for (int i = 0; i < a; i++) {
            linkedList.add(0, i); 
        }

        long linkedListEnd = System.currentTimeMillis();
        System.out.println("LinkedList insertion time: " + (linkedListEnd - linkedListStart) + " ms");
    }
}
