package pt.isec.tpamovfqj2324.utils

import java.io.Serializable

data class Place (
    val description: String = "",
    val name: String = "",
    val useradded:String = "",
    val category:String ="",
    val location:String= "",
    val approvals:Int = 0,
    val latitude:Double = 0.0,
    val longitude:Double = 0.0,
    val canVote:Boolean = true,
    val image:String = ""): Serializable
data class Location (
    val name: String = "",
    val description: String = "",
    val useradded:String = "",
    val approvals:Int = 0,
    val canVote:Boolean = true,
    val imageId:String = ""
)

data class Rating (
    val comment: String = "",
    val place: String = "",
    val rating: Double = 0.0,
    val useradded:String = "",
    val image:String = ""
)

fun getImageId(firestorePath: String): String? {
    val pathSegments = firestorePath.split("/")
    val lastSegment = pathSegments.lastOrNull()
    return lastSegment?.takeIf { it.matches(Regex("\\d+")) }
}
