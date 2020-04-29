package com.example.androbank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androbank.ui.Accounts;
import com.example.androbank.ui.AccountsCards;
import com.example.androbank.ui.RecyclerViewObject;

import java.util.ArrayList;



public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
    private ArrayList<RecyclerViewObject> adapterList;
    private Fragment fragmentInstance;


    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;

        public RecyclerViewHolder (@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cardImage);
            textView = itemView.findViewById(R.id.cardText);

            // on item click StackOverFlow: https://stackoverflow.com/a/39707729
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    // get position
                    int pos = getAdapterPosition();

                    // check if item still exists
                    if(pos != RecyclerView.NO_POSITION){
                        RecyclerViewObject clickedDataItem = adapterList.get(pos);

                        if (fragmentInstance instanceof Accounts) {
                            ((Accounts)fragmentInstance).addMoney(clickedDataItem.getCardText());
                        } else if (fragmentInstance instanceof AccountsCards) {
                            ((AccountsCards)fragmentInstance).selectCard(clickedDataItem.getCardText());
                        }
                    }
                }
            });
        }
    }

    /** Constructor
     * @param adapterList List containing the data for the recycler view
     * @param fragmentInstance Instance of Accounts or Cards. Needed for accessing their functions.
     */
    public RecyclerAdapter(ArrayList<RecyclerViewObject> adapterList, Fragment fragmentInstance) {
        this.adapterList = adapterList;
        this.fragmentInstance = fragmentInstance;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view,null);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        RecyclerViewObject currentItem = adapterList.get(position);
        holder.imageView.setImageResource(currentItem.getImageResource());
        holder.textView.setText(currentItem.getCardText());
    }

    @Override
    public int getItemCount() {
        return adapterList.size();
    }


}
