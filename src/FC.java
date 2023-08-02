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
//        create a new arraylist to store the result
        ArrayList<String> result = new ArrayList<>();
//        invoke FC_entailment to check entailment
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
//        create a count table that contain the count of the premises of the algorithm
        LinkedHashMap<Clause, Integer> count = new LinkedHashMap<>();
//        create a table contain all the symbols in the knowledge base and their logical value
        LinkedHashMap<String, Boolean> inferred = new LinkedHashMap<>();
//        Create an agenda for the algorithm
        Queue<Node> agenda = new LinkedList<Node>();

//        put all the clauses into the count table
        for (Clause c : kb.clause_list) {
            count.put(c, c.premises.size());
        }
//        put all the symbol into the inferred table => false initially
        for (String symbol : kb.symbol_list) {
            inferred.put(symbol, false);
        }
//        put all the ground truth node into the agenda
        for (Node current_truth : kb.truth_list) {
//
            for (String key : inferred.keySet()) {
                if (key.equals(current_truth)) {
                    inferred.put(key, true);
                }
            }
            agenda.add(current_truth);

        }

        while (!agenda.isEmpty()) {
//            pull out a value from agenda
            Node p = agenda.poll();
//            update the output string
            path = path + p.value + ";";
//            if the value is the target => end the function return true
            if (p.value.equals(kb.target.value)) {
                return true;
            }
//            if the current symbols is false in the inferred table
            if (!inferred.get(p.value)) {
//                update it to true
                inferred.put(p.value, true);
//                check if there is any clause contain the symbols as its premise => update the count table if yes
                for (Clause c : kb.clause_list) {
                    if (c.premises.contains(p.value)) {
                        count.put(c, count.get(c) - 1);
                    }
//                    if there is a clause inside count table has its count reach 0 => add it conclusion to agenda and remove it from count table
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
