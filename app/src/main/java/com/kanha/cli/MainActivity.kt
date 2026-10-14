package com.kanha.cli

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
        }
        root.addView(TextView(this).apply {
            text = "MovieBox Integration Starter"
            textSize = 22f
            gravity = Gravity.CENTER
        })
        root.addView(TextView(this).apply {
            text = "Kotlin + XML-ready Android scaffold. API integration is not configured yet."
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, 20, 0, 0)
        })
        setContentView(root)
    }
}
