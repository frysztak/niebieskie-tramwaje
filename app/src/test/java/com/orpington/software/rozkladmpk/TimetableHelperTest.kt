package com.orpington.software.rozkladmpk

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.orpington.software.rozkladmpk.routeDetails.TimetableHelper
import junit.framework.Assert
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*

@RunWith(Enclosed::class)
class TimetableHelperTest {

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
                    arrayOf(Calendar.SUNDAY, DayType.Sunday)
                )
            }
        }

        @Test
        fun getCurrentDayType_Test() {
            val calendar = mock<Calendar> {
                on { get(Calendar.DAY_OF_WEEK) } doReturn currentDay
            }

            val helper = TimetableHelper(calendar)
            Assert.assertEquals(dayType, helper.getCurrentDayType())
        }

    }

    @RunWith(Parameterized::class)
    class CalculateRowToScrollIntoTest(
        private val currentDay: Int,
        private val currentHour: Int,
        private val currentMinute: Int,
        private val items: List<ViewItem>,
        private val expectedRowIndex: Int,
        private val expectedColumnIndex: Int
    ) {

        companion object {
            @JvmStatic
            @Parameterized.Parameters
            fun data(): Collection<Array<Any>> {
                return listOf(
                    // 0: Typical situation
                    arrayOf(Calendar.MONDAY, 12, 6, listOf(
                        HeaderItem(DayType.Weekday, ""),
                        RowItem(arrayListOf("10", "07")),
                        RowItem(arrayListOf("11", "07")),
                        RowItem(arrayListOf("12", "07"))
                    ), 3, 0),

                    // 1: Typical situation
                    arrayOf(Calendar.MONDAY, 12, 50, listOf(
                        HeaderItem(DayType.Weekday, ""),
                        RowItem(arrayListOf("10", "07")),
                        RowItem(arrayListOf("11", "07")),
                        RowItem(arrayListOf("12", "07", "12", "24", "36", "48", "54"))
                    ), 3, 5),

                    // 2: Typical situation
                    arrayOf(Calendar.MONDAY, 11, 50, listOf(
                        HeaderItem(DayType.Weekday, ""),
                        RowItem(arrayListOf("10", "07")),
                        RowItem(arrayListOf("11", "07")),
                        RowItem(arrayListOf("12", "07", "12", "24", "36", "48", "54"))
                    ), 3, 0),

                    // 3: Current hour is past last available one
                    arrayOf(Calendar.MONDAY, 13, 6, listOf(
                        HeaderItem(DayType.Weekday, ""),
                        RowItem(arrayListOf("10", "07")),
                        RowItem(arrayListOf("11", "07")),
                        RowItem(arrayListOf("12", "07"))
                    ), -1, -1),

                    // 4: It's sunday but the route only runs on weekdays
                    arrayOf(Calendar.SUNDAY, 13, 6, listOf(
                        HeaderItem(DayType.Weekday, ""),
                        RowItem(arrayListOf("10", "07")),
                        RowItem(arrayListOf("11", "07")),
                        RowItem(arrayListOf("12", "07"))
                    ), -1, -1)
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

            val helper = TimetableHelper(calendar)
            val timeIndices = helper.calculateRowAndColumnToScrollInto(items)
            Assert.assertEquals(expectedRowIndex, timeIndices.hourIdx)
            Assert.assertEquals(expectedColumnIndex, timeIndices.minuteIdx)
        }

    }

}
