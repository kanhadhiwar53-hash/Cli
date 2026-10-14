package com.kanha.cli

import android.app.Activity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchInput = findViewById<EditText>(R.id.searchInput)
        val searchButton = findViewById<Button>(R.id.searchButton)
        val statusText = findViewById<TextView>(R.id.statusText)

        searchButton.setOnClickListener {
            val query = searchInput.text.toString().trim()
            if (query.isEmpty()) {
                searchInput.error = "Movie ya series ka naam likhein"
                return@setOnClickListener
            }

            (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
                ?.hideSoftInputFromWindow(searchInput.windowToken, 0)

            statusText.text = "Search query ready: \"$query\"\n\nMetadata provider abhi connect nahi hai. Agla step: authorized API endpoint aur API key configure karna."
            Toast.makeText(this, "Search UI working", Toast.LENGTH_SHORT).show()
        }
    }
}
