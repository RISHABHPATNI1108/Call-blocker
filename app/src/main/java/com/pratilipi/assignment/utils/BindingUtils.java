package com.pratilipi.assignment.utils;

import android.net.Uri;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.pratilipi.assignment.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class BindingUtils {

  /**
   * Binding adapters are used to bind some properties with the values,
   * the properties here might be not custom properties,
   * but still need to bind them as currently direct binding of these properties is not available
   **/
  @BindingAdapter({"phoneNumbers"})
  public static void setDrawableRight(AppCompatTextView textView, ArrayList<String> list) {
    StringBuilder builder = new StringBuilder();
    for (String details : list) {
      builder.append(details).append("\n");
    }


    textView.setText(builder.toString().trim());
  }

  @BindingAdapter({"photoUri"})
  public static void setDrawableRight(AppCompatImageView imageView, String uri) {

    if (!TextUtils.isEmpty(uri)) {
      Uri uri1 = Uri.parse(uri);

      Glide.with(imageView.getContext()).load(uri1).error(R.drawable.ic_person).into(imageView);
    } else {
      imageView.setImageResource(R.drawable.ic_person);
    }
  }

  @BindingAdapter({"date"})
  public static void setDate(TextView textView, Date date) {
    SimpleDateFormat output = new SimpleDateFormat("MMM dd yyyy - hh:mm a", Locale.getDefault());
    output.setTimeZone(TimeZone.getDefault());
    String date2;
    if (date != null) {
      date2 = output.format(date);
    } else {
      date2 = "";
    }

    textView.setText(date2);

  }

}
