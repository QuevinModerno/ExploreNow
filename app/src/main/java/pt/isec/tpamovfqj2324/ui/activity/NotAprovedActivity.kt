package pt.isec.tpamovfqj2324.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import pt.isec.tpamovfqj2324.ViewModel.FirestoreViewModel
import pt.isec.tpamovfqj2324.ViewModel.LocationViewModel
import pt.isec.tpamovfqj2324.ui.theme.Tpamovfqj2324Theme

class NotAprovedActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        val viewPlaces: FirestoreViewModel by viewModels()
        val viewLocals: LocationViewModel by viewModels()

        super.onCreate(savedInstanceState)
        setContent {
            Tpamovfqj2324Theme {
                MyContent(viewPlaces, viewLocals)
            }
        }
    }

    @Composable
    fun MyContent(viewPlaces: FirestoreViewModel, viewLocals: LocationViewModel) {
        val context = LocalContext.current
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

    }
}