package com.jim.imagesearch.activity;

import android.net.Uri;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.jim.imagesearch.R;
import com.jim.imagesearch.api.GoogleImageSearch;
import com.jim.imagesearch.model.ImageResult;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ImageDetailActivity extends ActionBarActivity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
  public static final String TAG = "ImageDetailActivity";

  @InjectView(R.id.tvCaption)
  TextView tvCaption;

  @InjectView(R.id.tvSite)
  TextView tvSite;

  @InjectView(R.id.ivBigImage)
  ImageView ivBigImage;

  private GestureDetectorCompat mDetector;

  int currentIndex;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_image_detail);

    ButterKnife.inject(this);

    // Instantiate the gesture detector with the
    // application context and an implementation of
    // GestureDetector.OnGestureListener
    mDetector = new GestureDetectorCompat(this,this);
    // Set the gesture detector as the double tap
    // listener.
    mDetector.setOnDoubleTapListener(this);

    currentIndex = getIntent().getIntExtra("imageIndex", 0);
    loadImageDetial(currentIndex);
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
    ImageResult imageResult = null;
    if(index >= 0) {
      imageResult = GoogleImageSearch.getInstance().getImageResult(index);
      if (null != imageResult) {
        tvCaption.setText(Html.fromHtml(imageResult.getTitle()));
        tvSite.setText(imageResult.getVisibleUrl());
        Picasso.with(this).load(Uri.parse(imageResult.getUrl())).into(ivBigImage);
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
    if (id == R.id.action_settings) {
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
    return false;
  }

  @Override
  public boolean onDoubleTap(MotionEvent motionEvent) {
    return false;
  }

  @Override
  public boolean onDoubleTapEvent(MotionEvent motionEvent) {
    return false;
  }
}
