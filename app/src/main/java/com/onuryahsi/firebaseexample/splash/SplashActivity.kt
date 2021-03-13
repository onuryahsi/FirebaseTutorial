package com.onuryahsi.firebaseexample.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.onuryahsi.firebaseexample.MainActivity
import com.onuryahsi.firebaseexample.R
import com.onuryahsi.firebaseexample.auth.AuthActivity
import com.onuryahsi.firebaseexample.auth.AuthViewModel

class SplashActivity : AppCompatActivity() {

    private var viewModel: ViewModel = AuthViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        viewModel = ViewModelProvider(this@SplashActivity).get(AuthViewModel::class.java)
        observeAuthenticationState()
    }

    private fun observeAuthenticationState() {

        (viewModel as AuthViewModel).authenticationState.observe(this, Observer { authenticationState ->
            when (authenticationState) {
                AuthViewModel.AuthenticationState.AUTHENTICATED -> {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }
                else -> {
                    startActivity(Intent(this@SplashActivity, AuthActivity::class.java))
                    finish()
                }
            }
        })
    }

}
