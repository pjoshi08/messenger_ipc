package com.example.clientapp

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast

/** Command to the service to handle request and send response  */
private const val TO_UPPER_CASE = 1
private const val TO_UPPER_CASE_RESPONSE = 2

class MainActivity : AppCompatActivity() {
    /** Messenger for communicating with the service.  */
    private var messenger: Messenger? = null

    /** Flag indicating whether we have called bind on the service.  */
    private var bound = false

    /**
     * Class for interacting with the main interface of the service.
     */
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            bound = false
            messenger = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            bound = true
            messenger = Messenger(service)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        // Bind to the service
        Intent().apply {
            component = ComponentName("com.example.remoteservice", "com.example.remoteservice.MyService")
            bindService(this, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    fun sayHello(view: View) {
        val msg = Message.obtain(null, TO_UPPER_CASE)
        val stringHello = getString(R.string.hello)
        msg.data = Bundle().apply { putString("data", stringHello) }
        msg.replyTo = Messenger(ResponseHandler(this))

        messenger?.send(msg)
    }

    /**
     * This class handles the service response
     */
    class ResponseHandler(private val context: Context) : Handler() {
        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                TO_UPPER_CASE_RESPONSE -> {
                    val string = msg.data.getString("respData")
                    Toast.makeText(context, string, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
