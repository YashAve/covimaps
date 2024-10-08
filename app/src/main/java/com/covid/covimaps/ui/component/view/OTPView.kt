package com.covid.covimaps.ui.component.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Rect
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.text.method.TransformationMethod
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.covid.covimaps.R
import java.util.*
import kotlin.math.max

class OTPView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), TextWatcher, View.OnFocusChangeListener,
    View.OnKeyListener {
    private val DENSITY = getContext().resources.displayMetrics.density

    /**
     * Attributes
     */
    private var mPinLength = 10
    private val editTextList: MutableList<EditText>? = ArrayList()
    private var mPinWidth = 48
    private var mTextSize = 14
    private var mPinHeight = 48
    private var mSplitWidth = 8
    private var mCursorVisible = true
    private var mDelPressed = false

    @get:DrawableRes
    @DrawableRes
    var pinBackground = R.drawable.otp_edittext_background
        private set
    private var mPassword = false
    private var mHint: String? = ""
    private var inputType = InputType.NUMBER
    private var finalNumberPin = false
    private var mListener: PinViewEventListener? = null
    private var fromSetValue = false
    private var mForceKeyboard = true

    enum class InputType {
        NUMBER
    }

    /**
     * Interface for onDataEntered event.
     */
    interface PinViewEventListener {
        fun onDataEntered(pinview: OTPView?, fromUser: Boolean)
    }

    var mClickListener: OnClickListener? = null
    var currentFocus: View? = null
    var filters = arrayOfNulls<InputFilter>(1)
    var params: LayoutParams? = null

    /**
     * A method to take care of all the initialisations.
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        removeAllViews()
        mPinHeight *= DENSITY.toInt()
        mPinWidth *= DENSITY.toInt()
        mSplitWidth *= DENSITY.toInt()
        setWillNotDraw(false)
        initAttributes(context, attrs, defStyleAttr)
        params = LayoutParams(mPinWidth, mPinHeight)
        orientation = HORIZONTAL
        createEditTexts()
        super.setOnClickListener {
            var focused = false
            for (editText in editTextList!!) {
                if (editText.length() == 0) {
                    editText.requestFocus()
                    openKeyboard()
                    focused = true
                    break
                }
            }
            if (!focused && editTextList.size > 0) { // Focus the last view
                editTextList[editTextList.size - 1].requestFocus()
            }
            if (mClickListener != null) {
                mClickListener!!.onClick(this@OTPView)
            }
        }
        // Bring up the keyboard
        val firstEditText: View? = editTextList?.first()
        firstEditText?.postDelayed({ openKeyboard() }, 200)
        updateEnabledState()
    }

    /**
     * Creates editTexts and adds it to the pinview based on the pinLength specified.
     */
    private fun createEditTexts() {
        removeAllViews()
        editTextList!!.clear()
        var editText: EditText

        for (i in 0 until mPinLength) {
            editText = EditText(context)
            editText.textSize = mTextSize.toFloat()
            editTextList.add(i, editText)
            this.addView(editText)
            generateOneEditText(editText, "" + i)
        }
        setTransformation()
    }

    /**
     * This method gets the attribute values from the XML, if not found it takes the default values.
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private fun initAttributes(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        if (attrs == null) {
            return
        }

        val array = context.obtainStyledAttributes(attrs, R.styleable.Pinview, defStyleAttr, 0)
        pinBackground = array.getResourceId(R.styleable.Pinview_pinBackground, pinBackground)
        mPinLength = array.getInt(R.styleable.Pinview_pinLength, mPinLength)
        mPinHeight = array.getDimension(R.styleable.Pinview_pinHeight, mPinHeight.toFloat()).toInt()
        mPinWidth = array.getDimension(R.styleable.Pinview_pinWidth, mPinWidth.toFloat()).toInt()
        mSplitWidth =
            array.getDimension(R.styleable.Pinview_splitWidth, mSplitWidth.toFloat()).toInt()
        mTextSize = array.getDimension(R.styleable.Pinview_textSize, mTextSize.toFloat()).toInt()
        mCursorVisible = array.getBoolean(R.styleable.Pinview_cursorVisible, mCursorVisible)
        mPassword = array.getBoolean(R.styleable.Pinview_password, mPassword)
        mForceKeyboard = array.getBoolean(R.styleable.Pinview_forceKeyboard, mForceKeyboard)
        mHint = array.getString(R.styleable.Pinview_hint)
        val its = InputType.values()
        inputType = its[array.getInt(R.styleable.Pinview_inputType, 0)]
        array.recycle()
    }

    /**
     * Takes care of styling the editText passed in the param.
     * tag is the index of the editText.
     *
     * @param styleEditText
     * @param tag
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun generateOneEditText(styleEditText: EditText, tag: String) {
        params!!.setMargins(mSplitWidth / 2, mSplitWidth / 2, mSplitWidth / 2, mSplitWidth / 2)

        filters[0] = InputFilter.LengthFilter(1)

        styleEditText.filters = filters
        styleEditText.layoutParams = params
        styleEditText.gravity = Gravity.CENTER
        styleEditText.isCursorVisible = mCursorVisible

        if (!mCursorVisible) {
            styleEditText.isClickable = false
            styleEditText.hint = mHint
            styleEditText.setOnTouchListener { _, _ -> // When back space is pressed it goes to delete mode and when u click on an edit Text it should get out of the delete mode
                mDelPressed = false
                false
            }
        }
        styleEditText.apply {
            setBackgroundResource(pinBackground)
            setPadding(0, 0, 0, 0)
            this.tag = tag
            inputType = keyboardInputType
            addTextChangedListener(this@OTPView)
            onFocusChangeListener = this@OTPView
            setOnKeyListener(this@OTPView)
        }
    }

    private val keyboardInputType: Int
        get() {
            return when (inputType) {
                InputType.NUMBER -> android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
            }
        }

    /**
     * Returns the value of the Pinview
     *
     * @return
     */// Allow empty string to clear the fields
    /**
     * Sets the value of the Pinview
     *
     * @param value
     */
    var value: String
        get() {
            val sb = StringBuilder()
            for (et in editTextList!!) {
                sb.append(et.text.toString())
            }
            return sb.toString()
        }
        set(value) {
            val regex = Regex("[0-9]*") // Allow empty string to clear the fields
            fromSetValue = true

            if (inputType == InputType.NUMBER && !value.matches(regex) || editTextList.isNullOrEmpty()) {
                return
            }

            var lastTagHavingValue = -1
            for (i in editTextList.indices) {
                if (value.length > i) {
                    lastTagHavingValue = i
                    editTextList[i].setText(value[i].toString())
                } else {
                    editTextList[i].setText("")
                }
            }
            if (mPinLength > 0) {
                currentFocus = editTextList[mPinLength - 1]
                if (lastTagHavingValue == mPinLength - 1) {
                    currentFocus = editTextList[mPinLength - 1]
                    if (inputType == InputType.NUMBER || mPassword) {
                        this.finalNumberPin = true
                    }
                    this.mListener?.onDataEntered(this, false)
                }
                currentFocus?.requestFocus()
            }
            fromSetValue = false
            updateEnabledState()
        }

    /**
     * Requsets focus on current pin view and opens keyboard if forceKeyboard is enabled.
     *
     * @return the current focused pin view. It can be used to open softkeyboard manually.
     */
    fun requestPinEntryFocus(): View? {
        val currentTag = max(0, indexOfCurrentFocus)
        val currentEditText = editTextList?.get(currentTag)
        currentEditText?.requestFocus()
        openKeyboard()
        return currentEditText
    }

    private fun openKeyboard() {
        if (mForceKeyboard) {
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
        }
    }

    /**
     * Clears the values in the Pinview
     */
    fun clearValue() {
        value = ""
    }

    override fun onFocusChange(view: View, isFocused: Boolean) {
        if (isFocused && !mCursorVisible) {
            if (mDelPressed) {
                currentFocus = view
                mDelPressed = false
                return
            }
            for (editText in editTextList!!) {
                if (editText.length() == 0) {
                    if (editText !== view) {
                        editText.requestFocus()
                    } else {
                        currentFocus = view
                    }
                    return
                }
            }
            if (editTextList[editTextList.size - 1] !== view) {
                editTextList[editTextList.size - 1].requestFocus()
            } else {
                currentFocus = view
            }
        } else if (isFocused && mCursorVisible) {
            currentFocus = view
        } else {
            view.clearFocus()
        }
    }

    /**
     * Handles the character transformation for password inputs.
     */
    private fun setTransformation() {
        if (mPassword) {
            for (editText in editTextList!!) {
                editText.removeTextChangedListener(this)
                editText.transformationMethod = PinTransformationMethod()
                editText.addTextChangedListener(this)
            }
        } else {
            for (editText in editTextList!!) {
                editText.removeTextChangedListener(this)
                editText.transformationMethod = null
                editText.addTextChangedListener(this)
            }
        }
    }

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

    /**
     * Fired when text changes in the editTexts.
     * Backspace is also identified here.
     *
     * @param charSequence
     * @param start
     * @param i1
     * @param count
     */
    override fun onTextChanged(charSequence: CharSequence, start: Int, i1: Int, count: Int) {

        if (charSequence.length == 1 && currentFocus != null) {
            val currentTag = indexOfCurrentFocus
            if (currentTag < mPinLength - 1) {
                var delay: Long = 1
                if (mPassword) delay = 25
                postDelayed({
                    val nextEditText = editTextList?.get(currentTag + 1)
                    nextEditText?.isEnabled = true
                    nextEditText?.requestFocus()
                }, delay)
            }

            if (currentTag == mPinLength - 1 && inputType == InputType.NUMBER || currentTag == mPinLength - 1 && mPassword) {
                finalNumberPin = true
            }
        } else if (charSequence.isEmpty()) {
            if (indexOfCurrentFocus < 0) {
                return
            }
            val currentTag = indexOfCurrentFocus
            this.mDelPressed = true

            //For the last cell of the non password text fields. Clear the text without changing the focus.
            if (!this.editTextList?.get(currentTag)?.text.isNullOrEmpty()) {
                this.editTextList?.get(currentTag)?.setText("")
            }
        }

        this.mListener?.onDataEntered(this, true)

        updateEnabledState()
    }

    /**
     * Disable views ahead of current focus, so a selector can change the drawing of those views.
     */
    private fun updateEnabledState() {
        val currentTag = max(0, indexOfCurrentFocus)

        for (index in editTextList!!.indices) {
            val editText = editTextList[index]
            editText.isEnabled = index <= currentTag
        }
    }

    override fun afterTextChanged(editable: Editable) {}

    /**
     * Monitors keyEvent.
     *
     * @param view
     * @param i
     * @param keyEvent
     * @return
     */
    override fun onKey(view: View, i: Int, keyEvent: KeyEvent): Boolean {
        if (keyEvent.action == KeyEvent.ACTION_UP && i == KeyEvent.KEYCODE_DEL) {
            // Perform action on Del press
            val currentTag = indexOfCurrentFocus
            val currentEditText = editTextList?.get(currentTag)?.text
            //Last tile of the number pad. Clear the edit text without changing the focus.
            if (inputType == InputType.NUMBER && currentTag == mPinLength - 1 && finalNumberPin ||
                mPassword && currentTag == mPinLength - 1 && finalNumberPin
            ) {
                if (!currentEditText.isNullOrEmpty()) {
                    this.editTextList?.get(currentTag)?.setText("")
                }
                finalNumberPin = false
            } else if (currentTag > 0) {
                mDelPressed = true
                if (currentEditText.isNullOrEmpty()) {
                    //Takes it back one tile
                    this.editTextList?.get(currentTag - 1)?.requestFocus()
                }
                this.editTextList?.get(currentTag)?.setText("")
            } else {
                //For the first cell

                if (!currentEditText.isNullOrEmpty()) {
                    editTextList?.get(currentTag)?.setText("")
                }
            }
            return true
        }
        return false
    }

    /**
     * Getters and Setters
     */
    private val indexOfCurrentFocus: Int
        get() = editTextList!!.indexOf(currentFocus)

    var splitWidth: Int
        get() = mSplitWidth
        set(splitWidth) {
            mSplitWidth = splitWidth
            val margin = splitWidth / 2
            params?.setMargins(margin, margin, margin, margin)
            this.editTextList?.forEach {
                it.layoutParams = params
            }
        }

    var pinHeight: Int
        get() = mPinHeight
        set(pinHeight) {
            mPinHeight = pinHeight
            params?.height = pinHeight
            this.editTextList?.forEach {
                it.layoutParams = params
            }
        }

    var pinWidth: Int
        get() = mPinWidth
        set(pinWidth) {
            mPinWidth = pinWidth
            params?.width = pinWidth
            this.editTextList?.forEach {
                it.layoutParams = params
            }
        }

    var pinLength: Int
        get() = mPinLength
        set(pinLength) {
            mPinLength = pinLength
            createEditTexts()
        }

    var isPassword: Boolean
        get() = mPassword
        set(password) {
            mPassword = password
            setTransformation()
        }

    var hint: String?
        get() = mHint
        set(mHint) {
            this.mHint = mHint
            this.editTextList?.forEach {
                it.hint = mHint
            }
        }

    fun setPinBackgroundRes(@DrawableRes res: Int) {
        pinBackground = res
        this.editTextList?.forEach {
            it.setBackgroundResource(res)
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        mClickListener = l
    }

    fun getInputType(): InputType {
        return inputType
    }

    fun setInputType(inputType: InputType) {
        this.inputType = inputType
        val keyInputType = keyboardInputType
        editTextList?.forEach {
            it.inputType = keyInputType
        }
    }

    fun setPinViewEventListener(listener: PinViewEventListener?) {
        mListener = listener
    }

    fun showCursor(status: Boolean) {
        mCursorVisible = status
        this.editTextList?.forEach { it.isCursorVisible = status }
    }

    fun setTextSize(textSize: Int) {
        mTextSize = textSize
        this.editTextList?.forEach { it.textSize = mTextSize.toFloat() }
    }

    fun setCursorColor(@ColorInt color: Int) {
        this.editTextList?.forEach {
            setCursorColor(it, color)
        }
    }

    fun setTextColor(@ColorInt color: Int) {
        this.editTextList?.forEach {
            it.setTextColor(color)
        }
    }

    @SuppressLint("SoonBlockedPrivateApi")
    fun setCursorShape(@DrawableRes shape: Int) {
        editTextList?.forEach {
            try {
                val field = TextView::class.java.getDeclaredField("mCursorDrawableRes")
                field.isAccessible = true
                field[it] = shape
            } catch (ignored: Exception) {
            }
        }

    }

    @SuppressLint("SoonBlockedPrivateApi", "DiscouragedPrivateApi")
    private fun setCursorColor(view: EditText, @ColorInt color: Int) {
        try {
            // Get the cursor resource id
            var field = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            field.isAccessible = true
            val drawableResId = field.getInt(view)

            // Get the editor
            field = TextView::class.java.getDeclaredField("mEditor")
            field.isAccessible = true
            val editor = field[view]

            // Get the drawable and set a color filter
            val drawable = ContextCompat.getDrawable(view.context, drawableResId)
            drawable?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            val drawables = arrayOf(drawable, drawable)

            // Set the drawables
            field = editor.javaClass.getDeclaredField("mCursorDrawable")
            field.isAccessible = true
            field[editor] = drawables
        } catch (ignored: Exception) {
        }
    }

    init {
        gravity = Gravity.CENTER
        init(context, attrs, defStyleAttr)
    }
}

class PinTransformationMethod : TransformationMethod {

    private val BULLET:Char = '\u002A'

    override fun getTransformation(source: CharSequence, view: View): CharSequence {
        return PasswordCharSequence(source)
    }

    override fun onFocusChanged(view: View, sourceText: CharSequence, focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {}
    private inner class PasswordCharSequence(private val source: CharSequence) : CharSequence {
        override val length: Int
            get() = source.length


        override fun get(index: Int): Char {
            return BULLET
        }


        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return PasswordCharSequence(source.subSequence(startIndex, endIndex))
        }

    }
}