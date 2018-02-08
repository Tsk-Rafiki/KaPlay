package ru.rafiki.KaPlay.Screens.main_activity.fragments.PlayListFragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.fragment_playlist.view.*
import ru.rafiki.KaPlay.R
import ru.rafiki.KaPlay.services.kaudio_media_service.AudioRepository

/**
 * Created by denis.sakovich on 04.12.2017.
 */
class PlayListFragment : Fragment() {

    lateinit var recycler: RecyclerView
    lateinit var adapter: PlayListAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d("PlayListFragment", "OnCreateView")
        val rootView = inflater.inflate(R.layout.fragment_playlist, container, false)
        adapter = PlayListAdapter(AudioRepository.audioList)
        recycler = rootView.playListRecyclerView
        recycler.layoutManager = LinearLayoutManager(this.context, LinearLayout.VERTICAL, false)
        recycler.adapter = adapter
        adapter.notifyDataSetChanged()

        return rootView
    }

    companion object {
        fun newInstance(): PlayListFragment {
            return PlayListFragment()
        }
    }
}