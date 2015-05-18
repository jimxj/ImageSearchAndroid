package com.jim.imagesearch.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jim.imagesearch.R;
import com.jim.imagesearch.api.GoogleImageSearch;
import com.jim.imagesearch.model.ImageResult;
import com.ortiz.touch.TouchImageView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ImageDetailActivity extends ActionBarActivity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
  public static final String TAG = "ImageDetailActivity";

//  @InjectView(R.id.tvCaption)
//  TextView tvCaption;

  @InjectView(R.id.tvSite)
  TextView tvSite;

  @InjectView(R.id.ivBigImage)
  TouchImageView ivBigImage;

  @InjectView(R.id.ivLink)
  ImageView ivLink;

  @InjectView(R.id.rlBottom)
  RelativeLayout rlBottom;

  private GestureDetectorCompat mDetector;

  int currentIndex;

  ImageResult imageResult;

  int screenWidthInPixel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // Disable action bar animation
    //https://developer.android.com/training/basics/actionbar/overlaying.html#EnableOverlay
    //getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
    //getWindow().requestFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_image_detail);

    ButterKnife.inject(this);

    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayShowTitleEnabled(true);
    actionBar.setLogo(R.drawable.ic_launcher);

    // Instantiate the gesture detector with the
    // application context and an implementation of
    // GestureDetector.OnGestureListener
    mDetector = new GestureDetectorCompat(this,this);
    // Set the gesture detector as the double tap
    // listener.
    mDetector.setOnDoubleTapListener(this);

    currentIndex = getIntent().getIntExtra("imageIndex", 0);
    loadImageDetial(currentIndex);

    hideOthers();

    DisplayMetrics metrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(metrics);
    screenWidthInPixel = metrics.widthPixels;

    ivBigImage.setOnTouchImageViewListener(new TouchImageView.OnTouchImageViewListener() {
      @Override
      public void onMove() {
        Log.d(TAG, "--------TouchImageView.OnTouchImageViewListener.onMove");
      }
    });
    ivBigImage.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.d(TAG, "--------TouchImageView.OnTouchListener.onTouch");
        mDetector.onTouchEvent(motionEvent);
        return true;
      }
    });
    ivBigImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.d(TAG, "--------TouchImageView.OnClickListener.onClick");
      }
    });

    ivLink.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        openWebLink();
      }
    });
  }

  private void openWebLink() {
    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageResult.getUrl()));
    startActivity(Intent.createChooser(browserIntent, "Open the web page"));
  }

  @Override
  protected void onResume() {
    super.onResume();

  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    this.mDetector.onTouchEvent(event);
    // Be sure to call the superclass implementation
    return super.onTouchEvent(event);
//    int action = MotionEventCompat.getActionMasked(event);
//
//    switch(action) {
//      case (MotionEvent.ACTION_DOWN) :
//        Log.d(DEBUG_TAG, "Action was DOWN");
//        return true;
//      case (MotionEvent.ACTION_MOVE) :
//        Log.d(DEBUG_TAG,"Action was MOVE");
//        return true;
//      case (MotionEvent.ACTION_UP) :
//        Log.d(DEBUG_TAG,"Action was UP");
//        return true;
//      case (MotionEvent.ACTION_CANCEL) :
//        Log.d(DEBUG_TAG,"Action was CANCEL");
//        return true;
//      case (MotionEvent.ACTION_OUTSIDE) :
//        Log.d(DEBUG_TAG,"Movement occurred outside bounds " +
//                "of current screen element");
//        return true;
//      default :
//        return super.onTouchEvent(event);
//    }
  }

  private void loadNextImage() {
    ImageResult result = loadImageDetial(currentIndex + 1);
    if(null != result) {
      currentIndex++;
    }
  }

  private void loadPreviousImage() {
    ImageResult result = loadImageDetial(currentIndex - 1);
    if(null != result) {
      currentIndex--;
    }
  }

  private ImageResult loadImageDetial(int index) {
    imageResult = null;
    if(index >= 0) {
      imageResult = GoogleImageSearch.getInstance().getImageResult(index);
      if (null != imageResult) {
        //tvCaption.setText(Html.fromHtml(imageResult.getTitle()));
        getSupportActionBar().setTitle(Html.fromHtml(imageResult.getTitle()));
        tvSite.setText(imageResult.getVisibleUrl());

        new AsyncTask<Void, Void, Bitmap>() {

          @Override
          protected Bitmap doInBackground(Void... voids) {
            try {
              return Picasso.with(ImageDetailActivity.this)
                      .load(Uri.parse(imageResult.getTbUrl()))
                      .resize(imageResult.getWidthInt(),imageResult.getHeightInt())
                      .networkPolicy(NetworkPolicy.OFFLINE)
                      .get();
            } catch (IOException e) {
              e.printStackTrace();
            }
            return null;
          }

          @Override
          protected void onPostExecute(Bitmap bitmap) {
            RequestCreator requestCreator = Picasso.with(ImageDetailActivity.this).load(Uri.parse(imageResult.getUrl()));
            if(null != bitmap) {
              requestCreator.placeholder(new BitmapDrawable(getResources(), bitmap));
            }
            requestCreator.into(ivBigImage);
          }
        }.execute();

        //Picasso.with(this).load(Uri.parse(imageResult.getUrl())).placeholder(thumbnailDrawable).into(ivBigImage);
      }
    }

    return imageResult;
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_image_detail, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.mi_share) {
      Uri bmpUri = getLocalBitmapUri(ivBigImage);
      if (bmpUri != null) {

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/html");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("<p>\"" + imageResult.getTitle() + "\" from " + imageResult.getUrl() + "</p>"));
        sharingIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        startActivity(Intent.createChooser(sharingIntent, "Share a Image"));
      }
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onDown(MotionEvent motionEvent) {
    return false;
  }

  @Override
  public void onShowPress(MotionEvent motionEvent) {

  }

  @Override
  public boolean onSingleTapUp(MotionEvent motionEvent) {
    return false;
  }

  @Override
  public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
    return false;
  }

  @Override
  public void onLongPress(MotionEvent motionEvent) {

  }

  @Override
  public boolean onFling(MotionEvent e1, MotionEvent e2, float v, float v1) {
    //mGestureText.setText("onFling " + e1.getX() + " - " + e2.getX());

    if (e1.getX() < e2.getX()) {
      Log.d(TAG, "Left to Right swipe performed");
      loadPreviousImage();
    }

    if (e1.getX() > e2.getX()) {
      Log.d(TAG, "Right to Left swipe performed");
      loadNextImage();
    }

    if (e1.getY() < e2.getY()) {
      Log.d(TAG, "Up to Down swipe performed");
    }

    if (e1.getY() > e2.getY()) {
      Log.d(TAG, "Down to Up swipe performed");
    }

    return true;
  }

  @Override
  public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
    flipOthers();
    return true;
  }

  @Override
  public boolean onDoubleTap(MotionEvent motionEvent) {
    return false;
  }

  @Override
  public boolean onDoubleTapEvent(MotionEvent motionEvent) {
    return false;
  }

  private void flipOthers() {
    if(View.INVISIBLE == rlBottom.getVisibility()) {
      showOthers();
    } else {
      hideOthers();
    }
  }

  private void hideOthers() {
    getSupportActionBar().hide();
    //tvCaption.setVisibility(View.INVISIBLE);
    //tvSite.setVisibility(View.INVISIBLE);
    //ivLink.setVisibility(View.INVISIBLE);
    rlBottom.setVisibility(View.INVISIBLE);
  }

  private void showOthers() {
    getSupportActionBar().show();
//    tvCaption.setVisibility(View.VISIBLE);
    //tvSite.setVisibility(View.VISIBLE);
    //ivLink.setVisibility(View.VISIBLE);
    rlBottom.setVisibility(View.VISIBLE);
  }

  // Returns the URI path to the Bitmap displayed in specified ImageView
  public Uri getLocalBitmapUri(ImageView imageView) {
    // Extract Bitmap from ImageView drawable
    Drawable drawable = imageView.getDrawable();
    Bitmap bmp = null;
    if (drawable instanceof BitmapDrawable){
      bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
    } else {
      return null;
    }
    // Store image to default external storage directory
    Uri bmpUri = null;
    try {
      File file =  new File(Environment.getExternalStoragePublicDirectory(
              Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
      file.getParentFile().mkdirs();
      FileOutputStream out = new FileOutputStream(file);
      bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
      out.close();
      bmpUri = Uri.fromFile(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bmpUri;
  }
}
