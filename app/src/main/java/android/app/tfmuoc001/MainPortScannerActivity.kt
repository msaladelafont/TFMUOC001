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

class MainPortScannerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainportscanner_layout)

        // Test preferences
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val ipAddress = sharedPreferences.getString("settings_portscanner_ipaddress", "-").toString()
        val portList = sharedPreferences.getString("settings_portlist", "-").toString()
        val allPorts = sharedPreferences.getBoolean("settings_all", false)
        val scanTCP = sharedPreferences.getBoolean("settings_tcp", false)
        val scanUDP = sharedPreferences.getBoolean("settings_udp", false)
        var configText = ""
        if (allPorts) {
            configText += getString(R.string.portScannerSettingsAllPorts).plus(", ")
        }
        if (scanTCP) {
            configText += getString(R.string.portScannerSettingsTCPScan).plus(", ")
        }
        if (scanUDP) {
            configText += getString(R.string.portScannerSettingsUDPScan).plus(", ")
        }
        configText = configText.dropLast(2)

        // List
        val listview: ListView = findViewById(R.id.mainportscanner_listview)
        val list = mutableListOf<MainPortScannerListModel>()
        list.add(MainPortScannerListModel(getString(R.string.portScannerSettingsAddressIP), ipAddress))
        list.add(MainPortScannerListModel(getString(R.string.portScannerSettingsListPorts), portList))
        list.add(MainPortScannerListModel(getString(R.string.menuSettings), configText))
        listview.adapter = MainPortScannerListAdapter(this, R.layout.listview_mainportscanner_layout, list)

        // Action bar
        val toolbar: Toolbar = findViewById(R.id.mainportscanner_actionbar)
        toolbar.setTitle(R.string.portScannerName)
        setSupportActionBar(toolbar)

        // Navigation bar
        val drawer: DrawerLayout = findViewById(R.id.mainportscanner_drawerlayout)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        val navview: NavigationView = findViewById(R.id.mainportscanner_navigationview)
        navview.setNavigationItemSelectedListener(this)

        // Floating button
        val fab: View = findViewById(R.id.mainportscanner_floatingactionbutton)
        fab.setOnClickListener {
            startActivity(Intent(this, PortScannerActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_mainportscanner_menu, menu)
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
        if (item.itemId == R.id.mainportscanner_settings_button) {
            startActivity(Intent(this, PortScannerPreferencesActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val drawer: DrawerLayout = findViewById(R.id.mainportscanner_drawerlayout)
        if (id == R.id.navigationview_portscanner_button) {
            drawer.closeDrawer(GravityCompat.START)
            return true
        }
        if (id == R.id.navigationview_networkscanner_button) {
            startActivity(Intent(this, MainNetworkScannerActivity::class.java))
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