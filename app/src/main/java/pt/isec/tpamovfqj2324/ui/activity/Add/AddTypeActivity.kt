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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import pt.isec.tpamovfqj2324.ui.activity.ContributeActivity
import pt.isec.tpamovfqj2324.ui.theme.Tpamovfqj2324Theme

class AddTypeActivity : ComponentActivity() {
    private var db= Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Tpamovfqj2324Theme {
                    AddTypeScreen()
                }

        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddTypeScreen() {
        var titleText by remember { mutableStateOf("") }
        var phraseText by remember { mutableStateOf("") }
        var imageUri by remember{ mutableStateOf<Uri?>(null) }
        val bitmap = remember{ mutableStateOf<Bitmap?>(null) }
        val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()){
                uri : Uri? ->
            imageUri = uri
        }

        val context = LocalContext.current
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = titleText,
                        onValueChange = { titleText = it },
                        label = { Text(stringResource(R.string.title)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))


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
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(8.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { launcher.launch("image/*") }) {
                        Text(stringResource(R.string.pick_image))

                    }

                    OutlinedTextField(
                        value = phraseText,
                        onValueChange = { phraseText = it },
                        label = { Text(stringResource(R.string.simple_text)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Spacer(modifier = Modifier.height(40.dp))


                    Button(onClick = {
                        savenewCategory(titleText, phraseText, imageUri)
                        val intent = Intent(this@AddTypeActivity, ContributeActivity::class.java)
                        startActivity(intent)
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                        Text(stringResource(R.string.submit))
                    }

                    Button(onClick = {
                        val intent = Intent(this@AddTypeActivity, ContributeActivity::class.java)
                        startActivity(intent)
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.back))
                    }
                }
            }
        }
    }

    private fun savenewCategory(titleText: String, phraseText: String, imageUri: Uri?) {
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


        val useradded= FirebaseAuth.getInstance().currentUser!!.uid
        val categoryMap= hashMapOf(
            "Name" to titleText,
            "Description" to phraseText,
            "Useradded" to useradded,
            "image" to imageUri
        )
        db.collection("category").document(titleText).set(categoryMap)
            .addOnSuccessListener { Toast.makeText(this,R.string.success, Toast.LENGTH_SHORT).show()}
    }

}
