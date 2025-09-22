package net.Mirik9724.mouselock.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.glfwSetCursorPos
import org.lwjgl.glfw.GLFW.glfwGetCursorPos
import org.lwjgl.glfw.GLFW.glfwSetInputMode
import org.lwjgl.glfw.GLFW.GLFW_CURSOR
import org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED
import org.slf4j.LoggerFactory
import net.minecraft.client.util.InputUtil
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.option.KeyBinding
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

class MouseLockClient : ClientModInitializer {

    private lateinit var toggleKey: KeyBinding
    private var isLocked = false
    private var lockedX = 0.0
    private var lockedY = 0.0

    private val log = LoggerFactory.getLogger("MouseLock")

    @Environment(EnvType.CLIENT)
    override fun onInitializeClient() {
        log.info("MouseLock Mod ON")

        toggleKey = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.mouselock.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "category.mouselock.main"
            )
        )

        ClientTickEvents.END_CLIENT_TICK.register { client ->
            val window = client.window.handle

            // переключение состояния
            if (toggleKey.wasPressed()) {
                isLocked = !isLocked
                if (isLocked && client.currentScreen == null) {
                    val x = DoubleArray(1)
                    val y = DoubleArray(1)
                    glfwGetCursorPos(window, x, y)
                    lockedX = x[0]
                    lockedY = y[0]
                    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
                    log.info("Mouse locked")
                } else {
                    log.info("Mouse unlocked")
                }
            }

            if (isLocked && client.currentScreen == null) {
                glfwSetCursorPos(window, lockedX, lockedY)
                glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
            } else if (!isLocked || client.currentScreen != null) {
            }
        }
        HudRenderCallback.EVENT.register(HudRenderCallback { context: DrawContext, tickDelta: Float ->
            if (isLocked) {
                val client = MinecraftClient.getInstance()
                context.drawText(
                    client.textRenderer,
                    Text.translatable("key.mouselock.locked"),
                    10,
                    10,
                    0xFFFFFF,
                    true
                )
            }
        })
        ClientPlayConnectionEvents.DISCONNECT.register { handler, client ->
            resetMouseLock()
        }
    }

    private fun resetMouseLock() {
        isLocked = false
        log.info("Mouse unlocked due to disconnect/world unload")
    }
}
