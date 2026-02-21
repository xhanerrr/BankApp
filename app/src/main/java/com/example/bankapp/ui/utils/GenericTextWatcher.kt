package com.example.bankapp.ui.utils

import android.text.Editable
import android.text.TextWatcher

class GenericTextWatcher(
    private val onChange: (String) -> Unit,
    private val onResetUI: () -> Unit
) : TextWatcher {

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        onResetUI()
        onChange(s.toString())
    }

    override fun afterTextChanged(s: Editable?) {}
}
