package pt.isec.tpamovfqj2324.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.isec.tpamovfqj2324.R
import pt.isec.tpamovfqj2324.ViewModel.FirestoreViewModel
import pt.isec.tpamovfqj2324.ViewModel.LocationViewModel
import pt.isec.tpamovfqj2324.ui.theme.Tpamovfqj2324Theme
import pt.isec.tpamovfqj2324.utils.Place


class LocalActivity : ComponentActivity() {

    val viewModel : LocationViewModel by viewModels()
    val viewPlace : FirestoreViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Tpamovfqj2324Theme {
                LocationItem(viewModel, viewPlace,this)
            }
        }
    }
}

@Composable
fun LocationItem(viewModel:LocationViewModel ,viewPlace : FirestoreViewModel, context : ComponentActivity) {

    val locals = viewModel.local.value
    val places = viewPlace.state.value
    val userId = FirebaseAuth.getInstance().currentUser!!.uid
    var db = Firebase.firestore

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
    LazyColumn {
        items(locals) { location ->
            val bitmapState = remember { mutableStateOf<Bitmap?>(null) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        val intent = Intent(context, InterestPlacesActivity::class.java)
                        intent.putExtra(InterestPlacesActivity.LOCATION_NAME, location.name)
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
                            text = location.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = location.description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    var img = location.imageId
                    val storageRef = "https://storage.googleapis.com/tpamovqfj2324.appspot.com/images/$img"
                    LoadImage(url = storageRef.toString())


                    if (location.useradded == userId) {

                        if (placesIsEmpty(location.name, places, userId)) {

                            IconButton(
                                onClick = {
                                    val documentReference =
                                        db.collection("location").document(location.name)
                                    documentReference.delete()
                                        .addOnSuccessListener {
                                            context.startActivity(
                                                Intent(
                                                    context,
                                                    LocalActivity::class.java
                                                )
                                            )
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
                    }

                    if(location.approvals < 2)
                    {
                        if(location.canVote) {
                            Box {
                                IconButton(
                                    onClick = {
                                        viewModel.setCliqueNoBotao(location,false)
                                        val documentReference =
                                            db.collection("location").document(location.name)
                                        documentReference.update(
                                            "approvals",
                                            location.approvals + 1
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
                                    enabled = location.canVote
                                )
                            }
                        }
                    }

                }
            }
        }
    }
    Button(
        onClick = {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        },
        colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.Black)
    ) {
        Text(stringResource(R.string.back))
    }
}

}

@Composable
fun LoadImage(url: String) {
    Log.d("imagemurl:",url)
    val painter = rememberImagePainter(
        data = url,
        builder = {
            crossfade(true)
            placeholder(android.R.drawable.ic_menu_gallery)
        }
    )

    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .size(100.dp)
            .padding(20.dp),
        contentScale = ContentScale.Fit,
        alpha = if (painter.state is AsyncImagePainter.State.Loading) 0.5f else 1.0f
    )
}

private fun placesIsEmpty(location:String, places:List<Place>, userId:String) : Boolean
{
    places.forEach { place ->
        if (place.location == location) {return false}
    }

    return true;
}



