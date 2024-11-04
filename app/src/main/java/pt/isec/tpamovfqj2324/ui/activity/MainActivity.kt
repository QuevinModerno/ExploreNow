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
import pt.isec.tpamovfqj2324.ui.theme.Tpamovfqj2324Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Tpamovfqj2324Theme {
                    MainContent()
                }
        }
    }
}

@Composable
fun MainContent() {
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = stringResource(R.string.amov_practical_work))
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    MyButton(text = stringResource(R.string.places_of_interest)) {
                        context.startActivity(Intent(context, LocalActivity::class.java))
                    }
                    MyButton(text = stringResource(R.string.contribute)) {
                        context.startActivity(Intent(context, ContributeActivity::class.java))
                    }
                    MyButton(text = stringResource(R.string.my_contributions)) {
                        context.startActivity(Intent(context, NotAprovedActivity::class.java))
                    }
                    MyButton(text = stringResource(R.string.credits)) {
                        context.startActivity(Intent(context, CreditsActivity::class.java))
                    }
                    MyButton(text = stringResource(R.string.change_language)) {
                        context.startActivity(Intent(context, LanguageActivity::class.java))
                    }
                }
            }
        }
    }
}


@Composable
fun MyButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
    ) {
        Text(text = text)
    }
}



@Preview(showBackground = true)
@Composable
fun MainContentPreview() {
    Tpamovfqj2324Theme {
        MainContent()
    }
}
