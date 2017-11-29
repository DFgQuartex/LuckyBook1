package ru.lucky_book.entities.spread;

import com.alexvasilkov.gestures.State;

import java.io.Serializable;

/**
 * Created by Badr
 * on 31.08.2016 22:30.
 */
public class PictureMatrixState implements Serializable{
    private float x;
    private float y;
    private float zoom=1f;
    private float rotation;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void fromState(State state){
        x=state.getX();
        y=state.getY();
        zoom=state.getZoom();
        rotation=state.getRotation();
    }

    public State toState(){
        State state=new State();
        state.set(x,y,zoom,rotation);
        return state;
    }
    public void set(float x, float y, float zoom, float rotation) {
        // Keeping rotation within the range [-180..180]
        while (rotation < -180f) {
            rotation += 360f;
        }
        while (rotation > 180f) {
            rotation -= 360f;
        }

        this.x = x;
        this.y = y;
        this.zoom = zoom;
        this.rotation = rotation;
    }
}
