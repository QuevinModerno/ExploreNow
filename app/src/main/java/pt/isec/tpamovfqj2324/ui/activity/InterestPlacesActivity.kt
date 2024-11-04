package pt.isec.tpamovfqj2324.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.isec.tpamovfqj2324.R
import pt.isec.tpamovfqj2324.ViewModel.FirestoreViewModel
import pt.isec.tpamovfqj2324.ui.theme.Tpamovfqj2324Theme

class InterestPlacesActivity : ComponentActivity() {
    val viewModel : FirestoreViewModel by viewModels()

    companion object {
        const val LOCATION_NAME = "location_name"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            Tpamovfqj2324Theme {
                Place(viewModel, this)
            }
        }
    }

    @Composable
    fun Place(viewModel: FirestoreViewModel, context: ComponentActivity) {
        val value = intent.getStringExtra(LOCATION_NAME)
        var places = viewModel.state.value
        val userId = FirebaseAuth.getInstance().currentUser!!.email
        var db= Firebase.firestore

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                }) {
                    Text("Ordem Alfabética")
                }

                Button(onClick = {
                }) {
                    Text("Ordem de Distância")
                }
            }
            LazyColumn {
                items(places) { place ->

                    if (place.location != value) {
                    } else {
                        val bitmapState = remember { mutableStateOf<Bitmap?>(null) }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    val intent = Intent(context, PlaceActivity::class.java)
                                    intent.putExtra("PLACE_EXTRA", place)
                                    context.startActivity(intent)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {


                                    Text(
                                        text = place.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = place.description,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                var img = place.image
                                val storageRef = "https://storage.googleapis.com/tpamovqfj2324.appspot.com/images/$img"
                                LoadImage(url = storageRef.toString())
                            }


                            if (place.useradded == userId) {

                                    IconButton(
                                        onClick = {
                                            val documentReference = db.collection("Place").document(place.name)
                                            documentReference.delete()
                                                .addOnSuccessListener {
                                                    context.startActivity(Intent(context,LocalActivity::class.java))
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.w("Firestore", "Erro ao excluir documento", e)
                                                }
                                        },
                                        modifier = Modifier.size(36.dp),
                                        content = {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = Color.Red
                                            )
                                        }
                                    )
                            }

                            if(place.approvals < 2)
                            {
                                if(place.canVote) {
                                    Box {
                                        IconButton(
                                            onClick = {
                                                viewModel.setCliqueNoBotao(place,false)
                                                val documentReference =
                                                    db.collection("location").document(place.name)
                                                documentReference.update(
                                                    "approvals",
                                                    place.approvals + 1
                                                )
                                            },
                                            modifier = Modifier.size(36.dp),
                                            content = {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Accept",
                                                    tint = Color.Green
                                                )
                                            },
                                            enabled = place.canVote
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }


            Button(onClick = {
                val intent = Intent(context, LocalActivity::class.java)
                startActivity(intent)
            },colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.Black)) {
                Text(stringResource(R.string.back))
            }
        }
    }
}