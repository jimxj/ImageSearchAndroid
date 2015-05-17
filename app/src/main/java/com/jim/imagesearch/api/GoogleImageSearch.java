/**
 * Copyright (c) 2012-2015 Magnet Systems. All rights reserved.
 */
package com.jim.imagesearch.api;

import android.util.Log;

import com.jim.imagesearch.model.ImageResult;
import com.jim.imagesearch.model.SearchFilter;
import com.jim.imagesearch.model.SearchImageResult;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

public class GoogleImageSearch {
  public static final String TAG = "GoogleImageSearch";

  public static final int PAGE_SIZE = 8;

  private int currentPage;
  private int totalPage;

  private boolean hasChange;

  private String currentKeyword;
  private SearchFilter searchFilter;
  private List<ImageResult> resultList;

  private GoogleImageSearchApi googleImageSearchApi;

  private static GoogleImageSearch _instance;

  private GoogleImageSearch() {
    resultList = new ArrayList<>();
    searchFilter = new SearchFilter();

    RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint("https://ajax.googleapis.com/ajax/services/search")
            .build();

    googleImageSearchApi = restAdapter.create(GoogleImageSearchApi.class);
  }

  public static GoogleImageSearch getInstance() {
    if(null == _instance) {
      synchronized (GoogleImageSearch.class) {
        _instance = new GoogleImageSearch();
      }
    }

    return _instance;
  }

  public void searchImage() {
    searchImage(null);
  }

  public void searchNextPage(final AsyncResultCallback callback) {
    if(currentPage < totalPage - 1) {
      Log.d(TAG, "------next page for " + currentPage + ", totalPage = " + totalPage);
      currentPage++;
      searchImage(callback);
    } else {
      if(null != callback) {
        callback.onSuccess(false);
      }
    }
  }

  public void searchImage(final AsyncResultCallback callback) {
    Log.i(TAG, "Searching keyword : " + currentKeyword + ", filters : " + searchFilter);
    googleImageSearchApi.searchImage(currentKeyword, "1.0", searchFilter.getType(), searchFilter.getSize(), searchFilter.getColor(), searchFilter.getSite(), PAGE_SIZE, currentPage * PAGE_SIZE, new Callback<SearchImageResult>() {
      @Override
      public void success(SearchImageResult searchImageResult, Response response) {
        Log.d(TAG, "Image search result : " + searchImageResult);
        boolean dataChange = false;
        if (null != searchImageResult && null != searchImageResult.getResponseData()) {
          if (currentPage == 0) {
            Log.d(TAG, "------recreate list : ");
            resultList.clear();
            totalPage = searchImageResult.getResponseData().getCursor().getPages().size();
          }
          resultList.addAll(searchImageResult.getResponseData().getResults());
          Log.d(TAG, "------added to list : " + searchImageResult.getResponseData().getResults().size() + ", size : " + resultList.size());

          dataChange = true;
        }

        if (null != callback) {
          callback.onSuccess(dataChange);
        }
      }

      @Override
      public void failure(RetrofitError error) {
        Log.e(TAG, "Failed to search images : " + error);
        if (null != callback) {
          callback.onFailure();
        }
      }
    });
  }

  public ImageResult getImageResult(int i) {
    Log.d(TAG, "------getImageResult, current page : " + currentPage + ", page : " + i / PAGE_SIZE + ", i = " + i + ", resultList.size() = " + resultList.size());
    if(i < resultList.size() && i >= 0) {
      // pre-load
      if(toPreload(i)) {
        Log.d(TAG, "------preloading page " + i / PAGE_SIZE + ", i = " + i);
        searchNextPage(null);
        hasChange = true;
      }

      return resultList.get(i);
    } else {
      return null;
    }
  }

  public boolean hasChange() {
    return hasChange;
  }

  private boolean toPreload(int i) {
    int pagePosition = i % PAGE_SIZE;
    return pagePosition > PAGE_SIZE / 2 && resultList.size() < i + PAGE_SIZE;
  }

  public int getCurrentPage() {
    return currentPage;
  }

  public String getCurrentKeyword() {
    return currentKeyword;
  }

  public void setCurrentKeyword(String currentKeyword) {
    this.currentPage = 0;
    this.currentKeyword = currentKeyword;
  }

  public SearchFilter getSearchFilter() {
    return searchFilter;
  }

  public void setSearchFilter(SearchFilter searchFilter) {
    this.currentPage = 0;
    this.searchFilter = searchFilter;
  }

  public List<ImageResult> getResultList() {
    return resultList;
  }

  public static interface GoogleImageSearchApi {

    @GET("/images")
    void searchImage(@Query("q") String keyword, @Query("v") String version,
                     @Query("as_filetype") String fileType,
                     @Query("imgsz") String imageSize,
                     @Query("imgcolor") String imageColor,
                     @Query("as_sitesearch") String site,
                     @Query("rsz") int pageSize,
                     @Query("start") int startIndex,
                     Callback<SearchImageResult> cb);
  }

  public static interface AsyncResultCallback {
    void onSuccess(boolean dataChanged);
    void onFailure();
  }

}
