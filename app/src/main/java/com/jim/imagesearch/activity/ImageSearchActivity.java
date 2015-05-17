package com.jim.imagesearch.activity;

import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.etsy.android.grid.StaggeredGridView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.jim.imagesearch.R;
import com.jim.imagesearch.adapter.ImageGridArrayAdapter;
import com.jim.imagesearch.api.GoogleImageSearchApi;
import com.jim.imagesearch.model.ImageResult;
import com.jim.imagesearch.model.SearchImageResult;
import com.jim.imagesearch.util.EndlessScrollListener;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ImageSearchActivity extends ActionBarActivity {
  public static final String TAG = "ImageSearchActivity";


  private RestAdapter restAdapter;
  private GoogleImageSearchApi googleImageSearchApi;

  @InjectView(R.id.gvImages)
  StaggeredGridView gridView;

  @InjectView(R.id.vFilters)
  View vFilters;

  ImageGridArrayAdapter imageAdapter;

  MenuItem miFilter;
  MenuItem miActionProgressItem;

  int currentPage = 0;

  String currentKeyword;

  //DrawerLayout mDrawerLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_image_search);

    ButterKnife.inject(this);
    Fresco.initialize(this);

//    headerView = LayoutInflater.from(this).inflate(R.layout.view_progress, null);
//    //headerView.setVisibility(View.INVISIBLE);
//    gridView.addHeaderView(headerView);

    imageAdapter = new ImageGridArrayAdapter(this, new ArrayList<ImageResult>());
    gridView.setAdapter(imageAdapter);

    vFilters.setVisibility(View.INVISIBLE);
    vFilters.setAlpha(0.8f);

    gridView.setOnScrollListener(new EndlessScrollListener() {

      @Override
      public void onLoadMore(int page, int totalItemsCount) {
        Log.i(TAG, "page : " + page + ", totalItemsCount : " + totalItemsCount + ", current page : " + currentPage);
        if (totalItemsCount > currentPage * GoogleImageSearchApi.PAGE_SIZE) {
          Log.i(TAG, " fetch new page");
          currentPage++;
          searchImageAsync(currentKeyword);
        }
      }
    });

    restAdapter = new RestAdapter.Builder()
            .setEndpoint("https://ajax.googleapis.com/ajax/services/search")
            .build();

    googleImageSearchApi = restAdapter.create(GoogleImageSearchApi.class);
  }

  private void searchImageAsync(String q) {
    showHeaderProgress();
    googleImageSearchApi.searchImage(q, "1.0", null, null, null, GoogleImageSearchApi.PAGE_SIZE, currentPage * GoogleImageSearchApi.PAGE_SIZE, new Callback<SearchImageResult>() {
      @Override
      public void success(SearchImageResult searchImageResult, Response response) {
        Log.d(TAG, "Image search result : " + searchImageResult);
        if(null != searchImageResult && null != searchImageResult.getResponseData()) {
          if(currentPage == 0) {
            imageAdapter.clear();
          }
          imageAdapter.addAll(searchImageResult.getResponseData().getResults());
          imageAdapter.notifyDataSetChanged();
        }

        hideHeaderProgress();
      }

      @Override
      public void failure(RetrofitError error) {
        Log.e(TAG, "Failed to search images : " + error);
        hideHeaderProgress();
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_image_search, menu);
    MenuItem searchItem = menu.findItem(R.id.mi_search);
    SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        // perform query here
        Log.i(TAG, "onQueryTextSubmit query :" + query);
        //miActionProgressItem.setVisible(true);
        currentPage = 0;
        currentKeyword = query;
        searchImageAsync(query);

        miFilter.setVisible(true);

        return true;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        Log.i(TAG, "onQueryTextChange query :" + newText);

//                if(null != newText || newText.length() == 0) {
//                    fetchBooks(INIT_SEARCH_TERM);
//                }

        return false;
      }
    });

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.mi_filter) {
      if(vFilters.getVisibility() == View.INVISIBLE) {
        vFilters.setVisibility(View.VISIBLE);
      } else {
        vFilters.setVisibility(View.INVISIBLE);
      }

//      LayoutInflater inflater = LayoutInflater.from(this);
//      fileterView = inflater.inflate(R.layout.view_filters, null, false);
//      fileterView.getBackground().setAlpha(80);
//
//      ViewGroup rootFrameLayout = (ViewGroup) this.getWindow().peekDecorView();
//      rootFrameLayout.addView(fileterView, 1);
//      PopupWindow popupwindow = new PopupWindow(fileterView ,400,300, true);
//      popupwindow.setOutsideTouchable(true);
//      popupwindow.setTouchable(true);
//
//      popupwindow.showAtLocation(getWindow().getDecorView(), Gravity.NO_GRAVITY, 400, 100);
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    // Store instance of the menu item containing progress
    miFilter = menu.findItem(R.id.mi_filter);

    miActionProgressItem = menu.findItem(R.id.miActionProgress);
    // Extract the action-view from the menu item
    ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
    // Extract the action-view from the menu item
    //ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
    // Return to finish

    // FIXME : for testing
    //searchImageAsync("dog");

    return super.onPrepareOptionsMenu(menu);
  }

  private void hideHeaderProgress() {
    //headerView.getLayoutParams().height = 0;
    miActionProgressItem.setVisible(false);
  }

  private void showHeaderProgress() {
    //headerView.getLayoutParams().height = 0;
    miActionProgressItem.setVisible(true);
  }
}
