import java.util.*;

public class BC extends Algorithm {
    String path;

    public BC(KnowledgeBase kb) {
        super(kb);
        super.name = "BC";
        this.path = "";
    }

    @Override
    public ArrayList<String> calculate_result() {
        ArrayList<String> result = new ArrayList<>();

        if (BC_entailment(this.kb, this.kb.target.value)) {
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

    public boolean BC_entailment(KnowledgeBase kb, String target) {
        path =  target + ";" + path;
        for(Node t:kb.truth_list) {
            if (target.equals(t.value)) {
                return true;
            }
        }

        for (Clause c : kb.clause_list) {
            if (c.conclusion.value.equals(target)) {
                Boolean truth = true;
                for(String premise_truth: c.premises) {
                    truth = truth & BC_entailment(kb, premise_truth);
                }
                return truth;
            }
        }

        return false;




    }

}
