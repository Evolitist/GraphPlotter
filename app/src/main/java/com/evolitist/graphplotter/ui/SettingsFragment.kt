package com.evolitist.graphplotter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceGroupAdapter
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evolitist.graphplotter.R
import com.evolitist.graphplotter.getColorAttr

class SettingsFragment : Fragment() {
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = PreferenceManager(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val ctx = context ?: return null
        return FrameLayout(ctx).apply {
            setBackgroundColor(ctx.getColorAttr(R.attr.colorSurface))
            addView(RecyclerView(ctx).apply {
                layoutManager = LinearLayoutManager(ctx)
                adapter = PreferenceGroupAdapter(
                    preferenceManager.inflateFromResource(ctx, R.xml.preferences, null)
                )
            })
        }
    }
}