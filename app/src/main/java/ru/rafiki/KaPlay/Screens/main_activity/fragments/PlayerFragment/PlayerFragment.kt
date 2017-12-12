package ru.rafiki.KaPlay.Screens.main_activity.fragments.PlayerFragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_player.view.*
import ru.rafiki.KaPlay.network.LastFmApi
import ru.rafiki.KaPlay.R

/**
 * Created by denis.sakovich on 04.12.2017.
 */
class PlayerFragment : Fragment() {

    var api_key: String = "2aa7df3efc89883966893b7e54131845"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d("PlayerFragment", "OnCreateView")
        val rootView = inflater.inflate(R.layout.fragment_player, container, false)
        val lastFmApi = LastFmApi.create();
        lastFmApi.getTrackInfo("track.getInfo", api_key, "cher", "believe")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    result -> Log.d("myLog", result.toString())
                    rootView.section_label.text = result.track?.wiki?.content
                    }
                    , {
                        error -> error.printStackTrace()
                    })

        return rootView
    }

    companion object {
        fun newInstance(): PlayerFragment {
            return PlayerFragment()
        }
    }
}