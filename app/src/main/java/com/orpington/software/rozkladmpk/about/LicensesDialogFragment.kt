package com.orpington.software.rozkladmpk.about

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.webkit.WebView
import com.orpington.software.rozkladmpk.R


class LicensesDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_licenses, null) as WebView
        view.settings.loadWithOverviewMode = true
        view.settings.useWideViewPort = true
        view.loadUrl("file:///android_asset/open_source_licenses.html")

        return AlertDialog.Builder(activity!!, R.style.Theme_AppCompat_Light_Dialog_Alert)
            .setTitle(getString(R.string.oss_licenses))
            .setView(view)
            .setPositiveButton(android.R.string.ok, null)
            .create()
    }

    companion object {

        fun newInstance(): LicensesDialogFragment {
            return LicensesDialogFragment()
        }
    }

}

