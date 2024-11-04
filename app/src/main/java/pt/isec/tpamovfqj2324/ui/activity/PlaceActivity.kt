package pt.isec.tpamovfqj2324.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberImagePainter
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import pt.isec.tpamovfqj2324.R
import pt.isec.tpamovfqj2324.ViewModel.FirestoreViewModel
import pt.isec.tpamovfqj2324.ui.activity.Add.AddRateActivity
import pt.isec.tpamovfqj2324.ui.theme.Tpamovfqj2324Theme
import pt.isec.tpamovfqj2324.utils.Place
class PlaceActivity : ComponentActivity() {
    val viewModel : FirestoreViewModel by viewModels()

    companion object {
        const val PLACE_EXTRA = "place_name"
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
        val realPlace = intent.getSerializableExtra("PLACE_EXTRA") as? Place

        if(realPlace != null){
            val places = viewModel.state.value

            LazyColumn {
                items(places) { place ->

                    if (place != realPlace) {
                    } else {
                        val bitmapState = remember { mutableStateOf<Bitmap?>(null) }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            var img = place.image
                            val storageRef = "https://storage.googleapis.com/tpamovqfj2324.appspot.com/images/$img"
                            LoadImagePlace(url = storageRef)

                                    Text(
                                        text = place.name,
                                        fontWeight = FontWeight.Bold ,
                                        modifier = Modifier
                                            .fillMaxWidth(0.5f)
                                            .padding(horizontal = 16.dp)
                                    )

                                    Text(
                                        text = stringResource(R.string.description)+" ${place.description}",
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )


                                    Text(
                                        text = stringResource(R.string.user_added) +" ${place.useradded}",
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )

                                    Text(
                                        text = stringResource(R.string.category) +" ${place.category}",
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )

                                    Text(
                                        text = stringResource(R.string.location) +" ${place.location}",
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )

                                    Text(
                                        text = stringResource(R.string.latitude)+" ${place.latitude}"+stringResource(R.string.longitude)+" ${place.longitude}"+ stringResource(
                                            R.string.exact_location
                                        ),
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )

                            val localizacion= com.google.android.gms.maps.model.LatLng(
                                place.latitude,
                                place.longitude
                            )
                            val localizacionState= MarkerState(position = localizacion)
                            val cameraPositionState= rememberCameraPositionState{
                                position= CameraPosition.fromLatLngZoom(localizacion,10f)
                            }
                            GoogleMap( modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .padding(8.dp),
                                cameraPositionState=cameraPositionState) {
                                Marker(
                                    state=localizacionState,
                                    title= stringResource(R.string.localizacion),
                                )
                            }

                                Button(
                                    onClick = {
                                        val intent = Intent(context, AddRateActivity::class.java)
                                        intent.putExtra("PLACE_EXTRA", place)
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                ) {
                                    Text(text = stringResource(R.string.comment))
                                }

                            Button(onClick = {
                                val intent = Intent(context, ViewRateActivity::class.java)
                                intent.putExtra("PLACE_EXTRA", place)
                                startActivity(intent)
                            },colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                                Text(stringResource(R.string.view_comments))
                            }

                        }
                    }
                }
            }
        }
    }

    private @Composable
    fun LoadImagePlace(url: String) {
            Log.d("imagemurl:",url)
            val painter = rememberImagePainter(
                data = url,
                builder = {
                    crossfade(true)
                    placeholder(android.R.drawable.ic_menu_gallery) // Substitua pelo seu placeholder personalizado
                }
            )

            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .size(300.dp)
                    .padding(20.dp),
                contentScale = ContentScale.Fit,
                alpha = if (painter.state is AsyncImagePainter.State.Loading) 0.5f else 1.0f
            )
        }

}