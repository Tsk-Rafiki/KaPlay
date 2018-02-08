package ru.rafiki.KaPlay.Screens.main_activity.fragment_adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import ru.rafiki.KaPlay.Screens.main_activity.fragments.PlayListFragment.PlayListFragment
import ru.rafiki.KaPlay.Screens.main_activity.fragments.PlayerFragment.PlayerFragment
import ru.rafiki.KaPlay.Screens.main_activity.MainActivity
import ru.rafiki.KaPlay.Screens.main_activity.fragments.SettingsFragment.SettingsFragment

/**
 * Created by denis.sakovich on 04.12.2017.
 */
class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    fun setParametersToPlayerFragment(value: Int) {
        val bundle = Bundle()
        bundle.putInt("filesCount", value)
        getItem(MainActivity.FragmentType.player.ordinal).arguments = bundle
    }
    
    override fun getItem(position: Int): Fragment {
        return when(position) {
            MainActivity.FragmentType.player.ordinal -> PlayerFragment.newInstance()
            MainActivity.FragmentType.playlist.ordinal -> PlayListFragment.newInstance()
            else -> SettingsFragment.newInstance()
        }
    }

    override fun getCount(): Int {
        return MainActivity.FragmentType.values().size
    }
}
