package com.example.androbank.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.androbank.R;
import com.example.androbank.RecyclerAdapter;
import com.example.androbank.databinding.FragmentAccountsBinding;
import com.example.androbank.session.Account;
import com.example.androbank.session.Session;

import java.util.ArrayList;

public class Accounts extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private FragmentAccountsBinding binding;
    private View root;
    private Session session = Session.getSession();
    private Context context = getContext();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountsBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        //find recycler view, set up layout manager
        recyclerView = binding.accountsListView;
        mLayoutManager = new LinearLayoutManager(getActivity());
        populateAccountList();


        //Buttons
        binding.viewCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(root).navigate(R.id.action_accounts_to_cards);
            }
        });

        binding.AccountsNewCardPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_accounts_to_newCardPayment2);
            }
        });

        binding.newTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_accounts_to_newPayment);
            }
        });

        binding.createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.accounts.createAccount().observe(getViewLifecycleOwner(), new Observer<Account>() {
                    @Override
                    public void onChanged(Account account) {
                        System.out.println("Account created");
                        populateAccountList();
                    }
                });
            }
        });
        return root;
    }

    private void populateAccountList() {
        session.accounts.getAccountsList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Account>>() {
            @Override
            public void onChanged(ArrayList<Account> accounts) {
                System.out.println("Arraylist retrieved");
                ArrayList<RecyclerViewObject> itemList = new ArrayList<>();
                for (int i = 0; i < accounts.size(); i++){
                    String balance = String.format("%.2f",  (float) accounts.get(i).getBalance() / (float) 100 );
                    String a = accounts.get(i).getIban() + " - " + balance + "€";
                    itemList.add(new RecyclerViewObject(R.drawable.ic_forward, a));
                }
                mAdapter = new RecyclerAdapter(itemList, Accounts.this);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setHasFixedSize(true); //lista ei kasva kesken suorituksen (tehostaa suoritusta?)
                recyclerView.setAdapter(mAdapter);
            }
        });
    }
    public void addMoney(String iban) {
        Bundle bundle = new Bundle();
        bundle.putString("accountData", iban);
        Navigation.findNavController(root).navigate(R.id.AccountsAddMoney, bundle);
    }

}
