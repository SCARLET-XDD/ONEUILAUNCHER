package com.mahin.oneuilauncher

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mahin.oneuilauncher.adapter.AppDrawerAdapter
import com.mahin.oneuilauncher.adapter.AppGridAdapter
import com.mahin.oneuilauncher.anim.AppLaunchAnimator
import com.mahin.oneuilauncher.anim.HomeReturnAnimator
import com.mahin.oneuilauncher.model.AppInfo
import com.mahin.oneuilauncher.repo.AppRepository
import com.mahin.oneuilauncher.widget.SwipeUpDrawerController

class MainActivity : AppCompatActivity() {

    private lateinit var allApps: List<AppInfo>
    private lateinit var drawerAdapter: AppDrawerAdapter
    private lateinit var drawerController: SwipeUpDrawerController
    private lateinit var homeRoot: View
    private var hasResumedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        homeRoot = findViewById(R.id.homeRoot)
        allApps = AppRepository.loadAllApps(this)

        setupHomeGrid()
        setupDock()
        setupAppDrawer()

        drawerController = SwipeUpDrawerController(
            homeRoot = homeRoot,
            drawerView = findViewById(R.id.drawerContainer)
        )
        drawerController.attach()
    }

    override fun onResume() {
        super.onResume()
        // "Closing" half of the animation engine - see AppLaunchAnimator's
        // doc comment for why this animates the home screen back in rather
        // than reversing the other app's own close animation.
        if (hasResumedOnce) {
            HomeReturnAnimator.playReturnTransition(homeRoot)
        }
        hasResumedOnce = true
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawerController.isDrawerOpen()) {
            drawerController.close()
        }
        // A launcher's home screen is never "closable" via back press.
    }

    private fun setupHomeGrid() {
        val homeGrid = findViewById<RecyclerView>(R.id.homeGrid)
        homeGrid.layoutManager = GridLayoutManager(this, 4)
        val homeApps = allApps.take(16)
        homeGrid.adapter = AppGridAdapter(
            apps = homeApps,
            onAppClick = { iconView, app -> AppLaunchAnimator.launch(this, iconView, app) },
            onAppLongClick = { anchor, app -> showAppOptionsMenu(anchor, app) }
        )
    }

    private fun setupDock() {
        val dock = findViewById<LinearLayout>(R.id.dock)
        val favorites = allApps.take(4)
        val inflater = layoutInflater
        favorites.forEach { app ->
            val itemView = inflater.inflate(R.layout.item_app, dock, false)
            val icon = itemView.findViewById<ImageView>(R.id.iconImage)
            val label = itemView.findViewById<TextView>(R.id.iconLabel)
            icon.setImageDrawable(app.icon)
            label.visibility = View.GONE
            itemView.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            itemView.setOnClickListener { AppLaunchAnimator.launch(this, icon, app) }
            itemView.setOnLongClickListener { showAppOptionsMenu(itemView, app); true }
            dock.addView(itemView)
        }
    }

    private fun setupAppDrawer() {
        val drawerGrid = findViewById<RecyclerView>(R.id.drawerGrid)
        drawerGrid.layoutManager = GridLayoutManager(this, 4)
        drawerAdapter = AppDrawerAdapter(
            apps = allApps,
            onAppClick = { iconView, app -> AppLaunchAnimator.launch(this, iconView, app) },
            onAppLongClick = { anchor, app -> showAppOptionsMenu(anchor, app) }
        )
        drawerGrid.adapter = drawerAdapter

        findViewById<EditText>(R.id.searchBox).addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                drawerAdapter.filter(s?.toString().orEmpty(), allApps)
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun showAppOptionsMenu(anchor: View, app: AppInfo) {
        val popup = PopupMenu(this, anchor)
        popup.menu.add(getString(R.string.app_info))
        popup.menu.add(getString(R.string.uninstall))
        popup.setOnMenuItemClickListener { item ->
            when (item.title) {
                getString(R.string.app_info) -> {
                    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:${app.packageName}")
                    })
                    true
                }
                getString(R.string.uninstall) -> {
                    startActivity(Intent(Intent.ACTION_DELETE).apply {
                        data = Uri.parse("package:${app.packageName}")
                    })
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
}
