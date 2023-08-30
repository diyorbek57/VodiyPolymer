package uz.seppuku.vp.helper.quantitizer

interface QuantitizerListener {
    fun onIncrease()
    fun onDecrease()
    fun onValueChanged(value: Int)
}