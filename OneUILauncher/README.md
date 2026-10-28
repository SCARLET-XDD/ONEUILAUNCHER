# NovaOne Launcher — One UI–style launcher with a custom animation engine

A complete Android Studio project for a real home-screen launcher app:

- Registers as an actual Android **HOME app** — settable as your default launcher.
- One UI–inspired dark look: rounded-square icon grid, floating pill-shaped dock,
  wallpaper showing through (edge-to-edge, transparent status/nav bars).
- Swipe up from the home screen to open a searchable **app drawer**; swipe down
  (or back) to close it.
- Long-press an icon for **App info / Uninstall**, like One UI's icon popup.
- A custom **animation engine** (`anim/AppLaunchAnimator.kt` +
  `anim/HomeReturnAnimator.kt`) that scales an app open from the exact icon
  you tapped, and eases the home screen back in when you return to it.

## Build it (you said you'll use Android Studio — here's the exact path)

1. Install **Android Studio** if you haven't: https://developer.android.com/studio
2. **File → Open** → select this `OneUILauncher` folder.
3. Let Gradle sync. First sync needs internet — it downloads the Android SDK
   platform, build tools, and the libraries listed in `app/build.gradle`.
   This project has no Gradle wrapper checked in; Android Studio will offer
   to generate one automatically the first time you open it (accept that).
4. **Build → Build App Bundle(s) / APK(s) → Build APK(s)**.
5. When it finishes, click **locate** in the notification, or find it at:
   `app/build/outputs/apk/debug/app-debug.apk`

## Installing it on your Realme Note 70

1. Copy `app-debug.apk` to the phone (USB, or upload it somewhere and
   download it on-device).
2. On the phone: **Settings → Apps → Special access → Install unknown apps**
   → allow the app you'll open the APK with (Files, browser, etc.).
3. Open the APK on the phone → **Install**.
4. Press the physical/gesture **Home** button → pick **NovaOne Launcher** →
   **Always**, to make it your default home screen.
   (You can always switch back via Settings → Apps → Default apps → Home app.)

## About the animation engine — what it can and can't do, honestly

- **Opening an app**: real system-level scale/reveal animation, growing out
  of the exact icon you tapped (`ActivityOptions.makeScaleUpAnimation`).
  This is the actual public Android API for this effect.
- **"Closing" (returning to home)**: a true reverse of the *other app's own*
  close transition requires Android's privileged RemoteAnimation/QuickStep
  APIs — the same ones Samsung's system launcher and Lawnchair's Quickstep
  module use. Those require signature/system-app permissions that a normal
  sideloaded APK cannot get without root. So instead, this project animates
  the **launcher's own home screen** back in (fade + scale) the instant you
  return to it, timed off `onResume()`. It's the closest legitimate
  approximation to Lawnchair's engine without rooting the phone, and reads
  as a real closing transition even though technically it isn't reversing
  the other app's animation.

## Realistic scope

This is a genuine, from-scratch launcher — not a re-skin. It's deliberately
kept to the essentials (grid, dock, drawer, search, animations, uninstall)
so it's something you can build and run today. It doesn't yet include:
widgets, icon packs, folders, a settings screen, or drawer scroll-index
letters. All of those are addable incrementally — tell me which one you want
next and I'll build it into this same project.

If Gradle throws an error the first time you sync or build, paste the exact
error text back to me and I'll walk you through the fix.
