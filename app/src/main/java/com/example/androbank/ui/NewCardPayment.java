package com.example.androbank.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.androbank.R;
import com.example.androbank.databinding.FragmentNewCardPaymentBinding;


public class NewCardPayment extends Fragment {

    private FragmentNewCardPaymentBinding binding;
    private View root;
    private String card;
    private float amount;
    private String receiver;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNewCardPaymentBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        binding.payNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: Payment processing
                Navigation.findNavController(root).navigate(R.id.action_newCardPayment2_to_accounts);
            }
        });

        String[] cards = {"Card 1", "Card 2", "Card 3"};
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item, cards);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.cardDropdown.setAdapter(adapter);
        binding.cardDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getSelectedItem().toString();
                card = selectedItem;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        // Amount
        binding.amountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                amount = Float.parseFloat(editable.toString());
                //System.out.println(amount);
            }
        });

        // Receiver
        binding.receiverInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                receiver = editable.toString();
                //System.out.println(receiver);
            }
        });

        return root;
    }
}
