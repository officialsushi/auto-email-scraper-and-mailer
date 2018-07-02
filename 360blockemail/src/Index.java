/**
 * Object to describe from what index to what index a certain category is in the database
 * @author sush soohyun choi
 * @version 1.0
 */
public class Index {
    private int[] indices = new int[2];
    private String category;
    public Index(String a, int b, int c){
        category = a;
        this.indices[0] = b;
        this.indices[1] = c;
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
