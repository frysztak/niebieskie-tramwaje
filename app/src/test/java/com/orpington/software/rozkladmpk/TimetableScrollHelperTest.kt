package com.orpington.software.rozkladmpk

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.orpington.software.rozkladmpk.routeDetails.HourCoordinates
import com.orpington.software.rozkladmpk.routeDetails.TimetableScrollHelper
import com.orpington.software.rozkladmpk.routeDetails.TimetableViewHelper
import com.orpington.software.rozkladmpk.routeDetails.TimetableViewHelper.*
import junit.framework.Assert
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*

@RunWith(Enclosed::class)
class TimetableScrollHelperTest {

    @RunWith(Parameterized::class)
    class GetCurrentDayTypeTest(
        private val currentDay: Int,
        private val dayType: DayType
    ) {

        companion object {
            @JvmStatic
            @Parameterized.Parameters
            fun data(): Collection<Array<Any>> {
                return listOf(
                    arrayOf(Calendar.MONDAY, DayType.Weekday),
                    arrayOf(Calendar.TUESDAY, DayType.Weekday),
                    arrayOf(Calendar.WEDNESDAY, DayType.Weekday),
                    arrayOf(Calendar.THURSDAY, DayType.Weekday),
                    arrayOf(Calendar.FRIDAY, DayType.Weekday),
                    arrayOf(Calendar.SATURDAY, DayType.Saturday),
                    arrayOf(Calendar.SUNDAY, TimetableViewHelper.DayType.Sunday)
                )
            }
        }

        @Test
        fun getCurrentDayType_Test() {
            val calendar = mock<Calendar> {
                on { get(Calendar.DAY_OF_WEEK) } doReturn currentDay
            }

            val helper = TimetableScrollHelper(calendar)
            Assert.assertEquals(dayType, helper.getCurrentDayType())
        }

    }

    @RunWith(Parameterized::class)
    class CalculateRowToScrollIntoTest(
        private val currentDay: Int,
        private val currentHour: Int,
        private val currentMinute: Int,
        private val items: List<TimetableViewHelper.ViewItem>,
        private val expectedCoordinates: HourCoordinates?
    ) {

        companion object {
            @JvmStatic
            @Parameterized.Parameters
            fun data(): Collection<Array<out Any?>> {
                return listOf(
                    // 0: Typical situation
                    arrayOf(Calendar.MONDAY, 12, 6, listOf(
                        HeaderItem(DayType.Weekday, ""),
                        RowItem(DayType.Weekday, arrayListOf("10", "07")),
                        RowItem(DayType.Weekday, arrayListOf("11", "07")),
                        RowItem(DayType.Weekday, arrayListOf("12", "07"))
                    ), HourCoordinates(3, "WE:12")),

                    // 1: Typical situation
                    arrayOf(Calendar.MONDAY, 12, 50, listOf(
                        HeaderItem(DayType.Weekday, ""),
                        RowItem(DayType.Weekday, arrayListOf("10", "07")),
                        RowItem(DayType.Weekday, arrayListOf("11", "07")),
                        RowItem(DayType.Weekday, arrayListOf("12", "07", "12", "24", "36", "48", "54"))
                    ), HourCoordinates(3, "WE:12")),

                    // 2: Typical situation
                    arrayOf(Calendar.MONDAY, 11, 50, listOf(
                        HeaderItem(DayType.Weekday, ""),
                        RowItem(DayType.Weekday, arrayListOf("10", "07")),
                        RowItem(DayType.Weekday, arrayListOf("11", "07")),
                        RowItem(DayType.Weekday, arrayListOf("12", "07", "12", "24", "36", "48", "54"))
                    ), HourCoordinates(3, "WE:12")),

                    // 3: Current hour is past last available one
                    arrayOf(Calendar.MONDAY, 13, 6, listOf(
                        HeaderItem(DayType.Weekday, ""),
                        RowItem(DayType.Weekday, arrayListOf("10", "07")),
                        RowItem(DayType.Weekday, arrayListOf("11", "07")),
                        RowItem(DayType.Weekday, arrayListOf("12", "07"))
                    ), null),

                    // 4: It's sunday but the route only runs on weekdays
                    arrayOf(Calendar.SUNDAY, 13, 6, listOf(
                        HeaderItem(DayType.Weekday, ""),
                        RowItem(DayType.Weekday, arrayListOf("10", "07")),
                        RowItem(DayType.Weekday, arrayListOf("11", "07")),
                        RowItem(DayType.Weekday, arrayListOf("12", "07"))
                    ), null)
                )
            }
        }

        @Test
        fun calculateRowToScrollInto_Test() {
            val calendar = mock<Calendar> {
                on { get(Calendar.DAY_OF_WEEK) } doReturn currentDay
                on { get(Calendar.HOUR_OF_DAY) } doReturn currentHour
                on { get(Calendar.MINUTE) } doReturn currentMinute
            }

            val helper = TimetableScrollHelper(calendar)
            val coords = helper.calculateRowToScrollInto(items)
            Assert.assertEquals(expectedCoordinates, coords)
        }

    }

}
