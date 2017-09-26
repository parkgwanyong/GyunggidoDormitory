package org.androidtown.prototype;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageZoomActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    private PhotoViewAttacher mAttacher;

    private Toolbar mToolbar;

    private LinearLayout mImageZoomLayout;

    private boolean appBarShow = true;


    private ImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zoom);

        mToolbar = (Toolbar) findViewById(R.id.zoom_image_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("사진");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.repair_write_cancel);

        mImageView = (ImageView) findViewById(R.id.image_view);

        mImageZoomLayout = (LinearLayout) findViewById(R.id.image_zoom_layout);

        mImageZoomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ImageZoomActivity.this, "실행됨",Toast.LENGTH_SHORT).show();

            }
        });



        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("로딩중");
        mProgressDialog.setMessage("잠시만 기다려주세요.");
        mProgressDialog.show();

        final String image_uri = getIntent().getStringExtra("image_uri");


        //final Bitmap image = Picasso.with(this).load(image_uri).get();

        Picasso.with(getApplicationContext()).load(image_uri).into(mImageView, new com.squareup.picasso.Callback(){
            @Override
            public void onSuccess() {
                mProgressDialog.dismiss();
                mAttacher = new PhotoViewAttacher(mImageView);
                mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(View view, float v, float v1) {
                        if(appBarShow){
                            getSupportActionBar().hide();
                            appBarShow = false;
                        }else{
                            getSupportActionBar().show();
                            appBarShow = true;
                        }
                    }

                    @Override
                    public void onOutsidePhotoTap() {
                        if(appBarShow){
                            getSupportActionBar().hide();
                            appBarShow = false;
                        }else{
                            getSupportActionBar().show();
                            appBarShow = true;
                        }
                    }
                });
            }

            @Override
            public void onError() {
                mProgressDialog.dismiss();
                Toast.makeText(ImageZoomActivity.this,"사진을 불러올 수 없습니다.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.below_cancel_out, R.anim.below_cancel_in);
                return true;
        }
        return super.onOptionsItemSelected(item);
    };

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.below_cancel_out, R.anim.below_cancel_in);
    }
}


/*
        Picasso.with(getApplicationContext()).load(image_uri).into(mImageView, new com.squareup.picasso.Callback(){
            @Override
            public void onSuccess() {
                mProgressDialog.dismiss();
            }

            @Override
            public void onError() {
                mProgressDialog.dismiss();
                Toast.makeText(ImageZoomActivity.this,"사진을 불러올 수 없습니다.",Toast.LENGTH_SHORT).show();
            }
        });
    }
}

*/

/*
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int screenHeight = displayMetrics.heightPixels;
                int screenWidth = displayMetrics.widthPixels;

                int maxX = (int)((width / 2) - (screenWidth / 2));
                int maxY = (int)((height / 2) - (screenHeight / 2));

                // set scroll limits
                final int maxLeft = (maxX * -1);
                final int maxRight = maxX;
                final int maxTop = (maxY * -1);
                final int maxBottom = maxY;

                // set touchlistener
                mImageView.setOnTouchListener(new View.OnTouchListener()
                {
                    float downX, downY;
                    int totalX, totalY;
                    int scrollByX, scrollByY;
                    public boolean onTouch(View view, MotionEvent event)
                    {
                        float currentX, currentY;
                        switch (event.getAction())
                        {
                            case MotionEvent.ACTION_DOWN:
                                downX = event.getX();
                                downY = event.getY();
                                break;

                            case MotionEvent.ACTION_MOVE:
                                currentX = event.getX();
                                currentY = event.getY();
                                scrollByX = (int)(downX - currentX);
                                scrollByY = (int)(downY - currentY);

                                // scrolling to left side of image (pic moving to the right)
                                if (currentX > downX)
                                {
                                    if (totalX == maxLeft)
                                    {
                                        scrollByX = 0;
                                    }
                                    if (totalX > maxLeft)
                                    {
                                        totalX = totalX + scrollByX;
                                    }
                                    if (totalX < maxLeft)
                                    {
                                        scrollByX = maxLeft - (totalX - scrollByX);
                                        totalX = maxLeft;
                                    }
                                }

                                // scrolling to right side of image (pic moving to the left)
                                if (currentX < downX)
                                {
                                    if (totalX == maxRight)
                                    {
                                        scrollByX = 0;
                                    }
                                    if (totalX < maxRight)
                                    {
                                        totalX = totalX + scrollByX;
                                    }
                                    if (totalX > maxRight)
                                    {
                                        scrollByX = maxRight - (totalX - scrollByX);
                                        totalX = maxRight;
                                    }
                                }

                                // scrolling to top of image (pic moving to the bottom)
                                if (currentY > downY)
                                {
                                    if (totalY == maxTop)
                                    {
                                        scrollByY = 0;
                                    }
                                    if (totalY > maxTop)
                                    {
                                        totalY = totalY + scrollByY;
                                    }
                                    if (totalY < maxTop)
                                    {
                                        scrollByY = maxTop - (totalY - scrollByY);
                                        totalY = maxTop;
                                    }
                                }

                                // scrolling to bottom of image (pic moving to the top)
                                if (currentY < downY)
                                {
                                    if (totalY == maxBottom)
                                    {
                                        scrollByY = 0;
                                    }
                                    if (totalY < maxBottom)
                                    {
                                        totalY = totalY + scrollByY;
                                    }
                                    if (totalY > maxBottom)
                                    {
                                        scrollByY = maxBottom - (totalY - scrollByY);
                                        totalY = maxBottom;
                                    }
                                }

                                mImageView.scrollBy(scrollByX, scrollByY);
                                downX = currentX;
                                downY = currentY;
                                break;

                        }

                        return true;
                    }
                });
*/
