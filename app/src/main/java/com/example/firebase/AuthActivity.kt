package com.example.firebase

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.firebase.databinding.ActivityAuthBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_auth.*


class AuthActivity : AppCompatActivity() {

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // handle the response in result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credencial = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credencial)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                showHome(account.email ?: "", ProviderType.GOOGLE)
                            } else {
                                showAlert()
                            }
                        }
                }
            } catch (e: ApiException) {
                showAlert()
            }
        }
    }
    private lateinit var binding : ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth)
        //setContentView(R.layout.activity_auth)
        binding.lifecycleOwner = this

        val analyticsc = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integracion de firebase completa")
        analyticsc.logEvent("InitScreen",bundle)
        setup()
        session()
    }
    private fun setup(){
        title = "Authentication"
        signUpButton.setOnClickListener{
            Log.d(ContentValues.TAG, "mira: ")
            if (binding.emailText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.emailText.text.toString(),
                    binding.passwordEditText.text.toString()).addOnCompleteListener{
                        if(it.isSuccessful){
                            showHome(it.result?.user?.email?:"",ProviderType.Basic)
                        }else{
                            showAlert()
                        }
                }
            }
        }
        loginButton.setOnClickListener{
            if (binding.emailText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.emailText.text.toString(),
                    binding.passwordEditText.text.toString()).addOnCompleteListener{
                    if(it.isSuccessful){
                        showHome(it.result?.user?.email?:"",ProviderType.Basic)
                    }else{
                        showAlert()
                    }
                }
            }
        }
        botonGoogle.setOnClickListener {
            val googleConf= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                            requestIdToken(getString(R.string.default_web_cliend_id)).
                            requestEmail().
                            build()
            val googleClient = GoogleSignIn.getClient(this,googleConf)
            //startActivityForResult(googleClient.signInIntent,GOOGLE_SIGN_IN)
            googleClient.signOut()
            launcher.launch(googleClient.signInIntent)
             //startActivityForResult(googleClient.signInIntent,GOOGLE_SIGN_IN)
        }
    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("aceptar",null)
        val dialog:AlertDialog = builder.create()
        dialog.show()
    }
    private fun showHome(email:String,provider:ProviderType){
        val homeIntent = Intent(this,HomeActivity::class.java).apply {
            putExtra("email",email)
            putExtra("provider",provider.name)
        }
        startActivity(homeIntent)
    }
    private fun session(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email",null)
        val provider = prefs.getString("provider",null)
        if (email !==null && provider !== null){
            showHome(email,ProviderType.valueOf(provider))
        }
    }
}

