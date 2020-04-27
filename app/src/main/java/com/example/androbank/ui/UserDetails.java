package com.example.androbank.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.androbank.R;
import com.example.androbank.databinding.FragmentUserDetailsBinding;
import com.example.androbank.session.Session;

public class UserDetails extends Fragment {

    private FragmentUserDetailsBinding binding;
    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserDetailsBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        binding.changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.userDetailsChangePassword);
            }
        });
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Session.getSession().sessionDestroy(getContext());
                Navigation.findNavController(root).navigate(R.id.action_userDetails_to_nav_home);
            }
        });
        binding.updateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate((R.id.action_userDetails_to_updateContact));
            }
        });

        return root;
    }
}
