package uz.seppuku.vp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Image(
    val image_id: String? = null,
    val image_url: String? = null,
    val image_created_at: String? = null,
    val image_updated_at: String? = null,
):Parcelable


