package com.wu.augus.tipbar

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toolbar
import androidx.annotation.IntDef
import androidx.annotation.LayoutRes
<<<<<<< HEAD
import androidx.annotation.StringRes
=======
>>>>>>> Feature/1-自定義View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListenerAdapter
import com.androidadvance.tsnackbar.kotlin.AnimationUtils.Companion.FAST_OUT_SLOW_IN_INTERPOLATOR
import com.androidadvance.tsnackbar.kotlin.SnackbarManager
import com.google.android.material.behavior.SwipeDismissBehavior
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * @author augus
 * @create 2020-03-14
 * @Describe
 */
class TSnackbar {

    abstract class Callback {

        /**
         * 狀態
         */
        companion object {

            const val DISMISS_EVENT_SWIPE = 0

            const val DISMISS_EVENT_ACTION = 1

            const val DISMISS_EVENT_TIMEOUT = 2

            const val DISMISS_EVENT_MANUAL = 3

            const val DISMISS_EVENT_CONSECUTIVE = 4
        }

        /**
         * @interface
         */
        @IntDef(DISMISS_EVENT_SWIPE, DISMISS_EVENT_ACTION, DISMISS_EVENT_TIMEOUT, DISMISS_EVENT_MANUAL, DISMISS_EVENT_CONSECUTIVE)
        @Retention(RetentionPolicy.SOURCE)
        annotation class DismissEvent
        fun onDismissed(snackbar: TSnackbar, @DismissEvent event: Int) {

        }
        fun onShown(snackbar: TSnackbar) {

        }
    }

    /**
     * @interface
     */
    @IntDef(LENGTH_INDEFINITE, LENGTH_SHORT, LENGTH_LONG)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class Duration

    companion object {

        const val LENGTH_INDEFINITE = -2

        const val LENGTH_SHORT = -1

        const val LENGTH_LONG = 0

<<<<<<< HEAD
=======
    }

>>>>>>> Feature/1-自定義View
    /**
     *  hanlder 顯示與消失
     */
    private val MSG_SHOW = 0
    private val MSG_DISMISS = 1
    private val sHandler: Handler = Handler(Looper.getMainLooper(), Handler.Callback { message ->
        when (message.what) {
            MSG_SHOW -> {
                Log.d(mContext?.packageName, "MSG_SHOW")
                (message.obj as TSnackbar).showView()
                true
            }
            MSG_DISMISS -> {
                Log.d(mContext?.packageName, "MSG_DISMISS")
                (message.obj as TSnackbar).hideView(message.arg1)
                true
            }
        }
        false
    })

    private var mParent: ViewGroup? = null
    private var mContext: Context? = null
    private var mView: SnackbarLayout? = null
    private var mCallback: Callback? = null
    private var mDuration: Int = 0
<<<<<<< HEAD
    @LayoutRes private var mShowLayout:Int = R.layout.view_tip
    private var mDelayTime: Long = 2000
    private var mShowAndHideTime: Long = 200

=======
    private var mShowAndHideTime: Long = 200
>>>>>>> Feature/1-自定義View


    constructor(parent: ViewGroup, @LayoutRes mShowLayout: Int) {
        this.mParent = parent
        this.mContext = parent.context
        val inflater = LayoutInflater.from(mContext)
        //建置顯示畫面
        mView = inflater.inflate(R.layout.tsnackbar_layout, mParent, false) as SnackbarLayout
        //設定顯示畫面 merge layout
        mView?.setLayout(mShowLayout)
    }

    object Snackbar {

        /**
         * @param view
         * @param duration
         * @param mShowLayout 顯示的畫面 default R.layout.view_tip
         * @param mDelayTime 停留時間
         * @param mShowAndHideTime 出現與消失時間
         */
<<<<<<< HEAD
        fun make (view : View, @Duration duration: Int, @LayoutRes mShowLayout:Int , mDelayTime : Long, mShowAndHideTime:Long): TSnackbar {
            val snackbar = TSnackbar(findSuitableParent(view)!!)
            //預設時間
            snackbar.mDuration = duration
            //顯示的畫面
            snackbar.mShowLayout = mShowLayout
            //停留時間
            snackbar.mDelayTime = mDelayTime
            //出現、顯示時間
            snackbar.mShowAndHideTime = mShowAndHideTime
            //
=======
        fun make(view: View, @Duration duration: Int, @LayoutRes mShowLayout: Int, mShowAndHideTime: Long): TSnackbar {
            //帶入顯示畫面
            val snackbar = TSnackbar(findSuitableParent(view)!!, mShowLayout)
            //時間
            snackbar.mDuration = duration
            //出現、顯示時間
            snackbar.mShowAndHideTime = mShowAndHideTime
>>>>>>> Feature/1-自定義View
            return snackbar
        }

        fun make(view: View, @Duration duration: Int): TSnackbar {
<<<<<<< HEAD
            //停留時間
            var mDelayTime : Long = if (duration == LENGTH_LONG) SnackbarManager.LONG_DURATION_MS.toLong() else SnackbarManager.SHORT_DURATION_MS.toLong()
            return make(view, duration,R.layout.view_tip,mDelayTime,200.toLong())
=======
            //預設畫面
            val defaultLayout = R.layout.view_tip
            //上下動畫時間
            val mShowAndHideTime = 200.toLong()
            return make(view, duration, defaultLayout, mShowAndHideTime)
>>>>>>> Feature/1-自定義View
        }

        private fun findSuitableParent(view: View?): ViewGroup? {
            Log.d("Snackbar", "findSuitableParent")
            var view = view
            var fallback: ViewGroup? = null
            do {
                if (view is CoordinatorLayout) {
                    return view
                } else if (view is FrameLayout) {
                    if (view.id == android.R.id.content) {
                        return view
                    } else {
                        fallback = view
                    }
                } else if (view is androidx.appcompat.widget.Toolbar || view is Toolbar) {
                    /*
                    If we return the toolbar here, the toast will be attached inside the toolbar.
                    So we need to find a some sibling ViewGroup to the toolbar that comes after the toolbar
                    If we don't find such view, the toast will be attached to the root activity view
                 */
                    if (view.parent is ViewGroup) {
                        val parent = view.parent as ViewGroup

                        // check if there's something else beside toolbar
                        if (parent.childCount > 1) {
                            val childrenCnt = parent.childCount
                            var toolbarIdx = 0
                            var i = 0
                            while (i < childrenCnt) {
                                // find the index of toolbar in the layout (most likely 0, but who knows)
                                if (parent.getChildAt(i) === view) {
                                    toolbarIdx = i
                                    // check if there's something else after the toolbar in the layout
                                    if (toolbarIdx < childrenCnt - 1) {
                                        //try to find some ViewGroup where you can attach the toast
                                        while (i < childrenCnt) {
                                            i++
                                            val v = parent.getChildAt(i)
                                            if (v is ViewGroup) return v
                                        }
                                    }
                                    break
                                }
                                i++
                            }
                        }
                    }
                }

                if (view != null) {
                    val parent = view.parent
                    view = if (parent is View) parent else null
                }
            } while (view != null)

            return fallback
        }
    }

<<<<<<< HEAD

=======
>>>>>>> Feature/1-自定義View
    /**
     * (可剔除方法)
     * 建立繪製範圍並寫入圖片
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getBitmap(vectorDrawable: VectorDrawable): Bitmap {
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return bitmap
    }

    /**
     * (可剔除方法)
     * 建立繪製範圍並寫入圖片
     */
    private fun getBitmap(drawable: Drawable): Bitmap {
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    drawable is VectorDrawable
                } else {
                    TODO("VERSION.SDK_INT < LOLLIPOP")
                }) {
            getBitmap(drawable)
        } else {
            throw IllegalArgumentException("unsupported drawable type")
        }
    }

<<<<<<< HEAD
    fun setDuration(@Duration duration: Int): TSnackbar {
=======
    /**
     * 設置時間
     */
    fun setDuration(duration: Int): TSnackbar {
>>>>>>> Feature/1-自定義View
        mDuration = duration
        return this
    }


    /**
     * @Duration
     * 設置時間
     */
    fun getDuration(): Int {
        return mDuration
    }

    /**
     * 取得顯示的畫面 View
     */
    fun getView(): View? {
        return mView
    }

    fun show() {
        Log.d(mContext?.packageName, "show")
        SnackbarManager.get().show(mDuration, mManagerCallback)
    }

    fun dismiss() {
        dispatchDismiss(Callback.DISMISS_EVENT_MANUAL)
    }

    private fun dispatchDismiss(@Callback.DismissEvent event: Int) {
        SnackbarManager.get().dismiss(mManagerCallback, event)
    }

    fun setCallback(callback: Callback): TSnackbar {
        mCallback = callback
        return this
    }


    fun isShown(): Boolean {
        return SnackbarManager.get().isCurrent(mManagerCallback)
    }

    fun isShownOrQueued(): Boolean {
        return SnackbarManager.get().isCurrentOrNext(mManagerCallback)
    }

    private val mManagerCallback = object : SnackbarManager.Callback {
        override fun show() {
            Log.d(mContext?.packageName, "show  !")
            sHandler?.sendMessage(sHandler?.obtainMessage(MSG_SHOW, this@TSnackbar))
        }

        override fun dismiss(event: Int) {
            Log.d(mContext?.packageName, "dismiss  !")
            sHandler?.sendMessage(sHandler?.obtainMessage(MSG_DISMISS, event, 0, this@TSnackbar))
        }
    }

    internal fun showView() {
        if (mView!!.getParent() == null) {
            val lp = mView!!.getLayoutParams()

            if (lp is CoordinatorLayout.LayoutParams) {
                val behavior = Behavior()
                behavior.setStartAlphaSwipeDistance(0.1f)
                behavior.setEndAlphaSwipeDistance(0.6f)
                behavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_START_TO_END)
                behavior.setListener(object : SwipeDismissBehavior.OnDismissListener {
                    override fun onDismiss(view: View) {
                        dispatchDismiss(Callback.DISMISS_EVENT_SWIPE)
                    }

                    override fun onDragStateChanged(state: Int) {
                        when (state) {
                            SwipeDismissBehavior.STATE_DRAGGING, SwipeDismissBehavior.STATE_SETTLING ->
                                SnackbarManager.get().cancelTimeout(mManagerCallback)
                            SwipeDismissBehavior.STATE_IDLE ->
                                SnackbarManager.get().restoreTimeout(mManagerCallback)
                        }
                    }
                })
                (lp as CoordinatorLayout.LayoutParams).behavior = behavior
            }
            mParent!!.addView(mView)
        }

        mView!!.setOnAttachStateChangeListener(object : SnackbarLayout.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {}

            override fun onViewDetachedFromWindow(v: View) {
                if (isShownOrQueued()) {
                    sHandler?.post(Runnable { onViewHidden(Callback.DISMISS_EVENT_MANUAL) })
                }
            }
        })

        if (ViewCompat.isLaidOut(mView!!)) {
            animateViewIn()
        } else {
            mView!!.setOnLayoutChangeListener(object : SnackbarLayout.OnLayoutChangeListener {
                override fun onLayoutChange(view: View, left: Int, top: Int, right: Int, bottom: Int) {
                    animateViewIn()
                    mView!!.setOnLayoutChangeListener(null)
                }
            })
        }
    }

    private fun animateViewIn() {
        Log.d(mContext?.packageName, "animateViewIn")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Log.d(mContext?.packageName, "animateViewIn 1")
            ViewCompat.setTranslationY(mView, (-mView!!.getHeight()).toFloat())
            ViewCompat.animate(mView!!)
                    .translationY(0f)
                    .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                    .setDuration(mShowAndHideTime)
                    .setListener(object : ViewPropertyAnimatorListenerAdapter() {
                        override fun onAnimationStart(view: View?) {
                            Log.d(mContext?.packageName, "animateViewIn 1-onAnimationStart ")
//                            mView!!.animateChildrenIn(mShowAndHideTime - ANIMATION_FADE_DURATION,
//                                    ANIMATION_FADE_DURATION)
                        }

                        override fun onAnimationEnd(view: View?) {
                            Log.d(mContext?.packageName, "animateViewIn 1-onAnimationEnd ")
                            if (mCallback != null) {
                                Log.d(mContext?.packageName, "animateViewIn 1-onAnimationEnd-onShown ")
                                mCallback!!.onShown(this@TSnackbar)
                            }
                            SnackbarManager.get()
                                    .onShown(mManagerCallback)
                        }
                    })
                    .start()
        } else {
            Log.d(mContext?.packageName, "animateViewIn 2")
            val anim = AnimationUtils.loadAnimation(mView!!.getContext(),
                    R.anim.top_in)
            anim.interpolator = FAST_OUT_SLOW_IN_INTERPOLATOR
            anim.duration = mShowAndHideTime
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationEnd(animation: Animation) {
                    if (mCallback != null) {
                        mCallback!!.onShown(this@TSnackbar)
                    }
                    SnackbarManager.get().onShown(mManagerCallback)
                }

                override fun onAnimationStart(animation: Animation) {}

                override fun onAnimationRepeat(animation: Animation) {}
            })
            mView!!.startAnimation(anim)
        }
    }

    private fun animateViewOut(event: Int) {
        Log.d(mContext?.packageName, "animateViewOut")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Log.d(mContext?.packageName, "animateViewOut 1")
            ViewCompat.animate(mView!!)
                    .translationY((-mView!!.getHeight()).toFloat())
                    .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                    .setDuration(mShowAndHideTime)
                    .setListener(object : ViewPropertyAnimatorListenerAdapter() {
                        override fun onAnimationStart(view: View?) {
//                            mView!!.animateChildrenOut(0, ANIMATION_FADE_DURATION)
                        }

                        override fun onAnimationEnd(view: View?) {
                            onViewHidden(event)
                        }
                    })
                    .start()
        } else {
            Log.d(mContext?.packageName, "animateViewOut 2")
            val anim = AnimationUtils.loadAnimation(mView!!.getContext(), R.anim.top_out)
            anim.interpolator = FAST_OUT_SLOW_IN_INTERPOLATOR
            anim.duration = mShowAndHideTime
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationEnd(animation: Animation) {
                    onViewHidden(event)
                }

                override fun onAnimationStart(animation: Animation) {}

                override fun onAnimationRepeat(animation: Animation) {}
            })
            mView!!.startAnimation(anim)
        }
    }

    internal fun hideView(event: Int) {
        if (mView!!.getVisibility() != View.VISIBLE || isBeingDragged()) {
            onViewHidden(event)
        } else {
            animateViewOut(event)
        }
    }

    private fun onViewHidden(event: Int) {

        SnackbarManager.get().onDismissed(mManagerCallback)

        if (mCallback != null) {
            mCallback!!.onDismissed(this, event)
        }

        val parent = mView!!.getParent()
        if (parent is ViewGroup) {
            (parent as ViewGroup).removeView(mView)
        }
    }

    private fun isBeingDragged(): Boolean {
        val lp = mView!!.getLayoutParams()

        if (lp is CoordinatorLayout.LayoutParams) {
            val cllp = lp as CoordinatorLayout.LayoutParams
            val behavior = cllp.behavior

            if (behavior is SwipeDismissBehavior<*>) {
                return behavior.dragState != SwipeDismissBehavior.STATE_IDLE
            }
        }
        return false
    }


    class SnackbarLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

        internal var mMaxWidth: Int
        private val mMaxInlineActionWidth: Int

        private var mOnLayoutChangeListener: OnLayoutChangeListener? = null
        private var mOnAttachStateChangeListener: OnAttachStateChangeListener? = null

        interface OnLayoutChangeListener {
            fun onLayoutChange(view: View, left: Int, top: Int, right: Int, bottom: Int)
        }

        interface OnAttachStateChangeListener {
            fun onViewAttachedToWindow(v: View)
            fun onViewDetachedFromWindow(v: View)
        }

        init {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SnackbarLayout)
            mMaxWidth = a.getDimensionPixelSize(R.styleable.SnackbarLayout_android_maxWidth, -1)
            mMaxInlineActionWidth = a.getDimensionPixelSize(
                    R.styleable.SnackbarLayout_maxActionInlineWidth, -1)
            if (a.hasValue(R.styleable.SnackbarLayout_elevation)) {
                ViewCompat.setElevation(this, a.getDimensionPixelSize(
                        R.styleable.SnackbarLayout_elevation, 0).toFloat())
            }
            //
            a.recycle()

            //可以點擊
            isClickable = true

<<<<<<< HEAD
            LayoutInflater.from(context)
                    .inflate(R.layout.view_tip, this)


=======
            //設置指定視圖的實時區域模式。
>>>>>>> Feature/1-自定義View
            ViewCompat.setAccessibilityLiveRegion(this,
                    ViewCompat.ACCESSIBILITY_LIVE_REGION_POLITE)
        }

        /**
         * 設置嵌入的layout
         */
        fun setLayout(showLayout: Int) {
            LayoutInflater.from(context)
                    .inflate(showLayout, this)

        }

        override fun onFinishInflate() {
            super.onFinishInflate()
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            var widthMeasureSpec = widthMeasureSpec
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }


        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
            super.onLayout(changed, l, t, r, b)
            if (mOnLayoutChangeListener != null) {
                mOnLayoutChangeListener!!.onLayoutChange(this, l, t, r, b)
            }
        }

        override fun onAttachedToWindow() {
            super.onAttachedToWindow()
            if (mOnAttachStateChangeListener != null) {
                mOnAttachStateChangeListener!!.onViewAttachedToWindow(this)
            }
        }

        override fun onDetachedFromWindow() {
            super.onDetachedFromWindow()
            if (mOnAttachStateChangeListener != null) {
                mOnAttachStateChangeListener!!.onViewDetachedFromWindow(this)
            }
        }

        internal fun setOnLayoutChangeListener(onLayoutChangeListener: OnLayoutChangeListener?) {
            mOnLayoutChangeListener = onLayoutChangeListener
        }

        internal fun setOnAttachStateChangeListener(listener: OnAttachStateChangeListener) {
            mOnAttachStateChangeListener = listener
        }
    }


    internal inner class Behavior : SwipeDismissBehavior<SnackbarLayout>() {
        override fun canSwipeDismissView(child: View): Boolean {
            return child is SnackbarLayout
        }

        override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: SnackbarLayout,
                                           event: MotionEvent): Boolean {

            if (parent.isPointInChildBounds(child, event.x.toInt(), event.y.toInt())) {
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> SnackbarManager.get().cancelTimeout(mManagerCallback)
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> SnackbarManager.get().restoreTimeout(mManagerCallback)
                }
            }

            return super.onInterceptTouchEvent(parent, child, event)
        }
    }
}