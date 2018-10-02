package me.askfix.api.askfix

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import me.askfix.api.askfix.C.ACCESS_TOKEN
import me.askfix.api.askfix.C.SHARED_PREFS
import me.askfix.api.askfix.api.ApiService
import me.askfix.api.askfix.model.LoginListener
import me.askfix.api.askfix.model.LoginResponse

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setFields()
        initLoginButton()
        initDeleteButton()
    }

    private fun initLoginButton() {
        btnLogin.setOnClickListener {
            ApiService.login(etLogin.text.toString(), etPassword.text.toString(), object : LoginListener {
                override fun onLoginResponse(loginResponse: LoginResponse?) {
                    loginResponse?.getAccessToken()
                }

            })
        }
    }

    private fun initDeleteButton() {
        btnDeleteToken.setOnClickListener {

        }
    }

    private fun setFields() {
        if (getSharedPrefs().contains(ACCESS_TOKEN)) {
            tvStatus.text = getString(R.string.token_exists)
            etLogin.visibility = View.GONE
            etPassword.visibility = View.GONE
        } else {
            btnDeleteToken.visibility = View.GONE
            tvStatus.text = getString(R.string.token_doesnt_exist)
        }
    }

    private fun getSharedPrefs(): SharedPreferences {
        return getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
    }
}