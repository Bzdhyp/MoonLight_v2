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
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.util.render.animations.advanced.Direction;
import wtf.moonlight.util.render.animations.advanced.impl.SmoothStepAnimation;
import wtf.moonlight.util.render.ColorUtil;
import wtf.moonlight.util.render.MouseUtil;
import wtf.moonlight.util.render.RoundedUtil;

import java.awt.*;

public class BooleanComponent extends Component {
    private final BoolValue setting;
    private final SmoothStepAnimation toggleAnimation = new SmoothStepAnimation(175, 1);

    public BooleanComponent(BoolValue setting) {
        this.setting = setting;
        this.toggleAnimation.setDirection(Direction.BACKWARDS);
        setHeight(Fonts.interRegular.get(15).getHeight() + 5);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        this.toggleAnimation.setDirection(this.setting.get() ? Direction.FORWARDS : Direction.BACKWARDS);

        Fonts.interRegular.get(15).drawString(setting.getName(), getX() + 4, getY() + 2.5f, -1);

        RoundedUtil.drawRound(getX() + getWidth() - 15.5f, getY() + 2.5f, 13f, 5, 2, ColorUtil.interpolateColorC(new Color(128, 128, 128, 255), INSTANCE.getModuleManager().getModule(ClickGUI.class).color.getValue(), (float) toggleAnimation.getOutput()));
        RoundedUtil.drawRound(getX() + getWidth() - 15.5f + 5 * (float) toggleAnimation.getOutput(), getY() + 2f, 7f, 5, 2, Color.WHITE);
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtil.isHovered2(getX() + getWidth() - 17.5f, getY() + 2.5f, 12.5f, 5, mouseX, mouseY) && mouseButton == 0)
            this.setting.set(!this.setting.get());
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean isVisible() {
        return this.setting.canDisplay();
    }
}
