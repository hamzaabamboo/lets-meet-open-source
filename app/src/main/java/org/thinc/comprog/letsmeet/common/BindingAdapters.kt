package org.thinc.comprog.letsmeet.common

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

@BindingAdapter("imageUrl")
fun setImageUrl(view: ImageView, imgURL: String?) {
    if (imgURL != "") Picasso.get().load(imgURL).into(view)
    else Picasso.get().load("https://www.shazam.com/resources/60c50135465fb5c062e8e325ce3dcfa99622d5f8/nocoverart.jpg").into(
        view
    )
}
