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

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = Firebase.auth

        val logintext: TextView =findViewById(R.id.textView2_login_now)

        logintext.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        val registerButton:Button=findViewById(R.id.button_register)
        registerButton.setOnClickListener{
            performSignUp()
        }

    }

    private fun performSignUp() {
        val email=findViewById<EditText>(R.id.editText_email_register)
        val password=findViewById<EditText>(R.id.editText_Password_register)

        if(email.text.isEmpty()||password.text.isEmpty()){
            Toast.makeText(this,R.string.fill,Toast.LENGTH_SHORT).show()
            return
        }
        val inputEmail=email.text.toString()
        val inputPassword = password.text.toString()

        auth.createUserWithEmailAndPassword(inputEmail,inputPassword)
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
            .addOnFailureListener { Toast.makeText(this,
                getString(R.string.error),Toast.LENGTH_SHORT).show() }
    }
}