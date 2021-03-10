package com.alamkanak.weekview


import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.*
import android.text.format.DateFormat
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.widget.OverScroller
import androidx.annotation.RequiresApi
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import com.techizer.customgriddemo.*
import com.techizer.customgriddemo.WeekViewUtil.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Raquib-ul-Alam Kanak on 7/21/2014.
 * Website: http://alamkanak.github.io/
 */
class NewGridLayout (var mContext: Context, attrs: AttributeSet) : View(mContext,attrs) {
    private enum class Direction {
        NONE, LEFT, RIGHT, VERTICAL
    }

    var attrs: AttributeSet? = null

    private var mHomeDate: Calendar? = null
    private var mMinDate: Calendar? = null
    private var mMaxDate: Calendar? = null
    private var mTimeTextPaint: Paint? = null
    private var mTimeTextWidth: Float = 0f
    private var mTimeTextHeight: Float = 0f
    private var mHeaderTextPaint: Paint? = null
    private var mHeaderTextHeight: Float = 0f
    private var mHeaderHeight: Float = 0f
    private var mGestureDetector: GestureDetectorCompat? = null
    private var mScroller: OverScroller? = null
    private val mCurrentOrigin: PointF = PointF(0f, 0f)
    private var mCurrentScrollDirection: Direction = Direction.NONE
    private var mHeaderBackgroundPaint: Paint? = null
    private var mWidthPerDay: Float = 0f
    private var mDayBackgroundPaint: Paint? = null
    private var mHourSeparatorPaint: Paint? = null
    private var mHeaderMarginBottom: Float = 0f
    private var mTodayBackgroundPaint: Paint? = null
    private var mFutureBackgroundPaint: Paint? = null
    private var mPastBackgroundPaint: Paint? = null
    private var mFutureWeekendBackgroundPaint: Paint? = null
    private var mPastWeekendBackgroundPaint: Paint? = null
    private var mNowLinePaint: Paint? = null
    private var mTodayHeaderTextPaint: Paint? = null
    private var mEventBackgroundPaint: Paint? = null
    private var mNewEventBackgroundPaint: Paint? = null
    private var mHeaderColumnWidth: Float = 0f
    private var mEventRects: MutableList<EventRect?>? = null
    private var mEvents: MutableList<WeekViewEvent?>? = null
    private var mEventTextPaint: TextPaint? = null
    private val mNewEventTextPaint: TextPaint? = null
    private var mHeaderColumnBackgroundPaint: Paint? = null
    private var mFetchedPeriod: Int = -1 // the middle period the calendar has fetched.
    private var mRefreshEvents: Boolean = false
    private var mCurrentFlingDirection: Direction = Direction.NONE
    private var mScaleDetector: ScaleGestureDetector? = null
    private var mIsZooming: Boolean = false
    private var mFirstVisibleDay: Calendar? = null
    private var mLastVisibleDay: Calendar? = null
    private var mMinimumFlingVelocity: Int = 0
    private var mScaledTouchSlop: Int = 0
    private var mNewEventRect: EventRect? = null
    private var textColorPicker: TextColorPicker? = null

    // Attributes and their default values.
    private var mHourHeight: Int = 50
    private var mNewHourHeight: Int = -1
    private var mMinHourHeight: Int = 0 //no minimum specified (will be dynamic, based on screen)
    private var mEffectiveMinHourHeight: Int = mMinHourHeight //compensates for the fact that you can't keep zooming out.
    private var mMaxHourHeight: Int = 250
    private var mColumnGap: Int = 10
    private var mFirstDayOfWeek: Int = Calendar.MONDAY
    private var mTextSize: Int = 12
    private var mHeaderColumnPadding: Int = 10
    private var mHeaderColumnTextColor: Int = Color.BLACK
    private var mNumberOfVisibleDays: Int = 3
    private var mHeaderRowPadding: Int = 10
    private var mHeaderRowBackgroundColor: Int = Color.WHITE
    private var mDayBackgroundColor: Int = Color.rgb(245, 245, 245)
    private var mPastBackgroundColor: Int = Color.rgb(227, 227, 227)
    private var mFutureBackgroundColor: Int = Color.rgb(245, 245, 245)
    private var mPastWeekendBackgroundColor: Int = 0
    private var mFutureWeekendBackgroundColor: Int = 0
    private var mNowLineColor: Int = Color.rgb(102, 102, 102)
    private var mNowLineThickness: Int = 5
    private var mHourSeparatorColor: Int = Color.rgb(230, 230, 230)
    private var mTodayBackgroundColor: Int = Color.rgb(239, 247, 254)
    private var mHourSeparatorHeight: Int = 2
    private var mTodayHeaderTextColor: Int = Color.rgb(39, 137, 228)
    private var mEventTextSize: Int = 12
    private var mEventTextColor: Int = Color.BLACK
    private var mEventPadding: Int = 8
    private var mHeaderColumnBackgroundColor: Int = Color.WHITE
    private var mDefaultEventColor: Int = 0
    private var mNewEventColor: Int = 0
    private var mNewEventIdentifier: String? = "-100"
    private var mNewEventIconDrawable: Drawable? = null
    private var mNewEventLengthInMinutes: Int = 60
    private var mNewEventTimeResolutionInMinutes: Int = 15
    private var mShowFirstDayOfWeekFirst: Boolean = false
    private var mIsFirstDraw: Boolean = true
    private var mAreDimensionsInvalid: Boolean = true

    @Deprecated("")
    private var mDayNameLength: Int = LENGTH_LONG
    private var mOverlappingEventGap: Int = 0
    private var mEventMarginVertical: Int = 0
    private var mXScrollingSpeed: Float = 1f
    private var mScrollToDay: Calendar? = null
    private var mScrollToHour: Double = -1.0
    private var mEventCornerRadius: Int = 0
    private var mShowDistinctWeekendColor: Boolean = false
    private var mShowNowLine: Boolean = false
    private var mShowDistinctPastFutureColor: Boolean = false
    private var mHorizontalFlingEnabled: Boolean = true
    private var mVerticalFlingEnabled: Boolean = true
    private var mAllDayEventHeight: Int = 100
    private var mZoomFocusPoint: Float = 0f
    private var mZoomFocusPointEnabled: Boolean = true
    private var mScrollDuration: Int = 250
    private var mTimeColumnResolution = 60
    private var mTypeface: Typeface = Typeface.DEFAULT_BOLD
    private var mMinTime: Int = 0
    private var mMaxTime: Int = 24
    private var mAutoLimitTime: Boolean = false
    private var mEnableDropListener: Boolean = false
    private var mMinOverlappingMinutes: Int = 0

    // Listeners.
    var eventClickListener: EventClickListener? = null
        private set
    var eventLongPressListener: EventLongPressListener? = null
   // private var mWeekViewLoader: WeekViewLoader? = null
    var emptyViewClickListener: EmptyViewClickListener? = null
    var emptyViewLongPressListener: EmptyViewLongPressListener? = null
    private var mDateTimeInterpreter: DateTimeInterpreter? = null
    var scrollListener: ScrollListener? = null
    var addEventClickListener: AddEventClickListener? = null
    private var mDropListener: DropListener? = null
    private var mZoomEndListener: ZoomEndListener? = null
    private val mGestureListener: SimpleOnGestureListener = object : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            stopScrolling()
            return true
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            goToNearestOrigin()
            return false
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            // Check if view is zoomed.
            if (mIsZooming) return true
            when (mCurrentScrollDirection) {
                Direction.NONE -> {

                    // Allow scrolling only in one direction.
                    if (Math.abs(distanceX) > Math.abs(distanceY)) {
                        if (distanceX > 0) {
                            mCurrentScrollDirection = Direction.LEFT
                        } else {
                            mCurrentScrollDirection = Direction.RIGHT
                        }
                    } else {
                        mCurrentScrollDirection = Direction.VERTICAL
                    }
                }
                Direction.LEFT -> {

                    // Change direction if there was enough change.
                    if (Math.abs(distanceX) > Math.abs(distanceY) && (distanceX < -mScaledTouchSlop)) {
                        mCurrentScrollDirection = Direction.RIGHT
                    }
                }
                Direction.RIGHT -> {

                    // Change direction if there was enough change.
                    if (Math.abs(distanceX) > Math.abs(distanceY) && (distanceX > mScaledTouchSlop)) {
                        mCurrentScrollDirection = Direction.LEFT
                    }
                }
                else -> {
                }
            }
            when (mCurrentScrollDirection) {
                Direction.LEFT, Direction.RIGHT -> {
                    val minX: Float = xMinLimit
                    val maxX: Float = xMaxLimit
                    if ((mCurrentOrigin.x - (distanceX * mXScrollingSpeed)) > maxX) {
                        mCurrentOrigin.x = maxX
                    } else if ((mCurrentOrigin.x - (distanceX * mXScrollingSpeed)) < minX) {
                        mCurrentOrigin.x = minX
                    } else {
                        mCurrentOrigin.x -= distanceX * mXScrollingSpeed
                    }
                    ViewCompat.postInvalidateOnAnimation(this@NewGridLayout)
                }
                Direction.VERTICAL -> {
                    val minY: Float = yMinLimit
                    val maxY: Float = yMaxLimit
                    if ((mCurrentOrigin.y - (distanceY)) > maxY) {
                        mCurrentOrigin.y = maxY
                    } else if ((mCurrentOrigin.y - (distanceY)) < minY) {
                        mCurrentOrigin.y = minY
                    } else {
                        mCurrentOrigin.y -= distanceY
                    }
                    ViewCompat.postInvalidateOnAnimation(this@NewGridLayout)
                }
                else -> {
                }
            }
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (mIsZooming) return true
            if (((mCurrentFlingDirection == Direction.LEFT && !mHorizontalFlingEnabled) ||
                            (mCurrentFlingDirection == Direction.RIGHT && !mHorizontalFlingEnabled) ||
                            (mCurrentFlingDirection == Direction.VERTICAL && !mVerticalFlingEnabled))) {
                return true
            }
            mScroller!!.forceFinished(true)
            mCurrentFlingDirection = mCurrentScrollDirection
            when (mCurrentFlingDirection) {
                Direction.LEFT, Direction.RIGHT -> mScroller!!.fling(mCurrentOrigin.x.toInt(), mCurrentOrigin.y.toInt(), (velocityX * mXScrollingSpeed).toInt(), 0, xMinLimit.toInt(), xMaxLimit.toInt(), yMinLimit.toInt(), yMaxLimit.toInt())
                Direction.VERTICAL -> mScroller!!.fling(mCurrentOrigin.x.toInt(), mCurrentOrigin.y.toInt(), 0, velocityY.toInt(), xMinLimit.toInt(), xMaxLimit.toInt(), yMinLimit.toInt(), yMaxLimit.toInt())
                else -> {
                }
            }
            ViewCompat.postInvalidateOnAnimation(this@NewGridLayout)
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            goToNearestOrigin()

            // If the tap was on an event then trigger the callback.
            if (mEventRects != null && eventClickListener != null) {
                val reversedEventRects: List<EventRect?> = mEventRects as MutableList<EventRect?>
                Collections.reverse(reversedEventRects)
                for (eventRect: EventRect? in reversedEventRects) {
                    if (!(mNewEventIdentifier == eventRect!!.event.getIdentifier()) && (eventRect.rectF != null) && (e.x > eventRect.rectF!!.left) && (e.x < eventRect.rectF!!.right) && (e.y > eventRect.rectF!!.top) && (e.y < eventRect.rectF!!.bottom)) {
                        eventClickListener!!.onEventClick(eventRect.originalEvent, eventRect.rectF)
                        playSoundEffect(SoundEffectConstants.CLICK)
                        return super.onSingleTapConfirmed(e)
                    }
                }
            }
            val xOffset: Float = xStartPixel
            val x: Float = e.x - xOffset
            val y: Float = e.y - mCurrentOrigin.y
            // If the tap was on add new Event space, then trigger the callback
            if (((addEventClickListener != null) && (mNewEventRect != null) && (mNewEventRect!!.rectF != null) &&
                            mNewEventRect!!.rectF!!.contains(x, y))) {
                addEventClickListener!!.onAddEventClicked(mNewEventRect!!.event.getStartTime(), mNewEventRect!!.event.getEndTime())
                return super.onSingleTapConfirmed(e)
            }

            // If the tap was on an empty space, then trigger the callback.
            if ((emptyViewClickListener != null || addEventClickListener != null) && (e.x > mHeaderColumnWidth) && (e.y > (mHeaderHeight + (mHeaderRowPadding * 2) + mHeaderMarginBottom))) {
                val selectedTime: Calendar? = getTimeFromPoint(e.x, e.y)
                if (selectedTime != null) {
                    val tempEvents: MutableList<WeekViewEvent?> = ArrayList(mEvents)
                    if (mNewEventRect != null) {
                        tempEvents.remove(mNewEventRect!!.event)
                        mNewEventRect = null
                    }
                    playSoundEffect(SoundEffectConstants.CLICK)
                    if (emptyViewClickListener != null) emptyViewClickListener!!.onEmptyViewClicked(selectedTime.clone() as Calendar)
                    if (addEventClickListener != null) {
                        //round selectedTime to resolution
                        selectedTime.add(Calendar.MINUTE, -(mNewEventLengthInMinutes / 2))
                        //Fix selected time if before the minimum hour
                        if (selectedTime.get(Calendar.HOUR_OF_DAY) < mMinTime) {
                            selectedTime.set(Calendar.HOUR_OF_DAY, mMinTime)
                            selectedTime.set(Calendar.MINUTE, 0)
                        }
                        val unroundedMinutes: Int = selectedTime.get(Calendar.MINUTE)
                        val mod: Int = unroundedMinutes % mNewEventTimeResolutionInMinutes
                        selectedTime.add(Calendar.MINUTE, if (mod < Math.ceil((mNewEventTimeResolutionInMinutes / 2).toDouble())) -mod else (mNewEventTimeResolutionInMinutes - mod))
                        val endTime: Calendar = selectedTime.clone() as Calendar

                        //Minus one to ensure it is the same day and not midnight (next day)
                        val maxMinutes: Int = ((mMaxTime - selectedTime.get(Calendar.HOUR_OF_DAY)) * 60) - selectedTime.get(Calendar.MINUTE) - 1
                        endTime.add(Calendar.MINUTE, Math.min(maxMinutes, mNewEventLengthInMinutes))
                        //If clicked at end of the day, fix selected startTime
                        if (maxMinutes < mNewEventLengthInMinutes) {
                            selectedTime.add(Calendar.MINUTE, maxMinutes - mNewEventLengthInMinutes)
                        }
                        val newEvent: WeekViewEvent = WeekViewEvent(mNewEventIdentifier, "", null, selectedTime, endTime)
                        val top: Float = mHourHeight * getPassedMinutesInDay(selectedTime) / 60 + eventsTop
                        val bottom: Float = mHourHeight * getPassedMinutesInDay(endTime) / 60 + eventsTop

                        // Calculate left and right.
                        val left: Float = mWidthPerDay * WeekViewUtil.daysBetween(getFirstVisibleDay(), selectedTime)
                        val right: Float = left + mWidthPerDay

                        // Add the new event if its bounds are valid
                        if ((left < right) && (
                                        left < width) && (
                                        top < height) && (
                                        right > mHeaderColumnWidth) && (
                                        bottom > 0)) {
                            val dayRectF: RectF = RectF(left, top, right, bottom - mCurrentOrigin.y)
                            newEvent.setColor(mNewEventColor)
                            mNewEventRect = EventRect(newEvent, newEvent, dayRectF)
                            tempEvents.add(newEvent)
                            clearEvents()
                            cacheAndSortEvents(tempEvents)
                            computePositionOfEvents(mEventRects)
                            invalidate()
                        }
                    }
                }
            }
            return super.onSingleTapConfirmed(e)
        }

        override fun onLongPress(e: MotionEvent) {
            super.onLongPress(e)
            goToNearestOrigin()
            if (eventLongPressListener != null && mEventRects != null) {
                val reversedEventRects: List<EventRect?> = mEventRects as MutableList<EventRect?>
                Collections.reverse(reversedEventRects)
                for (event: EventRect? in reversedEventRects) {
                    if ((event!!.rectF != null) && (e.x > event.rectF!!.left) && (e.x < event.rectF!!.right) && (e.y > event.rectF!!.top) && (e.y < event.rectF!!.bottom)) {
                        eventLongPressListener!!.onEventLongPress(event.originalEvent, event.rectF)
                        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        return
                    }
                }
            }

            // If the tap was on in an empty space, then trigger the callback.
            if ((emptyViewLongPressListener != null) && (e.x > mHeaderColumnWidth) && (e.y > (mHeaderHeight + (mHeaderRowPadding * 2) + mHeaderMarginBottom))) {
                val selectedTime: Calendar? = getTimeFromPoint(e.x, e.y)
                if (selectedTime != null) {
                    performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    emptyViewLongPressListener!!.onEmptyViewLongPress(selectedTime)
                }
            }
        }
    }

    private fun init() {
        resetHomeDate()

        // Scrolling initialization.
        mGestureDetector = GestureDetectorCompat(mContext, mGestureListener)
        mScroller = OverScroller(mContext, FastOutLinearInInterpolator())
        mMinimumFlingVelocity = ViewConfiguration.get(mContext).scaledMinimumFlingVelocity
        mScaledTouchSlop = ViewConfiguration.get(mContext).scaledTouchSlop

        // Measure settings for time column.
        mTimeTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTimeTextPaint!!.textAlign = Paint.Align.RIGHT
        mTimeTextPaint!!.textSize = mTextSize.toFloat()
        mTimeTextPaint!!.color = mHeaderColumnTextColor
        val rect: Rect = Rect()
        val exampleTime: String = if ((mTimeColumnResolution % 60 != 0)) "00:00 PM" else "00 PM"
        mTimeTextPaint!!.getTextBounds(exampleTime, 0, exampleTime.length, rect)
        mTimeTextWidth = mTimeTextPaint!!.measureText(exampleTime)
        mTimeTextHeight = rect.height().toFloat()
        mHeaderMarginBottom = mTimeTextHeight / 2
        initTextTimeWidth()

        // Measure settings for header row.
        mHeaderTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mHeaderTextPaint!!.color = mHeaderColumnTextColor
        mHeaderTextPaint!!.textAlign = Paint.Align.CENTER
        mHeaderTextPaint!!.textSize = mTextSize.toFloat()
        mHeaderTextPaint!!.getTextBounds(exampleTime, 0, exampleTime.length, rect)
        mHeaderTextHeight = rect.height().toFloat()
        mHeaderTextPaint!!.typeface = mTypeface


        // Prepare header background paint.
        mHeaderBackgroundPaint = Paint()
        mHeaderBackgroundPaint!!.color = mHeaderRowBackgroundColor

        // Prepare day background color paint.
        mDayBackgroundPaint = Paint()
        mDayBackgroundPaint!!.color = mDayBackgroundColor
        mFutureBackgroundPaint = Paint()
        mFutureBackgroundPaint!!.color = mFutureBackgroundColor
        mPastBackgroundPaint = Paint()
        mPastBackgroundPaint!!.color = mPastBackgroundColor
        mFutureWeekendBackgroundPaint = Paint()
        mFutureWeekendBackgroundPaint!!.color = mFutureWeekendBackgroundColor
        mPastWeekendBackgroundPaint = Paint()
        mPastWeekendBackgroundPaint!!.color = mPastWeekendBackgroundColor

        // Prepare hour separator color paint.
        mHourSeparatorPaint = Paint()
        mHourSeparatorPaint!!.style = Paint.Style.STROKE
        mHourSeparatorPaint!!.strokeWidth = mHourSeparatorHeight.toFloat()
        mHourSeparatorPaint!!.color = mHourSeparatorColor

        // Prepare the "now" line color paint
        mNowLinePaint = Paint()
        mNowLinePaint!!.strokeWidth = mNowLineThickness.toFloat()
        mNowLinePaint!!.color = mNowLineColor

        // Prepare today background color paint.
        mTodayBackgroundPaint = Paint()
        mTodayBackgroundPaint!!.color = mTodayBackgroundColor

        // Prepare today header text color paint.
        mTodayHeaderTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTodayHeaderTextPaint!!.textAlign = Paint.Align.CENTER
        mTodayHeaderTextPaint!!.textSize = mTextSize.toFloat()
        mTodayHeaderTextPaint!!.typeface = mTypeface
        mTodayHeaderTextPaint!!.color = mTodayHeaderTextColor

        // Prepare event background color.
        mEventBackgroundPaint = Paint()
        mEventBackgroundPaint!!.color = Color.rgb(174, 208, 238)
        // Prepare empty event background color.
        mNewEventBackgroundPaint = Paint()
        mNewEventBackgroundPaint!!.color = Color.rgb(60, 147, 217)

        // Prepare header column background color.
        mHeaderColumnBackgroundPaint = Paint()
        mHeaderColumnBackgroundPaint!!.color = mHeaderColumnBackgroundColor

        // Prepare event text size and color.
        mEventTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.LINEAR_TEXT_FLAG)
        mEventTextPaint!!.style = Paint.Style.FILL
        mEventTextPaint!!.color = mEventTextColor
        mEventTextPaint!!.textSize = mEventTextSize.toFloat()

        // Set default event color.
        mDefaultEventColor = Color.parseColor("#9fc6e7")
        // Set default empty event color.
        mNewEventColor = Color.parseColor("#3c93d9")
        mScaleDetector = ScaleGestureDetector(mContext, WeekViewGestureListener())
    }

    private fun resetHomeDate() {
        var newHomeDate: Calendar = today()
        if (mMinDate != null && newHomeDate.before(mMinDate)) {
            newHomeDate = mMinDate!!.clone() as Calendar
        }
        if (mMaxDate != null && newHomeDate.after(mMaxDate)) {
            newHomeDate = mMaxDate!!.clone() as Calendar
        }
        if (mMaxDate != null) {
            val date: Calendar = mMaxDate!!.clone() as Calendar
            date.add(Calendar.DATE, 1 - getRealNumberOfVisibleDays())
            while (date.before(mMinDate)) {
                date.add(Calendar.DATE, 1)
            }
            if (newHomeDate.after(date)) {
                newHomeDate = date
            }
        }
        mHomeDate = newHomeDate
    }

    private fun getXOriginForDate(date: Calendar): Float {
        return -daysBetween(mHomeDate, date) * (mWidthPerDay + mColumnGap)
    }

    private val numberOfPeriods: Int
        private get() = ((mMaxTime - mMinTime) * (60.0 / mTimeColumnResolution)).toInt()
    private val yMinLimit: Float
        private get() = -((((mHourHeight * (mMaxTime - mMinTime)
                ) + mHeaderHeight
                + (mHeaderRowPadding * 2
                ) + mHeaderMarginBottom
                + (mTimeTextHeight / 2))
                - height))
    private val yMaxLimit: Float
        private get() {
            return 0f
        }
    private val xMinLimit: Float
        private get() {
            if (mMaxDate == null) {
                return Int.MIN_VALUE.toFloat()
            } else {
                val date: Calendar = mMaxDate!!.clone() as Calendar
                date.add(Calendar.DATE, 1 - getRealNumberOfVisibleDays())
                while (date.before(mMinDate)) {
                    date.add(Calendar.DATE, 1)
                }
                return getXOriginForDate(date)
            }
        }
    private val xMaxLimit: Float
        private get() {
            if (mMinDate == null) {
                return Int.MAX_VALUE.toFloat()
            } else {
                return getXOriginForDate(mMinDate!!)
            }
        }

    // fix rotation changes
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mAreDimensionsInvalid = true
    }

    /**
     * Initialize time column width. Calculate value with all possible hours (supposed widest text).
     */
    private fun initTextTimeWidth() {
        mTimeTextWidth = 0f
        for (i in 0 until numberOfPeriods) {
            // Measure time string and get max width.
            val time: String? = getDateTimeInterpreter()!!.interpretTime(i, (i % 2) * 30)
            if (time == null) throw IllegalStateException("A DateTimeInterpreter must not return null time")
            mTimeTextWidth = Math.max(mTimeTextWidth, mTimeTextPaint!!.measureText(time))
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the header row.
        drawHeaderRowAndEvents(canvas)

        // Draw the time column and all the axes/separators.
        drawTimeColumnAndAxes(canvas)
    }

    private fun calculateHeaderHeight() {
        //Make sure the header is the right size (depends on AllDay events)
        var containsAllDayEvent: Boolean = false
        if (mEventRects != null && mEventRects!!.size > 0) {
            for (dayNumber in 0 until getRealNumberOfVisibleDays()) {
                val day: Calendar = getFirstVisibleDay()!!.clone() as Calendar
                day.add(Calendar.DATE, dayNumber)
                for (i in mEventRects!!.indices) {
                    if (isSameDay(mEventRects!!.get(i)!!.event.getStartTime(), day) && mEventRects!!.get(i)!!.event.isAllDay()) {
                        containsAllDayEvent = true
                        break
                    }
                }
                if (containsAllDayEvent) {
                    break
                }
            }
        }
        if (containsAllDayEvent) {
            mHeaderHeight = mHeaderTextHeight + (mAllDayEventHeight + mHeaderMarginBottom)
        } else {
            mHeaderHeight = mHeaderTextHeight
        }
    }

    private fun drawTimeColumnAndAxes(canvas: Canvas) {
        // Draw the background color for the header column.
        canvas.drawRect(0f, mHeaderHeight + mHeaderRowPadding * 2, mHeaderColumnWidth, height.toFloat(), (mHeaderColumnBackgroundPaint)!!)

        // Clip to paint in left column only.
        canvas.save()
        canvas.clipRect(0f, mHeaderHeight + mHeaderRowPadding * 2, mHeaderColumnWidth, height.toFloat())
        canvas.restore()
        for (i in 0 until numberOfPeriods) {
            // If we are showing half hours (eg. 5:30am), space the times out by half the hour height
            // and need to provide 30 minutes on each odd period, otherwise, minutes is always 0.
            var timeSpacing: Float
            var minutes: Int
            var hour: Int
            val timesPerHour: Float = 60.0.toFloat() / mTimeColumnResolution
            timeSpacing = mHourHeight / timesPerHour
            hour = mMinTime + i / (timesPerHour).toInt()
            minutes = i % (timesPerHour.toInt()) * (60 / timesPerHour.toInt())


            // Calculate the top of the rectangle where the time text will go
            val top: Float = mHeaderHeight + (mHeaderRowPadding * 2) + mCurrentOrigin.y + (timeSpacing * i) + mHeaderMarginBottom

            // Get the time to be displayed, as a String.
            val time: String? = getDateTimeInterpreter()!!.interpretTime(hour, minutes)
            // Draw the text if its y position is not outside of the visible area. The pivot point of the text is the point at the bottom-right corner.
            if (time == null) throw IllegalStateException("A DateTimeInterpreter must not return null time")
            if (top < height) canvas.drawText(time, mTimeTextWidth + mHeaderColumnPadding, top + mTimeTextHeight, (mTimeTextPaint)!!)
        }
    }

    private fun drawHeaderRowAndEvents(canvas: Canvas) {
        // Calculate the available width for each day.
        mHeaderColumnWidth = mTimeTextWidth + mHeaderColumnPadding * 2
        mWidthPerDay = width - mHeaderColumnWidth - (mColumnGap * (getRealNumberOfVisibleDays() - 1))
        mWidthPerDay = mWidthPerDay / getRealNumberOfVisibleDays()
        calculateHeaderHeight() //Make sure the header is the right size (depends on AllDay events)
        val today: Calendar = today()
        if (mAreDimensionsInvalid) {
            mEffectiveMinHourHeight = Math.max(mMinHourHeight, ((height - mHeaderHeight - (mHeaderRowPadding * 2) - mHeaderMarginBottom) / (mMaxTime - mMinTime)).toInt())
            mAreDimensionsInvalid = false
            if (mScrollToDay != null) goToDate(mScrollToDay!!)
            mAreDimensionsInvalid = false
            if (mScrollToHour >= 0) goToHour(mScrollToHour)
            mScrollToDay = null
            mScrollToHour = -1.0
            mAreDimensionsInvalid = false
        }
        if (mIsFirstDraw) {
            mIsFirstDraw = false

            // If the week view is being drawn for the first time, then consider the first day of the week.
            if ((getRealNumberOfVisibleDays() >= 7) && (mHomeDate!!.get(Calendar.DAY_OF_WEEK) != mFirstDayOfWeek) && mShowFirstDayOfWeekFirst) {
                val difference: Int = (mHomeDate!!.get(Calendar.DAY_OF_WEEK) - mFirstDayOfWeek)
                mCurrentOrigin.x += (mWidthPerDay + mColumnGap) * difference
            }
            setLimitTime(mMinTime, mMaxTime)
        }

        // Calculate the new height due to the zooming.
        if (mNewHourHeight > 0) {
            if (mNewHourHeight < mEffectiveMinHourHeight) mNewHourHeight = mEffectiveMinHourHeight else if (mNewHourHeight > mMaxHourHeight) mNewHourHeight = mMaxHourHeight
            mHourHeight = mNewHourHeight
            mNewHourHeight = -1
        }

        // If the new mCurrentOrigin.y is invalid, make it valid.
        if (mCurrentOrigin.y < height - (mHourHeight * (mMaxTime - mMinTime)) - mHeaderHeight - (mHeaderRowPadding * 2) - mHeaderMarginBottom - (mTimeTextHeight / 2)) mCurrentOrigin.y = height - (mHourHeight * (mMaxTime - mMinTime)) - mHeaderHeight - (mHeaderRowPadding * 2) - mHeaderMarginBottom - (mTimeTextHeight / 2)

        // Don't put an "else if" because it will trigger a glitch when completely zoomed out and
        // scrolling vertically.
        if (mCurrentOrigin.y > 0) {
            mCurrentOrigin.y = 0f
        }
        val leftDaysWithGaps: Int = leftDaysWithGaps
        // Consider scroll offset.
        val startFromPixel: Float = xStartPixel
        var startPixel: Float = startFromPixel

        // Prepare to iterate for each day.
        var day: Calendar = today.clone() as Calendar
        day.add(Calendar.HOUR_OF_DAY, 6)

        // Prepare to iterate for each hour to draw the hour lines.
        var lineCount: Int = (((height - mHeaderHeight - (mHeaderRowPadding * 2) -
                mHeaderMarginBottom)) / mHourHeight).toInt() + 1
        lineCount = (lineCount) * (getRealNumberOfVisibleDays() + 1)
        val hourLines: FloatArray = FloatArray(lineCount * 4)

        // Clear the cache for event rectangles.
        if (mEventRects != null) {
            for (eventRect: EventRect? in mEventRects!!) {
                eventRect!!.rectF = null
            }
        }

        // Clip to paint events only.
        canvas.save()
        canvas.clipRect(mHeaderColumnWidth, mHeaderHeight + (mHeaderRowPadding * 2) + mHeaderMarginBottom + (mTimeTextHeight / 2), width.toFloat(), height.toFloat())

        // Iterate through each day.
        val oldFirstVisibleDay: Calendar? = mFirstVisibleDay
        mFirstVisibleDay = mHomeDate!!.clone() as Calendar
        mFirstVisibleDay!!.add(Calendar.DATE, -(Math.round(mCurrentOrigin.x / (mWidthPerDay + mColumnGap))))
        if (!(mFirstVisibleDay == oldFirstVisibleDay) && scrollListener != null) {
            scrollListener!!.onFirstVisibleDayChanged(mFirstVisibleDay, oldFirstVisibleDay)
        }
        if (mAutoLimitTime) {
            val days: MutableList<Calendar> = ArrayList()
            for (dayNumber in leftDaysWithGaps + 1..leftDaysWithGaps + getRealNumberOfVisibleDays()) {
                day = mHomeDate!!.clone() as Calendar
                day.add(Calendar.DATE, dayNumber - 1)
                days.add(day)
            }
            limitEventTime(days)
        }
        for (dayNumber in leftDaysWithGaps + 1..leftDaysWithGaps + getRealNumberOfVisibleDays() + 1) {

            // Check if the day is today.
            day = mHomeDate!!.clone() as Calendar
            mLastVisibleDay = day.clone() as Calendar
            day.add(Calendar.DATE, dayNumber - 1)
            mLastVisibleDay!!.add(Calendar.DATE, dayNumber - 2)
            val isToday: Boolean = isSameDay(day, today)

            // Don't draw days which are outside requested range
            if (!dateIsValid(day)) {
                continue
            }

            // Get more events if necessary. We want to store the events 3 months beforehand. Get
            // events only when it is the first iteration of the loop.
 /*           if (((mEventRects == null) || mRefreshEvents ||
                            ((dayNumber == leftDaysWithGaps + 1) && (mFetchedPeriod != mWeekViewLoader.toWeekViewPeriodIndex(day) as Int) && (
                                    Math.abs(mFetchedPeriod - mWeekViewLoader.toWeekViewPeriodIndex(day)) > 0.5)))) {
                getMoreEvents(day)
                mRefreshEvents = false
            }
*/
            // Draw background color for each day.
            val start: Float = (if (startPixel < mHeaderColumnWidth) mHeaderColumnWidth else startPixel)
            if (mWidthPerDay + startPixel - start > 0) {
                if (mShowDistinctPastFutureColor) {
                    val isWeekend: Boolean = day.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || day.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                    val pastPaint: Paint? = if (isWeekend && mShowDistinctWeekendColor) mPastWeekendBackgroundPaint else mPastBackgroundPaint
                    val futurePaint: Paint? = if (isWeekend && mShowDistinctWeekendColor) mFutureWeekendBackgroundPaint else mFutureBackgroundPaint
                    val startY: Float = mHeaderHeight + (mHeaderRowPadding * 2) + (mTimeTextHeight / 2) + mHeaderMarginBottom + mCurrentOrigin.y
                    if (isToday) {
                        val now: Calendar = Calendar.getInstance()
                        val beforeNow: Float = (now.get(Calendar.HOUR_OF_DAY) - mMinTime + now.get(Calendar.MINUTE) / 60.0f) * mHourHeight
                        canvas.drawRect(start, startY, startPixel + mWidthPerDay, startY + beforeNow, (pastPaint)!!)
                        canvas.drawRect(start, startY + beforeNow, startPixel + mWidthPerDay, height.toFloat(), (futurePaint)!!)
                    } else if (day.before(today)) {
                        canvas.drawRect(start, startY, startPixel + mWidthPerDay, height.toFloat(), (pastPaint)!!)
                    } else {
                        canvas.drawRect(start, startY, startPixel + mWidthPerDay, height.toFloat(), (futurePaint)!!)
                    }
                } else {
                    canvas.drawRect(start, mHeaderHeight + (mHeaderRowPadding * 2) + (mTimeTextHeight / 2) + mHeaderMarginBottom, startPixel + mWidthPerDay, height.toFloat(), (if (isToday) mTodayBackgroundPaint else mDayBackgroundPaint)!!)
                }
            }

            // Prepare the separator lines for hours.
            var i: Int = 0
            for (hourNumber in mMinTime until mMaxTime) {
                val top: Float = mHeaderHeight + (mHeaderRowPadding * 2) + mCurrentOrigin.y + (mHourHeight * (hourNumber - mMinTime)) + (mTimeTextHeight / 2) + mHeaderMarginBottom
                if ((top > mHeaderHeight + (mHeaderRowPadding * 2) + (mTimeTextHeight / 2) + mHeaderMarginBottom - mHourSeparatorHeight) && (top < height) && (startPixel + mWidthPerDay - start > 0)) {
                    hourLines[i * 4] = start
                    hourLines[i * 4 + 1] = top
                    hourLines[i * 4 + 2] = startPixel + mWidthPerDay
                    hourLines[i * 4 + 3] = top
                    i++
                }
            }

            // Draw the lines for hours.
            canvas.drawLines(hourLines, (mHourSeparatorPaint)!!)

            // Draw the events.
            drawEvents(day, startPixel, canvas)

            // Draw the line at the current time.
            if (mShowNowLine && isToday) {
                val startY: Float = mHeaderHeight + (mHeaderRowPadding * 2) + (mTimeTextHeight / 2) + mHeaderMarginBottom + mCurrentOrigin.y
                val now: Calendar = Calendar.getInstance()
                val beforeNow: Float = (now.get(Calendar.HOUR_OF_DAY) - mMinTime + now.get(Calendar.MINUTE) / 60.0f) * mHourHeight
                val top: Float = startY + beforeNow
                canvas.drawLine(start, top, startPixel + mWidthPerDay, top, (mNowLinePaint)!!)
            }

            // In the next iteration, start from the next day.
            startPixel += mWidthPerDay + mColumnGap
        }
        canvas.restore()

        // Hide everything in the first cell (top left corner).
        canvas.save()
        canvas.clipRect(0f, 0f, mTimeTextWidth + mHeaderColumnPadding * 2, mHeaderHeight + mHeaderRowPadding * 2)
        canvas.drawRect(0f, 0f, mTimeTextWidth + mHeaderColumnPadding * 2, mHeaderHeight + mHeaderRowPadding * 2, (mHeaderBackgroundPaint)!!)
        canvas.restore()

        // Clip to paint header row only.
        canvas.save()
        canvas.clipRect(mHeaderColumnWidth, 0f, width.toFloat(), mHeaderHeight + mHeaderRowPadding * 2)

        // Draw the header background.
        canvas.drawRect(0f, 0f, width.toFloat(), mHeaderHeight + mHeaderRowPadding * 2, (mHeaderBackgroundPaint)!!)
        canvas.restore()

        // Draw the header row texts.
        startPixel = startFromPixel
        for (dayNumber in leftDaysWithGaps + 1..leftDaysWithGaps + getRealNumberOfVisibleDays() + 1) {
            // Check if the day is today.
            day = mHomeDate!!.clone() as Calendar
            day.add(Calendar.DATE, dayNumber - 1)
            val isToday: Boolean = isSameDay(day, today)

            // Don't draw days which are outside requested range
            if (!dateIsValid(day)) continue

            // Draw the day labels.
            val dayLabel: String? = getDateTimeInterpreter()!!.interpretDate(day)
            if (dayLabel == null) throw IllegalStateException("A DateTimeInterpreter must not return null date")
            canvas.drawText(dayLabel, startPixel + mWidthPerDay / 2, mHeaderTextHeight + mHeaderRowPadding, (if (isToday) mTodayHeaderTextPaint else mHeaderTextPaint)!!)
            drawAllDayEvents(day, startPixel, canvas)
            startPixel += mWidthPerDay + mColumnGap
        }
    }

    /**
     * Get the time and date where the user clicked on.
     *
     * @param x The x position of the touch event.
     * @param y The y position of the touch event.
     * @return The time and date at the clicked position.
     */
    private fun getTimeFromPoint(x: Float, y: Float): Calendar? {
        val leftDaysWithGaps: Int = leftDaysWithGaps
        var startPixel: Float = xStartPixel
        for (dayNumber in leftDaysWithGaps + 1..leftDaysWithGaps + getRealNumberOfVisibleDays() + 1) {
            val start: Float = (if (startPixel < mHeaderColumnWidth) mHeaderColumnWidth else startPixel)
            if ((mWidthPerDay + startPixel - start > 0) && (x > start) && (x < startPixel + mWidthPerDay)) {
                val day: Calendar = mHomeDate!!.clone() as Calendar
                day.add(Calendar.DATE, dayNumber - 1)
                val pixelsFromZero: Float = (y - mCurrentOrigin.y - mHeaderHeight
                        - (mHeaderRowPadding * 2) - (mTimeTextHeight / 2) - mHeaderMarginBottom)
                val hour: Int = (pixelsFromZero / mHourHeight).toInt()
                val minute: Int = (60 * (pixelsFromZero - hour * mHourHeight) / mHourHeight).toInt()
                day.add(Calendar.HOUR_OF_DAY, hour + mMinTime)
                day.set(Calendar.MINUTE, minute)
                return day
            }
            startPixel += mWidthPerDay + mColumnGap
        }
        return null
    }

    /**
     * limit current time of event by update mMinTime & mMaxTime
     * find smallest of start time & latest of end time
     */
    private fun limitEventTime(dates: List<Calendar>) {
        if (mEventRects != null && mEventRects!!.size > 0) {
            var startTime: Calendar? = null
            var endTime: Calendar? = null
            for (eventRect: EventRect? in mEventRects!!) {
                for (date: Calendar in dates) {
                    if (isSameDay(eventRect!!.event.getStartTime(), date) && !eventRect!!.event.isAllDay()) {
                        if (startTime == null || getPassedMinutesInDay(startTime) > getPassedMinutesInDay(eventRect.event.getStartTime())) {
                            startTime = eventRect.event.getStartTime()
                        }
                        if (endTime == null || getPassedMinutesInDay(endTime) < getPassedMinutesInDay(eventRect.event.getEndTime())) {
                            endTime = eventRect.event.getEndTime()
                        }
                    }
                }
            }
            if ((startTime != null) && (endTime != null) && startTime.before(endTime)) {
                setLimitTime(Math.max(0, startTime.get(Calendar.HOUR_OF_DAY)),
                        Math.min(24, endTime.get(Calendar.HOUR_OF_DAY) + 1))
                return
            }
        }
    }

    private val minHourOffset: Int
        private get() {
            return mHourHeight * mMinTime
        }

    // Calculate top.
    private val eventsTop: Float
        private get() {
            // Calculate top.
            return mCurrentOrigin.y + mHeaderHeight + (mHeaderRowPadding * 2) + mHeaderMarginBottom + (mTimeTextHeight / 2) + mEventMarginVertical - minHourOffset
        }

    private val leftDaysWithGaps: Int
        private get() {
            return (-(Math.ceil((mCurrentOrigin.x / (mWidthPerDay + mColumnGap)).toDouble()))).toInt()
        }
    private val xStartPixel: Float
        private get() {
            return (mCurrentOrigin.x + ((mWidthPerDay + mColumnGap) * leftDaysWithGaps) +
                    mHeaderColumnWidth)
        }

    /**
     * Draw all the events of a particular day.
     *
     * @param date           The day.
     * @param startFromPixel The left position of the day area. The events will never go any left from this value.
     * @param canvas         The canvas to draw upon.
     */
    private fun drawEvents(date: Calendar, startFromPixel: Float, canvas: Canvas) {
        if (mEventRects != null && mEventRects!!.size > 0) {
            for (i in mEventRects!!.indices) {
                if (isSameDay(mEventRects!!.get(i)!!.event.getStartTime(), date) && !mEventRects!!.get(i)!!.event.isAllDay()) {
                    val top: Float = mHourHeight * mEventRects!!.get(i)!!.top / 60 + eventsTop
                    val bottom: Float = mHourHeight * mEventRects!!.get(i)!!.bottom / 60 + eventsTop

                    // Calculate left and right.
                    var left: Float = startFromPixel + mEventRects!!.get(i)!!.left * mWidthPerDay
                    if (left < startFromPixel) left += mOverlappingEventGap.toFloat()
                    var right: Float = left + mEventRects!!.get(i)!!.width * mWidthPerDay
                    if (right < startFromPixel + mWidthPerDay) right -= mOverlappingEventGap.toFloat()

                    // Draw the event and the event name on top of it.
                    if ((left < right) && (
                                    left < width) && (
                                    top < height) && (
                                    right > mHeaderColumnWidth) && (
                                    bottom > mHeaderHeight + (mHeaderRowPadding * 2) + (mTimeTextHeight / 2) + mHeaderMarginBottom)) {
                        mEventRects!!.get(i)!!.rectF = RectF(left, top, right, bottom)
                        mEventBackgroundPaint!!.color = if (mEventRects!!.get(i)!!.event.getColor() === 0) mDefaultEventColor else mEventRects!!.get(i)!!.event.getColor()
                        mEventBackgroundPaint!!.shader = mEventRects!!.get(i)!!.event.getShader()
                        canvas.drawRoundRect(mEventRects!!.get(i)!!.rectF!!, mEventCornerRadius.toFloat(), mEventCornerRadius.toFloat(), (mEventBackgroundPaint)!!)
                        var topToUse: Float = top
                        if (mEventRects!!.get(i)!!.event.getStartTime().get(Calendar.HOUR_OF_DAY) < mMinTime) topToUse = mHourHeight * getPassedMinutesInDay(mMinTime, 0) / 60 + eventsTop
                        if (!(mNewEventIdentifier == mEventRects!!.get(i)!!.event.getIdentifier())) drawEventTitle(mEventRects!!.get(i)!!.event, mEventRects!!.get(i)!!.rectF, canvas, topToUse, left) else drawEmptyImage(mEventRects!!.get(i)!!.event, mEventRects!!.get(i)!!.rectF, canvas, topToUse, left)
                    } else mEventRects!!.get(i)!!.rectF = null
                }
            }
        }
    }

    /**
     * Draw all the Allday-events of a particular day.
     *
     * @param date           The day.
     * @param startFromPixel The left position of the day area. The events will never go any left from this value.
     * @param canvas         The canvas to draw upon.
     */
    private fun drawAllDayEvents(date: Calendar, startFromPixel: Float, canvas: Canvas) {
        if (mEventRects != null && mEventRects!!.size > 0) {
            for (i in mEventRects!!.indices) {
                if (isSameDay(mEventRects!!.get(i)!!.event.getStartTime(), date) && mEventRects!!.get(i)!!.event.isAllDay()) {

                    // Calculate top.
                    val top: Float = (mHeaderRowPadding * 2) + mHeaderMarginBottom + (+mTimeTextHeight / 2) + mEventMarginVertical

                    // Calculate bottom.
                    val bottom: Float = top + mEventRects!!.get(i)!!.bottom

                    // Calculate left and right.
                    var left: Float = startFromPixel + mEventRects!!.get(i)!!.left * mWidthPerDay
                    if (left < startFromPixel) left += mOverlappingEventGap.toFloat()
                    var right: Float = left + mEventRects!!.get(i)!!.width * mWidthPerDay
                    if (right < startFromPixel + mWidthPerDay) right -= mOverlappingEventGap.toFloat()

                    // Draw the event and the event name on top of it.
                    if ((left < right) && (
                                    left < width) && (
                                    top < height) && (
                                    right > mHeaderColumnWidth) && (
                                    bottom > 0)) {
                        mEventRects!!.get(i)!!.rectF = RectF(left, top, right, bottom)
                        mEventBackgroundPaint!!.color = if (mEventRects!!.get(i)!!.event.getColor() === 0) mDefaultEventColor else mEventRects!!.get(i)!!.event.getColor()
                        mEventBackgroundPaint!!.shader = mEventRects!!.get(i)!!.event.getShader()
                        canvas.drawRoundRect(mEventRects!!.get(i)!!.rectF!!, mEventCornerRadius.toFloat(), mEventCornerRadius.toFloat(), (mEventBackgroundPaint)!!)
                        drawEventTitle(mEventRects!!.get(i)!!.event, mEventRects!!.get(i)!!.rectF, canvas, top, left)
                    } else mEventRects!!.get(i)!!.rectF = null
                }
            }
        }
    }

    /**
     * Draw the name of the event on top of the event rectangle.
     *
     * @param event        The event of which the title (and location) should be drawn.
     * @param rect         The rectangle on which the text is to be drawn.
     * @param canvas       The canvas to draw upon.
     * @param originalTop  The original top position of the rectangle. The rectangle may have some of its portion outside of the visible area.
     * @param originalLeft The original left position of the rectangle. The rectangle may have some of its portion outside of the visible area.
     */
    private fun drawEventTitle(event: WeekViewEvent, rect: RectF?, canvas: Canvas, originalTop: Float, originalLeft: Float) {
        if (rect!!.right - rect.left - (mEventPadding * 2) < 0) return
        if (rect.bottom - rect.top - (mEventPadding * 2) < 0) return

        // Prepare the name of the event.
        val bob: SpannableStringBuilder = SpannableStringBuilder()
        if (!TextUtils.isEmpty(event.getName())) {
            bob.append(event.getName())
            bob.setSpan(StyleSpan(Typeface.BOLD), 0, bob.length, 0)
        }
        // Prepare the location of the event.
        if (!TextUtils.isEmpty(event.getLocation())) {
            if (bob.length > 0) bob.append(' ')
            bob.append(event.getLocation())
        }
        val availableHeight: Int = (rect.bottom - originalTop - (mEventPadding * 2)).toInt()
        val availableWidth: Int = (rect.right - originalLeft - (mEventPadding * 2)).toInt()

        // Get text color if necessary
        if (textColorPicker != null) {
            mEventTextPaint!!.color = textColorPicker!!.getTextColor(event)
        }
        // Get text dimensions.
        var textLayout: StaticLayout = StaticLayout(bob, mEventTextPaint, availableWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false)
        if (textLayout.lineCount > 0) {
            val lineHeight: Int = textLayout.height / textLayout.lineCount
            if (availableHeight >= lineHeight) {
                // Calculate available number of line counts.
                var availableLineCount: Int = availableHeight / lineHeight
                do {
                    // Ellipsize text to fit into event rect.
                    if (!(mNewEventIdentifier == event.getIdentifier())) textLayout = StaticLayout(TextUtils.ellipsize(bob, mEventTextPaint, (availableLineCount * availableWidth).toFloat(), TextUtils.TruncateAt.END), mEventTextPaint, (rect.right - originalLeft - (mEventPadding * 2)).toInt(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false)

                    // Reduce line count.
                    availableLineCount--

                    // Repeat until text is short enough.
                } while (textLayout.height > availableHeight)

                // Draw text.
                canvas.save()
                canvas.translate(originalLeft + mEventPadding, originalTop + mEventPadding)
                textLayout.draw(canvas)
                canvas.restore()
            }
        }
    }

    /**
     * Draw the text on top of the rectangle in the empty event.
     */
    private fun drawEmptyImage(event: WeekViewEvent, rect: RectF?, canvas: Canvas, originalTop: Float, originalLeft: Float) {
        val size: Int = Math.max(1, Math.floor(Math.min(0.8 * rect!!.height(), 0.8 * rect.width())).toInt())
        if (mNewEventIconDrawable == null) mNewEventIconDrawable = resources.getDrawable(android.R.drawable.ic_menu_add)
        var icon: Bitmap = (mNewEventIconDrawable as BitmapDrawable?)!!.bitmap
        icon = Bitmap.createScaledBitmap(icon, size, size, false)
        canvas.drawBitmap(icon, originalLeft + (rect.width() - icon.width) / 2, originalTop + (rect.height() - icon.height) / 2, Paint())
    }

    /**
     * A class to hold reference to the events and their visual representation. An EventRect is
     * actually the rectangle that is drawn on the calendar for a given event. There may be more
     * than one rectangle for a single event (an event that expands more than one day). In that
     * case two instances of the EventRect will be used for a single event. The given event will be
     * stored in "originalEvent". But the event that corresponds to rectangle the rectangle
     * instance will be stored in "event".
     */
    private inner class EventRect(event: WeekViewEvent, originalEvent: WeekViewEvent, rectF: RectF?) {
        var event: WeekViewEvent
        var originalEvent: WeekViewEvent
        var rectF: RectF?
        var left: Float = 0f
        var width: Float = 0f
        var top: Float = 0f
        var bottom: Float = 0f

        /**
         * Create a new instance of event rect. An EventRect is actually the rectangle that is drawn
         * on the calendar for a given event. There may be more than one rectangle for a single
         * event (an event that expands more than one day). In that case two instances of the
         * EventRect will be used for a single event. The given event will be stored in
         * "originalEvent". But the event that corresponds to rectangle the rectangle instance will
         * be stored in "event".
         *
         * @param event         Represents the event which this instance of rectangle represents.
         * @param originalEvent The original event that was passed by the user.
         * @param rectF         The rectangle.
         */
        init {
            this.event = event
            this.rectF = rectF
            this.originalEvent = originalEvent
        }
    }

    /**
     * Gets more events of one/more month(s) if necessary. This method is called when the user is
     * scrolling the week view. The week view stores the events of three months: the visible month,
     * the previous month, the next month.
     *
     * @param day The day where the user is currently is.
     */
    private fun getMoreEvents(day: Calendar) {

        // Get more events if the month is changed.
        if (mEventRects == null) mEventRects = ArrayList()
        if (mEvents == null) mEvents = ArrayList<WeekViewEvent?>()
       /* if (mWeekViewLoader == null && !isInEditMode) throw IllegalStateException("You must provide a MonthChangeListener")*/

        // If a refresh was requested then reset some variables.
        if (mRefreshEvents) {
            clearEvents()
            mFetchedPeriod = -1
        }
       /* if (mWeekViewLoader != null) {
            val periodToFetch: Int = mWeekViewLoader.toWeekViewPeriodIndex(day)
            if (!isInEditMode && ((mFetchedPeriod < 0) || (mFetchedPeriod != periodToFetch) || mRefreshEvents)) {
                val newEvents: List<WeekViewEvent?> = mWeekViewLoader.onLoad(periodToFetch)

                // Clear events.
                clearEvents()
                cacheAndSortEvents(newEvents)
                calculateHeaderHeight()
                mFetchedPeriod = periodToFetch
            }
        }*/

        // Prepare to calculate positions of each events.
        val tempEvents: ArrayList<EventRect?> = (mEventRects as ArrayList<EventRect?>?)!!
        mEventRects = ArrayList()

        // Iterate through each day with events to calculate the position of the events.
        while (tempEvents.size > 0) {
            val eventRects: ArrayList<EventRect?> = ArrayList(tempEvents.size)

            // Get first event for a day.
            val eventRect1: EventRect? = tempEvents.removeAt(0)
            eventRects.add(eventRect1)
            var i: Int = 0
            while (i < tempEvents.size) {
                // Collect all other events for same day.
                val eventRect2: EventRect? = tempEvents.get(i)
                if (isSameDay(eventRect1!!.event.getStartTime(), eventRect2!!.event.getStartTime())) {
                    tempEvents.removeAt(i)
                    eventRects.add(eventRect2)
                } else {
                    i++
                }
            }
            computePositionOfEvents(eventRects)
        }
    }

    private fun clearEvents() {
        mEventRects!!.clear()
        mEvents!!.clear()
    }

    /**
     * Cache the event for smooth scrolling functionality.
     *
     * @param event The event to cache.
     */
    private fun cacheEvent(event: WeekViewEvent?) {
        if (event!!.getStartTime().compareTo(event!!.getEndTime()) >= 0) return
        val splitedEvents: List<WeekViewEvent> = event.splitWeekViewEvents()
        for (splitedEvent: WeekViewEvent in splitedEvents) {
            mEventRects!!.add(EventRect(splitedEvent, event, null))
        }
        mEvents!!.add(event)
    }

    /**
     * Cache and sort events.
     *
     * @param events The events to be cached and sorted.
     */
    private fun cacheAndSortEvents(events: List<WeekViewEvent?>) {
        for (event: WeekViewEvent? in events) {
            cacheEvent(event)
        }
        sortEventRects(mEventRects)
    }

    /**
     * Sorts the events in ascending order.
     *
     * @param eventRects The events to be sorted.
     */
    private fun sortEventRects(eventRects: List<EventRect?>?) {
        Collections.sort(eventRects, object : Comparator<EventRect?> {

            override fun compare(left: EventRect?, right: EventRect?): Int {
                val start1: Long = left!!.event.getStartTime().getTimeInMillis()
                val start2: Long = right!!.event.getStartTime().getTimeInMillis()
                var comparator: Int = if (start1 > start2) 1 else (if (start1 < start2) -1 else 0)
                if (comparator == 0) {
                    val end1: Long = left.event.getEndTime().getTimeInMillis()
                    val end2: Long = right.event.getEndTime().getTimeInMillis()
                    comparator = if (end1 > end2) 1 else (if (end1 < end2) -1 else 0)
                }
                return comparator
            }
        })
    }

    /**
     * Calculates the left and right positions of each events. This comes handy specially if events
     * are overlapping.
     *
     * @param eventRects The events along with their wrapper class.
     */
    private fun computePositionOfEvents(eventRects: List<EventRect?>?) {
        // Make "collision groups" for all events that collide with others.
        val collisionGroups: MutableList<MutableList<EventRect?>> = ArrayList()
        for (eventRect: EventRect? in eventRects!!) {
            var isPlaced: Boolean = false
            outerLoop@ for (collisionGroup: MutableList<EventRect?> in collisionGroups) {
                for (groupEvent: EventRect? in collisionGroup) {
                    if (isEventsCollide(groupEvent!!.event, eventRect!!.event) && groupEvent.event.isAllDay() === eventRect.event.isAllDay()) {
                        collisionGroup.add(eventRect)
                        isPlaced = true
                        break@outerLoop
                    }
                }
            }
            if (!isPlaced) {
                val newGroup: MutableList<EventRect?> = ArrayList()
                newGroup.add(eventRect)
                collisionGroups.add(newGroup)
            }
        }
        for (collisionGroup: List<EventRect?> in collisionGroups) {
            expandEventsToMaxWidth(collisionGroup)
        }
    }

    /**
     * Expands all the events to maximum possible width. The events will try to occupy maximum
     * space available horizontally.
     *
     * @param collisionGroup The group of events which overlap with each other.
     */
    private fun expandEventsToMaxWidth(collisionGroup: List<EventRect?>) {
        // Expand the events to maximum possible width.
        val columns: MutableList<MutableList<EventRect?>> = ArrayList()
        columns.add(ArrayList())
        for (eventRect: EventRect? in collisionGroup) {
            var isPlaced: Boolean = false
            for (column: MutableList<EventRect?> in columns) {
                if (column.size == 0) {
                    column.add(eventRect)
                    isPlaced = true
                } else if (!isEventsCollide(eventRect!!.event, column.get(column.size - 1)!!.event)) {
                    column.add(eventRect)
                    isPlaced = true
                    break
                }
            }
            if (!isPlaced) {
                val newColumn: MutableList<EventRect?> = ArrayList()
                newColumn.add(eventRect)
                columns.add(newColumn)
            }
        }

        // Calculate left and right position for all the events.
        // Get the maxRowCount by looking in all columns.
        var maxRowCount: Int = 0
        for (column: List<EventRect?> in columns) {
            maxRowCount = Math.max(maxRowCount, column.size)
        }
        for (i in 0 until maxRowCount) {
            // Set the left and right values of the event.
            var j: Float = 0f
            for (column: List<EventRect?> in columns) {
                if (column.size >= i + 1) {
                    val eventRect: EventRect? = column.get(i)
                    eventRect!!.width = 1f / columns.size
                    eventRect.left = j / columns.size
                    if (!eventRect.event.isAllDay()) {
                        eventRect.top = getPassedMinutesInDay(eventRect.event.getStartTime()).toFloat()
                        eventRect.bottom = getPassedMinutesInDay(eventRect.event.getEndTime()).toFloat()
                    } else {
                        eventRect.top = 0f
                        eventRect.bottom = mAllDayEventHeight.toFloat()
                    }
                    mEventRects!!.add(eventRect)
                }
                j++
            }
        }
    }

    /**
     * Checks if two events overlap.
     *
     * @param event1 The first event.
     * @param event2 The second event.
     * @return true if the events overlap.
     */
    private fun isEventsCollide(event1: WeekViewEvent, event2: WeekViewEvent): Boolean {
        val start1: Long = event1.getStartTime().getTimeInMillis()
        val end1: Long = event1.getEndTime().getTimeInMillis()
        val start2: Long = event2.getStartTime().getTimeInMillis()
        val end2: Long = event2.getEndTime().getTimeInMillis()
        val minOverlappingMillis: Long = (mMinOverlappingMinutes * 60 * 1000).toLong()
        return !((start1 + minOverlappingMillis >= end2) || (end1 <= start2 + minOverlappingMillis))
    }

    /**
     * Checks if time1 occurs after (or at the same time) time2.
     *
     * @param time1 The time to check.
     * @param time2 The time to check against.
     * @return true if time1 and time2 are equal or if time1 is after time2. Otherwise false.
     */
    private fun isTimeAfterOrEquals(time1: Calendar?, time2: Calendar?): Boolean {
        return !(time1 == null || time2 == null) && time1.timeInMillis >= time2.timeInMillis
    }

    override fun invalidate() {
        super.invalidate()
        mAreDimensionsInvalid = true
    }

    /////////////////////////////////////////////////////////////////
    //
    //      Functions related to setting and getting the properties.
    //
    /////////////////////////////////////////////////////////////////
    fun setOnEventClickListener(listener: EventClickListener?) {
        eventClickListener = listener
    }

    fun setDropListener(dropListener: DropListener?) {
        mDropListener = dropListener
    }

  /*  @get:Nullable
    var monthChangeListener: MonthLoader.MonthChangeListener?
        get() {
            if (mWeekViewLoader is MonthLoader) return (mWeekViewLoader as MonthLoader?).getOnMonthChangeListener()
            return null
        }
        set(monthChangeListener) {
            mWeekViewLoader = MonthLoader(monthChangeListener)
        }*/
    /**
     * Get event loader in the week view. Event loaders define the  interval after which the events
     * are loaded in week view. For a MonthLoader events are loaded for every month. You can define
     * your custom event loader by extending WeekViewLoader.
     *
     * @return The event loader.
     */
    /**
     * Set event loader in the week view. For example, a MonthLoader. Event loaders define the
     * interval after which the events are loaded in week view. For a MonthLoader events are loaded
     * for every month. You can define your custom event loader by extending WeekViewLoader.
     *
     * @param loader The event loader.
     */
    /*var weekViewLoader: WeekViewLoader?
        get() {
            return mWeekViewLoader
        }
        set(loader) {
            mWeekViewLoader = loader
        }
*/
    fun setZoomEndListener(zoomEndListener: ZoomEndListener?) {
        mZoomEndListener = zoomEndListener
    }
    /**
     * Get the interpreter which provides the text to show in the header column and the header row.
     *
     * @return The date, time interpreter.
     */// Refresh time column width.
    /**
     * Set the interpreter which provides the text to show in the header column and the header row.
     *
     * @param dateTimeInterpreter The date, time interpreter.
     */
    open fun getDateTimeInterpreter(): DateTimeInterpreter? {
        if (mDateTimeInterpreter == null) {
            mDateTimeInterpreter = object : DateTimeInterpreter {
                override fun interpretDate(date: Calendar): String? {
                    return try {
                        val sdf = if (mDayNameLength == com.alamkanak.weekview.NewGridLayout.LENGTH_SHORT) SimpleDateFormat("EEEEE M/dd", Locale.getDefault()) else SimpleDateFormat("EEE M/dd", Locale.getDefault())
                        sdf.format(date.time).toUpperCase()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        ""
                    }
                }

                override fun interpretTime(hour: Int, minutes: Int): String? {
                    val calendar = Calendar.getInstance()
                    calendar[Calendar.HOUR_OF_DAY] = hour
                    calendar[Calendar.MINUTE] = minutes
                    return try {
                        val sdf: SimpleDateFormat
                        sdf = if (DateFormat.is24HourFormat(context)) {
                            SimpleDateFormat("HH:mm", Locale.getDefault())
                        } else {
                            if (mTimeColumnResolution % 60 != 0) {
                                SimpleDateFormat("hh:mm a", Locale.getDefault())
                            } else {
                                SimpleDateFormat("hh a", Locale.getDefault())
                            }
                        }
                        sdf.format(calendar.time)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        ""
                    }
                }
            }
        }
        return mDateTimeInterpreter
    }

    /**
     * Set the interpreter which provides the text to show in the header column and the header row.
     *
     * @param dateTimeInterpreter The date, time interpreter.
     */
    fun setDateTimeInterpreter(dateTimeInterpreter: DateTimeInterpreter?) {
        mDateTimeInterpreter = dateTimeInterpreter

        // Refresh time column width.
        initTextTimeWidth()
    }

    /**
     * Get the real number of visible days
     * If the amount of days between max date and min date is smaller, that value is returned
     *
     * @return The real number of visible days
     */
    fun getRealNumberOfVisibleDays(): Int {
        if (mMinDate == null || mMaxDate == null) return getNumberOfVisibleDays()
        return Math.min(mNumberOfVisibleDays, daysBetween(mMinDate, mMaxDate) + 1)
    }

    /**
     * Get the number of visible days
     *
     * @return The set number of visible days.
     */
    fun getNumberOfVisibleDays(): Int {
        return mNumberOfVisibleDays
    }

    /**
     * Set the number of visible days in a week.
     *
     * @param numberOfVisibleDays The number of visible days in a week.
     */
    fun setNumberOfVisibleDays(numberOfVisibleDays: Int) {
        mNumberOfVisibleDays = numberOfVisibleDays
        resetHomeDate()
        mCurrentOrigin.x = 0f
        mCurrentOrigin.y = 0f
        invalidate()
    }

    fun getHourHeight(): Int {
        return mHourHeight
    }

    fun setHourHeight(hourHeight: Int) {
        mNewHourHeight = hourHeight
        invalidate()
    }

    fun getColumnGap(): Int {
        return mColumnGap
    }

    fun setColumnGap(columnGap: Int) {
        mColumnGap = columnGap
        invalidate()
    }

    fun getFirstDayOfWeek(): Int {
        return mFirstDayOfWeek
    }

    /**
     * Set the first day of the week. First day of the week is used only when the week view is first
     * drawn. It does not of any effect after user starts scrolling horizontally.
     *
     *
     * **Note:** This method will only work if the week view is set to display more than 6 days at
     * once.
     *
     *
     * @param firstDayOfWeek The supported values are [java.util.Calendar.SUNDAY],
     * [java.util.Calendar.MONDAY], [java.util.Calendar.TUESDAY],
     * [java.util.Calendar.WEDNESDAY], [java.util.Calendar.THURSDAY],
     * [java.util.Calendar.FRIDAY].
     */
    fun setFirstDayOfWeek(firstDayOfWeek: Int) {
        mFirstDayOfWeek = firstDayOfWeek
        invalidate()
    }

    fun isShowFirstDayOfWeekFirst(): Boolean {
        return mShowFirstDayOfWeekFirst
    }

    fun setShowFirstDayOfWeekFirst(show: Boolean) {
        mShowFirstDayOfWeekFirst = show
    }

    fun getTextSize(): Int {
        return mTextSize
    }

    fun setTextSize(textSize: Int) {
        mTextSize = textSize
        mTodayHeaderTextPaint!!.textSize = mTextSize.toFloat()
        mHeaderTextPaint!!.textSize = mTextSize.toFloat()
        mTimeTextPaint!!.textSize = mTextSize.toFloat()
        invalidate()
    }

    fun getHeaderColumnPadding(): Int {
        return mHeaderColumnPadding
    }

    fun setHeaderColumnPadding(headerColumnPadding: Int) {
        mHeaderColumnPadding = headerColumnPadding
        invalidate()
    }

    fun getHeaderColumnTextColor(): Int {
        return mHeaderColumnTextColor
    }

    fun setHeaderColumnTextColor(headerColumnTextColor: Int) {
        mHeaderColumnTextColor = headerColumnTextColor
        mHeaderTextPaint!!.color = mHeaderColumnTextColor
        mTimeTextPaint!!.color = mHeaderColumnTextColor
        invalidate()
    }

    fun setTypeface(typeface: Typeface?) {
        if (typeface != null) {
            mEventTextPaint!!.typeface = typeface
            mTodayHeaderTextPaint!!.typeface = typeface
            mTimeTextPaint!!.typeface = typeface
            mTypeface = typeface
            init()
        }
    }

    fun getHeaderRowPadding(): Int {
        return mHeaderRowPadding
    }

    fun setHeaderRowPadding(headerRowPadding: Int) {
        mHeaderRowPadding = headerRowPadding
        invalidate()
    }

    fun getHeaderRowBackgroundColor(): Int {
        return mHeaderRowBackgroundColor
    }

    fun setHeaderRowBackgroundColor(headerRowBackgroundColor: Int) {
        mHeaderRowBackgroundColor = headerRowBackgroundColor
        mHeaderBackgroundPaint!!.color = mHeaderRowBackgroundColor
        invalidate()
    }

    fun getDayBackgroundColor(): Int {
        return mDayBackgroundColor
    }

    fun setDayBackgroundColor(dayBackgroundColor: Int) {
        mDayBackgroundColor = dayBackgroundColor
        mDayBackgroundPaint!!.color = mDayBackgroundColor
        invalidate()
    }

    fun getHourSeparatorColor(): Int {
        return mHourSeparatorColor
    }

    fun setHourSeparatorColor(hourSeparatorColor: Int) {
        mHourSeparatorColor = hourSeparatorColor
        mHourSeparatorPaint!!.color = mHourSeparatorColor
        invalidate()
    }

    fun getTodayBackgroundColor(): Int {
        return mTodayBackgroundColor
    }

    fun setTodayBackgroundColor(todayBackgroundColor: Int) {
        mTodayBackgroundColor = todayBackgroundColor
        mTodayBackgroundPaint!!.color = mTodayBackgroundColor
        invalidate()
    }

    fun getHourSeparatorHeight(): Int {
        return mHourSeparatorHeight
    }

    fun setHourSeparatorHeight(hourSeparatorHeight: Int) {
        mHourSeparatorHeight = hourSeparatorHeight
        mHourSeparatorPaint!!.strokeWidth = mHourSeparatorHeight.toFloat()
        invalidate()
    }

    fun getTodayHeaderTextColor(): Int {
        return mTodayHeaderTextColor
    }

    fun setTodayHeaderTextColor(todayHeaderTextColor: Int) {
        mTodayHeaderTextColor = todayHeaderTextColor
        mTodayHeaderTextPaint!!.color = mTodayHeaderTextColor
        invalidate()
    }

    fun getEventTextSize(): Int {
        return mEventTextSize
    }

    fun setEventTextSize(eventTextSize: Int) {
        mEventTextSize = eventTextSize
        mEventTextPaint!!.textSize = mEventTextSize.toFloat()
        invalidate()
    }

    fun getEventTextColor(): Int {
        return mEventTextColor
    }

    fun setEventTextColor(eventTextColor: Int) {
        mEventTextColor = eventTextColor
        mEventTextPaint!!.color = mEventTextColor
        invalidate()
    }

    fun setTextColorPicker(textColorPicker: TextColorPicker?) {
        this.textColorPicker = textColorPicker
    }

    fun getTextColorPicker(): TextColorPicker? {
        return textColorPicker
    }

    fun getEventPadding(): Int {
        return mEventPadding
    }

    fun setEventPadding(eventPadding: Int) {
        mEventPadding = eventPadding
        invalidate()
    }

    fun getHeaderColumnBackgroundColor(): Int {
        return mHeaderColumnBackgroundColor
    }

    fun setHeaderColumnBackgroundColor(headerColumnBackgroundColor: Int) {
        mHeaderColumnBackgroundColor = headerColumnBackgroundColor
        mHeaderColumnBackgroundPaint!!.color = mHeaderColumnBackgroundColor
        invalidate()
    }

    fun getDefaultEventColor(): Int {
        return mDefaultEventColor
    }

    fun setDefaultEventColor(defaultEventColor: Int) {
        mDefaultEventColor = defaultEventColor
        invalidate()
    }

    fun getNewEventColor(): Int {
        return mNewEventColor
    }

    fun setNewEventColor(defaultNewEventColor: Int) {
        mNewEventColor = defaultNewEventColor
        invalidate()
    }

    fun getNewEventIdentifier(): String? {
        return mNewEventIdentifier
    }

    @Deprecated("")
    fun getNewEventId(): Int {
        return mNewEventIdentifier!!.toInt()
    }

    fun setNewEventIdentifier(newEventId: String?) {
        mNewEventIdentifier = newEventId
    }

    @Deprecated("")
    fun setNewEventId(newEventId: Int) {
        mNewEventIdentifier = newEventId.toString()
    }

    fun getNewEventLengthInMinutes(): Int {
        return mNewEventLengthInMinutes
    }

    fun setNewEventLengthInMinutes(newEventLengthInMinutes: Int) {
        mNewEventLengthInMinutes = newEventLengthInMinutes
    }

    fun getNewEventTimeResolutionInMinutes(): Int {
        return mNewEventTimeResolutionInMinutes
    }

    fun setNewEventTimeResolutionInMinutes(newEventTimeResolutionInMinutes: Int) {
        mNewEventTimeResolutionInMinutes = newEventTimeResolutionInMinutes
    }

    /**
     * **Note:** Use [.setDateTimeInterpreter] and
     * [.getDateTimeInterpreter] instead.
     *
     * @return Either long or short day name is being used.
     */
    @Deprecated("")
    fun getDayNameLength(): Int {
        return mDayNameLength
    }

    /**
     * Set the length of the day name displayed in the header row. Example of short day names is
     * 'M' for 'Monday' and example of long day names is 'Mon' for 'Monday'.
     *
     *
     * **Note:** Use [.setDateTimeInterpreter] instead.
     *
     *
     * @param length Supported values are [com.alamkanak.weekview.NewGridLayout.LENGTH_SHORT] and
     * [com.alamkanak.weekview.NewGridLayout.LENGTH_LONG].
     */
    @Deprecated("")
    fun setDayNameLength(length: Int) {
        if (length != LENGTH_LONG && length != LENGTH_SHORT) {
            throw IllegalArgumentException("length parameter must be either LENGTH_LONG or LENGTH_SHORT")
        }
        mDayNameLength = length
    }

    fun getOverlappingEventGap(): Int {
        return mOverlappingEventGap
    }

    /**
     * Set the gap between overlapping events.
     *
     * @param overlappingEventGap The gap between overlapping events.
     */
    fun setOverlappingEventGap(overlappingEventGap: Int) {
        mOverlappingEventGap = overlappingEventGap
        invalidate()
    }

    fun getEventCornerRadius(): Int {
        return mEventCornerRadius
    }

    /**
     * Set corner radius for event rect.
     *
     * @param eventCornerRadius the radius in px.
     */
    fun setEventCornerRadius(eventCornerRadius: Int) {
        mEventCornerRadius = eventCornerRadius
    }

    fun getEventMarginVertical(): Int {
        return mEventMarginVertical
    }

    /**
     * Set the top and bottom margin of the event. The event will release this margin from the top
     * and bottom edge. This margin is useful for differentiation consecutive events.
     *
     * @param eventMarginVertical The top and bottom margin.
     */
    fun setEventMarginVertical(eventMarginVertical: Int) {
        mEventMarginVertical = eventMarginVertical
        invalidate()
    }

    /**
     * Returns the first visible day in the week view.
     *
     * @return The first visible day in the week view.
     */
    fun getFirstVisibleDay(): Calendar? {
        return mFirstVisibleDay
    }

    /**
     * Returns the last visible day in the week view.
     *
     * @return The last visible day in the week view.
     */
    fun getLastVisibleDay(): Calendar? {
        return mLastVisibleDay
    }

    /**
     * Get the scrolling speed factor in horizontal direction.
     *
     * @return The speed factor in horizontal direction.
     */
    fun getXScrollingSpeed(): Float {
        return mXScrollingSpeed
    }

    /**
     * Sets the speed for horizontal scrolling.
     *
     * @param xScrollingSpeed The new horizontal scrolling speed.
     */
    fun setXScrollingSpeed(xScrollingSpeed: Float) {
        mXScrollingSpeed = xScrollingSpeed
    }

    /**
     * Get the earliest day that can be displayed. Will return null if no minimum date is set.
     *
     * @return the earliest day that can be displayed, null if no minimum date set
     */
    fun getMinDate(): Calendar? {
        return mMinDate
    }

    /**
     * Set the earliest day that can be displayed. This will determine the left horizontal scroll
     * limit. The default value is null (allow unlimited scrolling into the past).
     *
     * @param minDate The new minimum date (pass null for no minimum)
     */
    fun setMinDate(minDate: Calendar?) {
        if (minDate != null) {
            minDate.set(Calendar.HOUR_OF_DAY, 0)
            minDate.set(Calendar.MINUTE, 0)
            minDate.set(Calendar.SECOND, 0)
            minDate.set(Calendar.MILLISECOND, 0)
            if (mMaxDate != null && minDate.after(mMaxDate)) {
                throw IllegalArgumentException("minDate cannot be later than maxDate")
            }
        }
        mMinDate = minDate
        resetHomeDate()
        mCurrentOrigin.x = 0f
        invalidate()
    }

    /**
     * Get the latest day that can be displayed. Will return null if no maximum date is set.
     *
     * @return the latest day the can be displayed, null if no max date set
     */
    fun getMaxDate(): Calendar? {
        return mMaxDate
    }

    /**
     * Set the latest day that can be displayed. This will determine the right horizontal scroll
     * limit. The default value is null (allow unlimited scrolling in to the future).
     *
     * @param maxDate The new maximum date (pass null for no maximum)
     */
    fun setMaxDate(maxDate: Calendar?) {
        if (maxDate != null) {
            maxDate.set(Calendar.HOUR_OF_DAY, 0)
            maxDate.set(Calendar.MINUTE, 0)
            maxDate.set(Calendar.SECOND, 0)
            maxDate.set(Calendar.MILLISECOND, 0)
            if (mMinDate != null && maxDate.before(mMinDate)) {
                throw IllegalArgumentException("maxDate has to be after minDate")
            }
        }
        mMaxDate = maxDate
        resetHomeDate()
        mCurrentOrigin.x = 0f
        invalidate()
    }

    /**
     * Whether weekends should have a background color different from the normal day background
     * color. The weekend background colors are defined by the attributes
     * `futureWeekendBackgroundColor` and `pastWeekendBackgroundColor`.
     *
     * @return True if weekends should have different background colors.
     */
    fun isShowDistinctWeekendColor(): Boolean {
        return mShowDistinctWeekendColor
    }

    /**
     * Set whether weekends should have a background color different from the normal day background
     * color. The weekend background colors are defined by the attributes
     * `futureWeekendBackgroundColor` and `pastWeekendBackgroundColor`.
     *
     * @param showDistinctWeekendColor True if weekends should have different background colors.
     */
    fun setShowDistinctWeekendColor(showDistinctWeekendColor: Boolean) {
        mShowDistinctWeekendColor = showDistinctWeekendColor
        invalidate()
    }

    /**
     * auto calculate limit time on events in visible days.
     */
    fun setAutoLimitTime(isAuto: Boolean) {
        mAutoLimitTime = isAuto
        invalidate()
    }

    private fun recalculateHourHeight() {
        val height: Int = ((height - (mHeaderHeight + (mHeaderRowPadding * 2) + (mTimeTextHeight / 2) + mHeaderMarginBottom)) / (mMaxTime - mMinTime)).toInt()
        if (height > mHourHeight) {
            if (height > mMaxHourHeight) mMaxHourHeight = height
            mNewHourHeight = height
        }
    }

    /**
     * Set visible time span.
     *
     * @param startHour limit time display on top (between 0~24)
     * @param endHour   limit time display at bottom (between 0~24 and larger than startHour)
     */
    fun setLimitTime(startHour: Int, endHour: Int) {
        if (endHour <= startHour) {
            throw IllegalArgumentException("endHour must larger startHour.")
        } else if (startHour < 0) {
            throw IllegalArgumentException("startHour must be at least 0.")
        } else if (endHour > 24) {
            throw IllegalArgumentException("endHour can't be higher than 24.")
        }
        mMinTime = startHour
        mMaxTime = endHour
        recalculateHourHeight()
        invalidate()
    }

    /**
     * Set minimal shown time
     *
     * @param startHour limit time display on top (between 0~24) and smaller than endHour
     */
    fun setMinTime(startHour: Int) {
        if (mMaxTime <= startHour) {
            throw IllegalArgumentException("startHour must smaller than endHour")
        } else if (startHour < 0) {
            throw IllegalArgumentException("startHour must be at least 0.")
        }
        mMinTime = startHour
        recalculateHourHeight()
    }

    /**
     * Set highest shown time
     *
     * @param endHour limit time display at bottom (between 0~24 and larger than startHour)
     */
    fun setMaxTime(endHour: Int) {
        if (endHour <= mMinTime) {
            throw IllegalArgumentException("endHour must larger startHour.")
        } else if (endHour > 24) {
            throw IllegalArgumentException("endHour can't be higher than 24.")
        }
        mMaxTime = endHour
        recalculateHourHeight()
        invalidate()
    }

    /**
     * Whether past and future days should have two different background colors. The past and
     * future day colors are defined by the attributes `futureBackgroundColor` and
     * `pastBackgroundColor`.
     *
     * @return True if past and future days should have two different background colors.
     */
    fun isShowDistinctPastFutureColor(): Boolean {
        return mShowDistinctPastFutureColor
    }

    /**
     * Set whether weekends should have a background color different from the normal day background
     * color. The past and future day colors are defined by the attributes `futureBackgroundColor`
     * and `pastBackgroundColor`.
     *
     * @param showDistinctPastFutureColor True if past and future should have two different
     * background colors.
     */
    fun setShowDistinctPastFutureColor(showDistinctPastFutureColor: Boolean) {
        mShowDistinctPastFutureColor = showDistinctPastFutureColor
        invalidate()
    }

    /**
     * Get whether "now" line should be displayed. "Now" line is defined by the attributes
     * `nowLineColor` and `nowLineThickness`.
     *
     * @return True if "now" line should be displayed.
     */
    fun isShowNowLine(): Boolean {
        return mShowNowLine
    }

    /**
     * Set whether "now" line should be displayed. "Now" line is defined by the attributes
     * `nowLineColor` and `nowLineThickness`.
     *
     * @param showNowLine True if "now" line should be displayed.
     */
    fun setShowNowLine(showNowLine: Boolean) {
        mShowNowLine = showNowLine
        invalidate()
    }

    /**
     * Get the "now" line color.
     *
     * @return The color of the "now" line.
     */
    fun getNowLineColor(): Int {
        return mNowLineColor
    }

    /**
     * Set the "now" line color.
     *
     * @param nowLineColor The color of the "now" line.
     */
    fun setNowLineColor(nowLineColor: Int) {
        mNowLineColor = nowLineColor
        invalidate()
    }

    /**
     * Get the "now" line thickness.
     *
     * @return The thickness of the "now" line.
     */
    fun getNowLineThickness(): Int {
        return mNowLineThickness
    }

    /**
     * Set the "now" line thickness.
     *
     * @param nowLineThickness The thickness of the "now" line.
     */
    fun setNowLineThickness(nowLineThickness: Int) {
        mNowLineThickness = nowLineThickness
        invalidate()
    }

    /**
     * Get whether the week view should fling horizontally.
     *
     * @return True if the week view has horizontal fling enabled.
     */
    fun isHorizontalFlingEnabled(): Boolean {
        return mHorizontalFlingEnabled
    }

    /**
     * Set whether the week view should fling horizontally.
     *
     * @param enabled whether the week view should fling horizontally
     */
    fun setHorizontalFlingEnabled(enabled: Boolean) {
        mHorizontalFlingEnabled = enabled
    }

    /**
     * Get whether the week view should fling vertically.
     *
     * @return True if the week view has vertical fling enabled.
     */
    fun isVerticalFlingEnabled(): Boolean {
        return mVerticalFlingEnabled
    }

    /**
     * Set whether the week view should fling vertically.
     *
     * @param enabled whether the week view should fling vertically
     */
    fun setVerticalFlingEnabled(enabled: Boolean) {
        mVerticalFlingEnabled = enabled
    }

    /**
     * Get the height of AllDay-events.
     *
     * @return Height of AllDay-events.
     */
    fun getAllDayEventHeight(): Int {
        return mAllDayEventHeight
    }

    /**
     * Set the height of AllDay-events.
     *
     * @param height the new height of AllDay-events
     */
    fun setAllDayEventHeight(height: Int) {
        mAllDayEventHeight = height
    }

    /**
     * Enable zoom focus point
     * If you set this to false the `zoomFocusPoint` won't take effect any more while zooming.
     * The zoom will always be focused at the center of your gesture.
     *
     * @param zoomFocusPointEnabled whether the zoomFocusPoint is enabled
     */
    fun setZoomFocusPointEnabled(zoomFocusPointEnabled: Boolean) {
        mZoomFocusPointEnabled = zoomFocusPointEnabled
    }

    /*
     * Is focus point enabled
     * @return fixed focus point enabled?
     */
    fun isZoomFocusPointEnabled(): Boolean {
        return mZoomFocusPointEnabled
    }

    /*
     * Get focus point
     * 0 = top of view, 1 = bottom of view
     * The focused point (multiplier of the view height) where the week view is zoomed around.
     * This point will not move while zooming.
     * @return focus point
     */
    fun getZoomFocusPoint(): Float {
        return mZoomFocusPoint
    }

    /**
     * Set focus point
     * 0 = top of view, 1 = bottom of view
     * The focused point (multiplier of the view height) where the week view is zoomed around.
     * This point will not move while zooming.
     *
     * @param zoomFocusPoint the new zoomFocusPoint
     */
    fun setZoomFocusPoint(zoomFocusPoint: Float) {
        if (0 > zoomFocusPoint || zoomFocusPoint > 1) throw IllegalStateException("The zoom focus point percentage has to be between 0 and 1")
        mZoomFocusPoint = zoomFocusPoint
    }

    /**
     * Get scroll duration
     *
     * @return scroll duration
     */
    fun getScrollDuration(): Int {
        return mScrollDuration
    }

    /**
     * Set the scroll duration
     *
     * @param scrollDuration the new scrollDuraction
     */
    fun setScrollDuration(scrollDuration: Int) {
        mScrollDuration = scrollDuration
    }

    fun getMaxHourHeight(): Int {
        return mMaxHourHeight
    }

    fun setMaxHourHeight(maxHourHeight: Int) {
        mMaxHourHeight = maxHourHeight
    }

    fun getMinHourHeight(): Int {
        return mMinHourHeight
    }

    fun setMinHourHeight(minHourHeight: Int) {
        mMinHourHeight = minHourHeight
    }

    fun getPastBackgroundColor(): Int {
        return mPastBackgroundColor
    }

    fun setPastBackgroundColor(pastBackgroundColor: Int) {
        mPastBackgroundColor = pastBackgroundColor
        mPastBackgroundPaint!!.color = mPastBackgroundColor
    }

    fun getFutureBackgroundColor(): Int {
        return mFutureBackgroundColor
    }

    fun setFutureBackgroundColor(futureBackgroundColor: Int) {
        mFutureBackgroundColor = futureBackgroundColor
        mFutureBackgroundPaint!!.color = mFutureBackgroundColor
    }

    fun getPastWeekendBackgroundColor(): Int {
        return mPastWeekendBackgroundColor
    }

    fun setPastWeekendBackgroundColor(pastWeekendBackgroundColor: Int) {
        mPastWeekendBackgroundColor = pastWeekendBackgroundColor
        mPastWeekendBackgroundPaint!!.color = mPastWeekendBackgroundColor
    }

    fun getFutureWeekendBackgroundColor(): Int {
        return mFutureWeekendBackgroundColor
    }

    fun setFutureWeekendBackgroundColor(futureWeekendBackgroundColor: Int) {
        mFutureWeekendBackgroundColor = futureWeekendBackgroundColor
        mFutureWeekendBackgroundPaint!!.color = mFutureWeekendBackgroundColor
    }

    fun getNewEventIconDrawable(): Drawable? {
        return mNewEventIconDrawable
    }

    fun setNewEventIconDrawable(newEventIconDrawable: Drawable?) {
        mNewEventIconDrawable = newEventIconDrawable
    }

    fun enableDropListener() {
        mEnableDropListener = true
        //set drag and drop listener, required Honeycomb+ Api level
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setOnDragListener(DragListener())
        }
    }

    fun disableDropListener() {
        mEnableDropListener = false
        //set drag and drop listener, required Honeycomb+ Api level
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setOnDragListener(null)
        }
    }

    fun isDropListenerEnabled(): Boolean {
        return mEnableDropListener
    }

    fun setMinOverlappingMinutes(minutes: Int) {
        mMinOverlappingMinutes = minutes
    }

    fun getMinOverlappingMinutes(): Int {
        return mMinOverlappingMinutes
    }

    /////////////////////////////////////////////////////////////////
    //
    //      Functions related to scrolling.
    //
    /////////////////////////////////////////////////////////////////
    override fun onTouchEvent(event: MotionEvent): Boolean {
        mScaleDetector!!.onTouchEvent(event)
        val `val`: Boolean = mGestureDetector!!.onTouchEvent(event)

        // Check after call of mGestureDetector, so mCurrentFlingDirection and mCurrentScrollDirection are set.
        if ((event.action == MotionEvent.ACTION_UP) && !mIsZooming && (mCurrentFlingDirection == Direction.NONE)) {
            if (mCurrentScrollDirection == Direction.RIGHT || mCurrentScrollDirection == Direction.LEFT) {
                goToNearestOrigin()
            }
            mCurrentScrollDirection = Direction.NONE
        }
        return `val`
    }

    /**
     * A lighter function to stop the current scroll animation
     */
    private fun stopScrolling() {
        //force scroller animation stop
        mScroller!!.forceFinished(true)
        // Reset scrolling and fling direction.
        mCurrentFlingDirection = Direction.NONE
        mCurrentScrollDirection = mCurrentFlingDirection
    }

    private fun goToNearestOrigin() {
        var leftDays: Double = (mCurrentOrigin.x / (mWidthPerDay + mColumnGap)).toDouble()
        if (mCurrentFlingDirection != Direction.NONE) {
            // snap to nearest day
            leftDays = Math.round(leftDays).toDouble()
        } else if (mCurrentScrollDirection == Direction.LEFT) {
            // snap to last day
            leftDays = Math.floor(leftDays)
        } else if (mCurrentScrollDirection == Direction.RIGHT) {
            // snap to next day
            leftDays = Math.ceil(leftDays)
        } else {
            // snap to nearest day
            leftDays = Math.round(leftDays).toDouble()
        }
        val nearestOrigin: Int = (mCurrentOrigin.x - leftDays * (mWidthPerDay + mColumnGap)).toInt()
        val mayScrollHorizontal: Boolean = (mCurrentOrigin.x - nearestOrigin < xMaxLimit
                && mCurrentOrigin.x - nearestOrigin > xMinLimit)
        if (mayScrollHorizontal) {
            mScroller!!.startScroll(mCurrentOrigin.x.toInt(), mCurrentOrigin.y.toInt(), -nearestOrigin, 0)
            ViewCompat.postInvalidateOnAnimation(this@NewGridLayout)
        }
        if (nearestOrigin != 0 && mayScrollHorizontal) {
            // Stop current animation.
            mScroller!!.forceFinished(true)
            // Snap to date.
            mScroller!!.startScroll(mCurrentOrigin.x.toInt(), mCurrentOrigin.y.toInt(), -nearestOrigin, 0, (Math.abs(nearestOrigin) / mWidthPerDay * mScrollDuration).toInt())
            ViewCompat.postInvalidateOnAnimation(this@NewGridLayout)
        }
        // Reset scrolling and fling direction.
        mCurrentFlingDirection = Direction.NONE
        mCurrentScrollDirection = mCurrentFlingDirection
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mScroller!!.isFinished) {
            if (mCurrentFlingDirection != Direction.NONE) {
                // Snap to day after fling is finished.
                goToNearestOrigin()
            }
        } else {
            if (mCurrentFlingDirection != Direction.NONE && forceFinishScroll()) {
                goToNearestOrigin()
            } else if (mScroller!!.computeScrollOffset()) {
                mCurrentOrigin.y = mScroller!!.currY.toFloat()
                mCurrentOrigin.x = mScroller!!.currX.toFloat()
                ViewCompat.postInvalidateOnAnimation(this)
            }
        }
    }

    /**
     * Check if scrolling should be stopped.
     *
     * @return true if scrolling should be stopped before reaching the end of animation.
     */
    private fun forceFinishScroll(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // current velocity only available since api 14
            return mScroller!!.currVelocity <= mMinimumFlingVelocity
        } else {
            return false
        }
    }
    /////////////////////////////////////////////////////////////////
    //
    //      Public methods.
    //
    /////////////////////////////////////////////////////////////////
    /**
     * Show today on the week view.
     */
    fun goToToday() {
        val today: Calendar = Calendar.getInstance()
        goToDate(today)
    }

    /**
     * Show a specific day on the week view.
     *
     * @param date The date to show.
     */
    fun goToDate(date: Calendar) {
        mScroller!!.forceFinished(true)
        mCurrentFlingDirection = Direction.NONE
        mCurrentScrollDirection = mCurrentFlingDirection
        date.set(Calendar.HOUR_OF_DAY, 0)
        date.set(Calendar.MINUTE, 0)
        date.set(Calendar.SECOND, 0)
        date.set(Calendar.MILLISECOND, 0)
        if (mAreDimensionsInvalid) {
            mScrollToDay = date
            return
        }
        mRefreshEvents = true
        mCurrentOrigin.x = -daysBetween(mHomeDate, date) * (mWidthPerDay + mColumnGap)
        invalidate()
    }

    /**
     * Refreshes the view and loads the events again.
     */
    fun notifyDatasetChanged() {
        mRefreshEvents = true
        invalidate()
    }

    /**
     * Vertically scroll to a specific hour in the week view.
     *
     * @param hour The hour to scroll to in 24-hour format. Supported values are 0-24.
     */
    fun goToHour(hour: Double) {
        if (mAreDimensionsInvalid) {
            mScrollToHour = hour
            return
        }
        var verticalOffset: Int = 0
        if (hour > mMaxTime) verticalOffset = mHourHeight * (mMaxTime - mMinTime) else if (hour > mMinTime) verticalOffset = (mHourHeight * hour).toInt()
        if (verticalOffset > (mHourHeight * (mMaxTime - mMinTime) - height) + mHeaderHeight + (mHeaderRowPadding * 2) + mHeaderMarginBottom) verticalOffset = ((mHourHeight * (mMaxTime - mMinTime) - height) + mHeaderHeight + (mHeaderRowPadding * 2) + mHeaderMarginBottom).toInt()
        mCurrentOrigin.y = -verticalOffset.toFloat()
        invalidate()
    }

    /**
     * Get the first hour that is visible on the screen.
     *
     * @return The first hour that is visible.
     */
    fun getFirstVisibleHour(): Double {
        return (-mCurrentOrigin.y / mHourHeight).toDouble()
    }

    /**
     * Determine whether a given calendar day falls within the scroll limits set for this view.
     *
     * @param day the day to check
     * @return True if there are no limit or the date is within the limits.
     * @see .setMinDate
     * @see .setMaxDate
     */
    fun dateIsValid(day: Calendar): Boolean {
        if (mMinDate != null && day.before(mMinDate)) {
            return false
        }
        if (mMaxDate != null && day.after(mMaxDate)) {
            return false
        }
        return true
    }

    /////////////////////////////////////////////////////////////////
    //
    //      Interfaces.
    //
    /////////////////////////////////////////////////////////////////
    interface DropListener {
        /**
         * Triggered when view dropped
         *
         * @param view: dropped view.
         * @param date: object set with the date and time of the dropped coordinates on the view.
         */
        fun onDrop(view: View?, date: Calendar?)
    }

    interface EventClickListener {
        /**
         * Triggered when clicked on one existing event
         *
         * @param event:     event clicked.
         * @param eventRect: view containing the clicked event.
         */
        fun onEventClick(event: WeekViewEvent?, eventRect: RectF?)
    }

    interface EventLongPressListener {
        /**
         * Similar to [com.alamkanak.weekview.NewGridLayout.EventClickListener] but with a long press.
         *
         * @param event:     event clicked.
         * @param eventRect: view containing the clicked event.
         */
        fun onEventLongPress(event: WeekViewEvent?, eventRect: RectF?)
    }

    interface EmptyViewClickListener {
        /**
         * Triggered when the users clicks on a empty space of the calendar.
         *
         * @param date: [Calendar] object set with the date and time of the clicked position on the view.
         */
        fun onEmptyViewClicked(date: Calendar?)
    }

    interface EmptyViewLongPressListener {
        /**
         * Similar to [com.alamkanak.weekview.NewGridLayout.EmptyViewClickListener] but with long press.
         *
         * @param time: [Calendar] object set with the date and time of the long pressed position on the view.
         */
        fun onEmptyViewLongPress(time: Calendar?)
    }

    interface ScrollListener {
        /**
         * Called when the first visible day has changed.
         *
         *
         * (this will also be called during the first draw of the weekview)
         *
         * @param newFirstVisibleDay The new first visible day
         * @param oldFirstVisibleDay The old first visible day (is null on the first call).
         */
        fun onFirstVisibleDayChanged(newFirstVisibleDay: Calendar?, oldFirstVisibleDay: Calendar?)
    }

    interface AddEventClickListener {
        /**
         * Triggered when the users clicks to create a new event.
         *
         * @param startTime The startTime of a new event
         * @param endTime   The endTime of a new event
         */
        fun onAddEventClicked(startTime: Calendar?, endTime: Calendar?)
    }

    interface ZoomEndListener {
        /**
         * Triggered when the user finishes a zoom action.
         * @param hourHeight The final height of hours when the user finishes zoom.
         */
        fun onZoomEnd(hourHeight: Int)
    }

    /**
     * A simple GestureListener that holds the focused hour while scaling.
     */
    private inner class WeekViewGestureListener() : OnScaleGestureListener {
        var mFocusedPointY: Float = 0f
        override fun onScaleEnd(detector: ScaleGestureDetector) {
            mIsZooming = false
            if (mZoomEndListener != null) {
                mZoomEndListener!!.onZoomEnd(mHourHeight)
            }
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            mIsZooming = true
            goToNearestOrigin()

            // Calculate focused point for scale action
            if (mZoomFocusPointEnabled) {
                // Use fractional focus, percentage of height
                mFocusedPointY = (height - mHeaderHeight - (mHeaderRowPadding * 2) - mHeaderMarginBottom) * mZoomFocusPoint
            } else {
                // Grab focus
                mFocusedPointY = detector.focusY
            }
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scale: Float = detector.scaleFactor
            mNewHourHeight = Math.round(mHourHeight * scale)

            // Calculating difference
            var diffY: Float = mFocusedPointY - mCurrentOrigin.y
            // Scaling difference
            diffY = diffY * scale - diffY
            // Updating week view origin
            mCurrentOrigin.y -= diffY
            invalidate()
            return true
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private inner class DragListener() : OnDragListener {
        override fun onDrag(v: View, e: DragEvent): Boolean {
            when (e.action) {
                DragEvent.ACTION_DROP -> if (e.x > mHeaderColumnWidth && e.y > (mHeaderTextHeight + (mHeaderRowPadding * 2) + mHeaderMarginBottom)) {
                    val selectedTime: Calendar? = getTimeFromPoint(e.x, e.y)
                    if (selectedTime != null) {
                        mDropListener!!.onDrop(v, selectedTime)
                    }
                }
            }
            return true
        }
    }

    companion object {
        @Deprecated("")
        val LENGTH_SHORT: Int = 1

        @Deprecated("")
        val LENGTH_LONG: Int = 2
    }

    init {

        // Hold references.

        // Get the attribute values (if any).
        val a: TypedArray = mContext.theme.obtainStyledAttributes(attrs, R.styleable.NewGridLayout, 0, 0)
        try {
            mFirstDayOfWeek = a.getInteger(R.styleable.NewGridLayout_firstDayOfWeek, mFirstDayOfWeek)
            mHourHeight = a.getDimensionPixelSize(R.styleable.NewGridLayout_hourHeight, mHourHeight)
            mMinHourHeight = a.getDimensionPixelSize(R.styleable.NewGridLayout_minHourHeight, mMinHourHeight)
            mEffectiveMinHourHeight = mMinHourHeight
            mMaxHourHeight = a.getDimensionPixelSize(R.styleable.NewGridLayout_maxHourHeight, mMaxHourHeight)
            mTextSize = a.getDimensionPixelSize(R.styleable.NewGridLayout_textSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize.toFloat(), mContext.resources.displayMetrics).toInt())
            mHeaderColumnPadding = a.getDimensionPixelSize(R.styleable.NewGridLayout_headerColumnPadding, mHeaderColumnPadding)
            mColumnGap = a.getDimensionPixelSize(R.styleable.NewGridLayout_columnGap, mColumnGap)
            mHeaderColumnTextColor = a.getColor(R.styleable.NewGridLayout_headerColumnTextColor, mHeaderColumnTextColor)
            mNumberOfVisibleDays = a.getInteger(R.styleable.NewGridLayout_noOfVisibleDays, mNumberOfVisibleDays)
            mShowFirstDayOfWeekFirst = a.getBoolean(R.styleable.NewGridLayout_showFirstDayOfWeekFirst, mShowFirstDayOfWeekFirst)
            mHeaderRowPadding = a.getDimensionPixelSize(R.styleable.NewGridLayout_headerRowPadding, mHeaderRowPadding)
            mHeaderRowBackgroundColor = a.getColor(R.styleable.NewGridLayout_headerRowBackgroundColor, mHeaderRowBackgroundColor)
            mDayBackgroundColor = a.getColor(R.styleable.NewGridLayout_dayBackgroundColor, mDayBackgroundColor)
            mFutureBackgroundColor = a.getColor(R.styleable.NewGridLayout_futureBackgroundColor, mFutureBackgroundColor)
            mPastBackgroundColor = a.getColor(R.styleable.NewGridLayout_pastBackgroundColor, mPastBackgroundColor)
            mFutureWeekendBackgroundColor = a.getColor(R.styleable.NewGridLayout_futureWeekendBackgroundColor, mFutureBackgroundColor) // If not set, use the same color as in the week
            mPastWeekendBackgroundColor = a.getColor(R.styleable.NewGridLayout_pastWeekendBackgroundColor, mPastBackgroundColor)
            mNowLineColor = a.getColor(R.styleable.NewGridLayout_nowLineColor, mNowLineColor)
            mNowLineThickness = a.getDimensionPixelSize(R.styleable.NewGridLayout_nowLineThickness, mNowLineThickness)
            mHourSeparatorColor = a.getColor(R.styleable.NewGridLayout_hourSeparatorColor, mHourSeparatorColor)
            mTodayBackgroundColor = a.getColor(R.styleable.NewGridLayout_todayBackgroundColor, mTodayBackgroundColor)
            mHourSeparatorHeight = a.getDimensionPixelSize(R.styleable.NewGridLayout_hourSeparatorHeight, mHourSeparatorHeight)
            mTodayHeaderTextColor = a.getColor(R.styleable.NewGridLayout_todayHeaderTextColor, mTodayHeaderTextColor)
            mEventTextSize = a.getDimensionPixelSize(R.styleable.NewGridLayout_eventTextSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mEventTextSize.toFloat(), mContext.resources.displayMetrics).toInt())
            mEventTextColor = a.getColor(R.styleable.NewGridLayout_eventTextColor, mEventTextColor)
            mNewEventColor = a.getColor(R.styleable.NewGridLayout_newEventColor, mNewEventColor)
            mNewEventIconDrawable = a.getDrawable(R.styleable.NewGridLayout_newEventIconResource)
            // For backward compatibility : Set "mNewEventIdentifier" if the attribute is "NewGridLayout_newEventId" of type int
            setNewEventId(a.getInt(R.styleable.NewGridLayout_newEventId, mNewEventIdentifier!!.toInt()))
            mNewEventIdentifier = if ((a.getString(R.styleable.NewGridLayout_newEventIdentifier) != null)) a.getString(R.styleable.NewGridLayout_newEventIdentifier) else mNewEventIdentifier
            mNewEventLengthInMinutes = a.getInt(R.styleable.NewGridLayout_newEventLengthInMinutes, mNewEventLengthInMinutes)
            mNewEventTimeResolutionInMinutes = a.getInt(R.styleable.NewGridLayout_newEventTimeResolutionInMinutes, mNewEventTimeResolutionInMinutes)
            mEventPadding = a.getDimensionPixelSize(R.styleable.NewGridLayout_eventPadding, mEventPadding)
            mHeaderColumnBackgroundColor = a.getColor(R.styleable.NewGridLayout_headerColumnBackground, mHeaderColumnBackgroundColor)
            mDayNameLength = a.getInteger(R.styleable.NewGridLayout_dayNameLength, mDayNameLength)
            mOverlappingEventGap = a.getDimensionPixelSize(R.styleable.NewGridLayout_overlappingEventGap, mOverlappingEventGap)
            mEventMarginVertical = a.getDimensionPixelSize(R.styleable.NewGridLayout_eventMarginVertical, mEventMarginVertical)
            mXScrollingSpeed = a.getFloat(R.styleable.NewGridLayout_xScrollingSpeed, mXScrollingSpeed)
            mEventCornerRadius = a.getDimensionPixelSize(R.styleable.NewGridLayout_eventCornerRadius, mEventCornerRadius)
            mShowDistinctPastFutureColor = a.getBoolean(R.styleable.NewGridLayout_showDistinctPastFutureColor, mShowDistinctPastFutureColor)
            mShowDistinctWeekendColor = a.getBoolean(R.styleable.NewGridLayout_showDistinctWeekendColor, mShowDistinctWeekendColor)
            mShowNowLine = a.getBoolean(R.styleable.NewGridLayout_showNowLine, mShowNowLine)
            mHorizontalFlingEnabled = a.getBoolean(R.styleable.NewGridLayout_horizontalFlingEnabled, mHorizontalFlingEnabled)
            mVerticalFlingEnabled = a.getBoolean(R.styleable.NewGridLayout_verticalFlingEnabled, mVerticalFlingEnabled)
            mAllDayEventHeight = a.getDimensionPixelSize(R.styleable.NewGridLayout_allDayEventHeight, mAllDayEventHeight)
            mZoomFocusPoint = a.getFraction(R.styleable.NewGridLayout_zoomFocusPoint, 1, 1, mZoomFocusPoint)
            mZoomFocusPointEnabled = a.getBoolean(R.styleable.NewGridLayout_zoomFocusPointEnabled, mZoomFocusPointEnabled)
            mScrollDuration = a.getInt(R.styleable.NewGridLayout_scrollDuration, mScrollDuration)
            mTimeColumnResolution = a.getInt(R.styleable.NewGridLayout_timeColumnResolution, mTimeColumnResolution)
            mAutoLimitTime = a.getBoolean(R.styleable.NewGridLayout_autoLimitTime, mAutoLimitTime)
            mMinTime = a.getInt(R.styleable.NewGridLayout_minTime, mMinTime)
            mMaxTime = a.getInt(R.styleable.NewGridLayout_maxTime, mMaxTime)
            if (a.getBoolean(R.styleable.NewGridLayout_dropListenerEnabled, false)) enableDropListener()
            mMinOverlappingMinutes = a.getInt(R.styleable.NewGridLayout_minOverlappingMinutes, 0)
        } finally {
            a.recycle()
        }
        init()
    }

    /*constructor( mContext: Context, attrs: AttributeSet): this(mContext){
//        this.attrs=attrs
    }*/

    constructor( mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, dateTimeInterpreter: DateTimeInterpreter) : this(mContext, attrs!!) {

    }
}