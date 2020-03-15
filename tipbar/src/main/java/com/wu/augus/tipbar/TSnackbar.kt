package com.androidadvance.tsnackbar.kotlin

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
import android.util.DisplayMetrics
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
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListenerAdapter

import com.androidadvance.tsnackbar.kotlin.AnimationUtils.Companion.FAST_OUT_SLOW_IN_INTERPOLATOR
import com.google.android.material.behavior.SwipeDismissBehavior
import com.wu.augus.tipbar.R
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * @author augus
 * @create 2020-03-14
 * @Describe
 */
class TSnackbar {

    abstract class Callback {

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
    @Retention(RetentionPolicy.SOURCE)
    annotation class Duration

    companion object {
        const val LENGTH_INDEFINITE = -2

        const val LENGTH_SHORT = -1

        const val LENGTH_LONG = 0
    }

    private val ANIMATION_DURATION = 200
    private val ANIMATION_FADE_DURATION = 180

    private val MSG_SHOW = 0
    private val MSG_DISMISS = 1

    private val sHandler: Handler = Handler(Looper.getMainLooper(), Handler.Callback { message ->
        when (message.what) {
            MSG_SHOW -> {
                Log.d(mContext?.packageName, "MSG_SHOW  !")
                (message?.obj as TSnackbar).showView()
                true
            }
            MSG_DISMISS -> {
                Log.d(mContext?.packageName, "MSG_DISMISS  !")
                (message?.obj as TSnackbar).hideView(message?.arg1)
                true
            }
        }
        false
    })

    private var mParent: ViewGroup? = null
    private var mContext: Context? = null
    private var mView: SnackbarLayout? = null
    private var mDuration: Int = 0
    private var mCallback: Callback? = null

    constructor(parent: ViewGroup) {
        this.mParent = parent
        this.mContext = parent.context
        val inflater = LayoutInflater.from(mContext)
        mView = inflater.inflate(R.layout.tsnackbar_layout, mParent, false) as SnackbarLayout
        Log.d(mContext?.packageName, "constructor")
    }

    object Snackbar {

        fun make(view: View, text: CharSequence, @Duration duration: Int): TSnackbar {
            val snackbar = TSnackbar(findSuitableParent(view)!!)
//            snackbar.setText(text)
            snackbar.mDuration = duration
            Log.d("Snackbar", "make1" + "snackbar is null" + (snackbar == null))
            return snackbar
        }

        fun make(view: View, @StringRes resId: Int, @Duration duration: Int): TSnackbar {
            Log.d("Snackbar", "make2")
            return make(view, view.resources
                    .getText(resId), duration)
        }

//        fun make(view: View, text: CharSequence, @Duration duration: Int): TSnackbar {
//            Log.d("Snackbar", "make2")
//            return make(view, view.resources
//                    .getText(resId), duration)
//        }


        private fun findSuitableParent(view: View?): ViewGroup? {
            Log.d("Snackbar", "findSuitableParent")
            var view = view
            var fallback: ViewGroup? = null
            do {
                if (view is CoordinatorLayout) {
                    Log.d("Snackbar", "findSuitableParent CoordinatorLayout")
                    return view
                } else if (view is FrameLayout) {
                    if (view.id == android.R.id.content) {
                        Log.d("Snackbar", "findSuitableParent FrameLayout content")
                        return view
                    } else {
                        Log.d("Snackbar", "findSuitableParent FrameLayout")
                        fallback = view
                    }
                } else if (view is androidx.appcompat.widget.Toolbar || view is Toolbar) {
                    Log.d("Snackbar", "findSuitableParent Toolbar")
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

                    //                return (ViewGroup) view;
                }

                if (view != null) {
                    val parent = view.parent
                    view = if (parent is View) parent else null
                }
            } while (view != null)

            return fallback
        }
    }

//    @Deprecated("")
//    fun addIcon(resource_id: Int, size: Int): TSnackbar {
//        val tv = mView!!.getMessageView()
//
//        tv.setCompoundDrawablesWithIntrinsicBounds(BitmapDrawable(Bitmap.createScaledBitmap((mContext?.getResources()!!
//                .getDrawable(resource_id) as BitmapDrawable).bitmap, size, size, true)), null, null, null)
//
//        return this
//    }
//
//    fun setIconPadding(padding: Int): TSnackbar {
//        val tv = mView!!.getMessageView()
//        tv.setCompoundDrawablePadding(padding)
//        return this
//    }
//
//
//    fun setIconLeft(@DrawableRes drawableRes: Int, sizeDp: Float): TSnackbar {
//        val tv = mView!!.getMessageView()
//        var drawable = ContextCompat.getDrawable(mContext!!, drawableRes)
//        if (drawable != null) {
//            drawable = fitDrawable(drawable, convertDpToPixel(sizeDp, mContext!!).toInt())
//        } else {
//            throw IllegalArgumentException("resource_id is not a valid drawable!")
//        }
//        val compoundDrawables = tv.getCompoundDrawables()
//        tv.setCompoundDrawables(drawable, compoundDrawables[1], compoundDrawables[2], compoundDrawables[3])
//        return this
//    }
//
//    fun setIconRight(@DrawableRes drawableRes: Int, sizeDp: Float): TSnackbar {
//        val tv = mView!!.getMessageView()
//
//        var drawable = ContextCompat.getDrawable(mContext!!, drawableRes)
//        if (drawable != null) {
//            drawable = fitDrawable(drawable, convertDpToPixel(sizeDp, mContext!!).toInt())
//        } else {
//            throw IllegalArgumentException("resource_id is not a valid drawable!")
//        }
//        val compoundDrawables = tv.getCompoundDrawables()
//        tv.setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], drawable, compoundDrawables[3])
//        return this
//    }

    /**
     * Overrides the max width of this snackbar's layout. This is typically not necessary; the snackbar
     * width will be according to Google's Material guidelines. Specifically, the max width will be
     *
     *
     * To allow the snackbar to have a width equal to the parent view, set a value <= 0.
     *
     * @param maxWidth the max width in pixels
     * @return this TSnackbar
     */
    fun setMaxWidth(maxWidth: Int): TSnackbar {
        mView!!.mMaxWidth = maxWidth

        return this
    }

    private fun fitDrawable(drawable: Drawable, sizePx: Int): Drawable {
        var drawable = drawable
        if (drawable.intrinsicWidth != sizePx || drawable.intrinsicHeight != sizePx) {

            if (drawable is BitmapDrawable) {

                drawable = BitmapDrawable(mContext?.getResources(), Bitmap.createScaledBitmap(getBitmap(drawable), sizePx, sizePx, true))
            }
        }
        drawable.setBounds(0, 0, sizePx, sizePx)

        return drawable
    }

    private fun convertDpToPixel(dp: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getBitmap(vectorDrawable: VectorDrawable): Bitmap {
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return bitmap
    }

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

//    fun setAction(@StringRes resId: Int, listener: View.OnClickListener): TSnackbar {
////        return setAction(mContext!!.getText(resId), listener)
////    }
////
////    fun setAction(text: CharSequence, listener: View.OnClickListener): TSnackbar {
////        return setAction(text, true, listener)
////    }

//    fun setAction(text: CharSequence, shouldDismissOnClick: Boolean, listener: View.OnClickListener?): TSnackbar {
//        val tv = mView!!.getActionView()
//
//        if (TextUtils.isEmpty(text) || listener == null) {
//            tv.setVisibility(View.GONE)
//            tv.setOnClickListener(null)
//        } else {
//            tv.setVisibility(View.VISIBLE)
//            tv.setText(text)
//            tv.setOnClickListener(View.OnClickListener { view ->
//                listener.onClick(view)
//                if (shouldDismissOnClick) {
//                    dispatchDismiss(Callback.DISMISS_EVENT_ACTION)
//                }
//            })
//        }
//        return this
//    }

//    fun setActionTextColor(colors: ColorStateList): TSnackbar {
//        val tv = mView!!.getActionView()
//        tv.setTextColor(colors)
//        return this
//    }
//
//    fun setActionTextColor(@ColorInt color: Int): TSnackbar {
//        val tv = mView!!.getActionView()
//        tv.setTextColor(color)
//        return this
//    }
//
//
//    fun setText(message: CharSequence): TSnackbar {
//        val tv = mView!!.getMessageView()
//        tv.setText(message)
//        return this
//    }
//
//    fun setText(@StringRes resId: Int): TSnackbar {
//        return setText(mContext!!.getText(resId))
//    }

    fun setDuration(@Duration duration: Int): TSnackbar {
        mDuration = duration
        return this
    }

    @Duration
    fun getDuration(): Int {
        return mDuration
    }

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
                    .setDuration(ANIMATION_DURATION.toLong())
                    .setListener(object : ViewPropertyAnimatorListenerAdapter() {
                        override fun onAnimationStart(view: View?) {
                            Log.d(mContext?.packageName, "animateViewIn 1-onAnimationStart ")
//                            mView!!.animateChildrenIn(ANIMATION_DURATION - ANIMATION_FADE_DURATION,
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
            anim.duration = ANIMATION_DURATION.toLong()
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
                    .setDuration(ANIMATION_DURATION.toLong())
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
            anim.duration = ANIMATION_DURATION.toLong()
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

//        private var messageView: TextView? = null
//        private var actionView: Button? = null

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
            a.recycle()

            isClickable = true

            LayoutInflater.from(context)
                    .inflate(R.layout.view_tip, this)

//            LayoutInflater.from(context)
//                    .inflate(R.layout.tsnackbar_layout_include, this)

            ViewCompat.setAccessibilityLiveRegion(this,
                    ViewCompat.ACCESSIBILITY_LIVE_REGION_POLITE)
        }

        override fun onFinishInflate() {
            super.onFinishInflate()
//            messageView = findViewById(R.id.snackbar_text) as TextView
//            actionView = findViewById(R.id.snackbar_action) as Button
        }

//        fun getActionView(): Button {
//            return actionView!!;
//        }
//
//        fun getMessageView(): TextView {
//            return messageView!!;
//        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            var widthMeasureSpec = widthMeasureSpec
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)

//            if (mMaxWidth > 0 && measuredWidth > mMaxWidth) {
//                widthMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxWidth, MeasureSpec.EXACTLY)
//                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//            }
//
//            val multiLineVPadding = resources.getDimensionPixelSize(
//                    R.dimen.design_snackbar_padding_vertical_2lines)
//            val singleLineVPadding = resources.getDimensionPixelSize(
//                    R.dimen.design_snackbar_padding_vertical)
//            val isMultiLine = messageView!!.layout
//                    .lineCount > 1
//
//            var remeasure = false
//            if (isMultiLine && mMaxInlineActionWidth > 0
//                    && actionView!!.measuredWidth > mMaxInlineActionWidth) {
//                if (updateViewsWithinLayout(LinearLayout.VERTICAL, multiLineVPadding,
//                                multiLineVPadding - singleLineVPadding)) {
//                    remeasure = true
//                }
//            } else {
//                val messagePadding = if (isMultiLine) multiLineVPadding else singleLineVPadding
//                if (updateViewsWithinLayout(LinearLayout.HORIZONTAL, messagePadding, messagePadding)) {
//                    remeasure = true
//                }
//            }
//
//            if (remeasure) {
//                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//            }
        }

//        internal fun animateChildrenIn(delay: Int, duration: Int) {
//            ViewCompat.setAlpha(messageView!!, 0f)
//            ViewCompat.animate(messageView!!)
//                    .alpha(1f)
//                    .setDuration(duration.toLong())
//                    .setStartDelay(delay.toLong())
//                    .start()
//
//            if (actionView!!.visibility == View.VISIBLE) {
//                ViewCompat.setAlpha(actionView!!, 0f)
//                ViewCompat.animate(actionView!!)
//                        .alpha(1f)
//                        .setDuration(duration.toLong())
//                        .setStartDelay(delay.toLong())
//                        .start()
//            }
//        }
//
//        internal fun animateChildrenOut(delay: Int, duration: Int) {
//            ViewCompat.setAlpha(messageView!!, 1f)
//            ViewCompat.animate(messageView!!)
//                    .alpha(0f)
//                    .setDuration(duration.toLong())
//                    .setStartDelay(delay.toLong())
//                    .start()
//
//            if (actionView!!.visibility == View.VISIBLE) {
//                ViewCompat.setAlpha(actionView!!, 1f)
//                ViewCompat.animate(actionView!!)
//                        .alpha(0f)
//                        .setDuration(duration.toLong())
//                        .setStartDelay(delay.toLong())
//                        .start()
//            }
//        }

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

//        private fun updateViewsWithinLayout(orientation: Int,
//                                            messagePadTop: Int, messagePadBottom: Int): Boolean {
//            var changed = false
//            if (orientation != getOrientation()) {
//                setOrientation(orientation)
//                changed = true
//            }
//            if (messageView!!.paddingTop != messagePadTop || messageView!!.paddingBottom != messagePadBottom) {
//                updateTopBottomPadding(messageView!!, messagePadTop, messagePadBottom)
//                changed = true
//            }
//            return changed
//        }

//        private fun updateTopBottomPadding(view: View, topPadding: Int, bottomPadding: Int) {
//            if (ViewCompat.isPaddingRelative(view)) {
//                ViewCompat.setPaddingRelative(view,
//                        ViewCompat.getPaddingStart(view), topPadding,
//                        ViewCompat.getPaddingEnd(view), bottomPadding)
//            } else {
//                view.setPadding(view.paddingLeft, topPadding,
//                        view.paddingRight, bottomPadding)
//            }
//        }
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