package com.codemitry.scanme.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codemitry.scanme.R;
import com.codemitry.scanme.barcode.Barcode;
import com.codemitry.scanme.history.HistoryAction;
import com.codemitry.scanme.history.HistoryActionsAdapter;
import com.codemitry.scanme.history.HistoryActionsManager;
import com.codemitry.scanme.ui.scan.BarcodeResultFragment;

import java.util.List;

public class HistoryFragment extends Fragment {


    private RecyclerView recycler;

    private HistoryActionsAdapter adapter;
    private HistoryActionsManager historyActionsManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recycler = view.findViewById(R.id.historyList);
        LinearLayoutManager lim = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(lim);

        view.findViewById(R.id.back).setOnClickListener((View v) -> {
            if (requireActivity() instanceof MainActivity)
                ((MainActivity) requireActivity()).onBackPressed();
        });

        historyActionsManager = new ViewModelProvider(requireActivity()).get(HistoryActionsManager.class);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new HistoryActionsAdapter((List<HistoryAction>) historyActionsManager.getHistoryActions());
        adapter.setOnHistoryActionClickListener((HistoryAction.Actions action, Barcode barcode) -> {
            if (action == HistoryAction.Actions.SCAN) {
                startBarcodeResultFragment(barcode);
            }

        });
        recycler.setAdapter(adapter);
    }

    private void startBarcodeResultFragment(Barcode barcode) {
        BarcodeResultFragment barcodeResultFragment = new BarcodeResultFragment(barcode);

        barcodeResultFragment.show(requireFragmentManager(), barcodeResultFragment.getClass().getSimpleName());
    }

}