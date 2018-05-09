package com.orpington.software.rozkladmpk

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import com.orpington.software.rozkladmpk.database.TransportLine
import kotlinx.android.synthetic.main.activity_main.*
import net.grandcentrix.thirtyinch.TiActivity


class MainActivity : TiActivity<MainPresenter, MainView>(), MainView {

    override fun providePresenter(): MainPresenter {
        return MainPresenter()
    }

    private lateinit var transportLinesAdapter: TransportLineListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var interactor = TransportLinesInteractorImpl(baseContext)
        var transportLinesPresenter = TransportLinesPresenter(interactor)
        val listener = object: RecyclerViewClickListener {
            override fun onClick(view: View, position: Int) {
                Toast.makeText(applicationContext, "Position " + position, Toast.LENGTH_SHORT).show()
            }
        }
        transportLinesAdapter = TransportLineListAdapter(this, transportLinesPresenter, listener)
        searchView?.queryHint = "Przystanek..."
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                var result = transportLinesPresenter.onQueryTextChange(newText)
                transportLinesAdapter.notifyDataSetChanged()
                return result
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = transportLinesAdapter
        val dividerItemDecoration = DividerItemDecoration(
                applicationContext,
                layoutManager.orientation
        )
        recyclerView.addItemDecoration(dividerItemDecoration)
    }

    override fun updateCurrentLines(lines: List<TransportLine>, enteredByUser: String) {
        //transportLinesAdapter.updateItems(lines, enteredByUser)
    }

}
