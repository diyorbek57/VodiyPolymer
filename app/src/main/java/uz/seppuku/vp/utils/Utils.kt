package uz.seppuku.vp.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import kotlinx.serialization.descriptors.PrimitiveKind
import uz.seppuku.vp.R
import uz.seppuku.vp.databinding.ItemEmptyStateBinding
import uz.seppuku.vp.model.helpermodel.CustomLocation
import uz.seppuku.vp.model.helpermodel.ScreenSize
import uz.seppuku.vp.utils.Extensions.show
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


object Utils {

    @RequiresApi(Build.VERSION_CODES.M)
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Logger.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Logger.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Logger.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }

    @SuppressLint("HardwareIds")
    fun getDeviceID(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun screenSize(context: Application): ScreenSize {
        val displayMetrics = DisplayMetrics()
        val windowsManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
        windowsManager.defaultDisplay.getMetrics(displayMetrics)
        val deviceWidth = displayMetrics.widthPixels
        val deviceHeight = displayMetrics.heightPixels
        return ScreenSize(deviceWidth, deviceHeight)
    }

    fun dialogDouble(context: Context?, title: String?, callback: DialogListener) {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.view_dialog_double)
        dialog.setCanceledOnTouchOutside(true)
        val d_title = dialog.findViewById<TextView>(R.id.d_title)
        val d_confirm = dialog.findViewById<TextView>(R.id.d_confirm)
        val d_cancel = dialog.findViewById<TextView>(R.id.d_cancel)
        d_title.text = title
        d_confirm.setOnClickListener {
            dialog.dismiss()
            callback.onCallback(true)
        }
        d_cancel.setOnClickListener {
            dialog.dismiss()
            callback.onCallback(false)
        }
        dialog.show()
    }

    fun dialogSingle(context: Context?, title: String?, callback: DialogListener) {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.view_dialog_single)
        dialog.setCanceledOnTouchOutside(true)
        val d_title = dialog.findViewById<TextView>(R.id.d_title)
        val d_confirm = dialog.findViewById<TextView>(R.id.d_confirm)
        d_title.text = title
        d_confirm.setOnClickListener {
            dialog.dismiss()
            callback.onCallback(true)
        }
        dialog.show()
    }

    fun validEditText(editText: EditText?, errorText: String): Boolean {
        val fullName: String = editText?.text.toString()
        return if (fullName.isEmpty()) {
            if (editText != null) {
                editText.error = errorText
            }
            false
        } else {
            if (editText != null) {
                editText.error = null
            }
            true
        }
    }

    fun getCoordinateName(context: Context, latitude: Double, longitude: Double): CustomLocation? {
        try {


            val geocoder = Geocoder(context, Locale.getDefault());
            val addresses = geocoder.getFromLocation(latitude, longitude, 1);
            val address =
                addresses?.get(0)
                    ?.getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

            val city = addresses?.get(0)?.locality
            val state = addresses?.get(0)?.adminArea
            val country = addresses?.get(0)?.countryName
            val postalCode = addresses?.get(0)?.postalCode
            val knownName = addresses?.get(0)?.featureName // Only if available else return NULL

            return CustomLocation(address, city, state, country, postalCode, knownName)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun replaceWords(word: String, replace: String?, newWord: String): String? {
        return word.replace(replace!!, newWord)
    }

    fun isValidPassword(password: String?): Boolean {
        password?.let {
            val passwordPattern =
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
            val passwordMatcher = Regex(passwordPattern)

            return passwordMatcher.find(password) != null
        } ?: return false
    }

    fun getTimeAgo(created_at: String): String {
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        return try {
            val time: Long = sdf.parse(created_at)!!.time
            val now = System.currentTimeMillis()
            val ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS)
            ago.toString()
        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }
    }

    fun formatingDateAsTime(created_at: String): String {
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
        return try {
            val date = df.parse(created_at)
            val dateString = DateFormat.format("HH:mm", date).toString()
            dateString.toString()
        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }
    }

    fun getCurrentTime(): String {
        val formatted =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
        return formatted.toString()
    }

    fun getUUID(): String {
        val uuid: String = UUID.randomUUID().toString().replace("-", "");
        return uuid
    }


    fun showSuccessToast(activity: Activity, title: String, message: String) {
        MotionToast.darkToast(
            activity,
            title,
            message,
            MotionToastStyle.SUCCESS,
            Gravity.CENTER_HORIZONTAL,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(activity, R.font.generalsans_semibold)
        )
    }

    fun showErrorToast(activity: Activity, title: String, message: String) {
        MotionToast.darkToast(
            activity,
            title,
            message,
            MotionToastStyle.ERROR,
            Gravity.CENTER_HORIZONTAL,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(activity, R.font.generalsans_semibold)
        )
    }

    fun showWarningToast(activity: Activity, title: String, message: String) {
        MotionToast.darkToast(
            activity,
            title,
            message,
            MotionToastStyle.WARNING,
            Gravity.CENTER_HORIZONTAL,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(activity, R.font.generalsans_semibold)
        )
    }

    fun showInfoToast(activity: Activity, title: String, message: String) {
        MotionToast.darkToast(
            activity,
            title,
            message,
            MotionToastStyle.INFO,
            Gravity.CENTER_HORIZONTAL,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(activity, R.font.generalsans_semibold)
        )
    }

    fun setLocaleLanguage(activity: Activity, languageCode: String?): Configuration {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        return config
    }

    //    fun prettyCount(number: Number): String? {
//        val suffix = arrayOf(' ', "ming", "", 'B', 'T', 'P', 'E')
//        val numValue = number.toLong()
//        val value = Math.floor(Math.log10(numValue.toDouble())).toInt()
//        val base = value / 3
//        return if (value >= 3 && base < suffix.size) {
//            DecimalFormat("#0.0").format(
//                numValue / Math.pow(
//                    10.0,
//                    (base * 3).toDouble()
//                )
//            ) + suffix[base]
//        } else {
//            DecimalFormat("#,##0").format(numValue)
//        }
//    }
    fun addSpaceToNumber(number: String): String {
        val dec = DecimalFormat("###,###,###,###,###", DecimalFormatSymbols(Locale.ENGLISH))
        return dec.format(number.toInt()).replace(",", " ")
    }

    fun showLoading(progressBar: ProgressBar) {
        progressBar.visibility = View.VISIBLE
    }

    fun hideLoading(progressBar: ProgressBar) {
        progressBar.visibility = View.GONE
    }

    fun showEmptyState(
        emptyState: ItemEmptyStateBinding,
        visibility: Int,
        image: Int,
        title: String,
        message: String
    ) {
        emptyState.llEmpty.visibility = visibility
        emptyState.ivImageEmptyState.setImageResource(image)
        emptyState.tvTitleEmptyState.text = title
        emptyState.tvMessageEmptyState.text = message
    }

}

interface DialogListener {
    fun onCallback(isChosen: Boolean)
}
