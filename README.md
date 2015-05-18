# ImageSearchAndroid
A image search Android App using google image search API

## Time spent : 16 hours

## User Storeis
Basic
* [x] User can enter a search query that will display a grid of image results from the Google Image API.
* [x] User can click on "settings" which allows selection of advanced search options to filter results
* [x] User can configure advanced search filters such as:
  * [x] Size (small, medium, large, extra-large)
  * [x] Color filter (black, blue, brown, gray, green, etc...)
  * [x] Type (faces, photo, clip art, line art)
  * [x] Site (espn.com)
  * [x] Subsequent searches will have any filters applied to the search results
* [x] User can tap on any image in results to see the image full-screen
* [x] User can scroll down “infinitely” to continue loading more image results (up to 8 pages)

Advanced
* [x] Advanced: Robust error handling, check if internet is available, handle error cases, network failures
* [x] Advanced: Use the ActionBar SearchView or custom layout as the query box instead of an EditText
* [x] Advanced: User can share an image to their friends or email it to themselves
* [x] Advanced: Replace Filter Settings Activity with a lightweight modal overlay
* [x] Advanced: Improve the user interface and experiment with image assets and/or styling and coloring
* [x] Bonus: Use the StaggeredGridView to display improve the grid of image results
* [x] Bonus: User can zoom or pan images displayed in full-screen detail view

Extra
* [x] Guesture (swipe) to navigate to previous/next image
* [x] Low resolution image as place holder before high resolution image is loaded 
* [x] Search keyword and filters are saved in SharedPreferences

##GIF walkthrough
[![IMAGE ALT TEXT HERE](http://img.youtube.com/vi/MFf8syyke_A/0.jpg)](http://www.youtube.com/watch?v=MFf8syyke_A)

##Credits
* R2M : https://github.com/magnetsystems/r2m-plugin-android
* Retrofit : https://github.com/square/retrofit
* picasso : https://github.com/square/picasso
* AndroidStaggeredGrid : https://github.com/f-barth/AndroidStaggeredGrid
