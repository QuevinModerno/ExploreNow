package pt.isec.tpamovfqj2324.ui.activity


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.isec.tpamovfqj2324.R
import pt.isec.tpamovfqj2324.ui.theme.Tpamovfqj2324Theme

class CreditsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Tpamovfqj2324Theme {
                    Credits()
                }

        }
    }

    @Composable
    fun Credits() {
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Quevin Moderno-2019135563\nFrancisco Reis-2019149992\nJoao Castro-2019128258")
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Aplicações Moveis")
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "3º Ano")
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Engenharia Informática")
                        Column(
                            modifier = Modifier
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(onClick = {
                                val intent = Intent(this@CreditsActivity, MainActivity::class.java)
                                startActivity(intent)

                            }, colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                                Text(text = stringResource(R.string.back))
                            }
                        }

                    }
                }
            }
           
        }
    }


    @Preview
    @Composable
    fun LanguageSelectionPreview() {
        Tpamovfqj2324Theme {
            Credits()
        }


    }
}