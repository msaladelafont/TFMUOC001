package android.app.tfmuoc001

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView

class MainNetworkScannerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navview: NavigationView
    private lateinit var listview: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainnetworkscanner_layout)

        // NetworkPreferences
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val ipAddress = sharedPreferences.getString("settings_networkscanner_ipaddress", "-").toString()
        var netMask = sharedPreferences.getString("settings_networkscanner_networkmask", "3").toString()
        if (netMask == "1") {
            netMask = "8"
        }
        if (netMask == "2") {
            netMask = "16"
        }
        if (netMask == "3") {
            netMask = "24"
        }

        // ListView
        listview = findViewById(R.id.mainnetworkscanner_listview)
        val list = mutableListOf<MainNetworkScannerListModel>()
        list.add(MainNetworkScannerListModel(getString(R.string.portScannerSettingsAddressIP), "$ipAddress/$netMask"))
        listview.adapter = MainNetworkScannerListAdapter(this, R.layout.listview_mainnetworkscanner_layout, list)

        // ActionBar
        val toolbar: Toolbar = findViewById(R.id.mainnetworkscanner_actionbar)
        toolbar.setTitle(R.string.networkScannerName)
        setSupportActionBar(toolbar)

        // NavigationBar
        drawer = findViewById(R.id.mainnetworkscanner_drawerlayout)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        navview = findViewById(R.id.mainnetworkscanner_navigationview)
        navview.setNavigationItemSelectedListener(this)

        // FloatingButton
        val fab: View = findViewById(R.id.mainnetworkscanner_floatingactionbutton)
        fab.setOnClickListener {
            startActivity(Intent(this, NetworkScannerActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_mainnetworkscanner_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.mainnetworkscanner_settings_button) {
            startActivity(Intent(this, NetworkScannerPreferencesActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val drawer: DrawerLayout = findViewById(R.id.mainnetworkscanner_drawerlayout)
        if (id == R.id.navigationview_portscanner_button) {
            startActivity(Intent(this, MainPortScannerActivity::class.java))
            return true
        }
        if (id == R.id.navigationview_networkscanner_button) {
            drawer.closeDrawer(GravityCompat.START)
            return true
        }
        if (id == R.id.navigationview_home_button) {
            startActivity(Intent(this, MainPageActivity::class.java))
            return true
        }
        if (id == R.id.navigationview_settings_button) {
            startActivity(Intent(this, MainPagePreferencesActivity::class.java))
            return true
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}