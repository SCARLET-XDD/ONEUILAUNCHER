package com.mahin.oneuilauncher.widget

import android.animation.ValueAnimator
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.PathInterpolator

/**
 * Handles the One-UI-style "swipe up from home to open the app drawer"
 * gesture, and swipe-down-to-close.
 */
class SwipeUpDrawerController(
    private val homeRoot: View,
    private val drawerView: View
) {
    private val interpolator = PathInterpolator(0.25f, 0.1f, 0.25f, 1f)
    private var isOpen = false

    private val gestureDetector = GestureDetector(homeRoot.context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (e1 == null) return false
            val deltaY = e2.y - e1.y
            if (!isOpen && deltaY < -80 && velocityY < -300) { open(); return true }
            if (isOpen && deltaY > 80 && velocityY > 300) { close(); return true }
            return false
        }
    })

    fun attach() {
        drawerView.translationY = drawerView.resources.displayMetrics.heightPixels.toFloat()
        drawerView.visibility = View.INVISIBLE

        homeRoot.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event); false }
        drawerView.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event); false }
    }

    fun open() {
        if (isOpen) return
        isOpen = true
        drawerView.visibility = View.VISIBLE
        drawerView.alpha = 0.4f
        animateTranslation(0f)
        drawerView.animate().alpha(1f).setDuration(220).start()
    }

    fun close() {
        if (!isOpen) return
        isOpen = false
        val screenHeight = drawerView.resources.displayMetrics.heightPixels.toFloat()
        animateTranslation(screenHeight) { drawerView.visibility = View.INVISIBLE }
    }

    fun isDrawerOpen() = isOpen

    private fun animateTranslation(target: Float, onEnd: (() -> Unit)? = null) {
        val animator = ValueAnimator.ofFloat(drawerView.translationY, target)
        animator.duration = 320
        animator.interpolator = interpolator
        animator.addUpdateListener {
            drawerView.translationY = it.animatedValue as Float
            if (it.animatedFraction >= 1f) onEnd?.invoke()
        }
        animator.start()
    }
}
