package michi.bot.commands.math

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import michi.bot.commands.CommandScope
import michi.bot.commands.MichiCommand
import michi.bot.commands.misc.Raccoon
import michi.bot.listeners.SlashCommandListener
import michi.bot.util.Emoji
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

object Math: MichiCommand("math", "Gives you a basic math problem.", CommandScope.GUILD_SCOPE) {

    override val botPermisions: List<Permission>
        get() = listOf(Permission.MESSAGE_SEND)

    /**
     * Creates a math problem for the user if possible.
     * @param context The interaction to retrieve info from.
     * @author Slz
     * @see canHandle
     */
    @OptIn(DelicateCoroutinesApi::class)
    override fun execute(context: SlashCommandInteractionEvent) {
        val sender = context.user
        val guild = context.guild
        if (!canHandle(context)) return
        if (guild != null) {
            val bot = guild.selfMember
            if (!bot.permissions.any { permission -> Raccoon.botPermisions.contains(permission) }) {
                context.reply("I don't have the permissions to execute this command ${Emoji.michiSad}").setEphemeral(true).queue()
                return
            }
        }

        MathProblemManager.instances.add(MathProblemManager(MathProblem(sender), context))

        // puts the user that sent the command in cooldown
        GlobalScope.launch { SlashCommandListener.cooldownManager(sender) }
    }

    /**
     * Checks if the user that sent the slashCommand already has an active mathProblem to solve.
     * @param context The SlashCommandInteractionEvent that called the math function.
     * @author Slz
     * @see execute
     */
    override fun canHandle(context: SlashCommandInteractionEvent): Boolean {
        val sender = context.user
        MathProblemManager.instances.forEach {
            if (sender == it.problemInstance.user) {
                context.reply("Solve one problem before calling another ${Emoji.smolMichiAngry}").setEphemeral(true).queue()
                return false
            }
        }
        return true
    }

}