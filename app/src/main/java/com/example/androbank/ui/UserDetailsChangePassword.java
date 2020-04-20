package com.example.androbank.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import com.example.androbank.databinding.FragmentUserDetailsChangePasswordBinding;
import com.example.androbank.session.Session;

public class UserDetailsChangePassword extends Fragment {

    private FragmentUserDetailsChangePasswordBinding binding;
    private View root;
    private Session session = Session.getSession();
    private boolean passwordsMatch = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserDetailsChangePasswordBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        return root;
    }

}


