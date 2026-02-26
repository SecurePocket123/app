package com.example.finance;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView transactionsRecyclerView;
    private TransactionAdapter adapter;
    private List<Transaction> transactionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        transactionsRecyclerView = findViewById(R.id.transactionsRecyclerView);
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        createSampleTransactions();

        adapter = new TransactionAdapter(transactionList);
        transactionsRecyclerView.setAdapter(adapter);

        setupClickListeners();
    }

    private void createSampleTransactions() {
        transactionList = new ArrayList<>();
        String today = new SimpleDateFormat("dd. MMM yyyy", Locale.GERMAN).format(new Date());

        transactionList.add(new Transaction("Gehalt", "01. Feb 2026", 500.00, true, android.R.drawable.ic_menu_upload));
        transactionList.add(new Transaction("Einkauf Supermarkt", "03. Feb 2026", 85.50, false, android.R.drawable.ic_menu_gallery));
        transactionList.add(new Transaction("Netflix Abo", "05. Feb 2026", 12.99, false, android.R.drawable.ic_menu_slideshow));
        transactionList.add(new Transaction("Freelance Projekt", today, 450.00, true, android.R.drawable.ic_menu_edit));
        transactionList.add(new Transaction("Tankstelle", today, 65.00, false, android.R.drawable.ic_menu_directions));
    }

    private void setupClickListeners() {
        findViewById(R.id.btnAddIncome).setOnClickListener(v -> Toast.makeText(this, "Einnahme hinzufügen", Toast.LENGTH_SHORT).show());
        findViewById(R.id.btnAddExpense).setOnClickListener(v -> Toast.makeText(this, "Ausgabe hinzufügen", Toast.LENGTH_SHORT).show());
        FloatingActionButton fab = findViewById(R.id.fabAddTransaction);
        fab.setOnClickListener(v -> Toast.makeText(this, "Neue Transaktion", Toast.LENGTH_SHORT).show());
        findViewById(R.id.viewAllTransactions).setOnClickListener(v -> Toast.makeText(this, "Alle Transaktionen anzeigen", Toast.LENGTH_SHORT).show());
    }
}
