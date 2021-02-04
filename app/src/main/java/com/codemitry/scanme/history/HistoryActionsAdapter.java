package com.codemitry.scanme.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codemitry.qr_code_generator_lib.qrcode.Barcode;
import com.codemitry.scanme.R;

import java.util.List;

public class HistoryActionsAdapter extends RecyclerView.Adapter<HistoryActionsAdapter.ActionHolder> {

    List<HistoryAction> actions;

    private OnHistoryActionClickListener onHistoryActionClickListener;

    public HistoryActionsAdapter(List<HistoryAction> actions) {
        this.actions = actions;
    }

    @NonNull
    @Override
    public ActionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_action, parent, false);
        return new ActionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActionHolder holder, int position) {
        HistoryAction action = actions.get(holder.getAdapterPosition());

        // Получение текста  и его установка для соответствующего action из HistoryAction
        holder.setAction(holder.action.getContext().getResources().getString(HistoryAction.Actions.Companion.getString(action.getAction())));

        holder.setBarcodeData(action.getBarcode().getDisplayValue());

        holder.itemView.setOnClickListener((View view) -> {
            if (onHistoryActionClickListener != null) {
                onHistoryActionClickListener.onHistoryActionClick(action.getAction(), action.getBarcode());
            }
        });
    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

    public static class ActionHolder extends RecyclerView.ViewHolder {
        private TextView action;
        private TextView barcodeData;

        public ActionHolder(@NonNull View itemView) {
            super(itemView);

            this.action = itemView.findViewById(R.id.action);
            this.barcodeData = itemView.findViewById(R.id.barcodeData);
        }

        public void setAction(String action) {
            this.action.setText(action);
        }

        public void setBarcodeData(String barcodeData) {
            this.barcodeData.setText(barcodeData);
        }
    }

    public void setOnHistoryActionClickListener(OnHistoryActionClickListener listener) {
        this.onHistoryActionClickListener = listener;
    }

    public interface OnHistoryActionClickListener {
        void onHistoryActionClick(HistoryAction.Actions action, Barcode barcode);
    }
}
