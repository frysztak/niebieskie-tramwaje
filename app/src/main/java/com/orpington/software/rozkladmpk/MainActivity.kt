package com.orpington.software.rozkladmpk

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.orpington.software.rozkladmpk.database.TransportLine
import kotlinx.android.synthetic.main.activity_main.*
import net.grandcentrix.thirtyinch.TiActivity

class MainActivity : TiActivity<MainPresenter, MainView>(), MainView {

    override fun providePresenter(): MainPresenter {
        val interactor = TransportLinesInteractorImpl(baseContext)
        return MainPresenter(interactor)
    }

    private lateinit var transportLinesAdapter: TransportLineListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gridView.adapter = GridViewKeyboardAdapter(this, resources.getStringArray(R.array.keyboardKeys))
        gridView.setOnItemClickListener { parent: AdapterView<*>, view: View, idx: Int, id: Long ->
            val item = parent.getItemAtPosition(idx)
            if (item is String) {
                presenter.userEnteredCharacter(item)
            }
        }
        transportLinesAdapter = TransportLineListAdapter(this)
        listView.adapter = transportLinesAdapter
    }

    override fun updateCurrentLines(lines: List<TransportLine>, enteredByUser: String) {
        transportLinesAdapter.updateItems(lines, enteredByUser)
    }

}
