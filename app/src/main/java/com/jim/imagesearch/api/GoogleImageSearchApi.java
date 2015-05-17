/**
 * Copyright (c) 2012-2015 Magnet Systems. All rights reserved.
 */
package com.jim.imagesearch.api;

import com.jim.imagesearch.model.SearchImageResult;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface GoogleImageSearchApi {
  int PAGE_SIZE = 8;

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
