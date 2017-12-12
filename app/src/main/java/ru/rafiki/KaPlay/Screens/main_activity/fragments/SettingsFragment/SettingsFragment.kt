package ru.rafiki.KaPlay.Screens.main_activity.fragments.SettingsFragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_player.view.*
import ru.rafiki.KaPlay.R

/**
 * Created by denis.sakovich on 04.12.2017.
 */
class SettingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d("SettingsFragment", "OnCreateView")
        val rootView = inflater.inflate(R.layout.fragment_player, container, false)
        rootView.section_label.text = getString(R.string.section_format, " settings")
        return rootView
    }

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}