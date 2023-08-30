package uz.seppuku.vp.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import java.util.*

object Extensions {
    fun Activity.toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun Fragment.toast(msg: String) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }
    fun Fragment.getNavigationResult(key: String = "result") =
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(key)

    fun Fragment.setNavigationResult(result: String, key: String = "result") {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
    }
    fun Fragment.hideSoftKeyboard(
        view: View? = requireActivity().currentFocus
    ) {
        view?.let {
            val inputMethodManager =
                ContextCompat.getSystemService(requireActivity(), InputMethodManager::class.java)!!
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }

        view?.clearFocus()
    }

    fun View.hideKeyboard(): Boolean {
        return try {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
        } catch (ignored: RuntimeException) {
            false
        }
    }
    fun TextInputLayout.text(): String {
        return editText!!.text.toString().trim()
    }

    fun TextInputLayout.checkError() {
        editText!!.doOnTextChanged { text, _, _, _ ->
            if (text!!.isEmpty()) {
                isErrorEnabled = true
                error = "error"
            } else {
                isErrorEnabled = false
                error = null
            }
        }
    }

    fun EditText.getSearchText(): List<String> =
        text.toString().trim().lowercase(Locale.getDefault()).split(" ")



    fun View.click(click: () -> Unit) {
        setOnClickListener {
            click()
        }
    }

    fun View.hide() {
        visibility = View.INVISIBLE
    }

    fun View.show() {
        visibility = View.VISIBLE
    }

    fun View.gone() {
        visibility = View.GONE
    }
}