package com.bri64.discord.chillbot.web

import com.bri64.discord.audio.send.LoopMode
import com.bri64.discord.chillbot.web.plugins.kwebStyle
import com.bri64.discord.chillbot.web.plugins.semanticUIPlugin
import io.ktor.application.call
import io.ktor.http.content.resolveResource
import io.ktor.response.respond
import io.ktor.routing.get
import io.kweb.Kweb
import io.kweb.WebBrowser
import io.kweb.dom.element.Element
import io.kweb.dom.element.creation.ElementCreator
import io.kweb.dom.element.creation.tags.*
import io.kweb.dom.element.events.on
import io.kweb.dom.element.events.onImmediate
import io.kweb.dom.element.new
import io.kweb.state.KVar
import io.kweb.state.persistent.render

/**
 * Created by Dan Schmitt
 */

class PlayingState {

    companion object {
        var playing = KVar(false)
        var nowPlaying = KVar("No song playing!")
        var playlist = KVar(arrayOf<String>())
        var loop = KVar(LoopMode.ALL)

        var playCallback: () -> Unit = {}
        var prevCallback: () -> Unit = {}
        var nextCallback: () -> Unit = {}
        var shuffleCallback: () -> Unit = {}
        var loopCallback: () -> Unit = {}
        var commandCallback: (String?) -> Unit = {}

        fun getPlayingText(): String {
            return if (playing.value) "Pause" else "Play"
        }

        fun getPlayingIcon(): String {
            return if (playing.value) "pause icon" else "play icon"
        }
    }
}

class DropDownState {
    companion object {
        var command = false
        var list = false

        fun showCommandMenu() {
            list = false
            command = !command
        }

        fun showListMenu() {
            command = false
            list = !list
        }

    }
}

fun init() {
    Kweb(
            port = 8080,
            debug = false,
            plugins = listOf(semanticUIPlugin, kwebStyle),
            appServerConfigurator = { routing ->
                routing.get("/style.css") {
                    call.respond(call.resolveResource("static/style.css")!!)
                }
            }) {
        doc.body.new {
            this.background()
            this.centerContent {
                this.mainController {
                    div().setAttribute("class", "settings").new {
                        this.consoleButton(this@Kweb)
                        this.playlistButton(this@Kweb)
                    }
                    this.currentlyPlaying("http://google.com/")
                    this.controlButtons()
                }
                this.consoleWindow()
                this.playlistWindow()
            }
        }
    }
}

private fun ElementCreator<Element>.playButton(callback: () -> Unit, content: ElementCreator<Element>.() -> Unit): ButtonElement {
    val btn = button()
    btn.setAttribute("class", "ui inverted secondary vertical animated button").setAttribute("tabindex", "-1")
    btn.new {
        div().new {
            render(PlayingState.playing) {
                div().setAttribute("class", "visible content").new {
                    i().setAttribute("class", PlayingState.getPlayingIcon())
                }
                div().setAttribute("class", "hidden content").text(PlayingState.getPlayingText())
            }
        }
        content(this)
    }
    btn.onImmediate.mouseleave {
        btn.blur()
    }
    btn.on.click {
        callback()
    }
    return btn
}

private fun ElementCreator<Element>.clickButton(text: String, icon: String, callback: () -> Unit, content: ElementCreator<Element>.() -> Unit): ButtonElement {
    val btn = button()
    btn.setAttribute("class", "ui inverted secondary vertical animated button").setAttribute("tabindex", "-1")
    btn.new {
        div().new {
            div().setAttribute("class", "visible content").new {
                i().setAttribute("class", icon)
            }
            div().setAttribute("class", "hidden content").text(text)
        }
        content(this)
    }
    btn.onImmediate.mouseleave {
        btn.blur()
    }
    btn.on.click {
        callback()
    }
    return btn
}

private fun ElementCreator<Element>.loopButton(callback: () -> Unit, content: ElementCreator<Element>.() -> Unit): ButtonElement {
    val btn = button()
    btn.setAttribute("class", "ui inverted vertical animated button")
    btn.setAttribute("tabindex", "-1")
    btn.new {
        render(PlayingState.loop) {
            btn.removeClasses("secondary", "primary", "purple")
            btn.addClasses("secondary", onlyIf = PlayingState.loop.value == LoopMode.NONE)
            btn.addClasses("primary", onlyIf = PlayingState.loop.value == LoopMode.ONE)
            btn.addClasses("purple", onlyIf = PlayingState.loop.value == LoopMode.ALL)
            div().new {
                div().setAttribute("class", "visible content").new {
                    i().setAttribute("class", "sync alternate icon")
                }
                div().setAttribute("class", "hidden content").text("Loop")
            }
            content(this)
        }
        //div().setAttribute("class", "floating ui tiny red circular label").text("2").setAttribute("style", "position: absolute;")
    }
    btn.onImmediate.mouseleave {
        btn.blur()
    }
    btn.on.click {
        callback()
    }
    return btn
}

private fun ElementCreator<Element>.background() {
    div().setAttribute("class", "bg-underlay").new()
    div().setAttribute("class", "bg").new()
}

private fun ElementCreator<Element>.centerContent(content: ElementCreator<Element>.() -> Unit) {
    div().setAttribute("class", "ui middle aligned center aligned three column grid fullheight").new {
        div().setAttribute("class", "container one column wide middle aligned").new {
            content(this)
        }
    }
}

private fun ElementCreator<Element>.mainController(content: ElementCreator<Element>.() -> Unit) {
    div().setAttribute("class", "ui raised segment main").new {
        content(this)
    }
}

private fun ElementCreator<Element>.consoleButton(browserContext: WebBrowser) {
    val btn = button()
            .setAttribute("class", "circular ui icon mini button")
            .setAttribute("data-tooltip", "Console")
    btn.onImmediate.click {
        // Hide playlist if it's there
        if (DropDownState.list) {
            browserContext.execute("$('.list-slider').transition('hide', true);")
        }
        DropDownState.showCommandMenu()
        browserContext.execute(
                """
                    ${'$'}('.command-slider').transition({
                        animation: 'slide down',
                        queue: 'false',
                    }).transition('repaint', true);
                """.trimIndent()
        )
    }
    btn.onImmediate.mouseleave {
        btn.blur()
    }
    btn.new {
        // Settings button icon
        i().setAttribute("class", "cog icon")
    }
}

private fun ElementCreator<Element>.playlistButton(browserContext: WebBrowser) {
    val btn = button()
            .setAttribute("class", "circular ui icon mini button")
            .setAttribute("data-tooltip", "Playlist")
    btn.onImmediate.click {
        if (DropDownState.command) {
            browserContext.execute("$('.command-slider').transition('hide', true);")
        }
        DropDownState.showListMenu()
        browserContext.execute(
                """
                    ${'$'}('.list-slider').transition({
                        animation: 'slide down',
                        queue: 'false',
                    }).transition('repaint', true);
                """.trimIndent()
        )
    }
    btn.onImmediate.mouseleave {
        btn.blur()
    }
    btn.new {
        // Playlist button icon
        i().setAttribute("class", "list icon")
    }
}

private fun ElementCreator<Element>.currentlyPlaying(link: String) {
    div().new {
        h3().setAttribute("class", "noselect").text("Currently Playing:")
        render(PlayingState.nowPlaying) {
            p().new {
                a().setAttribute("href", "#")
                        .setAttribute("onClick",
                                """
                var win = window.open("$link", '_blank');
                win.focus();
                 """.trimIndent()).text(PlayingState.nowPlaying.value)
            }
        }
    }
}

private fun ElementCreator<Element>.controlButtons() {
    div().setAttribute("class", "ui center aligned one column grid").new {
        div().setAttribute("class", "column ui").new {
            playButton(callback = {
                /* Execute Play/Pause */
                PlayingState.playCallback.invoke()
            }) {}
            clickButton("Prev", "left arrow icon", {
                /* Execute Prev Song */
                PlayingState.prevCallback.invoke()
            }) {}
            clickButton("Next", "right arrow icon", {
                /* Execute Next Song */
                PlayingState.nextCallback.invoke()
            }) {}
            clickButton("Shuffle", "random icon", {
                /* Execute Shuffle */
                PlayingState.shuffleCallback.invoke()
            }) {}
            loopButton(callback = {
                /* Execute Looping */
                PlayingState.loopCallback.invoke()
            }) {}
        }
    }
}

private fun ElementCreator<Element>.consoleWindow() {
    div()
            .setAttribute("class", "ui inverted raised segment middle aligned center aligned command-slider dropdown transition hidden")
            .new {
                h3().text("Execute Command:")
                div().setAttribute("class", "ui grid").new {
                    div().setAttribute("class", "sixteen wide column").new {
                        div().setAttribute("class", "ui fluid inverted left icon input").new {
                            val inputText = input(InputType.text, placeholder = "Enter Command")
                            inputText.on.keypress {
                                if (it.key == "Enter") {
                                    inputText.getValue().whenComplete { text, _ ->
                                        run {
                                            PlayingState.commandCallback.invoke(text)
                                            inputText.setValue("")
                                        }
                                    }

                                }
                            }
                            i().setAttribute("class", "terminal icon")
                        }
                    }
                }
            }
}

private fun ElementCreator<Element>.playlistWindow() {
    div()
            .setAttribute("class", "ui inverted raised segment middle aligned center aligned list-slider dropdown transition hidden")
            .new {
                h3().setAttribute("class", "noselect").text("Up Next:")
                div().setAttribute("class", "ui inverted relaxed divided list").new {
                    render(PlayingState.playlist) {
                        for (s in PlayingState.playlist.value) {
                            a().setAttribute("href", "#").setAttribute("class", "item").text(s)
                        }
                    }
                }
            }
}