package com.onuryahsi.firebaseexample.notification

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.onuryahsi.firebaseexample.R
import kotlinx.android.synthetic.main.activity_notification_detail.*

class NotificationDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_detail)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.setDisplayShowHomeEnabled(true);

        val intent: Intent = intent

        var text = ""

        text = intent.getStringExtra("title")
        text += "\n"
        text += intent.getStringExtra("content")
        text += "\n"
        text += intent.getStringExtra("message_id")
        text += "\n"
        text += intent.getStringExtra("channel_id")
        text += "\n"
        text += intent.getBooleanExtra("is_read", false).toString()

        text_view_notification_center.text = text

        notification_details_layout.setBackgroundColor(Color.parseColor("" + intent.getStringExtra("channel_id")))
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_details, menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val i = Intent()
        when (item!!.itemId) {
            android.R.id.home -> {

            }
            R.id.action_logout -> {
                i.putExtra("ACTION_TYPE", "LOGOUT")
                setResult(Activity.RESULT_OK, i)
            }
            R.id.action_delete -> {
                i.putExtra("ACTION_TYPE", "DELETE")
                setResult(Activity.RESULT_OK, i)
            }

        }
        finish()
        return super.onOptionsItemSelected(item)
    }

    private fun showMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}
