package com.orpington.software.rozkladmpk.presenter

import com.orpington.software.rozkladmpk.adapter.RowView

interface BindingPresenter {
    fun onBindTransportLineRowViewAtPosition(position: Int, rowView: RowView)
    fun getSize(): Int
    fun getItemViewType(position: Int): Int
}