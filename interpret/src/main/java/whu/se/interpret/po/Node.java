package whu.se.interpret.po;

import java.util.ArrayList;

/**
 * @author xsy
 * @description: 产生式
 * @date 2019/9/26 8:43
 */
public class Node implements Cloneable{

    /*
     * 产生式
     * <expr> → <expr> + <term>
     *
     * left -- 产生式左部 -- <expr>
     * right -- 产生式右部 -- <expr> + <term>
     */
    private String left;
    private ArrayList<String> right;
    private int index = 0;

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public ArrayList<String> getRight() {
        return right;
    }

    public void setRight(ArrayList<String> right) {
        this.right = right;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "Node{" +
                "left='" + left + '\'' +
                ", right=" + right +
                '}';
    }

    public String toSLRString(){
        StringBuilder stringBuilder =  new StringBuilder();
        for (int i = 0;i<right.size();i++){
            if(i == index) {
                stringBuilder.append(".").append(right.get(i));
            } else
                stringBuilder.append(right.get(i));
        }
        if(index == right.size())
            stringBuilder.append(".");
        return left + "->"+stringBuilder.toString()+"\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj instanceof Node){
            return this.left.equals(((Node) obj).left)
                    && this.right.equals(((Node) obj).right)
                    && this.index == ((Node) obj).index;
        }
        return false;
    }

    public boolean equalsExceptIndex(Object obj) {
        if (obj == null)
            return false;
        if (obj instanceof Node){
            return this.left.equals(((Node) obj).left)
                    && this.right.equals(((Node) obj).right);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.index;
    }

    //实现浅拷贝，只需更改index的值时互不影响
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ignored){}
        return null;
    }
}
