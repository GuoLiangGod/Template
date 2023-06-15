package com.example.template

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.TextureView
import android.view.WindowManager
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private lateinit var mCamera2Helper: Camera2Helper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        findViewById<Button>(R.id.camera_change).setOnClickListener {
            mCamera2Helper.exchangeCamera()
        }
        mCamera2Helper = Camera2Helper(this, findViewById<TextureView>(R.id.textureView),findViewById<TextureView>(R.id.textureView2))
    }
    override fun onDestroy() {
        super.onDestroy()
        mCamera2Helper.releaseCamera()
        mCamera2Helper.releaseThread()
    }
}