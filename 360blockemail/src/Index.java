/**
 * @author sush soohyun choi
 *
 */
public class Index {
    private int[] indices = new int[2];
    private String category;
    public Index(String a, int b, int c){
        category = a;
        this.indices[0] = b;
        this.indices[1] = c;
    }
    public int[] getIndices() {
        return indices;
    }
    public int getStart(){
        return indices[0];
    }
    public int getEnd(){
        return indices[1];
    }
    public String getCategory(){
        return category;
    }
}
