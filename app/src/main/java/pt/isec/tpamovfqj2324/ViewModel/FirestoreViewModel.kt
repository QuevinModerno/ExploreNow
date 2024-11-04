package pt.isec.tpamovfqj2324.ViewModel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import pt.isec.tpamovfqj2324.utils.Location
import pt.isec.tpamovfqj2324.utils.Place
import pt.isec.tpamovfqj2324.utils.getImageId
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
class FirestoreViewModel: ViewModel(){
    val state = mutableStateOf<List<Place>>(emptyList())
    private val _listaStates = mutableStateListOf<Place>()
    init{
        getPlaces()
    }
    private fun getPlaces(){
        viewModelScope.launch {
            _listaStates.addAll(fetchPlaces())
            state.value = _listaStates.toList()
        }
    }

    fun setCliqueNoBotao(place: Place, foiClicado: Boolean) {
        val index = _listaStates.indexOf(place)
        if (index != -1) {
            _listaStates[index] = place.copy(canVote = foiClicado)
            state.value = _listaStates
        }
    }


}
suspend fun fetchPlaces(): List<Place> {
    return suspendCoroutine { continuation ->
        val db = Firebase.firestore
        val places = mutableListOf<Place>()
        db.collection("Place").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    try {
                        // Acessar os campos do documento
                        val name = document.getString("Name")
                        val description = document.getString("Description")
                        val imageId = document.getString("image")?.let { getImageId(it) }
                        val category = document.getString("category")
                        val usermail=document.getString("Useradded")
                        val location = document.getString("location")
                        val n =  document.getLong("Approvals")?.toInt() ?: 0
                        val latitude = document.getDouble("latitude")
                        val longitude = document.getDouble("longitude")

                        // Criar um objeto Location com os dados do documento
                        val place = imageId?.let {
                            Place( description.orEmpty(),name.orEmpty(), usermail.orEmpty(),category.orEmpty(), location.orEmpty(),n,latitude?: 0.0, longitude?: 0.0, true,
                                it
                            )
                        }

                        // Adicionar à lista geral de localizações
                        if (place != null) {
                            places.add(place)
                        }
                    } catch (e: Exception) {
                        // Lidar com erros ao acessar os campos do documento
                        Log.e("Firebase", "Error fetching document fields: $e")
                    }
                }
                continuation.resume(places)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error getting documents: ", e)
                continuation.resume(places)
            }
    }
}