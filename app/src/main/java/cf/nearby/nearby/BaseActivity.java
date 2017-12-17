package cf.nearby.nearby;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by tw on 2017-09-28.
 */

public class BaseActivity extends AppCompatActivity {

    public void logout(Activity activity){

        new MaterialDialog.Builder(activity)
                .title(R.string.ok)
                .content(R.string.are_you_sure_logout)
                .positiveText(R.string.logout)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        SharedPreferences.Editor editor = getSharedPreferences("setting", 0).edit();
                        editor.putString("login_id", null);
                        editor.putString("login_pw", null);
                        editor.putBoolean("isEmployee", false);
                        editor.commit();

                        StartActivity.initLoginData();
                        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .negativeText(R.string.cancel)
                .show();

    }

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
                        return true;
                    }
                    case MotionEvent.ACTION_CANCEL:
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

    public void setDateText(TextView tv, String text){

        tv.setText(text);
        tv.setTextColor(getColorId(R.color.dark_gray));
        tv.setTypeface(tv.getTypeface(), Typeface.NORMAL);

    }

    public void redirectStartPage(){
        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void showSnackbar(String msg){
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();
        view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.snackbar_color));
        snackbar.show();
    }

    public void showSnackbar(int id){
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), getResources().getString(id), Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();
        view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.snackbar_color));
        snackbar.show();
    }

    public int getColorId(int id){
        return ContextCompat.getColor(getApplicationContext(), id);
    }



    public void setStatusColor(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        }
    }

    public void setStatusColorDark(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        }
    }

}
