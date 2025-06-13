package com.amoos.imperfectmagic.utils;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class IM_Component {
    public static final ChatFormatting[] RAINBOW = new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.GOLD, ChatFormatting.YELLOW, ChatFormatting.GREEN, ChatFormatting.AQUA, ChatFormatting.BLUE, ChatFormatting.LIGHT_PURPLE};

    public static String format(String input, ChatFormatting[] style, double delay, int step) {
        StringBuilder Builder = new StringBuilder(input.length() * 3);
        int Offset = (int) Math.floor(Util.getMillis() / delay) % style.length;
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            int col = (i * step + style.length - Offset) % style.length;
            Builder.append(style[col]);
            Builder.append(c);
        }
        return Builder.toString();
    }

    /*
        public String getName() {
            return ColorPutter.rainbow("name");
        }
     */
    public static String arrayToString(Component... input) {
        StringBuilder str = new StringBuilder();
        for (Component cp : input) {
            str.append(cp.getString());
        }
        return str.toString();
    }

    public static Component arrayToComponent(Component... input) {
        return Component.literal(arrayToString(input));
    }

    public static String rainbowString(String input) {
        return format(input, RAINBOW, 80.0D, 1);
    }

    public static String rainbowString(Component... input) {
        return format(arrayToString(input), RAINBOW, 80.0D, 1);
    }


    public static MutableComponent rainbow(String input) {
        return Component.literal(rainbowString(input));
    }

    public static MutableComponent rainbow(Component... input) {
        return Component.literal(rainbowString(input));
    }


    public static String colorString(Object color, String input) {
        return color + input;
    }

    public static String colorString(Object color, Component... input) {
        return color + arrayToString(input);
    }


    public static Component colorText(Object color, Component... input) {
        return Component.literal(colorString(color, input));
    }

    public static MutableComponent colorText(Object color, String input) {
        return Component.literal(colorString(color, input));
    }

    public static class color {
        public final int color;
        public color(Vec3 color){//255
            this((int) color.x,(int) color.y, (int) color.z);
        }

        public color(int r, int g, int b){//255
            this.color = (r << 16) + (g << 8) + (b);
        }

        @Override
        public String toString() {
            return "\u00a7" + color;
        }
    }

}

/*package com.amoos.imperfectmagic.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Arrays;

public class ColorPutter {
    public static final ChatFormatting[] RAINBOW = new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.GOLD, ChatFormatting.YELLOW, ChatFormatting.GREEN, ChatFormatting.AQUA, ChatFormatting.BLUE, ChatFormatting.LIGHT_PURPLE};

    public static String format(String input, ChatFormatting[] style, double InTick, int step) {
        StringBuilder Builder = new StringBuilder(input.length() * 3);
        int Offset = (int) Math.floor(Util.getMillis() / InTick) % style.length;
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            int col = (i * step + style.length - Offset) % style.length;
            Builder.append(style[col]);
            Builder.append(c);
        }
        return Builder.toString();
    }
    public static String arrayToString(Component... input) {
        StringBuilder str = new StringBuilder();
        for(Component cp : input){
            str.append(cp.getString());
        }
        return str.toString();
    }
    public static String rainbowString(String input) {
        return format(input, RAINBOW, 80.0D, 1);
    }

    public static String rainbowString(Component... input) {return format(arrayToString(input), RAINBOW, 80.0D, 1);}

    public static MutableComponent rainbow(String input){
        return Component.literal(rainbowString(input));
    }

    public static MutableComponent rainbow(Component... input){
        return Component.literal(rainbowString(input));
    }

    public static String colorString(ChatFormatting color, String input){
        return color + input;
    }

    public static String colorString(ChatFormatting color, Component... input){return color + arrayToString(input);}

    public static Component colorText(ChatFormatting color, Component... input){return Component.literal(colorString(color,input));}

    public static MutableComponent colorText(ChatFormatting color, String input){return Component.literal(colorString(color, input));}

}*/