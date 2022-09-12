package uz.ayizor.vp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Location(
    val location_id: String? = null,
    val location_name: String? = null,
    val location_latitude: String? = null,
    val location_longitude: String? = null,
    val location_isDefault: Boolean? = null,
    val location_created_at: String? = null,
    val location_updated_at: String? = null
): Parcelable
