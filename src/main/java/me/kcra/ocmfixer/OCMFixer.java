package me.kcra.ocmfixer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class OCMFixer extends JavaPlugin {
    private boolean patched = false;

    @Override
    public void onEnable() {
        if (!patched) {
            ByteBuddyAgent.install();
            try {
                Class<?> clazz = Class.forName("kernitus.plugin.OldCombatMechanics.utilities.packet.mitm.PacketInjector");
                new ByteBuddy()
                        .redefine(clazz)
                        .method(named("write"))
                        .intercept(Advice.to(WriteMethodAdvice.class))
                        .make()
                        .load(
                                clazz.getClassLoader(),
                                ClassReloadingStrategy.fromInstalledAgent()
                        );
                getLogger().log(Level.INFO, "Intercepted kernitus.plugin.OldCombatMechanics.utilities.packet.mitm.PacketInjector#write() successfully.");
                patched = true;
            } catch (Throwable e) {
                getLogger().log(Level.SEVERE, "Could not intercept kernitus.plugin.OldCombatMechanics.utilities.packet.mitm.PacketInjector#write()", e);
            }
        }
    }

    static class WriteMethodAdvice {
        @Advice.OnMethodEnter
        static void write(@Advice.Argument(0) ChannelHandlerContext channelHandlerContext, @Advice.Argument(1) Object packet, @Advice.Argument(2) ChannelPromise channelPromise) {
            if (packet.getClass().isAssignableFrom(ByteBuf.class)) {
                channelHandlerContext.write(packet, channelPromise);
            }
        }
    }
}
