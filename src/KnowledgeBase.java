import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static java.util.Arrays.stream;

public class KnowledgeBase {

    ArrayList<String> given;
    ArrayList<ArrayList<Node>> logic_trees;
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
        operators = new String[]{"=>", "<=>", "&", "||", "\\)", "\\("};
        GenerateKB(filename);
    }

    void GenerateKB(String filepath) {
        try {
//            read the filename
            File myObj = new File(filepath);
//            read the file
            Scanner myReader = new Scanner(myObj);
//            read in the size of the map
            String isTell = myReader.nextLine();
            if (isTell.trim().equals("TELL")) {

                String KB_input = myReader.nextLine().replaceAll(" ", "");
                System.out.println(KB_input);
//                get the list of entities and initialize graph
                String KB_input_1 = KB_input;
                for (String o : this.operators) {
                    KB_input_1 = KB_input_1.replaceAll(o, ";").replaceAll("~", "");
                }

                ArrayList<String> entities = new ArrayList<>(Arrays.asList(KB_input_1.split(";")));
                entities.remove(entities.size() - 1);
                ArrayList<String> entities_list = new ArrayList<>(new HashSet<String>(entities));
                for (String entity : entities_list) {
                    Vertex en = new Vertex();
                    en.value = entity;
                    KB.add(en);
                }

//                create the relationship between vertices

//                break the implication into literals
                ArrayList<String> KB_input_splited = new ArrayList<>(Arrays.asList(KB_input.split(";")));
                Node root = new Node();
                root.role = Operators.AND;
                root.value = "&";
                this.logic_trees = new ArrayList<>();
                for (String input : KB_input_splited) {
                    ArrayList<Node> logic_tree = new ArrayList<>();
                    int i = 0;
                    System.out.println(input);

                    while (i < input.length()) {
                        Node node = new Node();
                        switch (input.charAt(i)) {
                            case ('('):
                                node.role = Operators.LEFT_BRAC;
                                node.value = "(";
                                logic_tree.add(node);
                                i++;
                                break;
                            case (')'):
                                node.role = Operators.RIGHT_BRAC;
                                node.value = ")";
                                logic_tree.add(node);
                                i++;
                                break;
                            case ('&'):
                                node.role = Operators.AND;
                                node.value = "&";
                                logic_tree.add(node);
                                i++;
                                break;

                            case ('~'):
                                node.role = Operators.NOT;
                                node.value = "~";
                                logic_tree.add(node);
                                i++;
                                break;
                            case ('|'):

                                node.role = Operators.OR;
                                node.value = "||";
                                logic_tree.add(node);
                                i++;
                                i++;
                                break;
                            case ('='):


                                node.role = Operators.IMPLICATION;
                                node.value = "=>";

                                logic_tree.add(node);
                                i++;
                                i++;
                                break;

                            case ('<'):


                                node.role = Operators.BICON;
                                node.value = "<=>";

                                logic_tree.add(node);
                                i++;
                                i++;
                                i++;
                                break;
                            default:
                                if (Character.isLetter(input.charAt(i))) {
                                    int endPosition = i + 1;
                                    while (endPosition < input.length() && Character.isLetterOrDigit(input.charAt(endPosition))) {
                                        endPosition++;
                                    }
                                    String variable = input.substring(i, endPosition);
                                    node.role = Operators.SENTENCE;
                                    node.value = variable;
                                    logic_tree.add(node);
                                    i = endPosition;
                                } else {
                                    throw new IllegalArgumentException("Invalid character in expression: " + input.charAt(i));
                                }
                                break;
                        }


                    }
                    logic_trees.add(logic_tree);
                }
//                System.out.print(logic_trees.get(2).get(0).role);
                for (ArrayList<Node> tree : logic_trees) {
                    Node aroot = KB_tree_parse(tree);
//                    for (Node n : tree) {
//                        System.out.print(n.value);
//                    }
                    System.out.println(tree_print(aroot));
                }

            }


            String isAsk = myReader.nextLine();
            if (isAsk.trim().equals("ASK")) {
                this.target = myReader.nextLine().replaceAll(" ", "");
            }


//            close file
            myReader.close();
//          return error if cannot find the file
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();

        }
    }

    int tree_size (Node root) {
        int i = 1;
        Stack<Node> stack = new Stack<>();
        stack.push(root);
        while(!stack.empty()) {
            Node current = stack.pop();
            if(!(current.left==null )) {
                stack.push(current.left);
                i++;
            }
            if(!(current.right==null )) {
                stack.push(current.right);
                i++;
            }
        }

        return i;
    }

    String tree_print (Node root) {
        String s = "";
        Stack<Node> stack = new Stack<>();
        stack.push(root);
        s = root.value;
        while(!stack.empty()) {
            Node current = stack.pop();
            if(!(current.left==null )) {
                stack.push(current.left);
                s = current.left.value + s;
            }
            if(!(current.right==null )) {
                stack.push(current.right);
                s = s + current.right.value;
            }
        }

        return s;
    }

    Node KB_tree_parse(ArrayList<Node> tree) {
        Node subroot = new Node();
        subroot.role = Operators.INIT;
        Node current = subroot;
        int i = 0;
        while (i < tree.size()) {
            if(tree.get(i).role.equals(Operators.RIGHT_BRAC)){
                break;
            }
            System.out.println("at least working");
            if (tree.get(i).role.equals(Operators.LEFT_BRAC)) {

                i++;
                ArrayList<Node> subtree = new ArrayList<>(tree.subList(i, tree.size()));
                Node temp = KB_tree_parse(subtree);
                tree.subList(i,i+tree_size(temp)).clear();
                tree.set(i-1,temp);
                System.out.println(tree.get(i).value);
                if(subroot.role.equals(Operators.INIT)) {

                    current = temp;
                    subroot = temp;
                } else {
                    current.right = temp;
                    temp.parent = current;
                }




            } else {
                if (tree.get(i).role.equals(Operators.SENTENCE)) {

                    if(subroot.role.equals(Operators.INIT)) {
                        current = tree.get(i);
                        subroot = tree.get(i);
                    }

                    i++;

                } else if(tree.get(i).role.equals(Operators.NOT)) {
                    tree.get(i).right = tree.get(i+1);

                    if(subroot.role.equals(Operators.INIT)) {

                        subroot = tree.get(i);
                    }
                    tree.remove(i+1);
                    current = tree.get(i);
                    i++;
                }  else {
                    Node order_compare = current;
                    while((tree.get(i).role.ordinal() < order_compare.role.ordinal()) & (order_compare.parent != null)) {
                        order_compare = order_compare.parent;
                    }

                    if(order_compare.parent != null) {
                        tree.get(i).left = order_compare.right.parent.right;
                        order_compare.right.parent.right = tree.get(i);
                        tree.get(i).parent = tree.get(i).left.parent;
                        tree.get(i).left.parent = tree.get(i);
                        tree.get(i+1).parent = tree.get(i);
                        tree.get(i).right = tree.get(i+1);
                        current = tree.get(i);


                    } else if(order_compare.role.equals(Operators.SENTENCE)) {
                        tree.get(i).left = order_compare;
                        tree.get(i-1).parent = tree.get(i);
                        tree.get(i+1).parent = tree.get(i);
                        tree.get(i).right = tree.get(i+1);
                        subroot = tree.get(i);
                        current = tree.get(i);
                    } else {
                        tree.get(i).left = order_compare.left.parent;
                        order_compare.right.parent.parent =tree.get(i);
                        tree.get(i).right = tree.get(i+1);
                        tree.get(i+1).parent = tree.get(i);
                        subroot = tree.get(i);
                        current = tree.get(i);
                    }
                    i++;
                }
            }
        }

        return subroot;
    }


}
