package uz.seppuku.vp.helper.quantitizer

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import uz.seppuku.vp.databinding.QuantitizerBinding
import uz.seppuku.vp.R
import uz.seppuku.vp.utils.Extensions.hideKeyboard
@SuppressLint("CustomViewStyleable")
class Quantitizer @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attributeSet, defStyle) {

    private var listener: QuantitizerListener? = null
    private val binding = QuantitizerBinding.inflate(LayoutInflater.from(context), this, true)
    private var currentValue: Int = 0


    private var _minValue: Int = 1
    private var _maxValue: Int = Int.MAX_VALUE
    private var _isMinValue: Boolean = false
    private var _isCartPage: Boolean = false
    private var _isReadOnly: Boolean = false

    var minValue: Int
        get() = _minValue
        set(value) {
            if (value >= currentValue) {
                binding.edtCount.setText(value.toString())
                currentValue = value
                _minValue = value
            } else {
                _minValue = value
                currentValue = value
            }
        }

    var maxValue: Int
        get() = _maxValue
        set(value) {
            _maxValue = value
        }

    var value: Int
        get() = currentValue
        set(value) {
            currentValue = value
            binding.edtCount.setText(value.toString())
        }

    var isMinValue: Boolean
        get() = _isMinValue
        set(value) {
           _isMinValue = value
        }

    var isCartPage: Boolean
        get() = _isCartPage
        set(value) {
            _isCartPage = value
        }
    var isReadOnly: Boolean
        get() = _isReadOnly
        set(value) {
            isReadOnly(value)
        }

    init {
        val a = context.obtainStyledAttributes(
            attributeSet, R.styleable.Quantitizer, defStyle, 0
        )

        minValue = a.getInteger(
            R.styleable.Quantitizer_minValue, 0
        )

        maxValue = a.getInteger(
            R.styleable.Quantitizer_maxValue, Int.MAX_VALUE
        )

        value = a.getInteger(
            R.styleable.Quantitizer_value, 0
        )

        /*decrease*/
        binding.ivMinus.setOnClickListener {
            hideKeyboard()
            if (minValue >= currentValue) {
                //do nothing
            } else {
                doDec()
                showCurrentValue(currentValue)

            }
        }

        /*increase*/
        binding.ivPlus.setOnClickListener {
            hideKeyboard()
            if (maxValue <= currentValue) {
                //do  nothing
            } else {
                doInc()
                showCurrentValue(currentValue)
            }
        }

        /*make edit text cursor visible when clicked*/
        binding.edtCount.setOnClickListener {
            if (_isReadOnly.not()) {
                binding.edtCount.isCursorVisible = true
            }
        }

        binding.edtCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentValue = if (s.toString().isNotEmpty() || s.toString() != "") {
                    val value = Integer.parseInt(s.toString())
                    listener?.onValueChanged(value)
                    value
                } else {
                    0
                }
            }

            override fun afterTextChanged(s: Editable?) {
                val value = s.toString().toIntOrNull()
                if (s.toString().isEmpty()) {
                    //do nothing
                } else if (value!! < minValue && s.toString().isBlank().not()) {
                    binding.edtCount.text =
                        Editable.Factory.getInstance().newEditable(minValue.toString())
                    currentValue = minValue
                    // Toast.makeText(context, "Min value is $minValue", Toast.LENGTH_SHORT).show()
                } else if (value > maxValue && s.toString().isBlank().not()) {
                    binding.edtCount.text =
                        Editable.Factory.getInstance().newEditable(minValue.toString())
                    currentValue = minValue
                    // Toast.makeText(context, "Max value is $maxValue", Toast.LENGTH_SHORT).show()

                }
            }
        })

        /*TypedArrays are heavyweight objects that should be recycled immediately
         after all the attributes you need have been extracted.*/
        a.recycle()
    }

    private fun showCurrentValue(currentValue: Int) {
        if (isCartPage){
            isMinValue = checkMinValue()
        }
        binding.edtCount.setText(currentValue.toString())
    }

    private fun doInc() {
        binding.edtCount.isCursorVisible = false // hide cursor if it's visible
        val increasedValue: Int = currentValue.inc()
        currentValue = increasedValue

    }

    private fun doDec() {
        binding.edtCount.isCursorVisible = false  // hide cursor if it's visible
        val decreasedValue: Int = currentValue.dec()
        currentValue = decreasedValue

    }

    private fun checkMinValue(): Boolean {

        return if (currentValue == minValue) {
            binding.ivMinus.setImageResource(R.drawable.ic_trash_line)
            true
        } else {
            binding.ivMinus.setImageResource(R.drawable.ic_minus_line)
            false
        }

    }

    fun setQuantitizerListener(listener: QuantitizerListener) {
        this.listener = listener
    }

    private fun isReadOnly(isReadOnly: Boolean): Boolean {
        return if (isReadOnly) {//if user wants read only, then set edittext enabled to false
            binding.edtCount.apply {
                isFocusableInTouchMode = false
                isCursorVisible = false
                inputType = InputType.TYPE_NULL
            }
            true
        } else {//else set enabled to true
            binding.edtCount.apply {
                isFocusableInTouchMode = true
                isCursorVisible = true
            }
            false
        }
    }
//    fun setIconWidthAndHeight(width: Int, height: Int) {
//        val density = Resources.getSystem().displayMetrics.density
//
//        binding.decreaseIb.requestLayout()
//        binding.decreaseIb.layoutParams.width = width * density.toInt()
//        binding.decreaseIb.layoutParams.height = height * density.toInt()
//
//        binding.increaseIb.requestLayout()
//        binding.increaseIb.layoutParams.width  = width * density.toInt()
//        binding.increaseIb.layoutParams.height  = height * density.toInt()
//    }
//
//    fun setPlusIconBackgroundColor(@ColorRes colorRes: Int) {
//        binding.increaseIb.backgroundTintList = resources.getColorStateList(colorRes, context.theme)
//    }
//
//    fun setPlusIconBackgroundColor(colorString: String) {
//        binding.increaseIb.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorString))
//    }
//
//    fun setPlusIconBackgroundColor(colorStateList: ColorStateList) {
//        binding.increaseIb.backgroundTintList = colorStateList
//    }
//
//    fun setPlusIconBackgroundColorInt(@ColorInt colorInt: Int) {
//        binding.increaseIb.backgroundTintList = ColorStateList.valueOf(colorInt)
//    }
//
//    fun setMinusIconBackgroundColor(@ColorRes colorRes: Int) {
//        binding.decreaseIb.backgroundTintList = resources.getColorStateList(colorRes, context.theme)
//    }
//
//    fun setMinusIconBackgroundColor(colorString: String) {
//        binding.decreaseIb.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorString))
//    }
//
//    fun setMinusIconBackgroundColor(colorStateList: ColorStateList) {
//        binding.decreaseIb.backgroundTintList = colorStateList
//    }
//
//    fun setMinusIconBackgroundColorInt(@ColorInt colorInt: Int) {
//        binding.decreaseIb.backgroundTintList = ColorStateList.valueOf(colorInt)
//    }
//
//    fun setPlusIconColor(@ColorRes colorRes: Int) {
//        binding.increaseIb.imageTintList = resources.getColorStateList(colorRes, context.theme)
//    }
//
//    fun setPlusIconColor(colorString: String) {
//        binding.increaseIb.imageTintList = ColorStateList.valueOf(Color.parseColor(colorString))
//    }
//
//    fun setPlusIconColorInt(colorStateList: ColorStateList) {
//        binding.increaseIb.imageTintList = colorStateList
//    }
//
//    fun setPlusIconColorInt(@ColorInt colorInt: Int) {
//        binding.increaseIb.imageTintList = ColorStateList.valueOf(colorInt)
//    }
//
//    fun setMinusIconColor(@ColorRes colorRes: Int) {
//        binding.decreaseIb.imageTintList = resources.getColorStateList(colorRes, context.theme)
//    }
//
//    fun setMinusIconColor(colorString: String) {
//        binding.decreaseIb.imageTintList = ColorStateList.valueOf(Color.parseColor(colorString))
//    }
//
//    fun setMinusIconColor(colorStateList: ColorStateList) {
//        binding.decreaseIb.imageTintList = colorStateList
//    }
//
//    fun setMinusIconColorInt(@ColorInt colorInt: Int) {
//        binding.decreaseIb.imageTintList = ColorStateList.valueOf(colorInt)
//    }
//
//    fun setValueBackgroundColor(@ColorRes colorRes: Int) {
//        binding.bgBg.backgroundTintList = resources.getColorStateList(colorRes, context.theme)
//    }
//
//    fun setValueBackgroundColor(colorString: String) {
//        binding.bgBg.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorString))
//    }
//
//    fun setValueBackgroundColor(colorStateList: ColorStateList) {
//        binding.bgBg.backgroundTintList = colorStateList
//    }
//
//    fun setValueBackgroundColorInt(@ColorInt colorInt: Int) {
//        binding.bgBg.backgroundTintList = ColorStateList.valueOf(colorInt)
//    }
//
//    fun setValueTextColor(@ColorRes colorRes: Int) {
//        binding.quantityTv.setTextColor(resources.getColor(colorRes, context.theme))
//    }
//
//    fun setValueTextColor(colorString: String) {
//        binding.quantityTv.setTextColor(Color.parseColor(colorString))
//    }
//
//    fun setValueTextColor(colors: ColorStateList) {
//        binding.quantityTv.setTextColor(colors)
//    }
//
//    fun setValueTextColorInt(@ColorInt colorInt: Int) {
//        binding.quantityTv.setTextColor(colorInt)
//    }
//
//    fun setPlusIcon(@DrawableRes icon: Int) {
//        binding.increaseIb.setImageResource(icon)
//    }
//
//    fun setMinusIcon(@DrawableRes icon: Int) {
//        binding.decreaseIb.setImageResource(icon)
//    }


}