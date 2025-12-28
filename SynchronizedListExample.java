import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SynchronizedListExample {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>(List.of("a", "b", "d"));
        Collections.sort(list);       
        List<String> syncList = Collections.synchronizedList(list);
        synchronized (syncList) {
            System.out.println("Synchronized and sorted list:");
            for (String s : syncList) {
                System.out.println(s);
            }
        }
        syncList.add("c");
        Collections.sort(syncList);
        synchronized (syncList) {
            System.out.println("\nList after adding 'c' and re-sorting:");
            for (String s : syncList) {
                System.out.println(s);
            }
        }
    }
}
