package com.example.ft_hangout

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class SmsAdapter(/*private val*/ messages: ArrayList<Message>, context: Context) :
    ArrayAdapter<Message>(context, R.layout.activity_sms, messages) {

    private class SmsViewHolder {
        internal var sms: TextView? = null
        internal var date: TextView? = null
    }

    override fun add(`object`: Message?) {
        //.add(`object`!!)
        super.add(`object`)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val message = getItem(position) as Message
        Log.d("DEBUG", "ADAPTER GET VIEW ${message.receiver}, ${message.date}, ${message.body}")
        var view = convertView
        val viewHolder: SmsViewHolder
        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = if (message.receiver == Receiver.Contact) {
                inflater.inflate(R.layout.my_sms, parent, false)
            } else {
                inflater.inflate(R.layout.contact_sms, parent, false)
            }
            viewHolder = SmsViewHolder()
            viewHolder.sms = view!!.findViewById(R.id.message_body)
            viewHolder.date = view.findViewById(R.id.message_date)
        } else {
            viewHolder = view.tag as SmsViewHolder
        }
        viewHolder.sms!!.text = message.body
        viewHolder.date!!.text = message.formattedDate()
        view.tag = viewHolder
        return view
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getItemViewType(position: Int): Int {
        return (getItem(position) as Message).receiver.value
    }
}
