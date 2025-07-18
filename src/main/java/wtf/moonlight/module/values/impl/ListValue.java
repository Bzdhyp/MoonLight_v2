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
package wtf.moonlight.module.values.impl;

import lombok.Getter;
import lombok.Setter;
import wtf.moonlight.module.Module;
import wtf.moonlight.module.values.Value;

import java.util.Arrays;
import java.util.function.Supplier;

@Getter
public class ListValue extends Value {
    @Setter
    private int index;
    public String[] modes;

    public ListValue(String name, String[] modes, String current, Module module, Supplier<Boolean> visible) {
        super(name, module, visible);
        this.modes = modes;
        this.index = Arrays.asList(modes).indexOf(current);
    }

    public ListValue(String name, String[] modes, String current, Module module) {
        super(name, module, () -> true);
        this.modes = modes;
        this.index = Arrays.asList(modes).indexOf(current);
    }

    public boolean is(String mode) {
        return getValue().equals(mode);
    }

    public String getValue() {
        try {
            if (index < 0 || index >= modes.length) {
                return modes[0];
            }
            return modes[index];
        } catch (ArrayIndexOutOfBoundsException e) {
            return "ERROR";
        }
    }

    public void setValue(String mode) {
        this.index = Arrays.asList(modes).indexOf(mode);
    }

    public void setValue(int mode) {
        this.index = mode;
    }
}
