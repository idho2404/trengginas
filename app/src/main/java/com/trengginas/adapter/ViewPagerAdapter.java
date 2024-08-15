package com.trengginas.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.trengginas.fragment.DesaKelurahanFragment;
import com.trengginas.fragment.KecamatanFragment;
import com.trengginas.fragment.SLSFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new KecamatanFragment();
            case 1:
                return new DesaKelurahanFragment();
            case 2:
                return new SLSFragment();
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Total number of tabs
    }
}
