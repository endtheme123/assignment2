import java.util.*;

public class BC extends Algorithm {
    String path;

    public BC(KnowledgeBase kb) {
//        initialize the needed variables and parameters
        super(kb);
        super.name = "BC";
        this.path = "";
    }

//    get the result from the Backward Chaining
    @Override
    public ArrayList<String> calculate_result() {
//        create a new arraylist to store the result
        ArrayList<String> result = new ArrayList<>();
//        invoke BC_entailment to check entailment
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

//    a recursive function to run Backward chaining
    public boolean BC_entailment(KnowledgeBase kb, String target) {
//        update the path value
        path =  target + ";" + path;
//        if the current node is in the truth list of the KB => return true
        for(Node t:kb.truth_list) {
            if (target.equals(t.value)) {
                return true;
            }
        }
//        else
        for (Clause c : kb.clause_list) {
            if (c.conclusion.value.equals(target)) {// => check if there are any clauses that can give the conclusion to the premises of the current node
                Boolean truth = true;
                for(String premise_truth: c.premises) {
                    truth = truth & BC_entailment(kb, premise_truth); //=> run recursively to check if the premises is address able or not
                }
                return truth;
            }
        }

        return false;




    }

}
