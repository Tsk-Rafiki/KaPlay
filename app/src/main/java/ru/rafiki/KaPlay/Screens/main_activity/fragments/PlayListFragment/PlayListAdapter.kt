package ru.rafiki.KaPlay.Screens.main_activity.fragments.PlayListFragment

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.playlist_recycler_item.view.*
import ru.rafiki.KaPlay.R
import ru.rafiki.KaPlay.network.model.Audio

/**
 * Created by denis.sakovich on 15.12.2017.
 */
class PlayListAdapter(outData: ArrayList<Audio>): RecyclerView.Adapter<PlayListAdapter.ViewHolder>() {

    var items: ArrayList<Audio>

    init {
        items = outData
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.playlist_recycler_item, parent, false)
        return ViewHolder(v)
    }

    fun setData(data: ArrayList<Audio>) {
        val localItem: ArrayList<Audio> = ArrayList()
        for (d in data) {
            localItem.add(Audio(d.data, d.title, d.album, d.artist, d.length))
        }
        items = localItem
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Audio) = with(itemView) {
            artistTextView.text = item.artist
            titleTextView.text = item.title
            timeTextView.text = item.length
        }
    }
}