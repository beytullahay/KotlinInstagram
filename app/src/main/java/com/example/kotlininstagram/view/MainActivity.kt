package com.example.kotlininstagram.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.kotlininstagram.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    lateinit var email: String
    lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        // kullanıcı var mı kontrol, varsa logini atlayacak.
        val currentUser = auth.currentUser
        if(currentUser != null) {
            var intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    fun signInClicked (view: View){

        email = binding.emailText.text.toString()
         password = binding.passwordText.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                // eğer başarılı olursa FeeadActivity'e gönderdik.
                val intent = Intent (this@MainActivity, FeedActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                // başarısız olursa hata mesajı yazdıracağız ekrana.
                Toast.makeText(this@MainActivity, it.localizedMessage, Toast.LENGTH_LONG).show()

            }
        } else {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_LONG).show()
        }

    }


    // KAYIT OL
    fun signUpClicked (view: View){

          email = binding.emailText.text.toString()
          password = binding.passwordText.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
                // eğer başarılı olursa FeeadActivity'e gönderdik.
                val intent = Intent (this@MainActivity, FeedActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                // başarısız olursa hata mesajı yazdıracağız ekrana.
                Toast.makeText(this@MainActivity, it.localizedMessage, Toast.LENGTH_LONG).show()

            }
        } else {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_LONG).show()
        }


    }
}