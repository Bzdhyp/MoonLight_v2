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
package wtf.moonlight.command;

import wtf.moonlight.Client;
import com.cubk.EventTarget;
import wtf.moonlight.events.misc.SendMessageEvent;
import wtf.moonlight.command.impl.*;
import wtf.moonlight.util.DebugUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CommandManager {

    public List<Command> cmd = new ArrayList<>();

    public CommandManager() {
        addCommands(new HelpCommand(), new ToggleCommand(), new BindCommand(), new HideCommand(), new FriendCommand(), new ConfigCommand());

        Client.INSTANCE.getModuleManager().getModules().forEach(module -> {
            if(!module.getValues().isEmpty())
                cmd.add(new ModuleCommand(module,module.getValues()));
        });

        Client.INSTANCE.getEventManager().register(this);
    }

    @EventTarget
    public void onSendMessageEvent(final SendMessageEvent event) {
        final String message;
        if ((message = event.getMessage()).startsWith(".")) {
            event.setCancelled(true);
            final String removedPrefix = message.substring(1);
            final String[] arguments = removedPrefix.split(" ");
            if (!removedPrefix.isEmpty() && arguments.length > 0) {
                for (final Command command : cmd) {
                    for (final String alias : command.getAliases()) {
                        if (alias.equalsIgnoreCase(arguments[0])) {
                            try {
                                command.execute(arguments);
                            } catch (CommandExecutionException e) {
                                DebugUtil.sendMessage("Invalid commands syntax. Hint: " + e.getMessage());
                            }
                            return;
                        }
                    }
                }
                DebugUtil.sendMessage("'" + arguments[0] + "' is not a commands. " + "Try '.help'");
            } else {
                DebugUtil.sendMessage("No arguments were supplied. Try '.help'");
            }
        }
    }
    public void addCommands(Command... checks) {
        this.cmd.addAll(Arrays.asList(checks));
    }
}
