package android.app.tfmuoc001

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.coroutines.*
import java.net.*

class PortScannerActivity : AppCompatActivity() {

    private val listPort: ArrayList<PortScannerListModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portscanner_layout)

        // Action bar
        supportActionBar?.setTitle(R.string.resultsName)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Port list database
        val portListTCP = mutableListOf<PortListModel>()
        val portListUDP = mutableListOf<PortListModel>()
        val csvfile = assets.open("service-names-port-numbers.csv")
        csvReader().open(csvfile) {
            readAllAsSequence().forEach { row ->
                if (row[2] == "tcp") {
                    portListTCP.add(PortListModel(row[1], row[0], row[2], row[3]))
                }
                if (row[2] == "udp") {
                    portListUDP.add(PortListModel(row[1], row[0], row[2], row[3]))
                }
            }
        }

        // Progress bar
        val progressBar: ProgressBar = findViewById(R.id.portscanner_progressbar);
        val progressBarText: TextView = findViewById(R.id.portscanner_progressbar_text)
        progressBarText.text = getString(R.string.progressBarName)

        // Port scanner
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val ipAddress = sharedPreferences.getString("settings_portscanner_ipaddress", "-").toString()
        val portList = sharedPreferences.getString("settings_portlist", "-").toString()
        val allPorts = sharedPreferences.getBoolean("settings_all", false)
        val scanTCP = sharedPreferences.getBoolean("settings_tcp", false)
        val scanUDP = sharedPreferences.getBoolean("settings_udp", false)

        val statusScanner: Deferred<Boolean> = CoroutineScope(Dispatchers.IO).async {
            if (allPorts) {
                for (port in 1..65536) {
                    if (scanTCP) {
                        scannerTCP(ipAddress, port, portListTCP)
                    }
                    if (scanUDP) {
                        scannerUDP(ipAddress, port, portListUDP)
                    }
                }
            } else {
                val strList = portList.split(",").toTypedArray()
                val list = getPortList(strList)
                list.sort()
                for (port in list) {
                    if (scanTCP) {
                        scannerTCP(ipAddress, port, portListTCP)
                    }
                    if (scanUDP) {
                        scannerUDP(ipAddress, port, portListUDP)
                    }
                }
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
            val sortedPortList = listPort.sortedBy { PortScannerListModel -> PortScannerListModel.port.toInt() }
            val listview: ListView = findViewById(R.id.portscanner_listview)
            val adapter = PortScannerListAdapter(this@PortScannerActivity, R.layout.listview_portscanner_layout, sortedPortList)
            listview.adapter = adapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (android.R.id.home == item.itemId) {
            startActivity(Intent(this, MainPortScannerActivity::class.java))
            return true
        }
        return true
    }

    private fun getPortList(list: Array<String>): MutableList<Int> {
        val portlist = mutableListOf<Int>()
        for (port in list) {
            if (port.contains("-")) {
                val range = port.split("-")
                for (element in range.elementAt(0).toInt()..range.elementAt(1).toInt()) {
                    portlist.add(element)
                }
            } else {
                portlist.add(port.toInt())
            }
        }
        return portlist
    }

    private fun scannerTCP(ip: String, port: Int, portList: MutableList<PortListModel>) {
        val statusTCP = try {
            val socket = Socket(ip, port)
            socket.isConnected
        } catch (e: Exception) {
            false
        }
        if (statusTCP) {
            val model: PortListModel? = portList.find { it.port == port.toString() }
            val description = model?.description ?: "-"
            val service = model?.service ?: "-"
            val detail = "[tcp] $service - $description"
            listPort.add(PortScannerListModel(port.toString(), detail, "open"))
        }
    }

    private fun scannerUDP(ip: String, port: Int, portList: MutableList<PortListModel>) {
        val statusUDP = try {
            val socketSender = DatagramSocket()
            val message = "hello world".toByteArray()
            val packetSender = DatagramPacket(message, message.size, InetAddress.getByName(ip), port)
            socketSender.soTimeout = 100
            socketSender.connect(InetAddress.getByName(ip), port)
            socketSender.send(packetSender)
            socketSender.receive(packetSender)
            socketSender.close()
            "open"
        } catch (e: PortUnreachableException) {
            "closed"
        } catch (e: SocketTimeoutException) {
            "open|filtered"
        } catch (e: Exception) {
            "other"
        }
        if (statusUDP == "open") {
            val model: PortListModel? = portList.find { it.port == port.toString() }
            val description = model?.description ?: "-"
            val service = model?.service ?: "-"
            val detail = "[udp] $service - $description"
            listPort.add(PortScannerListModel(port.toString(), detail, statusUDP))
        }
        if (statusUDP == "open|filtered") {
            val model: PortListModel? = portList.find { it.port == port.toString() }
            val description = model?.description ?: "-"
            val service = model?.service ?: "-"
            val detail = "[udp] $service - $description"
            listPort.add(PortScannerListModel(port.toString(), detail, statusUDP))
        }
    }
}