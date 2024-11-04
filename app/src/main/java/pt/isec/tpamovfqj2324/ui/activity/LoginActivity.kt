package pt.isec.tpamovfqj2324.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import pt.isec.tpamovfqj2324.R

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth= Firebase.auth

        val registertext:TextView=findViewById(R.id.textView2_register)

        registertext.setOnClickListener {
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }

        val loginButton:Button=findViewById(R.id.button_login)
        loginButton.setOnClickListener{
            performSignin()
        }




    }

    private fun performSignin() {
        val email=findViewById<EditText>(R.id.editText_email_login)
        val password=findViewById<EditText>(R.id.editText_Password_login)

        if(email.text.isEmpty()||password.text.isEmpty()){
            Toast.makeText(this, getString(R.string.fill), Toast.LENGTH_SHORT).show()
            return
        }
        val inputEmail=email.text.toString()
        val inputPassword = password.text.toString()

        auth.signInWithEmailAndPassword(inputEmail, inputPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent=Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(
                        baseContext,
                        getString(R.string.success),
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    Toast.makeText(
                        baseContext,
                        getString(R.string.authentication_failed),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
            .addOnFailureListener { Toast.makeText(
                baseContext,
                getString(R.string.authentication_failed),
                Toast.LENGTH_SHORT,
            ).show() }
    }
}