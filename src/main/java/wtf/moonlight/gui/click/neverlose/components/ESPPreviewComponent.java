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
package wtf.moonlight.gui.click.neverlose.components;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import net.minecraft.client.renderer.GlStateManager;
import wtf.moonlight.Client;
import wtf.moonlight.module.impl.visual.ESP2D;
import wtf.moonlight.module.values.Value;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.gui.click.neverlose.components.espelements.BoxElement;
import wtf.moonlight.gui.click.neverlose.components.espelements.HealthElement;
import wtf.moonlight.gui.click.neverlose.components.espelements.NameElement;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.util.misc.InstanceAccess;
import wtf.moonlight.util.render.ColorUtil;
import wtf.moonlight.util.render.MouseUtil;
import wtf.moonlight.util.render.RenderUtil;
import wtf.moonlight.util.render.RoundedUtil;

import java.util.List;

import static net.minecraft.client.gui.inventory.GuiInventory.drawEntityOnScreen;
import static wtf.moonlight.gui.click.neverlose.NeverLose.*;

@Getter
public class ESPPreviewComponent extends Component implements InstanceAccess {
    private final ElementsManage elementsManage = new ElementsManage();
    private int posX,posY,dragX,dragY;
    private boolean dragging = false;
    private boolean adsorb = true;
    private final List<Value> values = Client.INSTANCE.getModuleManager().getModule(ESP2D.class).getValues();
    private final ObjectArrayList<Component> elements = new ObjectArrayList<>();

    public ESPPreviewComponent() {
        elements.add(new BoxElement());
        elements.add(new HealthElement());
        elements.add(new NameElement());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        //update dragging coordinate
        if (dragging) {
            posX = mouseX + dragX;
            posY = mouseY + dragY;
        }
        if (adsorb && !dragging) {
            //update coordinate
            posX = INSTANCE.getNeverLose().getPosX();
            posY = INSTANCE.getNeverLose().getPosY();
        }
        adsorb = Math.abs(posX - INSTANCE.getNeverLose().getPosX()) <= 30
                && Math.abs(posY - INSTANCE.getNeverLose().getPosY()) <= INSTANCE.getNeverLose().getHeight();
        //rect
        RoundedUtil.drawRoundOutline(posX + INSTANCE.getNeverLose().getWidth() + 12,posY + 10,200,INSTANCE.getNeverLose().getHeight() - 20,2,.1f, ColorUtil.applyOpacity(bgColor2,.7f),outlineColor);
        Fonts.neverlose.get(30).drawString("b",posX + INSTANCE.getNeverLose().getWidth() + 18,posY + 16,textRGB);
        Fonts.interSemiBold.get(16).drawString("Interactive ESP Preview",posX + INSTANCE.getNeverLose().getWidth() + 206 - Fonts.interSemiBold.get(16).getStringWidth("Interactive ESP Preview"),posY + 18,textRGB);
        RenderUtil.resetColor();
        //render model
        GlStateManager.pushMatrix();
        drawEntityOnScreen((posX + INSTANCE.getNeverLose().getWidth() + 110), (int) (posY + 210 + 75 * (1 - elementsManage.open.getOutput())), 80, 0, 0, mc.thePlayer);
        GlStateManager.popMatrix();
        //manage
        elementsManage.drawScreen(mouseX,mouseY);
        //elements
        elements.forEach(elements->elements.drawScreen(mouseX,mouseY));
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtil.isHovered2(posX + INSTANCE.getNeverLose().getWidth() + 12,posY + 10,200,INSTANCE.getNeverLose().getHeight() - (INSTANCE.getNeverLose().getHeight() - 20),mouseX,mouseY) && mouseButton == 0 && !elementsManage.opened) {
            dragging = true;
            dragX = posX - mouseX;
            dragY = posY - mouseY;
        }
        elementsManage.mouseClicked(mouseX,mouseY,mouseButton);
        elements.forEach(elements->elements.mouseClicked(mouseX,mouseY,mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0){
            dragging = false;
        }
        elementsManage.mouseReleased(mouseX,mouseY,state);
        elements.forEach(elements->elements.mouseReleased(mouseX,mouseY,state));
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
    }
}
