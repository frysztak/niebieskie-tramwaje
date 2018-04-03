package com.orpington.software.rozkladmpk

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_main.*
import net.grandcentrix.thirtyinch.TiActivity

class MainActivity : TiActivity<MainPresenter, MainView>(), MainView {
    override fun providePresenter(): MainPresenter {
        val interactor = TransportLinesInteractorImpl(baseContext)
        return MainPresenter(interactor)
    }

    override fun updateNameEnteredByUser(name: String) {
        textView2.text = name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val booksAdapter = GridViewKeyboardAdapter(this, presenter.alphabet)
        gridView.adapter = booksAdapter
        gridView.setOnItemClickListener { parent: AdapterView<*>, view: View, idx: Int, id: Long ->
            val item = presenter.alphabet[idx]
            presenter.appendToCurrentSelection(item)
        }
    }
}
