package com.example.androbank.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import com.example.androbank.R;
import com.example.androbank.databinding.FragmentNewPaymentBinding;

import java.time.LocalDate;


public class NewPayment extends Fragment {

    private FragmentNewPaymentBinding binding;
    private View root;
    private String account;
    private float amount;
    private boolean ownTransfer;
    private String receiver;
    private LocalDate duedate;
    private String option;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNewPaymentBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        duedate = LocalDate.now();
        binding.payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(account+" "+amount+" "+ownTransfer+" "+ receiver +" "+duedate+" "+option);
                //TODO: Payment processing
                Navigation.findNavController(root).navigate(R.id.action_newPayment_to_accounts);
            }
        });

        // Accounts spinner
        String[] accounts = {"Account 1", "Account 2", "Account 3"};
        ArrayAdapter<CharSequence> accountsadapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item, accounts);
        accountsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.accountDropdown.setAdapter(accountsadapter);
        binding.accountDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getSelectedItem().toString();
                account = selectedItem;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Options spinner
        String[] options = {"Only once", "Every week", "Every month"};
        ArrayAdapter<CharSequence> optionsadapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item, options);
        optionsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.recurringOptions.setAdapter(optionsadapter);
        binding.recurringOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getSelectedItem().toString();
                //System.out.println(selectedItem);
                option = selectedItem;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
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
                //System.out.println(amount);
            }
        });

        //Switch
        binding.ownTransferSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ownTransfer = b;
                if (b = true){
                    binding.receiverInput.setText(account);
                }
            }
        });

        return root;
    }
}
