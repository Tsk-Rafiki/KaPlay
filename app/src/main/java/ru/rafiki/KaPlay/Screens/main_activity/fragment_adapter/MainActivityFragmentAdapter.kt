package ru.rafiki.KaPlay.Screens.main_activity.fragment_adapter

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

    override fun getItem(position: Int): Fragment {
        return when(position) {
            MainActivity.fragmentType.player.ordinal -> PlayerFragment.newInstance()
            MainActivity.fragmentType.playlist.ordinal -> PlayListFragment.newInstance()
            else -> SettingsFragment.newInstance()
        }
    }

    override fun getCount(): Int {
        return MainActivity.fragmentType.values().size
    }
}
