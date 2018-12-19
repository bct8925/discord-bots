package com.bri64.discord.chillbot.web.plugins

import io.kweb.plugins.KWebPlugin

class KWebStyle : KWebPlugin() {
    override fun decorate(startHead: StringBuilder, endHead: StringBuilder) {
        startHead.append(
                """

                <link rel="stylesheet" href="style.css">
            """.trimIndent()
        )
    }
}

val kwebStyle = KWebStyle()