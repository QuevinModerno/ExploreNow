package pt.isec.tpamovfqj2324.ViewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import pt.isec.tpamovfqj2324.utils.Place
import pt.isec.tpamovfqj2324.utils.Rating
import pt.isec.tpamovfqj2324.utils.getImageId
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RateViewModel: ViewModel(){
    val rate = mutableStateOf<List<Rating>>(emptyList())

    init{
        getRates()
    }
    private fun getRates(){
        viewModelScope.launch {
            rate.value = fetchRates()
        }

    }
}
suspend fun fetchRates(): List<Rating> {
    return suspendCoroutine { continuation ->
        val db = Firebase.firestore
        val rates = mutableListOf<Rating>()
        db.collection("rate").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    try {
                        // Acessar os campos do documento
                        val comment = document.getString("Comment")
                        val place_name = document.getString("Place")
                        val imageId = document.getString("image")?.let { getImageId(it) }
                        val rating = document.getDouble("Rating")
                        val user = document.getString("Useradded")

                        // Criar um objeto Location com os dados do documento
                        val rate = imageId?.let {
                            Rating( comment.orEmpty(),place_name.orEmpty(), rating?:0.0,user.orEmpty(),
                                it
                            )
                        }

                        // Adicionar à lista geral de localizações
                        if (rate != null) {
                            rates.add(rate)
                        }
                    } catch (e: Exception) {
                        // Lidar com erros ao acessar os campos do documento
                        Log.e("Firebase", "Error fetching document fields: $e")
                    }
                }
                continuation.resume(rates)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error getting documents: ", e)
                continuation.resume(rates)
            }
    }
}