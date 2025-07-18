/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & wxdbie & opZywl & MukjepScarlet & lucas & eonian]
 */
package wtf.moonlight.gui.click.dropdown.component.impl;

import wtf.moonlight.module.impl.visual.ClickGUI;
import wtf.moonlight.module.values.impl.BoolValue;
import wtf.moonlight.module.values.impl.MultiBoolValue;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.util.render.animations.advanced.Direction;
import wtf.moonlight.util.render.animations.advanced.impl.EaseOutSine;
import wtf.moonlight.util.render.ColorUtil;
import wtf.moonlight.util.render.MouseUtil;
import wtf.moonlight.util.render.RoundedUtil;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;


public class MultiBooleanComponent extends Component {
    private final MultiBoolValue setting;
    private final Map<BoolValue, EaseOutSine> select = new HashMap<>();

    public MultiBooleanComponent(MultiBoolValue setting) {
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        float offset = 8;
        float heightoff = 0;

        RoundedUtil.drawRound(getX() + offset, getY() + Fonts.interRegular.get(15).getHeight() + 2, getWidth() - 5, heightoff, 4, INSTANCE.getModuleManager().getModule(ClickGUI.class).color.getValue());
        Fonts.interRegular.get(15).drawString(setting.getName(), getX() + 4, getY(), -1);

        for (BoolValue boolValue : setting.getValues()) {
            float off = Fonts.interRegular.get(13).getStringWidth(boolValue.getName()) + 4;
            if (offset + off >= getWidth() - 5) {
                offset = 8;
                heightoff += Fonts.interRegular.get(13).getHeight() + 2;
            }
            select.putIfAbsent(boolValue, new EaseOutSine(250, 1));
            select.get(boolValue).setDirection(boolValue.get() ? Direction.FORWARDS : Direction.BACKWARDS);

            Fonts.interRegular.get(13).drawString(boolValue.getName(), getX() + offset, getY() + Fonts.interRegular.get(15).getHeight() + 2 + heightoff, ColorUtil.interpolateColor2(new Color(128, 128, 128), INSTANCE.getModuleManager().getModule(ClickGUI.class).color.getValue(), (float) select.get(boolValue).getOutput()));

            offset += off;
        }

        setHeight(Fonts.interRegular.get(15).getHeight() + 10 + heightoff);
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouse) {
        float offset = 8;
        float heightoff = 0;
        for (BoolValue boolValue : setting.getValues()) {
            float off = Fonts.interRegular.get(13).getStringWidth(boolValue.getName()) + 4;
            if (offset + off >= getWidth() - 5) {
                offset = 8;
                heightoff += Fonts.interRegular.get(13).getHeight() + 2;
            }
            if (MouseUtil.isHovered2(getX() + offset, getY() + Fonts.interRegular.get(15).getHeight() + 2 + heightoff, Fonts.interRegular.get(13).getStringWidth(boolValue.getName()), Fonts.interRegular.get(13).getHeight(), mouseX, mouseY) && mouse == 0) {
                boolValue.set(!boolValue.get());
            }
            offset += off;
        }
        super.mouseClicked(mouseX, mouseY, mouse);
    }

    @Override
    public boolean isVisible() {
        return setting.canDisplay();
    }
}
