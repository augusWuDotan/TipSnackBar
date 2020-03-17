package com.androidadvance.tsnackbar.kotlin

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.wu.augus.tipbar.TSnackbar
import java.lang.ref.WeakReference


/**
 * @author augus
 * @create 2020-03-14
 * @Describe
 */
class SnackbarManager {

    companion object {

        val MSG_TIMEOUT = 0
        val SHORT_DURATION_MS = 1500
        val LONG_DURATION_MS = 2750

        private var instance: SnackbarManager? = null
            get() {
                if (field == null) {
                    field = SnackbarManager()
                }
                return field
            }

        @Synchronized
        fun get(): SnackbarManager {
            return instance!!
        }
    }

    private var mLock: Any? = null
    private var mHandler: Handler? = null
    private var mCurrentSnackbar: SnackbarRecord? = null
    private var mNextSnackbar: SnackbarRecord? = null

    constructor() {
        mLock = Any()
        mHandler = Handler(Looper.getMainLooper(), Handler.Callback { message ->
            when (message.what) {
                MSG_TIMEOUT -> {
                    Log.d("SnackbarManager", "mHandler MSG_TIMEOUT")
                    handleTimeout(message.obj as SnackbarRecord)
                    return@Callback true
                }
            }
            false
        })
    }

    interface Callback {
        fun show()
        fun dismiss(event: Int)

        //test


    }

    @Synchronized
    fun show(duration: Int, callback: Callback) {
        Log.d("SnackbarManager", "show")
        if (isCurrentSnackbar(callback)) {
            Log.d("SnackbarManager", "show 1")
            // Means that the callback is already in the queue. We'll just update the duration
            mCurrentSnackbar?.duration = duration
            // If this is the TSnackbar currently being shown, call re-schedule it's
            // timeout
            mHandler?.removeCallbacksAndMessages(mCurrentSnackbar)
            scheduleTimeoutLocked(mCurrentSnackbar!!)
            return
        } else if (isNextSnackbar(callback)) {
            Log.d("SnackbarManager", "show 2")
            // We'll just update the duration
            mNextSnackbar?.duration = duration
        } else {
            Log.d("SnackbarManager", "show 3")
            // Else, we need to create a new record and queue it
            mNextSnackbar = SnackbarRecord(duration, callback)
        }

        if (mCurrentSnackbar != null && cancelSnackbarLocked(mCurrentSnackbar!!,
                        TSnackbar.Callback.DISMISS_EVENT_CONSECUTIVE)) {
            Log.d("SnackbarManager", "show 4")
            // If we currently have a TSnackbar, try and cancel it and wait in line
            return
        } else {
            Log.d("SnackbarManager", "show 5")
            // Clear out the current snackbar
            mCurrentSnackbar = null
            // Otherwise, just show it now
            showNextSnackbarLocked()
        }

    }

    @Synchronized
    fun dismiss(callback: Callback, event: Int) {
        Log.d("SnackbarManager", "dismiss")
        if (isCurrentSnackbar(callback)) {
            Log.d("SnackbarManager", "dismiss 1")
            cancelSnackbarLocked(mCurrentSnackbar!!, event)
        } else if (isNextSnackbar(callback)) {
            Log.d("SnackbarManager", "dismiss 2")
            cancelSnackbarLocked(mNextSnackbar!!, event)
        }
    }

    /**
     * Should be called when a TSnackbar is no longer displayed. This is after any exit
     * animation has finished.
     */
    @Synchronized
    fun onDismissed(callback: Callback) {
        Log.d("SnackbarManager", "onDismissed")
        if (isCurrentSnackbar(callback)) {
            Log.d("SnackbarManager", "onDismissed 1")
            // If the callback is from a TSnackbar currently show, remove it and show a new one
            mCurrentSnackbar = null
            if (mNextSnackbar != null) {
                Log.d("SnackbarManager", "onDismissed 2")
                showNextSnackbarLocked()
            }
        }
    }

    /**
     * Should be called when a TSnackbar is being shown. This is after any entrance animation has
     * finished.
     */
    @Synchronized
    fun onShown(callback: Callback) {
        Log.d("SnackbarManager", "onShown")
        if (isCurrentSnackbar(callback)) {
            Log.d("SnackbarManager", "onShown 1")
            scheduleTimeoutLocked(mCurrentSnackbar!!)
        }
    }

    @Synchronized
    fun cancelTimeout(callback: Callback) {
        if (isCurrentSnackbar(callback)) {
            mHandler!!.removeCallbacksAndMessages(mCurrentSnackbar)
        }
    }

    @Synchronized
    fun restoreTimeout(callback: Callback) {
        if (isCurrentSnackbar(callback)) {
            scheduleTimeoutLocked(mCurrentSnackbar!!)
        }
    }

    @Synchronized
    fun isCurrent(callback: Callback): Boolean {
        return isCurrentSnackbar(callback)
    }

    @Synchronized
    fun isCurrentOrNext(callback: Callback): Boolean {
        return isCurrentSnackbar(callback) || isNextSnackbar(callback)
    }


    private class SnackbarRecord internal constructor(private val _duration: Int, callback: Callback) {
        val callback: WeakReference<Callback>
        var duration: Int? = null

        init {
            this.callback = WeakReference<Callback>(callback)
            this.duration = _duration;
        }

        internal fun isSnackbar(callback: Callback?): Boolean {
            return callback != null && this.callback.get() === callback
        }
    }

    private fun showNextSnackbarLocked() {
        if (mNextSnackbar != null) {
            mCurrentSnackbar = mNextSnackbar
            mNextSnackbar = null

            val callback = mCurrentSnackbar?.callback?.get()
            if (callback != null) {
                callback.show()
            } else {
                // The callback doesn't exist any more, clear out the TSnackbar
                mCurrentSnackbar = null
            }
        }
    }

    private fun cancelSnackbarLocked(record: SnackbarRecord, event: Int): Boolean {
        val callback = record.callback.get()
        if (callback != null) {
            callback.dismiss(event)
            return true
        }
        return false
    }


    private fun synchronized(mLock: Any?, block: () -> Unit) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private fun isCurrentSnackbar(callback: Callback): Boolean {
        return mCurrentSnackbar != null && mCurrentSnackbar?.isSnackbar(callback)!!
    }

    private fun isNextSnackbar(callback: Callback): Boolean {
        return mNextSnackbar != null && mNextSnackbar?.isSnackbar(callback)!!
    }

    private fun scheduleTimeoutLocked(r: SnackbarRecord) {
        Log.d("SnackbarManager", "scheduleTimeoutLocked")
        if (r.duration == TSnackbar.LENGTH_INDEFINITE) {
            Log.d("SnackbarManager", "scheduleTimeoutLocked 1")
            // If we're set to indefinite, we don't want to set a timeout
            return
        }

        var durationMs = LONG_DURATION_MS
        if (r.duration!! > 0) {
            Log.d("SnackbarManager", "scheduleTimeoutLocked 2")
            durationMs = r.duration!!
        } else if (r.duration == TSnackbar.LENGTH_SHORT) {
            Log.d("SnackbarManager", "scheduleTimeoutLocked 3")
            durationMs = SHORT_DURATION_MS
        }

        mHandler?.removeCallbacksAndMessages(r)
        mHandler?.sendMessageDelayed(Message.obtain(mHandler, MSG_TIMEOUT, r), durationMs.toLong())
    }

    @Synchronized
    private fun handleTimeout(record: SnackbarRecord) {
        Log.d("SnackbarManager", "handleTimeout")
        if (mCurrentSnackbar === record || mNextSnackbar === record) {
            cancelSnackbarLocked(record, TSnackbar.Callback.DISMISS_EVENT_TIMEOUT)
        }
    }
}