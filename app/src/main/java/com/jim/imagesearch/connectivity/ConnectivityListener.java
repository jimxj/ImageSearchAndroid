package com.jim.imagesearch.connectivity;

public interface ConnectivityListener {
  void onConnectivityStatusChanged(int lastKnowStatus, int newStatus);
}
