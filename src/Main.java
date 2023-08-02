import java.util.ArrayList;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        // Press Alt+Enter with your caret at the highlighted text to see how
        // IntelliJ IDEA suggests fixing it.
//        initialize the needed variables
        String filepath = "";
        String algo_name = "";


//        validate the input values before running the program
        if (args.length ==2) {

            filepath = args[1];
            algo_name = args[0];



        } else {
            System.out.println("invalid syntax");
            return;
        }
//        create KB
        KnowledgeBase kb = new KnowledgeBase(filepath);
//        run the engine
        InferenceEngine e = new InferenceEngine(kb,algo_name);
//        get the result
        ArrayList<String> result = e.get_result();
//        join the result
        String output = String.join("", result);
//          print
        System.out.println(output);
    }
}