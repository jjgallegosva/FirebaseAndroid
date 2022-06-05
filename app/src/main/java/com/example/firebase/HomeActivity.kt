package com.example.firebase

import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.firebase.databinding.ActivityAuthBinding
import com.example.firebase.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

enum class ProviderType{
    Basic,GOOGLE
}
class HomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        //setContentView(R.layout.activity_auth)
        binding.lifecycleOwner = this
        setContentView(R.layout.activity_home)
        val bundle = intent.extras

        val email = bundle?.getString("email")
        var provider = bundle?.getString("provider")
        Log.d("valor llegado", email.toString())
        setup(email.toString(),provider.toString())

        val pref: SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        pref.putString("email",email)
        pref.putString("provider",provider)
        pref.apply()
    }
    private fun setup(email:String,provider:String){
        title= "Inicio"
        ecetextdd2.text = email
        contrasena.text = provider

        cerrarSesion.setOnClickListener {
            Log.d("valor salido2","mira aca2")
            val pref: SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            pref.clear()
            pref.apply()
            Log.d("valor salido","mira aca")
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }


}