package com.mahin.oneuilauncher.anim

import android.app.Activity
import android.app.ActivityOptions
import android.view.View
import android.view.animation.PathInterpolator
import com.mahin.oneuilauncher.model.AppInfo
import com.mahin.oneuilauncher.repo.AppRepository

/**
 * The launcher's animation engine for OPENING apps.
 *
 * We tell Android's ActivityOptions system exactly where the tapped icon
 * sits on screen; the OS then plays a genuine scale/reveal transition
 * growing out of that icon. This is the real public API for this effect
 * (the same one stock Android and Samsung's own launcher fall back to for
 * third-party/non-privileged callers).
 *
 * A true reverse-of-the-other-app's-close animation is only available
 * through Android's privileged RemoteAnimation/QuickStep APIs (what
 * Lawnchair's Quickstep module and Samsung's system launcher use), which
 * require system-app signature permissions not available to a normal
 * sideloaded APK without root. See HomeReturnAnimator for the closest
 * achievable approximation: animating the launcher's own home screen back
 * in when it regains focus.
 */
object AppLaunchAnimator {

    private val INTERPOLATOR = PathInterpolator(0.2f, 0f, 0f, 1f)

    fun launch(activity: Activity, iconView: View, app: AppInfo) {
        val intent = AppRepository.launchIntentFor(app)
        val options = ActivityOptions.makeScaleUpAnimation(
            iconView, 0, 0, iconView.width, iconView.height
        )

        // Small tap-pop for immediate tactile feedback before the system
        // transition takes over.
        iconView.animate()
            .scaleX(0.85f).scaleY(0.85f)
            .setDuration(90)
            .setInterpolator(INTERPOLATOR)
            .withEndAction {
                iconView.animate().scaleX(1f).scaleY(1f).setDuration(120).start()
                activity.startActivity(intent, options.toBundle())
            }
            .start()
    }
}
