/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.command.api;

import im.expensive.Expensive;
import im.expensive.command.api.CommandException;
import im.expensive.command.api.DispatchResult;
import im.expensive.command.interfaces.AdviceCommandFactory;
import im.expensive.command.interfaces.Command;
import im.expensive.command.interfaces.CommandDispatcher;
import im.expensive.command.interfaces.CommandProvider;
import im.expensive.command.interfaces.CommandWithAdvice;
import im.expensive.command.interfaces.Logger;
import im.expensive.command.interfaces.MultiNamedCommand;
import im.expensive.command.interfaces.ParametersFactory;
import im.expensive.command.interfaces.Prefix;
import im.expensive.modules.api.ModuleManager;
import im.expensive.modules.impl.misc.SelfDestruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.text.TextFormatting;

public class StandaloneCommandDispatcher
implements CommandDispatcher,
CommandProvider {
    private static final String DELIMITER = " ";
    private final Prefix prefix;
    private final ParametersFactory parametersFactory;
    private final Logger logger;
    private final Map<String, Command> aliasToCommandMap;

    public StandaloneCommandDispatcher(List<Command> commands, AdviceCommandFactory adviceCommandFactory, Prefix prefix, ParametersFactory parametersFactory, Logger logger) {
        this.prefix = prefix;
        this.parametersFactory = parametersFactory;
        this.logger = logger;
        this.aliasToCommandMap = this.commandsToAliasToCommandMap(this.commandsWithAdviceCommand(adviceCommandFactory, commands));
    }

    @Override
    public DispatchResult dispatch(String message) {
        ModuleManager moduleManager = Expensive.getInstance().getModuleManager();
        SelfDestruct selfDestruct = moduleManager.getSelfDestruct();
        if (SelfDestruct.unhooked) {
            return DispatchResult.NOT_DISPATCHED;
        }
        String prefix = this.prefix.get();
        if (!message.startsWith(prefix)) {
            return DispatchResult.NOT_DISPATCHED;
        }
        String[] split = message.split(DELIMITER);
        String commandName = split[0].substring(prefix.length());
        Command command = this.aliasToCommandMap.get(commandName);
        try {
            String parameters = this.extractParametersFromMessage(message, split);
            command.execute(this.parametersFactory.createParameters(parameters, DELIMITER));
        } catch (Exception e) {
            this.handleCommandException(e, command);
        }
        return DispatchResult.DISPATCHED;
    }

    @Override
    public Command command(String alias) {
        return this.aliasToCommandMap.get(alias);
    }

    private Map<String, Command> commandsToAliasToCommandMap(List<Command> commands) {
        return commands.stream().flatMap(this::commandToWrappedCommandStream).collect(Collectors.toMap(FlatMapCommand::getAlias, FlatMapCommand::getCommand));
    }

    private Stream<FlatMapCommand> commandToWrappedCommandStream(Command command) {
        Stream<FlatMapCommand> wrappedCommandStream = Stream.of(new FlatMapCommand(command.name(), command));
        if (command instanceof MultiNamedCommand) {
            MultiNamedCommand multiNamedCommand = (MultiNamedCommand)((Object)command);
            return Stream.concat(wrappedCommandStream, multiNamedCommand.aliases().stream().map(alias -> new FlatMapCommand((String)alias, command)));
        }
        return wrappedCommandStream;
    }

    private void handleCommandException(Exception e, Command command) {
        if (e instanceof CommandException) {
            this.logger.log(e.getMessage());
        } else {
            this.logger.log("\u041f\u0440\u043e\u0438\u0437\u043e\u0448\u043b\u0430 \u043e\u0448\u0438\u0431\u043a\u0430 \u0432\u043e \u0432\u0440\u0435\u043c\u044f \u0432\u044b\u043f\u043e\u043b\u043d\u0435\u043d\u0438\u044f \u043a\u043e\u043c\u0430\u043d\u0434\u044b!");
            String details = "\u0414\u0435\u0442\u0430\u043b\u0438 \u043e\u0448\u0438\u0431\u043a\u0438: ";
            String errorMessage = e instanceof NullPointerException ? "\u0422\u0430\u043a\u043e\u0439 \u043a\u043e\u043c\u0430\u043d\u0434\u044b \u043d\u0435 \u0441\u0443\u0449\u0435\u0441\u0442\u0432\u0443\u0435\u0442." : e.getMessage();
            this.logger.log(details.concat(errorMessage));
        }
        if (command instanceof CommandWithAdvice) {
            this.logger.log(String.format(TextFormatting.GRAY + "\u0412\u0432\u0435\u0434\u0438\u0442\u0435 %sadvice %s", this.prefix.get(), command.name()));
        }
    }

    private String extractParametersFromMessage(String message, String[] split) {
        return message.substring((split.length != 1 ? DELIMITER.length() : 0) + split[0].length());
    }

    private List<Command> commandsWithAdviceCommand(AdviceCommandFactory adviceCommandFactory, List<Command> commands) {
        ArrayList<Command> commandsWithAdvices = new ArrayList<Command>(commands);
        commandsWithAdvices.add(adviceCommandFactory.adviceCommand(this));
        return commandsWithAdvices;
    }

    private static final class FlatMapCommand {
        private final String alias;
        private final Command command;

        public FlatMapCommand(String alias, Command command) {
            this.alias = alias;
            this.command = command;
        }

        public String getAlias() {
            return this.alias;
        }

        public Command getCommand() {
            return this.command;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof FlatMapCommand)) {
                return false;
            }
            FlatMapCommand other = (FlatMapCommand)o;
            String this$alias = this.getAlias();
            String other$alias = other.getAlias();
            if (this$alias == null ? other$alias != null : !this$alias.equals(other$alias)) {
                return false;
            }
            Command this$command = this.getCommand();
            Command other$command = other.getCommand();
            return !(this$command == null ? other$command != null : !this$command.equals(other$command));
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            String $alias = this.getAlias();
            result = result * 59 + ($alias == null ? 43 : $alias.hashCode());
            Command $command = this.getCommand();
            result = result * 59 + ($command == null ? 43 : $command.hashCode());
            return result;
        }

        public String toString() {
            return "StandaloneCommandDispatcher.FlatMapCommand(alias=" + this.getAlias() + ", command=" + this.getCommand() + ")";
        }
    }
}

