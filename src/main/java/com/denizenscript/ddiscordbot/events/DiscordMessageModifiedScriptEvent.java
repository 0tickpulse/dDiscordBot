package com.denizenscript.ddiscordbot.events;

import com.denizenscript.ddiscordbot.DiscordScriptEvent;
import discord4j.core.event.domain.message.MessageUpdateEvent;
import discord4j.core.object.entity.GuildChannel;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import com.denizenscript.denizencore.objects.Element;
import com.denizenscript.denizencore.objects.dList;
import com.denizenscript.denizencore.objects.dObject;
import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.utilities.CoreUtilities;

public class DiscordMessageModifiedScriptEvent extends DiscordScriptEvent {
    public static DiscordMessageModifiedScriptEvent instance;

    // <--[event]
    // @Events
    // discord message modified
    //
    // @Regex ^on discord message modified$
    //
    // @Switch for <bot>
    // @Switch channel <channel_id>
    // @Switch group <group_id>
    //
    // @Triggers when a Discord user modified a message.
    //
    // @Plugin dDiscordBot
    //
    // @Context
    // <context.bot> returns the Denizen ID of the bot.
    // <context.self> returns the bots own Discord user ID.
    // <context.channel> returns the channel ID.
    // <context.channel_name> returns the channel name.
    // <context.group> returns the group ID.
    // <context.group_name> returns the group name.
    // <context.author_id> returns the author's internal ID.
    // <context.author_name> returns the author's name.
    // <context.mentions> returns a list of all mentioned user IDs.
    // <context.mention_names> returns a list of all mentioned user names.
    // <context.is_direct> returns whether the message was sent directly to the bot (if false, the message was sent to a public channel).
    // <context.message> returns the message (raw).
    // <context.no_mention_message> returns the message with all user mentions stripped.
    // <context.formatted_message> returns the formatted message (mentions/etc. are written cleanly). CURRENTLY NON-FUNCTIONAL.
    // <context.old_message_valid> returns whether the old message is available (it may be lost due to caching).
    // <context.old_message> returns the previous message (raw).
    // <context.old_no_mention_message> returns the previous message with all user mentions stripped.
    // <context.old_formatted_message> returns the formatted previous message (mentions/etc. are written cleanly). CURRENTLY NON-FUNCTIONAL.
    //
    // -->

    public MessageUpdateEvent getEvent() {
        return (MessageUpdateEvent) event;
    }


    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        return CoreUtilities.toLowerCase(s).startsWith("discord message modified");
    }

    @Override
    public boolean matches(ScriptPath path) {
        if (!path.checkSwitch("channel", String.valueOf(getEvent().getChannelId().asLong()))) {
            return false;
        }
        if (getEvent().getGuildId().isPresent() && !path.checkSwitch("group", String.valueOf(getEvent().getGuildId().get().asLong()))) {
            return false;
        }
        return super.matches(path);
    }

    @Override
    public dObject getContext(String name) {
        if (name.equals("channel")) {
            return new Element(getEvent().getChannelId().asLong());
        }
        else if (name.equals("channel_name")) {
            MessageChannel channel = getEvent().getChannel().block();
            if (channel instanceof GuildChannel) {
                return new Element(((GuildChannel) channel).getName());
            }
        }
        else if (name.equals("group")) {
            if (getEvent().getChannel().block() instanceof GuildChannel) {
                return new Element(((GuildChannel) getEvent().getChannel().block()).getGuildId().asLong());
            }
        }
        else if (name.equals("group_name")) {
            if (getEvent().getChannel().block() instanceof GuildChannel) {
                return new Element(((GuildChannel) getEvent().getChannel().block()).getGuild().block().getName());
            }
        }
        else if (name.equals("old_message_valid")) {
            return new Element(getEvent().getOld().isPresent());
        }
        else if (name.equals("old_message")) {
            if (getEvent().getOld().isPresent()) {
                return new Element(getEvent().getOld().get().getContent().get());
            }
        }
        else if (name.equals("old_no_mention_message")) {
            if (getEvent().getOld().isPresent()) {
                return new Element(stripMentions(getEvent().getOld().get().getContent().get(),
                        getEvent().getOld().get().getUserMentions()));
            }
        }
        else if (name.equals("old_formatted_message")) {
            if (getEvent().getOld().isPresent()) {
                return new Element(getEvent().getOld().get().getContent().get());
            }
        }
        else if (name.equals("message")) {
            return new Element(getEvent().getMessage().block().getContent().orElse(""));
        }
        else if (name.equals("no_mention_message")) {
            return new Element(stripMentions(getEvent().getMessage().block().getContent().orElse(""),
                    getEvent().getMessage().block().getUserMentions()));
        }
        else if (name.equals("formatted_message")) {
            return new Element(getEvent().getMessage().block().getContent().orElse(""));
        }
        else if (name.equals("author_id")) {
            return new Element(getEvent().getMessage().block().getAuthor().get().getId().asLong());
        }
        else if (name.equals("author_name")) {
            return new Element(getEvent().getMessage().block().getAuthor().get().getUsername());
        }
        else if (name.equals("mentions")) {
            dList list = new dList();
            for (Snowflake user : getEvent().getMessage().block().getUserMentionIds()) {
                list.add(String.valueOf(user.asLong()));
            }
            return list;
        }
        else if (name.equals("mention_names")) {
            dList list = new dList();
            for (User user : getEvent().getMessage().block().getUserMentions().toIterable()) {
                list.add(String.valueOf(user.getUsername()));
            }
            return list;
        }
        else if (name.equals("is_direct")) {
            return new Element(!(getEvent().getChannel().block() instanceof GuildChannel));
        }
        return super.getContext(name);
    }

    @Override
    public String getName() {
        return "DiscordModifiedMessage";
    }
}
