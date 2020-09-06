package com.onuryahsi.firebaseexample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.onuryahsi.firebaseexample.model.MyNotification
import kotlinx.android.synthetic.main.item_notifications.view.*

class NotificationsAdapter() : RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {

    private var mList: List<MyNotification>
    lateinit var listener: OnItemClickListener

    init {
        mList = ArrayList()
    }

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textViewNotificationTitle: TextView = itemView.findViewById(R.id.item_notification_title)
        var textViewNotificationContent: TextView = itemView.item_notification_content
        var textViewNotificationMessageId: TextView = itemView.item_notification_message_id
        var textViewNotificationChannelId: TextView = itemView.item_notification_channed_id
        var textViewNotificationIsRead: TextView = itemView.item_notification_is_read

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_notifications, parent, false)
        return NotificationViewHolder(v);
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.textViewNotificationTitle.text = mList[position].title
        holder.textViewNotificationContent.text = mList[position].content
        holder.textViewNotificationMessageId.text = mList[position].message_id
        holder.textViewNotificationChannelId.text = mList[position].channel_id
        holder.textViewNotificationIsRead.text = mList[position].isRead.toString()

        holder.itemView.setOnClickListener(View.OnClickListener {
            listener.onItemClick(mList[position])
        })
    }

    fun setNotificationList(list: List<MyNotification>) {
        this.mList = list
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(myNotification: MyNotification)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}