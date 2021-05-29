package android.app.tfmuoc001

import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import kotlinx.coroutines.*
import java.net.InetAddress
import java.util.*
import kotlin.experimental.and


class NetworkScannerActivity : AppCompatActivity() {

    private val listDevices: ArrayList<NetworkScannerListModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_networkscanner_layout)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Action bar
        supportActionBar?.setTitle(R.string.resultsName)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Progress bar
        val progressBar: ProgressBar = findViewById(R.id.networkscanner_progressbar);
        val progressBarText: TextView = findViewById(R.id.networkscanner_progressbar_text)
        progressBarText.text = getString(R.string.progressBarName)

        // Network scanner
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val ipAddress = sharedPreferences.getString("settings_networkscanner_ipaddress", "-").toString()
        val netMask = sharedPreferences.getString("settings_networkscanner_networkmask", "3").toString()

        val statusScanner: Deferred<Boolean> = CoroutineScope(Dispatchers.IO).async {
            val subnet = getSubnet(ipAddress, netMask)
            if (netMask == "1") {
                netMask8(subnet)
            }
            if (netMask == "2") {
                netMask16(subnet)
            }
            if (netMask == "3") {
                netMask24(subnet)
            }
            true
        }

        val statusProgressbar: Deferred<Boolean> = CoroutineScope(Dispatchers.Main).async {
            progressBar.visibility = View.VISIBLE
            progressBarText.visibility = View.VISIBLE
            true
        }

        CoroutineScope(Dispatchers.Main).launch {
            statusScanner.await()
            statusProgressbar.await()
            progressBar.visibility = View.GONE
            progressBarText.visibility = View.GONE
            val listview: ListView = findViewById(R.id.networkscanner_listview)
            val adapter = NetworkScannerListAdapter(this@NetworkScannerActivity, R.layout.listview_networkscanner_layout, listDevices)
            listview.adapter = adapter
            listview.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                val preferenceEditor = sharedPreferences.edit()
                preferenceEditor.putString("settings_portscanner_ipaddress", listDevices[position].ip)
                preferenceEditor.commit()
                startActivity(Intent(this@NetworkScannerActivity, MainPortScannerActivity::class.java))
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (android.R.id.home == item.itemId) {
            startActivity(Intent(this, MainNetworkScannerActivity::class.java))
            return true
        }
        return true
    }

    private fun getSubnet(ip: String, netMask: String): MutableList<String> {
        val ipAddress = InetAddress.getByName(ip).address
        val subnetByte = ByteArray(4)
        if (netMask == "1") {
            val netMask8 = byteArrayOf(0xFF.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte())
            for (byte in 0..3) {
                subnetByte[byte] = netMask8[byte] and ipAddress[byte]
            }
        }
        if (netMask == "2") {
            val netMask16 = byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0x00.toByte(), 0x00.toByte())
            for (byte in 0..3) {
                subnetByte[byte] = netMask16[byte] and ipAddress[byte]
            }
        }
        if (netMask == "3") {
            val netMask24 = byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0x00.toByte())
            for (byte in 0..3) {
                subnetByte[byte] = netMask24[byte] and ipAddress[byte]
            }
        }
        val subnet = mutableListOf<String>()
        for (byte in subnetByte) {
            subnet.add(byte.toUByte().toInt().toString())
        }
        return subnet
    }

    private fun netMask8(subnet: MutableList<String>) {
        for (netMask8 in 1..254) {
            for (netMask16 in 1..254) {
                for (netMask24 in 1..254) {
                    val scanIPstr = subnet[0] + "." + netMask8 + "." + netMask16 + "." + netMask24
                    val scanIP = InetAddress.getByName(scanIPstr)
                    val status = scanIP.isReachable(50)
                    if (status) {
                        var detail = ""
                        if (netMask24 == 1) {
                            detail = "[gateway]"
                        }
                        if (isDevice(scanIPstr)) {
                            detail = "[${getString(R.string.networkScannerThisDevice)}]"
                        }
                        listDevices.add(NetworkScannerListModel(scanIPstr, detail, "scan"))
                    }
                }
            }
        }
    }

    private fun netMask16(subnet: MutableList<String>) {
        for (netMask16 in 1..254) {
            for (netMask24 in 1..254) {
                val scanIPstr = subnet[0] + "." + subnet[1] + "." + netMask16 + "." + netMask24
                val scanIP = InetAddress.getByName(scanIPstr)
                val status = scanIP.isReachable(50)
                if (status) {
                    var detail = ""
                    if (netMask24 == 1) {
                        detail = "[gateway]"
                    }
                    if (isDevice(scanIPstr)) {
                        detail = "[${getString(R.string.networkScannerThisDevice)}]"
                    }
                    listDevices.add(NetworkScannerListModel(scanIPstr, detail, "scan"))
                }
            }
        }
    }

    private fun netMask24(subnet: MutableList<String>) {
        for (netMask24 in 1..254) {
            val scanIPstr = subnet[0] + "." + subnet[1] + "." + subnet[2] + "." + netMask24
            val scanIP = InetAddress.getByName(scanIPstr)
            val status = scanIP.isReachable(100)
            if (status) {
                var detail = ""
                if (netMask24 == 1) {
                    detail = "[gateway]"
                }
                if (isDevice(scanIPstr)) {
                    detail = "[${getString(R.string.networkScannerThisDevice)}]"
                }
                listDevices.add(NetworkScannerListModel(scanIPstr, detail, "scan"))
            }
        }
    }

    private fun isDevice(ip: String): Boolean {
        val wifiManager: WifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val localIP: String = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
        return localIP == ip
    }
}