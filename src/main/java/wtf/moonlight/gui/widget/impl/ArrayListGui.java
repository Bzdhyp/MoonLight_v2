package wtf.moonlight.gui.widget.impl;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.StringUtils;
import wtf.moonlight.Client;
import wtf.moonlight.events.render.Shader2DEvent;
import wtf.moonlight.gui.font.FontRenderer;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.gui.widget.Widget;
import wtf.moonlight.module.Categor;
import wtf.moonlight.module.impl.display.ArrayList;
import wtf.moonlight.module.Module;
import wtf.moonlight.module.impl.display.Interface;
import wtf.moonlight.util.animations.advanced.Animation;
import wtf.moonlight.util.animations.advanced.Direction;
import wtf.moonlight.util.render.ColorUtil;
import wtf.moonlight.util.render.RenderUtil;
import wtf.moonlight.util.render.RoundedUtil;

import java.awt.*;
import java.util.Comparator;

/**
 * @Author：Guyuemang
 * @Date：2025/7/12 16:22
 */
public class ArrayListGui extends Widget {

    public ArrayListGui() {
        super("ArrayListGUI");
    }

    public static final ArrayList arraylist = Client.INSTANCE.getModuleManager().getModule(ArrayList.class);
    public static final Interface interfaces = Client.INSTANCE.getModuleManager().getModule(Interface.class);

    @Override
    public void onShader(Shader2DEvent event) {
        FontRenderer fontRenderer = getFont();
        java.util.ArrayList<Module> enabledMods = getModuleArrayList(fontRenderer);
        int count = 0;
        int counts = 0;
        ScaledResolution sr = new ScaledResolution(mc);
        for (Module module : enabledMods) {
            counts++;
            if (arraylist.importantModules.get()) {
                if (module.getCategory() == Categor.Visual) continue;
                if (module.getCategory() == Categor.Display) continue;
            }
            Animation moduleAnimation = module.getAnimation();
            moduleAnimation.setDirection(module.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
            if (!module.isEnabled() && moduleAnimation.finished(Direction.BACKWARDS)) continue;
            int renderx = (int) renderX - 2;
            int rendery = (int) renderY + 4;
            boolean flip = renderX + width / 2 <= sr.getScaledWidth() / 2f;
            String displayText = module.getName() + module.getTag();
            int x = flip ? (renderx + 4) : (int) (renderx + (this.width - (arraylist.fontmode.getValue().equals("Custom") ? mc.fontRendererObj.getStringWidth(displayText) : fontRenderer.getStringWidth(displayText))));
            int y = rendery + count + arraylist.fontSize.getValue().intValue();
            switch (arraylist.animation.getValue()) {
                case "Move In":
                    if (flip) {
                        x -= (int) Math.abs((moduleAnimation.getOutput() - 1.0) * (12.0 + fontRenderer.getStringWidth(displayText)));
                    } else {
                        x += (int) Math.abs((moduleAnimation.getOutput() - 1.0) * (12.0 + fontRenderer.getStringWidth(displayText)));
                    }
                    break;
                case "Scale In":
                    if (flip) {
                        RenderUtil.scaleStart(x, rendery + count + mc.fontRendererObj.FONT_HEIGHT, (float) moduleAnimation.getOutput());
                    } else {
                        RenderUtil.scaleStart(x + fontRenderer.getStringWidth(displayText), rendery + count + mc.fontRendererObj.FONT_HEIGHT, (float) moduleAnimation.getOutput());
                    }
                    break;
            }

            int index = (int) (counts * arraylist.colorIndex.getValue());
            int textcolor = ColorUtil.swapAlpha(color(index), 255);
            if (arraylist.color.is("Tenacity")) {
                textcolor = ColorUtil.interpolateColorsBackAndForth(arraylist.colorspeed.getValue().intValue(), index, interfaces.getMainColor(), interfaces.getSecondColor(), false).getRGB();
            }
            int w = arraylist.fontmode.getValue().equals("Custom") ? mc.fontRendererObj.getStringWidth(displayText) + 4 : fontRenderer.getStringWidth(displayText) + 6;
            switch (arraylist.misc.getValue()) {
                case "Top":
                    if (count == 0) {
                        RenderUtil.drawRect(x - 2, rendery - 4, w, 2, textcolor);
                    }
                    break;
                case "Side":
                    if (flip) {
                        RenderUtil.drawRect(x - 4, y - 4, 2, arraylist.hight.getValue().intValue(), textcolor);
                    } else {
                        RenderUtil.drawRect(x + (arraylist.fontmode.getValue().equals("Custom") ? mc.fontRendererObj.getStringWidth(displayText) + 2 : fontRenderer.getStringWidth(displayText) + 2), y - 4.5f, 2, arraylist.hight2.getValue().intValue(), textcolor);
                    }
                    break;
                default:
                    break;
            }

            if (arraylist.background.get()) {
                switch (arraylist.bgMode.getValue()) {
                    case "Rect":
                        RenderUtil.drawRect(x - 2, rendery + count - 4, w, arraylist.hight.getValue().intValue(), ColorUtil.applyOpacity(new Color(1, 1, 1), arraylist.backgroundAlpha.getValue().floatValue()));
                        break;
                    case "Round":
                        RoundedUtil.drawRound(x - 2, rendery + count - 4, w, arraylist.hight.getValue().intValue(), arraylist.radius.getValue().intValue(), ColorUtil.applyOpacity3(new Color(1, 1, 1).getRGB(), arraylist.backgroundAlpha.getValue().floatValue()));
                        break;
                }
            }

            switch (arraylist.textShadow.getValue()) {
                case "None":
                    if (arraylist.fontmode.getValue().equals("Custom")) {
                        mc.fontRendererObj.drawString(displayText, x + 1, y, textcolor);
                    } else {
                        fontRenderer.drawString(displayText, x + 1, y, textcolor);
                    }
                    break;
                case "Colored":
                    RenderUtil.resetColor();
                    if (arraylist.fontmode.getValue().equals("Custom")) {
                        mc.fontRendererObj.drawString(StringUtils.stripColorCodes(displayText), x + 2, y + 1 - 2, ColorUtil.darker(textcolor, .5f));
                    } else {
                        fontRenderer.drawString(StringUtils.stripColorCodes(displayText), x + 2, y + 1 - 2, ColorUtil.darker(textcolor, .5f));
                    }
                    RenderUtil.resetColor();
                    if (arraylist.fontmode.getValue().equals("Custom")) {
                        mc.fontRendererObj.drawString(displayText, x + 1, y - 2, textcolor);
                    } else {
                        fontRenderer.drawString(displayText, x + 1, y - 2, textcolor);
                    }
                    break;
                case "Black":
                    float f = 2f;
                    if (arraylist.fontmode.getValue().equals("Custom")) {
                        mc.fontRendererObj.drawString(StringUtils.stripColorCodes(displayText), (int) (x + f), (int) (y + f - 2),
                                ColorUtil.applyOpacity(Color.BLACK, 1f).getRGB());
                    } else {
                        fontRenderer.drawString(StringUtils.stripColorCodes(displayText), x + f, y + f - 2,
                                ColorUtil.applyOpacity(Color.BLACK, 1f).getRGB());
                    }
                    RenderUtil.resetColor();
                    if (arraylist.fontmode.getValue().equals("Custom")) {
                        mc.fontRendererObj.drawString(displayText, x + 1, y - 2, textcolor);
                    } else {
                        fontRenderer.drawString(displayText, x + 1, y - 2, textcolor);
                    }
                    break;
            }
            if (arraylist.animation.getValue().equals("Scale In")) {
                RenderUtil.scaleEnd();
            }
            count += (int) (moduleAnimation.getOutput() * (arraylist.hight.getValue() * this.arraylist.count.getValue()));
            this.height = count;
        }
        this.width = 52;
    }

    @Override
    public void render() {
        FontRenderer fontRenderer = getFont();

        java.util.ArrayList<Module> enabledMods = getModuleArrayList(fontRenderer);
        int count = 0;
        int counts = 0;
        ScaledResolution sr = new ScaledResolution(mc);
        for (Module module : enabledMods) {
            counts++;
            if (arraylist.importantModules.get()) {
                if (module.getCategory() == Categor.Visual) continue;
                if (module.getCategory() == Categor.Display) continue;
            }
            Animation moduleAnimation = module.getAnimation();
            moduleAnimation.setDirection(module.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
            if (!module.isEnabled() && moduleAnimation.finished(Direction.BACKWARDS)) continue;
            int renderx = (int) renderX - 2;
            int rendery = (int) renderY + 4;
            boolean flip = renderX + width / 2 <= sr.getScaledWidth() / 2f;
            String displayText = module.getName() + module.getTag();
            int x = flip ? (renderx + 4) : (int) (renderx + (this.width - (arraylist.fontmode.getValue().equals("Custom") ? mc.fontRendererObj.getStringWidth(displayText) : fontRenderer.getStringWidth(displayText))));
            int y = rendery + count + arraylist.fontSize.getValue().intValue();
            switch (arraylist.animation.getValue()) {
                case "Move In":
                    if (flip) {
                        x -= (int) Math.abs((moduleAnimation.getOutput() - 1.0) * (12.0 + fontRenderer.getStringWidth(displayText)));
                    } else {
                        x += (int) Math.abs((moduleAnimation.getOutput() - 1.0) * (12.0 + fontRenderer.getStringWidth(displayText)));
                    }
                    break;
                case "Scale In":
                    if (flip) {
                        RenderUtil.scaleStart(x, rendery + count + mc.fontRendererObj.FONT_HEIGHT, (float) moduleAnimation.getOutput());
                    } else {
                        RenderUtil.scaleStart(x + fontRenderer.getStringWidth(displayText), rendery + count + mc.fontRendererObj.FONT_HEIGHT, (float) moduleAnimation.getOutput());
                    }
                    break;
            }

            int index = (int) (counts * arraylist.colorIndex.getValue());
            int textcolor = ColorUtil.swapAlpha(color(index), 255);
            if (arraylist.color.is("Tenacity")) {
                textcolor = ColorUtil.interpolateColorsBackAndForth(arraylist.colorspeed.getValue().intValue(), index, interfaces.getMainColor(), interfaces.getSecondColor(), false).getRGB();
            }

            int w = arraylist.fontmode.getValue().equals("Custom") ? mc.fontRendererObj.getStringWidth(displayText) + 4 : fontRenderer.getStringWidth(displayText) + 6;

            switch (arraylist.misc.getValue()) {
                case "Top":
                    if (count == 0) {
                        RenderUtil.drawRect(x - 2, rendery - 4, w, 2, textcolor);
                    }
                    break;
                case "Side":
                    if (flip) {
                        RenderUtil.drawRect(x - 4, y - 4, 2, arraylist.hight.getValue().intValue(), textcolor);
                    } else {
                        RenderUtil.drawRect(x + (arraylist.fontmode.getValue().equals("Custom") ? mc.fontRendererObj.getStringWidth(displayText) + 2 : fontRenderer.getStringWidth(displayText) + 2), y - 4.5f, 2, arraylist.hight2.getValue().intValue(), textcolor);
                    }
                    break;
                default:
                    break;
            }

            if (arraylist.background.get()) {
                switch (arraylist.bgMode.getValue()) {
                    case "Rect":
                        RenderUtil.drawRect(x - 2, rendery + count - 4, w, arraylist.hight.getValue().intValue(), ColorUtil.applyOpacity(new Color(1, 1, 1), arraylist.backgroundAlpha.getValue().floatValue()));
                        break;
                    case "Round":
                        RoundedUtil.drawRound(x - 2, rendery + count - 4, w, arraylist.hight.getValue().intValue(), arraylist.radius.getValue().intValue(), ColorUtil.applyOpacity3(new Color(1, 1, 1).getRGB(), arraylist.backgroundAlpha.getValue().floatValue()));
                        break;
                }
            }

            switch (arraylist.textShadow.getValue()) {
                case "None":
                    if (arraylist.fontmode.getValue().equals("Custom")) {
                        mc.fontRendererObj.drawString(displayText, x + 1, y, textcolor);
                    } else {
                        fontRenderer.drawString(displayText, x + 1, y, textcolor);
                    }
                    break;
                case "Colored":
                    RenderUtil.resetColor();
                    if (arraylist.fontmode.getValue().equals("Custom")) {
                        mc.fontRendererObj.drawString(StringUtils.stripColorCodes(displayText), x + 2, y + 1 - 2, ColorUtil.darker(textcolor, .5f));
                    } else {
                        fontRenderer.drawString(StringUtils.stripColorCodes(displayText), x + 2, y + 1 - 2, ColorUtil.darker(textcolor, .5f));
                    }
                    RenderUtil.resetColor();
                    if (arraylist.fontmode.getValue().equals("Custom")) {
                        mc.fontRendererObj.drawString(displayText, x + 1, y - 2, textcolor);
                    } else {
                        fontRenderer.drawString(displayText, x + 1, y - 2, textcolor);
                    }
                    break;
                case "Black":
                    float f = 2f;
                    if (arraylist.fontmode.getValue().equals("Custom")) {
                        mc.fontRendererObj.drawString(StringUtils.stripColorCodes(displayText), (int) (x + f), (int) (y + f - 2),
                                ColorUtil.applyOpacity(Color.BLACK, 1f).getRGB());
                    } else {
                        fontRenderer.drawString(StringUtils.stripColorCodes(displayText), x + f, y + f - 2,
                                ColorUtil.applyOpacity(Color.BLACK, 1f).getRGB());
                    }

                    RenderUtil.resetColor();
                    if (arraylist.fontmode.getValue().equals("Custom")) {
                        mc.fontRendererObj.drawString(displayText, x + 1, y - 2, textcolor);
                    } else {
                        fontRenderer.drawString(displayText, x + 1, y - 2, textcolor);
                    }
                    break;
            }
            if (arraylist.animation.getValue().equals("Scale In")) {
                RenderUtil.scaleEnd();
            }
            count += (int) (moduleAnimation.getOutput() * (arraylist.hight.getValue() * this.arraylist.count.getValue()));
            this.height = count;
        }
        this.width = 52;
    }

    public static FontRenderer getFont() {
        FontRenderer fontRenderer = Fonts.Regular.get(18);
        switch (arraylist.fontmode.getValue()) {
            case "Bold" -> fontRenderer = Fonts.Bold.get(18);
            case "Semibold" -> fontRenderer = Fonts.Semibold.get(18);
            case "Regular" -> fontRenderer = Fonts.Regular.get(18);
            case "Light" -> fontRenderer = Fonts.Light.get(18);
        }

        return fontRenderer;
    }

    public int color(int counter) {
        return color(counter, interfaces.getMainColor().getAlpha());
    }

    public int color(int counter, int alpha) {
        int colors = interfaces.getMainColor().getRGB();
        colors = switch (arraylist.color.getValue()) {
            case "Rainbow" -> RenderUtil.getRainbow(System.currentTimeMillis(), 2000, counter);
            case "Dynamic" ->
                    ColorUtil.swapAlpha(ColorUtil.colorSwitch(interfaces.getMainColor(), new Color(ColorUtil.darker(interfaces.getMainColor().getRGB(), 0.25F)), 2000.0F, counter, counter * 10L, arraylist.colorspeed.getColor().getRGB()).getRGB(), alpha);
            case "Double" ->
                    new Color(RenderUtil.colorSwitch(interfaces.getMainColor(), interfaces.getSecondColor(), 2000, -counter / 40, 75, 2)).getRGB();
            case "Astolfo" ->
                    ColorUtil.swapAlpha(Interface.astolfoRainbow(counter, interfaces.mainColor.getSaturation(), interfaces.mainColor.getBrightness()), alpha);
            case "Custom" -> ColorUtil.swapAlpha(interfaces.getMainColor().getRGB(), alpha);
            case "Tenacity" ->
                    ColorUtil.interpolateColorsBackAndForth(arraylist.colorspeed.getValue().intValue(), Client.INSTANCE.getModuleManager().getModules().size() * arraylist.count.getValue().intValue(), interfaces.getMainColor(), interfaces.getSecondColor(), false).getRGB();
            default -> colors;
        };
        return new Color(colors, true).getRGB();
    }

    private java.util.ArrayList<Module> getModuleArrayList(FontRenderer string) {
        Comparator<Module> sort = (m1, m2) -> {
            double ab = arraylist.fontmode.getValue().equals("Custom") ? mc.fontRendererObj.getStringWidth(m1.getName() + m1.getTag()) : string.getStringWidth(m1.getName() + m1.getTag());
            double bb = arraylist.fontmode.getValue().equals("Custom") ? mc.fontRendererObj.getStringWidth(m2.getName() + m2.getTag()) : string.getStringWidth(m2.getName() + m2.getTag());
            return Double.compare(bb, ab);
        };
        java.util.ArrayList<Module> enabledMods = new java.util.ArrayList<>(INSTANCE.getModuleManager().getModules());
        enabledMods.sort(sort);
        return enabledMods;
    }

    @Override
    public boolean shouldRender() {
        return Client.INSTANCE.getModuleManager().getModule(ArrayList.class).isEnabled();
    }
}
