package net.Mirik9724.mouselock.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW
import org.slf4j.LoggerFactory

class MouseLockClient : ClientModInitializer {

    private lateinit var toggleKey: KeyBinding
    private var isLocked = false
    private var lockedYaw = 0f
    private var lockedPitch = 0f

    val log = LoggerFactory.getLogger("MouseLock");

    override fun onInitializeClient() {
        log.info("Mod ON")

        toggleKey = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.mouselock.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_L,
                "category.mouselock.main"
            )
        )

        ClientTickEvents.END_CLIENT_TICK.register { client ->
            while (toggleKey.wasPressed()) {
                isLocked = !isLocked
                if (isLocked && client.player != null) {
                    lockedYaw = client.player!!.yaw
                    lockedPitch = client.player!!.pitch
                    client.mouse.unlockCursor()
                    log.info("Mouse - lock")
                } else {
                    client.mouse.lockCursor()
                    log.info("Mouse - unlock")
                }
            }

            if (isLocked && client.player != null) {
                client.player!!.yaw = lockedYaw
                client.player!!.pitch = lockedPitch
            }
        }
    }
}
