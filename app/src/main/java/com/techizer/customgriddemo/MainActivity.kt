package com.techizer.customgriddemo

import android.graphics.RectF
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.alamkanak.weekview.NewGridLayout
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), NewGridLayout.EventClickListener, NewGridLayout.EventLongPressListener, NewGridLayout.EmptyViewLongPressListener, NewGridLayout.EmptyViewClickListener, NewGridLayout.AddEventClickListener, NewGridLayout.DropListener {

    protected var mWeekView: NewGridLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get a reference for the week view in the layout.


        // Get a reference for the week view in the layout.
        mWeekView = findViewById(R.id.newgrid) as NewGridLayout

        // Show a toast message about the touched event.

        // Show a toast message about the touched event.
        mWeekView!!.setOnEventClickListener(this)

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
/*        mWeekView.setMonthChangeListener(this)

        // Set long press listener for events.

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this)

        // Set long press listener for empty view

        // Set long press listener for empty view
        mWeekView.setEmptyViewLongPressListener(this)

        // Set EmptyView Click Listener

        // Set EmptyView Click Listener
        mWeekView.setEmptyViewClickListener(this)

        // Set AddEvent Click Listener

        // Set AddEvent Click Listener
        mWeekView.setAddEventClickListener(this)
*/
        // Set Drag and Drop Listener

        // Set Drag and Drop Listener
        mWeekView!!.setDropListener(this)


        // Set minDate
        /*Calendar minDate = Calendar.getInstance();
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        minDate.add(Calendar.MONTH, 1);
        mWeekView.setMinDate(minDate);

        // Set maxDate
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.MONTH, 1);
        maxDate.set(Calendar.DAY_OF_MONTH, 10);
        mWeekView.setMaxDate(maxDate);

        Calendar calendar = (Calendar) maxDate.clone();
        calendar.add(Calendar.DATE, -2);
        mWeekView.goToDate(calendar);*/

        //mWeekView.setAutoLimitTime(true);
        //mWeekView.setLimitTime(4, 16);

        //mWeekView.setMinTime(10);
        //mWeekView.setMaxTime(20);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false)

    }

    override fun onDrop(view: View?, date: Calendar?) {
        TODO("Not yet implemented")
    }

    override fun onEventClick(event: WeekViewEvent?, eventRect: RectF?) {
        TODO("Not yet implemented")
    }

    override fun onEventLongPress(event: WeekViewEvent?, eventRect: RectF?) {
        TODO("Not yet implemented")
    }

    override fun onEmptyViewClicked(date: Calendar?) {
        TODO("Not yet implemented")
    }

    override fun onEmptyViewLongPress(time: Calendar?) {
        TODO("Not yet implemented")
    }

    override fun onAddEventClicked(startTime: Calendar?, endTime: Calendar?) {
        TODO("Not yet implemented")
    }

    private fun setupDateTimeInterpreter(shortDate: Boolean) {
        mWeekView!!.setDateTimeInterpreter(object : DateTimeInterpreter {
            override fun interpretDate(date: Calendar): String? {
                val weekdayNameFormat = SimpleDateFormat("EEE", Locale.getDefault())
                var weekday = weekdayNameFormat.format(date.time)
                val format = SimpleDateFormat(" M/d", Locale.getDefault())

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate) weekday = weekday[0].toString()
                return weekday.toUpperCase() + format.format(date.time)
            }

            override fun interpretTime(hour: Int, minutes: Int): String? {
                val strMinutes = String.format("%02d", minutes)
                return if (hour > 11) {
                    (hour - 12).toString() + ":" + strMinutes + " PM"
                } else {
                    if (hour == 0) {
                        "12:$strMinutes AM"
                    } else {
                        "$hour:$strMinutes AM"
                    }
                }
            }
        })
    }
}