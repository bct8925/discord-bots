package com.bri64.discord.chillbot.web.plugins

import io.kweb.plugins.KWebPlugin
import io.kweb.plugins.jqueryCore.jqueryCore

class SemanticUIPlugin : KWebPlugin(dependsOn = setOf(jqueryCore)) {
    override fun decorate(startHead: StringBuilder, endHead: StringBuilder) {
        startHead.append("""
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.4.1/semantic.min.css">
            <script src="https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.4.1/semantic.min.js"></script>
            <script src="https://semantic-ui.com/javascript/library/tablesort.js"></script>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
""".trimIndent())
    }

}

val semanticUIPlugin = SemanticUIPlugin()