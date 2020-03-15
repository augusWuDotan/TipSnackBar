package com.androidadvance.tsnackbar.kotlin

import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

/**
 * @author augus
 * @create 2020-03-14
 * @Describe
 */
class AnimationUtils {

    companion object {

        /**
         * Interpolator
         */
        val LINEAR_INTERPOLATOR: Interpolator = LinearInterpolator()
        val FAST_OUT_SLOW_IN_INTERPOLATOR: Interpolator = FastOutSlowInInterpolator()
        val DECELERATE_INTERPOLATOR: Interpolator = DecelerateInterpolator()

        /**
         *
         */
        fun lerp(startValue: Float, endValue: Float, fraction: Float): Float {
            return startValue + fraction * (endValue - startValue)
        }

        /**
         *
         */
        fun lerp(startValue: Int, endValue: Int, fraction: Float): Int {
            return startValue + Math.round(fraction * (endValue - startValue).toFloat())
        }

    }

    class AnimationListenerAdapter : Animation.AnimationListener {

        constructor()

        override fun onAnimationStart(animation: Animation) {}

        override fun onAnimationEnd(animation: Animation) {}

        override fun onAnimationRepeat(animation: Animation) {}
    }
}

