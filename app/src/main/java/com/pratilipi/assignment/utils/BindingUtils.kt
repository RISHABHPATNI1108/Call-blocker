package com.pratilipi.assignment.utils

import android.net.Uri
import android.text.TextUtils
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.pratilipi.assignment.R
import java.text.SimpleDateFormat
import java.util.*

object BindingUtils {

    /**
     * Binding adapters are used to bind some properties with the values,
     * the properties here might be not custom properties,
     * but still need to bind them as currently direct binding of these properties is not available
     */
    @JvmStatic
    @BindingAdapter("phoneNumbers")
    fun setDrawableRight(textView: AppCompatTextView, list: ArrayList<String?>) {
        val builder = StringBuilder()
        for (details in list) {
            builder.append(details).append("\n")
        }
        textView.text = builder.toString().trim { it <= ' ' }
    }

    @JvmStatic
    @BindingAdapter("photoUri")
    fun setDrawableRight(imageView: AppCompatImageView, uri: String?) {
        if (!TextUtils.isEmpty(uri)) {
            val uri1 = Uri.parse(uri)
            Glide.with(imageView.context).load(uri1).error(R.drawable.ic_person).into(imageView)
        } else {
            imageView.setImageResource(R.drawable.ic_person)
        }
    }

    @JvmStatic
    @BindingAdapter("date")
    fun setDate(textView: TextView, date: Date?) {
        val output = SimpleDateFormat("MMM dd yyyy - hh:mm a", Locale.getDefault())
        output.timeZone = TimeZone.getDefault()
        val date2: String
        date2 = if (date != null) {
            output.format(date)
        } else {
            ""
        }
        textView.text = date2
    }
}