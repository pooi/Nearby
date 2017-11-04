package cf.nearby.nearby.util;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

import cf.nearby.nearby.activity.ShowImageActivity;


/**
 * Created by tw on 2017. 7. 10..
 */

public class AdvancedImageView extends AppCompatImageView {

    private ArrayList<String> imageList;
    private int position;
    private String title;

    public AdvancedImageView(Context context) {
        super(context, null);
        setOnClick();
        initValue();
    }

    public AdvancedImageView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        setOnClick();
        initValue();
    }

    public AdvancedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClick();
        initValue();
    }

    private void initValue(){
        imageList = new ArrayList<>();
        position = 0;
        title = "Image";
    }
    public void setImage(String image){
        initValue();
        imageList.add(image);
    }
    public void setImage(String image, String title){
        this.setImage(image);
        this.title = title;
    }
    public void setImageList(ArrayList<String> imageList){
        initValue();
        this.imageList = imageList;
    }
    public void setImageList(ArrayList<String> imageList, int position){
        this.setImageList(imageList);
        this.position = position;
    }
    public void setImageList(ArrayList<String> imageList, int position, String title){
        this.setImageList(imageList, position);
        this.title = title;
    }
    public void setPosition(int position){
        this.position = position;
    }
    public void setTitle(String title){
        this.title = title;
    }

    public String getImage(){
        if(imageList != null && imageList.size() > 0){
            return imageList.get(0);
        }else{
            return "";
        }
    }
    public ArrayList<String> getImageList(){
        return imageList;
    }
    public int getPosition(){
        return position;
    }
    public String getTitle(){
        return title;
    }

    private void setOnClick(){

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageList != null && imageList.size() > 0){
                    Intent intent = new Intent(getContext(), ShowImageActivity.class);
                    intent.putExtra("image", imageList);
                    intent.putExtra("position", position);
                    intent.putExtra("title", title);
                    getContext().startActivity(intent);
                }
            }
        });

    }

}
