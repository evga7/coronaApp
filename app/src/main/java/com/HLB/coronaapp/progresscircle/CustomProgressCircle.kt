package com.HLB.coronaapp.progresscircle

import android.app.Dialog
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import androidx.annotation.NonNull
import androidx.core.content.res.ResourcesCompat
import com.example.coronaapp.R
import kotlinx.android.synthetic.main.progress_circle.view.*

class CustomProgressCircle {

    lateinit var dialog: Dialog

    fun show(context: Context): Dialog {
        val inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflator.inflate(R.layout.progress_circle, null)

        view.cicle_view.setBackgroundColor(Color.parseColor("#60000000"))
        view.card_view.setCardBackgroundColor(Color.parseColor("#70000000"))

        setColorFilter(view.prg_circle.indeterminateDrawable,
            ResourcesCompat.getColor(context.resources, R.color.circleColor, null))

        dialog = Dialog(context, R.style.CustomProgressCircleTheme)
        dialog.setContentView(view)
        dialog.show()

        return dialog
    }

    fun setColorFilter(@NonNull drawable: Drawable, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            @Suppress("DEPRECATION")
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

}