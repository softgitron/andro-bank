package com.example.androbank.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.androbank.R;
import com.example.androbank.databinding.FragmentPasscodeBinding;
import java.util.Random;


public class Passcode extends Fragment {

    private FragmentPasscodeBinding binding;
    private View root;
    private int passcode;
    private String code;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPasscodeBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        passcode = new Random().nextInt((999999 -  100000) + 1) + 100000;
        code = String.valueOf(passcode);
        System.out.println(passcode);
        binding.passcode.setText(code);


        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = binding.codeInput.getText().toString();

                if (userInput.equals(code)) {
                    binding.passcodeError.setVisibility(View.INVISIBLE);
                    Navigation.findNavController(root).navigate(R.id.action_passcode_to_main_Menu);
                } else {
                    binding.passcodeError.setVisibility(View.VISIBLE);
                }
            }
        });
        return root;
    }
}
