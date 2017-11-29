package ru.lucky_book.entities.spread;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class, that represents spread of two pages. (разворот)
 *
 * Created by histler
 * on 29.08.16 15:36.
 */
public class Spread implements Serializable{
    private Page left;
    private Page right;

    public Page getLeft() {
        return left;
    }

    public void setLeft(Page left) {
        this.left = left;
    }

    public Page getRight() {
        return right;
    }

    public void setRight(Page right) {
        this.right = right;
    }

    public int getTotalImages(){
        return (left!=null&&left.getPictures()!=null?left.getPictures().length:0)
                +(right!=null&&right.getPictures()!=null?right.getPictures().length:0);
    }

    public List<Picture> getAllPictures(){
        List<Picture> pictures=left!=null?new ArrayList<>(Arrays.asList(left.getPictures())):new ArrayList<Picture>();
        if(right!=null){
            pictures.addAll(Arrays.asList(right.getPictures()));
        }
        return pictures;
    }
}
