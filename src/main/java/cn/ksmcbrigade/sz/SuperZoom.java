package cn.ksmcbrigade.sz;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Mod("sz")
@Mod.EventBusSubscriber
public class SuperZoom {

    public static File config = new File("config/zoom.json");

    public static boolean EnabledTick = false;

    public static double zoom = 100.0D;

    public static double nowZoom = zoom;

    public static KeyMapping keyMapping = new KeyMapping("sz.name", GLFW.GLFW_KEY_B,"sz.name");

    public SuperZoom() throws IOException {
        MinecraftForge.EVENT_BUS.register(this);

        if(!config.exists()){
            save();
        }
        zoom = JsonParser.parseString(Files.readString(config.toPath())).getAsJsonObject().get("zoom").getAsDouble();

        ClientRegistry.registerKeyBinding(keyMapping);
    }

    @SubscribeEvent
    public void tick(TickEvent.PlayerTickEvent event){
        EnabledTick = keyMapping.isDown();

        if(nowZoom==0.0D || !EnabledTick){
            nowZoom = zoom;
        }
    }

    @SubscribeEvent
    public void mouseEvent(InputEvent.MouseScrollEvent event){

        if(EnabledTick){
            if(nowZoom==0.0D){
                nowZoom = zoom;
            }

            if(event.getScrollDelta()>0){
                nowZoom*=1.1;
            }
            else if(event.getScrollDelta()<0){
                nowZoom*=0.9;
            }
        }
    }

    @SubscribeEvent
    public void change(RegisterClientCommandsEvent event){
        event.getDispatcher().register(Commands.literal("super-zoom").executes(context -> {
            Entity entity = context.getSource().getEntity();
            if(entity!=null){
                entity.sendMessage(Component.nullToEmpty(I18n.get("sz.command").replace("{v}",String.valueOf(zoom))),entity.getUUID());
            }
            return 0;
        }).then(Commands.argument("value", DoubleArgumentType.doubleArg()).executes(context -> {
            zoom = DoubleArgumentType.getDouble(context,"value");
            if(nowZoom==0.0D){
                nowZoom = zoom;
            }
            try {
                save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return 0;
        })));
    }

    public static void save() throws IOException{
        JsonObject object = new JsonObject();
        object.addProperty("zoom",zoom);
        Files.write(config.toPath(),object.toString().getBytes());
    }

    public static double getZoom(Double fov){

        if(nowZoom==0.0D){
            nowZoom = zoom;
        }

        return fov / nowZoom;
    }
}
