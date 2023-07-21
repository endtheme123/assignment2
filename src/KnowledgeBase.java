import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static java.util.Arrays.stream;

public class KnowledgeBase {

    ArrayList<String> given;
    ArrayList<Node> truth_table;
    ArrayList<Vertex> KB;
    String filename;
    String target;
    String[] operators;
    public KnowledgeBase(String filename) {
        this.truth_table = new ArrayList<Node>();
        this.KB = new ArrayList<>();
        this.given = new ArrayList<>();
        this.filename = filename;
        operators = new String[]{"=>", "<=>", "&", "||",")","("};
        GenerateKB(filename);
    }

    void GenerateKB(String filepath) {
        try{
//            read the filename
            File myObj = new File(filepath);
//            read the file
            Scanner myReader = new Scanner(myObj);
//            read in the size of the map
            String isTell = myReader.nextLine();
            if(isTell.trim().equals("TELL")) {

                String KB_input = myReader.nextLine().trim();

//                get the list of entities and initialize graph
                String KB_input_1 = KB_input;
                for(String o: this.operators) {
                    KB_input_1 = KB_input_1.replaceAll(o, ";").replaceAll("~", "");
                }

                ArrayList<String> entities = new ArrayList<>(Arrays.asList(KB_input_1.split(";")));
                entities.remove(entities.size() - 1);
                ArrayList<String> entities_list = new ArrayList<>(new HashSet<String>(entities));
                for(String entity: entities_list) {
                    Vertex en = new Vertex();
                    en.value = entity;
                    KB.add(en);
                }

//                create the relationship between vertices

//                break the implication into literals
                ArrayList<String> KB_input_splited = new ArrayList<>(Arrays.asList(KB_input.split(";")));
                Node root = new Node();
                root.role = "and";
                ArrayList<ArrayList<Node>> logic_trees = new ArrayList<>();
                for(String input: KB_input_splited){
                    ArrayList<Node> logic_tree = new ArrayList<>();
                    for(int i = 0; i<input.length(); i++){
                        Node node = new Node();
                        switch (input.charAt(i)){
                            case ('('):
                                node.role = "(";
                                logic_tree.add(node);
                                break;
                            case (')'):
                                node.role = ")";
                                logic_tree.add(node);
                                break;
                            case ('&'):
                                node.role = "&";
                                logic_tree.add(node);
                                break;
                            case ('|'):
                                i++;
                                node.role = "||";
                                logic_tree.add(node);
                                break;
                            case ('='):
                                i++;
                                node.role = "||";
                                logic_tree.add(new Node());

                                logic_tree.set(logic_tree.size()-1,logic_tree.get(logic_tree.size()-2)) ;
                                logic_tree.get(logic_tree.size()-2).role = "~";
                                logic_tree.add(node);
                                break;

                            case ('<'):
                                i++;
                                i++;
                                i++;
                                node.role = "&";
                                logic_tree.add(new Node());
                                logic_tree.add(new Node());
                                logic_tree.add(new Node());
                                logic_tree.add(node);
                        }


                    }
                }

            }

            String isAsk = myReader.nextLine();
            if(isAsk.trim().equals("ASK")) {
                this.target = myReader.nextLine().trim();
            }


//            close file
            myReader.close();
//          return error if cannot find the file
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();

        }
    }


    public Node find_node(String entity_name) {
        Node result = new Node();
        for(Node entity: this.KB) {
            if(entity.value.equals(entity_name)) {
                result = entity;
            }
        }
        return result;
    }

    public String contain_operator(String value) {

        for(String operator: this.operators) {
            if(value.contains(operator)) {
                return operator;
            }
        }
        return "Something went wrong";
    }

}
