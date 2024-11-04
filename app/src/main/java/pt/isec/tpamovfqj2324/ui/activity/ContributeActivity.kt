package pt.isec.tpamovfqj2324.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.isec.tpamovfqj2324.R
import pt.isec.tpamovfqj2324.ui.activity.Add.AddInterestPlacesActivity
import pt.isec.tpamovfqj2324.ui.activity.Add.AddLocationActivity
import pt.isec.tpamovfqj2324.ui.activity.Add.AddTypeActivity
import pt.isec.tpamovfqj2324.ui.theme.Tpamovfqj2324Theme

class ContributeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Tpamovfqj2324Theme {
                    ContributeContent()
                }
            }
        }


    @Composable
    fun ContributeContent() {
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
                    ContributeButton(
                        label = stringResource(R.string.add_location),
                        onClick = {
                            context.startActivity(Intent(context, AddLocationActivity::class.java))
                        }
                    )

                    ContributeButton(
                        label = stringResource(R.string.add_type),
                        onClick = {
                            context.startActivity(Intent(context, AddTypeActivity::class.java))
                        }
                    )

                    ContributeButton(
                        label = stringResource(R.string.add_interest_place),
                        onClick = {
                            context.startActivity(
                                Intent(
                                    context,
                                    AddInterestPlacesActivity::class.java
                                )
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ContributeButton(
                        label = stringResource(R.string.back),
                        onClick = {
                            val intent = Intent(this@ContributeActivity, MainActivity::class.java)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun ContributeButton(label: String, onClick: () -> Unit) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(text = label)
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun ContributeContentPreview() {
        Tpamovfqj2324Theme {
            ContributeContent()
        }
    }
}
