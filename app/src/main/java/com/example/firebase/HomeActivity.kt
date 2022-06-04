package com.example.firebase

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.firebase.databinding.ActivityAuthBinding
import com.example.firebase.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType{
    Basic
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
        Log.d("valor llegado", email.toString())
        setup(email.toString())
    }
    private fun setup(email:String){
        title= "Inicio"
        binding.ecetextdd2.text = "hola amigo"

        binding.cerrarSesion.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }


}