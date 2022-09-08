package uz.ayizor.vp.model


data class User(
    val user_id: String? = null,
    val user_first_name: String? = null,
    val user_last_name: String? = null,
    val user_company_name: String? = null,
    val user_phone_number: String? = null,
    val user_device_id: String? = null,
    val user_device_token: String? = null,
    val user_location: ArrayList<Location>? = null,
    val user_notifications: ArrayList<Notification>? = null,
    val user_created_at: String? = null,
    val user_updated_at: String? = null
)
