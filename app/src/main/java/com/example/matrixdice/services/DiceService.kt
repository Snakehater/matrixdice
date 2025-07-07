package com.example.matrixdice.services

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.os.*
import com.nothing.ketchum.Glyph
import com.nothing.ketchum.GlyphMatrixFrame
import com.nothing.ketchum.GlyphMatrixManager
import com.nothing.ketchum.GlyphMatrixObject
import com.nothing.ketchum.GlyphToy

class DiceService : Service() {

    private var glyphMatrixManager: GlyphMatrixManager? = null

    override fun onBind(intent: Intent?): IBinder? {
        init()
        return serviceMessenger.binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        glyphMatrixManager?.unInit()
        glyphMatrixManager = null
        return false
    }

    private fun init() {
        glyphMatrixManager = GlyphMatrixManager.getInstance(this)

        glyphMatrixManager?.init(object : GlyphMatrixManager.Callback {
            override fun onServiceConnected(p0: ComponentName?) {
                glyphMatrixManager?.register(Glyph.DEVICE_23112)
                displayNumber(1)
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                TODO("Not yet implemented")
            }
        })
    }

    private val serviceHandler = Handler(Looper.getMainLooper()) {
        when (it.what) {
            GlyphToy.MSG_GLYPH_TOY -> {
                val data = it.data.getString(GlyphToy.MSG_GLYPH_TOY_DATA)
                if (data == GlyphToy.EVENT_CHANGE) {
                    val random = (1..20).random()
                    displayNumber(random)
                }
            }
        }
        true
    }

    private val serviceMessenger = Messenger(serviceHandler)

    private fun displayNumber(number: Int) {
        val text = number.toString()

        val objectBuilder = GlyphMatrixObject.Builder()
            .setText(text)
            .setPosition(4, 4)
            .setScale(100)
            .setBrightness(255)

        val glyphObject = objectBuilder.build()

        val frameBuilder = GlyphMatrixFrame.Builder()
            .addTop(glyphObject)

        val frame = frameBuilder.build(this)

        glyphMatrixManager?.setMatrixFrame(frame.render())
    }
}
