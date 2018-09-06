package software.orpington.rozkladmpk.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

// https://issuetracker.google.com/issues/37043700#comment29
class InputMethodManagerLeakFix {
    companion object {
        fun fixInputMethod(context: Context?) {
            if (context == null) {
                return
            }
            var inputMethodManager: InputMethodManager? = null
            try {
                inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            } catch (th: Throwable) {
                th.printStackTrace()
            }
            if (inputMethodManager == null) {
                return
            }
            val declaredFields = inputMethodManager.javaClass.declaredFields
            for (declaredField in declaredFields) {
                try {
                    if (!declaredField.isAccessible) {
                        declaredField.isAccessible = true
                    }
                    val obj = declaredField.get(inputMethodManager)
                    if (obj == null || obj !is View) {
                        continue
                    }
                    val view: View = obj as View
                    if (view.context === context) {
                        declaredField.set(inputMethodManager, null)
                    } else {
                        return
                    }
                } catch (th: Throwable) {
                    th.printStackTrace()
                }
            }
        }
    }
}