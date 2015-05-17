/**
 * Copyright (c) 2012-2015 Magnet Systems. All rights reserved.
 */
package com.jim.imagesearch.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jim.imagesearch.R;
import com.jim.imagesearch.model.ImageResult;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageGridArrayAdapter extends ArrayAdapter<ImageResult> {

  // View lookup cache
  private static class ViewHolder {
    TextView caption;
    //SimpleDraweeView image;
    ImageView image;
  }


  public ImageGridArrayAdapter(Context context, List<ImageResult> objects) {
    super(context, R.layout.grid_item_image, objects);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final ImageResult photo = getItem(position);

    final ViewHolder viewHolder = null == convertView ? new ViewHolder() : (ViewHolder) convertView.getTag();
    if(null == convertView) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_image, parent, false);

      //viewHolder.caption = (TextView) convertView.findViewById(R.id.tvCaption);
      viewHolder.image = (ImageView) convertView.findViewById(R.id.ivImage);

      convertView.setTag(viewHolder);
    }

    //viewHolder.caption.setText(null != photo.getTitle() ? Html.fromHtml(photo.getTitle()) : "");
    //viewHolder.image.setImageURI(Uri.parse(photo.getUrl()));
    //Fresco.initialize(getContext());
    Picasso.with(getContext()).load(Uri.parse(photo.getTbUrl())).placeholder(R.drawable.image_placeholder).into(viewHolder.image);

    return convertView;
  }
}
