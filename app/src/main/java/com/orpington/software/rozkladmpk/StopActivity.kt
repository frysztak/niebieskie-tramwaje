package com.orpington.software.rozkladmpk

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class StopActivity :  MainActivityTemplate() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchView?.visibility = View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return false //super.onOptionsItemSelected(item)
        }
    }

    override fun loadData() {
        val stopName = intent.getStringExtra("stopName")
        title = stopName
        transportLinesPresenter.loadRoutesForStop(stopName)
        transportLinesAdapter.notifyDataSetChanged()
    }
}

