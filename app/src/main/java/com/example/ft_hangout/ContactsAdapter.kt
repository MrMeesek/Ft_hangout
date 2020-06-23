package com.example.ft_hangout

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

class ContactsAdapter(contacts: ArrayList<Contact>, context: Context) :
    ArrayAdapter<Contact>(context, R.layout.contact_item, contacts) {

    private class ContactItemViewHolder {
        internal var contactImage: ImageView? = null
        internal var contactInfo: TextView? = null
        internal var contactPhoneNumber: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val viewHolder: ContactItemViewHolder
        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.contact_item, parent, false)
            viewHolder = ContactItemViewHolder()
            viewHolder.contactImage = view!!.findViewById(R.id.contactPicture) as ImageView
            viewHolder.contactInfo = view.findViewById(R.id.contactInfo) as TextView
            viewHolder.contactPhoneNumber = view.findViewById(R.id.contactPhoneNumber) as TextView
        } else {
            viewHolder = view.tag as ContactItemViewHolder
        }
        val contact = this.getItem(position) as Contact
        view.setOnClickListener(View.OnClickListener {
            contact.onShowAllInfo(context, position)
        })

        view.findViewById<ImageButton>(R.id.messageButton).setOnClickListener {
            contact.sendMessage(context, position)
            Log.d("DEBUG", "send message")
        }

        view.findViewById<ImageButton>(R.id.callButton).setOnClickListener {
            // val contact = this.getItem(position) as Contact
            contact.onContactCall(context)
        }


        //   val contact = getItem(position)

        if (contact.picture != Uri.EMPTY) {
            viewHolder.contactImage?.setImageURI(contact.picture)
        } else viewHolder.contactImage?.setImageResource(R.mipmap.default_contact_picture)
        //viewHolder.contactImage!!.setImageResource(R.mipmap.default_contact_picture)
        viewHolder.contactInfo!!.text = contact.name
        viewHolder.contactPhoneNumber!!.text = contact.phoneNumber.toString()
        view.tag = viewHolder
        return view
    }
}