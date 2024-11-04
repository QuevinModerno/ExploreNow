package pt.isec.tpamovfqj2324.ui.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pt.isec.tpamovfqj2324.R
import pt.isec.tpamovfqj2324.ViewModel.RateViewModel
import pt.isec.tpamovfqj2324.ui.theme.Tpamovfqj2324Theme
import pt.isec.tpamovfqj2324.utils.Place
import java.util.Locale

class ViewRateActivity  : ComponentActivity() {
    val viewModel: RateViewModel by viewModels()

    companion object {
        const val PLACE_EXTRA = "place_name"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            Tpamovfqj2324Theme {
                ViewRate(viewModel, this)
            }

        }
    }

    @Composable
    fun ViewRate(viewModel: RateViewModel, context: ComponentActivity) {
        val realPlace = intent.getSerializableExtra("PLACE_EXTRA") as? Place
        if (realPlace != null) {
            val rates = viewModel.rate.value

            LazyColumn {
                items(rates){rate ->
                    if(rate.place == realPlace.name) {


                        val bitmapState = remember { mutableStateOf<Bitmap?>(null) }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {


                            var img = rate.image
                            val storageRef = "https://storage.googleapis.com/tpamovqfj2324.appspot.com/images/$img"
                            LoadImage(url = storageRef.toString())

                            Text(
                                text = rate.comment,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .padding(horizontal = 16.dp)
                            )

                            Text(
                                text = stringResource(R.string.user) +" ${rate.useradded}",
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            val formattedValue = String.format(Locale.US, "%.1f", rate.rating)
                            Text(
                                text = stringResource(R.string.rate) +" $formattedValue",
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
        else{
            Log.d("TEXT:", "NUTINTO")
        }
    }
}