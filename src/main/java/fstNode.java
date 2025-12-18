
import com.google.common.collect.HashMultimap;

import java.util.HashMap;


public class fstNode {

    private boolean isEndNode;

    private boolean isFinal;

    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    private boolean isRoot;
    private String word;
    private HashMap<String, fstEdge> children;

    public HashMultimap<String, fstEdge> ancestors;


    public fstNode(){
        isEndNode = false;
        isRoot = false;
        word = "";
    }

    public fstNode(String word){
        super();
        isEndNode = false;
        this.word = word;
    }

    public String getWord(){
        return this.word;
    }

//    public HashMap<String, fstEdge>  getAncestors(){
//        return this.ancestors;
//    }

    public HashMultimap<String, fstEdge>  getAncestors(){
        return this.ancestors;
    }


    public void setEndNode(boolean endNode) {
        isEndNode = endNode;
    }

    public void addChild(String child, fstEdge edge){
        if(children==null){
            children = new HashMap<>();
        }

        if(ancestors==null){
            ancestors = HashMultimap.create();
        }
        children.put(child, edge);
    }

    public void addAncestor(String ancestor, fstEdge edge){
        if(ancestors==null){
            ancestors = HashMultimap.create();
        }
        ancestors.put(ancestor, edge);
    }

    public void addEndNodeAsChild(fstEdge edge){
        if(children==null){
            children = new HashMap<>();
        }
        children.put("\"", edge);
    }

    public void bindChildAncestor(fstNode child, fstEdge edge){
        if(this.children==null){
            this.children = new HashMap<>();
        }
        if(child.getAncestors()==null){
            child.ancestors = HashMultimap.create();
        }

        this.children.put(child.getWord(), edge);
        child.ancestors.put(this.word, edge);
    }


    public HashMap<String, fstEdge> getChildren(){
        return children;
    }

    public boolean contains(String word){
        if(this.children==null){
            this.children = new HashMap<>();
        }
        return children.containsKey(word);
    }

    public fstNode getChild(String word){
        return children.get(word).getChild();
    }


    public boolean isEndNode(){
        return isEndNode;
    }

    public boolean isRoot(){
        return this.isRoot;
    }

    @Override
    public String toString() {
        return this.word;
    }


}
