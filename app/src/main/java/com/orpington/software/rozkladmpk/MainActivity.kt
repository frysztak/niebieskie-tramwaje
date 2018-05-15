package com.orpington.software.rozkladmpk

import android.view.View
import android.widget.SearchView
import android.widget.Toast


class MainActivity : MainActivityTemplate()
{
    override val itemClickListener: RecyclerViewClickListener? = object: RecyclerViewClickListener {
        override fun onClick(view: View, position: Int) {
            transportLinesPresenter.onItemClicked(position)
            Toast.makeText(applicationContext, "Position " + position, Toast.LENGTH_SHORT).show()
        }
    }

    override val searchViewTextListener: SearchView.OnQueryTextListener? = object: SearchView.OnQueryTextListener {
        override fun onQueryTextChange(newText: String?): Boolean {
            var result = transportLinesPresenter.onQueryTextChange(newText)
            transportLinesAdapter.notifyDataSetChanged()
            return result
        }

        override fun onQueryTextSubmit(newText: String?): Boolean {
            return true
        }
    }

    override fun loadData() {

    }
}
