package android.app.tfmuoc001

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainPageActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navview: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainpage_layout)

        // List
        val listview: ListView = findViewById(R.id.mainpage_listview)
        val list = mutableListOf<String>()
        list.add(getString(R.string.portScannerName))
        list.add(getString(R.string.networkScannerName))
        val adapter = MainPageListAdapter(this, R.layout.listview_mainpage_layout, list)
        listview.adapter = adapter
        listview.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            if (position == 1) {
                startActivity(Intent(this, MainPortScannerActivity::class.java))
            }
            if (position == 2) {
                startActivity(Intent(this, MainNetworkScannerActivity::class.java))
            }
        }
        val header = layoutInflater.inflate(R.layout.listview_header_layout, null)
        listview.addHeaderView(header)

        // Action bar
        val toolbar: Toolbar = findViewById(R.id.mainpage_actionbar)
        toolbar.setTitle(R.string.appName)
        setSupportActionBar(toolbar)

        // Navigation bar
        drawer = findViewById(R.id.mainpage_drawerlayout)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        navview = findViewById(R.id.mainpage_navigationview)
        navview.setNavigationItemSelectedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_mainpage_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.mainpage_settings_button) {
            startActivity(Intent(this, MainPagePreferencesActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val drawer: DrawerLayout = findViewById(R.id.mainpage_drawerlayout)
        if (id == R.id.navigationview_portscanner_button) {
            startActivity(Intent(this, MainPortScannerActivity::class.java))
            return true
        }
        if (id == R.id.navigationview_networkscanner_button) {
            startActivity(Intent(this, MainNetworkScannerActivity::class.java))
            return true
        }
        if (id == R.id.navigationview_home_button) {
            drawer.closeDrawer(GravityCompat.START)
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