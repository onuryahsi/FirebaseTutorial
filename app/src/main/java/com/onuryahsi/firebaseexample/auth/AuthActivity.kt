package com.onuryahsi.firebaseexample.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.onuryahsi.firebaseexample.MainActivity
import com.onuryahsi.firebaseexample.R
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {

    private var viewModel: ViewModel = AuthViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        viewModel = ViewModelProvider(this@AuthActivity).get(AuthViewModel::class.java)
        textview_login.text = "AuthActivity"
        button_login.setOnClickListener {
            launchSignInIntent()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                Log.i(TAG, "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!")
                redirectToMainActivity()
            } else {
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }

    private fun launchSignInIntent() {

        val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.ThemeOverlay_AppCompat_Dark)
                        .build(),
                SIGN_IN_REQUEST_CODE
        )
    }

    private fun redirectToMainActivity() {
        startActivity(Intent(this@AuthActivity, MainActivity::class.java))
        finish()
    }

    companion object {
        private const val TAG: String = "AuthActivity"
        private const val SIGN_IN_REQUEST_CODE: Int = 0
    }
// https://github.com/googlecodelabs/android-kotlin-login/tree/master/app/src/main/java/com/example/android/firebaseui_login_sample
}
