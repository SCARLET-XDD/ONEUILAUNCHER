package com.mahin.oneuilauncher.anim

import android.view.View
import android.view.animation.PathInterpolator

/**
 * Plays when the launcher's onResume() fires - i.e. whenever the user
 * backs out of / closes an app and lands back on the home screen. Gives a
 * soft scale-in + fade so the return to home doesn't feel like a hard cut.
 * This is the "closing" half of the animation engine (see AppLaunchAnimator
 * for why a true reverse of the other app's own close isn't possible
 * without privileged system APIs).
 */
object HomeReturnAnimator {

    private val INTERPOLATOR = PathInterpolator(0.3f, 0f, 0.1f, 1f)

    fun playReturnTransition(root: View) {
        root.alpha = 0f
        root.scaleX = 0.94f
        root.scaleY = 0.94f
        root.animate()
            .alpha(1f).scaleX(1f).scaleY(1f)
            .setDuration(260)
            .setInterpolator(INTERPOLATOR)
            .start()
    }
}
