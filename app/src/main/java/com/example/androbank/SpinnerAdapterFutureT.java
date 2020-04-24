package com.example.androbank;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.androbank.session.FutureTransaction;

import java.util.ArrayList;
/*****************************************************************
// SOURCE: https://www.edureka.co/blog/custom-spinner-in-android
      AND: https://stackoverflow.com/questions/16694786/how-to-customize-a-spinner-in-android
 ****************************************************************/

public class SpinnerAdapterFutureT extends ArrayAdapter {
    private Context context;
    private ArrayList<FutureTransaction> transactions;

    public SpinnerAdapterFutureT(Context context, int textViewResourceId, ArrayList<FutureTransaction> transactions) {
        super(context, textViewResourceId, transactions);
        this.context = context;
        this.transactions = transactions;
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        // Inflating the layout for the custom Spinner
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.spinner_custom_future_transactions, parent, false);

        // Declaring and Typecasting the textview in the inflated layout
        TextView transactionText = (TextView) layout.findViewById(R.id.future_transaction_text);

        // Setting the text using the array
        transactionText.setText(transactions.get(position).toString() );



/*// Setting Special atrributes for 1st element
        if (position == 0) {
// Removing the image view
            img.setVisibility(View.GONE);
// Setting the size of the text
            tvLanguage.setTextSize(20f);
// Setting the text Color
            tvLanguage.setTextColor(Color.BLACK);

        }*/

        return layout;
    }

    // It gets a View that displays in the drop down popup the data at the specified position
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // It gets a View that displays the data at the specified position
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
}
