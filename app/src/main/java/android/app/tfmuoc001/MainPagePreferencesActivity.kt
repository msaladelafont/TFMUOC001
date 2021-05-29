package android.app.tfmuoc001

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class MainPagePreferencesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainpagepreferences_layout)

        // Action bar
        supportActionBar?.setTitle(R.string.menuSettings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // ListView
        val listview: ListView = findViewById(R.id.mainpagepreferences_listview)
        val list = mutableListOf<String>()
        list.add(getString(R.string.portScannerName))
        list.add(getString(R.string.networkScannerName))
        val adapter = MainPageListAdapter(this, R.layout.listview_mainpage_layout, list)
        listview.adapter = adapter
        listview.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            if (position == 0) {
                startActivity(Intent(this, NetworkScannerPreferencesActivity::class.java))
            }
            if (position == 1) {
                startActivity(Intent(this, PortScannerPreferencesActivity::class.java))
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (android.R.id.home == item.itemId) {
            startActivity(Intent(this, MainPageActivity::class.java))
            return true
        }
        return true
    }
}