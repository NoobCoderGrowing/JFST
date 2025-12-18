public class fstEdge {

    private long output;

    private fstNode child;

    private fstNode ancestor;

    public void setOutput(long output) {
        this.output = output;
    }

    public fstEdge(){
        this.output = 0;
    }

    public fstEdge(long output){
        this.output = output;
    }

    public void setChild(fstNode child) {
        this.child = child;
    }

    public void setAncestor(fstNode ancestor) {
        this.ancestor = ancestor;
    }

    public void setChildAncestor(fstNode child, fstNode ancestor){
        this.child = child;
        this.ancestor = ancestor;
    }

    public long getOutput() {
        return output;
    }

    public fstNode getChild() {
        return child;
    }

    public fstNode getAncestor() {
        return ancestor;
    }
}
