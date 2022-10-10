package uz.ayizor.vp.model


data class User(
    val user_id: String? = "",
    val user_first_name: String? = "",
    val user_last_name: String? = "",
    val user_company_name: String? = "",
    val user_phone_number: String? = "",
    val user_device_id: String? = "",
    val user_device_type: String? = "",
    val user_device_token: String? = "",
    val user_created_at: String? = "",
    val user_updated_at: String? = ""
)
