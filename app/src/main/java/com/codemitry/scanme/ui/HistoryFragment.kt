package com.codemitry.scanme.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codemitry.scanme.R
import com.codemitry.scanme.history.HistoryAction
import com.codemitry.scanme.history.HistoryActionsAdapter
import com.codemitry.scanme.history.HistoryActionsManager
import com.codemitry.scanme.ui.scan.BarcodeResultFragment

class HistoryFragment : Fragment() {
    private var recycler: RecyclerView? = null

    private var adapter: HistoryActionsAdapter? = null
    private var historyActionsManager: HistoryActionsManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        historyActionsManager = ViewModelProvider(requireActivity()).get(HistoryActionsManager::class.java)

        recycler = view.findViewById(R.id.historyList)

        initRecycler()

        view.findViewById<ImageButton>(R.id.back).setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun initRecycler() {
        val lim = LinearLayoutManager(context)
        recycler?.layoutManager = lim

        adapter = HistoryActionsAdapter(historyActionsManager?.historyActions as MutableList<HistoryAction>?)
        adapter?.setOnHistoryActionClickListener { action, barcode ->
            when (action) {
                HistoryAction.Actions.SCAN -> startBarcodeResultFragment(barcode)
                HistoryAction.Actions.CREATE -> TODO()
            }
        }
        recycler?.adapter = adapter
    }

    private fun startBarcodeResultFragment(barcode: com.codemitry.scanme.barcode.Barcode) {
        val barcodeResultFragment = BarcodeResultFragment(barcode)

        barcodeResultFragment.show(parentFragmentManager, BarcodeResultFragment::class.simpleName)
    }
}