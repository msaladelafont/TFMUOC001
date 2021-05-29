package android.app.tfmuoc001

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class NetworkScannerListAdapter(var cntxt: Context, var resource: Int, var items: ArrayList<NetworkScannerListModel>) : ArrayAdapter<NetworkScannerListModel>(cntxt, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(cntxt)
        val view: View = layoutInflater.inflate(resource, null)
        val ipView: TextView = view.findViewById(R.id.networkscanner_textview_ip)
        val detailView: TextView = view.findViewById(R.id.networkscanner_textview_detail)
        val scanView: TextView = view.findViewById(R.id.networkscanner_textview_scan)
        val element: NetworkScannerListModel = items[position]
        ipView.text = element.ip
        detailView.text = element.detail
        scanView.text = element.scan

        notifyDataSetChanged()

        return view
    }

}