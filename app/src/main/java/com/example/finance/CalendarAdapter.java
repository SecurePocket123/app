package com.example.finance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    public interface OnDayClickListener {
        void onDayClick(CalendarDay day);
    }

    private final List<CalendarDay> days;
    private final Calendar selectedDate;
    private final Calendar today;
    private final OnDayClickListener listener;

    public CalendarAdapter(List<CalendarDay> days, Calendar selectedDate, Calendar today, OnDayClickListener listener) {
        this.days = days;
        this.selectedDate = selectedDate;
        this.today = today;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_day, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        CalendarDay day = days.get(position);

        if (day.getDate() == null) {
            holder.tvDay.setText("");
            holder.itemView.setBackground(null);
            holder.tvDay.setTextColor(0x00FFFFFF);
            holder.dotIndicator.setVisibility(View.INVISIBLE);
            holder.itemView.setOnClickListener(null);
            return;
        }

        int dayNumber = day.getDate().get(Calendar.DAY_OF_MONTH);
        holder.tvDay.setText(String.valueOf(dayNumber));

        if (isSameDay(day.getDate(), selectedDate)) {
            holder.itemView.setBackgroundResource(R.drawable.bg_calendar_selected);
            holder.tvDay.setTextColor(0xFF155FA0);
        } else if (isSameDay(day.getDate(), today)) {
            holder.itemView.setBackgroundResource(R.drawable.bg_calendar_today);
            holder.tvDay.setTextColor(0xFFFFFFFF);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_calendar_day);
            holder.tvDay.setTextColor(0xFFFFFFFF);
        }

        holder.dotIndicator.setVisibility(day.hasTransactions() ? View.VISIBLE : View.INVISIBLE);
        holder.itemView.setOnClickListener(v -> listener.onDayClick(day));
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;
        View dotIndicator;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            dotIndicator = itemView.findViewById(R.id.viewTransactionDot);
        }
    }

    private boolean isSameDay(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }
}