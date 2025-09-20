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
import net.minecraft.client.option.KeyBinding

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
            if (toggleKey.wasPressed()) {
                isLocked = !isLocked

                val window = client.window.handle

                if (isLocked) {
                    val x = DoubleArray(1)
                    val y = DoubleArray(1)
                    glfwGetCursorPos(window, x, y)
                    lockedX = x[0]
                    lockedY = y[0]

                    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
                    log.info("Mouse locked")
                } else {
                    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
                    log.info("Mouse unlocked")
                }
            }

            if (isLocked) {
                val window = client.window.handle
                glfwSetCursorPos(window, lockedX, lockedY)
            }
        }
    }
}

