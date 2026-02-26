package com.example.finance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactions;

    public TransactionAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction t = transactions.get(position);
        holder.title.setText(t.getTitle());
        holder.date.setText(t.getDate());
        holder.amount.setText(t.getFormattedAmount());
        holder.icon.setImageResource(t.getIconResource());
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, amount;
        ImageView icon;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.transactionTitle);
            date = itemView.findViewById(R.id.transactionDate);
            amount = itemView.findViewById(R.id.transactionAmount);
            icon = itemView.findViewById(R.id.transactionIcon);

        }
    }
}
