package android.app.tfmuoc001

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class MainPageListAdapter(private var cntxt: Context, var resource: Int, var items: List<String>) : ArrayAdapter<String>(cntxt, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(cntxt)
        val view: View = layoutInflater.inflate(resource, null)
        val titleView: TextView = view.findViewById(R.id.mainpage_textview_title)
        titleView.text = items[position]

        return view
    }
}