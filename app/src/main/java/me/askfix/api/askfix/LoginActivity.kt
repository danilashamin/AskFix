package me.askfix.api.askfix

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import me.askfix.api.askfix.C.ACCESS_TOKEN
import me.askfix.api.askfix.C.JWT
import me.askfix.api.askfix.C.SHARED_PREFS
import me.askfix.api.askfix.C.UUID
import me.askfix.api.askfix.api.ApiService
import me.askfix.api.askfix.model.LoginListener
import me.askfix.api.askfix.model.LoginResponse
import retrofit2.Call

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setFields()
        initLoginButton()
        initDeleteButton()
        initStartButton()
    }

    private fun initStartButton() {
        btnStart.setOnClickListener {
            MainActivity.start(this)
        }
    }

    private fun initLoginButton() {
        btnLogin.setOnClickListener {
            ApiService.login(etLogin.text.toString(), etPassword.text.toString(), object : LoginListener {
                override fun onError(call: Call<LoginResponse>, t: Throwable) {
                    tvStatus.text = getString(R.string.invalid_values)
                }

                override fun onLoginResponse(loginResponse: LoginResponse?) {
                    putDataInSharedPrefs(loginResponse)
                    tvStatus.text = getString(R.string.token_successful)
                    setFields()
                }

            })
        }
    }

    private fun initDeleteButton() {
        btnDeleteToken.setOnClickListener {
            deleteToken()
            setFields()
        }
    }

    private fun setFields() {
        if (getSharedPrefs().contains(ACCESS_TOKEN)) {
            tvStatus.text = getString(R.string.token_exists)
            etLogin.visibility = View.GONE
            etPassword.visibility = View.GONE
            btnLogin.visibility = View.GONE
            btnStart.visibility = View.VISIBLE
            btnDeleteToken.visibility = View.VISIBLE
        } else {
            etLogin.visibility = View.VISIBLE
            etPassword.visibility = View.VISIBLE
            btnDeleteToken.visibility = View.GONE
            btnLogin.visibility = View.VISIBLE
            btnStart.visibility = View.GONE
            tvStatus.text = getString(R.string.token_doesnt_exist)
        }
    }

    private fun getSharedPrefs(): SharedPreferences {
        return getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
    }

    private fun putDataInSharedPrefs(loginResponse: LoginResponse?) {
        val editor = getSharedPrefs().edit()
        editor.putString(ACCESS_TOKEN, loginResponse?.getAccessToken())
        editor.putString(JWT, loginResponse?.getJWT())
        editor.putString(UUID, loginResponse?.getUUID())
        editor.apply()


    }

    private fun deleteToken() {
        val editor = getSharedPrefs().edit()
        editor.remove(ACCESS_TOKEN)
        editor.remove(JWT)
        editor.remove(UUID)
        editor.apply()
    }
}