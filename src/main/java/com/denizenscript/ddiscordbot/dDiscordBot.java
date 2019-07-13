package com.denizenscript.ddiscordbot;

import com.denizenscript.ddiscordbot.events.*;
import com.denizenscript.ddiscordbot.objects.*;
import net.aufdemrand.denizen.utilities.debugging.dB;
import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.Element;
import com.denizenscript.denizencore.objects.ObjectFetcher;
import com.denizenscript.denizencore.objects.TagRunnable;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ReplaceableTagEvent;
import com.denizenscript.denizencore.tags.TagManager;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class dDiscordBot extends JavaPlugin {

    public static dDiscordBot instance;

    public HashMap<String, DiscordConnection> connections = new HashMap<>();

    @Override
    public void onEnable() {
        dB.log("dDiscordBot loaded!");
        instance = this;
        try {
            DenizenCore.getCommandRegistry().registerCoreMember(DiscordCommand.class, "DISCORD", "DISCORD [read(the<meta>)]", 2);
            ScriptEvent.registerScriptEvent(DiscordMessageModifiedScriptEvent.instance = new DiscordMessageModifiedScriptEvent());
            ScriptEvent.registerScriptEvent(DiscordMessageDeletedScriptEvent.instance = new DiscordMessageDeletedScriptEvent());
            ScriptEvent.registerScriptEvent(DiscordMessageReceivedScriptEvent.instance = new DiscordMessageReceivedScriptEvent());
            ScriptEvent.registerScriptEvent(DiscordUserJoinsScriptEvent.instance = new DiscordUserJoinsScriptEvent());
            ScriptEvent.registerScriptEvent(DiscordUserLeavesScriptEvent.instance = new DiscordUserLeavesScriptEvent());
            ScriptEvent.registerScriptEvent(DiscordUserRoleChangeScriptEvent.instance = new DiscordUserRoleChangeScriptEvent());
            ObjectFetcher.registerWithObjectFetcher(dDiscordChannel.class);
            dDiscordChannel.registerTags();
            ObjectFetcher.registerWithObjectFetcher(dDiscordConnection.class);
            dDiscordConnection.registerTags();
            ObjectFetcher.registerWithObjectFetcher(dDiscordGroup.class);
            dDiscordGroup.registerTags();
            ObjectFetcher.registerWithObjectFetcher(dDiscordRole.class);
            dDiscordRole.registerTags();
            ObjectFetcher.registerWithObjectFetcher(dDiscordUser.class);
            dDiscordUser.registerTags();
            ObjectFetcher._initialize();
            TagManager.registerTagHandler(new TagRunnable.RootForm() {
                @Override
                public void run(ReplaceableTagEvent event) {
                    discordTagBase(event);
                }
            }, "discord");
        }
        catch (Throwable ex) {
            dB.echoError(ex);
        }
    }

    public void discordTagBase(ReplaceableTagEvent event) {
        if (!event.matches("discord") || event.replaced()) {
            return;
        }

        dDiscordConnection bot = null;

        if (event.hasNameContext()) {
            bot = dDiscordConnection.valueOf(event.getNameContext(), event.getAttributes().context);
        }

        Attribute attribute = event.getAttributes().fulfill(1);

        // <--[tag]
        // @attribute <discord[<bot-id>].exists>
        // @returns Element(Boolean)
        // @plugin dDiscordBot
        // @description
        // Returns whether a Discord bot exists with the given bot ID.
        // -->
        if (attribute.startsWith("exists")) {
            event.setReplacedObject(CoreUtilities.autoAttrib(new Element(bot != null), attribute.fulfill(1)));
            return;
        }

        // <--[tag]
        // @attribute <discord[<bot-id>]>
        // @returns Discord
        // @plugin dDiscordBot
        // @description
        // Returns the Discord bot for the given bot ID.
        // -->
        if (bot == null) {
            return;
        }
        event.setReplacedObject(CoreUtilities.autoAttrib(bot, attribute));
    }
}
