/**
 * Copyright (c) 2012-2015 Magnet Systems. All rights reserved.
 */
package com.jim.imagesearch.model;

public class SearchFilter {
  private String size;
  private String type;
  private String color;
  private String site;

  public void reset() {
    size = null;
    type = null;
    color = null;
    site = null;
  }

  public String getSize() {
    return size;
  }

  public void setSize(String size) {
    this.size = size;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getSite() {
    return site;
  }

  public void setSite(String site) {
    this.site = site;
  }

  @Override
  public String toString() {
    return "size : " + size + ", type : " + type + ", color : " + color + ", site : " + site;
  }

  @Override
  public boolean equals(Object o) {
    SearchFilter theOther = (SearchFilter) o;
    return isStringEqual(size, theOther.getSize()) &&
            isStringEqual(type, theOther.getType()) &&
            isStringEqual(color, theOther.getColor()) &&
            isStringEqual(site, theOther.getSite());
  }

  private boolean isStringEqual(String s1, String s2) {
    if(null == s1) {
      return null == s2;
    } else {
      return s1.equals(s2);
    }
  }
}
