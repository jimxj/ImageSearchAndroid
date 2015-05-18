package com.jim.imagesearch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.etsy.android.grid.StaggeredGridView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.jim.imagesearch.R;
import com.jim.imagesearch.adapter.ImageGridArrayAdapter;
import com.jim.imagesearch.api.GoogleImageSearch;
import com.jim.imagesearch.connectivity.ConnectivityListener;
import com.jim.imagesearch.connectivity.ConnectivityManager;
import com.jim.imagesearch.model.SearchFilter;
import com.jim.imagesearch.util.EndlessScrollListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;


public class ImageSearchActivity extends ActionBarActivity {
  public static final String TAG = "ImageSearchActivity";

  @InjectView(R.id.gvImages)
  StaggeredGridView gridView;

  @InjectView(R.id.vFilters)
  View vFilters;

  @InjectView(R.id.llNetworkStatus)
  LinearLayout llNetworkStatus;

  ImageGridArrayAdapter imageAdapter;

  MenuItem miFilter;
  MenuItem miActionProgressItem;

  GoogleImageSearch.AsyncResultCallback imageSearchCallback;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_image_search);

    ActionBar actionBar = getSupportActionBar();
    //actionBar.setDisplayShowTitleEnabled(false);
    //actionBar.setDisplayUseLogoEnabled(true);
    //actionBar.setDisplayShowHomeEnabled(true);
    //actionBar.setIcon(R.drawable.ic_launcher);

    ButterKnife.inject(this);
    Fresco.initialize(this);
    ConnectivityManager.initialize(this.getApplicationContext());

    ConnectivityManager.getInstance().registerListener(new ConnectivityListener() {
      @Override
      public void onConnectivityStatusChanged(int lastKnowStatus, int newStatus) {
        if(newStatus == ConnectivityManager.TYPE_NOT_CONNECTED) {
          llNetworkStatus.setVisibility(View.VISIBLE);
        } else {
          llNetworkStatus.setVisibility(View.INVISIBLE);
        }
      }
    });

    if(ConnectivityManager.TYPE_NOT_CONNECTED == ConnectivityManager.getInstance().getConnectivityStatus()) {
      llNetworkStatus.setVisibility(View.VISIBLE);
    }

    imageAdapter = new ImageGridArrayAdapter(this, GoogleImageSearch.getInstance().getResultList());
    gridView.setAdapter(imageAdapter);

    initFilterView();

    gridView.setOnScrollListener(new EndlessScrollListener() {

      @Override
      public void onLoadMore(int page, int totalItemsCount) {
        Log.i(TAG, "page : " + page + ", totalItemsCount : " + totalItemsCount + ", current page : " + GoogleImageSearch.getInstance().getCurrentPage());
        doSearchNextPageAsync();
      }
    });

    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(ImageSearchActivity.this, ImageDetailActivity.class);
        intent.putExtra("imageIndex", i);
        startActivity(intent);
      }
    });

    imageSearchCallback = new GoogleImageSearch.AsyncResultCallback() {
      @Override
      public void onSuccess(boolean dataChange) {
        if(View.VISIBLE == llNetworkStatus.getVisibility()) {
          llNetworkStatus.setVisibility(View.INVISIBLE);
        }

        hideHeaderProgress();
        if(dataChange) {
          imageAdapter.notifyDataSetChanged();
        }
      }

      @Override
      public void onFailure(RetrofitError error) {
        if(error.getKind() == RetrofitError.Kind.NETWORK) {
          llNetworkStatus.setVisibility(View.VISIBLE);
        }
        hideHeaderProgress();
      }
    };
  }

  @Override
  protected void onResume() {
    super.onResume();

    if(GoogleImageSearch.getInstance().hasChange()) {
      imageAdapter.notifyDataSetChanged();
    }
  }

  @Override
  protected void onStart() {
    super.onStart();

    GoogleImageSearch.getInstance().loadSearchParameters(this);
    if(null != GoogleImageSearch.getInstance().getCurrentKeyword()) {
      doSearchImageAsync();
    }
  }

  @Override
  protected void onStop() {
    super.onStop();

    GoogleImageSearch.getInstance().saveSearchParameters(this);
  }

  private void doSearchImageAsync() {
    showHeaderProgress();

    GoogleImageSearch.getInstance().searchImage(imageSearchCallback);
  }

  private void doSearchNextPageAsync() {
    showHeaderProgress();

    GoogleImageSearch.getInstance().searchNextPage(imageSearchCallback);
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
        GoogleImageSearch.getInstance().setCurrentKeyword(query);
        doSearchImageAsync();

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

    return super.onPrepareOptionsMenu(menu);
  }

  private void hideHeaderProgress() {
    //headerView.getLayoutParams().height = 0;
    if(null != miActionProgressItem) {  // onPrepareOptionsMenu may star later than onPrepareOptionsMenu
      miActionProgressItem.setVisible(false);
    }
  }

  private void showHeaderProgress() {
    //headerView.getLayoutParams().height = 0;
    if(null != miActionProgressItem) {
      miActionProgressItem.setVisible(true);
    }
  }

  private void initFilterView() {
    vFilters.setVisibility(View.INVISIBLE);
    vFilters.setAlpha(0.95f);


    final Spinner spSize = (Spinner) vFilters.findViewById(R.id.spSize);
    final Spinner spType = (Spinner) vFilters.findViewById(R.id.spType);
    final Spinner spColor = (Spinner) vFilters.findViewById(R.id.spColor);
    final EditText etSite = (EditText) vFilters.findViewById(R.id.etSite);

    Button btnSave = (Button) vFilters.findViewById(R.id.btSave);
    btnSave.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        vFilters.setVisibility(View.INVISIBLE);

        SearchFilter newFilter = new SearchFilter();

        if (!isAll(spSize.getSelectedItem().toString())) {
          newFilter.setSize(spSize.getSelectedItem().toString());
        }
        if (!isAll(spType.getSelectedItem().toString())) {
          newFilter.setType(spType.getSelectedItem().toString());
        }
        if (!isAll(spColor.getSelectedItem().toString())) {
          newFilter.setColor(spColor.getSelectedItem().toString());
        }
        if (null != etSite.getText()) {
          newFilter.setSite(etSite.getText().toString());
        }

        vFilters.setVisibility(View.INVISIBLE);

        if (!newFilter.equals(GoogleImageSearch.getInstance().getSearchFilter())) {
          GoogleImageSearch.getInstance().setSearchFilter(newFilter);
          doSearchImageAsync();
        }
      }
    });

    Button btnCancel = (Button) vFilters.findViewById(R.id.btCancel);
    btnCancel.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {
        vFilters.setVisibility(View.INVISIBLE);
      }
    });
  }

  private boolean isAll(String selectedValue) {
    return "ALL".equalsIgnoreCase(selectedValue);
  }

  private void showNetworkError() {

  }

}
