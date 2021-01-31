package com.codemitry.scanme.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codemitry.qr_code_generator_lib.qrcode.Barcode
import com.codemitry.scanme.BarcodeDataAdapter.Companion.tableToBitmap
import com.codemitry.scanme.R
import com.codemitry.scanme.history.HistoryAction
import com.codemitry.scanme.history.HistoryActionsAdapter
import com.codemitry.scanme.history.HistoryActionsManager
import com.codemitry.scanme.ui.create.GeneratedQRCodeFragment
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
                HistoryAction.Actions.SCAN -> showBarcodeScannedFragment(barcode)
                HistoryAction.Actions.CREATE -> showBarcodeCreatedFragment(barcode)
            }
        }
        recycler?.adapter = adapter
    }

    private fun showBarcodeScannedFragment(qrCode: Barcode) {
        val barcodeResultFragment = BarcodeResultFragment(qrCode)

        barcodeResultFragment.show(parentFragmentManager, BarcodeResultFragment::class.simpleName)
    }

    private fun showBarcodeCreatedFragment(qrCode: Barcode) {
        val qrCodeBitmap = Bitmap.createScaledBitmap(tableToBitmap(qrCode.getCode()), 512, 512, false)

        GeneratedQRCodeFragment(qrCodeBitmap).show(parentFragmentManager, GeneratedQRCodeFragment::class.simpleName)
    }
}