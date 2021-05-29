package android.app.tfmuoc001

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat

class PortScannerListAdapter(var cntxt: Context, var resource: Int, var items: List<PortScannerListModel>) : ArrayAdapter<PortScannerListModel>(cntxt, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(cntxt)
        val view: View = layoutInflater.inflate(resource, null)
        val portView: TextView = view.findViewById(R.id.scanner_textview_title)
        val detailView: TextView = view.findViewById(R.id.scanner_textview_detail)
        val statusView: TextView = view.findViewById(R.id.scanner_textview_status)
        val element: PortScannerListModel = items[position]
        portView.text = element.port
        detailView.text = element.detail
        if (element.status == "open") {
            statusView.text = cntxt.getString(R.string.portScannerStatusOpen)
            statusView.setTextColor(ContextCompat.getColor(cntxt, R.color.green_400))
        }
        if (element.status == "closed") {
            statusView.text = cntxt.getString(R.string.portScannerStatusClosed)
            statusView.setTextColor(ContextCompat.getColor(cntxt, R.color.red_400))
        }
        if (element.status == "open|filtered") {
            statusView.text = cntxt.getString(R.string.portScannerStatusOpenFiltered)
            statusView.setTextColor(ContextCompat.getColor(cntxt, R.color.orange_400))
        }

        notifyDataSetChanged()

        return view
    }

}