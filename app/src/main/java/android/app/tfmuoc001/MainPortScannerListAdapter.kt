package android.app.tfmuoc001

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class MainPortScannerListAdapter(var cntxt: Context, var resource: Int, var items: List<MainPortScannerListModel>) : ArrayAdapter<MainPortScannerListModel>(cntxt, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(cntxt)
        val view: View = layoutInflater.inflate(resource, null)
        val titleView: TextView = view.findViewById(R.id.mainportscanner_textview_title)
        val detailView: TextView = view.findViewById(R.id.mainportscanner_textview_detail)
        val element: MainPortScannerListModel = items[position]
        titleView.text = element.title
        detailView.text = element.detail

        return view
    }

}