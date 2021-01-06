package rip.deadcode.xkcdtool.core.io

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth8.assertThat
import org.junit.jupiter.api.Test

class HttpTest {

    @Test
    fun testRequestIndex() {

        mockNet(html)

        val result = requestIndex()

        assertThat(result).containsExactly(
            IndexEntry(1, "Barrel - Part 1"),
            IndexEntry(2, "Petit Trees (sketch)"),
            IndexEntry(3, "Island (sketch)")
        )
    }

    @Test
    fun testRequestJson() {

        mockNet(xkcdJsonStr)

        val result = requestJson("http://localhost:1234")

        assertThat(result).hasValue(
            XkcdJson(
                308,
                "",
                "Interesting Life",
                "Interesting Life",
                "[[On the left hand side of the panel is a cutaway of several floors of an office, in gray.  On the right side a blue sky with clouds, and green hills.  Hanging from a cable is a GIRL, clearly having rappelled down the side of the building]]\nGIRL: You know how some people consider \"May you have an interesting life\" to be a curse?\nGUY IN OFFICE: Yeah...\nGIRL: Fuck those people.  Wanna have an adventure?\n{{Alt-text: Quick, fashion a climbing harness out of a cat-6 cable and follow me down}}",
                "https://imgs.xkcd.com/comics/interesting_life.png",
                "Quick, fashion a climbing harness out of cat-6 cable and follow me down.",
                "",
                "2007",
                "8",
                "27"
            )
        )
    }
}
