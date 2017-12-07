package cf.nearby.nearby.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import cf.nearby.nearby.R;

/**
 * Created by tw on 2017-09-28.
 */

public class BaseFragment extends Fragment {

    Rect rect;

    public void setCardButtonOnTouchAnimation(final View v){

        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
//                System.out.println(motionEvent.getAction());
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN: {
                        rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
//                        System.out.println("action down");
                        Animation anim = new ScaleAnimation(
                                1f, 0.95f, // Start and end values for the X axis scaling
                                1f, 0.95f, // Start and end values for the Y axis scaling
                                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
                        anim.setFillAfter(true); // Needed to keep the result of the animation
                        anim.setDuration(300);
                        v.startAnimation(anim);
                        v.requestLayout();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
//                        System.out.println("action up");
                        Animation anim = new ScaleAnimation(
                                0.95f, 1f, // Start and end values for the X axis scaling
                                0.95f, 1f, // Start and end values for the Y axis scaling
                                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
                        anim.setFillAfter(true); // Needed to keep the result of the animation
                        anim.setDuration(300);
                        v.startAnimation(anim);
                        v.requestLayout();
                        if(!rect.contains(v.getLeft() + (int) motionEvent.getX(), v.getTop() + (int) motionEvent.getY())){
                            // User moved outside bounds
                        }else{
                            v.callOnClick();
                        }
                        return true;
                    }
                }
                return false;
            }
        };
        v.setOnTouchListener(onTouchListener);
    }

    public void setFadeInAnimation(View view){
        Animation animation = new AlphaAnimation(0, 1);
        animation.setDuration(500);
        view.setAnimation(animation);
    }

    public void setFadeOutAnimation(View view){
        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(500);
        view.setAnimation(animation);
    }

    public void showSnackbar(View g_view, Context context, String msg){
        Snackbar snackbar = Snackbar.make(g_view, msg, Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();
        view.setBackgroundColor(ContextCompat.getColor(context, R.color.snackbar_color));
        snackbar.show();
    }

    public void showSnackbar(View g_view, Context context, int id){
        Snackbar snackbar = Snackbar.make(g_view, getResources().getString(id), Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();
        view.setBackgroundColor(ContextCompat.getColor(context, R.color.snackbar_color));
        snackbar.show();
    }

    public int getColorId(Context context, int id){
        return ContextCompat.getColor(context, id);
    }

}
