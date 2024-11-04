package pt.isec.tpamovfqj2324.ui.activity.Add

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import pt.isec.tpamovfqj2324.R
import pt.isec.tpamovfqj2324.ui.activity.InterestPlacesActivity
import pt.isec.tpamovfqj2324.ui.theme.Tpamovfqj2324Theme
import pt.isec.tpamovfqj2324.utils.Place
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddRateActivity : ComponentActivity() {
    private var db = Firebase.firestore

    companion object {
        const val PLACE_EXTRA = "place_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            Tpamovfqj2324Theme {
                Rate()
            }

        }
    }

    @Composable
    fun Rate()
    {
        val realPlace = intent.getSerializableExtra("PLACE_EXTRA") as? Place
        var titleText by remember { mutableStateOf("") }
        var rating by remember { mutableStateOf(0f) }
        var imageUri by remember{ mutableStateOf<Uri?>(null) }
        val bitmap = remember{ mutableStateOf<Bitmap?>(null) }
        val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()){
                uri : Uri? ->
            imageUri = uri
        }
        val context = LocalContext.current

        if (realPlace != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text( text = stringResource(R.string.comment) )
                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text(stringResource(R.string.title)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )


                Slider(
                    value = rating,
                    onValueChange = { newRating ->
                        rating = newRating
                    },
                    valueRange = 0f..3f,
                    steps = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                Text( text = stringResource(R.string.rating) +"$rating",  modifier = Modifier.padding(8.dp) )

                Spacer(modifier = Modifier.height(8.dp))

                imageUri?.let {
                    if (Build.VERSION.SDK_INT < 28) {
                        bitmap.value = MediaStore.Images
                            .Media.getBitmap(context.contentResolver, it)
                    } else {
                        val source = ImageDecoder.createSource(context.contentResolver, it)
                        bitmap.value = ImageDecoder.decodeBitmap(source)
                    }

                    bitmap.value?.let { btm ->
                        Image(
                            bitmap = btm.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .padding(20.dp)
                        )
                    }
                }

                Button(onClick = { launcher.launch("image/*")},colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                    Text(stringResource(R.string.pick_image))
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(onClick = {
                    addRate(titleText, rating, imageUri, realPlace.name)
                    val intent = Intent(context, InterestPlacesActivity::class.java)
                    startActivity(intent)
                },colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                    Text(stringResource(R.string.submit))
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    val intent = Intent(context, InterestPlacesActivity::class.java)
                    startActivity(intent)
                },colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                    Text(stringResource(R.string.back))
                }
            }
        }

    }


    private fun addRate(comment: String, rating: Float, imageUri: Uri?,place:String) {
        val storageRef = Firebase.storage.reference
        val imagesRef = storageRef.child("images/${imageUri?.lastPathSegment}")
        imageUri?.let {
            imagesRef.putFile(it) .addOnCompleteListener { task->
                if(task.isSuccessful){

                    imagesRef.downloadUrl.addOnCompleteListener {

                    }
                }else{
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }


        val useradded= FirebaseAuth.getInstance().currentUser!!.email
        val rateMap= hashMapOf(
            "Rating" to rating,
            "Comment" to comment,
            "Useradded" to useradded,
            "image" to imageUri,
            "Place" to place
        )
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        db.collection("rate").document(timeStamp).set(rateMap)
            .addOnSuccessListener { Toast.makeText(this,R.string.success, Toast.LENGTH_SHORT).show()}
    }

}