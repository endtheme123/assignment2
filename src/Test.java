import java.util.ArrayList;

public class Test {
    public String test() {
        ArrayList<Integer> a = new ArrayList<>();
        a.add(1);
        a.add(2);
        a.add(3);
        a.add(4);
        a.subList(1, 3).clear();
        String r = "";
        for(Integer i: a) {
            r+= i;
        }
        return r;
    }
}
