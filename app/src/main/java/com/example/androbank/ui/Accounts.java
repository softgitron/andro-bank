package com.example.androbank.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
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
        //inflate fragment

        //View rootView = inflater.inflate(R.layout.fragment_accounts, container, false);

        //find recycler view, set up layout manager
        recyclerView = binding.accountsListView;
        //recyclerView = (RecyclerView) rootView.findViewById(R.id.accountsListView);

        recyclerView.setHasFixedSize(true); //lista ei kasva kesken suorituksen (tehostaa suoritusta?)
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        /*ArrayList<RecyclerViewObject> itemList = new ArrayList<>();
        itemList.add(new RecyclerViewObject(R.drawable.ic_forward, "Account 1 - 100 €"));
        itemList.add(new RecyclerViewObject(R.drawable.ic_forward, "Account 2 - 1440 €"));
        itemList.add(new RecyclerViewObject(R.drawable.ic_forward, "Account 3 - 30 €"));
        itemList.add(new RecyclerViewObject(R.drawable.ic_forward, "Account 4 - 100 €"));
        itemList.add(new RecyclerViewObject(R.drawable.ic_forward, "Account 5 - 1440 €"));
        itemList.add(new RecyclerViewObject(R.drawable.ic_forward, "Account 6 - 30 €"));
        itemList.add(new RecyclerViewObject(R.drawable.ic_forward, "Account 7 - 100 €"));

        //get adapter
        mAdapter = new RecyclerAdapter(itemList);
        recyclerView.setAdapter(mAdapter);*/


        populateAccountList();

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        //Buttons
        binding.viewCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(root).navigate(R.id.action_accounts_to_cards);
            }
        });

        binding.newCardPayment.setOnClickListener(new View.OnClickListener() {
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


        return root;
    }

    private void populateAccountList() {
        session.accounts.getAccountsList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Account>>() {
            @Override
            public void onChanged(ArrayList<Account> accounts) {
                ArrayList<RecyclerViewObject> itemList = new ArrayList<>();
                for (int i = 0; i < accounts.size(); i++){
                    String balance = String.format("%.2f",  (float) accounts.get(i).getBalance() / 100 );
                    String a = accounts.get(i).getIban() + " - " + balance + "€";
                    itemList.add(new RecyclerViewObject(R.drawable.ic_forward, a));
                }
                mAdapter = new RecyclerAdapter(itemList, Accounts.this);
                recyclerView.setAdapter(mAdapter);
            }
        });
    }
    public void addMoney(String iban) {
        Bundle bundle = new Bundle();
        bundle.putString("accountData", iban);
        Navigation.findNavController(root).navigate(R.id.accountAddMoney, bundle);
    }

}
