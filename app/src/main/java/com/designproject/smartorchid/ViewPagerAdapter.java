package com.designproject.smartorchid;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.designproject.smartorchid.activity.HomeActivity;
import com.designproject.smartorchid.fragment.BooksFragment;
import com.designproject.smartorchid.fragment.CommunicationFragment;
import com.designproject.smartorchid.fragment.HomeFragment;
import com.designproject.smartorchid.fragment.NetworkFragment;
import com.designproject.smartorchid.fragment.StationariesFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull HomeActivity fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:return new BooksFragment();
            case 1: return new StationariesFragment();
            case 2: return new NetworkFragment();
            case 3: return new CommunicationFragment();
            default: return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
