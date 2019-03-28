package com.example.remoteservice

import android.app.Service
import android.content.Intent
import android.os.*

/** Command to the service to generate random number  */
private const val TO_UPPER_CASE = 1
private const val TO_UPPER_CASE_RESPONSE = 2

class MyService : Service() {

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    private lateinit var messenger: Messenger

    /**
     * Handler of incoming messages from clients.
     */
    internal class IncomingHandler : Handler() {
        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                TO_UPPER_CASE -> {
                    val string = msg.data.getString("data")
                    val response = Message.obtain(null, TO_UPPER_CASE_RESPONSE)
                    response.data = Bundle().apply { putString("respData", string?.toUpperCase()) }
                    msg.replyTo.send(response)
                }

                else -> super.handleMessage(msg)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        messenger = Messenger(IncomingHandler())
        return messenger.binder
    }



}
