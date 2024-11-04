package pt.isec.tpamovfqj2324.ui.activity

import android.app.LocaleManager
import android.content.Intent
import android.os.Bundle
import android.os.LocaleList
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
import pt.isec.tpamovfqj2324.ui.theme.Tpamovfqj2324Theme

class LanguageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Tpamovfqj2324Theme {

                    LanguageSelection()
                }


        }
    }

    @Composable
    fun LanguageSelection() {
        val context= LocalContext.current
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
                    LanguageButton(language = stringResource(R.string.portuguese)) {
                        context.getSystemService(LocaleManager::class.java)
                            .applicationLocales = LocaleList.forLanguageTags("pt")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    LanguageButton(language = stringResource(R.string.english)) {
                        context.getSystemService(LocaleManager::class.java)
                            .applicationLocales = LocaleList.forLanguageTags("en")
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                    Button(onClick = {
                        val intent = Intent(this@LanguageActivity, MainActivity::class.java)
                        startActivity(intent)

                    }) {
                        Text(text = stringResource(R.string.back))
                    }
                }
            }
        }
    }

    @Composable
    fun LanguageButton(language: String, onClick: () -> Unit) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)        ) {
            Text(text = language)
        }
    }

    @Preview
    @Composable
    fun LanguageSelectionPreview() {
        Tpamovfqj2324Theme {
            LanguageSelection()
        }


    }
}