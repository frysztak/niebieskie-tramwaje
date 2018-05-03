package com.orpington.software.rozkladmpk

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import com.orpington.software.rozkladmpk.database.TransportLine
import kotlinx.android.synthetic.main.activity_main.*
import net.grandcentrix.thirtyinch.TiActivity

class MainActivity : TiActivity<MainPresenter, MainView>(), MainView {

    private lateinit var interactor: TransportLinesInteractor
    override fun providePresenter(): MainPresenter {
        interactor = TransportLinesInteractorImpl(baseContext)
        return MainPresenter(interactor)
    }

    private lateinit var transportLinesAdapter: TransportLineListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchView?.queryHint = "Przystanek..."
        searchView?.setOnQueryTextListener(object: android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                transportLinesAdapter.updateItems(newText!!)
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

        recyclerView.layoutManager = LinearLayoutManager(this)
        transportLinesAdapter = TransportLineListAdapter(this, interactor)
        recyclerView.adapter = transportLinesAdapter
    }

    override fun updateCurrentLines(lines: List<TransportLine>, enteredByUser: String) {
        //transportLinesAdapter.updateItems(lines, enteredByUser)
    }

}
