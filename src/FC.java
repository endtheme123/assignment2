import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Queue;

public class FC extends Algorithm {
    String path;

    public FC(KnowledgeBase kb) {
        super(kb);
        super.name = "FC";
        this.path = "";
    }

    @Override
    public ArrayList<String> calculate_result() {
        ArrayList<String> result = new ArrayList<>();

        if (FC_entailment(this.kb)) {
            result.add("YES");
            result.add(": ");
            result.add(path);
        } else {
            result.add("NO");
            result.add(": ");
            result.add(path);
        }

//        System.out.println(result.size());
        return result;


    }

    public boolean FC_entailment(KnowledgeBase kb) {
        LinkedHashMap<Clause, Integer> count = new LinkedHashMap<>();
        LinkedHashMap<String, Boolean> inferred = new LinkedHashMap<>();
        Queue<Node> agenda = new LinkedList<Node>();


        for (Clause c : kb.clause_list) {
            count.put(c, c.premises.size());
        }
        for (String symbol : kb.symbol_list) {
            inferred.put(symbol, false);
        }
        for (Node current_truth : kb.truth_list) {
            for (String key : inferred.keySet()) {
                if (key.equals(current_truth)) {
                    inferred.put(key, true);
                }
            }
            agenda.add(current_truth);

        }
        while (!agenda.isEmpty()) {
            Node p = agenda.poll();
            path = path + p.value + ";";
            if (p.value.equals(kb.target.value)) {
                return true;
            }
            if (!inferred.get(p.value)) {
                inferred.put(p.value, true);
                for (Clause c : kb.clause_list) {
                    if (c.premises.contains(p.value)) {
                        count.put(c, count.get(c) - 1);
                    }
                    if (count.keySet().contains(c)) {
                        if (count.get(c) == 0) {
                            count.remove(c);


                            agenda.add(c.conclusion);
//                            System.out.println(c.conclusion.value);


                        }
                    }


                }
            }

        }
        return false;

    }


}
