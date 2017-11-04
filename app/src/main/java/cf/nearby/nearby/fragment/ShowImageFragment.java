package cf.nearby.nearby.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import cf.nearby.nearby.R;
import cf.nearby.nearby.activity.ShowImageActivity;


public class ShowImageFragment extends BaseFragment {
    // UI
    private View view;
    private Context context;
    private ImageView imageView;
    private PhotoViewAttacher mAttacher;

    private String image;
    private boolean touched = true;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        image = getArguments().getString("url");
//        image = getArguments().get("url");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // UI
        view = inflater.inflate(R.layout.fragment_show_image, container, false);
        context = container.getContext();

        init();

        return view;

    }

    @Override
    public void onStart(){
        super.onStart();
    }

    private void init(){

        PhotoView photoView = (PhotoView)view.findViewById(R.id.image);

        mAttacher = new PhotoViewAttacher(photoView);

        Picasso.with(context)
                .load(image)
                .into(photoView, new Callback() {
                    @Override
                    public void onSuccess() {
                        mAttacher.update();
                    }

                    @Override
                    public void onError() {

                    }
                });

        mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                setBackground();
            }
        });

//        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
//            @Override
//            public void onPhotoTap(View view, float x, float y) {
//
//                setBackground();
//            }
//
//            @Override
//            public void onOutsidePhotoTap() {
//
//                setBackground();
//            }
//        });

    }

    private void setBackground(){


        if(touched){
            ((ShowImageActivity)getActivity()).hideToolbar();
            touched = false;
        }else{
            ((ShowImageActivity)getActivity()).showToolbar();
            touched = true;
        }

    }
}
